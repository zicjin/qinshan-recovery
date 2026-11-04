/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pdf.core;

import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentBean;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfImageDTO;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;

public class CorePdfManager {
    private static final int SIZE = 1500;

    public static PDDocument getPDDocument(File pdfFile) {
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
            if (document.isEncrypted()) {
                document.close();
                document = PDDocument.load(pdfFile, "");
                document.setAllSecurityToBeRemoved(true);
            }
        }
        catch (IOException e) {
            throw new FileException(e);
        }
        return document;
    }

    public static List<CorePdfDocumentBean> getPdfDocumentList(CoreFileBean coreFile) {
        ArrayList<CorePdfDocumentBean> corePdfDocumentList = new ArrayList<CorePdfDocumentBean>();
        File temp = null;
        ObjectExtractor extractor = null;
        try {
            CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
            temp = FileUtils.createTempFile();
            fileManager.download(coreFile, temp.toPath());
            PDDocument document = CorePdfManager.getPDDocument(temp);
            extractor = new ObjectExtractor(document);
            PageIterator it = extractor.extract();
            while (it.hasNext()) {
                Page p = it.next();
                CorePdfDocumentBean corePdfDocumentBean = new CorePdfDocumentBean();
                corePdfDocumentBean.setWidth(p.getWidth());
                corePdfDocumentBean.setHeight(p.getHeight());
                corePdfDocumentBean.setPageNumber(p.getPageNumber());
                corePdfDocumentBean.setRotation(p.getRotation());
                corePdfDocumentList.add(corePdfDocumentBean);
            }
            Collections.reverse(corePdfDocumentList);
            ArrayList<CorePdfDocumentBean> arrayList = corePdfDocumentList;
            return arrayList;
        }
        catch (Exception e) {
            throw new FileException(e);
        }
        finally {
            if (extractor != null) {
                try {
                    extractor.close();
                }
                catch (IOException e) {
                    throw new FileException(e);
                }
            }
            FileUtils.deleteQuietly(temp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static List<CorePdfImageDTO> getPdfImageList(CoreFileBean coreFile) {
        ArrayList<CorePdfImageDTO> pdfImageList = new ArrayList<CorePdfImageDTO>();
        File temp = null;
        PDDocument pdfDocument = null;
        try {
            CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
            temp = FileUtils.createTempFile();
            fileManager.download(coreFile, temp.toPath());
            pdfDocument = CorePdfManager.getPDDocument(temp);
            int totalPages = pdfDocument.getNumberOfPages();
            PDFRenderer renderer = new PDFRenderer(pdfDocument);
            for (int pi = 0; pi < totalPages; ++pi) {
                String imageFileName = String.format("%s_%d.png", StringUtils.randomUUID(16), pi + 1);
                File imageFile = new File(Paths.get(temp.getParent(), imageFileName).toString());
                if (imageFile.exists()) continue;
                try {
                    BufferedImage image = renderer.renderImageWithDPI(pi, 150.0f);
                    int imageWidth = image.getWidth();
                    int imageHeight = image.getHeight();
                    float scale = 1500.0f / (float)imageWidth;
                    BufferedImage bi = new BufferedImage(1500, Math.round((float)imageHeight * scale), image.getType());
                    bi.getGraphics().drawImage(image.getScaledInstance(1500, Math.round((float)imageHeight * scale), 4), 0, 0, null);
                    FilePathDTO filePath = CoreFileUtils.toFilePath(coreFile);
                    filePath.setFileName(imageFileName);
                    try (ByteArrayOutputStream out = new ByteArrayOutputStream();){
                        ImageIO.write((RenderedImage)bi, "png", out);
                        try (ByteArrayInputStream is = new ByteArrayInputStream(out.toByteArray());){
                            fileManager.upload(filePath, (InputStream)is);
                        }
                    }
                    CorePdfImageDTO corePdImageDTO = new CorePdfImageDTO();
                    corePdImageDTO.setImageWidth(1500.0);
                    corePdImageDTO.setImageHeight(Double.valueOf(Math.round((float)imageHeight * scale)));
                    corePdImageDTO.setImageName(fileManager.getDownloadUrl(filePath));
                    pdfImageList.add(corePdImageDTO);
                    continue;
                }
                finally {
                    FileUtils.deleteQuietly(imageFile);
                }
            }
            Collections.reverse(pdfImageList);
            ArrayList<CorePdfImageDTO> arrayList = pdfImageList;
            return arrayList;
        }
        catch (Exception e) {
            throw new FileException(e);
        }
        finally {
            if (pdfDocument != null) {
                try {
                    pdfDocument.close();
                }
                catch (IOException e) {
                    throw new FileException(e);
                }
            }
            FileUtils.deleteQuietly(temp);
        }
    }
}
