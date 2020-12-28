package io.github.rowak.recapture;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSlider;

import org.jnativehook.NativeHookException;

public class Launcher extends JFrame {
	public final int[] QUALITY_TYPES = {Image.SCALE_FAST, Image.SCALE_REPLICATE, Image.SCALE_SMOOTH};
	public final String[] QUALITY_NAMES = {"Fast", "Replicate", "Smooth"};
	
	private Rectangle captureArea;
	
	private JComboBox<String> cmbxSource;
	private JComboBox<String> cmbxDest;
	private JComboBox<String> cmbxQuality;
	private JButton btnSetArea;
	private JSlider frequencySlider;
	private JButton btnLaunch;
	
	public Launcher() {
		initUI();
	}
	
	private void initUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 350);
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][][][][][][][][][]"));
		
		JLabel lblSourceDisplay = new JLabel("Source Display");
		getContentPane().add(lblSourceDisplay, "cell 0 0");
		
		cmbxSource = new JComboBox<String>(getMonitors());
		getContentPane().add(cmbxSource, "flowx,cell 0 1,growx");
		
		JLabel lblDestinationDisplay = new JLabel("Destination Display");
		getContentPane().add(lblDestinationDisplay, "cell 0 2");
		
		cmbxDest = new JComboBox<String>(getMonitors());
		getContentPane().add(cmbxDest, "cell 0 3,growx");
		
		JLabel lblQuality = new JLabel("Quality");
		getContentPane().add(lblQuality, "cell 0 4");
		
		cmbxQuality = new JComboBox<String>(QUALITY_NAMES);
		getContentPane().add(cmbxQuality, "cell 0 5,growx");
		
		JLabel lblFrequency = new JLabel("Frequency");
		getContentPane().add(lblFrequency, "cell 0 6");
		
		btnSetArea = new JButton("Set Area");
		btnSetArea.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				createCaptureAreaWindow();
			}
		});
		getContentPane().add(btnSetArea, "cell 0 1");
		
		frequencySlider = new JSlider();
		frequencySlider.setMaximum(100);
		frequencySlider.setMinimum(1);
		frequencySlider.setValue(20);
		getContentPane().add(frequencySlider, "cell 0 7,growx");
		
		btnLaunch = new JButton("Launch");
		btnLaunch.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				createRecaptureFrame();
			}
		});
		getContentPane().add(btnLaunch, "cell 0 9,alignx center");
	}
	
	private String[] getMonitors() {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = g.getScreenDevices();

		String[] deviceStrs = new String[devices.length];
		for (int i = 0; i < devices.length; i++) {
			int width = devices[i].getDisplayMode().getWidth();
			int height = devices[i].getDisplayMode().getHeight();
			deviceStrs[i] = "Monitor " + i + " (" + width + "x" + height + ")";
		}
		return deviceStrs;
	}
	
	private Rectangle getMonitorBounds(int id) {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = g.getScreenDevices();
		return devices[id].getDefaultConfiguration().getBounds();
	}
	
	private int monitorStringToId(String str) {
		try {
			return Integer.parseInt(str.split(" ")[1]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	private int getSourceMonitor() {
		return monitorStringToId((String)cmbxSource.getSelectedItem());
	}
	
	private int getDestinationMonitor() {
		return monitorStringToId((String)cmbxDest.getSelectedItem());
	}
	
	private int getQuality() {
		return QUALITY_TYPES[cmbxQuality.getSelectedIndex()];
	}
	
	private int getFrequency() {
		return frequencySlider.getValue();
	}
	
	private LauncherInfo getLauncherInfo() {
		return new LauncherInfo(captureArea, getMonitorBounds(getSourceMonitor()),
				getQuality(), getFrequency());
	}
	
	private void createCaptureAreaWindow() {
		int sourceMonitor = getSourceMonitor();
		if (sourceMonitor != -1) {
			AreaCaptureWindow window = new AreaCaptureWindow(getSourceMonitor());
			ComponentResizer cr = new ComponentResizer();
			cr.setSnapSize(new Dimension(10, 10));
			cr.registerComponent(window);
			window.setAreaCaptureListener(new AreaCaptureListener()
			{
				@Override
				public void onCapture(Rectangle rect) {
					captureArea = rect;
				}
			});
		}
		else {
			// TODO: tell user to select a source monitor
		}
	}
	
	private void createRecaptureFrame() {
		if (isInputValid()) {
			try {
				RecaptureFrame frame = new RecaptureFrame(getLauncherInfo());
				frame.setUndecorated(true);
				frame.setVisible(true);
			} catch (AWTException | NativeHookException e) {
				e.printStackTrace();
			}
		}
		else {
			// TODO: tell user input is invalid
		}
	}
	
	private boolean isInputValid() {
		System.out.println((captureArea != null) + " " + (getSourceMonitor() != -1) + " " + (getDestinationMonitor() != -1) + " " +
				(getQuality() != -1) + " " + (getFrequency() != -1));
		return captureArea != null && getSourceMonitor() != -1 && getDestinationMonitor() != -1 &&
				getQuality() != -1 && getFrequency() != -1;
	}
}

