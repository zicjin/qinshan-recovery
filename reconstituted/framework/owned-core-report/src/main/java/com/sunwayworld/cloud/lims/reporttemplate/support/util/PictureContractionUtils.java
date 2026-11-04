/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.sunwayworld.framework.exception.FileException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;

public class PictureContractionUtils {
    public static BufferedImage contraction(File picFile) {
        try {
            BufferedImage image = ImageIO.read(picFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            BufferedImage back = null;
            if (imageBytes.length <= 0x200000) {
                back = image;
            } else {
                BufferedImage targetImage;
                int i = 0;
                double accuracy = PictureContractionUtils.getAccuracy(imageBytes.length / 1024);
                while (imageBytes.length > 0x200000) {
                    System.out.println(++i);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                    Thumbnails.of(inputStream).scale(accuracy).outputQuality(accuracy).toOutputStream(outputStream);
                    imageBytes = outputStream.toByteArray();
                }
                back = targetImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            }
            int angle = PictureContractionUtils.getAngle(picFile);
            if (angle == 90 || angle == 270) {
                BufferedImage srcImg = back;
                int imgWidth = srcImg.getHeight();
                int imgHeight = srcImg.getWidth();
                double centerWidth = (double)imgWidth / 2.0;
                double centerHeight = (double)imgHeight / 2.0;
                BufferedImage targetImg = new BufferedImage(imgWidth, imgHeight, 1);
                Graphics2D g = targetImg.createGraphics();
                g.rotate(Math.toRadians(angle), centerWidth, centerHeight);
                g.drawImage((Image)srcImg, (imgWidth - srcImg.getWidth()) / 2, (imgHeight - srcImg.getHeight()) / 2, null);
                g.rotate(Math.toRadians(-angle), centerWidth, centerHeight);
                g.dispose();
                back = targetImg;
            }
            return back;
        }
        catch (IOException var7) {
            throw new FileException(var7);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static double getAccuracy(long size) {
        double accuracy = size < 5120L ? 0.85 : (size < 10240L ? 0.6 : (size < 15360L ? 0.44 : 0.4));
        return accuracy;
    }

    private static int getAngle(File file) throws Exception {
        Metadata metadata = ImageMetadataReader.readMetadata(file);
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                if (!"Orientation".equals(tag.getTagName())) continue;
                String orientation = tag.getDescription();
                if (orientation.contains("90")) {
                    return 90;
                }
                if (orientation.contains("180")) {
                    return 180;
                }
                if (!orientation.contains("270")) continue;
                return 270;
            }
        }
        return 0;
    }
}
