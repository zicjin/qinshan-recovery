package com.sunwayworld.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.text.FileToTextExtractor;
import com.sunwayworld.framework.io.text.FileToTextExtractorFactory;

/**
 * 文件相关的工具类
 * 
 * @author zhangjr@sunwayworld.com 2017年7月7日
 *
 */
public final class FileUtils {
    public static final String FILE_ROOT_DIR_NAME = "files";

    public static final void makeDirs(Path regularFilePath) {
        if (regularFilePath == null) {
            return;
        }

        Path dirPath = regularFilePath.getParent();

        if (dirPath != null && !Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static final void write(File file, String content) {
        createFile(file);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            writer.write(content);
            writer.flush();
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final void write(File file, InputStream is) {
        createFile(file);
        try (FileOutputStream fos = new FileOutputStream(file);) {
            byte[] buffer = new byte[4096048];
            int size = 0;
            while ((size = is.read(buffer)) != -1) {
                fos.write(buffer, 0, size);
            }
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final void write(Path regularFilePath, String content) {
        makeDirs(regularFilePath);

        try (BufferedWriter writer = Files.newBufferedWriter(regularFilePath)) {
            writer.write(content);
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }
    
    public static final void write(Path regularFilePath, InputStream is) {
        makeDirs(regularFilePath);
        
        try (OutputStream os = Files.newOutputStream(regularFilePath);) {
            byte[] buffer = new byte[4096];
            int size = 0;
            
            while ((size = is.read(buffer)) != -1) {
                os.write(buffer, 0, size);
            }
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final List<String> readAllLines(File file) {
        try (FileInputStream fis = new FileInputStream(file);) {
            String content = StringUtils.read(fis);

            return Arrays.asList(StringUtils.regexSplit(content, "(\n|\r)+"));
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final File createTempFile() {
        try {
            return File.createTempFile(StringUtils.randomUUID(), "");
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final File createTempFile(String extension) {
        try {
            if (extension != null && !StringUtils.startsWith(extension, ".")) {
                extension = "." + extension;
            }

            return File.createTempFile(StringUtils.randomUUID(), extension);
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    /**
     * create the given file with directories if not exists
     */
    public static final void createFile(Path path) {
        if (Files.exists(path)) {
            return;
        }

        makeDirs(path);

        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    /**
     * create the given file with directories if not exists
     */
    public static final void createFile(File file) {
        if (file.exists()) {
            return;
        }

        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    public static final String getFileExtension(String fileName) {
        return getFileExtension(Paths.get(fileName));
    }

    public static final String getFileExtension(File file) {
        return getFileExtension(file.toPath());
    }

    public static final String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();

        if (fileName.indexOf(".") == -1) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    public static boolean isImage(File file) {
        String extension = getFileExtension(file).toLowerCase();
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension)
                || "bmp".equals(extension) || "gif".equals(extension);
    }

    public static boolean isExtension(File file, String extension) {
        return extension.equalsIgnoreCase(getFileExtension(file).toLowerCase());
    }

    /**
     * 获取不含后缀的文件名
     */
    public static final String getFileBaseName(File file) {
        return getFileBaseName(file.toPath());
    }

    /**
     * 获取不含后缀的文件名
     */
    public static final String getFileBaseName(Path path) {
        String fileName = path.getFileName().toString();
        return getFileBaseName(fileName);
    }

    /**
     * 获取不含后缀的文件名
     */
    public static String getFileBaseName(String fileName) {
        if (fileName.indexOf(".") == -1) {
            return fileName;
        }

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    public static String getFileNameWithNewExtension(String fileName, String newExtension) {
        return getFileBaseName(fileName) + "." + newExtension;
    }

    /**
     * 删除文件或文件夹，如果是文件夹，将会删除所有所属的文件和文件夹
     */
    public static void deleteRecursively(Path path) {
        if (Files.isDirectory(path)) {
            try (Stream<Path> stream = Files.list(path)) {
                stream.forEach(p -> deleteRecursively(p));

                Files.deleteIfExists(path);
            } catch (IOException ioe) {
                throw new FileException(ioe);
            }
        } else {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ioe) {
                throw new FileException(ioe);
            }
        }
    }

    public static void deleteQuietly(File file) {
        if (file != null) {
            try {
                file.delete();
            } catch (Exception ex) {
                /* ignore */
            }
        }
    }

    /**
     * 指定的{@code url}是否是在jar内部的
     */
    public static final boolean isInJarURL(URL url) {
        return StringUtils.contains(url.getPath(), ".jar!");
    }

    /**
     * 获取指定文件夹{@code dir}下所有文件
     */
    public static List<File> getFileList(Path dir) {
        if (!Files.isDirectory(dir)) {
            return CollectionUtils.emptyList();
        }

        List<File> fileList = new ArrayList<File>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    fileList.add(file.toFile());
                    return FileVisitResult.CONTINUE;
                }
            });

            return fileList;
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    public static final String makeValidFilename(String name) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);

            switch (c) {
            case ' ':
                sb.append("_");
                break;
            case '\\':
            case '/':
            case ':':
            case '<':
            case '>':
            case '|':
            case '?':
            case '*':
            case '"':
                break;
            default:
                sb.append(c);
                break;
            }
        }

        return sb.toString();
    }

    /**
     * 将文件的字符集转化成指定的字符集
     */
    public static final void encode(Path srcPath, Path destPath, Charset targetCharset) {
        encode(srcPath.toString(), destPath.toString(), targetCharset);
    }

    public static final void encode(String srcFile, String destFile, Charset targetCharset) {
        try (FileInputStream fis = new FileInputStream(srcFile); DataInputStream in = new DataInputStream(fis);) {
            BufferedReader d = new BufferedReader(new InputStreamReader(in, EncodingUtils.detect(srcFile)));

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = d.readLine()) != null) {
                if (sb.length() != 0) {
                    sb.append(System.lineSeparator());
                }

                sb.append(line);
            }

            File outFile = new File(destFile);
            try {
                Files.createDirectories(outFile.getParentFile().toPath());

                outFile.createNewFile();
            } catch (IOException ioe) {
                throw new FileException(ioe);
            }

            try (Writer ow = new OutputStreamWriter(new FileOutputStream(outFile), targetCharset)) {
                ow.write(sb.toString());
            }
        } catch (IOException ioe) {
            throw new FileException(ioe);
        }
    }

    /**
     * 将文件的文本内容解析出来
     */
    public static String extractTextFromFile(File file) {
        switch (getFileExtension(file.getName()).toLowerCase()) {
        case "xls":
            return extractTextFromXls(file, false);
        case "xlsx":
            return extractTextFromXlsx(file, false);
        case "csv":
            return extractTextFromCsv(file);
        case "doc":
            return extractTextFromDoc(file);
        case "docx":
            return extractTextFromDocx(file);
        case "pdf":
            return extractTextFromPDF(file);
        case "txt":
        case "xml":
            return extractTextFromTextFile(file);
        default:
            return "";
        }
    }

    public static String extractText(InputStream is, String extension) {
        FileToTextExtractor extractor = FileToTextExtractorFactory.getFileToTextExtractor(extension);
        return extractor == null ? "" : extractor.extract(is);
    }

    /**
     * 将文件的文本内容解析出来，与extractTextFromFile(File)相比： 1. .xls/.xlsx文件会将sheet页的名称解析出来
     */
    public static String extractFullTextFromFile(File file) {
        switch (getFileExtension(file.getName()).toLowerCase()) {
        case "xls":
            return extractTextFromXls(file, true);
        case "xlsx":
            return extractTextFromXlsx(file, true);
        case "csv":
            return extractTextFromCsv(file);
        case "doc":
            return extractTextFromDoc(file);
        case "docx":
            return extractTextFromDocx(file);
        case "pdf":
            return extractTextFromPDF(file);
        case "txt":
        case "xml":
            return extractTextFromTextFile(file);
        default:
            return "";
        }
    }

    public static String extractFullText(InputStream is, String extension) {
        FileToTextExtractor extractor = FileToTextExtractorFactory.getFileToFullTextExtractor(extension);
        return extractor == null ? "" : extractor.extract(is);
    }

    // -------------------------------------------------------------------
    // 私有方法
    // -------------------------------------------------------------------
    private static String extractTextFromXls(File file, boolean includingSheetName) {
        HSSFWorkbook wb = null;
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));) {
            wb = new HSSFWorkbook(is);
            int sheetNum = wb.getNumberOfSheets();
            StringBuilder sb = new StringBuilder();

            for (int sheetNo = 0; sheetNo < sheetNum; sheetNo++) {
                HSSFSheet sheet = wb.getSheetAt(sheetNo);
                if (wb.isSheetHidden(sheetNo)) {
                    continue;
                }
                sb.append(includingSheetName ? "sheetName:" + sheet.getSheetName() + "\r\n" : "");
                int endRowNum = sheet.getLastRowNum() + 1;
                for (int rowNum = sheet.getFirstRowNum(); rowNum <= endRowNum; rowNum++) {
                    HSSFRow row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }

                    for (int cellIndex = row.getFirstCellNum(); cellIndex <= row.getLastCellNum(); cellIndex++) {
                        HSSFCell cell = row.getCell(cellIndex);
                        String value = ExcelHelper.getCellValue(cell, String.class);
                        if ("".equals(value)) {
                            value = "\t";
                        }
                        sb.append(" ").append(value);
                    }
                    sb.append("\r\n");
                }
            }
            return sb.toString();
        } catch (IOException ioe) {
            throw new FileException(ioe);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromXlsx(File file, boolean includingSheetName) {
        XSSFWorkbook wb = null;
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));) {
            wb = new XSSFWorkbook(is);
            int sheetNum = wb.getNumberOfSheets();
            StringBuilder sb = new StringBuilder();
            for (int sheetNo = 0; sheetNo < sheetNum; sheetNo++) {
                XSSFSheet sheet = wb.getSheetAt(sheetNo);
                if (wb.isSheetHidden(sheetNo)) {
                    continue;
                }
                sb.append(includingSheetName ? "sheetName:" + sheet.getSheetName() + "\r\n" : "");
                int endRowNum = sheet.getLastRowNum() + 1;
                for (int rowNum = sheet.getFirstRowNum(); rowNum < endRowNum; rowNum++) {
                    XSSFRow row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    int columnQty = row.getLastCellNum();
                    for (int cellIndex = 0; cellIndex < columnQty; cellIndex++) {
                        XSSFCell cell = row.getCell(cellIndex);
                        String value = ExcelHelper.getCellValue(cell, String.class);
                        if ("".equals(value)) {
                            value = "\t";
                        }
                        sb.append(" ").append(value);
                    }
                    sb.append("\r\n");
                }
            }
            return sb.toString();
        } catch (IOException ioe) {
            throw new FileException(ioe);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromCsv(File file) {
        BufferedReader br = null;
        DataInputStream in = null;
        StringBuilder sb = new StringBuilder();
        try {
            in = new DataInputStream(new FileInputStream(file));
            br = new BufferedReader(new InputStreamReader(in, EncodingUtils.detect(file)));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
            }
            return sb.toString();
        } catch (Exception e) {
            throw new FileException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromDoc(File file) {
        FileInputStream ins = null;
        WordExtractor ex = null;
        try {
            ins = new FileInputStream(file);
            ex = new WordExtractor(ins);
            return ex.getText();
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (ex != null) {
                try {
                    ex.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromDocx(File file) {
        FileInputStream ins = null;
        XWPFWordExtractor extractor = null;
        try {
            ins = new FileInputStream(file);
            XWPFDocument xdoc = new XWPFDocument(ins);
            extractor = new XWPFWordExtractor(xdoc);
            return extractor.getText();
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (extractor != null) {
                try {
                    extractor.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromTextFile(File file) {
        FileInputStream ins = null;
        BufferedInputStream bi = null;
        StringBuilder sb = new StringBuilder();
        try {
            ins = new FileInputStream(file);
            bi = new BufferedInputStream(ins);
            byte[] byteArray = new byte[4096];
            int length = 0;
            String charset = EncodingUtils.detect(file);
            while ((length = bi.read(byteArray)) != -1) {
                sb.append(new String(byteArray, 0, length, charset));
            }
            return sb.toString();
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (bi != null) {
                try {
                    bi.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    private static String extractTextFromPDF(File file) {
        PDDocument pdfDocument = null;
        try {
            pdfDocument = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(pdfDocument);
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }

    public static MultipartFile makeMultipartFile(File file) {
        DiskFileItem fileItem = new DiskFileItem(
                "file", "multipart/form-data", true, file.getName(), 0xA00000, file.getParentFile());
        try (OutputStream os = fileItem.getOutputStream();
             FileInputStream is = new FileInputStream(file)) {
            byte[] bytes = new byte[4096];
            int length;
            while ((length = is.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
        } catch (IOException ex) {
            throw new FileException(ex);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static MultipartFile makeMultipartFile(String name, InputStream is) {
        DiskFileItem fileItem = new DiskFileItem(
                "file", "multipart/form-data", true, name, 0xA00000, getTempDir());
        try (OutputStream os = fileItem.getOutputStream()) {
            byte[] bytes = new byte[4096];
            int length;
            while ((length = is.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
        } catch (IOException ex) {
            throw new FileException(ex);
        }
        return new CommonsMultipartFile(fileItem);
    }

    public static File getTempDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }
}
