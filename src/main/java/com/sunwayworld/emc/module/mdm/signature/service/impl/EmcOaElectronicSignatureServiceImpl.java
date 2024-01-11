package com.sunwayworld.emc.module.mdm.signature.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.module.mdm.signature.bean.EmcOaElectronicSignatureBean;
import com.sunwayworld.emc.module.mdm.signature.dao.EmcOaElectronicSignatureDao;
import com.sunwayworld.emc.module.mdm.signature.service.EmcOaElectronicSignatureService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnCommentBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnCommentService;

/**
* @author zhengxy@sunwayworld.com 2020年12月30日
*/
@Repository
public class EmcOaElectronicSignatureServiceImpl implements EmcOaElectronicSignatureService {

    @Autowired
    private CoreFileService coreFileService;

    @Autowired
    private CoreBpmnCommentService bpmnCommentService;

    @Autowired
    private EmcOaElectronicSignatureDao emcOaElectronicSignatureDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOaElectronicSignatureDao getDao() {
        return emcOaElectronicSignatureDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcOaElectronicSignatureBean signature = wrapperBean.parseUnique(EmcOaElectronicSignatureBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        signature.setId(id);
        getDao().insert(signature);
        return id;
    }

    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcOaElectronicSignatureService.super.startProcess(wrapper);
        List<Long> signatureIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        List<String> targetIdList = signatureIdList.stream().map(id -> getDao().getTable() + "$" + id).collect(Collectors.toList());
        List<CoreFileBean> fileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("bizCategory", "signatureReport").filter(MatchPattern.EQ));

        List<CoreBpmnCommentBean> commentList = bpmnCommentService.selectListByFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("status", "REJECT").filter(MatchPattern.DIFFER));
        CoreBpmnCommentBean submitComment = commentList.stream().filter(e -> ObjectUtils.equals("Draft", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElseGet(CoreBpmnCommentBean::new);

        List<String> userIdList = commentList.stream().map(CoreBpmnCommentBean::getUserId).collect(Collectors.toList());
        List<String> userTargetIdList = userIdList.stream().map(userId -> "T_CORE_USER$" + userId).collect(Collectors.toList());
        List<CoreFileBean> userSignatureFileList = coreFileService.getDao().selectListByOneColumnValues(userTargetIdList, "targetId");

        fileList.forEach(file -> {
            File templateFile = FileHelper.getAbsolutePath(FileRepresentation.of(file.getScope(), DateTimeUtils.getDateSerialNumber(file.getCreatedTime()),
                    file.getMd5Name(), file.getName())).toFile();
            try (FileInputStream inputStream = new FileInputStream(templateFile);
                 XWPFDocument document = new XWPFDocument(inputStream);
                 FileOutputStream outputStream = new FileOutputStream(templateFile)) {

                // 编制人
                userSignatureFileList.stream().filter(signFile -> StringUtils.equals(signFile.getTargetId(), "T_CORE_USER$" + submitComment.getUserId())).findFirst().ifPresent(signFile -> {
                    String userSignatureFileUrl = FileHelper.getAbsolutePath(FileRepresentation.of(signFile.getScope(), DateTimeUtils.getDateSerialNumber(signFile.getCreatedTime()), signFile.getMd5Name(), signFile.getName())).toString();
                    if (!new File(userSignatureFileUrl).exists()) {
                        return;
                    }

                    Word2007Utils.insertImageOfKey("{editor}", document, userSignatureFileUrl);
                });

                document.write(outputStream);
            } catch (IOException e) {
                throw new FileException(e);
            }
        });

        saveFile(fileList);

        return coreBpmnInstanceStatusDTOList;
    }

    @Override
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = EmcOaElectronicSignatureService.super.completeTask(wrapper);
        List<Long> signatureIdList = coreBpmnInstanceStatusDTOList.stream().map(CoreBpmnInstanceStatusDTO::getId).collect(Collectors.toList());

        List<String> targetIdList = signatureIdList.stream().map(id -> getDao().getTable() + "$" + id).collect(Collectors.toList());
        List<CoreFileBean> fileList = coreFileService.selectListByFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("bizCategory", "signatureReport").filter(MatchPattern.EQ));

        List<CoreBpmnCommentBean> commentList = bpmnCommentService.selectListByFilter(SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("status", "REJECT").filter(MatchPattern.DIFFER));
        CoreBpmnCommentBean auditComment = commentList.stream().filter(e -> ObjectUtils.equals("audit", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElseGet(CoreBpmnCommentBean::new);
        CoreBpmnCommentBean approveComment = commentList.stream().filter(e -> ObjectUtils.equals("approve", e.getStatusCode()))
                .sorted(Comparator.comparing(CoreBpmnCommentBean::getCreatedTime).reversed()).limit(1).findFirst().orElseGet(CoreBpmnCommentBean::new);

        List<String> userIdList = commentList.stream().map(CoreBpmnCommentBean::getUserId).collect(Collectors.toList());
        List<String> userTargetIdList = userIdList.stream().map(userId -> "T_CORE_USER$" + userId).collect(Collectors.toList());
        List<CoreFileBean> userSignatureFileList = coreFileService.getDao().selectListByOneColumnValues(userTargetIdList, "targetId");

        fileList.forEach(file -> {
            File templateFile = FileHelper.getAbsolutePath(FileRepresentation.of(file.getScope(), DateTimeUtils.getDateSerialNumber(file.getCreatedTime()),
                    file.getMd5Name(), file.getName())).toFile();
            try (FileInputStream inputStream = new FileInputStream(templateFile);
                 XWPFDocument document = new XWPFDocument(inputStream);
                 FileOutputStream outputStream = new FileOutputStream(templateFile)) {

                // 审核人
                userSignatureFileList.stream().filter(signFile -> StringUtils.equals(signFile.getTargetId(), "T_CORE_USER$" + auditComment.getUserId())).findFirst().ifPresent(signFile -> {
                    String userSignatureFileUrl = FileHelper.getAbsolutePath(FileRepresentation.of(signFile.getScope(), DateTimeUtils.getDateSerialNumber(signFile.getCreatedTime()), signFile.getMd5Name(), signFile.getName())).toString();
                    if (!new File(userSignatureFileUrl).exists()) {
                        return;
                    }

                    Word2007Utils.insertImageOfKey("{auditor}", document, userSignatureFileUrl);
                });

                // 批准人
                userSignatureFileList.stream().filter(signFile -> StringUtils.equals(signFile.getTargetId(), "T_CORE_USER$" + approveComment.getUserId())).findFirst().ifPresent(signFile -> {
                    String userSignatureFileUrl = FileHelper.getAbsolutePath(FileRepresentation.of(signFile.getScope(), DateTimeUtils.getDateSerialNumber(signFile.getCreatedTime()), signFile.getMd5Name(), signFile.getName())).toString();
                    if (!new File(userSignatureFileUrl).exists()) {
                        return;
                    }

                    Word2007Utils.insertImageOfKey("{approver}", document, userSignatureFileUrl);
                });

                document.write(outputStream);
            } catch (IOException e) {
                throw new FileException(e);
            }
        });

        saveFile(fileList);

        return coreBpmnInstanceStatusDTOList;
    }

    private void saveFile(List<CoreFileBean> originalFileList) {
        List<CoreFileBean> newFileList = new ArrayList<>();
        originalFileList.forEach(f -> {
            CoreFileBean newFile = new CoreFileBean();
            BeanUtils.copyProperties(f, newFile);
            newFile.setId(ApplicationContextHelper.getNextIdentity());
            newFileList.add(newFile);

            f.setTargetId("X" + f.getTargetId());
        });

        coreFileService.getDao().update(originalFileList, "targetId");
        coreFileService.getDao().insert(newFileList);
    }
}
