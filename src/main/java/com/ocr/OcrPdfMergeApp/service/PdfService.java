package com.ocr.OcrPdfMergeApp.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.awt.image.BufferedImage;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class PdfService {

     public File processAndMergePdfs(List<MultipartFile> files) throws IOException, TesseractException {
        List<File> ocrPdfFiles = new ArrayList<>();
        System.out.println("------ocrPdfFiles ------");

        for (MultipartFile file : files) {
            System.out.println("------For Loop -- ------");

            File ocrPdf = convertScannedPdfToSearchablePdf(file);
            ocrPdfFiles.add(ocrPdf);
        }

        return mergePdfs(ocrPdfFiles);
    }

    private File convertScannedPdfToSearchablePdf(MultipartFile file) throws IOException, TesseractException {
        System.out.println("------OCR Process Started------");

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or not provided.");
        }

        String tessDataPath = "C:/Program Files/Tesseract-OCR/tessdata";
        File tessDataFile = new File(tessDataPath + "/eng.traineddata");

        if (!tessDataFile.exists()) {
            throw new FileNotFoundException("eng.traineddata not found in " + tessDataPath);
        }

        File tempOcrPdf = File.createTempFile("ocr_", ".pdf");

        try (PDDocument document = PDDocument.load(file.getInputStream());
            PDDocument searchablePdf = new PDDocument()) {

            if (document.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("Uploaded PDF has no pages.");
            }

            PDFRenderer pdfRenderer = new PDFRenderer(document);
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessDataPath);
            tesseract.setLanguage("eng");

            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(i, 300);
                if (image == null) {
                    System.out.println("Skipping null image for page " + i);
                    continue;
                }

                String ocrText = tesseract.doOCR(image);
                ocrText = ocrText.replace("ﬂ", "fl").replace("ﬁ", "fi"); // Handle ligatures

                if (ocrText.isBlank()) {
                    System.out.println("No OCR text found for page " + i);
                    continue;
                }

                PDPage page = new PDPage();
                searchablePdf.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(searchablePdf, page)) {
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(25, 750);

                    String[] lines = ocrText.split("\n");
                    for (String line : lines) {
                        try {
                            contentStream.showText(line);
                            contentStream.newLineAtOffset(0, -15);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Skipping unsupported text on page " + i + ": " + line);
                        }
                    }

                    contentStream.endText();
                }
            }

            searchablePdf.save(tempOcrPdf);
        }

        System.out.println("------OCR Process Completed------");
        return tempOcrPdf;
    }

    private File mergePdfs(List<File> pdfFiles) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        System.out.println("------ merger ------");
        File mergedFile = File.createTempFile("merged_", ".pdf");
        
        System.out.println("------ mergedFile ------");

        for (File pdfFile : pdfFiles) {
            merger.addSource(pdfFile);
        }

        merger.setDestinationFileName(mergedFile.getAbsolutePath());
        merger.mergeDocuments(null);

        System.out.println("------ Mergedc successfully ------");
        return mergedFile;
    }    
}
