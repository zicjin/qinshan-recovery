package com.sunwayworld.emc.module.bp.result.curve.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.bp.computing.EmcOrderTaskResultComputingEngine;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskResultService;
import com.sunwayworld.emc.module.bp.result.curve.bean.EmcCurveBean;
import com.sunwayworld.emc.module.bp.result.curve.service.EmcCurveService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.file.FileHelper;
import com.sunwayworld.framework.io.file.FileRepresentation;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.ImportResultRepresentation;
import com.sunwayworld.module.item.file.function.ImportFileFunction;

@Component(value = "emcCurveImportFileFunction")
public class EmcCurveImportFileFunction implements ImportFileFunction {

    private static final String STAND_QTY = "标准物量";
    private static final String RN6 = "标液加入量";
    private static final String ORIGINAL_RESULT = "原始结果";

    @Autowired
    private EmcOrderTaskResultService orderTaskResultService;

    @Autowired
    private EmcItemService itemService;

    @Autowired
    private EmcCurveService curveService;

    @Override
    public boolean test(CoreFileBean file, String service) {
        return true;
    }

    @Override
    public ImportResultRepresentation apply(CoreFileBean file) {
        ImportResultRepresentation importResultRepresentation = new ImportResultRepresentation();

        FileRepresentation fr = FileRepresentation.of(file);
        String[] idArray = StringUtils.split(file.getTargetId(), "$");
        long curveId = NumberUtils.parseLong(idArray[0]);
        long folderId = NumberUtils.parseLong(idArray[1]);
        importOrderTaskResultFromExcel(FileHelper.getAbsolutePath(fr).toFile().getAbsolutePath(), folderId, curveId);
        return importResultRepresentation;
    }

    private void importOrderTaskResultFromExcel(String filePath, Long folderId, Long curveId) {
        try (InputStream is = new BufferedInputStream(new FileInputStream(filePath)); HSSFWorkbook wb = new HSSFWorkbook(is)) {
            // 默认获取第一个Sheet表格
            HSSFSheet firstSheet = wb.getSheetAt(0);
            // 总行数
            int realRowCount = firstSheet.getPhysicalNumberOfRows();

            // 读取样品测试ID，从第二行开始读取，排除第一行标题
            List<Long> orderTaskIdList = new ArrayList<>(realRowCount - 1);
            IntStream.range(1, realRowCount).forEach(i -> orderTaskIdList.add(ExcelHelper.getCellValue(firstSheet.getRow(i).getCell(0), Long.class)));

            // 查询点位下的样品测试结果
            List<EmcOrderTaskResultBean> allOrderTaskResultList = orderTaskResultService.selectListByFilter(SearchFilter.instance().match("FOLDERID", folderId).filter(MatchPattern.EQ));
            List<EmcOrderTaskResultBean> updateResultList = new ArrayList<>();

            IntStream.range(0, orderTaskIdList.size()).forEach(i -> {
                List<EmcOrderTaskResultBean> currentResultList = allOrderTaskResultList.stream().filter(e -> ObjectUtils.equals(orderTaskIdList.get(i), e.getOrderTaskId())).collect(Collectors.toList());
                if (currentResultList.isEmpty()) {
                    return;
                }

                List<String> itemNameList = currentResultList.stream().map(EmcOrderTaskResultBean::getItemName).distinct().collect(Collectors.toList());
                HSSFRow firstRow = firstSheet.getRow(0);
                int cellNum = firstRow.getPhysicalNumberOfCells();
                itemNameList.forEach(name -> {
                    IntStream.range(3, cellNum).forEach(m -> {
                        HSSFCell cell = firstRow.getCell(m);
                        String cellValue = ExcelHelper.getCellValue(cell, String.class);
                        if (cellValue.contains(RN6)) {
                            return;
                        }

                        if (cellValue.contains(STAND_QTY)) {
                            String itemName = StringUtils.split(cellValue, STAND_QTY)[0];
                            List<EmcOrderTaskResultBean> standQtyAndRn6ResultList = currentResultList.stream().filter(c -> ObjectUtils.equals(itemName, c.getItemName()))
                                    .filter(e -> "NUMERIC_CON_NOB".equals(e.getExt$Item("setname")) || "NUMERIC_CON".equals(e.getExt$Item("setname"))).collect(Collectors.toList());
                            if (!standQtyAndRn6ResultList.isEmpty()) {
                                Row resultRow = firstSheet.getRow(i + 1);
                                String standQty = ExcelHelper.getCellValue(resultRow.getCell(m), String.class);
                                String rn6 = ExcelHelper.getCellValue(resultRow.getCell(m + 1), String.class);
                                standQtyAndRn6ResultList.forEach(result -> {
                                    result.setStandQty(standQty);
                                    result.setRn6(rn6);
                                    updateResultList.add(result);
                                });
                            }
                        }

                        if (cellValue.contains(ORIGINAL_RESULT)) {
                            String itemName = StringUtils.split(cellValue, ORIGINAL_RESULT)[0];
                            List<EmcOrderTaskResultBean> noScriptTaskResultList = currentResultList.stream().filter(c -> ObjectUtils.equals(itemName, c.getItemName())).filter(e -> Constant.NO.equals(e.getScriptFlag())).collect(Collectors.toList());
                            if (!noScriptTaskResultList.isEmpty()) {
                                Row resultRow = firstSheet.getRow(i + 1);
                                String originalResult = ExcelHelper.getCellValue(resultRow.getCell(m), String.class);
                                noScriptTaskResultList.forEach(result -> {
                                    result.setOriginalResult(originalResult);
                                    updateResultList.add(result);
                                });
                            }
                        }
                    });
                });
            });

            // 更新数据
            update(allOrderTaskResultList, updateResultList, folderId, curveId);
        } catch (IOException e) {
            throw new FileException(e);
        }
    }

    private void update(List<EmcOrderTaskResultBean> allOrderTaskResultList, List<EmcOrderTaskResultBean> updateResultList, Long folderId, Long curveId) {
        // 更新数据
        orderTaskResultService.getDao().update(updateResultList, "ORIGINALRESULT", "STANDQTY", "RN6");

        // 触发计算公式
        EmcOrderTaskResultComputingEngine.getEngine(allOrderTaskResultList.stream().map(EmcOrderTaskResultBean::getId).collect(Collectors.toList())).compute();

        // 重新查询该点位下所有数据，当所有曲线校准的分析项的原始结果和标准物量都不为空才做斜率截距相关系数的计算
        List<EmcOrderTaskResultBean> orderTaskResultList = orderTaskResultService.getDao().selectListByOneColumnValue(folderId, "FOLDERID");
        List<Long> itemIdList = orderTaskResultList.stream().map(EmcOrderTaskResultBean::getItemId).distinct().collect(Collectors.toList());

        // 筛选出指标名称为曲线校准的分析项id
        List<EmcItemBean> curveItemList = itemService.selectListByFilter(SearchFilter.instance().match("ID", itemIdList).filter(MatchPattern.OR)
                .match("SETNAME", Arrays.asList("NUMERIC_CON", "NUMERIC_CON_NOB")).filter(MatchPattern.OR));

        List<Long> curveItemIdList = curveItemList.stream().map(EmcItemBean::getId).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> curveOrderTaskResultList = orderTaskResultList.stream().filter(e -> curveItemIdList.contains(e.getItemId())).collect(Collectors.toList());

        List<EmcOrderTaskResultBean> updateOrderTaskResultList = curveService.calculateSlopeIntercept(curveOrderTaskResultList, curveItemList);

        if (!updateOrderTaskResultList.isEmpty()) {
            orderTaskResultService.getDao().update(curveOrderTaskResultList, "RN2", "RN3", "RN4");

            // 更新曲线的斜率截距相关系数值
            EmcOrderTaskResultBean firstOrderTaskResult = curveOrderTaskResultList.get(0);
            EmcCurveBean curve = new EmcCurveBean();
            curve.setId(curveId);
            curve.setSlope(firstOrderTaskResult.getRn2());
            curve.setIntercept(firstOrderTaskResult.getRn3());
            curve.setCoefficient(firstOrderTaskResult.getRn4());

            curveService.getDao().update(curve, "SLOPE", "INTERCEPT", "COEFFICIENT");
        }
    }

}
