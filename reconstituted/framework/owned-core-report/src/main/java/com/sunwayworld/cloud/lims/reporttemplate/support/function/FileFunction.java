package com.sunwayworld.cloud.lims.reporttemplate.support.function;

import com.alibaba.fastjson.JSONObject;
import com.deepoove.poi.data.DocxRenderData;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.TextSelection;
import com.spire.doc.fields.TextRange;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsDocxRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.helper.LimsTemplateDataCacheHolder;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.FrUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.PictureContractionUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.QRPicUtil;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface FileFunction {
   Logger logger = LoggerFactory.getLogger("FileFunction");

   default DocxRenderData subD(List<LimsTemplateDataMap> paramList, String templateField) {
      return this.subD(paramList, templateField, false);
   }

   default CoreFileBean getFrDocx(LimsTemplateDataMap renderData) throws IOException {
      CoreReportConfigLineBean coreReportConfigLineBean = PersistableHelper.mapToPersistable(renderData, CoreReportConfigLineBean.class);
      Map<String, String> ext$ = coreReportConfigLineBean.getExt$();
      String targetid = ext$.remove("targetid");
      if (coreReportConfigLineBean.getId() != null) {
         ext$.put("id", coreReportConfigLineBean.getId().toString());
      }

      CoreFileService coreFileService = ApplicationContextHelper.getBean(CoreFileService.class);
      CoreFileBean frFileBean = coreFileService.selectFileListByTargetIds(targetid).stream().findFirst().orElseThrow(() -> new InvalidDataException("报表附件不存在"));
      CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);
      coreFileManager.download(frFileBean);
      File wTemp = FileUtils.createTempFile("doc");
      FrUtil.createWord(coreReportConfigLineBean, frFileBean, wTemp, ext$);
      File wordTempTemp = FileUtils.createTempFile("docx");
      docToDocx(wTemp.toPath(), wordTempTemp.toPath());
      File wordTemp = FileUtils.createTempFile("docx");
      changeFont(wordTempTemp, wordTemp);
      CoreFileBean insertCoreFile = new CoreFileBean();
      insertCoreFile.setId(ApplicationContextHelper.getNextIdentity());
      insertCoreFile.setTargetId("QIAO_TEMP$" + ApplicationContextHelper.getNextIdentity());
      insertCoreFile.setName(wordTemp.getName());
      insertCoreFile.setFileExt(FileUtils.getFileExtension(insertCoreFile.getName()));
      insertCoreFile.setMd5Name(EncryptUtils.MD5Encrypt(insertCoreFile.getTargetId() + "$" + insertCoreFile.getName()));
      insertCoreFile.setScope(FileScope.secure.name());
      insertCoreFile.setSize(wordTemp.length());
      insertCoreFile.setTimes(0L);
      insertCoreFile.setDownloadable("1");
      insertCoreFile.setCreatedTime(LocalDateTime.now());
      coreFileManager.upload(insertCoreFile, wordTemp);
      coreFileService.getDao().insert(insertCoreFile);
      return insertCoreFile;
   }

   static void docToDocx(Path wordFilePath, Path docxpath) {
      FileUtils.makeDirs(docxpath);

      try (
         OutputStream os = new BufferedOutputStream(Files.newOutputStream(docxpath.toFile().toPath()));
         InputStream nis = Files.newInputStream(wordFilePath);
      ) {
         Document document = new Document();
         document.loadFromStream(nis, FileFormat.Auto);
         document.saveToStream(os, FileFormat.Docx);
      } catch (IOException var34) {
         throw new FileException(var34);
      }
   }

   default DocxRenderData subD(List<LimsTemplateDataMap> paramList, String templateField, Boolean pageBreak) {
      return this.subD(paramList, templateField, pageBreak, 7);
   }

   default DocxRenderData subD(List<LimsTemplateDataMap> paramList, String templateField, Boolean pageBreak, Integer pageBreakLevel) {
      if (CollectionUtils.isEmpty(paramList)) {
         return null;
      } else {
         List<DocxRenderData> subDocxList = new ArrayList<>();
         paramList.forEach(param -> {
            Object templateFile = param.get(templateField);
            if (templateFile instanceof CoreFileBean) {
               byte[] fileByte = LimsFileUtils.getFileByte((CoreFileBean)templateFile);
               DocxRenderData subDocxRenderData = new DocxRenderData(fileByte, ArrayUtils.asList(param));
               subDocxList.add(subDocxRenderData);
            } else if (templateFile instanceof List && !CollectionUtils.isEmpty((List)templateFile) && ((List)templateFile).get(0) instanceof CoreFileBean) {
               ((List)templateFile).forEach(template -> {
                  byte[] fileBytex = LimsFileUtils.getFileByte((CoreFileBean)template);
                  DocxRenderData subDocxRenderDatax = new DocxRenderData(fileBytex, ArrayUtils.asList(param));
                  subDocxList.add(subDocxRenderDatax);
               });
            } else {
               logger.error("the templateField {} for subDocxFunction not found or not a CoreFileBean", templateField);
            }
         });
         if (CollectionUtils.isEmpty(subDocxList)) {
            return null;
         } else {
            LimsDocxRenderData limsDocxRenderData = new LimsDocxRenderData(new byte[0], subDocxList);
            limsDocxRenderData.setBreakPage(pageBreak);
            limsDocxRenderData.setPageBreakLevel(pageBreakLevel);
            return limsDocxRenderData;
         }
      }
   }

   default DocxRenderData getDocx(CoreFileBean docFile, List<? extends Serializable> paramArray) {
      return this.getDocx(docFile, paramArray, false);
   }

   default DocxRenderData getDocx(CoreFileBean docFile, List<? extends Serializable> paramArray, boolean pageBreak) {
      return this.getDocx(docFile, paramArray, pageBreak, 7);
   }

   default DocxRenderData getDocx(CoreFileBean docFile, List<? extends Serializable> paramArray, Boolean pageBreak, Integer pageBreakLevel) {
      if (docFile == null) {
         return null;
      } else {
         LimsDocxRenderData limsDocxRenderData = new LimsDocxRenderData(LimsFileUtils.getFilePath(docFile).toFile(), paramArray);
         limsDocxRenderData.setBreakPage(pageBreak);
         limsDocxRenderData.setPageBreakLevel(pageBreakLevel);
         return limsDocxRenderData;
      }
   }

   default DocxRenderData getDocx(String fileTag, List<? extends Serializable> paramArray) {
      return this.getDocx(fileTag, paramArray, Boolean.valueOf(false));
   }

   default DocxRenderData getDocx(String fileTag, List<? extends Serializable> paramArray, Boolean pageBreak) {
      return this.getDocx(fileTag, paramArray, pageBreak, 7);
   }

   default DocxRenderData getDocx(String filePath) {
      return StringUtils.isEmpty(filePath) ? null : new LimsDocxRenderData(new File(filePath));
   }

   default DocxRenderData getDocx(String fileTag, List<? extends Serializable> paramArray, Boolean pageBreak, Integer pageBreakLevel) {
      CoreFileBean docFile = this.getTagFile(fileTag);
      if (docFile == null) {
         return null;
      } else {
         LimsDocxRenderData limsDocxRenderData = new LimsDocxRenderData(LimsFileUtils.getFilePath(docFile).toFile(), paramArray);
         limsDocxRenderData.setBreakPage(pageBreak);
         limsDocxRenderData.setPageBreakLevel(pageBreakLevel);
         return limsDocxRenderData;
      }
   }

   default CoreFileBean getTagFile(String field) {
      return LimsTemplateDataCacheHolder.get(field);
   }

   default DocxRenderData getDocx(List<CoreFileBean> docxList) {
      return this.getDocx(docxList, false);
   }

   default DocxRenderData getDocx(List<CoreFileBean> docxList, Boolean pageBreak) {
      return this.getDocx(docxList, pageBreak, 7);
   }

   default DocxRenderData getDocx(List<CoreFileBean> docxList, Boolean pageBreak, Integer pageBreakLevel) {
      if (CollectionUtils.isEmpty(docxList)) {
         return null;
      } else {
         LimsTemplateDataList<DocxRenderData> subDocxList = new LimsTemplateDataList<>();
         docxList.forEach(docxFile -> {
            try {
               byte[] fileByte = LimsFileUtils.getFileByte(docxFile);
               subDocxList.add(new DocxRenderData(fileByte, null));
            } catch (Exception var3) {
               logger.error("GET FILE {} ERROR", docxFile.getName());
            }
         });
         LimsDocxRenderData limsDocxRenderData = new LimsDocxRenderData(new byte[0], subDocxList);
         limsDocxRenderData.setBreakPage(pageBreak);
         limsDocxRenderData.setPageBreakLevel(pageBreakLevel);
         return limsDocxRenderData;
      }
   }

   default PictureRenderData sign() {
      return this.getPic(this.userSign());
   }

   default CoreFileBean userSign() {
      String loginUserId = LocalContextHelper.getLoginUserId();
      CoreFileService coreFileService = ApplicationContextHelper.getBean(CoreFileService.class);
      return coreFileService.selectFirstByFilter(
         SearchFilter.instance()
            .match("targetId", "T_CORE_USER$" + loginUserId)
            .filter(MatchPattern.EQ)
            .match("bizCategory", "SIGNATURE")
            .filter(MatchPattern.EQ),
         new Order[0]
      );
   }

   default PictureRenderData getPic(CoreFileBean coreFileBean, int... sizeArray) {
      if (coreFileBean == null) {
         return null;
      } else {
         CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);

         try {
            coreFileManager.download(coreFileBean);
         } catch (Exception var14) {
            logger.error("some error happened when download file {}", JSONObject.toJSONString(coreFileBean), var14);
            throw new FileException(var14);
         }

         int width = sizeArray.length > 0 ? sizeArray[0] : 0;
         int height = sizeArray.length > 1 ? sizeArray[1] : 0;
         File picFile = null;

         try {
            picFile = LimsFileUtils.getFilePath(coreFileBean).toFile();
         } catch (Exception var13) {
            logger.error("get picture error {}", JSONObject.toJSONString(coreFileBean), var13);
         }

         if (picFile != null && picFile.exists()) {
            BufferedImage image = null;

            try {
               image = PictureContractionUtils.contraction(picFile);
            } catch (Exception var12) {
               logger.error("get pic to bufferImage error");
            }

            if (image != null) {
               int imgWidth = image.getWidth();
               int imgHeight = image.getHeight();
               if (width != 0 && height != 0) {
                  if (imgWidth >= imgHeight) {
                     double rate = ArithUtils.div(width, imgWidth);
                     height = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                  } else {
                     double rate = ArithUtils.div(height, imgHeight);
                     width = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                  }
               } else if (width == 0 && height != 0) {
                  double rate = ArithUtils.div(height, imgHeight);
                  width = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
               } else if (width != 0) {
                  double rate = ArithUtils.div(width, imgWidth);
                  height = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
               } else {
                  width = imgWidth;
                  height = imgHeight;
               }
            }

            try {
               if (image != null) {
                  ImageIO.write(image, "png", picFile);
               }
            } catch (IOException var15) {
               throw new RuntimeException(var15);
            }

            return new PictureRenderData(width, height, picFile);
         } else {
            logger.error("PIC FILE NOT FOUND : {}", coreFileBean.getName());
            return null;
         }
      }
   }

   default PictureRenderData getPic(CoreFileBean coreFileBean, double accuracy, int... sizeArray) {
      if (coreFileBean == null) {
         return null;
      } else {
         CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);

         try {
            coreFileManager.download(coreFileBean);
         } catch (Exception var16) {
            logger.error("some error happened when download file {}", JSONObject.toJSONString(coreFileBean), var16);
            throw new FileException(var16);
         }

         int width = sizeArray.length > 0 ? sizeArray[0] : 0;
         int height = sizeArray.length > 1 ? sizeArray[1] : 0;
         File picFile = null;

         try {
            picFile = LimsFileUtils.getFilePath(coreFileBean).toFile();
         } catch (Exception var15) {
            logger.error("get picture error {}", JSONObject.toJSONString(coreFileBean), var15);
         }

         if (picFile != null && picFile.exists()) {
            BufferedImage image = null;

            try {
               image = PictureContractionUtils.contraction(picFile);
            } catch (Exception var14) {
               logger.error("get pic to bufferImage error");
            }

            if (image != null) {
               int imgWidth = image.getWidth();
               int imgHeight = image.getHeight();
               if (width != 0 && height != 0) {
                  if (imgWidth >= imgHeight) {
                     double rate = ArithUtils.div(width, imgWidth);
                     height = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                  } else {
                     double rate = ArithUtils.div(height, imgHeight);
                     width = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                  }
               } else if (width == 0 && height != 0) {
                  double rate = ArithUtils.div(height, imgHeight);
                  width = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
               } else if (width != 0) {
                  double rate = ArithUtils.div(width, imgWidth);
                  height = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
               } else {
                  width = imgWidth;
                  height = imgHeight;
               }
            }

            try {
               if (image != null) {
                  ImageIO.write(image, "png", picFile);
               }
            } catch (IOException var17) {
               throw new RuntimeException(var17);
            }

            return getCompressPicFile(picFile, width, height, accuracy);
         } else {
            logger.error("PIC FILE NOT FOUND : {}", coreFileBean.getName());
            return null;
         }
      }
   }

   default PictureRenderData getPic(String filePath, int... sizeArray) {
      int width = sizeArray.length > 0 ? sizeArray[0] : 0;
      int height = sizeArray.length > 1 ? sizeArray[1] : 0;
      if (!StringUtils.isEmpty(filePath)) {
         File picFile = new File(filePath);
         if (picFile.exists()) {
            if (width == 0 || height == 0) {
               BufferedImage image = null;

               try {
                  image = ImageIO.read(picFile);
               } catch (IOException var11) {
                  logger.error("get pic to bufferImage error");
               }

               if (image != null) {
                  int imgWidth = image.getWidth();
                  int imgHeight = image.getHeight();
                  if (width == 0 && height != 0) {
                     double rate = ArithUtils.div(height, imgHeight);
                     width = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                  } else if (width != 0) {
                     double rate = ArithUtils.div(width, imgWidth);
                     height = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                  } else {
                     width = image.getWidth();
                     height = image.getHeight();
                  }
               }
            }

            return new PictureRenderData(width, height, picFile);
         }

         logger.error("PIC FILE NOT FOUND : {}", picFile.getName());
      }

      return null;
   }

   default PictureRenderData getPic(String filePath, double accuracy, int... sizeArray) {
      int width = sizeArray.length > 0 ? sizeArray[0] : 0;
      int height = sizeArray.length > 1 ? sizeArray[1] : 0;
      if (!StringUtils.isEmpty(filePath)) {
         File picFile = new File(filePath);
         if (picFile.exists()) {
            if (width == 0 || height == 0) {
               BufferedImage image = null;

               try {
                  image = ImageIO.read(picFile);
               } catch (IOException var13) {
                  logger.error("get pic to bufferImage error");
               }

               if (image != null) {
                  int imgWidth = image.getWidth();
                  int imgHeight = image.getHeight();
                  if (width == 0 && height != 0) {
                     double rate = ArithUtils.div(height, imgHeight);
                     width = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                  } else if (width != 0) {
                     double rate = ArithUtils.div(width, imgWidth);
                     height = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                  } else {
                     width = image.getWidth();
                     height = image.getHeight();
                  }
               }
            }

            return getCompressPicFile(picFile, width, height, accuracy);
         }

         logger.error("PIC FILE NOT FOUND : {}", picFile.getName());
      }

      return null;
   }

   default List<PictureRenderData> getPicL(Integer width, Integer height, List<CoreFileBean> picFileList) {
      List<PictureRenderData> picRenderDataList = new ArrayList<>();
      if (CollectionUtils.isEmpty(picFileList)) {
         return picRenderDataList;
      } else {
         final int resolvedWidth = width != null ? width : 150;
         final int resolvedHeight = height != null ? height : 150;
         picFileList.forEach(coreFileBean -> {
            new PictureRenderData(resolvedWidth, resolvedHeight, LimsFileUtils.getFilePath(coreFileBean).toString());
            System.out.println(LimsFileUtils.getFilePath(coreFileBean).toString());
            int picWidth = resolvedWidth;
            int picHeight = resolvedHeight;
            if (coreFileBean != null) {
               File picFile = null;

               try {
                  picFile = LimsFileUtils.getFilePath(coreFileBean).toFile();
               } catch (Exception var14) {
                  logger.error("get picture error {}", JSONObject.toJSONString(coreFileBean), var14);
               }

               if (picFile != null) {
                  if (picWidth == 0 || picHeight == 0) {
                     BufferedImage image = null;

                     try {
                        image = ImageIO.read(picFile);
                     } catch (IOException var13) {
                        logger.error("get pic to bufferImage error");
                     }

                     if (image != null) {
                        int imgWidth = image.getWidth();
                        int imgHeight = image.getHeight();
                        if (picWidth == 0 && picHeight != 0) {
                           double rate = ArithUtils.div(picHeight, imgHeight);
                           picWidth = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                        } else if (picWidth != 0) {
                           double rate = ArithUtils.div(picWidth, imgWidth);
                           picHeight = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                        } else {
                           picWidth = image.getWidth();
                           picHeight = image.getHeight();
                        }
                     }
                  }

                  picRenderDataList.add(new PictureRenderData(picWidth, picHeight, picFile));
               }
            }
         });
         return picRenderDataList;
      }
   }

   default List<PictureRenderData> getPicL(Integer width, Integer height, double accuracy, List<CoreFileBean> picFileList) {
      List<PictureRenderData> picRenderDataList = new ArrayList<>();
      if (CollectionUtils.isEmpty(picFileList)) {
         return picRenderDataList;
      } else {
         final int resolvedWidth = width != null ? width : 150;
         final int resolvedHeight = height != null ? height : 150;
         picFileList.forEach(coreFileBean -> {
            new PictureRenderData(resolvedWidth, resolvedHeight, LimsFileUtils.getFilePath(coreFileBean).toString());
            System.out.println(LimsFileUtils.getFilePath(coreFileBean).toString());
            int picWidth = resolvedWidth;
            int picHeight = resolvedHeight;
            if (coreFileBean != null) {
               File picFile = null;

               try {
                  picFile = LimsFileUtils.getFilePath(coreFileBean).toFile();
               } catch (Exception var16) {
                  logger.error("get picture error {}", JSONObject.toJSONString(coreFileBean), var16);
               }

               if (picFile != null) {
                  if (picWidth == 0 || picHeight == 0) {
                     BufferedImage image = null;

                     try {
                        image = ImageIO.read(picFile);
                     } catch (IOException var15) {
                        logger.error("get pic to bufferImage error");
                     }

                     if (image != null) {
                        int imgWidth = image.getWidth();
                        int imgHeight = image.getHeight();
                        if (picWidth == 0 && picHeight != 0) {
                           double rate = ArithUtils.div(picHeight, imgHeight);
                           picWidth = NumberUtils.parseInt(ArithUtils.mul(rate, imgWidth));
                        } else if (picWidth != 0) {
                           double rate = ArithUtils.div(picWidth, imgWidth);
                           picHeight = NumberUtils.parseInt(ArithUtils.mul(rate, imgHeight));
                        } else {
                           picWidth = image.getWidth();
                           picHeight = image.getHeight();
                        }
                     }
                  }

                  PictureRenderData compressPicFileRenderData = getCompressPicFile(picFile, picWidth, picHeight, accuracy);
                  if (compressPicFileRenderData != null) {
                     picRenderDataList.add(compressPicFileRenderData);
                  }
               }
            }
         });
         return picRenderDataList;
      }
   }

   static PictureRenderData getCompressPicFile(File picFile, int picWidth, int picHeight, double accuracy) {
      byte[] oldFileBytes = null;

      try {
         byte[] originFileBytes = Files.readAllBytes(picFile.toPath());
         oldFileBytes = compressPicForScale(originFileBytes, accuracy);
      } catch (IOException var7) {
         var7.printStackTrace();
      }

      if (oldFileBytes != null && oldFileBytes.length != 0) {
         return new PictureRenderData(picWidth, picHeight, PictureType.suggestFileType(picFile.getPath()), oldFileBytes);
      } else {
         logger.error("PIC FILE COMPRESS FAIL : {}", picFile.getName());
         return null;
      }
   }

   static byte[] compressPicForScale(byte[] imageBytes, double accuracy) {
      if (imageBytes != null && imageBytes.length > 0) {
         try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(imageBytes.length);
            Thumbnails.of(inputStream).scale(accuracy).outputQuality(accuracy).toOutputStream(outputStream);
            imageBytes = outputStream.toByteArray();
         } catch (Exception var5) {
            var5.printStackTrace();
         }

         return imageBytes;
      } else {
         return imageBytes;
      }
   }

   default PictureRenderData getPicB(byte[] picByte, int... sizeArray) {
      int width = sizeArray.length > 0 ? sizeArray[0] : 150;
      int height = sizeArray.length > 1 ? sizeArray[1] : 150;
      return new PictureRenderData(width, height, PictureType.suggestFileType(picByte), picByte);
   }

   default PictureRenderData getQr(String code, int width, int height, int margin) {
      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

      try {
         QRPicUtil.generateQRCodeImage(BarcodeFormat.QR_CODE, code, width, height, margin, byteOut);
         PictureRenderData pictureRenderData = new PictureRenderData(width, height, PictureType.PNG, byteOut.toByteArray());
         byteOut.close();
         return pictureRenderData;
      } catch (IOException | WriterException var17) {
         logger.error("create qrCode error", (Throwable)var17);
      } finally {
         try {
            byteOut.close();
         } catch (IOException var16) {
         }
      }

      return null;
   }

   default PictureRenderData getQ(String code, int width, int height, int margin, String barcode) throws IOException {
      try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
         BarcodeFormat barcodeFormat;
         try {
            barcodeFormat = BarcodeFormat.valueOf(barcode);
         } catch (Exception var21) {
            barcodeFormat = BarcodeFormat.QR_CODE;
         }

         QRPicUtil.generateQRCodeImage(barcodeFormat, code, width, height, margin, byteOut);
         PictureRenderData pictureRenderData = new PictureRenderData(width, height, PictureType.PNG, byteOut.toByteArray());
         byteOut.close();
         return pictureRenderData;
      } catch (Exception var24) {
         logger.error("create qrCode error", (Throwable)var24);
         return null;
      }
   }

   default PictureRenderData getQ(String code, int width, int height, int margin, String barcode, boolean writeText) throws IOException {
      try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
         BarcodeFormat barcodeFormat;
         try {
            barcodeFormat = BarcodeFormat.valueOf(barcode);
         } catch (Exception var22) {
            barcodeFormat = BarcodeFormat.QR_CODE;
         }

         QRPicUtil.generateQRCodeImage(barcodeFormat, code, width, height, margin, byteOut, writeText);
         PictureRenderData pictureRenderData = new PictureRenderData(width, height, PictureType.PNG, byteOut.toByteArray());
         byteOut.close();
         return pictureRenderData;
      } catch (Exception var25) {
         logger.error("create qrCode error", (Throwable)var25);
         return null;
      }
   }

   static void changeFont(File source, File target) throws IOException {
      Document document = new Document();
      document.loadFromStream(Files.newInputStream(source.toPath()), FileFormat.Auto);
      TextSelection[] sections = document.findAllString("", true, true);

      for (Object obj : sections) {
         if (obj instanceof TextSelection) {
            try {
               TextSelection textSelection = (TextSelection)obj;
               TextRange range = textSelection.getAsOneRange();
               if (range != null) {
                  range.getCharacterFormat().setFontNameFarEast("宋体");
                  range.getCharacterFormat().setFontNameAscii("Times New Roman");
               }
            } catch (Exception var10) {
            }
         }
      }

      OutputStream os = new BufferedOutputStream(new FileOutputStream(target));
      document.saveToStream(os, document.getDetectedFormatType());
   }
}
