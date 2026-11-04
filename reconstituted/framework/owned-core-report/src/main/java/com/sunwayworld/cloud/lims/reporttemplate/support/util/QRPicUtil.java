/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.StringUtils;

public class QRPicUtil {
    private static final String CHARSET = "utf-8";
    private static final int FONT_SIZE_RATE = 10;
    private static final int WORD_AND_CODE_SPACING = 3;

    public static void generateQRCodeImage(BarcodeFormat barcodeFormat, String text, int width, int height, Path filePath) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, (Object)ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, barcodeFormat, width, height, hints);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", filePath);
    }

    public static void generateQRCodeImage(BarcodeFormat barcodeFormat, String text, int width, int height, int margin, OutputStream outputStream) throws WriterException, IOException {
        QRPicUtil.generateQRCodeImage(barcodeFormat, text, width, height, margin, outputStream, false);
    }

    public static void generateQRCodeImage(BarcodeFormat barcodeFormat, String text, int width, int height, int margin, OutputStream outputStream, boolean withText) throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, (Object)ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, margin);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, barcodeFormat, width, height, hints);
        bitMatrix = QRPicUtil.deleteWhite(bitMatrix);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        if (withText) {
            bufferedImage = QRPicUtil.getCodeWithWords(bufferedImage, text, bitMatrix.getWidth(), bitMatrix.getHeight());
        }
        if (!ImageIO.write((RenderedImage)bufferedImage, "PNG", outputStream)) {
            throw new IOException("Could not write an image of format PNG");
        }
    }

    public static BufferedImage getCodeWithWords(BufferedImage codeImage, String bottomStr, int pictureWidth, int pictureHeight) {
        int fontSize = codeImage.getHeight() / 10;
        BufferedImage picImage = new BufferedImage(pictureWidth, pictureHeight + (fontSize + 6), 1);
        Graphics2D g2d = picImage.createGraphics();
        QRPicUtil.setGraphics2D(g2d);
        QRPicUtil.setColorWhite(g2d, picImage.getWidth(), picImage.getHeight());
        int codeStartX = (pictureWidth - codeImage.getWidth()) / 2;
        int codeStartY = (pictureHeight - codeImage.getHeight()) / 2;
        g2d.drawImage(codeImage, codeStartX, codeStartY, codeImage.getWidth(), codeImage.getHeight(), null);
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("\u5fae\u8f6f\u96c5\u9ed1", 0, fontSize));
        if (StringUtils.isNotEmpty(bottomStr)) {
            int strWidth = g2d.getFontMetrics().stringWidth(bottomStr);
            int strStartX = codeStartX + (codeImage.getWidth() - strWidth) / 2;
            int strStartY = codeStartY + codeImage.getHeight() + fontSize + 3;
            g2d.drawString(bottomStr, strStartX, strStartY);
        }
        g2d.dispose();
        picImage.flush();
        return picImage;
    }

    private static void setGraphics2D(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        BasicStroke s = new BasicStroke(1.0f, 1, 0);
        g2d.setStroke(s);
    }

    private static void setColorWhite(Graphics2D g2d, int width, int height) {
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3];
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; ++i) {
            for (int j = 0; j < resHeight; ++j) {
                if (!matrix.get(i + rec[0], j + rec[1])) continue;
                resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }
}
