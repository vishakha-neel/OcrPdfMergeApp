package com.ocr.OcrPdfMergeApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class OcrPdfMergeAppApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(OcrPdfMergeAppApplication.class, args);
		System.out.println("Application Started Successfully .......");
	}

}
