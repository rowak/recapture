package io.github.rowak.recapture;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
	private JCheckBox ckbxEmulateMouse;
	private JButton btnLaunch;
	
	public Launcher() {
		initUI();
		loadLauncherInfo();
	}
	
	private void initUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(getDefaultLauncherBounds());
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
		
		ckbxEmulateMouse = new JCheckBox("Emulate mouse");
		ckbxEmulateMouse.setSelected(true);
		getContentPane().add(ckbxEmulateMouse, "cell 0 8");
		
		btnLaunch = new JButton("Launch");
		btnLaunch.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				createRecaptureFrame();
			}
		});
		getContentPane().add(btnLaunch, "cell 0 10,alignx center");
	}
	
	private void loadLauncherInfo() {
		LauncherInfo info = LauncherInfoManager.loadLauncherInfo();
		if (info != null) {
			captureArea = new Rectangle();
			captureArea.setBounds(info.getCaptureArea());
			cmbxSource.setSelectedIndex(info.getSourceDisplayId());
			cmbxDest.setSelectedIndex(info.getDestinationDisplayId());
			cmbxQuality.setSelectedItem(qualityIdToName(info.getQuality()));
			frequencySlider.setValue((int)info.getFrequency());
			ckbxEmulateMouse.setSelected(info.isMouseEmulated());
		}
	}
	
	private Rectangle getDefaultLauncherBounds() {
		Rectangle display = getDisplayBounds(0);
		return new Rectangle(display.width/2 - 150 + display.x,
				display.height/2 - 162 + display.y , 300, 325);
	}
	
	private String[] getMonitors() {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = g.getScreenDevices();

		String[] deviceStrs = new String[devices.length];
		for (int i = 0; i < devices.length; i++) {
			int width = devices[i].getDisplayMode().getWidth();
			int height = devices[i].getDisplayMode().getHeight();
			deviceStrs[i] = "Display " + i + " (" + width + "x" + height + ")";
		}
		return deviceStrs;
	}
	
	private Rectangle getDisplayBounds(int id) {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = g.getScreenDevices();
		return devices[id].getDefaultConfiguration().getBounds();
	}
	
	private int displayStringToId(String str) {
		try {
			return Integer.parseInt(str.split(" ")[1]);
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	private String qualityIdToName(int id) {
		for (int i = 0; i < QUALITY_TYPES.length; i++) {
			if (id == QUALITY_TYPES[i]) {
				return QUALITY_NAMES[i];
			}
		}
		return QUALITY_NAMES[0];
	}
	
	private int getSourceDisplay() {
		return displayStringToId((String)cmbxSource.getSelectedItem());
	}
	
	private int getDestinationDisplay() {
		return displayStringToId((String)cmbxDest.getSelectedItem());
	}
	
	private int getQuality() {
		return QUALITY_TYPES[cmbxQuality.getSelectedIndex()];
	}
	
	private int getFrequency() {
		return frequencySlider.getValue();
	}
	
	private boolean isMouseEmulated() {
		return ckbxEmulateMouse.isSelected();
	}
	
	private LauncherInfo getLauncherInfo() {
		return new LauncherInfo(captureArea, getDisplayBounds(getDestinationDisplay()),
				getSourceDisplay(), getDestinationDisplay(), getQuality(),
				getFrequency(), isMouseEmulated());
	}
	
	private void createCaptureAreaWindow() {
		int sourceMonitor = getSourceDisplay();
		if (sourceMonitor != -1) {
			AreaCaptureWindow window = new AreaCaptureWindow(getSourceDisplay(), captureArea);
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
		int inputCode = validateInput();
		if (inputCode == 0) {
			try {
				LauncherInfo info = getLauncherInfo();
				LauncherInfoManager.storeLauncherInfo(info);
				RecaptureFrame frame = new RecaptureFrame(info);
				frame.setUndecorated(true);
				frame.setVisible(true);
			} catch (AWTException | NativeHookException e) {
				e.printStackTrace();
			}
		}
		else if (inputCode == 1) {
			JOptionPane.showMessageDialog(this, "Source display capture area is unset.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
		else if (inputCode == 2) {
			JOptionPane.showMessageDialog(this, "Source display cannot be the same as " +
					"destination display.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private int validateInput() {
		if (captureArea == null) {
			return 1; // error; capture area unset
		}
		else if (getSourceDisplay() == getDestinationDisplay()) {
			return 2; // error; src = dest
		}
		return 0; // valid
	}
}

