package com.sunwayworld.framework.security.authentication;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.provider.service.SecurityProviderService;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationHelper {
    private static final Map<String, Authentication> AUTHENTICATION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, String> PREVIOUS_LOGIN_TOKENS = new ConcurrentHashMap<>();

    private AuthenticationHelper() {
    }

    public static String getPreviousLoginToken(String userId) {
        return userId == null ? null : PREVIOUS_LOGIN_TOKENS.get(userId);
    }

    public static void login(HttpServletRequest request, Authentication authentication, String token) {
        if (authentication == null || token == null || token.trim().isEmpty()) {
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AUTHENTICATION_CACHE.put(token, authentication);
        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            PREVIOUS_LOGIN_TOKENS.put(((LoginUser) principal).getUsername(), token);
        }
    }

    public static void logout(String userId, String token, String category) {
        if (token != null && !token.trim().isEmpty()) {
            AUTHENTICATION_CACHE.remove(token);
        }
        if (userId != null) {
            PREVIOUS_LOGIN_TOKENS.remove(userId);
        }
    }

    public static Authentication getCacheAuthentication(String token) {
        return token == null ? null : AUTHENTICATION_CACHE.get(token);
    }

    public static void updateCacheAuthentication(String token, Authentication authentication) {
        if (token == null || token.trim().isEmpty()) return;
        if (authentication == null) AUTHENTICATION_CACHE.remove(token);
        else AUTHENTICATION_CACHE.put(token, authentication);
    }

    public static void markOffline(String rememberMeToken, String category) {
        RedisHelper.put("MARK_OFFLINE", rememberMeToken, category, 300);
    }

    public static String getOfflineCategory(String rememberMeToken) {
        return (String) RedisHelper.get("MARK_OFFLINE", rememberMeToken);
    }

    public static String getLoginUserToken() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) return null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sunway-remember-me-cookie".equals(cookie.getName())) return cookie.getValue();
            }
        }
        return request.getHeader("sunway-remember-me");
    }

    public static String getRawPassword(String password) {
        SecurityProviderService providerService;
        String publicKey;
        if (ServletUtils.getCurrentRequest() != null
                && !StringUtils.isBlank(publicKey = ServletUtils.getCurrentRequest().getHeader("SECURITY-RSA-KEY"))
                && (providerService = com.sunwayworld.framework.context.ApplicationContextHelper.getBeanIfPresent(SecurityProviderService.class)) != null) {
            return EncryptUtils.RSADecrypt(password, providerService.getPrivateKey(publicKey));
        }
        return password;
    }
}
