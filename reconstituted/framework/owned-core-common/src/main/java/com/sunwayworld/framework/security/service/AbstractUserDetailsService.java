/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.service;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.tenant.bean.CoreTenantBean;
import com.sunwayworld.module.item.tenant.service.CoreTenantService;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractUserDetailsService
implements UserDetailsService {
    @Autowired
    @Lazy
    protected CoreOrgService orgService;
    @Autowired
    @Lazy
    protected CoreRoleService roleService;
    @Autowired
    @Lazy
    protected CoreTenantService tenantService;
    @Autowired
    private GlobalMapper globalMapper;

    protected CoreOrgService getOrgService() {
        if (this.orgService == null) {
            this.orgService = ApplicationContextHelper.getBeanIfPresent(CoreOrgService.class);
        }
        return this.orgService;
    }

    protected CoreRoleService getRoleService() {
        if (this.roleService == null) {
            this.roleService = ApplicationContextHelper.getBeanIfPresent(CoreRoleService.class);
        }
        return this.roleService;
    }

    protected CoreTenantService getTenantService() {
        if (this.tenantService == null) {
            this.tenantService = ApplicationContextHelper.getBeanIfPresent(CoreTenantService.class);
        }
        return this.tenantService;
    }

    protected GlobalMapper getGlobalMapper() {
        if (this.globalMapper == null) {
            this.globalMapper = ApplicationContextHelper.getBeanIfPresent(GlobalMapper.class);
        }
        return this.globalMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        if (StringUtils.isBlank(loginId)) {
            throw new UsernameNotFoundException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
        }
        CoreUserBean user = this.getLoginUser(loginId);
        return this.handleLoginUser(user);
    }

    protected abstract CoreUserBean getLoginUser(String var1);

    private UserDetails handleLoginUser(CoreUserBean user) {
        DefaultUserDetailsChecker.check(user);
        LoginUser loginUser = new LoginUser(user.getId(), user.getPassword());
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        loginUser.setOrgId(request != null ? request.getParameter("orgId") : null);
        if (!StringUtils.isEmpty(loginUser.getOrgId())) {
            CoreOrgService os = this.getOrgService();
            CoreOrgBean org = os != null ? (CoreOrgBean)os.selectById(loginUser.getOrgId()) : null;
            if (org != null) {
                loginUser.setOrgName(org.getOrgName());
            }
        } else {
            String replaceSql;
            Map<String, Object> map;
            String selectSql = ApplicationContextHelper.getConstantValue("LOGIN_USER_SELECT_ORG_SQL");
            GlobalMapper gm = this.getGlobalMapper();
            if (!StringUtils.isBlank(selectSql) && gm != null && !(map = gm.selectOne(replaceSql = StringUtils.replace(selectSql, "${userId}", user.getId()))).isEmpty()) {
                loginUser.setOrgId((String)CollectionUtils.getValueIgnorecase(map, "ORGID"));
                loginUser.setOrgName((String)CollectionUtils.getValueIgnorecase(map, "ORGNAME"));
            }
            if (StringUtils.isBlank(loginUser.getOrgId())) {
                loginUser.setOrgId(user.getOrgId());
                loginUser.setOrgName(user.getOrgName());
            }
        }
        loginUser.setRoleId(ConvertUtils.convert(request != null ? request.getParameter("roleId") : null, Long.class));
        if (loginUser.getRoleId() != null) {
            CoreRoleService rs = this.getRoleService();
            CoreRoleBean role = rs != null ? (CoreRoleBean)rs.selectById(loginUser.getRoleId()) : null;
            if (role != null) {
                loginUser.setRoleName(role.getRoleName());
            }
        }
        loginUser.setTenantId(ConvertUtils.convert(request != null ? request.getParameter("tenantId") : null, Long.class));
        if (loginUser.getTenantId() != null) {
            CoreTenantService ts = this.getTenantService();
            CoreTenantBean tenant = ts != null ? (CoreTenantBean)ts.selectById(loginUser.getTenantId()) : null;
            if (tenant != null) {
                loginUser.setTenantName(tenant.getTenantName());
            }
        }
        return loginUser;
    }
}
