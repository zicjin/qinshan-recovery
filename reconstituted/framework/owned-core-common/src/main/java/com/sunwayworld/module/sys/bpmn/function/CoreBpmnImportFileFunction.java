/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.function;

import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnImportDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDraftService;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository(value="coreBpmnImportFileFunction")
public class CoreBpmnImportFileFunction
implements CoreFileImportFunction {
    @Autowired
    private CoreBpmnDraftService draftService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean coreFile) {
        Path path = CoreFileUtils.getLocalPath(coreFile);
        String json = FileUtils.extractTextFromFile(path.toFile());
        List<CoreBpmnImportDTO> bpmnImportList = JsonUtils.parseList(json, CoreBpmnImportDTO.class);
        if (bpmnImportList.isEmpty()) {
            return null;
        }
        this.draftService.importBpmn(bpmnImportList);
        return new CoreFileImportResultDTO();
    }
}
