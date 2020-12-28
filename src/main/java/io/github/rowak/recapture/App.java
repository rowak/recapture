package io.github.rowak.recapture;

import javax.swing.JFrame;

public class App extends JFrame {
	public static void main(String[] args) throws Exception {
		RecaptureFrame frame = new RecaptureFrame();
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}

