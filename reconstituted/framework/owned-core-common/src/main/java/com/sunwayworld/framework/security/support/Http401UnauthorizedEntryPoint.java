package com.sunwayworld.framework.security.support;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.exception.AccountOfflineException;
import com.sunwayworld.framework.utils.ServletUtils;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Component
@Primary
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {
   private static final Logger logger = LogManager.getLogger(Http401UnauthorizedEntryPoint.class);
   @Autowired
   private RequestMappingHandlerMapping requestMappingHandlerMapping;

   private RequestMappingHandlerMapping resolveHandlerMapping() {
      if (this.requestMappingHandlerMapping == null) {
         try {
            this.requestMappingHandlerMapping = ApplicationContextHelper.getBean(RequestMappingHandlerMapping.class);
         } catch (Exception ignored) {
         }
      }
      return this.requestMappingHandlerMapping;
   }

   @Override
   public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
      if (logger.isDebugEnabled()) {
         logger.debug(exception.getMessage(), (Throwable)exception);
      } else {
         logger.warn(exception.getMessage());
      }

      String requestUri = ServletUtils.getRequestUri(request);
      RequestMappingHandlerMapping rmhm = this.resolveHandlerMapping();
      Map<RequestMappingInfo, HandlerMethod> map = rmhm != null ? rmhm.getHandlerMethods() : null;

      if (map != null) {
      for (Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
         RequestMappingInfo info = m.getKey();
         HandlerMethod method = m.getValue();
         PatternsRequestCondition p = info.getPatternsCondition();

         if (p != null && p.getPatterns() != null) {
         for (String url : p.getPatterns()) {
            if (requestUri.equals(url) && method.getMethod().getDeclaringClass().isAnnotationPresent(Controller.class)) {
               ServletUtils.sendRedirect(request, response, ServletUtils.getContextPath() + "/");
               return;
            }
         }
         }
      }
      }

      Class<?> clazz = exception.getClass();
      if (AccountOfflineException.class.isAssignableFrom(clazz)) {
         ResponseStatus status = clazz.getAnnotation(ResponseStatus.class);
         int httpStatus = status == null ? HttpStatus.GONE.value() : status.value().value();
         ServletUtils.responseMessage(response, httpStatus, exception.getMessage());
      } else {
         ServletUtils.responseMessage(response, JSON.toJSONString(Collections.singletonMap("message", "core.security.login.unauthorized")));
      }
   }
}
