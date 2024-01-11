package com.sunwayworld.framework.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
/**
 * 
 * @author magt 2021-03-24
 *
 * pdf添加图片水印的工具类
 *
 */
public class PicWatermarkUtils {
    
    private static final Pattern IMG_PATTERN = Pattern.compile(".*[.](jpg|png|bmp|gif)$");
    private static final Pattern PDF_PATTERN = Pattern.compile(".*[.]pdf$");
    /**
     * 
     * @param pdf pdfFile
     * @param pic 水印图片File
     * @param transparency 透明度,最大1F (example : 0.5F)
     * @param scale 缩放百分比 (example : 30F)
     * @param x x轴绝对位置 (example : 200F)
     * @param y y轴绝对位置 (example : 300F)
     */
    public static void addWatermark(File inPdf, File pic, Float transparency, Float scale, Float x, Float y) {
        if (IMG_PATTERN.matcher(pic.getName().toLowerCase()).matches() && PDF_PATTERN.matcher(inPdf.getName().toLowerCase()).matches()) {
            PdfReader reader = null;
            FileOutputStream os = null;
            PdfStamper stamp = null;
            
            String fileName = inPdf.getAbsolutePath();// 源文件的路径
            String newName = fileName.substring(0, fileName.lastIndexOf("."));
            newName = newName + "tem.pdf";// 源文件tem.pdf
            File temp = new File(newName);
            try {
                reader = new PdfReader(inPdf.getAbsolutePath());
                os = new FileOutputStream(temp);
                stamp = new PdfStamper(reader, os);
                Image image = Image.getInstance(pic.getAbsolutePath());
                // 页数
                int nums = reader.getNumberOfPages();
                for (int n = 1; n <= nums; n++) {
                    PdfContentByte over = stamp.getOverContent(n);// 设置在第几页打印印章
                    // 控制图片位置
                    image.setAbsolutePosition(x, y);
                    // 设置等比缩放
                    image.scalePercent(scale);// 依照比例缩放
                    // 设置水印透明度
                    PdfGState gs = new PdfGState();
                    // 设置笔触字体不透明度
                    gs.setFillOpacity(transparency);
                    over.setGState(gs);
                    over.addImage(image);
                }
                stamp.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.flush();
                        os.close();
                    } catch (Exception ex) {
                        /* ignore */
                    }
                }
            }
            inPdf.delete();// 删除源文件
            temp.renameTo(new File(fileName));// 生成新的同名文件
        } else {
            throw new UnexpectedException(I18nHelper.getMessage("GIKAM.EXCEPTION.FILE_FORMAT_WRONG"));
        }
    }
}
