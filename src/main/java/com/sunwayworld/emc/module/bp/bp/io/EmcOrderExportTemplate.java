package com.sunwayworld.emc.module.bp.bp.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

@Component(value = "emcOrderExportTemplate")
public class EmcOrderExportTemplate implements GenericExportTemplate<String> {

    @Autowired
    private EmcOrderService orderService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcOrderExportTemplate", "样品信息导入模版");
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String id) {
        List<EmcOrderBean> orderList = orderService.selectListByFilter(SearchFilter.instance().match("PROJID", id).filter(MatchPattern.EQ));
        writeExcel(orderList, path);
    }

    public void writeExcel(List<EmcOrderBean> orderList, Path path) {
        HSSFWorkbook wb = null;
        FileOutputStream fos = null;

        try {
            wb = new HSSFWorkbook(new FileInputStream(path.toString()));
            // 第一张表格
            HSSFSheet sheet = (HSSFSheet) wb.getSheetAt(0);
            // 样式
            HSSFCellStyle style = wb.createCellStyle();
            style.setBorderBottom(BorderStyle.THIN);// 下边框
            style.setBorderLeft(BorderStyle.THIN);// 左边框
            style.setBorderRight(BorderStyle.THIN);// 右边框
            style.setBorderTop(BorderStyle.THIN);// 上边框

            for (int i = 0; i < orderList.size(); i++) {
                EmcOrderBean order = orderList.get(i);
                Row row = sheet.getRow(i + 2) == null ? sheet.createRow(i + 2) : sheet.getRow(i + 2);
                Cell orderNoCell = row.getCell(0) == null ? row.createCell(0) : row.getCell(0);
                orderNoCell.setCellValue(order.getOrderNo() == null ? "" : order.getOrderNo());
                orderNoCell.setCellStyle(style);
                Cell labelNoCell = row.getCell(1) == null ? row.createCell(1) : row.getCell(1);
                labelNoCell.setCellValue(order.getLabelNo() == null ? "" : order.getLabelNo());
                labelNoCell.setCellStyle(style);
                Cell clientOrderNameCell = row.getCell(2) == null ? row.createCell(2) : row.getCell(2);
                clientOrderNameCell.setCellValue(order.getClientOrderName() == null ? "" : order.getClientOrderName());
                clientOrderNameCell.setCellStyle(style);
                Cell sampleAddressCell = row.getCell(3) == null ? row.createCell(3) : row.getCell(3);
                sampleAddressCell.setCellValue(order.getSampleAddress() == null ? "" : order.getSampleAddress());
                sampleAddressCell.setCellStyle(style);
                Cell orderDescCell = row.getCell(4) == null ? row.createCell(4) : row.getCell(4);
                orderDescCell.setCellValue(order.getOrderDesc() == null ? "" : order.getOrderDesc());
                orderDescCell.setCellStyle(style);
                Cell remarkCell = row.getCell(5) == null ? row.createCell(5) : row.getCell(5);
                remarkCell.setCellValue(order.getRemark() == null ? "" : order.getRemark());
                remarkCell.setCellStyle(style);
            }

            fos = new FileOutputStream(path.toString());
            wb.write(fos);
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
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    /* ignore */
                }
            }
        }
    }

}
