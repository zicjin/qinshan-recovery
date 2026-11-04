/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import com.blueconic.browscap.Capabilities;
import com.blueconic.browscap.ParseException;
import com.blueconic.browscap.UserAgentParser;
import com.blueconic.browscap.UserAgentService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

public abstract class ServletUtils {
    private static UserAgentParser userAgentParser;
    private static String contextPath;

    public static final Capabilities getCurrentRequestCapabilities() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            return null;
        }
        return userAgentParser.parse(request.getHeader("user-agent"));
    }

    public static final String getRemoteIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ServletUtils.isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ServletUtils.isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ServletUtils.isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ServletUtils.isBlankOrUnknownIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ServletUtils.isBlankOrUnknownIp(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static final HttpServletRequest getCurrentRequest() {
        return LocalContextHelper.getHttpServletRequest();
    }

    public static final HttpServletResponse getCurrentResponse() {
        return LocalContextHelper.getHttpServletResponse();
    }

    public static final HttpSession getCurrentSession() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            return null;
        }
        return request.getSession(false);
    }

    public static final String getCurrentRawSessionId() {
        HttpSession session = ServletUtils.getCurrentSession();
        if (session == null) {
            return null;
        }
        return ServletUtils.getRawSessionId(session.getId());
    }

    public static final ServletContext getServletContext() {
        WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
        return webApplicationContext.getServletContext();
    }

    public static final String getServletRootPath() {
        return ServletUtils.getServletContext().getRealPath("/");
    }

    public static final String getRawSessionId(String sessionId) {
        if (sessionId.contains("!")) {
            return sessionId.substring(0, sessionId.indexOf("!"));
        }
        return sessionId;
    }

    public static final Map<String, String> getCurrentRequestParamMap() {
        HashMap<String, String> map = new HashMap<String, String>();
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request != null) {
            request.getParameterMap().forEach((k, v) -> map.put((String)k, v[0]));
        }
        return map;
    }

    @Deprecated
    public static final void responseError(String error) {
        ServletUtils.responseError(400, error);
    }

    public static final void responseError(HttpErrorMessage errorMessage) {
        ServletUtils.responseError(errorMessage.getStatus(), errorMessage.toString());
    }

    public static final void responseError(int httpStatus, String error) {
        HttpServletResponse response = ServletUtils.getCurrentResponse();
        if (response != null && !response.isCommitted()) {
            response.reset();
            response.setStatus(httpStatus);
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter pw = response.getWriter();){
                pw.print(error);
                pw.flush();
            }
            catch (IOException ioe) {
                throw new UnexpectedException(ioe);
            }
        }
    }

    public static final void responseMessage(ServletResponse response, String message) {
        if (response != null && !response.isCommitted()) {
            response.reset();
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter writer = response.getWriter();){
                writer.write(message);
                writer.flush();
            }
            catch (IOException ioe) {
                throw new UnexpectedException(ioe);
            }
        }
    }

    public static final void responseMessage(ServletResponse response, int httpStatus, String message) {
        if (response != null && !response.isCommitted()) {
            response.reset();
            ((HttpServletResponse)response).setStatus(httpStatus);
            response.setCharacterEncoding("UTF-8");
            try (PrintWriter writer = response.getWriter();){
                writer.write(message);
                writer.flush();
            }
            catch (IOException ioe) {
                throw new UnexpectedException(ioe);
            }
        }
    }

    public static final String getRequestUri() {
        return ServletUtils.getRequestUri(ServletUtils.getCurrentRequest());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static String getContextPath() {
        if (contextPath != null) return contextPath;
        Class<ServletUtils> clazz = ServletUtils.class;
        synchronized (ServletUtils.class) {
            if (contextPath != null) return contextPath;
            String envContextPath = ApplicationContextHelper.getEnvironment().getProperty("server.servlet.context-path", "");
            contextPath = StringUtils.isBlank(envContextPath) || "/".equals(envContextPath) ? "" : envContextPath;
            // ** MonitorExit[var0] (shouldn't be in output)
            return contextPath;
        }
    }

    public static final String getRequestUri(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        if (StringUtils.isBlank(ServletUtils.getContextPath())) {
            return request.getRequestURI();
        }
        return StringUtils.removeStart(request.getRequestURI(), ServletUtils.getContextPath());
    }

    public static final String getRequestUrl() {
        return ServletUtils.getRequestUrl(ServletUtils.getCurrentRequest());
    }

    public static final String getRequestUrl(HttpServletRequest request) {
        if (request == null) {
            return "";
        }
        if (StringUtils.isBlank(ServletUtils.getContextPath())) {
            return request.getRequestURL().toString();
        }
        return StringUtils.removeStart(request.getRequestURL().toString(), ServletUtils.getContextPath());
    }

    public static final String getUrl(String url, String replativeUrl) {
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(url)) {
            sb.append(StringUtils.removeEnd(url, "/"));
        }
        return sb.append(replativeUrl).toString();
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue) {
        ServletUtils.addCookie(request, response, cookieName, cookieValue, -1);
    }

    public static void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int expirySeconds) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(StringUtils.isBlank(request.getContextPath()) ? "/" : request.getContextPath());
        cookie.setSecure(false);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(expirySeconds);
        response.addCookie(cookie);
    }

    public static Cookie getCookie(HttpServletRequest request, String cookieName) {
        for (Cookie cookie : request.getCookies()) {
            if (!cookieName.equals(cookie.getName())) continue;
            return cookie;
        }
        return null;
    }

    public static void enhanceByRequest(MapperParameter parameter) {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request != null) {
            request.getParameterMap().forEach((k, v) -> {
                if (v != null) {
                    parameter.put(k, v[0]);
                }
            });
        }
    }

    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String location) {
        response.setStatus(302);
        response.setHeader("location", location);
    }

    public static boolean isMobileClient() {
        Capabilities capabilities = ServletUtils.getCurrentRequestCapabilities();
        if (capabilities == null) {
            return false;
        }
        return !capabilities.getDeviceType().contains("Desktop");
    }

    private static boolean isBlankOrUnknownIp(String ip) {
        return StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip) || StringUtils.split(ip, ".").length != 4;
    }

    static {
        try {
            userAgentParser = new UserAgentService().loadParser();
        }
        catch (ParseException | IOException ex) {
            throw new UnexpectedException(ex);
        }
    }
}
