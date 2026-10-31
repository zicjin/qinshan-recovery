package com.sunwayworld.emc.module.bp.io;

import java.util.List;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

@Component(value = "emcOrderTaskResultImportFileFunction")
public class EmcOrderTaskResultImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();

        try {
            // 导入数据录入结果明细
            List<EmcOrderTaskResultBean> EmcOrderTaskResultBeanList = ExcelHelper.readDataFromExcel(CoreFileUtils.getLocalPath(file).toString(), 1, 2, EmcOrderTaskResultBean.class);

            // 获取主表id
            // String trainPlanId = file.getTargetId().split("\\$")[1];
            if (EmcOrderTaskResultBeanList.size() > 0) {
                List<Long> idList = ApplicationContextHelper.getNextIdentityList(EmcOrderTaskResultBeanList.size());
                for (int i = 0; i < EmcOrderTaskResultBeanList.size(); i++) {
                    EmcOrderTaskResultBean EmcOrderTaskResultBean = EmcOrderTaskResultBeanList.get(i);
                    // 设置主键
                    EmcOrderTaskResultBean.setId(idList.get(i));
                    // 设置主表id
                    // EmcOrderTaskResultBean.setTrainPlanId(trainPlanId);
                    // 设置额外信息

                }
                orderTaskResultService.getDao().insert(EmcOrderTaskResultBeanList);
            } else {
                // 存在错误信息
                String errorFileName = "数据录入结果明细导入错误信息.xls";
                FilePathDTO pathDTO = ExcelHelper.writeImportFailureListAsExcel(CoreFileUtils.getLocalPath(file).toString(), EmcOrderTaskResultBeanList, 1, 2, errorFileName);
                fileImportResultDTO.setUrl(FilePathManager.getUrl(pathDTO));
            }
            return fileImportResultDTO;
        } catch (Exception e) {
            e.printStackTrace();
            fileImportResultDTO.setMessage(e.getCause().getMessage());
            return fileImportResultDTO;
        }

    }
}
