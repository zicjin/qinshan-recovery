package com.sunwayworld.emc.module.bp.bp.io;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

/**
 * @author zhengxy@sunwayworld.com 2021年10月28日
 */
@Component(value = "pfOrderImportFileFunction")
public class PfOrderImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcOrderService orderService;

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(file);

        List<EmcOrderBean> orderList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 0, 1, EmcOrderBean.class);
        orderService.getDao().update(orderList, Arrays.asList("labelNo", "clientOrderName", "sampleAddress", "orderDesc", "volume", "remark","qcName","sender","checker","sendDate","packing","isblank"), "orderNo");

        return importResultRepresentation;
    }
}
