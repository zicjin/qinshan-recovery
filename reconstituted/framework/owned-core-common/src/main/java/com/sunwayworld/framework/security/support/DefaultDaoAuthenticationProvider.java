/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.support;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.PasswordException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.security.AccountCategory;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.captcha.SecurityCaptchaService;
import com.sunwayworld.framework.security.exception.PreviousLoginDetectedException;
import com.sunwayworld.framework.security.exception.SingleBrowserMultiUserException;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.security.sso.RemoteAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DefaultDaoAuthenticationProvider
extends DaoAuthenticationProvider {
    private static final Logger logger = LogManager.getLogger(DaoAuthenticationProvider.class);
    @Autowired
    @Lazy
    protected CoreUserService userService;
    @Autowired
    private CorePasswordPolicyService passwordPolicyService;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Value(value="${sunway.security.remote-authenticator-name:}")
    private String remoteAuthenticatorName;
    @Autowired
    @Lazy
    private SecurityCaptchaService captchaService;
    @Autowired
    private CoreLoginLogService loginLogService;

    public DefaultDaoAuthenticationProvider() {
        this.setHideUserNotFoundExceptions(false);
    }

    protected CoreUserService getUserService() {
        if (this.userService == null) {
            this.userService = ApplicationContextHelper.getBeanIfPresent(CoreUserService.class);
        }
        return this.userService;
    }

    protected CorePasswordPolicyService getPasswordPolicyService() {
        if (this.passwordPolicyService == null) {
            this.passwordPolicyService = ApplicationContextHelper.getBeanIfPresent(CorePasswordPolicyService.class);
        }
        return this.passwordPolicyService;
    }

    protected PasswordEncoder getPasswordEncoder() {
        if (this.passwordEncoder == null) {
            this.passwordEncoder = ApplicationContextHelper.getBeanIfPresent(PasswordEncoder.class);
        }
        return this.passwordEncoder;
    }

    protected SecurityCaptchaService getCaptchaService() {
        if (this.captchaService == null) {
            this.captchaService = ApplicationContextHelper.getBeanIfPresent(SecurityCaptchaService.class);
        }
        return this.captchaService;
    }

    protected CoreLoginLogService getLoginLogService() {
        if (this.loginLogService == null) {
            this.loginLogService = ApplicationContextHelper.getBeanIfPresent(CoreLoginLogService.class);
        }
        return this.loginLogService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RemoteAuthenticator remoteAuthenticator;
        LoginUser loginUser;
        SecurityCaptchaService cs = this.getCaptchaService();
        if (cs != null) {
            cs.checkCaptcha();
        }
        if (LocalContextHelper.isUserLogin()) {
            throw new SingleBrowserMultiUserException();
        }
        HttpServletRequest currentReq = ServletUtils.getCurrentRequest();
        String loginType = currentReq != null ? currentReq.getParameter("loginType") : null;
        if (!StringUtils.isEmpty(loginType)) {
            SsoAuthenticator authenticator = SsoAuthenticatorRegistry.INSTANCE.getAuthenticator(loginType);
            if (authenticator == null) {
                throw new SecurityException("GIKAM.SECURITY.SSO_LOGIN.INVALID_LOGINTYPE");
            }
            loginUser = authenticator.authenticate();
            authentication = new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getAdditionalCheck() ? authentication.getCredentials() : loginUser.getPassword(), CollectionUtils.emptyList());
            if (loginUser.getAdditionalCheck()) {
                authentication.setAuthenticated(false);
            }
        }
        if (StringUtils.isEmpty(loginType) && !StringUtils.isBlank(this.remoteAuthenticatorName) && (remoteAuthenticator = (RemoteAuthenticator)ApplicationContextHelper.getBeanIfPresent(this.remoteAuthenticatorName)) != null) {
            loginUser = remoteAuthenticator.authenticate((String)authentication.getPrincipal(), AuthenticationHelper.getRawPassword((String)authentication.getCredentials()));
            CoreUserService us = this.getUserService();
            CoreUserBean user = us != null ? (CoreUserBean)us.selectById(loginUser.getUsername()) : null;
            if (!ApplicationContextHelper.stressTesting()) {
                this.logoutPreviousLoginIfNecessary(user);
            }
            return new UsernamePasswordAuthenticationToken(loginUser, user.getPassword());
        }
        return super.authenticate(authentication);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        block20: {
            try {
                CoreUserService us = this.getUserService();
                CoreUserBean user = us != null ? (CoreUserBean)us.selectById(userDetails.getUsername()) : null;
                if (user == null) {
                    throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
                }
                try {
                    String rawPassword = null;
                    if (!authentication.isAuthenticated() && ((LoginUser)userDetails).getAdditionalCheck()) {
                        rawPassword = AuthenticationHelper.getRawPassword((String)authentication.getCredentials());
                        PasswordEncoder pe = this.getPasswordEncoder();
                        String encoded = pe != null ? pe.encode(rawPassword) : rawPassword;
                        DefaultUserDetailsChecker.checkPassword((String)authentication.getPrincipal(), encoded);
                    }
                    if (ApplicationContextHelper.stressTesting()) break block20;
                    CorePasswordPolicyService pps = this.getPasswordPolicyService();
                    List<CorePasswordPolicyBean> passwordPolicyList = pps != null ? pps.selectEffectedList(user.getId()) : null;
                    Long passwordExpireRemainingDays = (pps != null && passwordPolicyList != null) ? pps.selectPasswordExpireRemainingDays(user, passwordPolicyList) : null;
                    if (passwordExpireRemainingDays != null && passwordExpireRemainingDays < 0L) {
                        CoreUserBean updateUser = new CoreUserBean();
                        updateUser.setId(user.getId());
                        boolean locked = false;
                        Long passwordExpiredMaxUse = pps != null ? pps.selectPasswordExpiredMaxUse(passwordPolicyList) : null;
                        if (user.getExpiredPasswordUses() == null || passwordExpiredMaxUse == null) {
                            locked = true;
                        }
                        if (user.getExpiredPasswordUses() != null && passwordExpiredMaxUse != null && user.getExpiredPasswordUses() >= passwordExpiredMaxUse) {
                            locked = true;
                        }
                        if (locked) {
                            updateUser.setStatus("locked");
                            if (us != null) us.update(updateUser);
                            throw new LockedException("GIKAM.SECURITY.LOGIN.LOCKED");
                        }
                        updateUser.setExpiredPasswordUses(user.getExpiredPasswordUses() == null ? 1L : user.getExpiredPasswordUses() + 1L);
                        if (us != null) us.update(updateUser);
                    }
                    HttpServletRequest req = ServletUtils.getCurrentRequest();
                    String loginType = req != null ? req.getParameter("loginType") : null;
                    if ("1".equals(ApplicationContextHelper.getConstantValue("UPDATE_BAD_PASSWORD_AFTER_LOGIN")) && !"AD".equals(loginType)) {
                        if (StringUtils.isBlank(rawPassword)) {
                            rawPassword = AuthenticationHelper.getRawPassword((String)authentication.getCredentials());
                        }
                        if (!StringUtils.isBlank(rawPassword)) {
                            try {
                                if (pps != null) pps.checkPassword(user.getId(), rawPassword);
                            }
                            catch (PasswordException pe) {
                                logger.warn(user.getId() + "-" + pe.getMessage(), (Throwable)pe);
                                ((LoginUser)userDetails).setBadPassword(true);
                            }
                        }
                    }
                    this.detectMultiLogin(user);
                    if (user.getFailedLoginAttempts() == null || user.getFailedLoginAttempts() > 0L) {
                        CoreUserBean updateUser = new CoreUserBean();
                        updateUser.setId(user.getId());
                        updateUser.setFailedLoginAttempts(0L);
                        if (us != null) us.update(updateUser);
                    }
                    this.logoutPreviousLoginIfNecessary(user);
                }
                catch (BadCredentialsException bce) {
                    DefaultUserDetailsChecker.postAuthenticationFailure(userDetails.getUsername());
                }
                finally {
                    if (userDetails instanceof CredentialsContainer) {
                        ((CredentialsContainer)((Object)userDetails)).eraseCredentials();
                    }
                }
            }
            catch (Exception ex) {
                if (AuthenticationException.class.isAssignableFrom(ex.getClass())) {
                    throw ex;
                }
                logger.error(ex.getMessage(), (Throwable)ex);
                throw new ApplicationWarningException("GIKAM.EXCEPTION.UNEXPECTED", new String[0]);
            }
        }
    }

    private void logoutPreviousLoginIfNecessary(CoreUserBean user) {
        String token;
        if ("0".equals(user.getMultiLogin()) && !StringUtils.isBlank(token = AuthenticationHelper.getPreviousLoginToken(user.getId()))) {
            CoreLoginLogBean filter = new CoreLoginLogBean();
            filter.setToken(token);
            CoreLoginLogBean previousLoginLog = this.loginLogService.getDao().selectFirstIfPresent((CoreLoginLogBean)filter, Order.desc("ID"));
            if (previousLoginLog != null && StringUtils.equals(previousLoginLog.getDevice(), "Desktop")) {
                AuthenticationHelper.markOffline(token, AccountCategory.OFFLINE_PRELOGIN.name());
                AuthenticationHelper.logout(user.getId(), token, "\u4e0d\u5141\u8bb8\u91cd\u590d\u767b\u5f55\uff0c\u5f3a\u5236\u4e0b\u7ebf");
            }
        }
    }

    private void detectMultiLogin(CoreUserBean user) {
        String token;
        if (!"1".equals(ServletUtils.getCurrentRequest().getParameter("force")) && "0".equals(user.getMultiLogin()) && !StringUtils.isBlank(token = AuthenticationHelper.getPreviousLoginToken(user.getId()))) {
            CoreLoginLogBean filter = new CoreLoginLogBean();
            filter.setToken(token);
            CoreLoginLogBean previousLoginLog = this.loginLogService.getDao().selectFirstIfPresent((CoreLoginLogBean)filter, Order.desc("ID"));
            if (previousLoginLog != null && StringUtils.equals(previousLoginLog.getDevice(), "Desktop")) {
                throw new PreviousLoginDetectedException(previousLoginLog.getLoginIp());
            }
        }
    }
}
