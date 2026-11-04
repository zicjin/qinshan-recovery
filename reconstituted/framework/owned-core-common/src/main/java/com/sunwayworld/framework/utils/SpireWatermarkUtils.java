/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.TextWatermark;
import com.spire.doc.collections.SectionCollection;
import com.spire.doc.documents.WatermarkLayout;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.PdfBrush;
import com.spire.pdf.graphics.PdfFontBase;
import com.spire.pdf.graphics.PdfRGBColor;
import com.spire.pdf.graphics.PdfSolidBrush;
import com.spire.pdf.graphics.PdfStringFormat;
import com.spire.pdf.graphics.PdfTextAlignment;
import com.spire.pdf.graphics.PdfTilingBrush;
import com.spire.pdf.graphics.PdfTrueTypeFont;
import com.spire.pdf.widget.PdfPageCollection;
import com.spire.presentation.IAutoShape;
import com.spire.presentation.PortionEx;
import com.spire.presentation.Presentation;
import com.spire.presentation.ShapeType;
import com.spire.presentation.collections.SlideCollection;
import com.spire.presentation.drawing.FillFormatType;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import com.spire.xls.collections.WorksheetsCollection;
import com.spire.xls.core.IWorksheet;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class SpireWatermarkUtils {
    private static String watermarkText = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.file.watermark.text", String.class, "Sunwayworld");
    private static Long watermarkSize = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.file.watermark.size", Long.TYPE, 50L);
    private static Color watermarkColor;
    private static WatermarkLayout watermarkLayout;
    private static final String WATERMARK_LAYOUT_DIAGONAL = "DIAGONAL";

    public static Path bakSourceFile(FilePathDTO sourceFilePath) throws IOException {
        String fileBaseName = FileUtils.getFileBaseName(sourceFilePath.getFileName());
        String fileExt = FileUtils.getFileExtension(sourceFilePath.getFileName());
        String targetFileName = fileBaseName + "_REVERT_WATERMARK_BAK." + fileExt;
        FilePathDTO targetFilePath = FilePathDTO.of(sourceFilePath.getScope().name(), sourceFilePath.getTimestamp(), sourceFilePath.getIrregularName(), targetFileName);
        Path sourceFileLocalPath = FilePathManager.getLocalPath(sourceFilePath);
        Path targetFileLocalPath = FilePathManager.getLocalPath(targetFilePath);
        FileUtils.makeDirs(targetFileLocalPath);
        Files.copy(sourceFileLocalPath, targetFileLocalPath, StandardCopyOption.REPLACE_EXISTING);
        return targetFileLocalPath;
    }

    public static void revertBakSourceFile(FilePathDTO targetFilePath) throws IOException {
        String fileBaseName = FileUtils.getFileBaseName(targetFilePath.getFileName());
        String fileExt = FileUtils.getFileExtension(targetFilePath.getFileName());
        String sourceFileName = fileBaseName + "_REVERT_WATERMARK_BAK." + fileExt;
        FilePathDTO sourceFilePath = FilePathDTO.of(targetFilePath.getScope().name(), targetFilePath.getTimestamp(), targetFilePath.getIrregularName(), sourceFileName);
        Path sourceFileLocalPath = FilePathManager.getLocalPath(sourceFilePath);
        Path targetFileLocalPath = FilePathManager.getLocalPath(targetFilePath);
        Files.copy(sourceFileLocalPath, targetFileLocalPath, StandardCopyOption.REPLACE_EXISTING);
        Files.delete(sourceFileLocalPath);
    }

    public static void insertTextWatermark(FilePathDTO sourceFilePath) throws IOException {
        String fileExt = FileUtils.getFileExtension(sourceFilePath.getFileName());
        Path sourceFileLocalPath = null;
        Path targetFileLocalPath = FilePathManager.getLocalPath(sourceFilePath);
        if ("doc".equalsIgnoreCase(fileExt) || "docx".equalsIgnoreCase(fileExt) || "docm".equalsIgnoreCase(fileExt)) {
            sourceFileLocalPath = SpireWatermarkUtils.bakSourceFile(sourceFilePath);
            SpireWatermarkUtils.word(sourceFileLocalPath, targetFileLocalPath);
        } else if ("xls".equalsIgnoreCase(fileExt) || "xlsx".equalsIgnoreCase(fileExt)) {
            sourceFileLocalPath = SpireWatermarkUtils.bakSourceFile(sourceFilePath);
            SpireWatermarkUtils.excel(sourceFileLocalPath, targetFileLocalPath);
        } else if ("ppt".equalsIgnoreCase(fileExt) || "pptx".equalsIgnoreCase(fileExt) || "pps".equalsIgnoreCase(fileExt) || "ppa".equalsIgnoreCase(fileExt)) {
            sourceFileLocalPath = SpireWatermarkUtils.bakSourceFile(sourceFilePath);
            SpireWatermarkUtils.ppt(sourceFileLocalPath, targetFileLocalPath);
        } else if ("pdf".equalsIgnoreCase(fileExt)) {
            sourceFileLocalPath = SpireWatermarkUtils.bakSourceFile(sourceFilePath);
            SpireWatermarkUtils.pdf(sourceFileLocalPath, targetFileLocalPath);
        }
    }

    public static void word(Path sourceFileLocalPath, Path targetFileLocalPath) {
        Document document = null;
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFileLocalPath.toFile()));
             InputStream nis = Files.newInputStream(sourceFileLocalPath, new OpenOption[0]);){
            document = new Document();
            document.loadFromStream(nis, FileFormat.Auto);
            SectionCollection sections = document.getSections();
            for (int i = 0; i < sections.getCount(); ++i) {
                Section section = sections.get(i);
                TextWatermark txtWatermark = new TextWatermark();
                txtWatermark.setText(watermarkText);
                txtWatermark.setFontSize(watermarkSize.longValue());
                txtWatermark.setColor(watermarkColor);
                txtWatermark.setLayout(watermarkLayout);
                section.getDocument().setWatermark(txtWatermark);
            }
            document.saveToFile(os, FileFormat.Auto);
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

    public static void excel(Path sourceFileLocalPath, Path targetFileLocalPath) {
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFileLocalPath.toFile()));
             InputStream nis = Files.newInputStream(sourceFileLocalPath, new OpenOption[0]);){
            Workbook workbook = new Workbook();
            workbook.loadFromStream(nis);
            Font watermarkFont = new Font("\u5b8b\u4f53", 0, watermarkSize.intValue());
            WorksheetsCollection wsc = workbook.getWorksheets();
            for (int i = 0; i < wsc.getCount(); ++i) {
                IWorksheet sheet = wsc.get(i);
                BufferedImage imgWatermark = SpireWatermarkUtils.drawText(watermarkText, watermarkFont, watermarkColor, Color.white, ((Worksheet)sheet).getPageSetup().getPageHeight(), ((Worksheet)sheet).getPageSetup().getPageWidth());
                ((Worksheet)sheet).getPageSetup().setLeftHeaderImage(imgWatermark);
                ((Worksheet)sheet).getPageSetup().setLeftHeader("&G");
            }
            workbook.saveToStream(os);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }

    public static void ppt(Path sourceFileLocalPath, Path targetFileLocalPath) {
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(targetFileLocalPath.toFile()));){
            Presentation presentation = new Presentation();
            presentation.loadFromFile(sourceFileLocalPath.toAbsolutePath().toString());
            int width = 400;
            int height = 300;
            Rectangle2D.Double rect = new Rectangle2D.Double((presentation.getSlideSize().getSize().getWidth() - (double)width) / 2.0, (presentation.getSlideSize().getSize().getHeight() - (double)height) / 2.0, width, height);
            SlideCollection sc = presentation.getSlides();
            for (int i = 0; i < sc.getCount(); ++i) {
                IAutoShape shape = sc.get(i).getShapes().appendShape(ShapeType.RECTANGLE, rect);
                shape.getFill().setFillType(FillFormatType.NONE);
                shape.getShapeStyle().getLineColor().setColor(Color.white);
                shape.setRotation(-45.0f);
                shape.getLocking().setSelectionProtection(true);
                shape.getLine().setFillType(FillFormatType.NONE);
                shape.getTextFrame().setText(watermarkText);
                PortionEx textRange = shape.getTextFrame().getTextRange();
                textRange.getFill().setFillType(FillFormatType.SOLID);
                textRange.getFill().getSolidColor().setColor(watermarkColor);
                textRange.setFontHeight(watermarkSize.longValue());
            }
            presentation.saveToFile(os, com.spire.presentation.FileFormat.AUTO);
        }
        catch (Exception e) {
            throw new FileException(e);
        }
    }

    public static void pdf(Path sourceFileLocalPath, Path targetFileLocalPath) {
        PdfDocument pdf = null;
        try {
            pdf = new PdfDocument();
            pdf.loadFromFile(sourceFileLocalPath.toAbsolutePath().toString());
            Font watermarkFont = new Font("\u5b8b\u4f53", 0, watermarkSize.intValue());
            PdfTrueTypeFont font = new PdfTrueTypeFont(watermarkFont);
            PdfSolidBrush pb = new PdfSolidBrush(new PdfRGBColor(watermarkColor));
            PdfPageCollection ppc = pdf.getPages();
            for (int i = 0; i < ppc.getCount(); ++i) {
                PdfPageBase page = ppc.get(i);
                SpireWatermarkUtils.pdfPageInsertWatermark(page, watermarkText, font, pb);
            }
            pdf.saveToFile(targetFileLocalPath.toAbsolutePath().toString());
        }
        catch (Exception e) {
            throw new FileException(e);
        }
        finally {
            if (null != pdf) {
                try {
                    pdf.close();
                }
                catch (Exception exception) {}
            }
        }
    }

    private static BufferedImage drawText(String text, Font font, Color textColor, Color backColor, double height, double width) {
        BufferedImage img = new BufferedImage((int)width, (int)height, 2);
        Graphics2D loGraphic = img.createGraphics();
        FontMetrics loFontMetrics = loGraphic.getFontMetrics(font);
        int liStrWidth = loFontMetrics.stringWidth(text);
        int liStrHeight = loFontMetrics.getHeight();
        loGraphic.setColor(backColor);
        loGraphic.fillRect(0, 0, (int)width, (int)height);
        loGraphic.translate(((int)width - liStrWidth) / 2, ((int)height - liStrHeight) / 2);
        loGraphic.rotate(Math.toRadians(-45.0));
        loGraphic.translate(-((int)width - liStrWidth) / 2, -((int)height - liStrHeight) / 2);
        loGraphic.setFont(font);
        loGraphic.setColor(textColor);
        loGraphic.drawString(text, ((int)width - liStrWidth) / 2, ((int)height - liStrHeight) / 2);
        loGraphic.dispose();
        return img;
    }

    private static void pdfPageInsertWatermark(PdfPageBase page, String watermark, PdfTrueTypeFont font, PdfBrush pb) {
        Dimension dimension2D = new Dimension();
        ((Dimension2D)dimension2D).setSize(page.getCanvas().getClientSize().getWidth(), page.getCanvas().getClientSize().getHeight());
        PdfTilingBrush ptb = new PdfTilingBrush(dimension2D);
        ptb.getGraphics().setTransparency(0.3f);
        ptb.getGraphics().save();
        ptb.getGraphics().translateTransform((float)ptb.getSize().getWidth() / 2.0f, (float)ptb.getSize().getHeight() / 2.0f);
        ptb.getGraphics().rotateTransform(-45.0f);
        ptb.getGraphics().drawString(watermark, (PdfFontBase)font, pb, 0.0f, 0.0f, new PdfStringFormat(PdfTextAlignment.Center));
        ptb.getGraphics().restore();
        ptb.getGraphics().setTransparency(1.0f);
        Rectangle2D.Float loRect = new Rectangle2D.Float();
        loRect.setFrame(new Point2D.Float(0.0f, 0.0f), page.getCanvas().getClientSize());
        page.getCanvas().drawRectangle(ptb, (Rectangle2D)loRect);
    }

    static {
        String watermarkColorStr = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.file.watermark.color", String.class, "128,128,128");
        String[] colorArray = StringUtils.split(watermarkColorStr, ",");
        watermarkColor = new Color(NumberUtils.parseInt(colorArray[0]), NumberUtils.parseInt(colorArray[1]), NumberUtils.parseInt(colorArray[2]));
        String watermarkLayoutStr = ApplicationContextHelper.getEnvironment().getProperty("sunway.config.file.watermark.layout", String.class, WATERMARK_LAYOUT_DIAGONAL);
        watermarkLayout = WATERMARK_LAYOUT_DIAGONAL.equalsIgnoreCase(watermarkLayoutStr) ? WatermarkLayout.Diagonal : WatermarkLayout.Horizontal;
    }
}
