/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.authentication;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.provider.service.SecurityProviderService;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public class AuthenticationHelper {
    public static native String getPreviousLoginToken(String var0);

    public static native void login(HttpServletRequest var0, Authentication var1, String var2);

    public static native void logout(String var0, String var1, String var2);

    public static native Authentication getCacheAuthentication(String var0);

    public static native void updateCacheAuthentication(String var0, Authentication var1);

    public static void markOffline(String rememberMeToken, String category) {
        RedisHelper.put("MARK_OFFLINE", rememberMeToken, category, 300);
    }

    public static String getOfflineCategory(String rememberMeToken) {
        return (String)RedisHelper.get("MARK_OFFLINE", rememberMeToken);
    }

    public static String getLoginUserToken() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request == null) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (!"sunway-remember-me-cookie".equals(cookie.getName())) continue;
                return cookie.getValue();
            }
        }
        return request.getHeader("sunway-remember-me");
    }

    public static String getRawPassword(String password) {
        SecurityProviderService providerService;
        String publicKey;
        if (ServletUtils.getCurrentRequest() != null && !StringUtils.isBlank(publicKey = ServletUtils.getCurrentRequest().getHeader("SECURITY-RSA-KEY")) && (providerService = ApplicationContextHelper.getBean(SecurityProviderService.class)) != null) {
            return EncryptUtils.RSADecrypt(password, providerService.getPrivateKey(publicKey));
        }
        return password;
    }
}
