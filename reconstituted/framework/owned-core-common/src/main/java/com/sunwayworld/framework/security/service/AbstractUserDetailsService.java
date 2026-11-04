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

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        CoreUserBean user = this.getLoginUser(loginId);
        return this.handleLoginUser(user);
    }

    protected abstract CoreUserBean getLoginUser(String var1);

    private UserDetails handleLoginUser(CoreUserBean user) {
        DefaultUserDetailsChecker.check(user);
        LoginUser loginUser = new LoginUser(user.getId(), user.getPassword());
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        loginUser.setOrgId(request.getParameter("orgId"));
        if (!StringUtils.isEmpty(loginUser.getOrgId())) {
            CoreOrgBean org = (CoreOrgBean)this.orgService.selectById(loginUser.getOrgId());
            loginUser.setOrgName(org.getOrgName());
        } else {
            String replaceSql;
            Map<String, Object> map;
            String selectSql = ApplicationContextHelper.getConstantValue("LOGIN_USER_SELECT_ORG_SQL");
            if (!StringUtils.isBlank(selectSql) && !(map = this.globalMapper.selectOne(replaceSql = StringUtils.replace(selectSql, "${userId}", user.getId()))).isEmpty()) {
                loginUser.setOrgId((String)CollectionUtils.getValueIgnorecase(map, "ORGID"));
                loginUser.setOrgName((String)CollectionUtils.getValueIgnorecase(map, "ORGNAME"));
            }
            if (StringUtils.isBlank(loginUser.getOrgId())) {
                loginUser.setOrgId(user.getOrgId());
                loginUser.setOrgName(user.getOrgName());
            }
        }
        loginUser.setRoleId(ConvertUtils.convert(request.getParameter("roleId"), Long.class));
        if (loginUser.getRoleId() != null) {
            CoreRoleBean role = (CoreRoleBean)this.roleService.selectById(loginUser.getRoleId());
            loginUser.setRoleName(role.getRoleName());
        }
        loginUser.setTenantId(ConvertUtils.convert(request.getParameter("tenantId"), Long.class));
        if (loginUser.getTenantId() != null) {
            CoreTenantBean tenant = (CoreTenantBean)this.tenantService.selectById(loginUser.getTenantId());
            loginUser.setTenantName(tenant.getTenantName());
        }
        return loginUser;
    }
}
