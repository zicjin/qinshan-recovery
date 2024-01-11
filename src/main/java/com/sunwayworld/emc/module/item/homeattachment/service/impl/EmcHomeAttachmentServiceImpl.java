package com.sunwayworld.emc.module.item.homeattachment.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentBean;
import com.sunwayworld.emc.module.item.homeattachment.bean.EmcHomeAttachmentPermissionBean;
import com.sunwayworld.emc.module.item.homeattachment.dao.EmcHomeAttachmentDao;
import com.sunwayworld.emc.module.item.homeattachment.service.EmcHomeAttachmentPermissionService;
import com.sunwayworld.emc.module.item.homeattachment.service.EmcHomeAttachmentService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;

@Repository
@GikamBean
public class EmcHomeAttachmentServiceImpl implements EmcHomeAttachmentService {

    @Autowired
    private EmcHomeAttachmentDao emcHomeAttachmentDao;

    @Autowired
    private EmcHomeAttachmentPermissionService emcHomeAttachmentPermissionService;

    @Autowired
    private CoreFileService coreFileService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcHomeAttachmentDao getDao() {
        return emcHomeAttachmentDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcHomeAttachmentBean emcHomeAttachment = new EmcHomeAttachmentBean();
        emcHomeAttachment.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcHomeAttachment);
        return emcHomeAttachment.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean jsonWrapper) {
        EmcHomeAttachmentService.super.delete(jsonWrapper);

        List<EmcHomeAttachmentBean> attachmentList = jsonWrapper.parse(EmcHomeAttachmentBean.class);
        List<String> targetIdList = new ArrayList<>();
        attachmentList.forEach(e -> {
            targetIdList.add(EmcConstant.ATTACHMENT_TARGETID + String.valueOf(e.getId()));
        });
        List<CoreFileBean> fileList = coreFileService.getDao().selectListByOneColumnValues(targetIdList, "TARGETID");
        if (!fileList.isEmpty()) {
            fileList.stream().forEach(e -> {
                e.setTargetId(EmcConstant.ATTACHMENT_TARGETID_DELETE + e.getTargetId());
            });
            coreFileService.getDao().update(fileList, "TARGETID");
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertAttachmentPermission(RestJsonWrapperBean wrapper) {
        emcHomeAttachmentPermissionService.insert(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteAttachmentPermission(RestJsonWrapperBean wrapper) {
        emcHomeAttachmentPermissionService.delete(wrapper);
    }

    @Override
    public Page<EmcHomeAttachmentPermissionBean> selectPermissionPagination(Long id, RestJsonWrapperBean wrapper) {
        return emcHomeAttachmentPermissionService.selectPaginationByFilter(SearchFilter.instance().match("ATTACHMENTID", id).filter(MatchPattern.SEQ), wrapper);
    }

    @Override
    public String getDownloadUrl(Long id, RestJsonWrapperBean wrapper) {
        String targetId = EmcConstant.ATTACHMENT_TARGETID + String.valueOf(id);
        CoreFileBean coreFile = coreFileService.selectFirstByFilter(SearchFilter.instance().match("TARGETID", targetId).filter(MatchPattern.SEQ));
        String url = FileHelper.getUrl(FileRepresentation.of(coreFile));
        return url;
    }

    @Override
    @Transactional
    public void batchEmpower(RestJsonWrapperBean wrapper) {
        List<EmcHomeAttachmentBean> attachmentList = wrapper.parse(EmcHomeAttachmentBean.class);
        String param = wrapper.getParamMap().get("userId");

        List<Long> attachmentIdList = attachmentList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<String> pointIdList = ArrayUtils.asList(param.split(","));

        List<EmcHomeAttachmentPermissionBean> permissionList = emcHomeAttachmentPermissionService.getDao().selectListByOneColumnValues(attachmentIdList, "ATTACHMENTID");

        List<EmcHomeAttachmentPermissionBean> insertPermissionList = new ArrayList<>();
        if (!permissionList.isEmpty()) {
            Map<Long, List<EmcHomeAttachmentPermissionBean>> permissionGroupList = permissionList.stream().collect(Collectors.groupingBy(EmcHomeAttachmentPermissionBean::getAttachmentId));

            attachmentList.stream().forEach(e -> {
                List<String> compareList = new ArrayList<>(pointIdList);
                List<EmcHomeAttachmentPermissionBean> tempList = permissionGroupList.get(e.getId());
                if (!ObjectUtils.isEmpty(tempList)) {
                    compareList.removeIf(u -> tempList.stream().anyMatch(m -> m.getUserId().equals(u)));
                }
                compareList.stream().forEach(permission -> {
                    EmcHomeAttachmentPermissionBean insertPermission = new EmcHomeAttachmentPermissionBean();
                    insertPermission.setId(ApplicationContextHelper.getNextIdentity());
                    insertPermission.setAttachmentId(e.getId());
                    insertPermission.setUserId(permission);
                    insertPermissionList.add(insertPermission);
                });
            });
        } else {
            pointIdList.stream().forEach(userId -> {
                attachmentList.stream().forEach(permission -> {
                    EmcHomeAttachmentPermissionBean insertPermission = new EmcHomeAttachmentPermissionBean();
                    insertPermission.setId(ApplicationContextHelper.getNextIdentity());
                    insertPermission.setAttachmentId(permission.getId());
                    insertPermission.setUserId(userId);
                    insertPermissionList.add(insertPermission);
                });
            });
        }
        emcHomeAttachmentPermissionService.getDao().insert(insertPermissionList);
    }

}
