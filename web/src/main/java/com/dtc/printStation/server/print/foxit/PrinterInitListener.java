package com.dtc.printStation.server.print.foxit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 不使用，Foxit 的 library 會過期，如果要使用請與業務聯繫
 * <p>
 * Max Jhuang: max_jhuang@foxitsoftware.com
 */
//@WebListener
public class PrinterInitListener implements ServletContextListener {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Common common;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		logger.info("========================= Try to load Foxit library =========================");

		try {
			String name = System.getProperty("os.name");
			String arch = System.getProperty("os.arch");
			String userDir = System.getProperty("user.dir");
			System.out.println("os.name = " + name);
			System.out.println("os.arch = " + arch);
			System.out.println("user.dir = " + userDir);

			String libName;
			if (name.toLowerCase().startsWith("win")) { //windows
				if (arch.contains("64")) {
					libName = "fsdk_java_win64.dll";
				} else {
					libName = "fsdk_java_win32.dll";
				}
			} else { //linux
				if (arch.contains("64")) {
					libName = "libfsdk_java_linux64.so";
				} else {
					libName = "libfsdk_java_linux32.so";
				}
			}

			System.out.println("libName = " + libName);
			File dll = new File(userDir, libName);
			Files.copy(
				PrinterInitListener.class.getClassLoader().getResourceAsStream(libName),
				dll.toPath(),
				StandardCopyOption.REPLACE_EXISTING
			);

			System.load(dll.getAbsolutePath());
		} catch (UnsatisfiedLinkError e) {
			logger.error("Native code library failed to load.\n" + e);
			System.exit(1);
		} catch (Exception e) {
			logger.error("contextInitialized()", e);
			System.exit(1);
		}

		common = new Common();
		common.initlize();
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (common != null) {
			common.release();
		}
	}
}
