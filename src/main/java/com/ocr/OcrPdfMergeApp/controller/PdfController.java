package com.ocr.OcrPdfMergeApp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ocr.OcrPdfMergeApp.service.PdfService;

@Controller
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/")
    public String showUploadForm() {
        System.out.println("------Get Page ------");
        return "upload";
    }

    @PostMapping("/process")
    @ResponseBody
    public String processPdfs(@RequestParam("pdfFiles") List<MultipartFile> pdfFiles , @RequestParam("destinationpath") String destinationPath) {
        System.out.println("------Out Page ------");
        return pdfService.processPdfs(pdfFiles,destinationPath);
    }

}
