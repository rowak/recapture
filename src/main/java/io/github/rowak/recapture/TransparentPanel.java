package io.github.rowak.recapture;

import java.awt.AWTException;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel {
	private Rectangle area;
	private Robot robot;
	
	public TransparentPanel(Rectangle area) {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		BufferedImage img = robot.createScreenCapture(area);
//		System.out.println(area + "  " + img.getWidth() + " " + img.getHeight());
		g.drawImage(img, 0, 0, this);
	}
	
	public void updateImage(Rectangle area) {
		this.area = area;
		repaint();
	}
}
