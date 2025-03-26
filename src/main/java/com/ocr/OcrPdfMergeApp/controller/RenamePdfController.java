package com.ocr.OcrPdfMergeApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ocr.OcrPdfMergeApp.service.PdfService;

@RestController
@EnableAsync
@RequestMapping("/rename")
public class RenamePdfController {

    @Autowired
    private PdfService pdfService;
	
	@PostMapping()
	public ResponseEntity<String> renamePdf( @RequestParam("files") List<MultipartFile> pdfFiles,
                             @RequestParam("renameDestinationpath") String destinationpath)
	{
		System.out.println("Renaming of pdf Start....");
		try {
            String savedFilePath = pdfService.renamePdfs(pdfFiles, destinationpath);
                	
            	return ResponseEntity.ok(savedFilePath);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error renaming PDFs: " + e.getMessage());
        }
		
	}
}
