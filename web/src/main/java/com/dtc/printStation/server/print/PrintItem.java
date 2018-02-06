package com.dtc.printStation.server.print;

import org.apache.commons.codec.binary.Base64;

import com.dtc.printStation.server.print.DtcPrintService.SupportFormat;

/**
 * 存放列印資料的 bean
 */
public class PrintItem {
	private byte[] binary;
	private String mimeType;
	private SupportFormat format;

	public PrintItem(String base64String, String mimeType) {
		this.binary = Base64.decodeBase64(base64String);
		this.mimeType = mimeType;
	}

	public PrintItem(byte[] binary, String mimeType) {
		this.binary = binary;
		this.mimeType = mimeType;
	}

	public byte[] getBinary() {
		return binary;
	}

	public void setBinary(byte[] binary) {
		this.binary = binary;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public SupportFormat getFormat() {
		if (format == null) {
			format = SupportFormat.fromValue(mimeType);
		}
		return format;
	}
}
