package io.github.rowak.recapture;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

public class AreaCaptureWindow extends JFrame {
	private int monitor;
	private Rectangle maxArea;
	private Rectangle selectedArea;
	private TransparentPanel tp;
	private AreaCaptureListener listener;
	
	public AreaCaptureWindow(int monitor) {
		this.monitor = monitor;
		selectedArea = getDefaultSelectionArea();
		initUI();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		selectedArea = getBounds();
	}
	
	public void setAreaCaptureListener(AreaCaptureListener listener) {
		this.listener = listener;
	}
	
	private Rectangle getMaxCaptureArea() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsConfiguration config = gs[monitor].getDefaultConfiguration();
		return config.getBounds();
	}
	
	private Rectangle getDefaultSelectionArea() {
		maxArea = getMaxCaptureArea();
		return new Rectangle(maxArea.width/2 - 200 + maxArea.x, maxArea.height/2 - 200 + maxArea.y, 400, 400);
	}
	
	private void initUI() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(selectedArea);

		setUndecorated(true);
		getRootPane().setBorder(new LineBorder(Color.RED, 10));
		
        setOpacity(0.5f);
        setAlwaysOnTop(true);
        
        setCursor(new Cursor(Cursor.MOVE_CURSOR));
        BoxDragListener boxDragListener = new BoxDragListener();
        addMouseListener(boxDragListener);
        addMouseMotionListener(boxDragListener);
        
        addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
					if (listener != null) {
//						selectedArea.x -= maxArea.x;
//						selectedArea.y -= maxArea.y;
						listener.onCapture(selectedArea);
					}
					dispose();
				}
			}
		});

		setVisible(true);
	}
	
	private class BoxDragListener extends MouseAdapter {
		private boolean resizing;
		private Point mouseLast;
		private Point lastLocation;
		
		public BoxDragListener() {
			lastLocation = new Point(selectedArea.x, selectedArea.y);
		}
		
		private void moveBox(Point mouse) {
			int xdiff = mouse.x - mouseLast.x;
			int ydiff = mouse.y - mouseLast.y;
			selectedArea.setLocation(lastLocation.x + xdiff,
					lastLocation.y + ydiff);
			setBounds(selectedArea);
			lastLocation = selectedArea.getLocation();
		}
		
		private int getEdge()
		{
			Point mouse = MouseInfo.getPointerInfo().getLocation();
			int leftEdge = selectedArea.x;
			int topEdge = selectedArea.y;
			int rightEdge = selectedArea.x + selectedArea.width;
			int bottomEdge = selectedArea.y + selectedArea.height;
			final int edgeWeight = 5;
			
			// check top edge
			if (mouse.x >= leftEdge && mouse.x <= rightEdge &&
					 mouse.y >= topEdge - edgeWeight && mouse.y <= topEdge + edgeWeight)
			{
				return 0;
			}
			// check right edge
			else if (mouse.x >= rightEdge - edgeWeight && mouse.x <= rightEdge + edgeWeight &&
					 mouse.y >= topEdge && mouse.y <= bottomEdge)
			{
				return 1;
			}
			// check bottom edge
			else if (mouse.x >= leftEdge && mouse.x <= rightEdge &&
					 mouse.y >= bottomEdge - edgeWeight && mouse.y <= bottomEdge + edgeWeight)
			{
				return 2;
			}
			// check left edge
			else if (mouse.x >= leftEdge - edgeWeight && mouse.x <= leftEdge + edgeWeight &&
				mouse.y >= topEdge && mouse.y <= bottomEdge)
			{
				return 3;
			}
			return -1;
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (getEdge() != -1) {
				resizing = true;
			}
			mouseLast = e.getPoint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			mouseLast = null;
			lastLocation = new Point(selectedArea.x, selectedArea.y);
			resizing = false;
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e) && getEdge() == -1 && !resizing) {
				moveBox(e.getPoint());
			}
		}
	}
}
