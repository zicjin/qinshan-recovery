package com.sunwayworld.emc.module.mdm.basemdm.mtl.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlService;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.export.GenericExportTemplate;

/**
 * @author zhengxy@sunwayworld.com 2020年07月31日
 */
@Component(value = "emcStockExportTemplate")
public class EmcStockExportTemplate implements GenericExportTemplate<String> {

    private static final int START_ROW_INDEX = 3;

    @Autowired
    private EmcMtlService mtlService;

    @Override
    public List<Pair<String, String>> getTemplatePairList() {
        List<Pair<String, String>> templatePairList = new ArrayList<>();
        Pair<String, String> templatePair = Pair.of("emcStockExportTemplate", EmcConstant.EMC_EXPORT_MTL_STOCK);
        templatePairList.add(templatePair);
        return templatePairList;
    }

    @Override
    public void initTemplateData(Path path, String id) {
        // 没有选择材料时，直接导出空模板
        if (StringUtils.isBlank(id)) {
            return;
        }

        String[] mtlIds = StringUtils.split(id, ",");
        List<EmcMtlBean> mtlList = mtlService.getDao().selectListByOneColumnValues(Arrays.asList(mtlIds), "ID", Arrays.asList("ID", "MTLNAME"));
        Map<String, String> mtlIdNameMap = mtlList.stream().collect(Collectors.toMap(EmcMtlBean::getId, EmcMtlBean::getMtlName));

        try(HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(path.toString()));
            FileOutputStream fos = new FileOutputStream(path.toString())) {

            // 默认获取第一个Sheet表格
            HSSFSheet firstSheet = wb.getSheetAt(0);
            IntStream.range(0, mtlIds.length).forEach(index -> {
                Row row = Optional.ofNullable(firstSheet.getRow(index + START_ROW_INDEX)).orElseGet(() -> firstSheet.createRow(index + START_ROW_INDEX));
                Cell mtlNameCell = Optional.ofNullable(row.getCell(0)).orElse(row.createCell(0));
                Cell mtlNoCell = Optional.ofNullable(row.getCell(1)).orElse(row.createCell(1));
                mtlNameCell.setCellValue(mtlIdNameMap.get(mtlIds[index]));
                mtlNoCell.setCellValue(mtlIds[index]);
            });

            wb.write(fos);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }
}
