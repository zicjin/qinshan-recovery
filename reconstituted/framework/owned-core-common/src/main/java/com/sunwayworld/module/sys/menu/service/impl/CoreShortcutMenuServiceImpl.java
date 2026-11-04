/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreShortcutMenuBean;
import com.sunwayworld.module.sys.menu.dao.CoreShortcutMenuDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuAccessLogService;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.menu.service.CoreShortcutMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRolePermissionBean;
import com.sunwayworld.module.sys.role.service.CoreRolePermissionService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreShortcutMenuServiceImpl
implements CoreShortcutMenuService {
    @Autowired
    protected CoreShortcutMenuDao coreShortcutMenuDao;
    @Lazy
    @Autowired
    private CoreRolePermissionService permissionService;
    @Autowired
    protected CoreMenuService coreMenuService;
    @Autowired
    private CoreMenuAccessLogService accessLogService;
    @Lazy
    @Autowired
    private CoreShortcutMenuService proxyInstance;

    @Override
    public CoreShortcutMenuDao getDao() {
        return this.coreShortcutMenuDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreShortcutMenuBean coreShortcutMenu = jsonWrapper.parseUnique(CoreShortcutMenuBean.class);
        coreShortcutMenu.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreShortcutMenu);
        return coreShortcutMenu.getId();
    }

    @Override
    public List<CoreShortcutMenuBean> selectCurrentUserAccessableList() {
        boolean admin;
        if (ApplicationContextHelper.stressTesting()) {
            return CollectionUtils.emptyList();
        }
        List<CoreShortcutMenuBean> shortcutMenuList = this.proxyInstance.selectListByUserId(LocalContextHelper.getLoginUserId());
        List<String> menuIdList = this.accessLogService.selectHighPriorityMenuIdList();
        if (!menuIdList.isEmpty()) {
            menuIdList.stream().filter(i -> shortcutMenuList.stream().noneMatch(m -> m.getMenuId().equals(i))).forEach(i -> {
                CoreShortcutMenuBean shortcutMenu = new CoreShortcutMenuBean();
                shortcutMenu.setMenuId((String)i);
                shortcutMenuList.add(shortcutMenu);
            });
        }
        if (shortcutMenuList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        List<CoreRolePermissionBean> permissionList = this.permissionService.selectActiveUserPermissionList(LocalContextHelper.getLoginUserId(), "T_CORE_MENU");
        if (LocalContextHelper.getLoginRoleId() != null) {
            permissionList.removeIf(p -> !p.getRoleId().equals(LocalContextHelper.getLoginRoleId()));
        }
        if (admin = permissionList.stream().anyMatch(p -> "0".equals(p.getTargetId()))) {
            return shortcutMenuList;
        }
        List permittedMenuIdList = permissionList.stream().map(p -> p.getTargetId()).distinct().collect(Collectors.toList());
        List<CoreShortcutMenuBean> permittedShortcutMenuList = shortcutMenuList.stream().filter(s -> permittedMenuIdList.contains(s.getMenuId())).collect(Collectors.toList());
        if (permittedShortcutMenuList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return permittedShortcutMenuList;
    }

    @Override
    @Cacheable(value={"T_CORE_SHORTCUT_MENU.BY_LOGIN_USER_ID"}, key="#userId", unless="#result == null")
    public List<CoreShortcutMenuBean> selectListByUserId(String userId) {
        return this.selectListByFilter(SearchFilter.instance().match("CREATEDBYID", userId).filter(MatchPattern.EQ), Order.asc("orderNo"));
    }

    @Override
    @Transactional
    public void moveMenu(Long id, int start, int end) {
        String loginUserId = LocalContextHelper.getLoginUserId();
        this.swapMenu(loginUserId, id, start, end);
    }

    @CachePut(value={"T_CORE_SHORTCUT_MENU.BY_LOGIN_USER_ID"}, key="#userId", unless="#result == null")
    public List<CoreShortcutMenuBean> swapMenu(String userId, Long id, int start, int end) {
        List<CoreShortcutMenuBean> coreShortcutMenuBeans = this.selectListByFilter(SearchFilter.instance().match("CREATEDBYID", userId).filter(MatchPattern.EQ), Order.asc("orderNo"));
        if (start >= coreShortcutMenuBeans.size() || end >= coreShortcutMenuBeans.size()) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.SYS.MENU.MOVE_MENU_PARAM_EXECTION_1", new String[0]));
        }
        if (id.longValue() != ((CoreShortcutMenuBean)coreShortcutMenuBeans.get(start)).getId().longValue()) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.SYS.MENU.MOVE_MENU_PARAM_EXECTION_2", new String[0]));
        }
        List orderNoList = coreShortcutMenuBeans.stream().map(t -> t.getOrderNo()).collect(Collectors.toList());
        CoreShortcutMenuBean coreShortcutMenuBean = (CoreShortcutMenuBean)coreShortcutMenuBeans.get(start);
        coreShortcutMenuBeans.remove(start);
        coreShortcutMenuBeans.add(end, coreShortcutMenuBean);
        ArrayList<CoreShortcutMenuBean> changeCoreShortcutMenuBeans = new ArrayList<CoreShortcutMenuBean>();
        for (int i = 0; i < orderNoList.size(); ++i) {
            if (((Long)orderNoList.get(i)).longValue() == ((CoreShortcutMenuBean)coreShortcutMenuBeans.get(i)).getOrderNo().longValue()) continue;
            coreShortcutMenuBeans.get(i).setOrderNo((Long)orderNoList.get(i));
            changeCoreShortcutMenuBeans.add(coreShortcutMenuBeans.get(i));
        }
        this.getDao().update(changeCoreShortcutMenuBeans, new String[]{"orderNo"});
        return coreShortcutMenuBeans;
    }
}
