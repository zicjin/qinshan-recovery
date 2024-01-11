package com.sunwayworld.emc.module.bp.bp.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component(value = "emcOrderImportFileFunction")
public class EmcOrderImportFileFunction implements ImportFileFunction {

    @Autowired
    private EmcOrderService orderService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);
        importOrderInfoByPOI(FileHelper.getAbsolutePath(fr).toFile().getAbsolutePath());
        return importResultRepresentation;
    }

    public void importOrderInfoByPOI(String filePath) {
        HSSFWorkbook wb = null;

        try {
            InputStream is = new BufferedInputStream(new FileInputStream(filePath));
            wb = new HSSFWorkbook(is);

            // 第一张表格
            HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
            // 总行数
            int realRowCount = sheet.getPhysicalNumberOfRows();

            // 开始取数据
            List<String> orderNoList = new ArrayList<>();
            for (int i = 2; i <= realRowCount; i++) {
                Row row = sheet.getRow(i);
                Cell orderNoCell = row.getCell(0);
                String orderNo = ExcelHelper.getCellValue(orderNoCell, String.class);
                orderNoList.add(orderNo);
            }
            List<EmcOrderBean> orderList = orderService.getDao().selectListByOneColumnValues(orderNoList, "ORDERNO");
            List<EmcOrderBean> updateOrderList = new ArrayList<>();

            for (int i = 2; i <= realRowCount; i++) {
                Row row = sheet.getRow(i);
                Cell orderNoCell = row.getCell(0);
                String orderNo = ExcelHelper.getCellValue(orderNoCell, String.class);
                EmcOrderBean oldOrder = orderList.stream().filter(e -> ObjectUtils.equals(e.getOrderNo(), orderNo)).findFirst().orElse(null);
                if (oldOrder != null) {
                    EmcOrderBean updateOrder = new EmcOrderBean();
                    updateOrder.setId(oldOrder.getId());
                    String labelNo = ExcelHelper.getCellValue(row.getCell(1), String.class);
                    updateOrder.setLabelNo("".equals(labelNo) ? null : labelNo);
                    String clientOrderName = ExcelHelper.getCellValue(row.getCell(2), String.class);
                    updateOrder.setClientOrderName("".equals(clientOrderName) ? null : clientOrderName);
                    String sampleAddress = ExcelHelper.getCellValue(row.getCell(3), String.class);
                    updateOrder.setSampleAddress("".equals(sampleAddress) ? null : sampleAddress);
                    String orderDesc = ExcelHelper.getCellValue(row.getCell(4), String.class);
                    updateOrder.setOrderDesc("".equals(orderDesc) ? null : orderDesc);
                    String remark = ExcelHelper.getCellValue(row.getCell(5), String.class);
                    updateOrder.setRemark("".equals(remark) ? null : remark);
                    updateOrderList.add(updateOrder);
                }
            }

            orderService.getDao().updateIfChanged(updateOrderList);

        } catch (IOException e) {
            throw new FileException(e);
        } finally {
            if (wb != null) {
                try {
                    wb.close();
                } catch (Exception ex) {
                    /* ignore */
                }
            }
        }
    }

}
