package io.github.rowak.recapture;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LauncherInfoManager {
	private static final String ROOT_DIR = getRootDir();
	private static final String FILE_LOCATION = getFileLocation();
	
	public static LauncherInfo loadLauncherInfo() {
		createDir();
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(new File(FILE_LOCATION)));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException | NullPointerException e) {
				e.printStackTrace();
			}
		}
		return LauncherInfo.fromString(sb.toString());
	}
	
	public static void storeLauncherInfo(LauncherInfo info) {
		createDir();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(FILE_LOCATION)));
			writer.write(info.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getRootDir() {
		return System.getProperty("user.home") + "/.recapture";
	}
	
	private static String getFileLocation() {
		return ROOT_DIR + "/prefs.txt";
	}
	
	private static void createDir() {
		File dir = new File(ROOT_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
}
