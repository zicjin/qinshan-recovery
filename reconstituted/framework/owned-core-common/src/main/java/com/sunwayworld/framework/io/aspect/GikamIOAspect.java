package com.sunwayworld.framework.io.aspect;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.extractor.DownloadParamExtractor;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.support.base.service.GenericDownloadableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.matrix.resource.GenericMatrixResource;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.item.notification.helper.CoreNotificationHelper;
import com.sunwayworld.module.sys.menu.resource.impl.CoreMenuRoleResourceImpl;
import java.io.File;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class GikamIOAspect {
   private static final Logger logger = LogManager.getLogger(GikamIOAspect.class);
   @Lazy
   @Autowired
   private CoreFileManager fileManager;

   @Around("within(com.sunwayworld..resource..*)")
   public Object setupDownloadUrlIfNecessary(ProceedingJoinPoint pjp) throws Throwable {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)Arrays.stream(pjp.getArgs())
         .filter(a -> a != null && RestJsonWrapperBean.class.isAssignableFrom(a.getClass()))
         .findFirst()
         .orElse(null);
      Object object = pjp.proceed();
      if (wrapper == null) {
         return object;
      } else {
         Signature signature = pjp.getSignature();
         MethodSignature methodSignature = (MethodSignature)signature;
         Method method = methodSignature.getMethod();
         if (signature instanceof MethodSignature) {
            boolean scriptFlag = this.isScriptPage(method, object);
            if (!Page.class.isAssignableFrom(method.getReturnType()) && !scriptFlag) {
               return object;
            }
         }

         HttpServletRequest request = ServletUtils.getCurrentRequest();
         boolean highPerformanceDownloadRequest = request != null && !StringUtils.isEmpty(request.getHeader("GIKAM-EXCEL-HIGH-PERFORMANCE"));
         String requestMode = request == null ? "" : request.getHeader("GIKAM-REQUEST-MODE");
         if (highPerformanceDownloadRequest) {
            Class<? extends Object> resourceClass = (Class<? extends Object>)ClassUtils.getRawType(pjp.getTarget().getClass());
            String methodName = GenericMatrixResource.class.isAssignableFrom(resourceClass) ? "getMatrixService" : "getService";
            Object service = ReflectionUtils.invokeMethod(ReflectionUtils.getUniqueMethodByName(resourceClass, methodName), pjp.getThis());
            GenericService<?, ?> genericService = (GenericService<?, ?>)service;
            GenericDownloadableService downloadableService = (GenericDownloadableService)service;
            if ("async".equals(requestMode)) {
               Consumer<ResultSet> consumer = r -> downloadableService.generateExportUrlAndNotification(
                  wrapper, genericService.getDao().getType(), r, LocalContextHelper.getLoginUserId(), requestMode
               );
               LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
               loginUser.setLoginToken(AuthenticationHelper.getLoginUserToken());
               ExecutorHelper.submit(() -> {
                  try {
                     LocalContextHelper.setUserLogin(loginUser);
                     MybatisPageHelper.setHighPerformanceDownloadConsumer(consumer);
                     pjp.proceed();
                  } catch (Throwable var10x) {
                     DownloadParamExtractor paramExtractor = new DownloadParamExtractor(wrapper);
                     this.sendErrorNotification(loginUser.getUsername(), paramExtractor.extract().getTopic(), var10x);
                     logger.error(var10x.getMessage(), var10x);
                  } finally {
                     LocalContextHelper.removeUserLogin();
                     MybatisPageHelper.clear();
                  }
               });
            } else {
               Consumer<ResultSet> consumer = r -> downloadableService.generateExcelDTO(
                  wrapper, genericService.getDao().getType(), r, LocalContextHelper.getLoginUserId(), requestMode
               );

               try {
                  MybatisPageHelper.setHighPerformanceDownloadConsumer(consumer);
                  pjp.proceed();
               } finally {
                  MybatisPageHelper.clear();
               }
            }

            return new Pagination();
         } else if (object == null) {
            return null;
         } else {
            boolean downloadRequest = request != null && !StringUtils.isEmpty(request.getHeader("GIKAM-EXCEL"));
            if (downloadRequest && wrapper != null) {
               Page<?> page;
               if ("callScript".equals(method.getName())) {
                  Map<String, Object> map = JSONObject.parseObject(String.valueOf(object), Map.class);
                  List<Map<String, Object>> originRows = (List<Map<String, Object>>)map.get("rows");
                  List<TableRowBean> rows = originRows.stream().map(originRow -> {
                     TableRowBean nextRow = new TableRowBean();
                     Object ext$ = originRow.remove("ext$");
                     nextRow.putAll((Map<? extends String, ? extends Object>)originRow);
                     if (ext$ != null) {
                        nextRow.setExt$((Map<String, Object>)ext$);
                     }

                     return nextRow;
                  }).collect(Collectors.toList());
                  page = new Pagination<>(rows);
               } else {
                  page = (Page<?>)object;
               }

               Class<? extends Object> resourceClass = (Class<? extends Object>)ClassUtils.getRawType(pjp.getTarget().getClass());
               String methodName = "getService";
               if (resourceClass.equals(CoreMenuRoleResourceImpl.class)) {
                  methodName = "getMatrixService";
               }

               GenericDownloadableService service = (GenericDownloadableService)ReflectionUtils.invokeMethod(
                  ReflectionUtils.getUniqueMethodByName(resourceClass, methodName), pjp.getThis()
               );
               if ("async".equals(requestMode)) {
                  LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
                  loginUser.setLoginToken(AuthenticationHelper.getLoginUserToken());
                  ExecutorHelper.submit(() -> {
                     try {
                        LocalContextHelper.setUserLogin(loginUser);
                        service.generateExportUrlAndNotification(wrapper, page, LocalContextHelper.getLoginUserId(), requestMode);
                     } catch (Throwable var11x) {
                        DownloadParamExtractor paramExtractor = new DownloadParamExtractor(wrapper);
                        this.sendErrorNotification(loginUser.getUsername(), paramExtractor.extract().getTopic(), var11x);
                        logger.error(var11x.getMessage(), var11x);
                     } finally {
                        LocalContextHelper.removeUserLogin();
                     }
                  });
               } else {
                  service.generateExcelDTO(wrapper, page, LocalContextHelper.getLoginUserId(), requestMode);
               }

               return "callScript".equals(method.getName()) ? "" : new Pagination();
            } else {
               return object;
            }
         }
      }
   }

   private boolean isScriptPage(Method method, Object object) {
      if ("callScript".equals(method.getName())) {
         if (object == null || !JsonUtils.isObject(String.valueOf(object))) {
            return false;
         }

         Map<String, Object> map = JSONObject.parseObject(String.valueOf(object), Map.class);
         if (map.containsKey("total") && map.containsKey("rows")) {
            return true;
         }
      }

      return false;
   }

   private void sendErrorNotification(String exportUserId, String topic, Throwable t) {
      String fileName = topic + "【ERROR】.txt";
      FilePathDTO filePath = CoreFileUtils.toFilePath(FileScope.temp, fileName);
      File exportFile = null;

      try {
         exportFile = FileUtils.createTempFile();
         FileUtils.write(exportFile, ObjectUtils.getStackTrace(t));
         this.fileManager.upload(filePath, exportFile);
      } finally {
         FileUtils.deleteQuietly(exportFile);
      }

      Pair filePair = Pair.of(fileName, this.fileManager.getDownloadUrl(filePath));
      CoreNotificationHelper.sendNotification("EXPORT_EXCEL_ERROR", ArrayUtils.asList(exportUserId), null, ArrayUtils.asList(filePair));
   }
}
