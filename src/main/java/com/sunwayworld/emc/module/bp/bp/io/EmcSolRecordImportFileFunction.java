package com.sunwayworld.emc.module.bp.bp.io;

import java.util.List;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolRecordService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author zhengxy@sunwayworld.com
 */
@Component(value = "emcSolRecordCoreFileImportFunction")
public class EmcSolRecordImportFileFunction implements CoreFileImportFunction {

    @Autowired
    @Lazy
    private EmcSolRecordService solRecordService;

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO importResultDto = new CoreFileImportResultDTO();

        FilePathDTO fr = CoreFileUtils.toFilePath(file);
        // 获取主表id
        Long solId = NumberUtils.parseLong(file.getTargetId().split("\\$")[1]);
        List<EmcSolRecordBean> solRecordBeanList = solRecordService.selectListByFilter(SearchFilter.instance().match("SOLID", solId).filter(MatchPattern.OR));
        int len = solRecordBeanList.size();
        List<EmcSolRecordBean> importSolRecord = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 1, 2, EmcSolRecordBean.class);
        for (int index = 0; index < importSolRecord.size(); index++) {
            EmcSolRecordBean item = importSolRecord.get(index);
            item.setId(ApplicationContextHelper.getNextIdentity());
            item.setTitrationNum(len + index + 1L);
            item.setSolId(solId);
            item.setUnitName("mL");
        }
        solRecordService.getDao().insert(importSolRecord);
        RestJsonWrapperBean wrapper = new RestJsonWrapperBean();
        wrapper.setFilterValue("solId",solId);
        solRecordService.calcResult(wrapper);
        return importResultDto;
    }
}
