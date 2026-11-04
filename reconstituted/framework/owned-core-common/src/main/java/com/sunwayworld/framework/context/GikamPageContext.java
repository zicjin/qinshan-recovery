package com.sunwayworld.framework.context;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.exception.ReflectionException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.log.aspect.LoggingAspect;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

public abstract class GikamPageContext {
   private static final String RAW_PAGE_CACHE_KEY = "CORE_RAW_PAGE";

   public static void cache() {
      try {
         LoggingAspect.disable();
         Map<String, HandlerMapping> allRequestMappings = BeanFactoryUtils.beansOfTypeIncludingAncestors(
            ApplicationContextHelper.getApplicationContext(), HandlerMapping.class, true, false
         );
         allRequestMappings.entrySet().stream().filter(e -> "requestMappingHandlerMapping".equals(e.getKey())).forEach(e -> {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = ((RequestMappingHandlerMapping)e.getValue()).getHandlerMethods();
            handlerMethods.entrySet().forEach(m -> {
               RequestMappingInfo mappingInfo = m.getKey();
               PatternsRequestCondition patternsCondition = mappingInfo.getPatternsCondition();
               Set<String> patterns = patternsCondition.getPatterns();
               if (patterns != null && !patterns.isEmpty()) {
                  Class<?> type = m.getValue().getBeanType();
                  if (ClassUtils.isAnnotationPrsent(type, Controller.class)) {
                     Method requestMethod = m.getValue().getMethod();
                     Object controller = ApplicationContextHelper.getBean(type);

                     try {
                        String path = (String)ReflectionUtils.invokeMethod(requestMethod, controller);

                        try (InputStream is = GikamPageContext.class.getClassLoader().getResourceAsStream("templates/" + path + ".html")) {
                           if (is != null) {
                              String content = StringUtils.read(is);
                              String pattern = patterns.iterator().next();
                              RedisHelper.put("CORE_RAW_PAGE", getCacheKey(pattern), content);
                           }
                        } catch (IOException var23) {
                           throw new UnexpectedException(var23);
                        }
                     } catch (ReflectionException var24) {
                     }
                  }
               }
            });
         });
      } finally {
         LoggingAspect.enable();
      }
   }

   public static String getRawPage(String uri) {
      return RedisHelper.get("CORE_RAW_PAGE", getCacheKey(uri));
   }

   private static String getCacheKey(String rawPath) {
      return StringUtils.removeEnd(StringUtils.removeStart(rawPath, "/"), "/");
   }
}
