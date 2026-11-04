package com.sunwayworld.cloud.module.sys.monitor.errorlog.aspect;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.service.CoreSystemMonitorErrorLogService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CoreSystemMonitorErrorLoggingAspect {
   public static final String UNKNOWN_MODULE_NAME = "UNKNOWN";
   public static final String UNKNOWN_LOG_NAME = "UNKNOWN";
   public static final String ERROR_LOG_STRING_EXCEPTION = "[EXCEPTION]";
   public static final String ERROR_LOG_DESCRIPTION_ORA = "ORA-";
   public static final String ERROR_LOG_TRIGGER_CATEGORY_SYSTEM = "系统";
   public static final String ERROR_LOG_TRIGGER_CATEGORY_USER = "用户";
   @Value("${sunway.monitor.error.log.enabled:true}")
   private boolean enabled;
   private static boolean logging = true;
   @Autowired
   private CoreSystemMonitorErrorLogService errorLogService;

   public static void enable() {
      logging = true;
   }

   @Around("within(com.sunwayworld..resource..*)&&!within(com.sunwayworld.cloud.module.lcdp.resource.service..*)||within(com.sunwayworld..controller..*)")
   public Object handleResourceException(ProceedingJoinPoint pjp) throws Throwable {
      if (logging && this.enabled) {
         Class<?> clazz = ClassUtils.getRawType(pjp.getTarget().getClass());
         LogModule logModule = ReflectionUtils.getAnnotation(clazz, LogModule.class);

         try {
            return pjp.proceed();
         } catch (Throwable var18) {
            CoreLogBean coreLog = this.getCoreLog(pjp, logModule == null ? "UNKNOWN" : logModule.value(), LogType.EXCEPTION.name());
            StringBuilder logContent = new StringBuilder();
            if (!StringUtils.isEmpty(coreLog.getContent())) {
               logContent.append(coreLog.getContent()).append("\n");
            }

            logContent.append(this.getThrowableContent(var18));
            String logDesc = logContent.toString();
            CoreSystemMonitorErrorLogBean errorLogBean = new CoreSystemMonitorErrorLogBean();
            errorLogBean.setLogTime(LocalDateTime.now());
            errorLogBean.setLogCategory("exception");
            errorLogBean.setLogTimes(1L);
            errorLogBean.setRevisionFlag("0");
            errorLogBean.setLogDetail(logContent.toString());
            String interceptUsefulInformation = this.getLogDesc(logDesc);
            errorLogBean.setLogDesc(interceptUsefulInformation);
            if (logModule != null) {
               String logModuleString = logModule.toString();
               String logSourceTitle = "";
               if (logModuleString.contains("=")) {
                  logSourceTitle = logModuleString.substring(logModuleString.indexOf("=") + 1, logModuleString.indexOf(")"));
               } else {
                  logSourceTitle = logModuleString.substring(logModuleString.indexOf("(") + 1, logModuleString.indexOf(")"));
               }

               errorLogBean.setLogModule(logSourceTitle);
            }

            String signature = this.getSimpleSignature(pjp.getTarget(), pjp.getSignature());
            String logInterfaceName = this.getLogInterfaceName(signature);
            errorLogBean.setLogInterfaceName(logInterfaceName);
            if (!LocalContextHelper.isUserLogin()) {
               errorLogBean.setTriggerCategory("系统");
            } else {
               errorLogBean.setTriggerCategory("用户(" + LocalContextHelper.getLoginUserName() + ")");
            }

            List<CoreSystemMonitorErrorLogBean> errorLogBeanList = this.errorLogService
               .selectListByFilter(SearchFilter.instance().match("LOGINTERFACENAME", errorLogBean.getLogInterfaceName()).filter(MatchPattern.SEQ), new Order[0]);
            List<CoreSystemMonitorErrorLogBean> cancelRevisionErrorLogBeanList = errorLogBeanList.stream()
               .filter(errLog -> "1".equals(errLog.getRevisionFlag()))
               .collect(Collectors.toList());
            List<CoreSystemMonitorErrorLogBean> revidionErrorLogBeanList = errorLogBeanList.stream()
               .filter(errLog -> "0".equals(errLog.getRevisionFlag()))
               .collect(Collectors.toList());
            if (errorLogBeanList.size() == 0) {
               this.errorLogService.insert(errorLogBean);
            }

            if (cancelRevisionErrorLogBeanList.size() == 0 && revidionErrorLogBeanList.size() != 0) {
               for (CoreSystemMonitorErrorLogBean logBean : revidionErrorLogBeanList) {
                  logBean.setLogTimes(logBean.getLogTimes() + 1L);
                  logBean.setLogTime(LocalDateTime.now());
                  if (!LocalContextHelper.isUserLogin()) {
                     logBean.setTriggerCategory("系统");
                  } else {
                     logBean.setTriggerCategory("用户(" + LocalContextHelper.getLoginUserName() + ")");
                  }
               }

               this.errorLogService.updateIfChanged(revidionErrorLogBeanList);
            }

            throw var18;
         }
      } else {
         return pjp.proceed();
      }
   }

   private CoreLogBean getCoreLog(ProceedingJoinPoint pjp, String module, String type) {
      CoreLogBean coreLog = new CoreLogBean();
      coreLog.setModule(module);
      coreLog.setType(type);
      coreLog.setSignature(this.getSimpleSignature(pjp.getTarget(), pjp.getSignature()));
      if (pjp.getArgs().length > 0) {
         coreLog.setContent(this.getContent(pjp.getSignature(), pjp.getArgs()));
      }

      return coreLog;
   }

   private String getContent(Signature signature, Object[] args) {
      MethodSignature methodSignature = (MethodSignature)signature;
      StringBuilder sb = new StringBuilder();
      sb.append("[params]");
      if (methodSignature.getParameterNames() != null) {
         int i = 0;

         for (int j = methodSignature.getParameterNames().length; i < j; i++) {
            sb.append("\n");
            String parameterName = methodSignature.getParameterNames()[i];
            Object value = args[i];
            sb.append(parameterName).append(" : ").append(ObjectUtils.toShortString(value));
         }
      }

      return sb.toString();
   }

   private String getThrowableContent(Throwable throwable) {
      return "[EXCEPTION]\n" + ObjectUtils.toShortString(throwable);
   }

   private String getSimpleSignature(Object target, Signature signature) {
      MethodSignature methodSignature = (MethodSignature)signature;
      StringBuilder builder = new StringBuilder();
      builder.append(Modifier.toString(methodSignature.getModifiers()))
         .append(" ")
         .append(methodSignature.getReturnType().getSimpleName())
         .append(" ")
         .append(target.getClass().getSimpleName())
         .append(".")
         .append(methodSignature.getName())
         .append("(");
      if (methodSignature.getParameterNames() != null) {
         int i = 0;

         for (int j = methodSignature.getParameterNames().length; i < j; i++) {
            if (i > 0) {
               builder.append(", ");
            }

            String parameterName = methodSignature.getParameterNames()[i];
            Class<?> parameterType = methodSignature.getParameterTypes()[i];
            builder.append(parameterType.getSimpleName()).append(" ").append(parameterName);
         }

         builder.append(")");
      }

      return builder.toString();
   }

   private String getLogDesc(String logDesc) {
      int index = logDesc.indexOf("[EXCEPTION]");
      String logContentSbuString = logDesc.substring(index + 1);
      String[] logDescArray = logContentSbuString.split("\n");
      StringBuilder logDescBuilder = new StringBuilder();
      int logRowCount = 0;

      for (int i = 0; i < logDescArray.length; i++) {
         if (logDescArray[i].contains("ORA-")) {
            logDescBuilder.append(logDescArray[i]);
            logRowCount = 5;
         }

         if (logDescArray[i].contains("### The error") || logDescArray[i].contains("### Error") && !logDescArray[i].contains("ORA-")) {
            logDescBuilder.append(logDescArray[i]);
         }

         if (logDescArray[i].contains("at com.sunwayworld")) {
            logDescBuilder.append(logDescArray[i]).append("\n");
            logRowCount++;
         }

         if (logRowCount >= 5) {
            break;
         }
      }

      return logRowCount >= 5 ? logDescBuilder.toString() : logContentSbuString;
   }

   private String getLogInterfaceName(String signature) {
      String[] logInterfaceNameStringArray = signature.split(" ");
      StringBuilder logInterfaceNameBuilder = new StringBuilder();
      if (logInterfaceNameStringArray.length >= 4) {
         int index1 = signature.indexOf("(");
         int index2 = signature.lastIndexOf(")");
         StringBuilder interfaceName = new StringBuilder();
         String[] removeParamStringArray = interfaceName.append(signature).delete(index1 + 1, index2).toString().split(" ");
         logInterfaceNameBuilder.append(removeParamStringArray[removeParamStringArray.length - 1]);
      }

      if (logInterfaceNameStringArray.length == 3) {
         logInterfaceNameBuilder.append(logInterfaceNameStringArray[logInterfaceNameStringArray.length - 1]);
      }

      return logInterfaceNameBuilder.toString().replace(" ", "");
   }
}
