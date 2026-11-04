package com.sunwayworld.framework.security.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.enhance.LoginUserEnhancer;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.utils.StringUtils;
import java.lang.reflect.Method;
import java.util.Optional;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.util.ReflectionUtils;

public class DefaultTokenBasedRememberMeServices implements RememberMeServices, InitializingBean, LogoutHandler {
   public static final String REMEMBER_ME_CACHE_NAME = "security#rememberme";
   private static final Logger logger = LogManager.getLogger(DefaultTokenBasedRememberMeServices.class);
   private String cookieName = "sunway-remember-me-cookie";
   private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
   private boolean alwaysRemember = true;
   private String key = "sunway-remember-me";
   private int tokenValiditySeconds = -1;
   private Method setHttpOnlyMethod = ReflectionUtils.findMethod(Cookie.class, "setHttpOnly", boolean.class);
   private Boolean useSecureCookie;

   @Override
   public void afterPropertiesSet() throws Exception {
   }

   @Override
   public Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
      UserDetails user = null;
      SsoAuthenticator authenticator = null;
      String loginType = request.getParameter("loginType");
      if (!StringUtils.isEmpty(loginType)) {
         authenticator = SsoAuthenticatorRegistry.INSTANCE.getAuthenticator(loginType);
         if (authenticator != null) {
            user = authenticator.authenticate();
            ((LoginUser)user).setLoginType(loginType);
         }
      }

      try {
         if (user == null) {
            String rememberMeCookieToken = AuthenticationHelper.getLoginUserToken();
            if (rememberMeCookieToken == null) {
               return null;
            }

            logger.debug("Remember-me cookie detected");
            if (rememberMeCookieToken.length() == 0) {
               logger.debug("Cookie was empty");
               this.cancelCookie(request, response);
               return null;
            }

            user = this.processAutoLoginCookie(rememberMeCookieToken, request, response);
         }

         if (user == null) {
            logger.debug("Remember-me login failed");
            this.cancelCookie(request, response);
            return null;
         }

         DefaultUserDetailsChecker.check(user);
         logger.debug("Remember-me cookie accepted");
         Authentication successfulAuthentication = this.createSuccessfulAuthentication(request, user);
         if (authenticator != null && authenticator.login()) {
            this.loginSuccess(request, response, successfulAuthentication);
         }

         return successfulAuthentication;
      } catch (CookieTheftException var7) {
         this.cancelCookie(request, response);
         throw var7;
      } catch (UsernameNotFoundException var8) {
         logger.debug("Remember-me login was valid but corresponding user not found.", (Throwable)var8);
      } catch (InvalidCookieException var9) {
         logger.debug("Invalid remember-me cookie: " + var9.getMessage());
      } catch (AccountStatusException var10) {
         logger.debug("Invalid UserDetails: " + var10.getMessage());
      } catch (RememberMeAuthenticationException var11) {
         logger.debug(var11.getMessage());
      }

      this.cancelCookie(request, response);
      return null;
   }

   @Override
   public void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
      if (!this.rememberMeRequested(request)) {
         logger.debug("Remember-me login not requested.");
      } else {
         LoginUser loginUser = (LoginUser)successfulAuthentication.getPrincipal();
         LoginUserEnhancer enhancer = ApplicationContextHelper.getBean(LoginUserEnhancer.class);
         enhancer.enhance(loginUser);
         String token = this.setCookie(request, response, successfulAuthentication);
         AuthenticationHelper.login(request, successfulAuthentication, token);
      }
   }

   @Override
   public void loginFail(HttpServletRequest request, HttpServletResponse response) {
      logger.debug("Interactive login attempt was unsuccessful.");
      this.cancelCookie(request, response);
   }

   @Override
   public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
      LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
      if (loginUser != null) {
         if (logger.isDebugEnabled()) {
            logger.debug("Logout of user " + loginUser.getUsername());
         }

         String loginType = Optional.ofNullable(request.getParameter("loginType")).orElse(loginUser.getLoginType());
         if (!StringUtils.isEmpty(loginType)) {
            SsoAuthenticator authenticator = SsoAuthenticatorRegistry.INSTANCE.getAuthenticator(loginType);
            if (authenticator != null) {
               authenticator.logout();
            }
         }

         String rememberMeCookieToken = AuthenticationHelper.getLoginUserToken();
         if (!StringUtils.isEmpty(rememberMeCookieToken)) {
            AuthenticationHelper.logout(loginUser.getUsername(), rememberMeCookieToken, "正常登出");
         }

         this.cancelCookie(request, response);
      }
   }

   public void setTokenValiditySeconds(int tokenValiditySeconds) {
      this.tokenValiditySeconds = tokenValiditySeconds;
   }

   public void setUseSecureCookie(Boolean useSecureCookie) {
      this.useSecureCookie = useSecureCookie;
   }

   public void setAlwaysRemember(boolean alwaysRemember) {
      this.alwaysRemember = alwaysRemember;
   }

   private void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
      logger.debug("Cancelling cookie");
      Cookie cookie = new Cookie(this.cookieName, null);
      cookie.setMaxAge(0);
      cookie.setPath(this.getCookiePath(request));
      response.addCookie(cookie);
   }

   private String getCookiePath(HttpServletRequest request) {
      String contextPath = request.getContextPath();
      return contextPath.length() > 0 ? contextPath : "/";
   }

   private UserDetails processAutoLoginCookie(String cookieToken, HttpServletRequest request, HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
      Authentication authentication = AuthenticationHelper.getCacheAuthentication(cookieToken);
      if (authentication == null) {
         throw new RememberMeAuthenticationException("Invalid cookie, contains invalid authentication.");
      } else {
         return (UserDetails)authentication.getPrincipal();
      }
   }

   private Authentication createSuccessfulAuthentication(HttpServletRequest request, UserDetails user) {
      RememberMeAuthenticationToken auth = new RememberMeAuthenticationToken(this.key, user, user.getAuthorities());
      auth.setDetails(this.authenticationDetailsSource.buildDetails(request));
      return auth;
   }

   private boolean rememberMeRequested(HttpServletRequest request) {
      return this.alwaysRemember;
   }

   protected String retrieveUserName(Authentication authentication) {
      return authentication instanceof UserDetails ? ((UserDetails)authentication.getPrincipal()).getUsername() : authentication.getPrincipal().toString();
   }

   private String setCookie(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
      String cookieValue = StringUtils.randomUUID();
      Cookie cookie = new Cookie(this.cookieName, cookieValue);
      if (this.tokenValiditySeconds >= 0) {
         cookie.setMaxAge(this.tokenValiditySeconds);
      }

      cookie.setPath(this.getCookiePath(request));
      if (this.useSecureCookie == null) {
         cookie.setSecure(request.isSecure());
      } else {
         cookie.setSecure(this.useSecureCookie);
      }

      if (this.setHttpOnlyMethod != null) {
         ReflectionUtils.invokeMethod(this.setHttpOnlyMethod, cookie, Boolean.TRUE);
      } else if (logger.isDebugEnabled()) {
         logger.debug("Note: Cookie will not be marked as HttpOnly because you are not using Servlet 3.0 (Cookie#setHttpOnly(boolean) was not found).");
      }

      response.addCookie(cookie);
      return cookieValue;
   }
}
