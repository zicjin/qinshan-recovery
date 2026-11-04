/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.service.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileTypeBean;
import com.sunwayworld.cloud.module.qms.filetype.persistent.dao.CoreFileTypeDao;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryRoleAuthService;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryService;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileTypeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreFileTypeServiceImpl
implements CoreFileTypeService {
    @Autowired
    private CoreFileTypeDao coreFileTypeDao;
    @Autowired
    private CoreFileCategoryService coreFileCategoryService;
    @Autowired
    private CoreFileCategoryRoleAuthService coreFileCategoryRoleAuthService;
    @Autowired
    @Lazy
    private CoreFileService coreFileService;

    @Override
    public CoreFileTypeDao getDao() {
        return this.coreFileTypeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileTypeBean coreFileType = jsonWrapper.parseUnique(CoreFileTypeBean.class);
        List coreFileTypeList = this.selectList(new MapperParameter());
        Long sortor = coreFileTypeList.isEmpty() ? 1L : ((CoreFileTypeBean)coreFileTypeList.get(coreFileTypeList.size() - 1)).getSortor() + 1L;
        coreFileType.setId(ApplicationContextHelper.getNextIdentity());
        coreFileType.setSortor(sortor);
        coreFileType.setActivatedById(LocalContextHelper.getLoginUserId());
        coreFileType.setActivatedByName(LocalContextHelper.getLoginUserName());
        coreFileType.setActivatedTime(LocalDateTime.now());
        this.getDao().insert(coreFileType);
        return coreFileType.getId();
    }

    @Override
    public Page<CoreFileCategoryBean> selectCategoryPaginationByFileTypeId(Long id, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("FILETYPEID", id).filter(MatchPattern.EQ));
        return this.coreFileCategoryService.selectPagination(parameter);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertCategory(Long id, RestJsonWrapperBean wrapper) {
        CoreFileCategoryBean coreFileCategory = wrapper.parseUnique(CoreFileCategoryBean.class);
        List coreFileCategoryList = this.coreFileCategoryService.getDao().selectListByOneColumnValue((Long)id, "FILETYPEID", new Order[0]);
        Long sortor = coreFileCategoryList.isEmpty() ? 1L : ((CoreFileCategoryBean)coreFileCategoryList.get(coreFileCategoryList.size() - 1)).getSortor() + 1L;
        coreFileCategory.setId(ApplicationContextHelper.getNextIdentity());
        coreFileCategory.setFileTypeId(id);
        coreFileCategory.setSortor(sortor);
        coreFileCategory.setActivatedFlag("1");
        coreFileCategory.setActivatedById(LocalContextHelper.getLoginUserId());
        coreFileCategory.setActivatedByName(LocalContextHelper.getLoginUserName());
        coreFileCategory.setActivatedTime(LocalDateTime.now());
        this.coreFileCategoryService.getDao().insert((CoreFileCategoryBean)coreFileCategory);
        return coreFileCategory.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteCategory(Long id, RestJsonWrapperBean wrapper) {
        this.coreFileCategoryService.delete(wrapper);
    }

    @Override
    public Page<CoreFileCategoryRoleAuthBean> selectCategoryRoleAuthPaginationByFileTypeId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreFileCategoryRoleAuthService.selectPaginationByFilter(SearchFilter.instance().match("FILECATTEGORYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertCategoryRoleAuth(Long id, RestJsonWrapperBean wrapper) {
        List<CoreFileCategoryRoleAuthBean> coreFileCategoryRoleAuthList = wrapper.parse(CoreFileCategoryRoleAuthBean.class);
        List coreFileCategoryRoleAuthAllList = this.coreFileCategoryRoleAuthService.selectListByFilter(SearchFilter.instance().match("fileCattegoryId", id).filter(MatchPattern.EQ), new Order[0]);
        Long sortor = coreFileCategoryRoleAuthAllList.isEmpty() ? 0L : ((CoreFileCategoryRoleAuthBean)coreFileCategoryRoleAuthAllList.get(coreFileCategoryRoleAuthAllList.size() - 1)).getSortor();
        for (CoreFileCategoryRoleAuthBean e : coreFileCategoryRoleAuthList) {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setFileCattegoryId(id);
            sortor = sortor + 1L;
            e.setSortor(sortor);
        }
        this.coreFileCategoryRoleAuthService.getDao().insert(coreFileCategoryRoleAuthList);
        return coreFileCategoryRoleAuthList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteCategoryRoleAuth(Long id, RestJsonWrapperBean wrapper) {
        this.coreFileCategoryRoleAuthService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public List<CoreFileCategoryBean> copyCategoryDraftTemplate(Long id, RestJsonWrapperBean wrapper) {
        List<CoreFileCategoryBean> coreFileCategoryList = wrapper.parse(CoreFileCategoryBean.class);
        CoreFileCategoryBean coreFileCategory = (CoreFileCategoryBean)this.coreFileCategoryService.selectById(id);
        ArrayList targetIdList = new ArrayList();
        String targetPrefix = this.coreFileCategoryService.getDao().getTable() + "$";
        coreFileCategoryList.forEach(e -> {
            if (!coreFileCategory.getId().equals(e.getId())) {
                this.coreFileService.copy(targetPrefix + coreFileCategory.getId(), targetPrefix + e.getId());
            }
            targetIdList.add(targetPrefix + e.getId());
        });
        List coreFileList = this.coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR), new Order[0]);
        coreFileCategoryList.forEach(e -> e.setFileDraftTemplateId(this.getFileIdByTargetId(targetPrefix + e.getId(), coreFileList)));
        this.coreFileCategoryService.getDao().update(coreFileCategoryList, (String[])new String[0]);
        return coreFileCategoryList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.ACTIVATE)
    @Audit(value="AUDIT.ACTIVATE")
    public void activateFileCategory(RestJsonWrapperBean wrapper) {
        this.coreFileCategoryService.activate(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.ACTIVATE)
    @Audit(value="AUDIT.ACTIVATE")
    public void deactivateFileCategory(RestJsonWrapperBean wrapper) {
        this.coreFileCategoryService.deactivate(wrapper);
    }

    private Long getFileIdByTargetId(String targetId, List<CoreFileBean> coreFileList) {
        return coreFileList.stream().filter(f -> f.getTargetId().equals(targetId)).map(e -> e.getId()).findFirst().orElse(null);
    }
}
