/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.addomain.bean.CoreAdDomainUserBean;
import com.sunwayworld.module.sys.addomain.service.CoreAdDomainUserService;
import com.sunwayworld.module.sys.config.bean.CoreLoginConfigBean;
import com.sunwayworld.module.sys.config.service.CoreLoginConfigService;
import java.util.Hashtable;
import java.util.List;
import javax.naming.AuthenticationException;
import javax.naming.ldap.InitialLdapContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.BadCredentialsException;

public class AdDomainSsoAuthenticator
implements SsoAuthenticator {
    private static final Logger logger = LogManager.getLogger(AdDomainSsoAuthenticator.class);
    private static final String ADDOMAIN_PROVIDER_URL_PREFIX = "ldap://";

    protected Hashtable<String, String> buildAdDomainAuthEnv(CoreLoginConfigBean loginConfig, String adUserId) {
        if (StringUtils.isEmpty(loginConfig.getAdDomainServer())) {
            throw new BadCredentialsException("CORE.MODULE.SYS.LOGIN_CONFIGS.AD_DOMAIN_SERVER_NOT_NULL");
        }
        if (loginConfig.getAdDomainPort() == null) {
            throw new BadCredentialsException("CORE.MODULE.SYS.LOGIN_CONFIGS.AD_DOMAIN_PORT_NOT_NULL");
        }
        Hashtable<String, String> hashEnv = new Hashtable<String, String>();
        String upnSuffix = StringUtils.isBlank(loginConfig.getAdDomainUpnSuffix()) ? "" : loginConfig.getAdDomainUpnSuffix();
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String username = "1".equals(loginConfig.getAdUserIdIgnoreCase()) ? StringUtils.lowerCase(adUserId) + upnSuffix : adUserId + upnSuffix;
        String rawPassword = request.getParameter("password");
        String password = AuthenticationHelper.getRawPassword(rawPassword);
        hashEnv.put("java.naming.security.authentication", "simple");
        hashEnv.put("java.naming.security.principal", username);
        hashEnv.put("java.naming.security.credentials", password);
        hashEnv.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
        hashEnv.put("com.sun.jndi.ldap.connect.timeout", "3000");
        hashEnv.put("java.naming.provider.url", ADDOMAIN_PROVIDER_URL_PREFIX + loginConfig.getAdDomainServer() + ":" + loginConfig.getAdDomainPort());
        return hashEnv;
    }

    protected LoginUser postAuthSuccessWithoutUser(CoreUserBean adUser) {
        throw new BadCredentialsException("CORE.SECURITY.AD_LOGIN.NO_USER_EXISTS");
    }

    @Override
    public LoginUser authenticate() {
        CoreLoginConfigService loginConfigService = ApplicationContextHelper.getBean(CoreLoginConfigService.class);
        CoreLoginConfigBean loginConfig = loginConfigService.selectLoginConfig();
        CoreAdDomainUserService adUserService = ApplicationContextHelper.getBean(CoreAdDomainUserService.class);
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String filterLoginId = "1".equals(loginConfig.getAdUserIdIgnoreCase()) ? StringUtils.lowerCase(request.getParameter("id")) : request.getParameter("id");
        List adDomainUserList = adUserService.selectListByFilter(SearchFilter.instance().match(new String[]{"ID", "ADUSERID"}, filterLoginId).filter(MatchPattern.OR), new Order[0]);
        if (adDomainUserList.isEmpty()) {
            CoreUserBean user = new CoreUserBean();
            user.setId(filterLoginId);
            this.postAuthSuccessWithoutUser(user);
            return null;
        }
        CoreAdDomainUserBean adDomainUser = (CoreAdDomainUserBean)adDomainUserList.get(0);
        String adUserId = adDomainUser.getAdUserId();
        Hashtable<String, String> hashEnv = this.buildAdDomainAuthEnv(loginConfig, adUserId);
        InitialLdapContext ctx = null;
        try {
            ctx = new InitialLdapContext(hashEnv, null);
        }
        catch (AuthenticationException e) {
            logger.error("AD SSO login failed, AD params: {}, exception: {}", (Object)hashEnv.toString(), (Object)e);
            throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
        }
        catch (Exception ex) {
            logger.error("AD SSO login failed, AD params: {}, exception: {}", (Object)hashEnv.toString(), (Object)ex);
            throw new UnexpectedException(ex);
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                }
                catch (Exception exception) {}
            }
        }
        CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
        CoreUserBean user = (CoreUserBean)userService.selectByIdIfPresent(adDomainUser.getId());
        LoginUser loginUser = new LoginUser(user.getId(), user.getPassword());
        loginUser.setAdditionalCheck(false);
        return loginUser;
    }
}
