package com.sunwayworld.emc.framework.utils;

import java.io.FileOutputStream;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;

/**
 * @author zhengxy@sunwayworld.com
 */
public class EmcPdfUtils {

    /** 竖版表格默认宽度 */
    private static final float DEFAULT_VERTICAL_TABLE_WIDTH = 530;
    /** 横板表格默认宽度 */
    private static final float DEFAULT_HORIZONTAL_TABLE_WIDTH = 800;
    /** 竖版签名图片默认X坐标 */
    private static final float DEFAULT_VERTICAL_SIGNED_X = 500;
    /** 横版签名图片默认Y坐标 */
    private static final float DEFAULT_HORIZONTAL_SIGNED_X = 720;

    /**
     * 向PDF文件中添加图片
     * @param sourceFr 源文件
     * @param destFr 新文件
     * @param imageFr 图片文件
     * @param x x坐标
     * @param y y坐标
     */
    public static void addImgInHeaderFooter(FileRepresentation sourceFr, FileRepresentation destFr, FileRepresentation imageFr, float x, float y) {
        String sourceFilePath = FileHelper.getAbsolutePath(sourceFr).toString();
        String destFilePath = FileHelper.getAbsolutePath(destFr).toString();
        if (!FileHelper.getAbsolutePath(imageFr).toFile().exists()) {
            return;
        }

        String imagePath = FileHelper.getAbsolutePath(imageFr).toString();

        addImgInHeaderFooter(sourceFilePath, destFilePath, imagePath, x, y);

    }

    /**
     * 向PDF文件中添加图片
     * @param filePath 源文件地址
     * @param destFilePath 新文件地址
     * @param imagePath 图片文件地址
     * @param x x坐标
     * @param y y坐标
     */
    public static void addImgInHeaderFooter(String filePath, String destFilePath, String imagePath, float x, float y) {
        Document document = null;
        PdfReader reader = null;
        PdfWriter writer = null;
        try {
            document = new Document();
            writer = PdfWriter.getInstance(document, new FileOutputStream(destFilePath));

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document doc) {
                    PdfPTable table = new PdfPTable(2);
                    table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.setTotalWidth(530);
                    table.getDefaultCell().setBorder(0);
                    table.setWidthPercentage(100);
                    try {
                        table.setTotalWidth(new float[] { 36, 100});
                    } catch (DocumentException e) {
                        /* ignore */
                    }

                    String fontName = "simsun.ttc,0";
                    Font font = FontFactory.getFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8, Font.NORMAL, BaseColor.BLACK);

                    Phrase textPhrase = new Phrase("分析员：", font);

                    PdfPCell textCell = new PdfPCell(textPhrase);
                    textCell.setPaddingTop(-16f);
                    textCell.setBorder(0);
                    table.addCell(textCell);

                    PdfPCell cell = new PdfPCell();
                    try {
                        Image image = Image.getInstance(imagePath);
                        image.scaleAbsoluteWidth(35);
                        cell.setPaddingTop(-20f);
                        cell.setBorder(0);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.addElement(image);
                    } catch (Exception e) {
                        throw new FileException(e);
                    }

                    table.addCell(cell);
                    table.writeSelectedRows(0, -1, x, y, writer.getDirectContent());
                }
            });

            document.open();

            PdfContentByte pdfContent = writer.getDirectContent();

            reader = new PdfReader(filePath);
            int n = reader.getNumberOfPages();
            PdfImportedPage page;
            for (int i = 1; i <= n; i++) {
                document.newPage();
                page = writer.getImportedPage(reader, i);

                pdfContent.addTemplate(page, 0, 0);
            }

        } catch (Exception e) {
            throw new FileException(e);
        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }

            if (document != null) {
                try {
                    document.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }
        }
    }

    /**
     * 向PDF文件底部添加图片
     * @param sourceFr 源文件
     * @param destFr 新文件
     * @param imageFr 图片文件
     * @param textTitle 文本标题
     */
    public static void addImgInFooter(FileRepresentation sourceFr, FileRepresentation destFr, FileRepresentation imageFr, String textTitle) {
        String sourceFilePath = FileHelper.getAbsolutePath(sourceFr).toString();
        String destFilePath = FileHelper.getAbsolutePath(destFr).toString();
        if (!FileHelper.getAbsolutePath(imageFr).toFile().exists()) {
            return;
        }

        String imagePath = FileHelper.getAbsolutePath(imageFr).toString();

        addImgInFooter(sourceFilePath, destFilePath, imagePath, textTitle);

    }

    /**
     * 向PDF文件底部添加图片
     * @param filePath 源文件
     * @param destFilePath 新文件
     * @param imagePath 图片文件
     * @param textTitle 文本标题
     */
    public static void addImgInFooter(String filePath, String destFilePath, String imagePath, String textTitle) {
        Document document = null;
        PdfReader reader = null;
        PdfWriter writer = null;
        try {
            reader = new PdfReader(filePath);
            Rectangle rectangle = reader.getPageSize(1);
            boolean vertical = (rectangle.getWidth() < rectangle.getHeight());

            document = new Document();
            if (!vertical) {
                document.setPageSize(PageSize.A4.rotate());
            }
            writer = PdfWriter.getInstance(document, new FileOutputStream(destFilePath));

            writer.setPageEvent(new PdfPageEventHelper() {
                @Override
                public void onEndPage(PdfWriter writer, Document doc) {
                    PdfPTable table = new PdfPTable(2);
                    table.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.setTotalWidth(vertical ? DEFAULT_VERTICAL_TABLE_WIDTH : DEFAULT_HORIZONTAL_TABLE_WIDTH);
                    table.getDefaultCell().setBorder(0);
                    table.setWidthPercentage(100);
                    try {
                        table.setTotalWidth(new float[] { 36, 100});
                    } catch (DocumentException e) {
                        /* ignore */
                    }

                    String fontName = "simsun.ttc,0";
                    Font font = FontFactory.getFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 8, Font.NORMAL, BaseColor.BLACK);

                    Phrase textPhrase = new Phrase(textTitle, font);

                    PdfPCell textCell = new PdfPCell(textPhrase);
                    textCell.setPaddingTop(-16f);
                    textCell.setBorder(0);
                    table.addCell(textCell);

                    PdfPCell cell = new PdfPCell();
                    try {
                        Image image = Image.getInstance(imagePath);
                        image.scaleAbsoluteWidth(35);
                        cell.setPaddingTop(-20f);
                        cell.setBorder(0);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                        cell.addElement(image);
                    } catch (Exception e) {
                        throw new FileException(e);
                    }

                    table.addCell(cell);
                    table.writeSelectedRows(0, -1, vertical ? DEFAULT_VERTICAL_SIGNED_X : DEFAULT_HORIZONTAL_SIGNED_X, 0, writer.getDirectContent());
                }
            });

            document.open();

            PdfContentByte pdfContent = writer.getDirectContent();

            int n = reader.getNumberOfPages();
            PdfImportedPage page;
            for (int i = 1; i <= n; i++) {
                document.newPage();
                page = writer.getImportedPage(reader, i);

                pdfContent.addTemplate(page, 0, 0);
            }

        } catch (Exception e) {
            throw new FileException(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }

            if (document != null) {
                try {
                    document.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }

        }

    }
}
