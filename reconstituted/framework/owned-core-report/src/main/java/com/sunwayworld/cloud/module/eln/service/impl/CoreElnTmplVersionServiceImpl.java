/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnTmplVersionDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplVersionService;
import com.sunwayworld.cloud.module.eln.support.constant.CoreElnHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreElnTmplVersionServiceImpl
implements CoreElnTmplVersionService {
    @Autowired
    private CoreElnTmplVersionDao coreElnTmplVersionDao;
    @Autowired
    private CoreFileManager fileManager;
    @Autowired
    private CoreFileService fileService;

    @Override
    public CoreElnTmplVersionDao getDao() {
        return this.coreElnTmplVersionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        Long elnTempFileId;
        CoreElnTmplVersionBean newVersion = wrapper.parseUnique(CoreElnTmplVersionBean.class);
        newVersion.setId(ApplicationContextHelper.getNextIdentity());
        newVersion.setVersionNo(1);
        GikamConcurrentLocker.block(this.getDao().getTable());
        List<CoreElnTmplVersionBean> activatedVersionList = this.selectListByFilter(SearchFilter.instance().match("ELNTMPLID", newVersion.getElnTmplId()).filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ), Order.desc("ID"));
        if (!activatedVersionList.isEmpty()) {
            activatedVersionList.forEach(e -> e.setActivatedFlag("0"));
            this.getDao().update(activatedVersionList, new String[]{"ACTIVATEDFLAG"});
            CoreElnTmplVersionBean activatedVersion = (CoreElnTmplVersionBean)activatedVersionList.get(0);
            if (activatedVersion.getElnTmplFileId() == null) {
                Long elnTempFileId2;
                if (!StringUtils.isBlank(activatedVersion.getElnTmplContent()) && (elnTempFileId2 = this.uploadElnTempVersion(activatedVersion.getElnTmplContent(), newVersion)) != null) {
                    activatedVersion.setElnTmplFileId(elnTempFileId2);
                }
            } else {
                newVersion.setElnTmplFileId(activatedVersion.getElnTmplFileId());
            }
            if (!ObjectUtils.isEmpty(activatedVersion.getVersionNo())) {
                newVersion.setVersionNo(activatedVersion.getVersionNo() + 1);
            }
            newVersion.setElnTmplContent(null);
        } else if (!StringUtils.isBlank(newVersion.getElnTmplContent()) && (elnTempFileId = this.uploadElnTempVersion(newVersion.getElnTmplContent(), newVersion)) != null) {
            newVersion.setElnTmplFileId(elnTempFileId);
            newVersion.setElnTmplContent(null);
        }
        newVersion.setActivatedFlag("1");
        this.getDao().insert(newVersion);
        return newVersion.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void updateVersion(RestJsonWrapperBean wrapper) {
        CoreElnTmplVersionBean tmplVersion = wrapper.parseUnique(CoreElnTmplVersionBean.class);
        if (!StringUtils.isBlank(tmplVersion.getElnTmplContent())) {
            Long elnTempFileId = this.uploadElnTempVersion(tmplVersion.getElnTmplContent(), tmplVersion);
            if (elnTempFileId != null) {
                tmplVersion.setElnTmplFileId(elnTempFileId);
                tmplVersion.setElnTmplContent(null);
            }
            this.getDao().update(tmplVersion, new String[]{"ELNTMPLFILEID", "ELNTMPLCONTENT"});
        }
        if (!StringUtils.isBlank(tmplVersion.getElnTmplParam())) {
            this.getDao().update(tmplVersion, new String[]{"ELNTMPLPARAM"});
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public Long uploadElnTempVersion(String elnTmplContent, CoreElnTmplVersionBean tmplVersion) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(elnTmplContent.getBytes());){
            CoreFileBean file = CoreElnHelper.getInsertElnTmplVersionContentFile(tmplVersion);
            this.fileManager.upload(file, (InputStream)is);
            this.fileService.getDao().insert((CoreFileBean)file);
            Long l = file.getId();
            return l;
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }
}
