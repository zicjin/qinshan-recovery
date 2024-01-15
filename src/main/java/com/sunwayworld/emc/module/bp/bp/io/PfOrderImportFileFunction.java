package com.sunwayworld.emc.module.bp.bp.io;

import java.util.Arrays;
import java.util.List;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;

/**
 * @author zhengxy@sunwayworld.com 2021年10月28日
 */
@Component(value = "pfOrderImportFileFunction")
public class PfOrderImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcOrderService orderService;

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();
        FilePathDTO fr = CoreFileUtils.toFilePath(file);

        List<EmcOrderBean> orderList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 0, 1, EmcOrderBean.class);
        orderService.getDao().update(orderList, Arrays.asList("labelNo", "clientOrderName", "sampleAddress", "orderDesc", "volume", "remark","qcName","sender","checker","sendDate","packing","isblank"), "orderNo");

        return CoreFileImportResultDTO;
    }
}
