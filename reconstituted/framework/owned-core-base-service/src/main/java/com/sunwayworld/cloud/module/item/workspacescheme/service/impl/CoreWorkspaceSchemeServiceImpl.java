/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.workspacescheme.service.impl;

import com.sunwayworld.cloud.module.item.workspacepanel.bean.CoreWorkspacePanelBean;
import com.sunwayworld.cloud.module.item.workspacepanel.service.CoreWorkspacePanelService;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemeBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigBean;
import com.sunwayworld.cloud.module.item.workspacescheme.bean.CoreWorkspaceSchemePanelConfigRepresentation;
import com.sunwayworld.cloud.module.item.workspacescheme.persistent.dao.CoreWorkspaceSchemeDao;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceSchemePanelConfigService;
import com.sunwayworld.cloud.module.item.workspacescheme.service.CoreWorkspaceSchemeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreWorkspaceSchemeServiceImpl
implements CoreWorkspaceSchemeService {
    @Autowired
    private CoreWorkspaceSchemeDao schemeDao;
    @Autowired
    private CoreWorkspacePanelService panelService;
    @Autowired
    private CoreWorkspaceSchemePanelConfigService schemePanelConfigService;

    @Override
    public CoreWorkspaceSchemeDao getDao() {
        return this.schemeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreWorkspaceSchemeBean coreWorkspaceScheme = jsonWrapper.parseUnique(CoreWorkspaceSchemeBean.class);
        coreWorkspaceScheme.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreWorkspaceScheme);
        return coreWorkspaceScheme.getId();
    }

    @Override
    @Transactional
    public void savePanelConfig(Long id, RestJsonWrapperBean wrapper) {
        List schemePanelConfigList = this.schemePanelConfigService.selectListByFilter(SearchFilter.instance().match("SCHEMEID", id).filter(MatchPattern.EQ), new Order[0]);
        this.schemePanelConfigService.getDao().deleteBy(schemePanelConfigList, new String[0]);
        List<CoreWorkspaceSchemePanelConfigBean> newSchemePanelConfigList = wrapper.parse(CoreWorkspaceSchemePanelConfigBean.class);
        newSchemePanelConfigList.forEach(c -> {
            c.setId(ApplicationContextHelper.getNextIdentity());
            c.setSchemeId(id);
        });
        this.schemePanelConfigService.getDao().insert(newSchemePanelConfigList);
    }

    @Override
    public CoreWorkspaceSchemePanelConfigRepresentation selectSchemePanelConfigById(Long id) {
        List<CoreWorkspacePanelBean> panelList = this.panelService.selectListByFilter(SearchFilter.instance().match("LASTSUSPENDEDFLAG", "0").filter(MatchPattern.EQ), new Order[0]);
        List<CoreWorkspaceSchemePanelConfigBean> schemeConfigList = this.schemePanelConfigService.selectListByFilter(SearchFilter.instance().match("SCHEMEID", id).filter(MatchPattern.EQ), new Order[0]);
        CoreWorkspaceSchemePanelConfigRepresentation representation = new CoreWorkspaceSchemePanelConfigRepresentation();
        representation.setSystemPanelList(panelList);
        representation.setPanelList(schemeConfigList);
        return representation;
    }

    @Override
    public List<CoreWorkspaceSchemeBean> selectAuthorizedList() {
        List<Map<String, Object>> mapList;
        MapperParameter parameter = new MapperParameter();
        parameter.put("authorized", "1");
        if (LocalContextHelper.getLoginRoleId() != null) {
            parameter.put("loginRoleId", LocalContextHelper.getLoginRoleId());
        }
        if ((mapList = this.schemeDao.selectByCondition(parameter)).isEmpty()) {
            return Collections.emptyList();
        }
        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, CoreWorkspaceSchemeBean.class)).collect(Collectors.toList());
    }
}
