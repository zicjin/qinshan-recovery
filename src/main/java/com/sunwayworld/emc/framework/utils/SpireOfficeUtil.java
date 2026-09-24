/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.framework.utils;

import com.spire.doc.Document;
import com.spire.doc.ToPdfParameterList;
import com.spire.license.LicenseProvider;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfImage;
import com.spire.xls.FileFormat;
import com.spire.xls.Workbook;
import com.spire.xls.collections.WorksheetsCollection;
import com.spire.xls.core.IWorksheet;
import com.spire.xls.core.spreadsheet.XlsWorksheet;
import com.sunwayworld.emc.framework.utils.AsposeExcelUtils;
import com.sunwayworld.framework.utils.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SpireOfficeUtil {
    private static final Logger logger = LogManager.getLogger(SpireOfficeUtil.class);
    private static final String SPIRE_LICENSE_KEY = "0WMBAOwo2TYqUQh1+sMBJJCIgEQiyRTRJRiBt6Sq92jUNGmkT8rro6dNHEZUyrvkatIplpbTYaNdLIEWFXD7ou6awGEByl3KKvTaJm1u8GCZf1KCUOoFwUXsCCb0CCQQ6yfxefZK2iNCPSPqZ4qE7F7Ugymq3IZ9xSu5JhqqROCznrm7W8s+fylAvzNgZ+7071TejSfEgevtjTmvJJ8YLUbEnBKFWQgoBRNCNl8x1BLsOf59zAuWY546qLFX741julwd0RKplkUZP2oe0Lcl5brZj+Ru/DHr/Yj15fT63GnhRDpXGffyeSbP+gpxbV+OOL2cJ+lCxSwTHfjuaB1XksXZnhHse4hx/OnBkdM9DwiiZ74xROR2gSa2JDD5cuqgO59azlG518mcMWZKSNDNr8O7389Wo9G4KbcmJF7sXMPq0q/vl2GckHV+TGgGnxlfup4K1YIZ7ua9wtxuBplTs+1QCAU4A6afiCv6SaHgqm/aXW72T4YUznQsNEUSYUQ2MHHLR+9C27ZLIMZ0bFEh4j8ZywCNv2dNY1ALwXLHAh/aQ14+QrBj3sZB9IYDDjQZRTQfI+ZQd15J002a4K07+453SKVpYuWcpRH65GCmyNkq69hboX7Ow0KtqyXk6pyPA4HFrWYcxVugRHGyMaKIHFmvNVT+5v1EyfkViAoLcDq4oWZt0eCm30PU8saeXO7KouT5+CAxonYPQeea0RZLs+Z13vEEDYGDGbpUvmYFSAl6nt+hiEFOjEGrJomY7hP3f++vIyiakjxSLyb63mYb8jvNX20iEmhTvdgVrx+ZPVnGm4l1jEez+OI5ZLe68mJ1OJ3nRRdwo2Mrhe51aORaCVSBek1r1sloSYPXmCihrUSaGgORnTWAdO6NxwPfsvJA1l6ZiRBi22X9ALokYO/P+OGi0/IILqh9EAiCcAigyGWpIik49USPJHvnyn2n8rP2MsClbb1tlNokyVP0OU2O/WskqDqB6EPgJJ7iopejLyY1dJbty2r8tjYPeyR2r3hBUzvZsDtgdxjcXngMOqqB7WjNhmvYdUXaQV7E3ya8P1V38RP538ulhpFPOPdqp4Fzy1OSQvqbyTRFwAJ0etAAv9Nok1K+D7StXVE/DSoIcS8YXno/Rg/NBFLkQYAQrRV/5xtBhpZjBXIhgufxI+NP6TE++uuCgvX8uLsmcjuK93PRXavfut4zlzgiIqBUE1N/Ok0N3fcIIZzQBlsmXa3mC9C3V4KmY0yEBlfX1lQ00eY+gQnn54KQG/4Mpscw9v9OWk8jVd5JTFYIxvrGfN3N3rJBn0fiG86/m46vfrDkAsPOdhOKawMpESmsaKCdyd5zpFgrzH8oxSh7uNvx4gTgRs+4nY7i3mvSbtgvAOLXOhzorDJA0qyN3n9hm02kuNqo41RsWp8Ze5AGEY+mvlO6fbC1AAZOXFZh7kT626mWQyah9aQEn4Wr3DiB9dwB9eEbmazKwNtmiWOcEh12/ynNtezgYe2JiSB852//YKY21gnH278hw++htFoWGRBDlQBv";

    public static void wordToPdf(File file, File pdfFile) {
        Document doc = new Document();
        doc.loadFromFile(file.getAbsolutePath());
        ToPdfParameterList ppl = new ToPdfParameterList();
        ppl.isEmbeddedAllFonts(true);
        ppl.setDisableLink(true);
        doc.setJPEGQuality(100);
        doc.saveToFile(pdfFile.getAbsolutePath(), ppl);
    }

    public static void excelToPdf(File file, File pdfFile) {
        Workbook wb = new Workbook();
        wb.loadFromFile(file.getAbsolutePath());
        wb.getConverterSetting().setSheetFitToPage(true);
        wb.saveToFile(pdfFile.getAbsolutePath(), FileFormat.PDF);
    }

    public static void excelSheetToPdf(File file, int sheetIndex, File pdfFile) {
        Workbook wb = new Workbook();
        wb.loadFromFile(file.getAbsolutePath());
        WorksheetsCollection workSheets = wb.getWorksheets();
        if (sheetIndex > workSheets.size() - 1) {
            return;
        }
        IWorksheet sheet = workSheets.get(sheetIndex);
        wb.getConverterSetting().setSheetFitToPage(true);
        ((XlsWorksheet)sheet).saveToPdf(pdfFile.getAbsolutePath());
    }

    public static void imageToPdf(File file, File pdfFile) {
        PdfDocument pdf = new PdfDocument();
        PdfPageBase page = pdf.getPages().add();
        PdfImage image = PdfImage.fromFile(file.getAbsolutePath());
        double widthFitRate = image.getPhysicalDimension().getWidth() / page.getCanvas().getClientSize().getWidth();
        double heightFitRate = image.getPhysicalDimension().getHeight() / page.getCanvas().getClientSize().getHeight();
        double fitRate = Math.max(widthFitRate, heightFitRate);
        double fitWidth = image.getPhysicalDimension().getWidth() / fitRate;
        double fitHeight = image.getPhysicalDimension().getHeight() / fitRate;
        page.getCanvas().drawImage(image, 0.0, 30.0, fitWidth, fitHeight);
        pdf.saveToFile(pdfFile.getAbsolutePath());
        pdf.close();
    }

    public static void fileToPdf(File file, File pdfFile) {
        if (SpireOfficeUtil.isImage(file)) {
            SpireOfficeUtil.imageToPdf(file, pdfFile);
            return;
        }
        if (SpireOfficeUtil.isExcel(file)) {
            SpireOfficeUtil.excelToPdf(file, pdfFile);
            if (pdfFile.length() == 0L) {
                AsposeExcelUtils.convertExcelToPdf(file, pdfFile);
            }
            return;
        }
        if (SpireOfficeUtil.isWord(file)) {
            SpireOfficeUtil.wordToPdf(file, pdfFile);
            return;
        }
        if (SpireOfficeUtil.isPdf(file)) {
            try {
                Files.copy(file.toPath(), pdfFile.toPath(), new CopyOption[0]);
            }
            catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            return;
        }
    }

    public static boolean isImage(File file) {
        String extension = FileUtils.getFileExtension(file).toLowerCase();
        return "jpg".equals(extension) || "jpeg".equals(extension) || "png".equals(extension) || "bmp".equals(extension) || "gif".equals(extension);
    }

    public static boolean isExcel(File file) {
        String extension = FileUtils.getFileExtension(file).toLowerCase();
        return "xls".equals(extension) || "xlsx".equals(extension);
    }

    public static boolean isWord(File file) {
        String extension = FileUtils.getFileExtension(file).toLowerCase();
        return "doc".equals(extension) || "docx".equals(extension);
    }

    public static boolean isPdf(File file) {
        String extension = FileUtils.getFileExtension(file).toLowerCase();
        return "pdf".equals(extension);
    }

    public static boolean isFileNameAnomaly(String fileName) {
        int pdfCount = 0;
        int index = 0;
        while ((index = fileName.indexOf(".pdf", index)) != -1) {
            ++pdfCount;
            index += ".pdf".length();
        }
        return pdfCount > 1;
    }

    public static void copyFile(File oldFile, File newFile) {
        try (FileInputStream fis = new FileInputStream(oldFile);
             FileOutputStream fos = new FileOutputStream(newFile);){
            int length;
            byte[] buffer = new byte[1024];
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
        catch (IOException e) {
            System.err.println("\u6587\u4ef6\u590d\u5236\u5931\u8d25: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        File file1 = new File("C:\\Users\\1\\Desktop\\\u7efc\u5408\u62a5\u544a-CQTW250010.docx");
        File file2 = new File("C:\\Users\\1\\Desktop\\666.pdf");
        SpireOfficeUtil.wordToPdf(file1, file2);
    }

    static {
        LicenseProvider.setLicenseKey(SPIRE_LICENSE_KEY);
    }
}
