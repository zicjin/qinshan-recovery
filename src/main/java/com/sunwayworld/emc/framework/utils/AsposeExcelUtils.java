// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.framework.utils;

import com.aspose.cells.License;
import com.aspose.cells.Workbook;
import com.aspose.words.Document;
import com.sunwayworld.emc.framework.utils.SpireOfficeUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.time.LocalDateTime;

public class AsposeExcelUtils {
    private static boolean getLicense() {
        boolean result = false;
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("cell-license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void convertExcelToPdf(File file, File pdfFile) {
        if (!AsposeExcelUtils.getLicense()) {
            return;
        }
        Workbook wb = null;
        FileOutputStream fileOS = null;
        try {
            wb = new Workbook(file.getPath());
            fileOS = new FileOutputStream(pdfFile);
            wb.save((OutputStream)fileOS, 13);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (fileOS != null) {
                try {
                    fileOS.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (wb != null) {
                wb.dispose();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void convertWordToPdf(File file, File pdfFile) {
        if (!AsposeExcelUtils.getLicense()) {
            return;
        }
        Document doc = null;
        try {
            doc = new Document(file.getPath());
            doc.save(pdfFile.getAbsolutePath(), 40);
        }
        catch (Exception e) {
            e.printStackTrace();
            SpireOfficeUtil.wordToPdf(file, pdfFile);
        }
        finally {
            if (doc != null) {
                doc = null;
            }
        }
    }

    public static void fileToPdf(File file, File pdfFile) {
        if (SpireOfficeUtil.isImage(file)) {
            SpireOfficeUtil.imageToPdf(file, pdfFile);
            return;
        }
        if (SpireOfficeUtil.isExcel(file)) {
            AsposeExcelUtils.convertExcelToPdf(file, pdfFile);
            return;
        }
        if (SpireOfficeUtil.isWord(file)) {
            AsposeExcelUtils.convertWordToPdf(file, pdfFile);
            return;
        }
        if (SpireOfficeUtil.isPdf(file)) {
            try {
                Files.copy(file.toPath(), pdfFile.toPath(), new CopyOption[0]);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
    }

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now());
        File file1 = new File("C:\\Users\\1\\Desktop\\\u7efc\u5408\u62a5\u544a-CQTW250010.docx");
        File file2 = new File("C:\\Users\\1\\Desktop\\888.pdf");
        AsposeExcelUtils.fileToPdf(file1, file2);
        System.out.println(LocalDateTime.now());
    }
}
