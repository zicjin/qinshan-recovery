package com.sunwayworld.emc.module.mdm.file.file.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionDistBean;
import com.sunwayworld.emc.module.mdm.file.file.dao.EmcFileDao;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileService;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileVersionDistService;
import com.sunwayworld.emc.module.mdm.file.file.service.EmcFileVersionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileServiceImpl implements EmcFileService {
    @Autowired
    private EmcFileDao emcFileDao;
    @Autowired
    private EmcFileVersionService fileVersionService;
    @Autowired
    private EmcFileVersionDistService fileVersionDistService;
    @Autowired
    private CoreFileService coreFileService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFileDao getDao() {
        return emcFileDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcFileBean emcFileBean = wrapper.parseUnique(EmcFileBean.class);
        Long id =  ApplicationContextHelper.getNextIdentity();
        emcFileBean.setId(id);
        getDao().insert(emcFileBean);
        return id;
    }

    @Override
    public Page<EmcFileVersionBean> selectFileVersionPaginationByFileId(Long id, RestJsonWrapperBean wrapper) {
        return fileVersionService.selectPaginationByFilter(SearchFilter.instance().match("FILEID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<EmcFileVersionDistBean> selectFileVersionDistPaginationByVersionId(Long versionId, RestJsonWrapperBean wrapper) {
        return fileVersionDistService.selectPaginationByFilter(SearchFilter.instance().match("FILEVERSIONID", versionId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Long insertFileVersion(Long id, RestJsonWrapperBean wrapper) {
        return fileVersionService.insert(id, wrapper);
    }

    @Override
    public void deleteFileVersion(Long id, RestJsonWrapperBean wrapper) {
        fileVersionService.delete(wrapper);
    }

    @Override
    public Long insertFileVersionDist(Long versionId, RestJsonWrapperBean wrapper) {
        return fileVersionDistService.insert(versionId, wrapper);
    }

    @Override
    public void deleteFileVersionDist(Long versionId, RestJsonWrapperBean wrapper) {
        fileVersionDistService.delete(wrapper);
    }

    @Override
    public String getDownloadUrl(Long id, RestJsonWrapperBean wrapper) {
        CoreFileBean coreFile = coreFileService.selectById(id);
        return FileHelper.getUrl(FileRepresentation.of(coreFile));
    }
}
