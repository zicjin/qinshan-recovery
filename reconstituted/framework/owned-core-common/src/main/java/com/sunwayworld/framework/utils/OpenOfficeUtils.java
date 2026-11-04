/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.EncodingUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

public class OpenOfficeUtils {
    private static final String OFFICE_HOME = SystemUtils.getWorkingPath() + "/openoffice4";
    private static final String FILE_PDF = "pdf";
    private static final String FILE_TXT = "txt";
    private static final int[] port = new int[]{8100};
    private static final DefaultOfficeManagerConfiguration configuration = new DefaultOfficeManagerConfiguration();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void convertToPdf(FilePathDTO filePath, FilePathDTO pdfFilePath) {
        OfficeManager officeManager = null;
        try {
            officeManager = configuration.buildOfficeManager();
            officeManager.start();
            FileUtils.createFile(FilePathManager.getLocalPath(pdfFilePath));
            if (FILE_TXT.equalsIgnoreCase(FileUtils.getFileExtension(filePath.getFileName())) && !"UTF-8".equals(EncodingUtils.detect(filePath.getFileName()))) {
                FilePathDTO utf8CodeFilePath = FilePathDTO.of(pdfFilePath.getScope().name(), pdfFilePath.getTimestamp(), pdfFilePath.getIrregularName(), StringUtils.removeEndIgnoreCase(pdfFilePath.getFileName(), FILE_PDF) + FILE_TXT);
                FileUtils.encode(FilePathManager.getLocalPath(filePath), FilePathManager.getLocalPath(utf8CodeFilePath), StandardCharsets.UTF_8);
                filePath = utf8CodeFilePath;
            }
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
            converter.convert(FilePathManager.getLocalPath(filePath).toFile(), FilePathManager.getLocalPath(pdfFilePath).toFile());
        }
        finally {
            if (officeManager != null) {
                try {
                    officeManager.stop();
                }
                catch (Exception exception) {}
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void convertToPdf(String inputFile, String outputFile) {
        OfficeManager officeManager = null;
        try {
            officeManager = configuration.buildOfficeManager();
            officeManager.start();
            File outFile = new File(outputFile);
            FileUtils.createFile(outFile);
            if (FILE_TXT.equalsIgnoreCase(FileUtils.getFileExtension(inputFile)) && !"UTF-8".equals(EncodingUtils.detect(inputFile))) {
                String outTxtFile = StringUtils.removeEnd(outputFile, ".pdf") + "." + FILE_TXT;
                FileUtils.encode(Paths.get(inputFile, new String[0]), Paths.get(outTxtFile, new String[0]), StandardCharsets.UTF_8);
                inputFile = outTxtFile;
            }
            OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
            converter.convert(new File(inputFile), outFile);
        }
        finally {
            if (officeManager != null) {
                try {
                    officeManager.stop();
                }
                catch (Exception exception) {}
            }
        }
    }

    public static final FilePathDTO convertToPdf(FilePathDTO filePath) {
        if (FILE_PDF.equalsIgnoreCase(FileUtils.getFileExtension(filePath.getFileName()))) {
            return filePath;
        }
        FilePathDTO pdfFilePath = FilePathDTO.of(FileScope.view.name(), filePath.getTimestamp(), filePath.getIrregularName(), FileUtils.getFileNameWithNewExtension(filePath.getFileName(), FILE_PDF));
        Path pdfPath = FilePathManager.getLocalPath(pdfFilePath);
        if (Files.isRegularFile(pdfPath, new LinkOption[0]) && Files.exists(pdfPath, new LinkOption[0])) {
            return pdfFilePath;
        }
        OpenOfficeUtils.convertToPdf(filePath, pdfFilePath);
        return pdfFilePath;
    }

    static {
        if (SystemUtils.isLinux()) {
            SystemUtils.exec("chmod +x " + OFFICE_HOME + "/program/soffice.bin");
        }
        configuration.setOfficeHome(OFFICE_HOME);
        configuration.setPortNumbers(port);
        configuration.setTaskExecutionTimeout(300000L);
        configuration.setTaskQueueTimeout(86400000L);
    }
}
