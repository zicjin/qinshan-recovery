/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.utils;

import com.sunwayworld.cloud.module.printer.exception.PrinterException;
import com.sunwayworld.cloud.module.printer.printer.bean.CorePrinterPropertiesDTO;
import com.sunwayworld.cloud.module.printer.utils.PrinterPaperUtils;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.print.PrintService;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.standard.Sides;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;

public class PrinterUtils {
    public static void print(CorePrinterPropertiesDTO printerProperties) {
        CoreFileBean coreFile = (CoreFileBean)ApplicationContextHelper.getBean(CoreFileService.class).selectById(printerProperties.getFileId());
        if (!"PDF".equals(FileUtils.getFileExtension(coreFile.getName().toUpperCase()))) {
            throw new PrinterException("CORE.PRINTER.TIP.SUPPORT_PDF_ONLY");
        }
        Pair printServicePair = PrinterUtils.selectPrinterList().stream().filter(p -> ((String)p.getFirst()).equals(printerProperties.getPrinterName())).findAny().orElse(null);
        if (printServicePair == null) {
            throw new PrinterException("CORE.PRINTER.TIP.PRINTER_NOT_FOUND");
        }
        File printFile = null;
        CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
        printFile = FileUtils.createTempFile();
        fileManager.download(coreFile, printFile.toPath());
        if (SystemUtils.isWindows()) {
            PrinterUtils.winPrint(printerProperties, printServicePair, printFile);
        } else if (SystemUtils.isLinux()) {
            PrinterUtils.linuxPrint(printerProperties, printServicePair, printFile);
        } else {
            try {
                Files.deleteIfExists(printFile.toPath());
            }
            catch (IOException iOException) {
                // empty catch block
            }
            throw new PrinterException("CORE.PRINTER.TIP.SYSTEM_NOT_SUPPORT");
        }
    }

    public static List<Pair<String, PrintService>> selectPrinterList() {
        PrintService[] printServices;
        ArrayList<Pair<String, PrintService>> printerList = new ArrayList<Pair<String, PrintService>>();
        for (PrintService printService : printServices = PrinterJob.lookupPrintServices()) {
            printerList.add(Pair.of(printService.getName(), printService));
        }
        return printerList;
    }

    public static List<String> selectPrinterNameList() {
        return PrinterUtils.selectPrinterList().stream().map(Pair::getFirst).collect(Collectors.toList());
    }

    private static void winPrint(CorePrinterPropertiesDTO printerProperties, Pair<String, PrintService> printServicePair, File printFile) {
        PDDocument pdfDocument = null;
        try {
            pdfDocument = PDDocument.load(printFile);
            PDFPrintable pdfPrintable = new PDFPrintable(pdfDocument, Scaling.ACTUAL_SIZE);
            PageFormat pageFormat = new PageFormat();
            pageFormat.setOrientation("portrait".equals(printerProperties.getLayout()) ? 1 : 0);
            pageFormat.setPaper(PrinterPaperUtils.getPaper(printerProperties.getPaperSize()));
            Book book = new Book();
            book.append(pdfPrintable, pageFormat, pdfDocument.getNumberOfPages());
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setJobName(printServicePair.getFirst() + "_" + printFile.getName());
            printJob.setPrintService(printServicePair.getSecond());
            printJob.setPageable(book);
            HashPrintRequestAttributeSet printAttribute = new HashPrintRequestAttributeSet();
            if ("1".equals(printerProperties.getBothSidesPrint())) {
                printAttribute.add("long".equals(printerProperties.getBothSidesPrintMethod()) ? Sides.TWO_SIDED_LONG_EDGE : Sides.TWO_SIDED_SHORT_EDGE);
            } else {
                printAttribute.add(Sides.ONE_SIDED);
            }
            printJob.setCopies(printerProperties.getCopies());
            printJob.print(printAttribute);
        }
        catch (Exception e) {
            throw new PrinterException("CORE.PRINTER.TIP.PRINT_FAILED");
        }
        finally {
            try {
                Files.deleteIfExists(printFile.toPath());
            }
            catch (IOException iOException) {}
            try {
                if (null != pdfDocument) {
                    pdfDocument.close();
                }
            }
            catch (IOException iOException) {}
        }
    }

    private static void linuxPrint(CorePrinterPropertiesDTO printerProperties, Pair<String, PrintService> printServicePair, File printFile) {
        try {
            StringBuilder cmd = new StringBuilder("lp");
            cmd.append(" -d ").append(printerProperties.getPrinterName());
            cmd.append(" -n ").append(printerProperties.getCopies());
            if ("landscape".equals(printerProperties.getLayout())) {
                cmd.append(" -o landscape ");
            }
            if ("1".equals(printerProperties.getBothSidesPrint())) {
                cmd.append("long".equals(printerProperties.getBothSidesPrintMethod()) ? " -o sides=two-sided-long-edge " : " -o sides=two-sided-short-edge ");
            } else {
                cmd.append(" -o sides=one-sided ");
            }
            cmd.append(PrinterPaperUtils.getLinuxPaperSize(printerProperties.getPaperSize()));
            cmd.append(printFile.getPath());
            SystemUtils.exec(cmd.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new PrinterException("CORE.PRINTER.TIP.PRINT_FAILED");
        }
        finally {
            try {
                Files.deleteIfExists(printFile.toPath());
            }
            catch (IOException iOException) {}
        }
    }
}
