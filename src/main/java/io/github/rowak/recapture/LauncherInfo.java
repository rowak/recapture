package io.github.rowak.recapture;

import java.awt.Rectangle;

public class LauncherInfo {
	private Rectangle captureArea;
	private Rectangle displayBounds;
	private int sourceDisplayId;
	private int destinationDisplayId;
	private int quality;
	private long frequency;
	private boolean emulateMouse;
	
	public LauncherInfo(Rectangle captureArea, Rectangle displayBounds, int sourceDisplayId,
			int destinationDisplayId, int quality, long frequency, boolean emulateMouse) {
		this.captureArea = captureArea;
		this.displayBounds = displayBounds;
		this.sourceDisplayId = sourceDisplayId;
		this.destinationDisplayId = destinationDisplayId;
		this.quality = quality;
		this.frequency = frequency;
		this.emulateMouse = emulateMouse;
	}
	
	public static LauncherInfo fromString(String str) {
		LauncherInfo info = new LauncherInfo(null, null, -1, -1, -1, -1, false);
		String[] props = str.split("\n");
		if (props.length != 7) {
			return null; // invalid number of properties
		}
		for (String prop : props) {
			String[] data = prop.split("=");
			if (data.length != 2) {
				return null; // invalid property
			}
			switch (data[0]) {
				case "source":
					info.captureArea = deserializeRectangle(data[1]);
					break;
				case "destination":
					info.displayBounds = deserializeRectangle(data[1]);
					break;
				case "sourceId":
					info.sourceDisplayId = Integer.parseInt(data[1]);
					break;
				case "destinationId":
					info.destinationDisplayId = Integer.parseInt(data[1]);
					break;
				case "quality":
					info.quality = Integer.parseInt(data[1]);
					break;
				case "frequency":
					info.frequency = Integer.parseInt(data[1]);
					break;
				case "emulateMouse":
					info.emulateMouse = Boolean.parseBoolean(data[1]);
					break;
			}
		}
		return info;
	}
	
	public Rectangle getCaptureArea() {
		return captureArea;
	}
	
	public Rectangle getDisplayBounds() {
		return displayBounds;
	}
	
	public int getSourceDisplayId() {
		return sourceDisplayId;
	}
	
	public int getDestinationDisplayId() {
		return destinationDisplayId;
	}
	
	public int getQuality() {
		return quality;
	}
	
	public long getFrequency() {
		return frequency;
	}
	
	public boolean isMouseEmulated() {
		return emulateMouse;
	}
	
	@Override
	public String toString() {
		StringBuilder info = new StringBuilder();
		info.append(String.format("source=%s\n", serializeRectangle(captureArea)));
		info.append(String.format("destination=%s\n", serializeRectangle(displayBounds)));
		info.append(String.format("sourceId=%d\n", sourceDisplayId));
		info.append(String.format("destinationId=%d\n", destinationDisplayId));
		info.append(String.format("quality=%d\n", quality));
		info.append(String.format("frequency=%d\n", frequency));
		info.append(String.format("emulateMouse=%b\n", emulateMouse));
		return info.toString();
	}
	
	private static String serializeRectangle(Rectangle rect) {
		if (rect != null) {
			return String.format("%d,%d,%d,%d", rect.x, rect.y, rect.width, rect.height);
		}
		return "";
	}
	
	private static Rectangle deserializeRectangle(String str) {
		Rectangle rect = new Rectangle();
		String[] values = str.split(",");
		try {
			rect.x = Integer.parseInt(values[0]);
			rect.y = Integer.parseInt(values[1]);
			rect.width = Integer.parseInt(values[2]);
			rect.height = Integer.parseInt(values[3]);
			return rect;
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}
}
