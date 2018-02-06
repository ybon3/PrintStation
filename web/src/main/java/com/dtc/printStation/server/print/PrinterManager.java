package com.dtc.printStation.server.print;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dtc.printStation.shared.vo.Printer;

/**
 * 管理系統層級印表機服務的 class，提供以下功能：
 * <ul>
 * 	<li>管理印表機使用權
 * 	<li>取得印表機服務（{@link DtcPrintService}）
 * </ul>
 *
 * <p>
 * Singleton Pattern
 */
public class PrinterManager {
	private static final Boolean DEFAULT_APPLY = Boolean.TRUE; //預設可使用
	private static PrinterManager instance = new PrinterManager();

	public static PrinterManager getInstance(){
		return instance;
	}

	// --------------------
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private final ConcurrentHashMap<String, Boolean> printerApplyMap = new ConcurrentHashMap<>();

	private PrinterManager() {
		//TODO 取得已儲存的 apply 資訊
		HashMap<String, Boolean> persistence = new HashMap<>();
		persistence.put("Microsoft XPS Document Writer", Boolean.FALSE); //預設不可使用
		persistence.put("Fax", Boolean.FALSE); //預設不可使用

		for (String name : persistence.keySet()) {
			logger.info(name + " : " + persistence.get(name));
		}

		printerApplyMap.putAll(persistence);
	}

	/**
	 * 取得指定印表機的列印服務
	 * <p>
	 * @return null 印表機不存在或者不開放使用
	 */
	public DtcPrintService getDtcPrintService(String printerName) {
		if (printerName == null) { return null; }

		printerName = printerName.trim();

		if (printerName.isEmpty()) { return null; }

		PrintService service = findPrintService(printerName);

		if (service == null) { return null; }

		//Found PrintService, check apply
		return isApply(printerName) ? new DtcPrintService(service) : null;
	}

	/**
	 * 取得印表機資訊
	 */
	public List<Printer> getPrinter() {
		return getPrinterByWmic();
	}

	/**
	 * 測試印表機，不論該印表機是否 apply
	 * @throws PrintException
	 */
	public void testPrinter(String printerName) throws PrintException {
		PrintService service = findPrintService(printerName.trim());

		if (service == null) { throw new PrintException("指定的印表機不存在"); }

		new DtcPrintService(service).print(new TestPage());
	}

	public boolean updateApply(String printerName, boolean isApply) {
		try {
			//TODO write persistence
			printerApplyMap.put(printerName, isApply);
			logger.info("updateApply() [" + printerName + "] " + isApply);
			return true;
		} catch (Exception e) {
			logger.error("updateApply()", e);
			return false;
		}
	}

	/**
	 * 取得指定 printerName 的 {@link PrintService}
	 */
	public static PrintService findPrintService(String printerName) {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

		for (PrintService printService : printServices) {
			if (printService.getName().trim().equals(printerName.trim())) {
				return printService;
			}
		}

		return null;
	}

	/**
	 * 取得印表機資訊
	 */
	private List<Printer> getPrinterByWmic() {
		List<Printer> list = new ArrayList<>();

		Runtime rt = Runtime.getRuntime();
		String execCommand = "wmic printer get Name, PortName";

		try {
			Process p = rt.exec(execCommand);
			p.waitFor();//會將目前的 Task block 住，直到 Process 執行完畢

			StringWriter writer = new StringWriter();
			IOUtils.copy(p.getInputStream(), writer, "MS950");

			//Parse wmic output to Properties
			List<Properties> wmicList = parseWmicOutput(writer.toString());

			//Parse Properties to Printer
			for (Properties prop : wmicList) {
				String printerName = prop.getProperty("Name");

				list.add(new Printer(
					printerName,
					prop.getProperty("PortName"),
					isApply(printerName)
				));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 取得印表機資訊
	 */
	private List<Printer> getPrinterByJavaApi() {
		List<Printer> list = new ArrayList<>();
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

		for (PrintService printService : printServices) {
			String printerName = printService.getName().trim();
			Boolean apply = isApply(printerName);
			list.add(new Printer(printerName, apply));
		}

		return list;
	}

	/**
	 * 將 wmic 指定的輸出結果轉換成 List<Properties>
	 * <p>
	 * Only for Windows system
	 */
	private List<Properties> parseWmicOutput(String in) {
		List<Properties> list = new ArrayList<>();

		String[] lines = in.split(System.getProperty("line.separator"));

		if (lines.length == 0) { return list; }

		if (lines[0].trim().length() == 0) { return list; }

		//解析 Column header
		List<String> columns = new ArrayList<>();
		List<Integer> columnsIndex = new ArrayList<>();
		String header = lines[0];

		int index = 0;
		while (index < header.length()) {
			int space = header.indexOf(" ", index);

			columnsIndex.add(index);
			if (space == -1) {
				columns.add(header.substring(index).trim());
				break; //結束
			}

			columns.add(header.substring(index, space).trim());

			//找到下一個非空白的 index
			while (++space < header.length()) {
				if (header.charAt(space) != ' ' &&
					header.charAt(space) != '\r') {
					break;
				}
			}

			index = space;
		}

//		System.out.println("\ncolumns size: " + columns.size());
//		for (int i = 0; i < columns.size(); i++) {
//			System.out.println(columns.get(i) + " | " + columnsIndex.get(i));
//		}

		//解析 row data
		for (int i = 1; i < lines.length; i++) {
			if (lines[i].trim().length() == 0) {
				continue; //pass
			}

			//處理每一個 column
			Properties prop = new Properties();
			for (int j = 0; j < columns.size(); j++) {
				if (j + 1 < columns.size()) {
					prop.setProperty(
						columns.get(j),
						lines[i].substring(columnsIndex.get(j), columnsIndex.get(j+1)).trim()
					);
				} else {
					prop.setProperty(
						columns.get(j),
						lines[i].substring(columnsIndex.get(j)).trim()
					);
				}
			}

			list.add(prop);
		}

		return list;
	}

	/**
	 * 檢查指定的 printerName 是否可使用，若不存在就回傳預設值
	 */
	private boolean isApply(String printerName) {
		Boolean apply = printerApplyMap.get(printerName);

		if (apply == null) {
			apply = DEFAULT_APPLY;
			printerApplyMap.put(printerName, apply);
		}

		return apply.booleanValue();
	}
}
