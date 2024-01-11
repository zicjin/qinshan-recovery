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
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlMaintainService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockLogService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;

/**
 * @author licm@sunwayworld.com 2021年03月13日
 */
@GikamBean
@Component(value = "pfStockImportFileFunction")
public class PfStockImportFileFunction extends EmcStockImportFileFunction {

    @Autowired
    private EmcMtlService mtlService;

    @Autowired
    private EmcStockService stockService;

    @Autowired
    private EmcMtlMaintainService mtlMaintainService;

    @Autowired
    private EmcStockConcentService stockConcentService;

    @Autowired
    private EmcStockLogService stockLogService;

    private static final String LOG_TYPE_ADD = "添加";

/*    *//** 浓度分隔符 *//*
    private static final String DELIMITER = "±";*/

    @Override
    @Transactional
    public ImportResultRepresentation apply(CoreFileBean excelFile) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();
        FileRepresentation fr = FileRepresentation.of(excelFile);

        List<EmcStockBean> stockList = ExcelHelper.readDataFromExcel(FileHelper.getAbsolutePath(fr).toString(), 2, 3, EmcStockBean.class);
        //模板只有材料名称
        stockList.forEach(e ->{
        	String mtlId =e.getMtlId();
            List<EmcMtlBean> mtlSelectIdList = mtlService.getDao().selectListByOneColumnValue(mtlId, "ID", Arrays.asList("ID", "MTLNAME"));
            e.setMtlId(mtlSelectIdList.get(0).getId());
        });
        if (stockList.isEmpty()) {
            return importResultRepresentation;
        }

        List<String> validateImpMsgList = stockList.stream().filter(stock -> !StringUtils.isEmpty(stock.getExt$Item("validateimpmsg"))).map(stock -> stock.getExt$Item("validateimpmsg")).collect(Collectors.toList());
        if (!validateImpMsgList.isEmpty()) {
            return importResultRepresentation;
        }

        List<String> batchNoList = stockList.stream().map(EmcStockBean::getBatchNo).collect(Collectors.toList());
        // 判断导入的唯一性编号是否重复
        boolean isRepeat = batchNoList.size() > batchNoList.stream().distinct().count();
        if (isRepeat) {
            importResultRepresentation.setMessage("EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.EXIST_REPEATABLE_BATCH_NO");
            return importResultRepresentation;
        }

        // 判断导入的唯一性编号与数据库中是否重复
        List<EmcStockBean> originalStockList = stockService.getDao().selectListByOneColumnValues(batchNoList, "BATCHNO", Collections.singletonList("ID"));
        if (!originalStockList.isEmpty()) {
            String repeatMtlIdList = " "+originalStockList.stream().map(EmcStockBean :: getMtlId).distinct().collect(Collectors.joining());
            importResultRepresentation.setMessage(repeatMtlIdList+"EMC.MODULE.MDM.BASEMDM.MTLS.STOCK.TIP.BATCH_NO_EXIST");
            return importResultRepresentation;
        }

        // 过滤掉数据库中不存在的材料数据
        List<String> mtlIdList = stockList.stream().map(EmcStockBean::getMtlId).distinct().collect(Collectors.toList());
        List<EmcMtlBean> mtlList = mtlService.getDao().selectListByOneColumnValues(mtlIdList, "ID", Arrays.asList("ID", "STOCKQTY"));
        List<String> validMtlIdList = mtlList.stream().map(EmcMtlBean::getId).collect(Collectors.toList());
        List<EmcStockBean> validStockList = stockList.stream().filter(s -> validMtlIdList.contains(s.getMtlId())).collect(Collectors.toList());
        if (validStockList.isEmpty()) {
            return importResultRepresentation;
        }

        List<EmcMtlMaintainBean> mtlMaintainList = mtlMaintainService.getDao().selectListByOneColumnValues(validMtlIdList, "MTLID");

        List<EmcStockLogBean> newStockLogList = new ArrayList<>(stockList.size());
        List<EmcStockConcentBean> newStockConcentList = new ArrayList<>();

        validStockList.forEach(s -> {
            s.setId(ApplicationContextHelper.getNextIdentity());
            s.setStockQty(1D);
            s.setStatus(Constant.NO);
           /* s.setBatchNo(s.getExt$Item("batchno"));
            s.setUnitName(s.getExt$Item("unitname"));
            s.setExpireDate( LocalDateTime.parse(s.getExt$Item("expiredate"),df));
            s.setStatus(s.getExt$Item("status"));*/

            EmcStockLogBean newStockLog = new EmcStockLogBean();
            
            newStockLog.setQty(1D);
            newStockLog.setId(ApplicationContextHelper.getNextIdentity());
            newStockLog.setVendorId(s.getExt$Item("vendorid"));
            newStockLog.setStockId(s.getId());
            newStockLog.setLogType(LOG_TYPE_ADD);
            newStockLog.setExt$Item("RECEIVETIME", s.getExt$Item("RECEIVETIME"));
            newStockLog.setExt$Item("SETVALUEDATE", s.getExt$Item("SETVALUEDATE"));
            newStockLog.setExt$Item("RECEIVEDBYNAME", s.getExt$Item("RECEIVEDBYNAME"));
            newStockLogList.add(newStockLog);

            EmcStockConcentBean newStockConcent = new EmcStockConcentBean();
            newStockConcent.setId(ApplicationContextHelper.getNextIdentity());
            newStockConcent.setStockId(s.getId());
            mtlMaintainList.stream().filter(m -> ObjectUtils.equals(m.getMtlId(), s.getMtlId())).findFirst().ifPresent(m -> {
                newStockConcent.setCategory(m.getCategory());
                newStockConcent.setMtlMaintainId(m.getId());
            });

            newStockConcent.setUncertainty(s.getExt$Item("UNCERTAINTY"));
            newStockConcent.setConcentUnitName(s.getExt$Item("CONCENTUNITNAME"));
            newStockConcent.setStdValue(s.getExt$Item("STDVALUE"));
            newStockConcent.setUncertaintyPercent(s.getExt$Item("UNCERTAINTYPERCENT"));
/*            if (!StringUtils.isBlank(concent)) {
                if (concent.contains(DELIMITER)) {
                    String[] pairs = concent.split(DELIMITER);
                    if (NumberUtils.isNumber(pairs[0].trim())) {
                        newStockConcent.setStdValue(pairs[0].trim());
                    }

                    if (pairs.length > 1) {
                        String[] arrays = pairs[1].split(" ");
                        if (NumberUtils.isNumber(arrays[0]) && arrays.length > 1) {
                            newStockConcent.setUncertainty(arrays[0]);
                            newStockConcent.setConcentUnitName(arrays[1]);
                            newStockConcent.setLowLimit(ArithUtils.sub(newStockConcent.getStdValue(), newStockConcent.getUncertainty()) + "");
                            newStockConcent.setHighLimit(ArithUtils.add(newStockConcent.getStdValue(), newStockConcent.getUncertainty()) + "");
                        }
                    }
                } else {
                    String[] pairs = concent.split(" ");
                    newStockConcent.setStdValue(pairs[0]);
                    if (pairs.length > 1) {
                        newStockConcent.setConcentUnitName(pairs[1]);
                    }
                }
            }*/

            newStockConcentList.add(newStockConcent);
        });

        Map<String, Long> mtlStockSizeMap = validStockList.stream().collect(Collectors.groupingBy(EmcStockBean::getMtlId, Collectors.counting()));
        mtlList.forEach(mtl -> mtl.setStockQty((double) Optional.ofNullable(mtlStockSizeMap.get(mtl.getId())).orElse(0L) + Optional.ofNullable(mtl.getStockQty()).orElse(0D)));

        stockService.getDao().insert(validStockList);
        stockLogService.getDao().insert(newStockLogList);
        stockConcentService.getDao().insert(newStockConcentList);
        mtlService.getDao().update(mtlList, "STOCKQTY");

        return importResultRepresentation;
    }

}
