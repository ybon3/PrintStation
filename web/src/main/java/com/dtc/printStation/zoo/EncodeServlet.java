package com.dtc.printStation.zoo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

/**
 * 提供將檔案轉換成 Base64 字串的服務
 */
@WebServlet(urlPatterns = "/encode")
@MultipartConfig
public class EncodeServlet extends HttpServlet {
	private static final long serialVersionUID = -43093894802984483L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		System.out.println("isMultipart: " + isMultipart);

		if (isMultipart) {
			// multipart/form-data 讀取
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			try {
				List<FileItem> items = upload.parseRequest(req);
				for (FileItem item : items) {
					if (!item.isFormField()) {
						byte[] base64EncodedData = Base64.encodeBase64(IOUtils.toByteArray(item.getInputStream()), true);

						resp.setContentType("text/plain");
						resp.setCharacterEncoding("UTF-8");
						IOUtils.copy(new ByteArrayInputStream(base64EncodedData), resp.getWriter(), "UTF-8");
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			}
		}
	}
}


