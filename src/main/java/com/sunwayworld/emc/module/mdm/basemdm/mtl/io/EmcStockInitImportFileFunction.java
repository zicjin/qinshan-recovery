/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.mdm.basemdm.mtl.io;

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
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean.EmcMtlCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.EmcMtlCategoryService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Component(value="emcStockInitImportFileFunction")
public class EmcStockInitImportFileFunction
implements CoreFileImportFunction {
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
    @Autowired
    private EmcMtlCategoryService emcMtlCategoryService;
    private static final String LOG_TYPE_ADD = "\u6dfb\u52a0";

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean excelFile) {
        CoreFileImportResultDTO CoreFileImportResultDTO2 = new CoreFileImportResultDTO();
        FilePathDTO fr = CoreFileUtils.toFilePath(excelFile);
        List<EmcStockBean> stockList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 2, 3, EmcStockBean.class);
        if (stockList.isEmpty()) {
            return CoreFileImportResultDTO2;
        }
        try (InputStream is = new BufferedInputStream(new FileInputStream(FilePathManager.getLocalPath(fr).toString()));
             HSSFWorkbook wb = new HSSFWorkbook(is);){
            HSSFSheet firstSheet = wb.getSheetAt(0);
            int realRowCount = firstSheet.getPhysicalNumberOfRows();
            IntStream.range(3, realRowCount).forEach(i -> {
                ((EmcStockBean)stockList.get(i - 3)).setExt$Item("mtlname", ExcelHelper.getCellValue(firstSheet.getRow(i).getCell(0), String.class));
                ((EmcStockBean)stockList.get(i - 3)).setExt$Item("mtlcategoryname", ExcelHelper.getCellValue(firstSheet.getRow(i).getCell(1), String.class));
                String productionDate = ExcelHelper.getCellValue(firstSheet.getRow(i).getCell(12), String.class);
                if (productionDate.length() == 6 || productionDate.length() == 7) {
                    productionDate = productionDate + "-1";
                }
                ((EmcStockBean)stockList.get(i - 3)).setExt$Item("productionDate", DateTimeUtils.formatLocalDate(DateTimeUtils.parseLocalDate(productionDate)));
            });
        }
        catch (IOException e2) {
            throw new FileException(e2);
        }
        List<EmcMtlBean> insertEmcMtlList = new ArrayList<>();
        List<EmcStockBean> insertEmcStockList = new ArrayList<>();
        List<EmcStockLogBean> newStockLogList = new ArrayList<>();
        List<EmcStockConcentBean> newStockConcentList = new ArrayList<>();
        List<EmcMtlMaintainBean> insertEmcMtlMaintainList = new ArrayList<>();
        stockList.stream().filter(e -> e.getExt$Item("mtlname") != null).collect(Collectors.groupingBy(e -> e.getExt$Item("mtlname"))).forEach((mtlName, list) -> {
            list.stream().filter(e -> e.getMtlId() != null).collect(Collectors.groupingBy(e -> e.getMtlId())).forEach((mtlNo, mtlStockList) -> {
                EmcMtlBean mtl = new EmcMtlBean();
                mtl.setId(ApplicationContextHelper.getNextIdentity().toString());
                mtl.setMtlNo((String)mtlNo);
                mtl.setMtlName((String)mtlName);
                mtl.setExt$Item("mtlcategoryname", ((EmcStockBean)mtlStockList.get(0)).getExt$Item("mtlcategoryname"));
                mtlStockList.forEach(e -> e.setMtlId(mtl.getId()));
                insertEmcMtlList.add(mtl);
                insertEmcStockList.addAll(mtlStockList);
            });
            List<EmcStockBean> mtlStockList2 = list.stream().filter(e -> e.getMtlId() == null).collect(Collectors.toList());
            if (!mtlStockList2.isEmpty()) {
                EmcMtlBean mtl = new EmcMtlBean();
                mtl.setId(ApplicationContextHelper.getNextSequence(this.mtlService.getDao().getTable()));
                mtl.setMtlNo(mtl.getId());
                mtl.setMtlName((String)mtlName);
                mtl.setExt$Item("mtlcategoryname", ((EmcStockBean)mtlStockList2.get(0)).getExt$Item("mtlcategoryname"));
                mtlStockList2.forEach(e -> e.setMtlId(mtl.getId()));
                insertEmcMtlList.add(mtl);
                insertEmcStockList.addAll(mtlStockList2);
            }
        });
        for (EmcMtlBean emcMtl : insertEmcMtlList) {
            EmcMtlMaintainBean emcMtlMaintain = new EmcMtlMaintainBean();
            emcMtlMaintain.setCategory(emcMtl.getMtlName());
            emcMtlMaintain.setMtlName(emcMtl.getMtlName());
            emcMtlMaintain.setMtlId(emcMtl.getId());
            emcMtlMaintain.setId(ApplicationContextHelper.getNextIdentity());
            insertEmcMtlMaintainList.add(emcMtlMaintain);
        }
        insertEmcStockList.forEach(s -> {
            s.setId(ApplicationContextHelper.getNextIdentity());
            s.setStockQty(1.0);
            s.setStatus("0");
            EmcStockLogBean newStockLog = new EmcStockLogBean();
            newStockLog.setQty(1.0);
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
            insertEmcMtlMaintainList.stream().filter(m -> ObjectUtils.equals(m.getMtlId(), s.getMtlId())).findFirst().ifPresent(m -> {
                newStockConcent.setCategory(m.getCategory());
                newStockConcent.setMtlMaintainId(m.getId());
            });
            newStockConcent.setUncertainty(s.getExt$Item("UNCERTAINTY"));
            newStockConcent.setConcentUnitName(s.getExt$Item("CONCENTUNITNAME"));
            newStockConcent.setStdValue(s.getExt$Item("STDVALUE"));
            newStockConcent.setUncertaintyPercent(s.getExt$Item("UNCERTAINTYPERCENT"));
            newStockConcentList.add(newStockConcent);
        });
        List<EmcMtlCategoryBean> mtlCategoryList = this.emcMtlCategoryService.selectAll();
        Map<String, Long> mtlCategoryNameIdMap = mtlCategoryList.stream().collect(Collectors.toMap(EmcMtlCategoryBean::getMtlCategoryName, EmcMtlCategoryBean::getId));
        Map<String, Long> mtlStockSizeMap = insertEmcStockList.stream().collect(Collectors.groupingBy(EmcStockBean::getMtlId, Collectors.counting()));
        insertEmcMtlList.forEach(mtl -> {
            mtl.setStockQty((double)Optional.ofNullable(mtlStockSizeMap.get(mtl.getId())).orElse(0L).longValue() + Optional.ofNullable(mtl.getStockQty()).orElse(0.0));
            mtl.setMtlCategoryId((Long)mtlCategoryNameIdMap.get(mtl.getExt$Item("mtlcategoryname")));
        });
        this.mtlService.getDao().insert(insertEmcMtlList);
        this.stockService.getDao().insert(insertEmcStockList);
        this.stockLogService.getDao().insert(newStockLogList);
        this.stockConcentService.getDao().insert(newStockConcentList);
        this.mtlMaintainService.getDao().insert(insertEmcMtlMaintainList);
        return CoreFileImportResultDTO2;
    }
}
