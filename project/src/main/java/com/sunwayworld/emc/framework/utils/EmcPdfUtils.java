package com.sunwayworld.emc.framework.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfStamper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.utils.ObjectUtils;

import javax.imageio.ImageIO;

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
    public static void addImgInHeaderFooter(FilePathDTO sourceFr, FilePathDTO destFr, FilePathDTO imageFr, float x, float y) {
        String sourceFilePath = FilePathManager.getLocalPath(sourceFr).toString();
        String destFilePath = FilePathManager.getLocalPath(destFr).toString();

        if (!FilePathManager.getLocalPath(imageFr).toFile().exists()) {
            return;
        }

        String imagePath = FilePathManager.getLocalPath(imageFr).toString();

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
    public static void addImgInFooter(FilePathDTO sourceFr, FilePathDTO destFr, FilePathDTO imageFr, String textTitle) {
        String sourceFilePath = FilePathManager.getLocalPath(sourceFr).toString();
        String destFilePath = FilePathManager.getLocalPath(destFr).toString();
        if (!FilePathManager.getLocalPath(imageFr).toFile().exists()) {
            return;
        }

        String imagePath = FilePathManager.getLocalPath(imageFr).toString();

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

    private static final Pattern IMG_PATTERN = Pattern.compile(".*[.](jpg|png|bmp|gif)$");
    private static final Pattern PDF_PATTERN = Pattern.compile(".*[.]pdf$");

    private static final String ODD_NUMBER = "odd_number"; //奇数页
    @SuppressWarnings("unused")
    private static final String EVEN_NUMBER = "even_number"; //偶数页

    public EmcPdfUtils() {
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void mergePdfFile(List<FilePathDTO> sourceFrList, FilePathDTO destFr) {
        if (sourceFrList != null && sourceFrList.size() != 0) {
            List<String> sourceFilePathList = (List) sourceFrList.stream().map((s) -> {
                return FilePathManager.getLocalPath(s).toString();
            }).collect(Collectors.toList());
            String destFilePath = FilePathManager.getLocalPath(destFr).toString();
            mergePdfFile(sourceFilePathList, destFilePath);
        }
    }

    @SuppressWarnings("rawtypes")
    public static void mergePdfFile(List<String> sourceFilePathList, String destFilePath) {
        if (sourceFilePathList != null && sourceFilePathList.size() != 0) {
            com.lowagie.text.Document document = null;
            PdfCopy copy = null;
            BufferedOutputStream os = null;

            try {
                Path dirPath = Paths.get(destFilePath.substring(0, destFilePath.lastIndexOf(File.separator)));
                Files.createDirectories(dirPath);
                os = new BufferedOutputStream(new FileOutputStream(new File(destFilePath)));
                document = new com.lowagie.text.Document((new com.lowagie.text.pdf.PdfReader((String) sourceFilePathList.get(0))).getPageSize(1));
                copy = new PdfCopy(document, os);
                document.open();
                Iterator var6 = sourceFilePathList.iterator();

                while (var6.hasNext()) {
                    String sourceFilePath = (String) var6.next();
                    com.lowagie.text.pdf.PdfReader reader = new com.lowagie.text.pdf.PdfReader(sourceFilePath);
                    int n = reader.getNumberOfPages();

                    for (int j = 1; j <= n; ++j) {
                        document.newPage();
                        com.lowagie.text.pdf.PdfImportedPage page = copy.getImportedPage(reader, j);
                        copy.addPage(page);
                    }
                }
            } catch (Exception var25) {
                throw new FileException(var25);
            } finally {
                if (copy != null) {
                    try {
                        copy.close();
                    } catch (Exception var24) {
                    }
                }

                if (document != null) {
                    try {
                        document.close();
                    } catch (Exception var23) {
                    }
                }

                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception var22) {
                    }
                }

            }

        }
    }

    public static  File addStamp(File inPdf, File pic,String stampPageType,float[] resu) {
        if (IMG_PATTERN.matcher(pic.getName().toLowerCase()).matches() && PDF_PATTERN.matcher(inPdf.getName().toLowerCase()).matches()) {
            com.lowagie.text.pdf.PdfReader reader = null;
            String fileName = inPdf.getAbsolutePath();
            String newName = fileName.substring(0, fileName.lastIndexOf("."));
            newName = newName + "tem.pdf";

            try {
                reader = new com.lowagie.text.pdf.PdfReader(inPdf.getAbsolutePath());
            } catch (IOException var13) {
                throw new FileException(var13);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception var12) {
                    }
                }

            }

            File temFile = addStamp(reader, pic.getAbsolutePath(), new File(newName),stampPageType,resu);
            inPdf.delete();
            temFile.renameTo(new File(fileName));
            return temFile;
        } else {
            throw new UnexpectedException(I18nHelper.getMessage("GIKAM.EXCEPTION.FILE_FORMAT_WRONG", new String[0]));
        }
    }

    private static File addStamp(com.lowagie.text.pdf.PdfReader reader, String picPath, File pdfFile, String stampPageType, float[] resu) {
        FileOutputStream os = null;
        PdfStamper stamp = null;
        com.lowagie.text.Image[] nImage = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            os = new FileOutputStream(pdfFile);
            stamp = new PdfStamper(reader, os);
            com.lowagie.text.Rectangle pageSize = reader.getPageSize(1);
            float height = pageSize.getHeight();
            float width = pageSize.getWidth();
            int pages = reader.getNumberOfPages();

            if(!ObjectUtils.isEmpty(stampPageType)){
                //选择盖章奇偶数页
                int nums = pages / 2;
                int pageNum;
                if (ODD_NUMBER.equals(stampPageType)) {
                    //盖章奇数页,且页数为奇数的。图片分割的数量需要+1
                    if (pages % 2 == 1) {
                        //表示页数为奇数，需要盖的页数+1
                        nums = nums +1;
                    }
                    pageNum = 1;
                } else{
                    //盖章偶数页
                    pageNum = 2;
                }
                nImage = subImages(picPath, nums);

                for (int n = 1; n <= nums; ++n) {
                    com.lowagie.text.pdf.PdfContentByte over = stamp.getOverContent(pageNum);
                    com.lowagie.text.Image img = nImage[n - 1];
                    img.scaleToFit(120/nums,120);
                    img.setAbsolutePosition(width - 120/nums, height / 2.0F - 120 / 2.0F);
                    over.addImage(img);
                    pageNum = pageNum + 2;
                }
            }else{
                nImage = subImages(picPath, pages);
                for (int n = 1; n <= pages; ++n) {
                    com.lowagie.text.pdf.PdfContentByte over = stamp.getOverContent(n);
                    com.lowagie.text.Image img = nImage[n - 1];
                    img.scaleToFit(120/pages,120);
                    img.setAbsolutePosition(width - 120/pages, height / 2.0F - 120 / 2.0F);
                    over.addImage(img);
                }
            }

            if(resu != null && resu.length==3) {
                float x = resu[0];
                float y = resu[1];
                float num = resu[2];
                com.lowagie.text.pdf.PdfContentByte over = stamp.getOverContent((int)num);
                BufferedImage imgSin = ImageIO.read(new File(picPath));

                ImageIO.write(imgSin, picPath.substring(picPath.lastIndexOf(46) + 1), out);
                com.lowagie.text.Image sin = com.lowagie.text.Image.getInstance(out.toByteArray());
                sin.scaleToFit(120,120);
                sin.setAbsolutePosition(x, y);
                over.addImage(sin);
            }
            stamp.close();
            return pdfFile;
        } catch (IOException | com.lowagie.text.DocumentException var23) {
            throw new FileException(var23);
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (Exception var22) {
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception var21) {
                }
            }

        }
    }

    private static com.lowagie.text.Image[] subImages(String imgPath, int n) {
        com.lowagie.text.Image[] nImage = new com.lowagie.text.Image[n];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(imgPath));
            //修改高度和宽度
            //img = updateImage(120,120,ImageIO.read(new File(imgPath)));

            int h = img.getHeight();
            int w = img.getWidth();
            int sw = w / n;
            for (int i = 0; i < n; ++i) {
                BufferedImage subImg = null;
                if (i == n - 1) {
                    subImg = img.getSubimage(i * sw, 0, w - i * sw, h);
                } else {
                    subImg = img.getSubimage(i * sw, 0, sw, h);
                }

                ImageIO.write(subImg, imgPath.substring(imgPath.lastIndexOf(46) + 1), out);
                nImage[i] = com.lowagie.text.Image.getInstance(out.toByteArray());
                out.flush();
                out.reset();
            }
        } catch (Exception var17) {
            throw new FileException(var17);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Exception var16) {
                }
            }

        }

        return nImage;
    }


    public static BufferedImage updateImage(int wid, int hig, BufferedImage oldImg){
        BufferedImage newImage = new BufferedImage(wid,hig, oldImg.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(oldImg, 0,0,wid,hig,null);
        g.dispose();
        return newImage;
    }
}
