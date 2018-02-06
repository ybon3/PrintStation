package com.dtc.printStation.server;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.print.PrintException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtc.printStation.server.print.DtcPrintService;
import com.dtc.printStation.server.print.PrintItem;
import com.dtc.printStation.server.print.PrinterManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 提供遠端列印的 API，接受兩種上傳檔案方式（混合亦可）：
 * <ul>
 * 	<li>Http 規範的檔案上傳，允許多檔上傳（以附檔名識別檔案類型）
 * 	<li>將檔案採 base64 編碼以 Form field 方式傳遞，允許多筆（以  type 參數識別檔案類型）
 * </ul>
 *
 * 允許的檔案類型（MIME type）：
 * <ul>
 * 	<li>application/pdf
 * 	<li>image/png
 * 	<li>image/jpeg
 * </ul>
 *
 * <p>
 * Parameters:
 * <ul>
 * 	<li>pn: 印表機名稱（required）
 * 	<li>b64: 採 base64 編碼的檔案內容（optional）
 * 	<li>type: 檔案類型，與 b64 參數成對
 * 	<li>multipart/form-data 中，所有 type=file 的 form parameter 都會被當成是要列印的檔案（optional）
 * </ul>
 */
@WebServlet(urlPatterns = "/print")
@MultipartConfig
public class PrintServlet extends HttpServlet {
	private static final long serialVersionUID = -2814635535454689391L;

	//Request parameters constant
	private static final String PARAM_PRINTER = "pn";
	private static final String PARAM_BASE64 = "b64";
	private static final String PARAM_MIMETYPE = "type";

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private PrinterManager printerManager = PrinterManager.getInstance();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String printer = null;
		List<String> b64List = new ArrayList<>();
		List<String> typeList = new ArrayList<>();
		List<PrintItem> fileList = new ArrayList<>();

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		logger.debug("isMultipart: " + isMultipart);

		if (isMultipart) {
			// multipart/form-data 讀取
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			try {
				List<FileItem> items = upload.parseRequest(req);
				for (FileItem item : items) {
					if (item.isFormField()) {
						// read form field into b64List & typeList
						if (PARAM_PRINTER.equals(item.getFieldName())) {
							printer = item.getString();
						} else if (PARAM_BASE64.equals(item.getFieldName())) {
							b64List.add(item.getString());
						} else if (PARAM_MIMETYPE.equals(item.getFieldName())) {
							typeList.add(item.getString());
						}
					} else {
						// read file, parse to PrintItem
						if (item.get().length == 0) { continue; } //pass

						fileList.add(
							new PrintItem(
								item.get(),
								Util.getMimeType(item.getName())
							)
						);
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
				response(-5, e.getMessage(), resp);
				return;
			}
		} else {
			//一般 form field 讀取
			printer = req.getParameter(PARAM_PRINTER);
			String[] base64Files = req.getParameterValues(PARAM_BASE64);
			String[] base64Types = req.getParameterValues(PARAM_MIMETYPE);

			if (base64Files != null) {
				b64List.addAll(Arrays.asList(base64Files));
			}
			if (base64Types != null) {
				typeList.addAll(Arrays.asList(base64Types));
			}
		}

		if (b64List.size() != typeList.size()) {
			response(-1, "Number of parameter 'b64' and 'type' not equal", resp);
			return;
		}

		for (int i = 0; i < b64List.size(); i++) {
			if (b64List.get(i).trim().isEmpty()) {
				continue; //pass
			}

			fileList.add(new PrintItem(b64List.get(i), typeList.get(i)));
		}

		//----------------------------------------

		//參數檢核
		if (StringUtils.isBlank(printer)) {
			response(-1, "No specific printer name", resp);
			return;
		}

		if (fileList.isEmpty()) {
			response(-1, "No specific print item", resp);
			return;
		}

		DtcPrintService service = printerManager.getDtcPrintService(printer);
		if (service == null) {
			response(-2, "PrintService: " + printer + " unavailable", resp);
			return;
		}

		try {
			for (PrintItem item : fileList) {
				if (item.getFormat() == null) {
					response(-3, "Unsupported MIME Type: " + item.getMimeType(), resp);
					return;
				}

				service.print(item);
			}
			//TODO
			response(1, "交易序號尚未實作", resp);
		} catch (PrintException e) {
			e.printStackTrace();
			response(0, e.getMessage(), resp);
		}
	}

	private void response(int stat, String msg, HttpServletResponse response) throws IOException {
		HashMap<String,Object> result = new HashMap<>();
		result.put("stat", stat + "");
		result.put("msg", msg);
		responseJson(result, response);
	}

	/**
	 * 將 result 以 Json 的形式寫入 {@link HttpServletResponse}，並 flush
	 */
	private void responseJson(Object result, HttpServletResponse response) throws IOException {
		Gson gson = new GsonBuilder().create();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		Writer writer = response.getWriter();

		gson.toJson(
			result,
			writer
		);

		writer.flush();
	}
}


