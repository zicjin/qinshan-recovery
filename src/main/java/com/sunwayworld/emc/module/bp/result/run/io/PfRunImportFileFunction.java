package com.sunwayworld.emc.module.bp.result.run.io;


import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.result.run.io.EmcRunImportFileFunction;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author licm@sunwayworld.com
 */
@Component(value = "pfRunImportFileFunction")
@GikamBean
public class PfRunImportFileFunction extends EmcRunImportFileFunction {

    @Override
    protected void importOrderTaskResult(File file) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));
             HSSFWorkbook wb = new HSSFWorkbook(is)) {

            HSSFSheet firstSheet = wb.getSheetAt(0);
            HSSFRow firstRow = firstSheet.getRow(0);

            int lastRowNum = firstSheet.getPhysicalNumberOfRows();
            int lastCellNum = firstRow.getLastCellNum();

            List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();

            Map<String, List<HSSFRow>> relationRowMap = new LinkedHashMap<>();
            IntStream.range(2, lastRowNum).forEach(rowIndex -> {
                HSSFRow row = firstSheet.getRow(rowIndex);
            	if(row ==null){
            		return;
            	}
                HSSFCell firstCell = row.getCell(0);
                String idArrayStr = ExcelHelper.getCellValue(firstCell, String.class);
                if (StringUtils.isBlank(idArrayStr)) {
                    return;
                }

                List<HSSFRow> rowList = Optional.ofNullable(relationRowMap.get(idArrayStr)).orElseGet(ArrayList::new);
                rowList.add(row);
                relationRowMap.put(idArrayStr, rowList);
            });

            relationRowMap.forEach((idArrayStr, rowList) -> {
                if (rowList.size() < MIN_ROW_SIZE) {
                    return;
                }

                List<Integer> cellIndexList = new ArrayList<>();
                List<String> resultIdList = new ArrayList<>();
                HSSFRow markRow = rowList.get(0);
                IntStream.range(3, lastCellNum).forEach(cellIndex -> {
                    HSSFCell cell = markRow.getCell(cellIndex);
                    if (cell != null && cell.getCellComment() != null) {
                        cellIndexList.add(cellIndex);
                        resultIdList.add(cell.getCellComment().getString().toString());
                    }
                });

                HSSFRow dataRow = rowList.get(1);
                IntStream.range(0, cellIndexList.size()).forEach(i -> {
                    HSSFCell cell = dataRow.getCell(cellIndexList.get(i));
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
                updateAndComputing(updateResultList);
            }

        } catch (IOException e) {
            throw new FileException(e);
        }
    }


}
