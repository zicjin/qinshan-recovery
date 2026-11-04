/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.service;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.security.sso.SsoAuthenticatorRegistry;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class DefaultUserDetailsChecker {
    private static CoreLoginLogService loginLogService;
    private static CoreUserService userService;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static final void checkPassword(String loginId, String encodedPassword) {
        String rawPasswordKey = "" + Thread.currentThread().getId();
        try {
            CoreUserBean user = DefaultUserDetailsChecker.getUserService().selectByLoginId(loginId);
            if (user == null) {
                DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(loginId, I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD", new String[0]));
                throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
            }
            if (!encodedPassword.equals(user.getPassword())) {
                String rawPassword = (String)MemoryCacheManager.get(rawPasswordKey);
                if (!StringUtils.isBlank(rawPassword) && (user.getPassword().equals(EncryptUtils.MD5Encrypt(rawPassword)) || user.getPassword().equals(EncryptUtils.SM3Encrypt(rawPassword)))) {
                    DefaultUserDetailsChecker.getUserService().updateColumn(user.getId(), "PASSWORD", encodedPassword);
                    return;
                }
                DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(loginId, I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD", new String[0]));
                throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
            }
        }
        finally {
            MemoryCacheManager.remove(rawPasswordKey);
        }
    }

    public static final void check(CoreUserBean user) {
        if (user == null) {
            throw new UsernameNotFoundException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
        }
        if ("1".equals(user.getLastSuspendedFlag())) {
            DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(user.getId(), I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.DEACTIVATED", new String[0]));
            throw new DisabledException("GIKAM.SECURITY.LOGIN.DEACTIVATED");
        }
        if ("locked".equals(user.getStatus())) {
            DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(user.getId(), I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.LOCKED", new String[0]));
            throw new LockedException("GIKAM.SECURITY.LOGIN.LOCKED");
        }
        if ("deprecated".equals(user.getStatus())) {
            DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(user.getId(), I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.DEPRECATED", new String[0]));
            throw new LockedException("GIKAM.SECURITY.LOGIN.DEPRECATED");
        }
        LocalDateTime now = LocalDateTime.now();
        if (user.getExpiryDate() != null && user.getExpiryDate().isBefore(now)) {
            DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(user.getId(), I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.EXPIRED", new String[0]));
            throw new LockedException("GIKAM.SECURITY.LOGIN.EXPIRED");
        }
    }

    public static final void check(UserDetails userDetails) {
        SsoAuthenticator authenticator;
        String loginType;
        CoreUserBean user = (CoreUserBean)DefaultUserDetailsChecker.getUserService().selectById(userDetails.getUsername());
        DefaultUserDetailsChecker.check(user);
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        if (request != null && !StringUtils.isBlank(loginType = request.getParameter("loginType")) && (authenticator = SsoAuthenticatorRegistry.INSTANCE.getAuthenticator(loginType)) != null) {
            authenticator.checkLoginUser((LoginUser)userDetails);
        }
    }

    public static final void postAuthenticationFailure(String loginId) {
        CoreUserBean user = DefaultUserDetailsChecker.getUserService().selectByLoginId(loginId);
        if (user != null) {
            List<CorePasswordPolicyBean> passwordPolicyList = ApplicationContextHelper.getBean(CorePasswordPolicyService.class).selectEffectedList(user.getId()).stream().filter(p -> p.getMaxFailedAttempts() != null && p.getMaxFailedAttempts() > 0L).collect(Collectors.toList());
            boolean bydayFailedAttemptPolicy = passwordPolicyList.stream().anyMatch(f -> "byday".equals(f.getFailedAttemptPolicy()) && f.getMaxFailedAttempts() != null && f.getMaxFailedAttempts() > 0L);
            if (!passwordPolicyList.isEmpty()) {
                CoreUserBean updateUser = new CoreUserBean();
                updateUser.setId(user.getId());
                if (bydayFailedAttemptPolicy) {
                    Long passwordAttempts = DefaultUserDetailsChecker.getUserService().selectLoginUserIncorrectPasswordAttempts(user.getId(), passwordPolicyList);
                    Long remainPasswordAttempts = passwordAttempts - 1L;
                    if (remainPasswordAttempts == 0L) {
                        updateUser.setStatus("locked");
                        DefaultUserDetailsChecker.getUserService().update(updateUser);
                        DefaultUserDetailsChecker.getUserService().setLoginUserIncorrectPasswordAttempts(user.getId(), remainPasswordAttempts);
                        DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(loginId, I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.LOCKED", new String[0]));
                        throw new LockedException("GIKAM.SECURITY.LOGIN.LOCKED");
                    }
                    if (remainPasswordAttempts > 0L) {
                        DefaultUserDetailsChecker.getUserService().setLoginUserIncorrectPasswordAttempts(user.getId(), remainPasswordAttempts);
                        throw new BadCredentialsException(I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.INCORRECT_PASSWORD_REMAIN_TIMES", String.valueOf(remainPasswordAttempts)));
                    }
                    throw new LockedException("GIKAM.SECURITY.LOGIN.LOCKED");
                }
                Long failedLoginAttempts = Optional.ofNullable(user.getFailedLoginAttempts()).orElse(0L);
                Long maxFaileldAttempts = passwordPolicyList.stream().mapToLong(p -> p.getMaxFailedAttempts()).min().orElse(0L);
                Long remainPasswordAttempts = maxFaileldAttempts - failedLoginAttempts - 1L;
                updateUser.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1L);
                if (remainPasswordAttempts <= 0L) {
                    updateUser.setStatus("locked");
                }
                DefaultUserDetailsChecker.getUserService().update(updateUser);
                if ("locked".equals(updateUser.getStatus())) {
                    DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(loginId, I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.LOCKED", new String[0]));
                    throw new LockedException("GIKAM.SECURITY.LOGIN.LOCKED");
                }
                throw new BadCredentialsException(I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.FAILEDATTEMPTPOLICY_TOTAL_INCORRECT_PASSWORD_REMAIN_TIMES", String.valueOf(remainPasswordAttempts)));
            }
        } else {
            DefaultUserDetailsChecker.getLoginLogService().insertLoginFailureLog(loginId, I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD", new String[0]));
        }
        throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
    }

    private static final CoreLoginLogService getLoginLogService() {
        if (loginLogService == null) {
            loginLogService = ApplicationContextHelper.getBean(CoreLoginLogService.class);
        }
        return loginLogService;
    }

    private static final CoreUserService getUserService() {
        if (userService == null) {
            userService = ApplicationContextHelper.getBean(CoreUserService.class);
        }
        return userService;
    }
}
