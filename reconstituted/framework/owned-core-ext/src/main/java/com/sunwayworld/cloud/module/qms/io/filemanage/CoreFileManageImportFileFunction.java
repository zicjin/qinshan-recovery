/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.io.filemanage;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageAuthBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageBean;
import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageAuthService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageService;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageVersionService;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileTypeBean;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryRoleAuthService;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryService;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileTypeService;
import com.sunwayworld.cloud.module.qms.filevolume.bean.CoreFileVolumeBean;
import com.sunwayworld.cloud.module.qms.filevolume.service.CoreFileVolumeService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component(value="coreFileManageImportFileFunction")
public class CoreFileManageImportFileFunction
implements CoreFileImportFunction {
    @Autowired
    private CoreFileManageService coreFileManageService;
    @Autowired
    @Lazy
    private CoreFileManageVersionService coreFileManageVersionService;
    @Autowired
    @Lazy
    private CoreFileTypeService coreFileTypeService;
    @Autowired
    @Lazy
    private CoreFileCategoryService coreFileCategoryService;
    @Autowired
    @Lazy
    private CoreFileVolumeService coreFileVolumeService;
    @Autowired
    @Lazy
    private CoreFileCategoryRoleAuthService coreFileCategoryRoleAuthService;
    @Autowired
    @Lazy
    private CoreFileManageAuthService coreFileManageAuthService;
    @Autowired
    @Lazy
    private CoreUserService coreUserService;
    @Autowired
    @Lazy
    private CoreOrgService coreOrgService;
    @Autowired
    @Lazy
    private CoreCodeService coreCodeService;
    @Autowired
    @Lazy
    private CoreFileManager coreFileManager;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();
        FilePathDTO fr = FilePathDTO.of(file.getScope(), file.getCreatedTime(), file.getMd5Name(), file.getName());
        ImportResult importResult = this.extractDataFromExcel(this.coreFileManager.getDownloadUrl(fr).toString(), 0, 1, 2, 8, CoreFileManageBean.class, CoreFileManageVersionBean.class, "fileManageId");
        List<CoreFileManageBean> coreFileManageList = importResult.getCoreFileManageList();
        List<CoreFileManageVersionBean> coreFileManageVersionList = importResult.getCoreFileManageVersionList();
        if (coreFileManageList.size() > 0) {
            List<CoreUserBean> coreUserList = this.coreUserService.selectAll();
            List<CoreOrgBean> coreOrgList = this.coreOrgService.selectAll();
            List<CoreFileTypeBean> coreFileTypeList = this.coreFileTypeService.selectAll();
            List<CoreFileCategoryBean> coreFileCategoryList = this.coreFileCategoryService.selectAll();
            List<CoreFileVolumeBean> coreFileVolumeList = this.coreFileVolumeService.selectAll();
            List<CoreCodeBean> coreCodeList = this.coreCodeService.getDao().selectListByOneColumnValue((String)"fileActivatedFlag", "CODECATEGORYID", new Order[0]);
            HashMap fileTypeMap = new HashMap();
            coreFileTypeList.forEach(e -> fileTypeMap.put(e.getFileTypeName(), e.getId()));
            HashMap fileCategoryMap = new HashMap();
            coreFileCategoryList.forEach(e -> fileCategoryMap.put(e.getFileCategoryName(), e.getId()));
            HashMap fileVolumeMap = new HashMap();
            coreFileVolumeList.forEach(e -> fileVolumeMap.put(e.getFileVolumeName(), e.getId()));
            HashMap versionStateMap = new HashMap();
            coreCodeList.forEach(e -> versionStateMap.put(e.getCodeName(), e.getCode()));
            coreFileManageList.forEach(e -> {
                e.setFileTypeId((Long)fileTypeMap.get(e.getFileTypeName()));
                e.setFileCategoryId((Long)fileCategoryMap.get(e.getFileCategoryName()));
                e.setFileVolumeId(String.valueOf(fileVolumeMap.get(e.getFileVolumeName())));
                List<CoreFileCategoryRoleAuthBean> coreFileCategoryRoleAuthList = this.coreFileCategoryRoleAuthService.getDao().selectListByOneColumnValue((Long)e.getFileCategoryId(), "fileCattegoryId", new Order[0]);
                if (!coreFileCategoryRoleAuthList.isEmpty()) {
                    ArrayList coreFileManageAuthList = new ArrayList();
                    coreFileCategoryRoleAuthList.forEach(r -> {
                        CoreFileManageAuthBean coreFileManageAuth = new CoreFileManageAuthBean();
                        coreFileManageAuth.setId(ApplicationContextHelper.getNextIdentity());
                        coreFileManageAuth.setFileManageId(e.getId());
                        coreFileManageAuth.setSortor(r.getSortor());
                        coreFileManageAuth.setRoleId(r.getRoleId());
                        coreFileManageAuth.setRoleName(r.getRoleName());
                        coreFileManageAuth.setPrintFlag(r.getPrintFlag());
                        coreFileManageAuth.setReadFlag(r.getReadFlag());
                        coreFileManageAuthList.add(coreFileManageAuth);
                    });
                    this.coreFileManageAuthService.getDao().insert(coreFileManageAuthList);
                }
            });
            this.coreFileManageService.getDao().insert(coreFileManageList);
            if (coreFileManageVersionList.size() > 0) {
                List<Long> idList = ApplicationContextHelper.getNextIdentityList(coreFileManageVersionList.size());
                HashMap userMap = new HashMap();
                coreUserList.forEach(e -> userMap.put(e.getUserName(), e.getId()));
                HashMap orgMap = new HashMap();
                coreOrgList.forEach(e -> orgMap.put(e.getOrgName(), e.getId()));
                for (int i = 0; i < coreFileManageVersionList.size(); ++i) {
                    CoreFileManageVersionBean fileManageVersion = coreFileManageVersionList.get(i);
                    fileManageVersion.setId(idList.get(i));
                    fileManageVersion.setEditUserId((String)userMap.get(fileManageVersion.getEditUserName()));
                    fileManageVersion.setCheckUserId((String)userMap.get(fileManageVersion.getCheckUserName()));
                    fileManageVersion.setApproveUserId((String)userMap.get(fileManageVersion.getApproveUserName()));
                    fileManageVersion.setReceiveOrgId((String)orgMap.get(fileManageVersion.getReceiveOrgName()));
                    fileManageVersion.setArchiveOrgId((String)orgMap.get(fileManageVersion.getArchiveOrgName()));
                    fileManageVersion.setVersionState((String)versionStateMap.get(fileManageVersion.getVersionState()));
                }
                this.coreFileManageVersionService.getDao().insert(coreFileManageVersionList);
            }
        } else {
            String errorFileName = "\u6587\u4ef6\u7ba1\u7406\u5bfc\u5165\u5bfc\u5165\u9519\u8bef\u4fe1\u606f.xls";
            FilePathDTO pathDTO = ExcelHelper.writeImportFailureListAsExcel(this.coreFileManager.getDownloadUrl(fr), coreFileManageList, 1, 2, errorFileName);
            fileImportResultDTO.setUrl(this.coreFileManager.getDownloadUrl(pathDTO));
        }
        return fileImportResultDTO;
    }

    /*
     * Exception decompiling
     */
    private ImportResult extractDataFromExcel(String filePath, Object sheetIndexOrSheetName, int headerRowIndex, int dataFromRowIndex, int childColumnIndex, Class<CoreFileManageBean> masterClazz, Class<CoreFileManageVersionBean> childClazz, String masterPropertyName) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         *
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private static void updateValidationImpMsg(Map<String, Object> map, String msg) {
        String origMsg = (String)map.get("validateimpmsg");
        if (StringUtils.isEmpty(origMsg)) {
            map.put("validateimpmsg", msg);
        } else {
            map.put("validateimpmsg", origMsg + "\uff1b" + msg);
        }
    }

    private static /* synthetic */ CoreFileManageVersionBean lambda$extractDataFromExcel$9(Class childClazz, Map m) {
        return (CoreFileManageVersionBean)PersistableHelper.mapToPersistable(m, childClazz);
    }

    private static /* synthetic */ CoreFileManageBean lambda$extractDataFromExcel$8(Class masterClazz, Map m) {
        return (CoreFileManageBean)PersistableHelper.mapToPersistable(m, masterClazz);
    }

    public static class ImportResult {
        private List<CoreFileManageBean> coreFileManageList;
        private List<CoreFileManageVersionBean> coreFileManageVersionList;

        public List<CoreFileManageBean> getCoreFileManageList() {
            return this.coreFileManageList;
        }

        public void setCoreFileManageList(List<CoreFileManageBean> coreFileManageList) {
            this.coreFileManageList = coreFileManageList;
        }

        public List<CoreFileManageVersionBean> getCoreFileManageVersionList() {
            return this.coreFileManageVersionList;
        }

        public void setCoreFileManageVersionList(List<CoreFileManageVersionBean> coreFileManageVersionList) {
            this.coreFileManageVersionList = coreFileManageVersionList;
        }
    }
}
