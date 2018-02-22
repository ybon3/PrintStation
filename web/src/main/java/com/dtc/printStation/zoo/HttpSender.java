package com.dtc.printStation.zoo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import com.google.common.io.CharStreams;

/**
 * API Call Example: HTTP POST 打印請求
 */
public class HttpSender {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		HttpSender sender = new HttpSender();

		List<NameValuePair> paramList = new ArrayList<NameValuePair>();
		paramList.add(new BasicNameValuePair("pn", "Microsoft XPS Document Writer"));

		//file 1
		paramList.add(new BasicNameValuePair("type", "application/pdf"));
		paramList.add(new BasicNameValuePair("b64", IOUtils.toString(new FileInputStream("d:/big.pdf.txt"), "UTF-8")));

		//file 2
//		paramList.add(new BasicNameValuePair("type", "image/png"));
//		paramList.add(new BasicNameValuePair("b64", IOUtils.toString(new FileInputStream("d:/test.png.txt"), "UTF-8")));

		//file 3
//		paramList.add(new BasicNameValuePair("type", "image/jpeg"));
//		paramList.add(new BasicNameValuePair("b64", IOUtils.toString(new FileInputStream("d:/test.jpg.txt"), "UTF-8")));

		System.out.println("response: " + sender.postFormData("http://localhost:8080/PrintStation/print", paramList));
	}

	public String postFormData(String url, List<NameValuePair> paramList) {
		HttpPost postRequest = new HttpPost(url);
		postRequest.setEntity(new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8));

		HttpClient client = HttpClientBuilder.create().build();

		HttpResponse response = null;
		try {
			return getContentString(client.execute(postRequest));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			closeResponse(response);
		}
	}

	private void closeResponse(HttpResponse response) {
		if (response != null && response instanceof CloseableHttpResponse) {
			try {
				((CloseableHttpResponse) response).close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String getContentString(HttpResponse response) throws IOException {
		return CharStreams.toString(
			new InputStreamReader(
				response.getEntity().getContent(),
				StandardCharsets.UTF_8
			)
		);
	}
}
