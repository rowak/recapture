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
	private final int X_OFFSET = 1920; // horizontal offset for extra monitors
	private final int Y_OFFSET = 0; // vertical offset for extra monitors
//	private final Rectangle CAPTURE_AREA = new Rectangle(0+X_OFFSET, 0+Y_OFFSET, 1920, 1080);
	private final Rectangle CAPTURE_AREA = new Rectangle(320+X_OFFSET, 189+Y_OFFSET, 1280, 720);
	private final Dimension DISPLAY_RESOLUTION = new Dimension(1920, 1200);
	private final Dimension SCALED_RESOLUTION = getScaledResolution();
	private final long INTERVAL = 2;
	
	private Timer timer;
	private Robot robot;
	private BufferedImage img;
	private BufferedImage buff;
	private Graphics buffGraphics;
	private Point mouseLoc;
	private MouseCaptureListener mouseCaptureListener;
	
	public RecaptureFrame(LauncherInfo launcherInfo) throws NativeHookException, AWTException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, DISPLAY_RESOLUTION.width, DISPLAY_RESOLUTION.height);
		robot = new Robot();
		buff = new BufferedImage(DISPLAY_RESOLUTION.width, DISPLAY_RESOLUTION.height, BufferedImage.TYPE_INT_RGB);
		buffGraphics = buff.getGraphics();
		registerGlobalListeners();
		start();
	}
	
	private void start() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				img = robot.createScreenCapture(CAPTURE_AREA);
				repaint();
			}
		}, 0, INTERVAL);
	}
	
	@Override
	public void paint(Graphics g) {
		buffGraphics.setColor(Color.BLACK);
		int x_off = (DISPLAY_RESOLUTION.width - SCALED_RESOLUTION.width)/2;
		int y_off = (DISPLAY_RESOLUTION.height - SCALED_RESOLUTION.height)/2;
		buffGraphics.fillRect(0, 0, DISPLAY_RESOLUTION.width, y_off); // top bar
		buffGraphics.fillRect(0, (DISPLAY_RESOLUTION.height-y_off), DISPLAY_RESOLUTION.width, y_off); // bottom bar
//		g.fillRect(0, 0, x_off, DISPLAY_RESOLUTION.height); // left bar
//		g.fillRect(DISPLAY_RESOLUTION.width-x_off, 0, x_off, DISPLAY_RESOLUTION.height); // right bar
		Image scaledImage = scaleImageFit(img, Image.SCALE_REPLICATE);
//		Image scaledImage = scaleImageFit(img);
		buffGraphics.drawImage(scaledImage, x_off, y_off, this);
		BufferedImage cursorImage = NativeCursor.getCursorImage();
		double scale = getScale();
		int scaledMouseX = (int)((mouseLoc.x - CAPTURE_AREA.x + X_OFFSET)*scale) + x_off - 10;
		int scaledMouseY = (int)((mouseLoc.y - CAPTURE_AREA.y + Y_OFFSET)*scale) + y_off - 5;
		Image scaledCursor = scaleImageFactor(cursorImage, scale, Image.SCALE_REPLICATE);
		buffGraphics.drawImage(scaledCursor, scaledMouseX, scaledMouseY, this);
		g.drawImage(buff, 0, 0, this);
	}
	
//	private Image scaleImageFit(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
////		BufferedImage dbi = null;
////		if (sbi != null) {
////			dbi = new BufferedImage(dWidth, 1080, imageType);
//////			Graphics2D g = dbi.createGraphics();
//////			double scale = (fWidth <= fHeight) ? fWidth : fHeight;
//////			AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
////			ResampleOp rsp = new ResampleOp(dWidth, 1080);
////			rsp.setUnsharpenMask(AdvancedResizeOp.UnsharpenMask.Normal);
//////			rsp.setFilter(ResampleFilters.);
////			rsp.filter(sbi, dbi);
//////			g.drawRenderedImage(sbi, at);
////		}
////		return dbi;
//		return sbi.getScaledInstance(dWidth, 1080, Image.SCALE_REPLICATE);
//	}
	
//	private Image scaleImageFit(BufferedImage img) {
//		double scaleFactor = getScale();
//		int width = (int)(img.getWidth()*scaleFactor);
//		int height = (int)(img.getHeight()*scaleFactor);
//		BufferedImage dbi = null;
//		dbi = new BufferedImage(width, height, img.getType());
//		Graphics2D g = dbi.createGraphics();
//		AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
//		g.drawRenderedImage(img, at);
//		return dbi;
//	}
	
	private Image scaleImageFit(BufferedImage img, int scaleType) {
		double scaleFactor = getScale();
		int width = (int)(img.getWidth()*scaleFactor);
		int height = (int)(img.getHeight()*scaleFactor);
		return img.getScaledInstance(width, height, scaleType);
	}
	
	private Image scaleImageFactor(BufferedImage img, double factor, int scaleType) {
		return img.getScaledInstance((int)(img.getWidth()*factor), (int)(img.getHeight()*factor), scaleType);
	}
	
//	private BufferedImage scaleImageFit(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
//		BufferedImage dbi = null;
//		if (sbi != null) {
//			dbi = new BufferedImage(dWidth, dHeight, imageType);
//			Graphics2D g = dbi.createGraphics();
//			double scale = (fWidth <= fHeight) ? fWidth : fHeight;
//			AffineTransform at = AffineTransform.getScaleInstance(scale, scale);
//			g.drawRenderedImage(sbi, at);
//		}
//		return dbi;
//	}
	
	private Dimension getScaledResolution() {
		double scale = getScale();
		return new Dimension((int)(CAPTURE_AREA.width*scale), (int)(CAPTURE_AREA.height*scale));
	}
	
	private double getScale() {
		double scaleFactorX = DISPLAY_RESOLUTION.width/(double)CAPTURE_AREA.width;
		double scaleFactorY = DISPLAY_RESOLUTION.height/(double)CAPTURE_AREA.height;
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

