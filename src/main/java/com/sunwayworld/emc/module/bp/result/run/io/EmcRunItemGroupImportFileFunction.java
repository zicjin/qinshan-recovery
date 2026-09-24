/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.emc.module.bp.result.run.io;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="emcRunItemGroupCoreFileImportFunction")
@GikamBean
public class EmcRunItemGroupImportFileFunction
implements CoreFileImportFunction {
    protected static final int MIN_ROW_SIZE = 2;
    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO2 = new CoreFileImportResultDTO();
        this.importOrderTaskResult(CoreFileUtils.getLocalPath(file).toFile());
        return CoreFileImportResultDTO2;
    }

    protected void importOrderTaskResult(File file) {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
             HSSFWorkbook wb = new HSSFWorkbook(is);){
            HSSFSheet firstSheet = wb.getSheetAt(0);
            HSSFRow firstRow = firstSheet.getRow(0);
            int lastRowNum = firstSheet.getPhysicalNumberOfRows();
            short lastCellNum = firstRow.getLastCellNum();
            ArrayList<EmcOrderTaskResultBean> updateResultList = new ArrayList<EmcOrderTaskResultBean>();
            LinkedHashMap<String, List> relationRowMap = new LinkedHashMap<String, List>();
            IntStream.range(3, lastRowNum).forEach(rowIndex -> {
                HSSFRow row = firstSheet.getRow(rowIndex);
                if (row == null) {
                    return;
                }
                HSSFCell firstCell = row.getCell(0);
                String idArrayStr = ExcelHelper.getCellValue(firstCell, String.class);
                if (StringUtils.isBlank(idArrayStr)) {
                    return;
                }
                List rowList = Optional.ofNullable(relationRowMap.get(idArrayStr)).orElseGet(ArrayList::new);
                rowList.add(row);
                relationRowMap.put(idArrayStr, rowList);
            });
            relationRowMap.forEach((idArrayStr, rowList) -> {
                if (rowList.size() < 2) {
                    return;
                }
                ArrayList cellIndexList = new ArrayList();
                ArrayList resultIdList = new ArrayList();
                HSSFRow markRow = (HSSFRow)rowList.get(0);
                IntStream.range(3, lastCellNum).forEach(cellIndex -> {
                    HSSFCell cell = markRow.getCell(cellIndex);
                    if (cell != null && cell.getCellComment() != null) {
                        cellIndexList.add(cellIndex);
                        resultIdList.add(((HSSFRichTextString)cell.getCellComment().getString()).toString());
                    }
                });
                HSSFRow dataRow = (HSSFRow)rowList.get(1);
                IntStream.range(0, cellIndexList.size()).forEach(i -> {
                    HSSFCell cell = dataRow.getCell((Integer)cellIndexList.get(i));
                    String value = ExcelHelper.getCellValue(cell, String.class);
                    if (!StringUtils.isBlank(value)) {
                        EmcOrderTaskResultBean updateResult = new EmcOrderTaskResultBean();
                        updateResult.setId(NumberUtils.parseLong(resultIdList.get(i)));
                        updateResult.setOriginalResult(value);
                        updateResultList.add(updateResult);
                    }
                });
            });
            if (!updateResultList.isEmpty()) {
                this.updateAndComputing(updateResultList);
            }
        }
        catch (IOException e) {
            throw new FileException(e);
        }
    }

    protected void updateAndComputing(List<EmcOrderTaskResultBean> updateList) {
        this.orderTaskResultService.getDao().update(updateList, (String[])new String[]{"ORIGINALRESULT"});
        if (updateList.size() > 0) {
            EmcOrderTaskResultComputingEngine.getEngine(updateList.stream().map(EmcOrderTaskResultBean::getId).distinct().collect(Collectors.toList())).compute();
        }
    }
}
