package com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.bean.LcdpCheckoutConfigBean;
import com.sunwayworld.cloud.module.lcdp.checkoutconfig.service.LcdpCheckoutConfigService;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckOutDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutDetailDTO;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCheckoutRecordBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.bean.LcdpCrdLogBean;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.persistent.dao.LcdpCheckoutRecordDao;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCheckoutRecordService;
import com.sunwayworld.cloud.module.lcdp.checkoutrecord.service.LcdpCrdLogService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.instance.EntityHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpCheckoutRecordServiceImpl implements LcdpCheckoutRecordService {
   @Autowired
   private LcdpCheckoutRecordDao lcdpCheckoutRecordDao;
   @Autowired
   private LcdpCrdLogService lcdpCrdLogService;
   @Autowired
   @Lazy
   private LcdpResourceService resourceService;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   private LcdpCheckoutConfigService checkoutConfigService;
   @Autowired
   private CoreFileManager fileManager;
   private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

   public LcdpCheckoutRecordDao getDao() {
      return this.lcdpCheckoutRecordDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckoutRecordBean checkoutRecord = jsonWrapper.parseUnique(LcdpCheckoutRecordBean.class);
      checkoutRecord.setId(ApplicationContextHelper.getNextIdentity());
      checkoutRecord.setCheckoutNo(ApplicationContextHelper.getNextSequence("T_LCDP_CHECKOUT_RECORD"));
      this.getDao().insert(checkoutRecord);
      this.resourceService.export(jsonWrapper, checkoutRecord);
      return checkoutRecord.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public LcdpCheckOutDTO confirm(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckOutDTO checkOutDTO = new LcdpCheckOutDTO();
      LcdpCheckoutRecordBean checkoutRecord = jsonWrapper.parseUnique(LcdpCheckoutRecordBean.class);
      checkoutRecord.setId(ApplicationContextHelper.getNextIdentity());
      checkoutRecord.setCheckoutNo(ApplicationContextHelper.getNextSequence("T_LCDP_CHECKOUT_RECORD"));
      EntityHelper.assignCreatedElement(checkoutRecord);
      String downLoadUrl = this.resourceService.export(jsonWrapper, checkoutRecord);
      List<LcdpCrdLogBean> crdLogList = jsonWrapper.parse(LcdpCrdLogBean.class);
      if (crdLogList.isEmpty()) {
         return null;
      } else {
         crdLogList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setCheckoutRecordId(checkoutRecord.getId());
         });
         List<LcdpCrdLogBean> checkoutList = crdLogList.stream()
            .filter(log -> StringUtils.equals("checkout", log.getCheckoutType()))
            .collect(Collectors.toList());
         if (!checkoutList.isEmpty()) {
            this.doCheckout(checkoutList, checkOutDTO, checkoutRecord);
         }

         LcdpCrdLogBean export = crdLogList.stream().filter(log -> StringUtils.equals("export", log.getCheckoutType())).findFirst().orElse(null);
         if (null != export) {
            crdLogList.get(0).setCheckoutStatus("1");
            checkOutDTO.setDownLoadUrl(downLoadUrl);
         }

         this.getDao().insert(checkoutRecord);
         this.lcdpCrdLogService.getDao().insert(crdLogList);
         return checkOutDTO;
      }
   }

   @Override
   public LcdpCheckOutDTO export(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckoutRecordBean checkoutRecord = jsonWrapper.parseUnique(LcdpCheckoutRecordBean.class);
      List<CoreFileBean> coreFileList = this.coreFileService.selectFileList(this.getDao().getTable(), checkoutRecord.getId());
      CoreFileBean coreFile = coreFileList.stream().findFirst().orElse(null);
      LcdpCheckOutDTO checkOutDTO = new LcdpCheckOutDTO();
      checkOutDTO.setDownLoadUrl(coreFile == null ? null : this.fileManager.getAbsoluteDownloadUrl(coreFile));
      LcdpCrdLogBean crdLogBean = new LcdpCrdLogBean();
      crdLogBean.setId(ApplicationContextHelper.getNextIdentity());
      crdLogBean.setCheckoutRecordId(checkoutRecord.getId());
      crdLogBean.setCheckoutType("export");
      crdLogBean.setCheckoutStatus("1");
      this.lcdpCrdLogService.getDao().insert(crdLogBean);
      return checkOutDTO;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public LcdpCheckOutDTO checkout(RestJsonWrapperBean jsonWrapper) {
      LcdpCheckoutRecordBean checkoutRecord = jsonWrapper.parseUnique(LcdpCheckoutRecordBean.class);
      List<LcdpCrdLogBean> crdLogList = jsonWrapper.parse(LcdpCrdLogBean.class);
      LcdpCheckOutDTO checkOutDTO = new LcdpCheckOutDTO();
      crdLogList.forEach(log -> {
         log.setId(ApplicationContextHelper.getNextIdentity());
         log.setCheckoutRecordId(checkoutRecord.getId());
      });
      this.doCheckout(crdLogList, checkOutDTO, checkoutRecord);
      this.lcdpCrdLogService.getDao().insert(crdLogList);
      return checkOutDTO;
   }

   @Override
   public Page<LcdpCrdLogBean> selectLogPaginationByCheckoutRecordId(Long id, RestJsonWrapperBean wrapper) {
      return this.lcdpCrdLogService.selectPaginationByFilter(SearchFilter.instance().match("CHECKOUTRECORDID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public void doCheckout(List<LcdpCrdLogBean> checkoutList, LcdpCheckOutDTO checkOutDTO, LcdpCheckoutRecordBean checkoutRecord) {
      List<CoreFileBean> coreFileList = this.coreFileService.selectFileList(this.getDao().getTable(), checkoutRecord.getId());
      CoreFileBean coreFile = coreFileList.stream().findFirst().orElse(null);
      Path filePath = CoreFileUtils.getLocalPath(coreFile);
      File file = filePath.toFile();
      List<LcdpCheckoutConfigBean> checkoutConfigList = this.checkoutConfigService
         .selectListByFilter(SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.EQ), new Order[0]);
      Map<Long, LcdpCheckoutConfigBean> checkoutConfigMap = checkoutConfigList.stream()
         .collect(Collectors.toMap(LcdpCheckoutConfigBean::getId, Function.identity()));
      List<LcdpCheckoutDetailDTO> detailList = new ArrayList<>();
      checkoutList.forEach(log -> {
         LcdpCheckoutDetailDTO checkoutDetailDTO = new LcdpCheckoutDetailDTO();
         String urlSuffix = "/core/module/lcdp/receive/checkin-records/action/receive";
         LcdpCheckoutConfigBean checkoutConfig = checkoutConfigMap.get(log.getCheckoutConfigId());
         String systemUrl = checkoutConfig.getSystemUrl();
         if (systemUrl.endsWith("/")) {
            systemUrl = systemUrl.substring(0, systemUrl.length() - 1);
         }

         String sendUrL = systemUrl + urlSuffix;
         Map<String, Object> responseMap = callSendFile(file, sendUrL, checkoutRecord, checkoutConfig);
         Object code = responseMap.get("code");
         Integer statusCode = ObjectUtils.isEmpty(code) ? null : Integer.valueOf(code.toString());
         checkoutDetailDTO.setSystemName(checkoutConfig.getSystemName());
         checkoutDetailDTO.setCheckoutStatus("1");
         log.setCheckoutStatus("1");
         if (statusCode == null || statusCode != 200) {
            checkoutDetailDTO.setCheckoutStatus("0");
            checkoutDetailDTO.setMessage(ObjectUtils.isEmpty(responseMap.get("result")) ? null : responseMap.get("result").toString());
            log.setCheckoutStatus("0");
         }

         detailList.add(checkoutDetailDTO);
      });
      checkOutDTO.setCheckoutDetailList(detailList);
   }

   public static Map<String, Object> callSendFile(File file, String sendUrL, LcdpCheckoutRecordBean checkoutRecord, LcdpCheckoutConfigBean checkoutConfig) {
      CloseableHttpClient httpclient = HttpClients.createDefault();
      Map<String, Object> responseMap = new HashMap<>();
      String result = null;

      try {
         try {
            HttpPost httpPost = new HttpPost(sendUrL);
            RequestConfig requestConfig = RequestConfig.custom()
               .setConnectTimeout(ApplicationContextHelper.getEnvironment().getProperty("sunway.http-client.connect-timeout", Integer.class, 3000))
               .build();
            httpPost.setConfig(requestConfig);
            Environment environment = ApplicationContextHelper.getEnvironment();
            StringBuilder serverAddress = new StringBuilder();
            StringBuffer requestURL = ServletUtils.getCurrentRequest().getRequestURL();
            Pattern addressHavePortPattern = Pattern.compile("//([0-9]{1,3}\\.){3}[0-9]{1,3}:([0-9]{1,5})");
            Matcher havePortMatcher = addressHavePortPattern.matcher(requestURL);
            Pattern addressNoPortPattern = Pattern.compile("//([0-9]{1,3}\\.){3}[0-9]{1,3}");
            Matcher noPortMatcher = addressNoPortPattern.matcher(requestURL);
            if (havePortMatcher.find()) {
               serverAddress.append(havePortMatcher.group());
            } else {
               if (!noPortMatcher.find()) {
                  responseMap.put("code", "500");
                  responseMap.put("result", I18nHelper.getMessage("GIKAM.EXCEPTION.AUTH_FAILD"));
                  return responseMap;
               }

               serverAddress.append(noPortMatcher.group());
            }

            serverAddress.append(environment.getProperty("server.servlet.context-path", ""));
            Header[] headers = new Header[]{
               new BasicHeader("license", checkoutConfig.getLicense()), new BasicHeader("serverAddress", serverAddress.toString())
            };
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpPost.setHeader("Accept-Encoding", "gzip, deflate");
            httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
            httpPost.setHeader("Connection", "keep-alive");
            httpPost.setHeader(
               "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36"
            );
            httpPost.setHeaders(headers);
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            ContentType infoCType = ContentType.create("application/json", CHARSET_UTF8);
            ContentType fileCType = ContentType.create("application/octet-stream", CHARSET_UTF8);
            entityBuilder.addPart("file", new FileBody(file, fileCType));
            entityBuilder.addTextBody("checkoutRecord", JSONObject.toJSONString(checkoutRecord), infoCType);
            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);
            HttpResponse response = httpclient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
               result = EntityUtils.toString(responseEntity, CHARSET_UTF8);
            }

            responseMap.put("code", response.getStatusLine().getStatusCode());
            responseMap.put("result", result);
         } catch (IOException var34) {
            responseMap.put("code", 500);
            responseMap.put("result", var34.getMessage());
            var34.printStackTrace();
         } catch (Exception var35) {
            responseMap.put("code", 500);
            responseMap.put("result", var35.getMessage());
            var35.printStackTrace();
         }

         return responseMap;
      } finally {
         try {
            httpclient.close();
         } catch (Exception var33) {
            var33.printStackTrace();
         }
      }
   }
}
