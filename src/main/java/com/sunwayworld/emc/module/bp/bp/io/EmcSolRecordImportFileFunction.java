package com.sunwayworld.emc.module.bp.bp.io;

import java.util.List;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.sol.bean.EmcSolRecordBean;
import com.sunwayworld.emc.module.bp.sol.service.EmcSolRecordService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

/**
 * @author zhengxy@sunwayworld.com
 */
@Component(value = "emcSolRecordImportFileFunction")
public class EmcSolRecordImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcSolRecordService solRecordService;

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);
        // 获取主表id
        Long solId = NumberUtils.parseLong(file.getTargetId().split("\\$")[1]);
        List<EmcSolRecordBean> solRecordBeanList = solRecordService.selectListByFilter(SearchFilter.instance().match("SOLID", solId).filter(MatchPattern.OR));
        int len = solRecordBeanList.size();
        List<EmcSolRecordBean> importSolRecord = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 1, 2, EmcSolRecordBean.class);
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
        return importResultRepresentation;
    }
}
