package com.sunwayworld.emc.module.mdm.bizmdm.method.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.service.EmcMethodService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

@Component(value = "emcMethodUpdateExportTemplate")
public class EmcMethodUpdateExportTemplate implements GenericExportTemplate<String> {

    @Autowired
    private EmcMethodService emcMethodService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcMethodUpdateExportTemplate", EmcConstant.EMC_EXPORT_METHOD_UPDATE);
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<EmcMethodBean> methodList = emcMethodService.selectListByIds(idList);
        writeExcel(methodList, path);
    }

    private void writeExcel(List<EmcMethodBean> methodList, Path path) {
        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            HSSFSheet firstSheet = wb.getSheetAt(0);

            AtomicInteger rowIndex = new AtomicInteger(1);
            methodList.forEach(method -> {
                // 数据行
                HSSFRow dataRow = Optional.ofNullable(firstSheet.getRow(rowIndex.get())).orElseGet(() -> firstSheet.createRow(rowIndex.get()));
                dataRow.createCell(1).setCellValue(method.getId());
                dataRow.createCell(2).setCellValue(method.getMethodName());
                dataRow.createCell(3).setCellValue(method.getMethodNameEng());
                dataRow.createCell(4).setCellValue(method.getMethodNo());
                dataRow.createCell(5).setCellValue(method.getMonitorCategory());
                dataRow.createCell(6).setCellValue(method.getMethodCategory());
                dataRow.createCell(7).setCellValue(method.getYear());
                dataRow.createCell(8).setCellValue(method.getSource());
                dataRow.createCell(9).setCellValue(method.getEffectiveDate() == null ? "" : method.getEffectiveDate().toString());
                dataRow.createCell(10).setCellValue(method.getExpirationDate() == null ? "" : method.getExpirationDate().toString());
                dataRow.createCell(11).setCellValue(method.getPublishOrgName());
                dataRow.createCell(12).setCellValue(method.getPublishDate() == null ? "" : method.getPublishDate().toString());
                dataRow.createCell(13).setCellValue(method.getVersion());
                dataRow.createCell(14).setCellValue(method.getRemark());
                dataRow.createCell(15).setCellValue(method.getRunStatus());
                dataRow.createCell(16).setCellValue(method.getReplacedMethodId() == null ? "" : method.getReplacedMethodId().toString());
                dataRow.createCell(17).setCellValue(method.getReplaceMethodId() == null ? "" : method.getReplaceMethodId().toString());
                dataRow.createCell(18).setCellValue(method.getReplacedContent());
                dataRow.createCell(19).setCellValue(method.getReplaceContent());
                rowIndex.incrementAndGet();
            });

            // 隐藏序号列
            firstSheet.setColumnWidth(0, 0);
            // 隐藏第二列id，避免用户误改
            firstSheet.setColumnWidth(1, 0);

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
