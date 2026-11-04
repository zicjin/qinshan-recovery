/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.ai.template.io;

import com.sunwayworld.cloud.module.ai.surport.ImageUtils;
import com.sunwayworld.cloud.module.ai.template.bean.CoreAiTemplateBean;
import com.sunwayworld.cloud.module.ai.template.service.CoreAiTemplateService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component(value="coreAiTemplateImportFileFunction")
public class CoreAiTemplateImportFileFunction
implements CoreFileImportFunction {
    @Autowired
    @Lazy
    private CoreAiTemplateService coreAiTemplateService;
    @Autowired
    @Lazy
    private CoreFileManager coreFileManager;
    @Autowired
    @Lazy
    private CoreFileService coreFileService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean coreFile) {
        if (!StringUtils.equalsIgnoreCase(coreFile.getFileExt(), "xlsx")) {
            throw new UnexpectedException("CORE.MODULE.AI.TEMPLATES.TIP.ONLY_SUPPORT_TEMPFILE_BY_XLSX");
        }
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();
        File tempFile = FileUtils.createTempFile();
        this.coreFileManager.download(coreFile, tempFile.toPath());
        List<CoreAiTemplateBean> coreAiTemplateList = this.extractDataFromExcel(tempFile.getAbsolutePath(), 0, 0, 1);
        if (coreAiTemplateList.size() > 0) {
            coreAiTemplateList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
            this.coreAiTemplateService.getDao().insert(coreAiTemplateList);
        } else {
            String errorFileName = "\u6587\u4ef6\u7ba1\u7406\u5bfc\u5165\u5bfc\u5165\u9519\u8bef\u4fe1\u606f.xls";
            FilePathDTO errorFilePathDTO = ExcelHelper.writeImportFailureListAsExcel(tempFile.getAbsolutePath(), coreAiTemplateList, 1, 2, errorFileName);
            fileImportResultDTO.setUrl(this.coreFileManager.getDownloadUrl(errorFilePathDTO));
        }
        FileUtils.deleteQuietly(tempFile);
        return fileImportResultDTO;
    }

    /*
     * Exception decompiling
     */
    private List<CoreAiTemplateBean> extractDataFromExcel(String filePath, Object sheetIndexOrSheetName, int headerRowIndex, int dataFromRowIndex) {
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

    private /* synthetic */ CoreAiTemplateBean lambda$extractDataFromExcel$1(Map m) {
        String base64 = (String)m.get("pic");
        String picName = (String)m.get("picName");
        File picFile = ImageUtils.base64ToFile(base64, picName);
        CoreFileBean coreFile = new CoreFileBean();
        coreFile.setTargetId("template$ocr");
        coreFile.setFileExt(FileUtils.getFileExtension(picName));
        coreFile.setScope("secure");
        Long fileId = this.coreFileService.upload(coreFile, picFile);
        coreFile = (CoreFileBean)this.coreFileService.selectById(fileId);
        m.remove("pic");
        m.put("fileId", fileId);
        m.put("picPath", this.coreFileManager.getDownloadUrl(coreFile));
        m.put("modifyTime", LocalDateTime.now());
        return PersistableHelper.mapToPersistable(m, CoreAiTemplateBean.class);
    }
}
