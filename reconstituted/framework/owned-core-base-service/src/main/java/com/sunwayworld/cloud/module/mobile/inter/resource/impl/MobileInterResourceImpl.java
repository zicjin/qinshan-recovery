package com.sunwayworld.cloud.module.mobile.inter.resource.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.cloud.module.mobile.inter.resource.MobileInterResource;
import com.sunwayworld.cloud.module.mobile.push.service.MobilePushMessageService;
import com.sunwayworld.cloud.module.mobile.push.service.MobilePushUserService;
import com.sunwayworld.cloud.module.mobile.utils.HttpUtils;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@GikamBean
@LogModule("移动端中转接口")
@RestController
@RequestMapping({"/open/core/module/sys/mobile/inters"})
public class MobileInterResourceImpl implements MobileInterResource {
   @Autowired
   private MobilePushUserService mobilePushUserService;
   @Autowired
   private MobilePushMessageService mobilePushMessageService;
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private CoreFileService fileService;

   @Override
   public String request(String method, Map<String, Object> body) {
      String url = (String)body.get("url");
      if (!url.contains("http") && !url.contains("https")) {
         url = "http://58.56.21.146:1148" + body.get("url");
      }

      String params = (String)body.getOrDefault("params", "");
      String cookie = "";

      try {
         cookie = (String)body.getOrDefault("cookie", "");
      } catch (Exception var15) {
      }

      String securityRsaKey = "";

      try {
         securityRsaKey = (String)body.getOrDefault("securityRsaKey", "");
      } catch (Exception var14) {
      }

      String sunwayMybatisMappedStatementId = "";

      try {
         sunwayMybatisMappedStatementId = (String)body.getOrDefault("sunwayMybatisMappedStatementId", "");
      } catch (Exception var13) {
      }

      Map<String, String> headers = new HashMap<>();
      Map<String, Object> map = new HashMap<>();
      String result = "";
      switch (method) {
         case "get":
            if (!StringUtils.isEmpty(params)) {
               map = JSONObject.parseObject(params, new TypeReference<Map<String, Object>>() {}, new Feature[0]);
            }

            if (url.endsWith("/open/security/public-key")) {
               result = HttpUtils.get(url);
            } else if (url.endsWith("/core/module/sys/logins/account")) {
               headers.put("SUNWAY-MYBATIS-MAPPED-STATEMENT-ID", sunwayMybatisMappedStatementId);
               headers.put("Cookie", cookie);
               result = HttpUtils.get(url, headers);
            } else {
               result = HttpUtils.get(url, map, cookie);
            }
            break;
         case "post":
            if (!url.endsWith("/core/module/sys/login") && !url.endsWith("/secure/core/module/sys/mobile/push_users/insert")) {
               HttpUtils.setContentType("application/json");
               result = HttpUtils.post(url, params, cookie);
            } else {
               if (!StringUtils.isEmpty(params)) {
                  map = JSONObject.parseObject(params, new TypeReference<Map<String, Object>>() {}, new Feature[0]);
               }

               if (url.endsWith("/core/module/sys/login")) {
                  headers.put("SECURITY-RSA-KEY", securityRsaKey);
                  result = HttpUtils.post(url, map, headers);
               } else if (url.endsWith("/secure/core/module/sys/mobile/push_users/insert")) {
                  this.mobilePushUserService.insertPushUser((String)map.get("userId"), (String)map.get("clientId"));
                  result = HttpUtils.post(url, map, cookie);
               }
            }
            break;
         case "put":
            HttpUtils.setContentType("application/json");
            result = HttpUtils.put(url, params, cookie);
            break;
         case "delete":
            HttpUtils.setContentType("application/json");
            result = HttpUtils.delete(url, params, cookie);
      }

      return result;
   }

   @Override
   public CoreFileBean downloadFile(Map<String, Object> body) {
      String url = (String)body.get("url");
      if (!url.contains("http") && !url.contains("https")) {
         url = "http://58.56.21.146:1148" + body.get("url");
      }

      String params = (String)body.getOrDefault("params", "");
      String cookie = "";

      try {
         cookie = (String)body.getOrDefault("cookie", "");
      } catch (Exception var8) {
      }

      if (!StringUtils.isEmpty(params)) {
         CoreFileBean coreFile = JsonUtils.parse(params, CoreFileBean.class);
         InputStream is = HttpUtils.download(url, "", cookie);
         FilePathDTO filePath = CoreFileUtils.toFilePath(coreFile);
         this.fileManager.upload(filePath, is);
         coreFile.setDownloadUrl(this.fileManager.getDownloadUrl(filePath));
         return coreFile;
      } else {
         return null;
      }
   }

   @Override
   public String uploadFile(Map<String, Object> body, MultipartFile uploadFile) {
      String url = (String)body.get("url");
      if (!url.contains("http") && !url.contains("https")) {
         url = "http://58.56.21.146:1148" + body.get("url");
         body.remove("url");
      }

      String params = (String)body.getOrDefault("params", "");
      new HashMap();
      if (!StringUtils.isEmpty(params) && uploadFile != null) {
         Map<String, Object> map = JSONObject.parseObject(params, new TypeReference<Map<String, Object>>() {}, new Feature[0]);
         String fileName = uploadFile.getOriginalFilename();
         CoreFileBean file = new CoreFileBean();
         BeanUtils.copyProperties(map, file);
         file.setName(fileName);
         this.fileService.upload(file, uploadFile);
         return this.fileManager.getDownloadUrl(file);
      } else {
         return null;
      }
   }

   @Override
   public void pushMessage(Map<String, String> body) {
      String userId = body.getOrDefault("userId", "");
      String title = body.getOrDefault("title", "");
      String content = body.getOrDefault("content", "");
      String data = body.getOrDefault("data", "");
      this.mobilePushMessageService.insertPushMessage(userId, title, content, data);
   }
}
