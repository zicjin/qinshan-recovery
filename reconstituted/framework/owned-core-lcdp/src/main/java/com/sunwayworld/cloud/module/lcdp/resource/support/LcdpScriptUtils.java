package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.cloud.module.lcdp.base.service.LcdpBaseService;
import com.sunwayworld.cloud.module.lcdp.configparam.service.LcdpConfigParamService;
import com.sunwayworld.cloud.module.lcdp.log.ScriptLogWebSocket;
import com.sunwayworld.cloud.module.lcdp.log.interceptor.ScriptLogInterceptor;
import com.sunwayworld.cloud.module.lcdp.resource.LcdpResourceCache;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resource.service.impl.LcdpResourceServiceImpl;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.exception.PasswordException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.jdk.tools.DynamicClassLoader;
import com.sunwayworld.framework.jdk.tools.JavaCompilerManager;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.script.exeception.BshException;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ExceptionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.exception.BpmnException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Blob;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LcdpScriptUtils {
   private static final Logger logger = LoggerFactory.getLogger(LcdpScriptUtils.class);

   public static String callScriptMethod(String scriptPath, Object... args) {
      String path = LcdpScriptPath.methodPath(scriptPath);
      String methodName = LcdpScriptPath.methodName(scriptPath);
      return isDebugCall() ? devCallScriptMethod(path, methodName, args) : proCallScriptMethod(path, methodName, args);
   }

   public static String proCallScriptMethod(String path, String methodName, Object[] args) {
      LcdpResourceBean memoryResource = LcdpResourceCache.getMemoryContent(path);
      if (memoryResource == null) {
         LcdpResourceService resourceService = ApplicationContextHelper.getBean(LcdpResourceService.class);
         LcdpResourceBean resource = resourceService.selectFirstByFilter(
            SearchFilter.instance().match("PATH", path).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
         );
         if (resource == null) {
            return null;
         }

         LcdpResourceCache.put(path, resource);
      }

      try {
         if (LcdpBaseService.class.isAssignableFrom(SpringUtils.getBean(LcdpResourceCache.getMemoryContent(path).getClassName().toLowerCase()).getClass())) {
            LcdpBaseService scriptService = SpringUtils.getBean(LcdpResourceCache.getMemoryContent(path).getClassName().toLowerCase());
            String tableName = scriptService.getTable();
            RedisHelper.put("SCRIPT_PATH_TABLE_MAPPING_CACHE", path, tableName);
            RedisHelper.put("SCRIPT_TABLE_PATH_MAPPING_CACHE", tableName, path);
         }

         Object result = SpringUtils.invoke(LcdpResourceCache.getMemoryContent(path).getClassName().toLowerCase(), methodName, args);
         return objectToJSONString(result);
      } catch (Exception var8) {
         Throwable cause = var8;

         for (InvocationTargetException invocationTargetException = ExceptionUtils.getCause(var8, InvocationTargetException.class);
            !ObjectUtils.isEmpty(invocationTargetException);
            invocationTargetException = ExceptionUtils.getCause(cause, InvocationTargetException.class)
         ) {
            cause = invocationTargetException.getTargetException();
         }

         validateTipException(cause);
         String exceptionLog = I18nHelper.getMessage("LCDP.RUN_LOGS.SCRIPT_INVOKE_ERROR", path, methodName, ObjectUtils.getStackTrace(cause));
         throw new RuntimeException("Script execution failed: " + path + "." + methodName, cause);
      }
   }

   public static String devCallScriptMethod(String path, String methodName, Object[] args) {
      String script = ApplicationContextHelper.getBean(LcdpResourceServiceImpl.class).getScriptByPath(path, "dev");
      ScriptLogWebSocket scriptLogWebSocket = ApplicationContextHelper.getBean(ScriptLogWebSocket.class);

      String var20;
      try {
         scriptLogWebSocket.pushInfoLog("脚本调用 ==>" + path + "." + methodName);
         ScriptLogWebSocket.setContextScriptDatasourceName(path + "@" + methodName);
         ScriptLogInterceptor.enable();
         String className = JavaCompilerManager.getClassName(script);
         synchronized (className.intern()) {
            if (!ApplicationContextHelper.getApplicationContext().containsBean(className.toLowerCase())) {
               Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
               SpringUtils.registerBean(className.toLowerCase(), clazz);
               if (LcdpBaseService.class.isAssignableFrom(SpringUtils.getBean(className.toLowerCase()).getClass())) {
                  LcdpBaseService scriptService = SpringUtils.getBean(className.toLowerCase());
                  String tableName = scriptService.getTable();
                  RedisHelper.put("TABLE_SCRIPT_MAPPING_CACHE", tableName, className.toLowerCase());
                  RedisHelper.put("SCRIPT_PATH_TABLE_MAPPING_CACHE", path, tableName);
                  RedisHelper.put("SCRIPT_TABLE_PATH_MAPPING_CACHE", tableName, path);
               }
            }
         }

         Object result = SpringUtils.invoke(className.toLowerCase(), methodName, args);
         var20 = objectToJSONString(result);
      } catch (Throwable var16) {
         Throwable cause = var16;

         for (InvocationTargetException invocationTargetException = ExceptionUtils.getCause(var16, InvocationTargetException.class);
            !ObjectUtils.isEmpty(invocationTargetException);
            invocationTargetException = ExceptionUtils.getCause(cause, InvocationTargetException.class)
         ) {
            cause = invocationTargetException.getTargetException();
         }

         validateTipException(cause);
         String exceptionLog = I18nHelper.getMessage("LCDP.RUN_LOGS.SCRIPT_INVOKE_ERROR", path, methodName, ObjectUtils.getStackTrace(cause));
         scriptLogWebSocket.pushErrorLog(exceptionLog);
         throw new UnexpectedException(exceptionLog);
      } finally {
         ScriptLogInterceptor.disable();
      }

      return var20;
   }

   public static Object call(String beanName, String methodName, Object... args) throws Throwable {
      if (isDebugCall()) {
         String script = ApplicationContextHelper.getBean(LcdpResourceService.class).getDevResourceContent(beanName);
         Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
         SpringUtils.registerBean(beanName, clazz);
         return SpringUtils.invoke(beanName, methodName, args);
      } else {
         String className = ApplicationContextHelper.getBean(LcdpResourceService.class).getResourceClassName(beanName);
         return SpringUtils.invoke(className.toLowerCase(), methodName, args);
      }
   }

   public static Object getBean(String beanName) {
      String className = null;
      if (isDebugCall()) {
         String script = ApplicationContextHelper.getBean(LcdpResourceService.class).getDevResourceContent(beanName);
         className = JavaCompilerManager.getClassName(script);
         Class<?> clazz = DynamicClassLoader.getInstance().loadSourceCode(script);
         SpringUtils.registerBean(className.toLowerCase(), clazz);
      } else {
         className = ApplicationContextHelper.getBean(LcdpResourceService.class).getResourceClassName(beanName);
      }

      return SpringUtils.getBean(className.toLowerCase());
   }

   public static String getUtilsClassName(String utilsName) {
      String className = null;
      if (isDebugCall()) {
         String script = ApplicationContextHelper.getBean(LcdpResourceService.class).getDevResourceContent(utilsName);
         className = JavaCompilerManager.getClassName(script);
      } else {
         className = ApplicationContextHelper.getBean(LcdpResourceService.class).getResourceClassName(utilsName);
      }

      return className;
   }

   public static boolean validateCurrentDBMybatisMapper() {
      String environmentGlobConfig = ApplicationContextHelper.getBean(LcdpConfigParamService.class).getCurrentDBMybatisMapperParam();
      return !StringUtils.isEmpty(environmentGlobConfig) && StringUtils.equals(environmentGlobConfig, "1");
   }

   public static boolean isDebugCall() {
      HttpServletRequest request = ServletUtils.getCurrentRequest();
      return request == null ? false : "development".equals(request.getHeader("lcdp-env"));
   }

   private static String objectToJSONString(Object result) {
      if (result == null) {
         return "";
      } else {
         filterBlobField(result);
         Object resultJsonObject = JSON.parse(JSONObject.toJSONStringWithDateFormat(result, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]));
         if (resultJsonObject instanceof JSONObject) {
            return JSONObject.toJSONString(keyToLowerCase((JSONObject)JSONObject.toJSON(resultJsonObject)));
         } else {
            return resultJsonObject instanceof JSONArray
               ? JSONObject.toJSONString(keyToLowerCase((JSONArray)JSONObject.toJSON(resultJsonObject)))
               : result.toString();
         }
      }
   }

   private static void filterBlobField(Object javaObject) {
      if (javaObject instanceof Map) {
         Map<Object, Object> map = (Map<Object, Object>)javaObject;

         for (Entry<Object, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Blob) {
               entry.setValue(null);
            } else if (entry.getValue() instanceof byte[]) {
               String text = ConvertUtils.convert(entry.getValue(), String.class);
               entry.setValue(text);
            }
         }
      } else if (!(javaObject instanceof Collection)) {
         if (javaObject instanceof Page) {
            Page<?> page = (Page<?>)javaObject;
            filterBlobField(page.getRows());
         }
      } else {
         for (Object item : (Collection)javaObject) {
            filterBlobField(item);
         }
      }
   }

   private static JSONObject keyToLowerCase(JSONObject o1) {
      JSONObject o2 = new JSONObject();

      for (String key : o1.keySet()) {
         Object object = o1.get(key);
         if (object != null) {
            if (object.getClass().toString().endsWith("JSONObject")) {
               o2.put(key.toLowerCase(), keyToLowerCase((JSONObject)object));
            } else if (object.getClass().toString().endsWith("JSONArray")) {
               JSONArray arr = (JSONArray)object;
               o2.put(key.toLowerCase(), keyToLowerCase(arr));
            } else {
               if (key.equalsIgnoreCase("id") && object.toString().length() > 15) {
                  object = object.toString();
               }

               o2.put(key.toLowerCase(), object);
            }
         }
      }

      return o2;
   }

   private static JSONArray keyToLowerCase(JSONArray o1) {
      JSONArray o2 = new JSONArray();

      for (int i = 0; i < o1.size(); i++) {
         if (String.class.isAssignableFrom(o1.get(i).getClass())) {
            o2.add(o1.get(i));
         } else {
            Object jArray = o1.get(i);
            if (jArray.getClass().toString().endsWith("JSONObject")) {
               o2.add(keyToLowerCase((JSONObject)jArray));
            } else if (jArray.getClass().toString().endsWith("JSONArray")) {
               o2 = keyToLowerCase((JSONArray)jArray);
            }
         }
      }

      return o2;
   }

   private static void validateTipException(Throwable cause) {
      if (cause instanceof InvalidDataException || cause instanceof CheckedException || cause instanceof ApplicationWarningException) {
         throw new CheckedException(cause.getMessage(), cause);
      } else if (cause instanceof BpmnException) {
         throw new BpmnException(cause.getMessage());
      } else if (cause instanceof BshException) {
         throw new BshException(cause.getMessage());
      } else if (cause instanceof PasswordException) {
         throw new PasswordException(cause.getMessage());
      }
   }
}
