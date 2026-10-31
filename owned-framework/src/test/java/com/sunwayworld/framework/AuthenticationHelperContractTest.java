package com.sunwayworld.framework;

import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/** Verifies owned cache/session semantics without a servlet or native runtime. */
public final class AuthenticationHelperContractTest {
    private AuthenticationHelperContractTest() {}

    public static void main(String[] args) {
        Authentication authentication = new UsernamePasswordAuthenticationToken("fixture-user", null);
        AuthenticationHelper.login(null, authentication, "fixture-token");
        if (AuthenticationHelper.getCacheAuthentication("fixture-token") != authentication) {
            throw new AssertionError("authentication cache did not retain login");
        }
        if (!"fixture-user".equals(AuthenticationHelper.getLoginUserToken())) {
            throw new AssertionError("security context did not retain principal");
        }
        AuthenticationHelper.markOffline("fixture-user", "manual");
        if (!"manual".equals(AuthenticationHelper.getOfflineCategory("fixture-user"))) {
            throw new AssertionError("offline category mismatch");
        }
        if (AuthenticationHelper.getPreviousLoginToken("fixture-user") != null) {
            throw new AssertionError("previous token lookup must fail closed");
        }
        AuthenticationHelper.logout("fixture-user", "fixture-token", "manual");
        if (AuthenticationHelper.getCacheAuthentication("fixture-token") != null) {
            throw new AssertionError("logout did not clear cache");
        }
        if (AuthenticationHelper.getLoginUserToken() != null) {
            throw new AssertionError("logout did not clear security context");
        }
        System.out.println("authentication helper contract checks passed");
    }
}
