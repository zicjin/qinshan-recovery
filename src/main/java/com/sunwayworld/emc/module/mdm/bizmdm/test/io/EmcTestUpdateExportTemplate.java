package com.sunwayworld.emc.module.mdm.bizmdm.test.io;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Component(value = "emcTestUpdateExportTemplate")
public class EmcTestUpdateExportTemplate implements GenericExportTemplate<String> {

    @Autowired
    private EmcTestService emcTestService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcTestUpdateExportTemplate", EmcConstant.EMC_EXPORT_TEST_UPDATE);
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String ids) {
        List<Long> idList = Arrays.stream(ids.split(",")).map(Long::parseLong).collect(Collectors.toList());
        List<EmcTestBean> testList = emcTestService.selectListByIds(idList);
        writeExcel(testList, path);
    }

    private void writeExcel(List<EmcTestBean> testList, Path path) {
        try (HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
             FileOutputStream fos = new FileOutputStream(path.toString())) {

            HSSFSheet firstSheet = wb.getSheetAt(0);

            AtomicInteger rowIndex = new AtomicInteger(2);
            testList.forEach(test -> {
                // 数据行
                HSSFRow dataRow = Optional.ofNullable(firstSheet.getRow(rowIndex.get())).orElseGet(() -> firstSheet.createRow(rowIndex.get()));
                dataRow.createCell(1).setCellValue(test.getId());
                dataRow.createCell(2).setCellValue(test.getTestName());
                dataRow.createCell(3).setCellValue(test.getEnglishName());
                rowIndex.incrementAndGet();
            });

            // 隐藏第二列id，避免用户误改
            firstSheet.setColumnWidth(1, 0);
            // 隐藏序号列
            firstSheet.setColumnWidth(0, 0);

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
