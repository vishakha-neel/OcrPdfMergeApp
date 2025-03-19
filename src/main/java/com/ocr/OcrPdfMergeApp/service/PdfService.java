package com.ocr.OcrPdfMergeApp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

    // private static final String INPUT_DIR = BASE_DIR + File.separator + "input";
    // private static final String OUTPUT_DIR = BASE_DIR + File.separator + "output";
    
    public String processPdfs(List<MultipartFile> pdfFiles) {
        if (pdfFiles.isEmpty()) {
            return "No PDFs selected.";
        }

        File inputDir = new File("E:\\OCR-Pdfs\\input");
        File outputDir = new File("E:\\OCR-Pdfs\\output");
    
        if (!inputDir.exists()) inputDir.mkdirs();
        if (!outputDir.exists()) outputDir.mkdirs();

        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");

        List<String> processedPdfs = new ArrayList<>();

        for (MultipartFile file : pdfFiles) {
            try {
                File inputPdf = new File(inputDir, file.getOriginalFilename());
                System.out.println("File saved to: " + inputPdf.getAbsolutePath());
                file.transferTo(inputPdf); 

                File outputPdf = new File(outputDir, "OCR_" + file.getOriginalFilename());

                String inputPath = inputPdf.getAbsolutePath().replace("\\", "/").replace("E:", "/mnt/e");
                String outputPath = outputPdf.getAbsolutePath().replace("\\", "/").replace("E:", "/mnt/e");

                String command = isWindows ?
                    "wsl ocrmypdf --force-ocr " + inputPath + " " + outputPath :
                    "ocrmypdf --force-ocr " + inputPdf.getAbsolutePath() + " " + outputPdf.getAbsolutePath();

                System.out.println("Command : " + command);

                Process process = Runtime.getRuntime().exec(command);  

                // ✅ Capture the output and errors
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("OCR Output: " + line);
                }
                while ((line = errorReader.readLine()) != null) {
                    System.out.println("OCR Error: " + line);
                }

                int exitCode = process.waitFor();
                System.out.println("OCR Process exited with code: " + exitCode);

                if (exitCode == 0) {
                    processedPdfs.add(outputPdf.getAbsolutePath());
                } else {
                    return "Error processing PDF: " + file.getOriginalFilename();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error processing PDFs.";
            }
        }

        String dateTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String mergedPdfPath = outputDir + File.separator + "pdf_all_" + dateTimeStr + ".pdf";
        mergePdfs(processedPdfs, mergedPdfPath);

        return "OCR completed. Merged PDF: " + mergedPdfPath;
    }

    public void mergePdfs(List<String> pdfPaths, String outputPath) {
        try {

            PDFMergerUtility merger = new PDFMergerUtility();
            merger.setDestinationFileName(outputPath);
            
            for (String pdfPath : pdfPaths) {
                File pdfFile = new File(pdfPath);
                if (pdfFile.exists()) {
                    merger.addSource(pdfFile);
                } else {
                    System.out.println("File not found: " + pdfPath);
                }
            }            

            merger.mergeDocuments(null);
            
            System.out.println("PDFs merged successfully to: " + outputPath);
            
        } catch (IOException e) {
            System.err.println("Error merging PDFs: " + e.getMessage());
        }
    }

}
