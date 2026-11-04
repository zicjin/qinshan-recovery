package com.sunwayworld.cloud.module.equipt.support;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqAreaDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigLineBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageImageDTO;
import com.sunwayworld.cloud.module.equipt.support.constant.PdfAcqParseType;
import com.sunwayworld.cloud.module.equipt.support.constant.PdfAcqTableDisplayWay;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.awt.image.BufferedImage;
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

public class CorePdfHepler {
   private static final int SIZE = 1500;

   public static List<CorePdfAcqAreaDTO> extractTableList(PDDocument document, List<CorePdfAcqAreaDTO> pdfAreaDTOList) {
      int i = 0;

      for (int j = pdfAreaDTOList.size(); i < j; i++) {
         pdfAreaDTOList.get(i).setExtractMethod("original");
         pdfAreaDTOList.get(i).setSpecIndex(i);
      }

      ObjectExtractor extractor = new ObjectExtractor(document);

      for (CorePdfAcqAreaDTO pdfAreaDTO : pdfAreaDTOList) {
         if (!PdfAcqParseType.RESULT_COLUMN.name().equals(pdfAreaDTO.getParseType())
            && !PdfAcqParseType.PAGE_HEADER.name().equals(pdfAreaDTO.getParseType())
            && !PdfAcqParseType.PAGE_FOOTER.name().equals(pdfAreaDTO.getParseType())) {
            CorePdfTableResolve corePdfTableResolve = new CorePdfTableResolve(document, pdfAreaDTO, pdfAreaDTOList, extractor);
            pdfAreaDTO.setData(corePdfTableResolve.getParseResult());
         }
      }

      if (extractor != null) {
         try {
            extractor.close();
         } catch (IOException var6) {
         }
      }

      return pdfAreaDTOList;
   }

   public static PDDocument getPDDocument(File pdfFile) {
      PDDocument document = null;

      try {
         document = PDDocument.load(pdfFile);
         if (document.isEncrypted()) {
            document.close();
            document = PDDocument.load(pdfFile, "");
            document.setAllSecurityToBeRemoved(true);
         }

         return document;
      } catch (IOException var3) {
         throw new FileException(var3);
      }
   }

   public static List<CorePdfPageBean> getPdfPageList(CoreFileBean file) {
      List<CorePdfPageBean> corePdfPageList = new ArrayList<>();
      File temp = null;
      ObjectExtractor extractor = null;

      Object var18;
      try {
         CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
         temp = FileUtils.createTempFile();
         fileManager.download(file, temp.toPath());
         PDDocument document = getPDDocument(temp);
         extractor = new ObjectExtractor(document);
         PageIterator it = extractor.extract();

         while (it.hasNext()) {
            Page p = it.next();
            CorePdfPageBean corePdfPageBean = new CorePdfPageBean();
            corePdfPageBean.setWidth(p.getWidth());
            corePdfPageBean.setHeight(p.getHeight());
            corePdfPageBean.setPageNumber(p.getPageNumber());
            corePdfPageBean.setRotation(p.getRotation());
            corePdfPageList.add(corePdfPageBean);
         }

         Collections.reverse(corePdfPageList);
         var18 = corePdfPageList;
      } catch (Exception var16) {
         throw new FileException(var16);
      } finally {
         if (extractor != null) {
            try {
               extractor.close();
            } catch (IOException var15) {
               throw new FileException(var15);
            }
         }

         FileUtils.deleteQuietly(temp);
      }

      return (List<CorePdfPageBean>)var18;
   }

   public static List<CorePdfAcqConfigLineBean> getPdfAcqConfigLineList(CoreFileBean coreFile) {
      List<CorePdfAcqConfigLineBean> pdfAcqConfigLineList = new ArrayList<>();
      File temp = null;
      ObjectExtractor extractor = null;

      Object var18;
      try {
         CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
         temp = FileUtils.createTempFile();
         fileManager.download(coreFile, temp.toPath());
         PDDocument document = getPDDocument(temp);
         extractor = new ObjectExtractor(document);
         PageIterator it = extractor.extract();

         while (it.hasNext()) {
            Page p = it.next();
            CorePdfAcqConfigLineBean pdfAcqConfigLine = new CorePdfAcqConfigLineBean();
            pdfAcqConfigLine.setWidth(p.getWidth());
            pdfAcqConfigLine.setHeight(p.getHeight());
            pdfAcqConfigLine.setPageNumber(p.getPageNumber());
            pdfAcqConfigLine.setRotation(p.getRotation());
            pdfAcqConfigLineList.add(pdfAcqConfigLine);
         }

         Collections.reverse(pdfAcqConfigLineList);
         var18 = pdfAcqConfigLineList;
      } catch (Exception var16) {
         throw new FileException(var16);
      } finally {
         if (extractor != null) {
            try {
               extractor.close();
            } catch (IOException var15) {
               throw new FileException(var15);
            }
         }

         FileUtils.deleteQuietly(temp);
      }

      return (List<CorePdfAcqConfigLineBean>)var18;
   }

   public static List<CorePdfPageImageDTO> getPdfPageImageList(CoreFileBean file) {
      List<CorePdfPageImageDTO> pdfImageList = new ArrayList<>();
      File temp = null;
      PDDocument pdfDocument = null;

      Object var86;
      try {
         CoreFileManager fileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
         temp = FileUtils.createTempFile();
         fileManager.download(file, temp.toPath());
         pdfDocument = getPDDocument(temp);
         int totalPages = pdfDocument.getNumberOfPages();
         PDFRenderer renderer = new PDFRenderer(pdfDocument);

         for (int pageNum = 0; pageNum < totalPages; pageNum++) {
            String imageFileName = String.format("%s_%d.png", StringUtils.randomUUID(16), pageNum + 1);
            File imageFile = new File(Paths.get(temp.getParent(), imageFileName).toString());
            if (!imageFile.exists()) {
               try {
                  BufferedImage image = renderer.renderImageWithDPI(pageNum, 150.0F);
                  int imageWidth = image.getWidth();
                  int imageHeight = image.getHeight();
                  float scale = 1500.0F / imageWidth;
                  BufferedImage bi = new BufferedImage(1500, Math.round(imageHeight * scale), image.getType());
                  bi.getGraphics().drawImage(image.getScaledInstance(1500, Math.round(imageHeight * scale), 4), 0, 0, null);
                  FilePathDTO filePath = CoreFileUtils.toFilePath(file);
                  filePath.setFileName(imageFileName);

                  try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                     ImageIO.write(bi, "png", out);

                     try (InputStream is = new ByteArrayInputStream(out.toByteArray())) {
                        fileManager.upload(filePath, is);
                     }
                  }

                  CorePdfPageImageDTO pdfImageDTO = new CorePdfPageImageDTO();
                  pdfImageDTO.setImageWidth(1500.0);
                  pdfImageDTO.setImageHeight((double)Math.round(imageHeight * scale));
                  pdfImageDTO.setImageName(fileManager.getDownloadUrl(filePath));
                  pdfImageList.add(pdfImageDTO);
               } finally {
                  FileUtils.deleteQuietly(imageFile);
               }
            }
         }

         Collections.reverse(pdfImageList);
         var86 = pdfImageList;
      } catch (Exception var84) {
         throw new FileException(var84);
      } finally {
         if (pdfDocument != null) {
            try {
               pdfDocument.close();
            } catch (IOException var76) {
               throw new FileException(var76);
            }
         }

         FileUtils.deleteQuietly(temp);
      }

      return (List<CorePdfPageImageDTO>)var86;
   }

   public static List<CorePdfAcqResultDTO> parse(List<CorePdfAcqAreaDTO> corePdfGridList) {
      List<CorePdfAcqResultDTO> pdfResultList = new ArrayList<>();
      List<String> sampleIdList = new ArrayList<>();
      List<String> testItemList = new ArrayList<>();
      corePdfGridList.forEach(e -> {
         if (PdfAcqParseType.SAMPLE_NO.name().toString().equals(e.getParseType())) {
            e.getData().forEach(f -> sampleIdList.add(f.get(0)));
         } else if (PdfAcqParseType.TEST_ITEM.name().toString().equals(e.getParseType())) {
            e.getData().forEach(f -> testItemList.add(f.get(0)));
         } else if (PdfAcqParseType.RESULT_TABLE.name().toString().equals(e.getParseType())) {
            List<List<String>> dataList = PdfAcqTableDisplayWay.LIST.toString().equals(e.getTableDataDisplayWay()) ? e.getData() : reverseList(e.getData());
            e.getResult().forEach(f -> {
               String itemName = f.getItemName();
               Long itemColumnNo = f.getItemColNo() == null ? -1L : f.getItemColNo() - 1L;
               Long resultColumnNo = f.getResultColNo() == null ? -1L : f.getResultColNo() - 1L;
               Long sampleNoColumnNo = f.getSampleNoColNo() == null ? -1L : f.getSampleNoColNo() - 1L;

               for (int i = 0; i < dataList.size(); i++) {
                  List<String> valueList = dataList.get(i);
                  CorePdfAcqResultDTO pdfResult = new CorePdfAcqResultDTO();
                  if (!StringUtils.isBlank(itemName)) {
                     pdfResult.setItemName(itemName);
                  } else if (itemColumnNo > -1L) {
                     String testName = valueList.get(itemColumnNo.intValue());
                     if (!StringUtils.isBlank(testName)) {
                        pdfResult.setItemName(testName);
                     } else if (pdfResultList.size() > 0) {
                        pdfResult.setItemName(pdfResultList.get(pdfResultList.size() - 1).getItemName());
                     }
                  }

                  if (sampleNoColumnNo > -1L) {
                     String SampleId = valueList.get(sampleNoColumnNo.intValue());
                     if (!StringUtils.isBlank(SampleId)) {
                        pdfResult.setSampleId(SampleId);
                     } else if (pdfResultList.size() > 0) {
                        pdfResult.setSampleId(pdfResultList.get(pdfResultList.size() - 1).getSampleId());
                     }
                  }

                  if (resultColumnNo > -1L) {
                     pdfResult.setResult(valueList.get(resultColumnNo.intValue()));
                  }

                  pdfResultList.add(pdfResult);
               }
            });
         }
      });
      if (sampleIdList.size() > 0) {
         for (int i = 0; i < pdfResultList.size(); i++) {
            if (StringUtils.isEmpty(pdfResultList.get(i).getSampleId())) {
               pdfResultList.get(i).setSampleId(sampleIdList.get(0));
            }
         }
      }

      if (testItemList.size() > 0) {
         for (int ix = 0; ix < pdfResultList.size(); ix++) {
            if (StringUtils.isEmpty(pdfResultList.get(ix).getItemName())) {
               pdfResultList.get(ix).setItemName(testItemList.get(0));
            }
         }
      }

      List<CorePdfAcqResultDTO> sortedPdfResultList = new ArrayList<>();
      pdfResultList.forEach(
         e -> {
            long count = sortedPdfResultList.stream()
               .filter(f -> StringUtils.equals(f.getSampleId(), e.getSampleId()) && StringUtils.equals(f.getItemName(), e.getItemName()))
               .count();
            e.setOrderNo(++count);
            sortedPdfResultList.add(e);
         }
      );
      return pdfResultList;
   }

   public static List<List<String>> reverseList(List<List<String>> inputList) {
      List<List<String>> outputList = new ArrayList<>();

      for (int i = 0; i < inputList.size(); i++) {
         for (int j = 0; j < inputList.get(i).size(); j++) {
            if (outputList.size() < j + 1) {
               List<String> out = new ArrayList<>();
               outputList.add(j, out);
            }

            outputList.get(j).add(inputList.get(i).get(j));
         }
      }

      return outputList;
   }
}
