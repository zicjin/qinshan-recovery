/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.log.aspect;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.ConcurrentException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.log.LogQueue;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    public static final String NOT_LOGIN_USERID = "NIL";
    public static final String NOT_LOGIN_USERNAME = "NIL";
    public static final String UNKNOWN_MODULE_NAME = "UNKNOWN";
    public static final String UNKNOWN_LOG_NAME = "UNKNOWN";
    private static final Logger logger = LogManager.getLogger(LoggingAspect.class);
    @Value(value="${sunway.logging.enabled:true}")
    private boolean enabled;
    @Value(value="${sunway.logging.persistence-mode:none}")
    private String persistenceMode;
    private static boolean logging = true;

    public static void disable() {
        logging = false;
    }

    public static void enable() {
        logging = true;
    }

    @Around(value="within(com.sunwayworld..resource..*)&&!within(com.sunwayworld.cloud.module.lcdp.resource.service..*)||within(com.sunwayworld..controller..*)")
    public Object handleResourceException(ProceedingJoinPoint pjp) throws Throwable {
        Object level;
        if (!logging || !this.enabled) {
            return pjp.proceed();
        }
        CoreLogBean coreLog = null;
        Class<?> clazz = ClassUtils.getRawType(pjp.getTarget().getClass());
        LogModule logModule = ReflectionUtils.getAnnotation(clazz, LogModule.class);
        MethodSignature methodSignature = (MethodSignature)pjp.getSignature();
        Log log = ReflectionUtils.getAnnotation(methodSignature.getMethod(), Log.class);
        if (log != null) {
            level = Level.getLevel(log.level().name());
            coreLog = this.getCoreLog(pjp, logModule == null ? "UNKNOWN" : logModule.value(), log.value(), log.type().name());
            if (logger.isEnabled((Level)level)) {
                logger.log((Level)level, coreLog.toLogString());
            }
        }
        try {
            level = pjp.proceed();
            return level;
        }
        catch (Throwable throwable) {
            if (ConcurrentException.class.isAssignableFrom(throwable.getClass())) {
                coreLog = null;
                throw throwable;
            }
            if (coreLog == null) {
                coreLog = this.getCoreLog(pjp, logModule == null ? "UNKNOWN" : logModule.value(), log == null ? "UNKNOWN" : log.value(), LogType.EXCEPTION.name());
            } else {
                coreLog.setType(LogType.EXCEPTION.name());
            }
            StringBuilder sb = new StringBuilder();
            if (!StringUtils.isEmpty(coreLog.getContent())) {
                sb.append(coreLog.getContent()).append("\n");
            }
            sb.append(this.getThrowableContent(throwable));
            coreLog.setContent(sb.toString());
            if (AuthenticationException.class.isAssignableFrom(throwable.getClass()) || CheckedException.class.isAssignableFrom(throwable.getClass())) {
                logger.warn(throwable.getMessage());
            } else {
                logger.error(coreLog.toLogString());
            }
            throw throwable;
        }
        finally {
            if (coreLog != null) {
                if ("default".equals(this.persistenceMode)) {
                    coreLog.setEndTime(LocalDateTime.now());
                    LogQueue.INSTANCE.offer(coreLog);
                } else if ("exception".equals(this.persistenceMode) && LogType.EXCEPTION.name().equals(coreLog.getType())) {
                    coreLog.setEndTime(LocalDateTime.now());
                    LogQueue.INSTANCE.offer(coreLog);
                }
            }
        }
    }

    private CoreLogBean getCoreLog(ProceedingJoinPoint pjp, String module, String name, String type) {
        CoreLogBean coreLog = new CoreLogBean();
        coreLog.setModule(module);
        coreLog.setName(name);
        coreLog.setType(type);
        coreLog.setSessionId(ServletUtils.getCurrentRawSessionId());
        if (LocalContextHelper.isUserLogin()) {
            coreLog.setUserId(LocalContextHelper.getLoginUserId());
            coreLog.setUserName(LocalContextHelper.getLoginUser().getUserName());
            coreLog.setUserOrgId(LocalContextHelper.getLoginOrgId());
            coreLog.setUserOrgName(LocalContextHelper.getLoginOrgName());
            coreLog.setLoginIp(ServletUtils.getRemoteIp(ServletUtils.getCurrentRequest()));
        } else {
            coreLog.setUserId("NIL");
            coreLog.setUserName("NIL");
        }
        coreLog.setStartTime(LocalDateTime.now());
        coreLog.setRequestUri(this.getLcdpScriptURI() == null ? this.getRequestURI() : this.getLcdpScriptURI());
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
            int j = methodSignature.getParameterNames().length;
            for (int i = 0; i < j; ++i) {
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

    private String getRequestURI() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request != null) {
            return ServletUtils.getRequestUri(request);
        }
        return null;
    }

    private String getLcdpScriptURI() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request != null && !StringUtils.isBlank(request.getHeader("script-path"))) {
            return request.getHeader("script-path");
        }
        return null;
    }

    private String getSimpleSignature(Object target, Signature signature) {
        MethodSignature methodSignature = (MethodSignature)signature;
        StringBuilder builder = new StringBuilder();
        builder.append(Modifier.toString(methodSignature.getModifiers())).append(" ").append(methodSignature.getReturnType().getSimpleName()).append(" ").append(target.getClass().getSimpleName()).append(".").append(methodSignature.getName()).append("(");
        if (methodSignature.getParameterNames() != null) {
            int j = methodSignature.getParameterNames().length;
            for (int i = 0; i < j; ++i) {
                if (i > 0) {
                    builder.append(", ");
                }
                String parameterName = methodSignature.getParameterNames()[i];
                Class parameterType = methodSignature.getParameterTypes()[i];
                builder.append(parameterType.getSimpleName()).append(" ").append(parameterName);
            }
            builder.append(")");
        }
        return builder.toString();
    }
}
