/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sunwayworld.framework.exception.UnexpectedException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;

public abstract class QRCodeManager {
    private static final int DEFAULT_WIDTH = 30;
    private static final int DEFAULT_HEIGHT = 30;

    public static void write(HttpServletResponse response, String content, Integer width, Integer height) {
        HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            response.reset();
            response.setHeader("Cache-Control", "no-store");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0L);
            response.setContentType("image/png");
            MatrixToImageWriter.writeToStream(QRCodeManager.getBitMatrix(content, Optional.ofNullable(width).orElse(30), Optional.ofNullable(height).orElse(30)), "png", response.getOutputStream());
        }
        catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private static BitMatrix getBitMatrix(String content, int width, int height) {
        HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 0);
        try {
            return new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        }
        catch (WriterException ex) {
            throw new UnexpectedException(ex);
        }
    }
}
