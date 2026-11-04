/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.surport;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.FileUtils;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.binary.Base64;

public class ImageUtils {
    public static BufferedImage Rotate(BufferedImage srcBufferedImage, int angel) {
        int srcWidth = srcBufferedImage.getWidth();
        int srcHeight = srcBufferedImage.getHeight();
        Rectangle rotatedRectangle = ImageUtils.CalcRotatedSize(new Rectangle(new Dimension(srcWidth, srcHeight)), angel);
        int rotatedWidth = rotatedRectangle.width;
        int rotatedHeight = rotatedRectangle.height;
        BufferedImage rotatedImage = new BufferedImage(rotatedWidth, rotatedHeight, 1);
        Graphics2D graphics2D = rotatedImage.createGraphics();
        rotatedImage = graphics2D.getDeviceConfiguration().createCompatibleImage(rotatedWidth, rotatedHeight, 3);
        graphics2D.dispose();
        graphics2D = rotatedImage.createGraphics();
        graphics2D.translate((rotatedWidth - srcWidth) / 2, (rotatedHeight - srcHeight) / 2);
        graphics2D.rotate(Math.toRadians(angel), srcWidth / 2, srcHeight / 2);
        graphics2D.drawImage(srcBufferedImage, null, null);
        graphics2D.dispose();
        return rotatedImage;
    }

    public static BufferedImage imageMisro(BufferedImage bufferedimage, int reverse) {
        int j;
        int i;
        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();
        int[][] datas = new int[width][height];
        for (int i2 = 0; i2 < height; ++i2) {
            for (int j2 = 0; j2 < width; ++j2) {
                datas[j2][i2] = bufferedimage.getRGB(j2, i2);
            }
        }
        int[][] tmps = new int[width][height];
        if (reverse == 0) {
            i = 0;
            int a = height - 1;
            while (i < height) {
                for (int j3 = 0; j3 < width; ++j3) {
                    tmps[j3][a] = datas[j3][i];
                }
                ++i;
                --a;
            }
        } else if (reverse == 1) {
            for (i = 0; i < height; ++i) {
                j = 0;
                int b = width - 1;
                while (j < width) {
                    tmps[b][i] = datas[j][i];
                    ++j;
                    --b;
                }
            }
        }
        for (i = 0; i < height; ++i) {
            for (j = 0; j < width; ++j) {
                bufferedimage.setRGB(j, i, tmps[j][i]);
            }
        }
        return bufferedimage;
    }

    public static String cutPic(File picFile, int xPos, int yPos, int width, int height) {
        FileInputStream inputStream = null;
        ImageInputStream imageInputStream = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            inputStream = new FileInputStream(picFile);
            imageInputStream = ImageIO.createImageInputStream(inputStream);
            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageInputStream);
            ImageReader imageReader = iterator.next();
            imageReader.setInput(imageInputStream, true);
            ImageReadParam imageReadParam = imageReader.getDefaultReadParam();
            Rectangle rectangle = new Rectangle(xPos, yPos, width, height);
            imageReadParam.setSourceRegion(rectangle);
            BufferedImage bufferedImage = imageReader.read(0, imageReadParam);
            byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)bufferedImage, "png", byteArrayOutputStream);
            String string = ImageUtils.byteToBase64(byteArrayOutputStream.toByteArray()).trim().replaceAll("\n", "").replaceAll("\r", "");
            return string;
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        finally {
            if (inputStream != null) {
                try {
                    ((InputStream)inputStream).close();
                }
                catch (IOException e) {
                    throw new FileException(e);
                }
            }
            if (imageInputStream != null) {
                try {
                    imageInputStream.close();
                }
                catch (IOException e) {
                    throw new FileException(e);
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                }
                catch (IOException e) {
                    throw new FileException(e);
                }
            }
        }
    }

    public static String fileToBase64(File file) {
        byte[] data = null;
        try {
            data = Files.readAllBytes(file.toPath());
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        return ImageUtils.byteToBase64(data);
    }

    public static File base64ToFile(String base64, String picName) {
        File picFile = new File(FileUtils.getTempDir() + File.separator + picName);
        byte[] bytes = ImageUtils.base64Tobyte(base64);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(picFile));){
            bos.write(bytes);
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        return picFile;
    }

    public static String byteToBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] base64Tobyte(String base64) {
        return Base64.decodeBase64(base64);
    }

    public static void checkFile(File file) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(file);
            StringBuilder description = new StringBuilder();
            metadata.getDirectories().forEach(directory -> directory.getTags().forEach(tag -> {
                if (tag.getTagType() == 274) {
                    description.append(tag.getDescription().replaceAll(" ", ""));
                }
            }));
            if (description.length() > 0) {
                int rotateIndex = description.indexOf("Rotate");
                int cwIndex = description.indexOf("CW");
                if (rotateIndex >= 0 && cwIndex > 0 && rotateIndex < cwIndex) {
                    int angel = Integer.valueOf(description.substring(rotateIndex + 6, cwIndex));
                    BufferedImage oldImage = ImageIO.read(file);
                    BufferedImage newImage = ImageUtils.Rotate(oldImage, angel);
                    newImage = ImageUtils.contraction(newImage);
                    ImageIO.write((RenderedImage)newImage, "png", file);
                    newImage.getGraphics().dispose();
                    oldImage.getGraphics().dispose();
                }
            }
        }
        catch (ImageProcessingException | IOException exception) {
            // empty catch block
        }
    }

    public static BufferedImage contraction(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            if (imageBytes.length <= 0x200000) {
                return image;
            }
            double accuracy = ImageUtils.getAccuracy(imageBytes.length / 1024);
            while (imageBytes.length > 0x200000) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
                Thumbnails.of(inputStream).scale(accuracy).outputQuality(accuracy).toOutputStream(outputStream);
                imageBytes = outputStream.toByteArray();
            }
            BufferedImage targetImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            return targetImage;
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }

    private static Rectangle CalcRotatedSize(Rectangle srcRectangle, int angel) {
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = srcRectangle.height;
                srcRectangle.height = srcRectangle.width;
                srcRectangle.width = temp;
            }
            angel %= 90;
        }
        double catercorner = Math.sqrt(srcRectangle.height * srcRectangle.height + srcRectangle.width * srcRectangle.width) / 2.0;
        double levelLen = 2.0 * Math.sin(Math.toRadians(angel) / 2.0) * catercorner;
        double alpha = (Math.PI - Math.toRadians(angel)) / 2.0;
        double angelDaltaWidth = Math.atan((double)srcRectangle.height / (double)srcRectangle.width);
        double angelDaltaHeight = Math.atan((double)srcRectangle.width / (double)srcRectangle.height);
        double lenDaltaWidth = levelLen * Math.cos(Math.PI - alpha - angelDaltaWidth);
        double lenDaltaHeight = levelLen * Math.cos(Math.PI - alpha - angelDaltaHeight);
        int desWidth = (int)((double)srcRectangle.width + lenDaltaWidth * 2.0);
        int desHeight = (int)((double)srcRectangle.height + lenDaltaHeight * 2.0);
        return new Rectangle(new Dimension(desWidth, desHeight));
    }

    private static double getAccuracy(long size) {
        double accuracy = size < 5120L ? 0.85 : (size < 10240L ? 0.6 : (size < 15360L ? 0.44 : 0.4));
        return accuracy;
    }
}
