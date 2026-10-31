/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.item.consume.consume.io;

import com.sunwayworld.emc.module.item.consume.consume.bean.EmcConsumeBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockBean;
import com.sunwayworld.emc.module.item.consume.consume.bean.EmcCsmStockLogBean;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcConsumeService;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockLogService;
import com.sunwayworld.emc.module.item.consume.consume.service.EmcCsmStockService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Component(value="emcConsumeImportFileFunction")
public class EmcConsumeImportFileFunction
implements CoreFileImportFunction {
    @Autowired
    private EmcConsumeService consumeService;
    @Autowired
    private EmcCsmStockService csmStockService;
    @Autowired
    private EmcCsmStockLogService csmStockLogService;
    private static final String LOG_TYPE_ADD = "\u6dfb\u52a0";

    @Override
    public boolean test(CoreFileBean coreFile, String service) {
        return true;
    }

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean excelFile) {
        CoreFileImportResultDTO importResultRepresentation = new CoreFileImportResultDTO();
        FilePathDTO fr = CoreFileUtils.toFilePath(excelFile);
        List<EmcConsumeBean> csmList = ExcelHelper.readDataFromExcel(FilePathManager.getLocalPath(fr).toString(), 1, 2, EmcConsumeBean.class);
        if (csmList.isEmpty()) {
            return importResultRepresentation;
        }
        List<String> productNoList = csmList.stream().filter(e -> !StringUtils.isBlank(e.getProductNo())).map(e -> e.getProductNo()).distinct().collect(Collectors.toList());
        List<String> mtlNameList = csmList.stream().filter(e -> StringUtils.isBlank(e.getProductNo()) && !StringUtils.isBlank(e.getMtlName())).map(e -> e.getMtlName()).distinct().collect(Collectors.toList());
        List<EmcConsumeBean> existcsmList = this.consumeService.getDao().selectListByOneColumnValues(productNoList, "productNo", new Order[0]);
        List<EmcConsumeBean> existcsmList2 = this.consumeService.getDao().selectListByOneColumnValues(mtlNameList, "mtlName", new Order[0]).stream().filter(e -> StringUtils.isBlank(e.getProductNo())).collect(Collectors.toList());
        List<EmcConsumeBean> insertCsmList = new ArrayList<EmcConsumeBean>();
        List<EmcConsumeBean> updateCsmList = new ArrayList<EmcConsumeBean>();
        List<EmcCsmStockBean> insertCsmStockList = new ArrayList<>();
        List<EmcCsmStockBean> updateCsmStockList = new ArrayList<>();
        List<EmcCsmStockLogBean> insertCsmLogList = new ArrayList<>();
        insertCsmList.addAll(csmList);
        updateCsmList.addAll(csmList);
        insertCsmList.removeIf(u -> existcsmList.stream().anyMatch(m -> StringUtils.equals(m.getProductNo(), u.getProductNo())));
        insertCsmList.removeIf(u -> existcsmList2.stream().anyMatch(m -> StringUtils.isBlank(u.getProductNo()) && StringUtils.equals(m.getMtlName(), u.getMtlName())));
        if (!insertCsmList.isEmpty()) {
            insertCsmList.forEach(e -> e.setId(ApplicationContextHelper.getNextSequence(this.consumeService.getDao().getTable())));
            insertCsmList.forEach(e -> {
                EmcCsmStockBean emcCsmStock = new EmcCsmStockBean();
                emcCsmStock.setId(ApplicationContextHelper.getNextIdentity());
                emcCsmStock.setStockQty(e.getStockQty());
                emcCsmStock.setMtlId(e.getId());
                emcCsmStock.setStatus("0");
                emcCsmStock.setBatchNo(e.getExt$Item("batchno"));
                insertCsmStockList.add(emcCsmStock);
                if (e.getStockQty() != null) {
                    EmcCsmStockLogBean emcCsmStockLog = new EmcCsmStockLogBean();
                    emcCsmStockLog.setId(ApplicationContextHelper.getNextIdentity());
                    emcCsmStockLog.setStockId(emcCsmStock.getId());
                    emcCsmStockLog.setLogType(LOG_TYPE_ADD);
                    emcCsmStockLog.setExt$Item("operateqty", e.getStockQty() + "");
                    emcCsmStockLog.setPreviousQty(0.0);
                    emcCsmStock.setStockQty(e.getStockQty());
                    emcCsmStockLog.setQty(emcCsmStock.getStockQty());
                    insertCsmLogList.add(emcCsmStockLog);
                }
            });
        }
        updateCsmList.removeIf(u -> insertCsmList.stream().anyMatch(m -> !StringUtils.isBlank(u.getProductNo()) && StringUtils.equals(m.getProductNo(), u.getProductNo()) || StringUtils.isBlank(u.getProductNo()) && StringUtils.isBlank(m.getProductNo()) && StringUtils.equals(m.getMtlName(), u.getMtlName())));
        updateCsmList.forEach(u -> {
            EmcConsumeBean csm = existcsmList.stream().filter(m -> StringUtils.equals(m.getProductNo(), u.getProductNo())).findFirst().orElse(null);
            if (csm == null) {
                csm = existcsmList2.stream().filter(m -> StringUtils.equals(m.getMtlName(), u.getMtlName())).findFirst().get();
            }
            u.setId(csm.getId());
            if (u.getStockQty() != null && u.getStockQty() != csm.getStockQty()) {
                EmcCsmStockBean filter = new EmcCsmStockBean();
                filter.setMtlId(csm.getId());
                EmcCsmStockBean emcCsmStock = this.csmStockService.getDao().selectFirstIfPresent((EmcCsmStockBean)filter, new Order[0]);
                if (emcCsmStock == null) {
                    emcCsmStock = new EmcCsmStockBean();
                    emcCsmStock.setId(ApplicationContextHelper.getNextIdentity());
                    emcCsmStock.setStockQty(u.getStockQty());
                    emcCsmStock.setMtlId(u.getId());
                    emcCsmStock.setStatus("0");
                    emcCsmStock.setBatchNo(u.getExt$Item("batchno"));
                    insertCsmStockList.add(emcCsmStock);
                    EmcCsmStockLogBean emcCsmStockLog = new EmcCsmStockLogBean();
                    emcCsmStockLog.setId(ApplicationContextHelper.getNextIdentity());
                    emcCsmStockLog.setStockId(emcCsmStock.getId());
                    emcCsmStockLog.setLogType(LOG_TYPE_ADD);
                    emcCsmStockLog.setExt$Item("operateqty", u.getStockQty() + "");
                    emcCsmStockLog.setPreviousQty(0.0);
                    emcCsmStock.setStockQty(u.getStockQty());
                    emcCsmStockLog.setQty(emcCsmStock.getStockQty());
                    insertCsmLogList.add(emcCsmStockLog);
                } else {
                    EmcCsmStockLogBean emcStockLogBean = new EmcCsmStockLogBean();
                    emcStockLogBean.setPreviousQty(emcCsmStock.getStockQty());
                    emcStockLogBean.setId(ApplicationContextHelper.getNextIdentity());
                    emcStockLogBean.setStockId(emcCsmStock.getId());
                    emcStockLogBean.setLogType("\u76d8\u5b58");
                    emcStockLogBean.setQty(u.getStockQty());
                    if (emcCsmStock.getStockQty() == null) {
                        emcStockLogBean.setExt$Item("operateqty", u.getStockQty() + "");
                    } else {
                        emcStockLogBean.setExt$Item("operateqty", u.getStockQty() - emcCsmStock.getStockQty() + "");
                    }
                    insertCsmLogList.add(emcStockLogBean);
                    emcCsmStock.setStockQty(u.getStockQty());
                    updateCsmStockList.add(emcCsmStock);
                }
            }
        });
        this.consumeService.getDao().insert(insertCsmList);
        this.consumeService.getDao().update(updateCsmList, new String[0]);
        this.csmStockService.getDao().insert(insertCsmStockList);
        this.csmStockService.getDao().update(updateCsmStockList, new String[0]);
        this.csmStockLogService.getDao().insert(insertCsmLogList);
        return importResultRepresentation;
    }
}
