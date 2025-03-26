package com.ocr.OcrPdfMergeApp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ocr.OcrPdfMergeApp.service.PdfService;

@RestController
public class SimplePdfMergerController {
	
	PdfService pdfService=new PdfService();
	
	@PostMapping("/merge")
	public ResponseEntity<String> mergerPdf( @RequestParam("files") List<MultipartFile> pdfFiles,
                             @RequestParam("destinationpath") String destinationpath)
	{
		System.out.println("Mergeing of pdf Start....");
		try {
            String savedFilePath = pdfService.mergePdfs(pdfFiles, destinationpath);
                	
            	return ResponseEntity.ok(savedFilePath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error merging PDFs: " + e.getMessage());
        }
		
	}
	
}