/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.service.impl;

import com.blueconic.browscap.Capabilities;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.AccountCategory;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.dao.CoreLoginLogDao;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLoginLogServiceImpl
implements CoreLoginLogService {
    @Autowired
    private CoreLoginLogDao loginLogDao;
    @Autowired
    @Lazy
    private CoreUserService userService;

    @Override
    public CoreLoginLogDao getDao() {
        return this.loginLogDao;
    }

    @Override
    public void insertLoginSuccessLog(String token, String userId, String orgId) {
        CoreUserBean userBean;
        if (ApplicationContextHelper.stressTesting()) {
            return;
        }
        CoreLoginLogBean loginLog = new CoreLoginLogBean();
        loginLog.setId(ApplicationContextHelper.getNextIdentity());
        loginLog.setToken(token);
        loginLog.setUserId(userId);
        loginLog.setStatus("1");
        if (StringUtils.isBlank(orgId) && (userBean = (CoreUserBean)this.userService.selectByIdIfPresent(userId)) != null) {
            orgId = userBean.getOrgId();
        }
        loginLog.setOrgId(orgId);
        loginLog.setLoginIp(ServletUtils.getRemoteIp(ServletUtils.getCurrentRequest()));
        loginLog.setLoginTime(LocalDateTime.now());
        this.setClientCapabilities(loginLog);
        this.getDao().insert(loginLog);
    }

    @Override
    public void insertLoginFailureLog(String userId, String remark) {
        if (ApplicationContextHelper.stressTesting()) {
            return;
        }
        CoreLoginLogBean loginLog = new CoreLoginLogBean();
        loginLog.setId(ApplicationContextHelper.getNextIdentity());
        loginLog.setUserId(userId);
        loginLog.setStatus("0");
        loginLog.setRemark(remark);
        loginLog.setLoginIp(ServletUtils.getRemoteIp(ServletUtils.getCurrentRequest()));
        loginLog.setLoginTime(LocalDateTime.now());
        this.setClientCapabilities(loginLog);
        this.getDao().insert(loginLog);
    }

    @Override
    public List<CoreLoginLogBean> selectOnlineUserLoginLogList(@Nullable String userId) {
        CoreLoginLogBean logSearchTerms = BeanUtils.getPropertyListenerProxy(CoreLoginLogBean.class);
        logSearchTerms.setLogoutTime(null);
        logSearchTerms.setStatus("1");
        if (!StringUtils.isBlank(userId)) {
            logSearchTerms.setUserId(userId);
        }
        return this.getDao().selectList(logSearchTerms, new Order[0]);
    }

    @Override
    public CoreLoginLogBean getCurrentLoginLog(String userId) {
        CoreLoginLogBean logSearchTerms = BeanUtils.getPropertyListenerProxy(CoreLoginLogBean.class);
        logSearchTerms.setToken(AuthenticationHelper.getLoginUserToken());
        logSearchTerms.setUserId(userId);
        logSearchTerms.setLogoutTime(null);
        logSearchTerms.setStatus("1");
        return this.getDao().selectOneIfPresent(logSearchTerms, new String[0]);
    }

    @Override
    public Page<CoreLoginLogBean> selectOnlinePagination(RestJsonWrapperBean wrapper) {
        Page<CoreLoginLogBean> loginLogPage = this.selectPaginationByFilter(SearchFilter.instance().match("LOGOUTTIME", null).filter(MatchPattern.EQ).match("STATUS", "1").filter(MatchPattern.EQ), wrapper);
        loginLogPage.getRows().forEach(e -> e.setToken(null));
        return loginLogPage;
    }

    @Override
    public void offline(RestJsonWrapperBean wrapper) {
        List<Long> loginLogIdList = wrapper.parseId(Long.class);
        List<CoreLoginLogBean> loginLogList = this.getDao().selectListByIds(loginLogIdList, new Order[0]);
        if (!loginLogList.isEmpty()) {
            String remark = "\u5f3a\u5236\u4e0b\u7ebf\uff08\u64cd\u4f5c\u4eba\uff1a" + LocalContextHelper.getLoginUserId() + "\uff09";
            loginLogList.forEach(l -> AuthenticationHelper.markOffline(l.getToken(), AccountCategory.OFFLINE.name()));
            loginLogList.forEach(l -> AuthenticationHelper.logout(l.getUserId(), l.getToken(), remark));
        }
    }

    @Override
    @Transactional
    public void assignLogout(String token, String remark) {
        if (ApplicationContextHelper.stressTesting()) {
            return;
        }
        if (StringUtils.isBlank(token)) {
            return;
        }
        CoreLoginLogBean filter = new CoreLoginLogBean();
        filter.setToken(token);
        CoreLoginLogBean loginLog = BeanUtils.getPropertyListenerProxy(this.getDao().selectOneIfPresent(filter, new String[0]));
        if (loginLog != null) {
            loginLog.setLogoutTime(LocalDateTime.now());
            loginLog.setRemark(remark);
            this.getDao().update(loginLog, new String[0]);
        }
    }

    private void setClientCapabilities(CoreLoginLogBean loginLog) {
        Capabilities capabilities = ServletUtils.getCurrentRequestCapabilities();
        if (capabilities != null) {
            loginLog.setDevice(capabilities.getDeviceType());
            StringBuilder browser = new StringBuilder();
            if (!StringUtils.isEmpty(capabilities.getBrowser())) {
                browser.append(capabilities.getBrowser());
            }
            if (!StringUtils.isEmpty(capabilities.getBrowserMajorVersion())) {
                if (browser.length() > 0) {
                    browser.append(" ");
                }
                browser.append(capabilities.getBrowserMajorVersion());
            }
            if (!StringUtils.isEmpty(capabilities.getBrowserType())) {
                if (browser.length() > 0) {
                    browser.append(" ");
                }
                browser.append(capabilities.getBrowserType());
            }
            loginLog.setBrowser(browser.toString());
            StringBuilder platform = new StringBuilder(capabilities.getPlatform());
            if (!StringUtils.isEmpty(capabilities.getPlatformVersion())) {
                platform.append(" ").append(capabilities.getPlatformVersion());
            }
            loginLog.setPlatform(platform.toString());
        }
    }
}
