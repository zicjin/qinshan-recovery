package com.sunwayworld.cloud.module.ai.template.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.ai.surport.ImageUtils;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiScanResultDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateDefineDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateFieldDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateLocationDTO;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateRotateResultDTO;
import com.sunwayworld.cloud.module.ai.template.persistent.dao.CoreAiTemplateDao;
import com.sunwayworld.cloud.module.ai.template.service.CoreAiTemplateService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.NetworkException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Repository
@GikamBean
public class CoreAiTemplateServiceImpl implements CoreAiTemplateService {
   private static final Logger log = LoggerFactory.getLogger(CoreAiTemplateServiceImpl.class);
   private static final String OCR_TEMPLATE_PIC_CACHE = "OCR_PIC_CHECHE";
   @Value("${sunway.ai.ocr.server.url:}")
   private String ocrServer;
   @Autowired
   private CoreAiTemplateDao coreAiTemplateDao;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   private CoreFileManager coreFileManager;

   public CoreAiTemplateDao getDao() {
      return this.coreAiTemplateDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreAiTemplateBean coreAiTemplate = jsonWrapper.parseUnique(CoreAiTemplateBean.class);
      String fileId = jsonWrapper.getParamValue("fileId");
      coreAiTemplate.setId(ApplicationContextHelper.getNextIdentity());
      coreAiTemplate.setFileId(NumberUtils.parseLong(fileId));
      coreAiTemplate.setName(coreAiTemplate.getExt$Item("templatename"));
      this.setTemplatePicPath(coreAiTemplate);
      File picFile = this.downloadPicFile(coreAiTemplate);
      ImageUtils.checkFile(picFile);
      CoreFileBean coreFile = this.uoloadFile(picFile);
      coreAiTemplate.setFileId(NumberUtils.parseLong(coreFile.getId()));
      this.setTemplatePicPath(coreAiTemplate);

      try {
         Image image = ImageIO.read(picFile);
         Long width = NumberUtils.parseLong(image.getWidth(null));
         Long height = NumberUtils.parseLong(image.getHeight(null));
         coreAiTemplate.setWidth(width);
         coreAiTemplate.setHeight(height);
      } catch (IOException var12) {
         throw new FileException(var12);
      } finally {
         FileUtils.deleteQuietly(picFile);
      }

      coreAiTemplate.setModifyTime(LocalDateTime.now());
      coreAiTemplate.setInfo("{\"benchmarks\":[],\"fixFields\":[]}");
      this.getDao().insert(coreAiTemplate);
      return coreAiTemplate.getId();
   }

   @Override
   public CoreAiTemplateDTO getTemplateDetail(Long id) {
      CoreAiTemplateBean coreAiTemplate = this.selectById(id);
      CoreAiTemplateDTO coreAiTemplateDTO = new CoreAiTemplateDTO();
      BeanUtils.copyProperties(coreAiTemplate, coreAiTemplateDTO, "info");
      String info = coreAiTemplate.getInfo();
      CoreAiTemplateDefineDTO coreAiTemplateDefineDTO = JSONObject.parseObject(info, CoreAiTemplateDefineDTO.class);
      coreAiTemplateDTO.setInfo(coreAiTemplateDefineDTO);
      RedisHelper.evict("OCR_PIC_CHECHE", String.valueOf(id));
      return coreAiTemplateDTO;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public CoreAiTemplateDTO changePic(Long id, RestJsonWrapperBean jsonWrapper) {
      CoreAiTemplateBean coreAiTemplate = this.selectById(id);
      String fileId = jsonWrapper.getParamValue("fileId");
      coreAiTemplate.setFileId(NumberUtils.parseLong(fileId));
      this.setTemplatePicPath(coreAiTemplate);
      File picFile = this.downloadPicFile(coreAiTemplate);
      ImageUtils.checkFile(picFile);
      CoreFileBean coreFile = this.uoloadFile(picFile);
      coreAiTemplate.setFileId(NumberUtils.parseLong(coreFile.getId()));
      this.setTemplatePicPath(coreAiTemplate);

      try {
         Image image = ImageIO.read(picFile);
         Long width = NumberUtils.parseLong(image.getWidth(null));
         Long height = NumberUtils.parseLong(image.getHeight(null));
         coreAiTemplate.setWidth(width);
         coreAiTemplate.setHeight(height);
         coreAiTemplate.setModifyTime(LocalDateTime.now());
      } catch (IOException var13) {
         throw new FileException(var13);
      } finally {
         FileUtils.deleteQuietly(picFile);
      }

      coreAiTemplate.setInfo("{\"benchmarks\":[],\"fixFields\":[]}");
      RedisHelper.put("OCR_PIC_CHECHE", String.valueOf(id), coreAiTemplate);
      CoreAiTemplateDTO var15 = new CoreAiTemplateDTO();
      BeanUtils.copyProperties(coreAiTemplate, var15, "info");
      CoreAiTemplateDefineDTO var16 = JSONObject.parseObject(coreAiTemplate.getInfo(), CoreAiTemplateDefineDTO.class);
      var15.setInfo(var16);
      return var15;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public CoreAiTemplateDTO rotatePic(Long id, RestJsonWrapperBean jsonWrapper) {
      CoreAiTemplateBean coreAiTemplate = this.selectById(id);
      CoreAiTemplateBean coreAiTemplateCache = RedisHelper.get("OCR_PIC_CHECHE", String.valueOf(id));
      if (!BeanUtils.isEmpty(coreAiTemplateCache)) {
         coreAiTemplate.setPicPath(coreAiTemplateCache.getPicPath());
         coreAiTemplate.setWidth(coreAiTemplateCache.getWidth());
         coreAiTemplate.setHeight(coreAiTemplateCache.getHeight());
         coreAiTemplate.setFileId(coreAiTemplateCache.getFileId());
      }

      Long xFlip = NumberUtils.parseLong(jsonWrapper.getParamValue("x"));
      Long yFlip = NumberUtils.parseLong(jsonWrapper.getParamValue("y"));
      Long rotate = NumberUtils.parseLong(jsonWrapper.getParamValue("z"));
      InputStream bins = null;
      OutputStream baos = null;
      File picFile = null;

      CoreAiTemplateDTO var21;
      try {
         picFile = this.downloadPicFile(coreAiTemplate);
         InputStream inputStream = new FileInputStream(picFile);
         BufferedImage bufferedimage = ImageIO.read(inputStream);
         bufferedimage = ImageUtils.Rotate(bufferedimage, NumberUtils.parseInt(rotate));
         if (xFlip > 0L) {
            bufferedimage = ImageUtils.imageMisro(bufferedimage, 0);
         }

         if (yFlip > 0L) {
            bufferedimage = ImageUtils.imageMisro(bufferedimage, 1);
         }

         baos = new FileOutputStream(picFile);
         ImageIO.write(bufferedimage, "png", baos);
         Image image = ImageIO.read(picFile);
         Long width = NumberUtils.parseLong(image.getWidth(null));
         Long height = NumberUtils.parseLong(image.getHeight(null));
         CoreFileBean coreFile = this.coreFileService.selectById(coreAiTemplate.getFileId());
         Long newFileId = this.coreFileService.upload(coreFile, picFile);
         CoreFileBean newCoreFile = this.coreFileService.selectById(newFileId);
         coreAiTemplate.setFileId(newCoreFile.getId());
         coreAiTemplate.setPicPath(this.coreFileManager.getDownloadUrl(newCoreFile));
         coreAiTemplate.setWidth(width);
         coreAiTemplate.setHeight(height);
         coreAiTemplate.setModifyTime(LocalDateTime.now());
         RedisHelper.put("OCR_PIC_CHECHE", String.valueOf(id), coreAiTemplate);
         CoreAiTemplateDTO coreAiTemplateDTO = new CoreAiTemplateDTO();
         BeanUtils.copyProperties(coreAiTemplate, coreAiTemplateDTO, "info");
         CoreAiTemplateDefineDTO coreAiTemplateDefineDTO = JSONObject.parseObject(coreAiTemplate.getInfo(), CoreAiTemplateDefineDTO.class);
         coreAiTemplateDTO.setInfo(coreAiTemplateDefineDTO);
         var21 = coreAiTemplateDTO;
      } catch (IOException var33) {
         throw new FileException(var33);
      } finally {
         if (baos != null) {
            try {
               baos.close();
            } catch (IOException var32) {
               throw new FileException(var32);
            }
         }

         if (bins != null) {
            try {
               bins.close();
            } catch (IOException var31) {
               throw new FileException(var31);
            }
         }

         FileUtils.deleteQuietly(picFile);
      }

      return var21;
   }

   @Override
   public CoreAiTemplateRotateResultDTO frameSelectedPic(Long id, RestJsonWrapperBean wrapper) {
      String retId = wrapper.getParamValue("id");
      CoreAiTemplateBean coreAiTemplate = this.selectById(id);
      CoreAiTemplateLocationDTO coreAiTemplateLocationDTO = wrapper.parseUnique(CoreAiTemplateLocationDTO.class);
      int xPos = NumberUtils.parseInt(coreAiTemplateLocationDTO.getLeft());
      int yPos = NumberUtils.parseInt(coreAiTemplateLocationDTO.getTop());
      int width = NumberUtils.parseInt(coreAiTemplateLocationDTO.getWidth());
      int height = NumberUtils.parseInt(coreAiTemplateLocationDTO.getHeight());
      File picFile = null;

      String base64Str;
      try {
         picFile = this.downloadPicFile(coreAiTemplate);
         base64Str = ImageUtils.cutPic(picFile, xPos, yPos, width, height);
      } finally {
         FileUtils.deleteQuietly(picFile);
      }

      String result = this.identifyFromTemplate(null, base64Str, null);
      JSONObject resultObject = JSONObject.parseObject(result);
      CoreAiTemplateRotateResultDTO coreAiTemplateRotateResultDTO = new CoreAiTemplateRotateResultDTO();
      if ("-1".equals(resultObject.getString("status"))) {
         coreAiTemplateRotateResultDTO.setId(retId);
         coreAiTemplateRotateResultDTO.setText("");
         coreAiTemplateRotateResultDTO.setError(resultObject.getString("msg"));
      } else {
         StringBuffer resultBuffer = new StringBuffer();
         JSONArray results = resultObject.getJSONArray("results");
         String error = "";
         int wordHeight = 0;
         int firstTop = 0;
         int lastTop = 0;

         for (Object e : results) {
            for (Object f : (JSONArray)e) {
               String value = ((JSONObject)f).getString("text");
               JSONArray locates = ((JSONObject)f).getJSONArray("text_region");
               if (wordHeight == 0) {
                  wordHeight = locates.getJSONArray(2).getInteger(1) - locates.getJSONArray(1).getInteger(1);
               }

               if (firstTop == 0) {
                  firstTop = locates.getJSONArray(0).getInteger(1);
               }

               lastTop = locates.getJSONArray(0).getInteger(1);
               resultBuffer.append(value);
            }
         }

         String textValue = resultBuffer.toString();
         if (StringUtils.isEmpty(textValue)) {
            error = "CORE.MODULE.AI.TEMPLATES.TIP.CAN_NOT_IDENTIFY_SELECT_AREA";
         } else if (lastTop - firstTop >= wordHeight) {
            error = "CORE.MODULE.AI.TEMPLATES.TIP.DO_NOT_SELECT_IN_MORE_THAN_ONE_ROW";
         }

         coreAiTemplateRotateResultDTO.setId(retId);
         coreAiTemplateRotateResultDTO.setText(textValue);
         coreAiTemplateRotateResultDTO.setError(error);
      }

      return coreAiTemplateRotateResultDTO;
   }

   @Override
   public CoreAiScanResultDTO testScanPic(Long id, RestJsonWrapperBean wrapper) {
      Long fileId = NumberUtils.parseLong(wrapper.getParamValue("fileId"));
      CoreFileBean coreFile = this.coreFileService.selectById(fileId);
      File file = FileUtils.createTempFile(coreFile.getFileExt());

      CoreAiScanResultDTO var7;
      try {
         this.coreFileManager.download(coreFile, file.toPath());
         ImageUtils.checkFile(file);
         CoreFileBean newCoreFile = this.uoloadFile(file);
         var7 = this.generateScanResult(id, newCoreFile.getId());
      } finally {
         FileUtils.deleteQuietly(file);
      }

      return var7;
   }

   @Override
   public CoreAiScanResultDTO generateScanResult(Long templateId, Long fileId) {
      CoreAiTemplateBean coreAiTemplate = this.selectById(templateId);
      CoreFileBean coreFile = this.coreFileService.selectById(NumberUtils.parseLong(fileId));
      File tempFile = null;
      FileInputStream in = null;
      String pic = null;
      File picFile = null;

      String templateBase;
      try {
         tempFile = FileUtils.createTempFile();
         this.coreFileManager.download(coreFile, tempFile.toPath());
         byte[] fileByte = new byte[(int)tempFile.length()];
         in = new FileInputStream(tempFile);
         in.read(fileByte);
         pic = Base64.encodeBase64String(fileByte);
         picFile = this.downloadPicFile(coreAiTemplate);
         templateBase = ImageUtils.fileToBase64(picFile);
      } catch (IOException var22) {
         throw new FileException(var22);
      } finally {
         if (in != null) {
            try {
               in.close();
            } catch (IOException var20) {
               throw new FileException(var20);
            }
         }

         FileUtils.deleteQuietly(tempFile);
         FileUtils.deleteQuietly(picFile);
      }

      String var25 = this.identifyFromTemplate(templateBase, pic, coreAiTemplate.getInfo());
      CoreAiScanResultDTO coreAiTemplateResultDTO = null;

      try {
         JSONObject jsonObject = JSONObject.parseObject(var25);
         JSONObject resultObject = jsonObject.getJSONArray("results").getJSONObject(0);
         coreAiTemplateResultDTO = new CoreAiScanResultDTO();
         List<CoreAiTemplateFieldDTO> coreAiTemplateKeyValueDTOList = new ArrayList<>();
         resultObject.forEach((k, v) -> {
            CoreAiTemplateFieldDTO coreAiTemplateKeyValueDTO = new CoreAiTemplateFieldDTO();
            coreAiTemplateKeyValueDTO.setTitle(k);
            coreAiTemplateKeyValueDTO.setValue(v.toString());
            coreAiTemplateKeyValueDTO.setField("field" + (coreAiTemplateKeyValueDTOList.size() + 1));
            coreAiTemplateKeyValueDTOList.add(coreAiTemplateKeyValueDTO);
         });
         coreAiTemplateResultDTO.setResult(coreAiTemplateKeyValueDTOList);
         return coreAiTemplateResultDTO;
      } catch (Exception var21) {
         log.error(var25);
         throw new UnexpectedException("CORE.MODULE.AI.TEMPLATES.TIP.FAIL_TO_SCAN_PLEASE_CALL_ADMINISTRATOR");
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   public void save(Long id, RestJsonWrapperBean wrapper) {
      CoreAiTemplateBean coreAiTemplate = wrapper.parseUnique(CoreAiTemplateBean.class);
      CoreAiTemplateBean coreAiTemplateCache = RedisHelper.get("OCR_PIC_CHECHE", String.valueOf(id));
      if (!BeanUtils.isEmpty(coreAiTemplateCache)) {
         coreAiTemplate.setPicPath(coreAiTemplateCache.getPicPath());
         coreAiTemplate.setWidth(coreAiTemplateCache.getWidth());
         coreAiTemplate.setHeight(coreAiTemplateCache.getHeight());
         coreAiTemplate.setFileId(coreAiTemplateCache.getFileId());
         RedisHelper.evict("OCR_PIC_CHECHE", String.valueOf(id));
      }

      coreAiTemplate.setModifyTime(LocalDateTime.now());
      this.getDao().update(coreAiTemplate, new String[0]);
   }

   @Override
   public File downloadPicFile(CoreAiTemplateBean coreAiTemplate) {
      CoreFileBean coreFile = this.coreFileService.selectById(coreAiTemplate.getFileId());
      File tempFile = FileUtils.createTempFile(coreFile.getFileExt());
      this.coreFileManager.download(coreFile, tempFile.toPath());
      return tempFile;
   }

   private String identifyFromTemplate(String templateImg, String img, String info) {
      MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
      if (!StringUtils.isEmpty(info)) {
         JSONObject jsonObject = new JSONObject();
         JSONObject infoObject = JSONObject.parseObject(info);
         JSONArray fixFieldsArray = infoObject.getJSONArray("fixFields");
         fixFieldsArray.forEach(ex -> {
            ((JSONObject)ex).remove("id");
            ((JSONObject)ex).remove("fieldResult");
         });
         JSONArray benchmarksArray = infoObject.getJSONArray("benchmarks");
         benchmarksArray.forEach(ex -> {
            ((JSONObject)ex).remove("id");
            ((JSONObject)ex).remove("error");
         });
         jsonObject.put("fixFields", fixFieldsArray);
         jsonObject.put("benchmarks", benchmarksArray);
         if (!StringUtils.isEmpty(templateImg)) {
            jsonObject.put("template_img", templateImg);
         }

         requestMap.add("formData", jsonObject);
      }

      HttpHeaders httpHeaders = new HttpHeaders();
      httpHeaders.setContentType(MediaType.APPLICATION_JSON);
      requestMap.add("images", img);
      requestMap.add("type", "form");
      HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(requestMap, httpHeaders);
      RestTemplate restTemplate = new RestTemplate();

      try {
         ResponseEntity<String> response = restTemplate.postForEntity(this.ocrServer, httpEntity, String.class);
         return response.getBody();
      } catch (Exception var9) {
         throw new NetworkException(var9);
      }
   }

   private void setTemplatePicPath(CoreAiTemplateBean coreAiTemplate) {
      Long fileId = coreAiTemplate.getFileId();
      CoreFileBean coreFile = this.coreFileService.selectById(fileId);
      coreAiTemplate.setPicName(coreFile.getName());
      String downloadUrl = this.coreFileManager.getDownloadUrl(coreFile);
      coreAiTemplate.setPicPath(downloadUrl);
   }

   private CoreFileBean uoloadFile(File file) {
      CoreFileBean coreFile = new CoreFileBean();
      coreFile.setTargetId("template$ocr");
      coreFile.setName(FileUtils.getFileBaseName(file));
      coreFile.setFileExt(FileUtils.getFileExtension(file));
      coreFile.setMd5Name(EncryptUtils.MD5Encrypt(coreFile.getTargetId() + "$" + coreFile.getName()));
      coreFile.setScope(FileScope.secure.name());
      coreFile.setSize(file.length());
      coreFile.setTimes(0L);
      coreFile.setDownloadable("1");
      coreFile.setCreatedTime(LocalDateTime.now());
      coreFile.setBizCategory("");
      Long fileId = this.coreFileService.upload(coreFile, file);
      coreFile.setId(fileId);
      return coreFile;
   }
}
