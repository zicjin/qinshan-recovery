package com.sunwayworld.owned.internal.web;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Shared transport registration for owned read modules, imported once by Boot. */
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OwnedReadWebConfiguration implements WebMvcConfigurer {
    @Override public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new WrapperArgumentResolver());
    }

    @Override public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new JsonMessageConverter());
    }

    public static final class WrapperArgumentResolver implements HandlerMethodArgumentResolver {
        private static final int MAX_BODY_BYTES = 1048576;
        @Override public boolean supportsParameter(MethodParameter parameter) {
            return parameter.getParameterType() == RestJsonWrapperBean.class;
        }
        @Override public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            if (request == null) throw new IllegalArgumentException("HTTP request is required");
            Map<String, String> params = new LinkedHashMap<String, String>();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                if (entry.getValue() == null || entry.getValue().length != 1) {
                    throw new IllegalArgumentException("repeated query parameter");
                }
                params.put(entry.getKey(), entry.getValue()[0]);
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[2048]; int size;
            while ((size = request.getInputStream().read(buffer)) != -1) {
                if (output.size() + size > MAX_BODY_BYTES) throw new IllegalArgumentException("query body too large");
                output.write(buffer, 0, size);
            }
            return new RestJsonWrapperBean(new String(output.toByteArray(), StandardCharsets.UTF_8), params, null, null);
        }
    }

    public static final class JsonMessageConverter extends AbstractHttpMessageConverter<Object> {
        public JsonMessageConverter() { super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json")); }
        @Override protected boolean supports(Class<?> type) { return type != String.class && type != byte[].class; }
        @Override public boolean canRead(Class<?> type, MediaType mediaType) { return false; }
        @Override protected Object readInternal(Class<?> type, HttpInputMessage input) { throw new UnsupportedOperationException("read handled by wrapper resolver"); }
        @Override protected void writeInternal(Object value, HttpOutputMessage output) throws IOException {
            byte[] bytes = JSON.toJSONString(value).getBytes(StandardCharsets.UTF_8);
            output.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            output.getHeaders().setContentLength(bytes.length);
            output.getBody().write(bytes);
        }
    }
}
