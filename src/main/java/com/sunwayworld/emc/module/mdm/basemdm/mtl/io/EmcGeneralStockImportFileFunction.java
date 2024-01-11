package com.sunwayworld.emc.module.mdm.basemdm.mtl.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

/**
 * 一般试剂导入
 * @author zhengxy@sunwayworld.com 2020年07月31日
 */
@Component(value = "emcGeneralStockImportFileFunction")
public class EmcGeneralStockImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcMtlService mtlService;

    @Autowired
    private EmcStockService stockService;

    @Autowired
    private EmcStockLogService stockLogService;

    private static final String LOG_TYPE_ADD = "添加";

    @Override
    public ImportResultRepresentation apply(CoreFileBean excelFile) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(excelFile);

        List<EmcStockBean> stockList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 2, 3, EmcStockBean.class);
        if (stockList.isEmpty()) {
            return importResultRepresentation;
        }

        List<String> validateImpMsgList = stockList.stream().filter(stock -> !StringUtils.isEmpty(stock.getExt$Item("validateimpmsg"))).map(stock -> stock.getExt$Item("validateimpmsg")).collect(Collectors.toList());
        if (!validateImpMsgList.isEmpty()) {
            return importResultRepresentation;
        }

        boolean loseStockQty = stockList.stream().anyMatch(stock -> ObjectUtils.isEmpty(stock.getStockQty()) || stock.getStockQty() < 1D);
        if (loseStockQty) {
            return importResultRepresentation;
        }

        // 过滤材料编码或者批号为空的数据
        List<EmcStockBean> notEmptyStockList = stockList.stream().filter(stock -> !StringUtils.isEmpty(stock.getMtlId()) && !StringUtils.isEmpty(stock.getBatchNo())).collect(Collectors.toList());
        if (notEmptyStockList.isEmpty()) {
            return importResultRepresentation;
        }

        List<String> batchNoList = notEmptyStockList.stream().map(EmcStockBean::getBatchNo).collect(Collectors.toList());
        // 判断导入的批号是否重复
        boolean isRepeat = batchNoList.size() > batchNoList.stream().distinct().count();
        if (isRepeat) {
            importResultRepresentation.setMessage("EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.EXIST_REPEATABLE_BATCH_NO2");
            return importResultRepresentation;
        }

        // 判断导入的唯一性编号与数据库中是否重复
        List<EmcStockBean> originalStockList = stockService.getDao().selectListByOneColumnValues(batchNoList, "BATCHNO", Collections.singletonList("ID"));
        if (!originalStockList.isEmpty()) {
            importResultRepresentation.setMessage("EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.BATCH_NO_EXIST2");
            return importResultRepresentation;
        }

        // 过滤掉数据库中不存在的材料数据
        List<String> mtlIdList = notEmptyStockList.stream().map(EmcStockBean::getMtlId).distinct().collect(Collectors.toList());
        List<EmcMtlBean> mtlList = mtlService.getDao().selectListByOneColumnValues(mtlIdList, "ID", Arrays.asList("ID", "STOCKQTY"));
        List<String> validMtlIdList = mtlList.stream().map(EmcMtlBean::getId).collect(Collectors.toList());
        List<EmcStockBean> validStockList = notEmptyStockList.stream().filter(s -> validMtlIdList.contains(s.getMtlId())).collect(Collectors.toList());
        if (validStockList.isEmpty()) {
            return importResultRepresentation;
        }

        List<EmcStockLogBean> newStockLogList = new ArrayList<>(notEmptyStockList.size());

        validStockList.forEach(s -> {
            s.setId(ApplicationContextHelper.getNextIdentity());
            s.setStatus(Constant.NO);

            EmcStockLogBean newStockLog = new EmcStockLogBean();
            newStockLog.setQty(s.getStockQty());
            newStockLog.setId(ApplicationContextHelper.getNextIdentity());
            newStockLog.setVendorId(s.getExt$Item("vendorid"));
            newStockLog.setStockId(s.getId());
            newStockLog.setLogType(LOG_TYPE_ADD);
            newStockLogList.add(newStockLog);
        });

        Map<String, Long> mtlStockSizeMap = validStockList.stream().collect(Collectors.groupingBy(EmcStockBean::getMtlId, Collectors.counting()));
        mtlList.forEach(mtl -> mtl.setStockQty((double) Optional.ofNullable(mtlStockSizeMap.get(mtl.getId())).orElse(0L) + Optional.ofNullable(mtl.getStockQty()).orElse(0D)));

        stockService.getDao().insert(validStockList);
        stockLogService.getDao().insert(newStockLogList);
        mtlService.getDao().update(mtlList, "STOCKQTY");

        return importResultRepresentation;
    }

}
