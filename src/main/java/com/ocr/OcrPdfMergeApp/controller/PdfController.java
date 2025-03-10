package com.ocr.OcrPdfMergeApp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ocr.OcrPdfMergeApp.service.PdfService;
import net.sourceforge.tess4j.TesseractException;

@Controller
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/")
    public String showUploadForm() {
        System.out.println("------Get Page ------");
        return "upload";
    }

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadPdfs(@RequestParam("files") List<MultipartFile> files, Model model) {
        try {
            System.out.println("------Upload ------");

            File mergedPdf = pdfService.processAndMergePdfs(files);
            System.out.println("------mergedPdf ------");

            byte[] pdfBytes = Files.readAllBytes(mergedPdf.toPath());

            System.out.println("------pdfBytes ------");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "merged_document.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            model.addAttribute("message", "Error processing PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
