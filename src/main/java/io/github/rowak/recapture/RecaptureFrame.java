package io.github.rowak.recapture;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

public class RecaptureFrame extends JFrame {
	private int quality;
	private long interval;
	private boolean emulateMouse;
	private Timer timer;
	private Robot robot;
	private BufferedImage img;
	private BufferedImage buff;
	private Graphics buffGraphics;
	private Rectangle captureArea;
	private Rectangle displayBounds;
	private Dimension scaledResolution;
	private Point mouseLoc;
	private MouseCaptureListener mouseCaptureListener;
	
	public RecaptureFrame(LauncherInfo launcherInfo) throws NativeHookException, AWTException {
		captureArea = launcherInfo.getCaptureArea();
		displayBounds = launcherInfo.getDisplayBounds();
		quality = launcherInfo.getQuality();
		interval = launcherInfo.getFrequency();
		emulateMouse = launcherInfo.isMouseEmulated();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, displayBounds.width, displayBounds.height);
		scaledResolution = getScaledResolution();
		robot = new Robot();
		buff = new BufferedImage(displayBounds.width, displayBounds.height, BufferedImage.TYPE_INT_RGB);
		buffGraphics = buff.getGraphics();
		registerGlobalListeners();
		start();
	}
	
	private void start() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				img = robot.createScreenCapture(captureArea);
				repaint();
			}
		}, 0, interval);
	}
	
	@Override
	public void paint(Graphics g) {
		buffGraphics.setColor(Color.BLACK);
		int x_off = (displayBounds.width - scaledResolution.width)/2;
		int y_off = (displayBounds.height - scaledResolution.height)/2;
		buffGraphics.fillRect(0, 0, displayBounds.width, y_off); // top bar
		buffGraphics.fillRect(0, (displayBounds.height-y_off), displayBounds.width, y_off); // bottom bar
		buffGraphics.fillRect(0, 0, x_off, displayBounds.height); // left bar
		buffGraphics.fillRect((displayBounds.width-x_off), 0, x_off, displayBounds.height); // right bar
		Image scaledImage = scaleImageFit(img, quality);
		buffGraphics.drawImage(scaledImage, x_off, y_off, this);
		if (emulateMouse && mouseLoc != null) {
			double scale = getScale();
	//		int scaledMouseX = (int)((mouseLoc.x - CAPTURE_AREA.x + X_OFFSET)*scale) + x_off - 10;
	//		int scaledMouseY = (int)((mouseLoc.y - CAPTURE_AREA.y + Y_OFFSET)*scale) + y_off - 5;
			int scaledMouseX = (int)((mouseLoc.x - captureArea.x + displayBounds.width)*scale) + x_off - 10;
			int scaledMouseY = (int)((mouseLoc.y - captureArea.y)*scale) + y_off - 5;
			BufferedImage cursorImage = NativeCursor.getCursorImage();
			Image scaledCursor = scaleImageFactor(cursorImage, scale, quality);
			buffGraphics.drawImage(scaledCursor, scaledMouseX, scaledMouseY, this);
		}
		g.drawImage(buff, 0, 0, this);
	}
	
	private Image scaleImageFit(BufferedImage img, int scaleType) {
		double scaleFactor = getScale();
		int width = (int)(img.getWidth()*scaleFactor);
		int height = (int)(img.getHeight()*scaleFactor);
		return img.getScaledInstance(width, height, scaleType);
	}
	
	private Image scaleImageFactor(BufferedImage img, double factor, int scaleType) {
		return img.getScaledInstance((int)(img.getWidth()*factor), (int)(img.getHeight()*factor), scaleType);
	}
	
	private Dimension getScaledResolution() {
		double scale = getScale();
		return new Dimension((int)(captureArea.width*scale), (int)(captureArea.height*scale));
	}
	
	private double getScale() {
		double scaleFactorX = displayBounds.width/(double)captureArea.width;
		double scaleFactorY = displayBounds.height/(double)captureArea.height;
		return (scaleFactorX <= scaleFactorY) ? scaleFactorX : scaleFactorY;
	}
	
	private void registerGlobalListeners() throws NativeHookException, AWTException {
		GlobalScreen.registerNativeHook();
		mouseCaptureListener = new MouseCaptureListener();
		GlobalScreen.addNativeMouseMotionListener(mouseCaptureListener);
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.WARNING);
	}
	
	class MouseCaptureListener implements NativeMouseMotionListener {
		@Override
		public void nativeMouseDragged(NativeMouseEvent e) {
			mouseLoc = e.getPoint();
		}

		@Override
		public void nativeMouseMoved(NativeMouseEvent e) {
			mouseLoc = e.getPoint();
		}
	}
}

