package com.sunwayworld.emc.module.bp.sample.sample.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.function.CoreFileImportFunction;

@Component(value = "emcSampleImportFileFunction")
public class EmcSampleImportFileFunction implements CoreFileImportFunction {

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Override
    public boolean test(CoreFileBean coreFile, String service) {
        return true;
    }
    @Override
    public CoreFileImportResultDTO apply(CoreFileBean file) {
        CoreFileImportResultDTO CoreFileImportResultDTO = new CoreFileImportResultDTO();

        FilePathDTO fr = CoreFileUtils.toFilePath(file);
        importOrderTaskResult(FilePathManager.getLocalPath(fr).toFile());
        return CoreFileImportResultDTO;
    }

    private void importOrderTaskResult(File file) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(file));
             XSSFWorkbook wb = new XSSFWorkbook(is)) {

            XSSFSheet firstSheet = wb.getSheetAt(0);
            Row firstRow = firstSheet.getRow(0);

            int lastRowNum = firstSheet.getPhysicalNumberOfRows();
            int lastCellNum = firstRow.getLastCellNum();

            List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();

            Map<String, List<Row>> relationRowMap = new LinkedHashMap<>();
            IntStream.range(2, lastRowNum).forEach(rowIndex -> {
                Row row = firstSheet.getRow(rowIndex);
                Cell firstCell = row.getCell(0);
                String idArrayStr = ExcelHelper.getCellValue(firstCell, String.class);
                if (StringUtils.isBlank(idArrayStr)) {
                    return;
                }

                List<Row> rowList = Optional.ofNullable(relationRowMap.get(idArrayStr)).orElseGet(ArrayList::new);
                rowList.add(row);
                relationRowMap.put(idArrayStr, rowList);
            });

            relationRowMap.forEach((idArrayStr, rowList) -> {
                List<Integer> cellIndexList = new ArrayList<>();
                List<String> resultIdList = new ArrayList<>();
                Row markRow = rowList.get(0);
                IntStream.range(3, lastCellNum).forEach(cellIndex -> {
                    Cell cell = markRow.getCell(cellIndex);
                    if (cell != null && cell.getCellComment() != null) {
                        cellIndexList.add(cellIndex);
                        resultIdList.add(cell.getCellComment().getString().toString());
                    }
                });

                Row dataRow = rowList.get(1);
                IntStream.range(0, cellIndexList.size()).forEach(i -> {
                    Cell cell = dataRow.getCell(cellIndexList.get(i));
                    String value = getCellValue(cell, String.class);
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

    @Transactional
    public void updateAndComputing(List<EmcOrderTaskResultBean> updateList) {
        if (updateList.isEmpty()) {
            return;
        }

        orderTaskResultService.getDao().updateIfChanged(updateList);
        // 触发计算公式
        EmcOrderTaskResultComputingEngine.getEngine(updateList.stream().map(EmcOrderTaskResultBean::getId).distinct().collect(Collectors.toList())).compute();
    }
    
    
    @SuppressWarnings({ "deprecation" })
    private static <T> T getCellValue(Cell cell, Class<?> clazz) {
        Object cellValue = "";
        Class<?> propertyType = String.class;

        if (cell != null) { // cell can be null.
            switch (cell.getCellType()) {
                case STRING:
                    cellValue = StringUtils.trim(cell.getRichStringCellValue().getString());
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        Date cellDate = HSSFDateUtil.getJavaDate(Double.valueOf(cell.getNumericCellValue()));
                        if (cellDate.before(new Date("1900/1/1"))) {
                            String hour = cellDate.getHours() + "";
                            if (cellDate.getHours() < 10) {
                                hour = "0" + hour;
                            }
                            String minutes = cellDate.getMinutes() + "";
                            if (cellDate.getMinutes() < 10) {
                                minutes = "0" + minutes;
                            }
                            cellValue = hour + ":" + minutes;
                        } else {
                            cellValue = cell.getDateCellValue();
                        }
                    } else {
                        cellValue = cell.getNumericCellValue();
                    }
                    break;
                case BOOLEAN:
                    cellValue = ObjectUtils.toString(cell.getBooleanCellValue());
                    break;
                case FORMULA: // Cell公式要特殊处理
                    final FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                    final CellValue cv = evaluator.evaluate(cell);
                    switch (cv.getCellType()) {
                        case STRING:
                            cellValue = StringUtils.trim(cv.getStringValue());
                            break;
                        case NUMERIC:
                            cellValue = "" + cv.getNumberValue();
                            break;
                        case BOOLEAN:
                            cellValue = ObjectUtils.toString(cv.getBooleanValue());
                            break;
                        default:
                            cellValue = null;
                    }
                    break;
                default:
                    cellValue = "";
            }
        }

        propertyType = ((propertyType == null) ? String.class : propertyType);
        @SuppressWarnings("unchecked")
        T value = (T) ConvertUtils.convert(cellValue, propertyType);

        return value;
    }

}
