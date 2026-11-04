/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.service.impl;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartBean;
import com.sunwayworld.cloud.module.item.customchart.persistent.dao.CoreCustomChartDao;
import com.sunwayworld.cloud.module.item.customchart.service.CoreCustomChartService;
import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacepanel.service.CoreWorkspacePanelService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomChartServiceImpl
implements CoreCustomChartService {
    private static final String PUBLISH_TO_MENU_URL = "/secure/core/module/item/custom-charts/page/display/display-menu?id=";
    private static final String PUBLISH_TO_WKPANEL_URL = "/secure/core/module/item/custom-charts/page/display/panel?id=";
    @Autowired
    private CoreCustomChartDao coreCustomChartDao;
    @Autowired
    private CoreWorkspacePanelService coreWorkspacePanelService;
    @Autowired
    private CoreMenuService coreMenuService;
    @Lazy
    @Autowired
    private CoreCustomChartService proxyInstance;

    @Override
    public CoreCustomChartDao getDao() {
        return this.coreCustomChartDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomChartBean coreCustomChart = jsonWrapper.parseUnique(CoreCustomChartBean.class);
        coreCustomChart.setId(ApplicationContextHelper.getNextIdentity());
        coreCustomChart.setChartNo(ApplicationContextHelper.getNextSequence("T_CORE_CUSTOM_CHART"));
        coreCustomChart.setPublishStatus("noPublish");
        coreCustomChart.setVersion(1);
        this.getDao().insert(coreCustomChart);
        return coreCustomChart.getId();
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void revoke(RestJsonWrapperBean wrapper) {
        List<CoreCustomChartBean> customChartList = wrapper.parse(CoreCustomChartBean.class);
        ArrayList menuIdList = new ArrayList();
        ArrayList wkPanelIdList = new ArrayList();
        customChartList.forEach(e -> {
            String wkPanelId;
            e.setPublishStatus("noPublish");
            String menuId = e.getMenuId();
            if (!StringUtils.isEmpty(menuId)) {
                menuIdList.add(menuId);
                e.setMenuId("");
                e.setMenuName("");
            }
            if (!StringUtils.isEmpty(wkPanelId = e.getWkPanelId())) {
                wkPanelIdList.add(wkPanelId);
                e.setWkPanelId("");
            }
        });
        if (!menuIdList.isEmpty()) {
            this.coreMenuService.getDao().deleteByIdList(menuIdList);
        }
        if (!wkPanelIdList.isEmpty()) {
            this.coreWorkspacePanelService.getDao().deleteByIdList(wkPanelIdList);
        }
        this.getDao().update(customChartList, new String[]{"PUBLISHSTATUS", "PUBLISHGOAL", "VERSION", "MENUID", "WKPANELID", "MENUNAME"});
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void publishToMenu(RestJsonWrapperBean wrapper) {
        List<Long> custocmChartIdList = wrapper.parseId(Long.class);
        Long id = Long.parseLong(wrapper.getParamValue("id"));
        String parentMenuId = wrapper.getParamValue("parentMenuId");
        String menuId = wrapper.getParamValue("menuId");
        String menuIcon = wrapper.getParamValue("menuIcon");
        String chartName = wrapper.getParamValue("chartName");
        CoreCustomChartBean custocmChart = new CoreCustomChartBean();
        if (StringUtils.isEmpty(menuId)) {
            menuId = ApplicationContextHelper.getNextIdentity() + "";
            CoreMenuBean coreMenuBean = new CoreMenuBean();
            coreMenuBean.setId(menuId);
            coreMenuBean.setMenuInitName(chartName);
            coreMenuBean.setMenuName(chartName);
            coreMenuBean.setParentId(parentMenuId);
            coreMenuBean.setIcon(menuIcon);
            coreMenuBean.setHref(PUBLISH_TO_MENU_URL + id);
            this.coreMenuService.getDao().insert((CoreMenuBean)coreMenuBean);
        } else {
            CoreMenuBean coreMenuBean = (CoreMenuBean)this.coreMenuService.selectById(menuId);
            coreMenuBean.setHref(PUBLISH_TO_MENU_URL + id);
            this.coreMenuService.update(coreMenuBean);
        }
        custocmChart.setMenuId(menuId);
        custocmChart.setPublishStatus("publish");
        custocmChart.setPublishGoal("\u53d1\u5e03\u81f3\u83dc\u5355");
        custocmChart.setMenuName(chartName);
        this.getDao().updateByIds(custocmChart, custocmChartIdList, "MENUID", "PUBLISHSTATUS", "PUBLISHGOAL", "MENUNAME");
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void publishToWKPanel(RestJsonWrapperBean wrapper) {
        CoreCustomChartBean coreCustomChart = wrapper.parseUnique(CoreCustomChartBean.class);
        List<Long> custocmChartIdList = wrapper.parseId(Long.class);
        CoreCustomChartBean customChart = new CoreCustomChartBean();
        String WKPanelId = coreCustomChart.getWkPanelId();
        if (StringUtils.isEmpty(WKPanelId)) {
            CoreWorkspacePanelBean workspacePanelBean = new CoreWorkspacePanelBean();
            workspacePanelBean.setId(ApplicationContextHelper.getNextIdentity().toString());
            workspacePanelBean.setTitle(coreCustomChart.getChartName());
            workspacePanelBean.setUrl(PUBLISH_TO_WKPANEL_URL + coreCustomChart.getId());
            workspacePanelBean.setAuthorityControl("1");
            workspacePanelBean.setTitleDisplay("1");
            this.coreWorkspacePanelService.getDao().insert((CoreWorkspacePanelBean)workspacePanelBean);
            WKPanelId = workspacePanelBean.getId();
        } else {
            CoreWorkspacePanelBean workspacePanelBean = (CoreWorkspacePanelBean)this.coreWorkspacePanelService.selectById(WKPanelId);
            workspacePanelBean.setUrl(PUBLISH_TO_WKPANEL_URL + coreCustomChart.getId());
            this.coreWorkspacePanelService.update(workspacePanelBean);
            RedisHelper.evict("core-user-panel-config-layout");
        }
        customChart.setWkPanelId(WKPanelId);
        customChart.setPublishStatus("publish");
        customChart.setPublishGoal("\u53d1\u5e03\u81f3\u9996\u9875");
        customChart.setMenuName(null);
        this.getDao().updateByIds(customChart, custocmChartIdList, "WKPANELID", "PUBLISHSTATUS", "PUBLISHGOAL", "MENUNAME");
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void copy(RestJsonWrapperBean jsonWrapper) {
        List<CoreCustomChartBean> coreCustomChartList = jsonWrapper.parse(CoreCustomChartBean.class);
        ArrayList<CoreCustomChartBean> newCoreCustomChartList = new ArrayList<CoreCustomChartBean>();
        for (int i = 0; i < coreCustomChartList.size(); ++i) {
            CoreCustomChartBean oldCustomChart = coreCustomChartList.get(i);
            String chartName = oldCustomChart.getChartName();
            CoreCustomChartBean newCustomChart = new CoreCustomChartBean();
            Long id = ApplicationContextHelper.getNextIdentity();
            newCustomChart.setId(id);
            newCustomChart.setChartNo(ApplicationContextHelper.getNextSequence("T_CORE_CUSTOM_CHART"));
            newCustomChart.setChartName(chartName + "-\u590d\u5236");
            newCustomChart.setMenuId("");
            newCustomChart.setContent(oldCustomChart.getContent());
            newCustomChart.setPublishStatus("noPublish");
            newCustomChart.setWkPanelId("");
            newCustomChart.setVersion(1);
            newCoreCustomChartList.add(newCustomChart);
        }
        this.getDao().insert(newCoreCustomChartList);
    }

    @Override
    public CoreCustomChartBean selectDetail(Long id) {
        CoreCustomChartBean coreCustomChartBean = (CoreCustomChartBean)this.getDao().selectById(id);
        if (!StringUtils.isBlank(coreCustomChartBean.getContent())) {
            coreCustomChartBean.setContent(coreCustomChartBean.getContent().trim());
        }
        return coreCustomChartBean;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void modify(RestJsonWrapperBean jsonWrapper) {
        List<CoreCustomChartBean> coreCustomChartList = jsonWrapper.parse(CoreCustomChartBean.class);
        ArrayList<CoreCustomChartBean> newCoreCustomChartList = new ArrayList<CoreCustomChartBean>();
        for (int i = 0; i < coreCustomChartList.size(); ++i) {
            CoreCustomChartBean oldCustomChart = coreCustomChartList.get(i);
            String chartName = oldCustomChart.getChartName();
            CoreCustomChartBean maxVersionChartBean = (CoreCustomChartBean)this.proxyInstance.selectFirstByFilter(SearchFilter.instance().match("CHARTNAME", chartName).filter(MatchPattern.SEQ), Order.desc("VERSION"));
            CoreCustomChartBean newCustomChart = new CoreCustomChartBean();
            newCustomChart.setChartName(chartName);
            newCustomChart.setId(ApplicationContextHelper.getNextIdentity());
            newCustomChart.setChartNo(ApplicationContextHelper.getNextSequence("T_CORE_CUSTOM_CHART"));
            newCustomChart.setMenuId(oldCustomChart.getMenuId());
            newCustomChart.setContent(oldCustomChart.getContent());
            newCustomChart.setPublishStatus("noPublish");
            newCustomChart.setWkPanelId(oldCustomChart.getWkPanelId());
            newCustomChart.setPublishGoal(oldCustomChart.getPublishGoal());
            newCustomChart.setVersion(maxVersionChartBean.getVersion() + 1);
            newCustomChart.setMenuName(oldCustomChart.getMenuName());
            newCoreCustomChartList.add(newCustomChart);
        }
        this.getDao().insert(newCoreCustomChartList);
    }
}
