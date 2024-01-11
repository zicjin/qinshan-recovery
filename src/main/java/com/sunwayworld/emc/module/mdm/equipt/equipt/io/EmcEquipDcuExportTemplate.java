package com.sunwayworld.emc.module.mdm.equipt.equipt.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestItemBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestItemService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

/**
 * 将DCU配置导出为采集结果
 * @author zhengxy@sunwayworld.com 2020年09月17日
 */
@Component(value = "emcEquipDcuExportTemplate")
public class EmcEquipDcuExportTemplate implements GenericExportTemplate<String> {

    @Autowired
    private EmcEquiptTestItemService testItemService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcEquipDcuExportTemplate", EmcConstant.EMC_EXPORT_DCU_EAS_RESULT);
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String equipmentId) {
        if (StringUtils.isEmpty(equipmentId)) {
            return;
        }

        List<EmcEquiptTestItemBean> testItemList = testItemService.getDao().selectListByOneColumnValue(equipmentId, "EQUIPTID");
        List<EmcEquiptTestItemBean> matchItemList = testItemList.stream().filter(item -> !StringUtils.isEmpty(item.getMatchItemName())).collect(Collectors.toList());
        if (matchItemList.isEmpty()) {
            return;
        }

        ArrayList<EmcEquiptTestItemBean> uniqueTestItemList = matchItemList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcEquiptTestItemBean::getMatchItemName))), ArrayList::new));

        try(HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
            FileOutputStream fos = new FileOutputStream(path.toString())) {

            // 默认获取第一个Sheet表格
            HSSFSheet firstSheet = wb.getSheetAt(0);
            IntStream.range(0, uniqueTestItemList.size()).forEach(index -> {
                EmcEquiptTestItemBean testItem = uniqueTestItemList.get(index);
                Row row = Optional.ofNullable(firstSheet.getRow(index + 1)).orElseGet(() -> firstSheet.createRow(index + 1));
                List<Object> valueList = Arrays.asList(index + 1, testItem.getMatchItemName(), 0, 0, 0, "");
                IntStream.range(0, 6).forEach(cellNum -> {
                    Cell cell = Optional.ofNullable(row.getCell(cellNum)).orElse(row.createCell(cellNum));
                    cell.setCellValue(valueList.get(cellNum).toString());
                });
            });

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
