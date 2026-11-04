/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.helpcenter.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterBean;
import com.sunwayworld.module.admin.helpcenter.bean.CoreHelpCenterDTO;
import com.sunwayworld.module.admin.helpcenter.dao.CoreHelpCenterDao;
import com.sunwayworld.module.admin.helpcenter.service.CoreHelpCenterService;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreHelpCenterServiceImpl
implements CoreHelpCenterService {
    @Autowired
    private CoreHelpCenterDao coreHelpCenterDao;
    @Autowired
    private CoreFileService coreFileService;
    @Autowired
    @Lazy
    private CoreFileManager fileManager;

    @Override
    public CoreHelpCenterDao getDao() {
        return this.coreHelpCenterDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        CoreHelpCenterBean coreHelpCenter = jsonWrapper.parseUnique(CoreHelpCenterBean.class);
        coreHelpCenter.setId(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
        this.getDao().insert(coreHelpCenter);
        return coreHelpCenter.getId();
    }

    @Override
    public CoreHelpCenterBean selectDetail(String id) {
        CoreHelpCenterBean helpCenter = (CoreHelpCenterBean)this.getDao().selectByIdIfPresent(id);
        return Optional.ofNullable(helpCenter).orElseGet(() -> new CoreHelpCenterBean());
    }

    @Override
    @Cacheable(value={"T_CORE_HELP_CENTER"}, key="'ALL'")
    public List<CoreHelpCenterBean> selectAll() {
        return CoreHelpCenterService.super.selectAll();
    }

    @Override
    public CoreHelpCenterDTO selectHelpCenterDTOByMenuId(String id) {
        CoreHelpCenterBean helpCenter = (CoreHelpCenterBean)this.getDao().selectByIdIfPresent(id);
        if (helpCenter == null) {
            return null;
        }
        CoreHelpCenterDTO helpCenterDTO = new CoreHelpCenterDTO();
        BeanUtils.copyProperties((Object)helpCenter, (Object)helpCenterDTO, new String[0]);
        CoreFileBean manualVedioFile = (CoreFileBean)this.coreFileService.selectFirstByFilter(SearchFilter.instance().match("TARGETID", this.getDao().getTable() + "$" + id).filter(MatchPattern.EQ).match("BIZCATEGORY", "HELPCENTER-VEDIO").filter(MatchPattern.EQ), Order.desc("ID"));
        CoreFileBean manualDocFile = (CoreFileBean)this.coreFileService.selectFirstByFilter(SearchFilter.instance().match("TARGETID", this.getDao().getTable() + "$" + id).filter(MatchPattern.EQ).match("BIZCATEGORY", "HELPCENTER-DOC").filter(MatchPattern.EQ), Order.desc("ID"));
        if (manualDocFile != null) {
            helpCenterDTO.setManualDocFileId(manualDocFile.getId());
            helpCenterDTO.setManualDocFileDownloadUrl(this.fileManager.getDownloadUrl(manualDocFile));
            helpCenterDTO.setManualDocFileName(manualDocFile.getName());
        }
        if (manualVedioFile != null) {
            helpCenterDTO.setManualVideoFileId(manualVedioFile.getId());
            helpCenterDTO.setManualVideoFileDownloadUrl(this.fileManager.getDownloadUrl(manualVedioFile));
            helpCenterDTO.setManualVideoFileName(manualVedioFile.getName());
        }
        return helpCenterDTO;
    }
}
