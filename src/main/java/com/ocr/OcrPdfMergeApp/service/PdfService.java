package com.ocr.OcrPdfMergeApp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService 
{

    // private static final String INPUT_DIR = BASE_DIR + File.separator + "input";
    // private static final String OUTPUT_DIR = BASE_DIR + File.separator + "output";

    private static final Pattern FILE_PATTERN = Pattern.compile("OCR_(\\d{1,2})-(\\d{1,2})-(\\d{4})-(\\w+)-(\\d{4})\\.pdf");
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ConcurrentHashMap<String, Integer> dateFileCount = new ConcurrentHashMap<>();
    
    public String processwithoutsnapPdfs(List<MultipartFile> pdfFiles) {
        if (pdfFiles.isEmpty()) {
            return "No PDFs selected.";
        }

        File inputDir = new File("D:\\Feb-1993\\input");
        File outputDir = new File("D:\\Feb-1993\\output");
    
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

                String inputPath = inputPdf.getAbsolutePath().replace("\\", "/").replace("D:", "/mnt/d");
                String outputPath = outputPdf.getAbsolutePath().replace("\\", "/").replace("D:", "/mnt/d");

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

       
        return "OCR completed.";
    }

    public String processPdfs(List<MultipartFile> pdfFiles , String destinationPath) 
    {
        if (pdfFiles.isEmpty()) {
            return "No PDFs selected.";
        }
    
        File inputDir = new File(destinationPath+File.pathSeparator+"input");
        
        String msg=isValidDirectory(destinationPath);

        if(!"Correct".equals(msg))
        {
        	return "Incorrect Destination Path ?? "+msg;
        }
        
        File outputDir = new File(destinationPath);
    
        if (!inputDir.exists()) inputDir.mkdirs();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    
        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
    
        List<String> processedPdfs = new ArrayList<>();
    
        for (MultipartFile file : pdfFiles) {
            try {
                File inputPdf = new File(inputDir, file.getOriginalFilename());
                System.out.println("File saved to: " + inputPdf.getAbsolutePath());
                file.transferTo(inputPdf);
    
                File outputPdf = new File(outputDir, "OCR_" + file.getOriginalFilename());
    
                String inputPath = inputPdf.getAbsolutePath().replace("\\", "/").replace("D:", "/mnt/d");
                String outputPath = outputPdf.getAbsolutePath().replace("\\", "/").replace("D:", "/mnt/d");
    
                // Define the command as an array
                String[] command;
                if (isWindows) {
                    command = new String[] {
                        "wsl", "bash", "-c",
                        "/snap/bin/ocrmypdf --force-ocr " + inputPath + " " + outputPath
                    };
                } else {
                    command = new String[] {
                        "ocrmypdf", "--force-ocr",
                        inputPdf.getAbsolutePath(), outputPdf.getAbsolutePath()
                    };
                }
    
                System.out.println("Command: " + String.join(" ", command));
    
                // Execute the command
                Process process = Runtime.getRuntime().exec(command);
    
                // Capture output and errors
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    
                String line;
                StringBuilder outputLog = new StringBuilder();
                StringBuilder errorLog = new StringBuilder();
    
                while ((line = reader.readLine()) != null) {
                    outputLog.append(line).append("\n");
                    System.out.println("OCR Output: " + line);
                }
                while ((line = errorReader.readLine()) != null) {
                    errorLog.append(line).append("\n");
                    System.out.println("OCR Error: " + line);
                }
    
                int exitCode = process.waitFor();
                System.out.println("OCR Process exited with code: " + exitCode);
    
                if (exitCode == 0) {
                    processedPdfs.add(outputPdf.getAbsolutePath());
                } else {
                    System.out.println("Output Log:\n" + outputLog.toString());
                    System.out.println("Error Log:\n" + errorLog.toString());
                    return "Error processing PDF: " + file.getOriginalFilename() + ". Check logs for details.";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Error processing PDFs: " + e.getMessage();
            }
        }
    
        return "OCR completed ";
    }

	public String directMergePdfs(List<String> pdfPaths, String outputPath) { 
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
    
            return "PDFs merged successfully to: " + outputPath;
    
        } catch (IOException e) { 
            return "Error merging PDFs: " + e.getMessage(); 
        } 
    }
    
    public String mergePdfs(List<MultipartFile> files, String destinationPath) throws Exception {

        PDFMergerUtility pdfMerger = new PDFMergerUtility();
        String msg=isValidDirectory(destinationPath);
        
        if(!"Correct".equals(msg))
        {
        	return "Incorrect Destination Path ?? "+msg;
        }
        
        // Ensure the directory exists
        File directory = new File(destinationPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a unique output file name
        String outputFileName = "merged_" + System.currentTimeMillis() + ".pdf";
        String outputFilePath = destinationPath + File.separator + outputFileName;

        List<File> tempFiles = new ArrayList<>();

        try {
            // Convert MultipartFiles to temporary files and add to the merger
            for (MultipartFile file : files) {
                File tempFile = File.createTempFile("temp_", ".pdf");
                tempFiles.add(tempFile);

                // Copy content to the temporary file
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(file.getBytes());
                }

                // Add the temporary file to the merger
                pdfMerger.addSource(tempFile);
            }

            // Merge and save the final PDF
            try (FileOutputStream outputStream = new FileOutputStream(outputFilePath)) {
                pdfMerger.setDestinationStream(outputStream);
                pdfMerger.mergeDocuments(null);
            }

            return "Merge Pdf Save at: "+outputFilePath;

        } finally {
            // Clean up temporary files
            for (File tempFile : tempFiles) {
                if (tempFile.exists()) {
                    tempFile.delete();
                }
            }
        }
    }
    
    public String isValidDirectory(String destinationPath) 
    {
        if (destinationPath == null || destinationPath.trim().isEmpty()) 
        {
            return "Invalid path: Path is null or empty.";
        }

        File directory = new File(destinationPath);

        // Check if the path exists and is a directory
        if (!directory.exists()) {
            return "Directory does not exist.";
        }

        if (!directory.isDirectory()) {
            return "Path is not a directory.";
        }

        // Check write permissions
        if (!directory.canWrite()) {
            return "No write permissions for the directory.";
        }

        return "Correct";
    }

    public String renamePdfs(List<MultipartFile> files, String destinationPath) {
        List<CompletableFuture<String>> futures = files.stream()
            .map(file -> CompletableFuture.supplyAsync(() -> processFile(file, destinationPath), executorService))
            .collect(Collectors.toList());
    
        List<String> renamedFiles = futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    
        return "Rename Successfully";
    }

    private String processFile(MultipartFile file, String uploadDir) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".pdf")) {
            return null;
        }

        String dateKey = extractDateKey(originalFilename);
        if (dateKey == null) {
            return null;
        }

        int sequence = dateFileCount.compute(dateKey, (key, val) -> (val == null) ? 1 : val + 1);
        String newFilename = generateNewFilename(originalFilename, sequence);
        
        if (newFilename != null && renameFileInPlace(file, newFilename, uploadDir)) {
            return newFilename;
        }
        return null;
    }

    private String extractDateKey(String originalFilename) {
        Matcher matcher = FILE_PATTERN.matcher(originalFilename);
        if (matcher.matches()) {
            String day = String.format("%02d", Integer.parseInt(matcher.group(1)));
            String month = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String year = matcher.group(3);
            String location = matcher.group(4);
            return day + month + year + "-" + location;
        }
        return null;
    }

    private String generateNewFilename(String originalFilename, int sequence) {
        Matcher matcher = FILE_PATTERN.matcher(originalFilename);
        if (matcher.matches()) {
            String day = String.format("%02d", Integer.parseInt(matcher.group(1)));
            String month = String.format("%02d", Integer.parseInt(matcher.group(2)));
            String year = matcher.group(3);
            String location = matcher.group(4);
            return day + month + year + "-" + location + "-" + String.format("%02d", sequence) + ".pdf";
        }
        return null;
    }

    private boolean renameFileInPlace(MultipartFile file, String newFilename, String uploadDir) {
        try {
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            File originalFile = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(originalFile); // Save the uploaded file first

            Path originalPath = originalFile.toPath();
            Path newPath = new File(uploadDir, newFilename).toPath();

            Files.move(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}