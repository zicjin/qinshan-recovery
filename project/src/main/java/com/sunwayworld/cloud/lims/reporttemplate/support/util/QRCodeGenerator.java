// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class QRCodeGenerator {
    public static void generateQRCodeImage(String text, String note, int width, int height, int margin, String filePath) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, margin);
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        if (!ObjectUtils.isEmpty(note)) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedImage image = ImageIO.read(inputStream);
            image = QRCodeGenerator.addNote(image, new String(note.getBytes("UTF-8"), "UTF-8"));
            File file = new File(filePath);
            ImageIO.write((RenderedImage)image, "PNG", file);
        } else {
            Path path = FileSystems.getDefault().getPath(filePath, new String[0]);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
        }
    }

    private static File createTempFile(String fileName) {
        FilePathDTO dest = FilePathDTO.of(FileScope.temp.name(), DateTimeUtils.getDateSerialNumber(LocalDateTime.now()), StringUtils.randomUUID(32), fileName);
        Path path = FilePathManager.getLocalPath(dest);
        FileUtils.makeDirs(path);
        return path.toFile();
    }

    public static File getQrPic(String fileName, String text, String note, int width, int height, int margin) {
        File file = QRCodeGenerator.createTempFile(fileName);
        try {
            QRCodeGenerator.generateQRCodeImage(text, note, width, height, margin, file.getAbsolutePath());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static BufferedImage addNote(BufferedImage image, String note) {
        int width = image.getWidth();
        int height = image.getHeight();
        Image src = image.getScaledInstance(width, height, 1);
        BufferedImage tag = new BufferedImage(width, height + 50, 1);
        Graphics2D g2 = tag.createGraphics();
        g2.setColor(Color.white);
        g2.fillRect(0, 0, height, height + 50);
        g2.drawImage(src, 0, 0, width, height, null);
        Font font = new Font("\u5b8b\u4f53", 1, 50);
        g2.setFont(font);
        g2.setPaint(Color.black);
        FontRenderContext context = g2.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(note, context);
        double x = ((double)width - bounds.getWidth()) / 2.0;
        double y = (double)height - bounds.getY() - 5.0;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawString(note, (int)x, (int)y);
        g2.dispose();
        tag.flush();
        return tag;
    }
}
