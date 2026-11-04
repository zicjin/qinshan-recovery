/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.spire.doc.Document;
import com.spire.pdf.graphics.PdfMargins;
import com.spire.pdf.htmlconverter.qt.HtmlConverter;
import com.spire.pdf.htmlconverter.qt.Size;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;

public class SpirePdfUtils {
    public static void wordToPdf(Path wordFilePath, Path pdfFilePath) {
        FileUtils.makeDirs(pdfFilePath);
        Document document = null;
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(pdfFilePath.toFile()));
             InputStream nis = Files.newInputStream(wordFilePath, new OpenOption[0]);){
            document = new Document();
            document.loadFromStream(nis, com.spire.doc.FileFormat.Auto);
            document.saveToStream((OutputStream)os, com.spire.doc.FileFormat.PDF);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        finally {
            if (null != document) {
                try {
                    document.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    public static void excelToPdf(Path excelPath, Path pdfFilePath) {
        FileUtils.makeDirs(pdfFilePath);
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(pdfFilePath.toFile()));
             InputStream nis = Files.newInputStream(excelPath, new OpenOption[0]);){
            Workbook wb = new Workbook();
            wb.loadFromStream(nis);
            wb.saveToStream((OutputStream)os, FileFormat.PDF);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void htmlContentToPdf(String htmlContent, Path pdfFilePath) {
        File temp = null;
        try {
            String searchPattern = "src=\"files";
            StringBuilder replacement = new StringBuilder();
            String serverAddress = ApplicationContextHelper.getServerAddress();
            String contextPath = ServletUtils.getContextPath();
            replacement.append("src=\"").append(serverAddress).append(contextPath).append("/files");
            htmlContent = StringUtils.replace(htmlContent, searchPattern, replacement.toString());
            temp = FileUtils.createTempFile("html");
            FileUtils.write(temp, htmlContent);
            SpirePdfUtils.htmlFileToPdf(temp.toPath(), pdfFilePath);
        }
        catch (Throwable throwable) {
            FileUtils.deleteQuietly(temp);
            throw throwable;
        }
        FileUtils.deleteQuietly(temp);
    }

    public static void htmlFileToPdf(Path htmlFilePath, Path pdfFilePath) {
        if (htmlFilePath == null || !Files.exists(htmlFilePath, new LinkOption[0])) {
            return;
        }
        FileUtils.makeDirs(pdfFilePath);
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(pdfFilePath.toFile()));){
            HtmlConverter.setPluginPath("spire-html2pdf-plugins");
            HtmlConverter.convert(htmlFilePath.toString(), os, true, 1000000, new Size(1000.0f, 1300.0f), new PdfMargins(0.0f));
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }
}
