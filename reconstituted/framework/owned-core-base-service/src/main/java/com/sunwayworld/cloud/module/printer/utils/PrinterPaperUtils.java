/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.utils;

import java.awt.print.Paper;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrinterPaperUtils {
    private static Paper LETTER = null;
    private static Paper LEGAL = null;
    private static Paper A3 = null;
    private static Paper A4 = null;
    private static Paper A5 = null;
    private static Paper A6 = null;

    public static final List<String> selectPaperNameList() {
        Field[] declaredFields = PrinterPaperUtils.class.getDeclaredFields();
        return Arrays.asList(declaredFields).stream().map(Field::getName).collect(Collectors.toList());
    }

    public static final Paper getPaper(String paperSize) {
        if ("LETTER".equals(paperSize)) {
            return PrinterPaperUtils.getLetter();
        }
        if ("LEGAL".equals(paperSize)) {
            return PrinterPaperUtils.getLegal();
        }
        if ("A3".equals(paperSize)) {
            return PrinterPaperUtils.getA3();
        }
        if ("A4".equals(paperSize)) {
            return PrinterPaperUtils.getA4();
        }
        if ("A5".equals(paperSize)) {
            return PrinterPaperUtils.getA5();
        }
        if ("A6".equals(paperSize)) {
            return PrinterPaperUtils.getA6();
        }
        return null;
    }

    public static final Paper getPaper(int width, int height, int marginLeft, int marginRight, int marginTop, int marginBottom) {
        Paper paper = new Paper();
        paper.setSize(width, height);
        paper.setImageableArea(marginLeft, marginRight, width - (marginLeft + marginRight), height - (marginTop + marginBottom));
        return paper;
    }

    public static String getLinuxPaperSize(String paperSize) {
        if ("LETTER".equals(paperSize)) {
            return " -o media=Letter ";
        }
        if ("LEGAL".equals(paperSize)) {
            return " -o media=Legal ";
        }
        if ("A3".equals(paperSize)) {
            return " -o media=Custom.297x420mm ";
        }
        if ("A4".equals(paperSize)) {
            return " -o media=A4 ";
        }
        if ("A5".equals(paperSize)) {
            return " -o media=Custom.148x210mm ";
        }
        if ("A6".equals(paperSize)) {
            return " -o media=Custom.105x148mm ";
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final Paper getLetter() {
        if (LETTER != null) return LETTER;
        Class<PrinterPaperUtils> clazz = PrinterPaperUtils.class;
        synchronized (PrinterPaperUtils.class) {
            if (LETTER != null) return LETTER;
            LETTER = PrinterPaperUtils.getPaper(611, 791, 10, 0, 10, 0);
            // ** MonitorExit[var0] (shouldn't be in output)
            return LETTER;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final Paper getLegal() {
        if (LEGAL != null) return LEGAL;
        Class<PrinterPaperUtils> clazz = PrinterPaperUtils.class;
        synchronized (PrinterPaperUtils.class) {
            if (LEGAL != null) return LEGAL;
            LEGAL = PrinterPaperUtils.getPaper(611, 1007, 10, 0, 10, 0);
            // ** MonitorExit[var0] (shouldn't be in output)
            return LEGAL;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final Paper getA3() {
        if (A3 != null) return A3;
        Class<PrinterPaperUtils> clazz = PrinterPaperUtils.class;
        synchronized (PrinterPaperUtils.class) {
            if (A3 != null) return A3;
            A3 = PrinterPaperUtils.getPaper(841, 1190, 10, 0, 10, 0);
            // ** MonitorExit[var0] (shouldn't be in output)
            return A3;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final Paper getA4() {
        if (A4 != null) return A4;
        Class<PrinterPaperUtils> clazz = PrinterPaperUtils.class;
        synchronized (PrinterPaperUtils.class) {
            if (A4 != null) return A4;
            A4 = PrinterPaperUtils.getPaper(595, 842, 28, 28, 28, 28);
            // ** MonitorExit[var0] (shouldn't be in output)
            return A4;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final Paper getA5() {
        if (A5 != null) return A5;
        Class<PrinterPaperUtils> clazz = PrinterPaperUtils.class;
        synchronized (PrinterPaperUtils.class) {
            if (A5 != null) return A5;
            A5 = PrinterPaperUtils.getPaper(420, 595, 10, 0, 10, 0);
            // ** MonitorExit[var0] (shouldn't be in output)
            return A5;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private static final Paper getA6() {
        if (A6 != null) return A6;
        Class<PrinterPaperUtils> clazz = PrinterPaperUtils.class;
        synchronized (PrinterPaperUtils.class) {
            if (A6 != null) return A6;
            A6 = PrinterPaperUtils.getPaper(297, 420, 10, 0, 10, 0);
            // ** MonitorExit[var0] (shouldn't be in output)
            return A6;
        }
    }
}
