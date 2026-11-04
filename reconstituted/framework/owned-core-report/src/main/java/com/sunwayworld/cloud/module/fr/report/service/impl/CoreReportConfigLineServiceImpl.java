/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.service.impl;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineTreeDTO;
import com.sunwayworld.cloud.module.fr.report.persistent.dao.CoreReportConfigLineDao;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.tree.GroupingTreeDescriptor;
import com.sunwayworld.framework.support.tree.GroupingTreeHelper;
import com.sunwayworld.framework.support.tree.bean.AbstractTreeNode;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreReportConfigLineServiceImpl
implements CoreReportConfigLineService {
    @Autowired
    private CoreReportConfigLineDao coreReportConfigLineDao;
    @Autowired
    private CoreUserPermissionService coreUserPermissionService;

    @Override
    public CoreReportConfigLineDao getDao() {
        return this.coreReportConfigLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(String reportConfigId, RestJsonWrapperBean wrapper) {
        CoreReportConfigLineBean coreReportConfigLine = wrapper.parseUnique(CoreReportConfigLineBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        coreReportConfigLine.setId(id);
        coreReportConfigLine.setReportConfigId(reportConfigId);
        this.getDao().insert(coreReportConfigLine);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void delete(RestJsonWrapperBean wrapper) {
        List<CoreReportConfigLineBean> coreReportConfigLineList = wrapper.parse(CoreReportConfigLineBean.class);
        ArrayList deletePermissionList = new ArrayList();
        coreReportConfigLineList.forEach(c -> {
            CoreUserPermissionBean reportPermission = new CoreUserPermissionBean();
            reportPermission.setTargetId(c.getId().toString());
            reportPermission.setPermissionTypeId("T_CORE_REPORT_CONFIG_LINE");
            deletePermissionList.add(reportPermission);
        });
        this.coreUserPermissionService.getDao().deleteBy(deletePermissionList, new String[]{"permissionTypeId", "targetId"});
        CoreReportConfigLineService.super.delete(wrapper);
    }

    @Override
    public List<CoreReportConfigLineTreeDTO> selectLineTree(RestJsonWrapperBean jsonWrapper) {
        MapperParameter parameter = jsonWrapper.extractMapFilter();
        GroupingTreeDescriptor.NodeMaker<CoreReportConfigLineBean> rootNodeMaker = new GroupingTreeDescriptor.NodeMaker<CoreReportConfigLineBean>(){

            @Override
            public String getId(CoreReportConfigLineBean item) {
                return item.getReportConfigId();
            }

            @Override
            public String getText(CoreReportConfigLineBean item) {
                return item.getExt$Item("name");
            }
        };
        GroupingTreeDescriptor.NodeMaker<CoreReportConfigLineBean> subNodeMaker = new GroupingTreeDescriptor.NodeMaker<CoreReportConfigLineBean>(){

            @Override
            public String getId(CoreReportConfigLineBean item) {
                return item.getId().toString();
            }

            @Override
            public String getText(CoreReportConfigLineBean item) {
                return item.getReportName();
            }

            @Override
            public void copy(CoreReportConfigLineBean item, AbstractTreeNode node) {
                node.setExt$Item("filename", item.getExt$Item("filename"));
                node.setExt$Item("param", item.getParam());
                node.setExt$Item("defaultparam", item.getDefaultParam());
                node.setExt$Item("reportconfiglineno", item.getReportConfigLineNo());
                node.setExt$Item("reportcategory", item.getReportCategory());
            }
        };
        List coreReportConfigList = this.getDao().selectTreeList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, CoreReportConfigLineBean.class)).collect(Collectors.toList());
        GroupingTreeDescriptor descriptor = new GroupingTreeDescriptor(Arrays.asList(rootNodeMaker, subNodeMaker));
        List<CoreReportConfigLineTreeDTO> coreReportConfigLineTreeDTOList = GroupingTreeHelper.parseTreeNode(coreReportConfigList, descriptor, CoreReportConfigLineTreeDTO.class);
        GroupingTreeHelper.updateChildQty(coreReportConfigLineTreeDTOList);
        return coreReportConfigLineTreeDTOList;
    }
}
