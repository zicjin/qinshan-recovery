/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.login.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.user.bean.UserAccountDTO;
import com.sunwayworld.cloud.module.user.login.service.CoreLoginService;
import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.MapUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleUserBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import com.sunwayworld.module.sys.role.service.CoreRoleUserService;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreLoginServiceImpl
implements CoreLoginService {
    @Autowired
    private CoreUserService userService;
    @Autowired
    private CoreRoleService roleService;
    @Autowired
    private CoreRoleUserService roleUserService;
    @Autowired
    private CoreOrgService orgService;
    @Autowired
    private CoreLoginLogService loginLogService;
    @Autowired
    private CorePasswordPolicyService passwordPolicyService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserAccountDTO getAccount(String userId) {
        List<Map<String, Object>> maps;
        String defaultPassword;
        if (userId == null) {
            return null;
        }
        CoreUserBean user = (CoreUserBean)this.userService.selectById(userId);
        UserAccountDTO account = UserAccountDTO.of(user);
        if (LocalContextHelper.isTrusteeLogin()) {
            LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
            account.setTrusteeId(loginUser.getTrustee().getUsername());
            account.setUserName(LocalContextHelper.getLoginUserName());
            account.setExt$Item("trustee", loginUser.getTrustee().getUsername());
        }
        if (!StringUtils.isEmpty(LocalContextHelper.getLoginOrgId())) {
            account.setExt$Item("loginOrgId", LocalContextHelper.getLoginOrgId());
            account.setExt$Item("loginOrgName", LocalContextHelper.getLoginOrgName());
        }
        if (LocalContextHelper.getLoginRoleId() != null) {
            account.setExt$Item("loginRoleId", "" + LocalContextHelper.getLoginRoleId());
            account.setExt$Item("loginRoleName", LocalContextHelper.getLoginRoleName());
        }
        if (LocalContextHelper.getLoginTenantId() != null) {
            account.setExt$Item("loginTenantId", "" + LocalContextHelper.getLoginTenantId());
            account.setExt$Item("loginTenantName", LocalContextHelper.getLoginTenantName());
        }
        if (EncryptUtils.MD5Encrypt(user.getId()).equals(user.getPassword())) {
            account.setExt$Item("passwordValidation", "core.validator.user.passwordsamewithname");
        }
        if (ApplicationContextHelper.stressTesting()) {
            account.setExt$Item("loginTime", DateTimeUtils.formatLocalDateTime(LocalDateTime.now()));
        } else {
            CoreLoginLogBean loginLog = this.loginLogService.getCurrentLoginLog(userId);
            if (loginLog != null) {
                account.setExt$Item("loginTime", DateTimeUtils.formatLocalDateTime(loginLog.getLoginTime()));
            } else {
                account.setExt$Item("loginTime", DateTimeUtils.formatLocalDateTime(LocalDateTime.now()));
            }
        }
        account.setExt$Item("loginToken", AuthenticationHelper.getLoginUserToken());
        String string = defaultPassword = this.userService.defaultPassword() ? "1" : "0";
        if (!ApplicationContextHelper.stressTesting()) {
            String modifyDefaultPassword = ApplicationContextHelper.getConstantValue("INIT-PASSWORD-MODIFY");
            if ("1".equals(modifyDefaultPassword) && "1".equals(defaultPassword) || LocalContextHelper.getLoginPrincipal().isBadPassword()) {
                account.setExt$Item("defaultPassword", "1");
            } else {
                List<CorePasswordPolicyBean> passwordPolicyList = this.passwordPolicyService.selectEffectedList(userId);
                Long passwordExpireRemainingDays = this.passwordPolicyService.selectPasswordExpireRemainingDays(passwordPolicyList);
                if (passwordExpireRemainingDays != null && passwordExpireRemainingDays <= 0L) {
                    boolean expiredPolicyMandatory = passwordPolicyList.stream().anyMatch(f -> "mandatory".equals(f.getExpiredPolicy()));
                    if (expiredPolicyMandatory) {
                        account.setExt$Item("expiredPolicy", "mandatory");
                        if (passwordExpireRemainingDays < 0L) {
                            account.setExt$Item("expiredPasswordRemainingUses", "0");
                        } else {
                            account.setExt$Item("passwordexpireremainingdays", "0");
                        }
                    } else if (passwordExpireRemainingDays == 0L) {
                        account.setExt$Item("passwordExpireRemainingDays", "0");
                    } else {
                        Long policyPasswordExpiredMaxUse = this.passwordPolicyService.selectPasswordExpiredMaxUse(passwordPolicyList);
                        Long userExpiredPasswordUse = user.getExpiredPasswordUses();
                        Long expiredPasswordRemainingUses = null;
                        if (policyPasswordExpiredMaxUse != null && userExpiredPasswordUse != null) {
                            expiredPasswordRemainingUses = policyPasswordExpiredMaxUse - userExpiredPasswordUse;
                        }
                        if (expiredPasswordRemainingUses != null && expiredPasswordRemainingUses > 0L) {
                            account.setExt$Item("expiredPasswordRemainingUses", String.valueOf(expiredPasswordRemainingUses));
                        }
                    }
                } else {
                    account.setExt$Item("passwordExpireRemainingDays", String.valueOf(passwordExpireRemainingDays));
                }
            }
        }
        if (ApplicationContextHelper.stressTesting()) {
            account.setExt$Item("avatarUrl", MemoryCacheManager.get("AVATAR_URL." + userId, () -> this.userService.selectAvatarUrl(userId)));
            account.setExt$Item("signatureUrl", MemoryCacheManager.get("SIGNATURE_URL." + userId, () -> this.userService.selectSignatureUrl(userId)));
        } else {
            account.setExt$Item("avatarUrl", this.userService.selectAvatarUrl(userId));
            account.setExt$Item("signatureUrl", this.userService.selectSignatureUrl(userId));
        }
        List<CoreRoleUserBean> roleUserList = this.roleUserService.selectByUserId(userId);
        if (LocalContextHelper.getLoginRoleId() != null) {
            account.setExt$Item("roles", JSON.toJSONString(Arrays.asList(LocalContextHelper.getLoginRoleId())));
            account.setExt$Item("lcdpDesignFlag", Constant.ROLE_LCDP_ID.equals(LocalContextHelper.getLoginRoleId()) ? "1" : "0");
        } else {
            if (!roleUserList.isEmpty()) {
                account.setExt$Item("roles", JSON.toJSONString(roleUserList));
            }
            if (roleUserList.stream().anyMatch(e -> Constant.ROLE_LCDP_ID.equals(e.getRoleId()))) {
                account.setExt$Item("lcdpDesignFlag", "1");
            } else {
                account.setExt$Item("lcdpDesignFlag", "0");
            }
        }
        Object lcdpConfigParamServiceImpl = ApplicationContextHelper.getBean("LcdpConfigParamServiceImpl");
        if (lcdpConfigParamServiceImpl != null && !(maps = ApplicationContextHelper.getBean(MapDao.class).selectListByOneColumnValue("T_LCDP_CONFIG_PARAM", "appuuid", "PARAMCODE", new Order[0])).isEmpty()) {
            account.setExt$Item("appuuid", MapUtils.getString(maps.get(0), "PARAMVALUE"));
        }
        return account;
    }

    @Override
    public boolean checkAccount(RestJsonWrapperBean wrapper) {
        String username = wrapper.getParamValue("username");
        String rawPassword = wrapper.getParamValue("password");
        try {
            rawPassword = AuthenticationHelper.getRawPassword(rawPassword);
            DefaultUserDetailsChecker.checkPassword(username, this.passwordEncoder.encode(rawPassword));
            return true;
        }
        catch (AuthenticationException ae) {
            DefaultUserDetailsChecker.postAuthenticationFailure(username);
            return false;
        }
    }

    @Override
    public void updateLoginInfo(String orgId, @Nullable Long roleId) {
        Authentication authentication;
        String token = AuthenticationHelper.getLoginUserToken();
        if (!StringUtils.isEmpty(token) && (authentication = AuthenticationHelper.getCacheAuthentication(token)) != null) {
            LoginUser loginUser = (LoginUser)authentication.getPrincipal();
            loginUser.setOrgId(orgId);
            loginUser.setOrgName(((CoreOrgBean)this.orgService.selectById(orgId)).getOrgName());
            if (roleId != null) {
                loginUser.setRoleId(roleId);
                loginUser.setRoleName(((CoreRoleBean)this.roleService.selectById(roleId)).getRoleName());
            }
            AuthenticationHelper.updateCacheAuthentication(token, authentication);
        }
    }
}
