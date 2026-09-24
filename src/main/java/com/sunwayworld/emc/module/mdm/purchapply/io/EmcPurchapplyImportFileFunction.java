package com.sunwayworld.emc.module.mdm.purchapply.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchDetailBean;
import com.sunwayworld.emc.module.mdm.purchapply.service.EmcPurchDetailService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

/**
 * @author luoc@sunwayworld.com 2020/11/19
 */
@Component(value = "emcPurchapplyImportFileFunction")
public class EmcPurchapplyImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcPurchDetailService emcPurchDetailService;

    @Override
    @Transactional
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO fileImportResultDTO = new CoreFileImportResultDTO();
        String purchId = file.getTargetId().split("\\$")[1];
        importPurchDetail(CoreFileUtils.getLocalPath(file).toFile().getAbsolutePath(), purchId);
        return fileImportResultDTO;
    }

    private void importPurchDetail(String filePath, String purchId) {
        HSSFWorkbook wb = null;

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            wb = new HSSFWorkbook(is);

            // 第一张表格
            HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
            // 总行数
            int realRowCount = sheet.getPhysicalNumberOfRows();

            List<EmcPurchDetailBean> detailList = new ArrayList<>();// 详细信息

            List<String> allColumn = Stream.of("PURCHASENAME", "SPECIFICATION", "TECHNICALINDEX", "SUPPLIER", "NUMB","REMARK").collect(Collectors.toList());
            List<Map<String, Object>> detailMapList = new ArrayList<>();
            for (int i = 2; i < realRowCount; i++) {// 添加数据进map
                Row rowFirst = sheet.getRow(i);
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("id",ApplicationContextHelper.getNextIdentity());
                detailMap.put("purchId",Long.parseLong(purchId));
                for (int m = 0; m < 6; m++) {
                    detailMap.put(allColumn.get(m), ExcelHelper.getCellValue(rowFirst.getCell(m), String.class));
                }
                detailMapList.add(detailMap);
            }
            detailList =  detailMapList.stream().map(m -> PersistableHelper.mapToPersistable(m, EmcPurchDetailBean.class)).collect(Collectors.toList());
            emcPurchDetailService.getDao().insert(detailList);
        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (Exception ex) {

                }
            }
        }
    }

}
