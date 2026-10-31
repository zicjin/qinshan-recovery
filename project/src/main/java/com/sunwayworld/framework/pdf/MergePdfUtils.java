package com.sunwayworld.framework.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
/**
 * 
 * @author magt
 * 多个pdf合成一个pdf工具类
 * 
 */
public class MergePdfUtils {
    public static void concatPDFs(List<File> files, String outputPath, String outputFileName) {
        String sep = java.io.File.separator;
        Document document = null;
        PdfCopy copy = null;
        PdfReader reader = null;
        try {
            document = new Document(new PdfReader(files.get(0).getAbsolutePath()).getPageSize(1));
            copy = new PdfCopy(document, new FileOutputStream(outputPath + sep + outputFileName));
            document.open();
            for (int i = 0; i < files.size(); i++) {
                reader = new PdfReader(files.get(i).getAbsolutePath());
                int numberOfPages = reader.getNumberOfPages();
                for (int j = 1; j <= numberOfPages; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("EMC.MODULE.BP.REPORT.REPORTS.TIP.CAN_NOT_MERGE_PDF", e);
        } catch (DocumentException e) {
            throw new RuntimeException("EMC.MODULE.BP.REPORT.REPORTS.TIP.CAN_NOT_MERGE_PDF", e);
        } finally {
            if (document != null)
                document.close();
            if (reader != null)
                reader.close();
            if (copy != null)
                copy.close();
        }
    }
}
