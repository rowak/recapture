package io.github.rowak.recapture;

import java.awt.Rectangle;

public class LauncherInfo {
	private Rectangle captureArea;
	private Rectangle displayBounds;
	private int quality;
	private int frequency;
	
	public LauncherInfo(Rectangle captureArea, Rectangle displayBounds, int quality, int frequency) {
		this.captureArea = captureArea;
		this.displayBounds = displayBounds;
		this.quality = quality;
		this.frequency = frequency;
	}
	
	public Rectangle getCaptureArea() {
		return captureArea;
	}
	
	public Rectangle getDisplayBounds() {
		return displayBounds;
	}
	
	public int getQuality() {
		return quality;
	}
	
	public int getFrequency() {
		return frequency;
	}
}
