package com.dtc.printStation.zoo;

/**
 * Base64 encode / decode 範例（這是網路上抓來的）
 *
 * @author Kushal Paudyal
 * www.icodejava.com
 *
 * This class can be used to Base64 encode or Base64 decode a file. It uses apache commons codec library.
 * The library used for testing the functionality was commons-codec-1.4.jar
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;

public class FileCodecBase64 {

	private static final boolean IS_CHUNKED = true;

	/**
	 * This method converts the content of a source file into Base64 encoded data and saves that to a target file.
	 * If isChunked parameter is set to true, there is a hard wrap of the output  encoded text.
	 */
	public static void encode(String sourceFile, String targetFile, boolean isChunked) throws Exception {

		byte[] base64EncodedData = Base64.encodeBase64(loadFileAsBytesArray(sourceFile), isChunked);

		writeByteArraysToFile(targetFile, base64EncodedData);
	}

	public static void decode(String sourceFile, String targetFile) throws Exception {

		byte[] decodedBytes = Base64.decodeBase64(loadFileAsBytesArray(sourceFile));

		writeByteArraysToFile(targetFile, decodedBytes);
	}

	public static void decode(String base64String, File targetFile) throws Exception {

		byte[] decodedBytes = Base64.decodeBase64(base64String);

		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(targetFile));
		writer.write(decodedBytes);
		writer.flush();
		writer.close();
	}

	/**
	 * This method loads a file from file system and returns the byte array of the content.
	 *
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static byte[] loadFileAsBytesArray(String fileName) throws Exception {
		File file = new File(fileName);
		int length = (int) file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();
		return bytes;
	}

	/**
	 * This method writes byte array content into a file.
	 *
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	public static void writeByteArraysToFile(String fileName, byte[] content) throws IOException {

		File file = new File(fileName);
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(file));
		writer.write(content);
		writer.flush();
		writer.close();
	}
}
