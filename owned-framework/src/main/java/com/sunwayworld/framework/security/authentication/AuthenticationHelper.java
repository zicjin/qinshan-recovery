package com.sunwayworld.framework.security.authentication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Owned authentication cache seam.
 *
 * <p>The historical methods were JNI entry points into the supplier session
 * and license runtime. This implementation keeps only session/cache behavior:
 * it never reads a machine identifier, license input, or supplier native
 * library.</p>
 */
public class AuthenticationHelper {
    private static final Map<String, Authentication> CACHE = new ConcurrentHashMap<String, Authentication>();
    private static final Map<String, String> OFFLINE = new ConcurrentHashMap<String, String>();

    public AuthenticationHelper() {}

    public static String getPreviousLoginToken(String userId) {
        // The owned runtime does not persist supplier token identifiers.
        // Returning null keeps the previous-session path fail-closed.
        return null;
    }

    public static void login(HttpServletRequest request, Authentication authentication, String token) {
        if (authentication == null) throw new IllegalArgumentException("authentication must not be null");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        if (token != null && !token.trim().isEmpty()) CACHE.put(token, authentication);
    }

    public static void logout(String userId, String token, String category) {
        if (token != null) CACHE.remove(token);
        if (userId != null) OFFLINE.remove(userId);
        SecurityContextHolder.clearContext();
    }

    public static Authentication getCacheAuthentication(String token) {
        return token == null ? null : CACHE.get(token);
    }

    public static void updateCacheAuthentication(String token, Authentication authentication) {
        if (token == null || token.trim().isEmpty()) return;
        if (authentication == null) CACHE.remove(token);
        else CACHE.put(token, authentication);
    }

    public static void markOffline(String userId, String category) {
        if (userId != null) OFFLINE.put(userId, category == null ? "" : category);
    }

    public static String getOfflineCategory(String userId) {
        return userId == null ? null : OFFLINE.get(userId);
    }

    public static String getLoginUserToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    public static String getRawPassword(String password) {
        return password;
    }
}
