package com.dtc.printStation.server;

import java.net.URLConnection;

public class Util {
	public static String getMimeType(String fname) {
		return URLConnection.guessContentTypeFromName(fname);
	}
}
