/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.function;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandHVGroupTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandTableFirstTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandTableSecondTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsFixedLoopMultiRowTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsFixedMultiColumnTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeOrdtaskVTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeVTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsSplitHTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface TableFunction {
    public static final Logger logger = LoggerFactory.getLogger("TableFunction");

    default public LimsMergeVTableRenderData mergeV(LimsTemplateDataList<LimsTemplateDataMap> list, int ... cellIndexArray) {
        return new LimsMergeVTableRenderData(list, cellIndexArray);
    }

    default public LimsMergeVTableRenderData mergeV(Boolean blankValueMergeFlag, LimsTemplateDataList<LimsTemplateDataMap> list, int ... cellIndexArray) {
        return new LimsMergeVTableRenderData(list, blankValueMergeFlag, cellIndexArray);
    }

    default public LimsMergeVTableRenderData mergeV(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeByField, int ... cellIndexArray) {
        LimsMergeVTableRenderData limsMergeVTableRenderData = new LimsMergeVTableRenderData(list, cellIndexArray);
        limsMergeVTableRenderData.setMergeByField(mergeByField);
        return limsMergeVTableRenderData;
    }

    default public LimsMergeVTableRenderData mergeV(String noGroupStr, LimsTemplateDataList<LimsTemplateDataMap> list, String mergeByField, int ... cellIndexArray) {
        LimsMergeVTableRenderData limsMergeVTableRenderData = new LimsMergeVTableRenderData(list, cellIndexArray);
        limsMergeVTableRenderData.setMergeByField(mergeByField);
        Set set = Arrays.stream(noGroupStr.split(",")).filter(NumberUtils::isNumber).map(e -> NumberUtils.parseInt(e)).collect(Collectors.toSet());
        if (!set.isEmpty()) {
            ArrayList<Integer> intList = new ArrayList<Integer>(set);
            limsMergeVTableRenderData.setNotMergeByFiledIndexArray(intList);
        }
        return limsMergeVTableRenderData;
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, String repeatHeaderStr) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, repeatHeaderStr);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, boolean isPage) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, isPage);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, int maxDynamicCellSize) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, maxDynamicCellSize);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, int maxDynamicCellSize, boolean isPage) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, maxDynamicCellSize, isPage);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, int maxDynamicCellSize, String tableTitle) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, maxDynamicCellSize, tableTitle);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, String repeatHeaderStr, String tableTitle) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, repeatHeaderStr, tableTitle);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, int maxDynamicCellSize, String tableTitle, String mergeSameColumn) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, maxDynamicCellSize, tableTitle, mergeSameColumn);
    }

    default public LimsMergeTemplateRenderData mergeRowAndColumn(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeRow, String mergeColumn, String repeatHeaderStr, String tableTitle, String mergeSameColumn) {
        return new LimsMergeTemplateRenderData(list, mergeRow, mergeColumn, repeatHeaderStr, tableTitle, mergeSameColumn);
    }

    default public LimsMergeOrdtaskVTableRenderData mergeOrdtaskV(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeByField, String mergeVIndexStr, String mergeHIndexStr) {
        LimsMergeOrdtaskVTableRenderData limsMergeOrdtaskVTableRenderData = new LimsMergeOrdtaskVTableRenderData(list, mergeVIndexStr, mergeHIndexStr);
        limsMergeOrdtaskVTableRenderData.setMergeByField(mergeByField);
        return limsMergeOrdtaskVTableRenderData;
    }

    default public LimsMergeVTableRenderData mergeVNoHead(LimsTemplateDataList<LimsTemplateDataMap> list, int ... cellIndexArray) {
        LimsMergeVTableRenderData limsMergeVTableRenderData = new LimsMergeVTableRenderData(list, cellIndexArray);
        limsMergeVTableRenderData.setNeedHead(false);
        return limsMergeVTableRenderData;
    }

    default public LimsMergeVTableRenderData mergeVNoHead(LimsTemplateDataList<LimsTemplateDataMap> list, String mergeByField, int ... cellIndexArray) {
        LimsMergeVTableRenderData limsMergeVTableRenderData = new LimsMergeVTableRenderData(list, cellIndexArray);
        limsMergeVTableRenderData.setNeedHead(false);
        limsMergeVTableRenderData.setMergeByField(mergeByField);
        return limsMergeVTableRenderData;
    }

    default public LimsSplitHTableRenderData splitH(LimsTemplateDataList<LimsTemplateDataMap> list, int maxCellCount, String subField, String titleIdField, String title, String value, int ... mergeCellIndexArray) {
        LimsSplitHTableRenderData limsSplitHTableRenderData = new LimsSplitHTableRenderData(list);
        limsSplitHTableRenderData.setMaxCellCount(maxCellCount);
        limsSplitHTableRenderData.setSubFiled(subField);
        limsSplitHTableRenderData.setTitleField(title);
        limsSplitHTableRenderData.setValueField(value);
        limsSplitHTableRenderData.setTitleIdField(titleIdField);
        limsSplitHTableRenderData.setMergeCellIndexArray(mergeCellIndexArray);
        return limsSplitHTableRenderData;
    }

    default public LimsSplitHTableRenderData splitShelfH(LimsTemplateDataList<LimsTemplateDataMap> list, int maxCellCount, String pivotField, String titleIdField, String titleField, String valueField, int ... mergeCellIndexArray) {
        LimsSplitHTableRenderData limsSplitHTableRenderData = new LimsSplitHTableRenderData(list);
        limsSplitHTableRenderData.setMaxCellCount(maxCellCount);
        limsSplitHTableRenderData.setTitleField(titleField);
        limsSplitHTableRenderData.setValueField(valueField);
        limsSplitHTableRenderData.setPivotField(pivotField);
        limsSplitHTableRenderData.setTitleIdField(titleIdField);
        limsSplitHTableRenderData.setMergeCellIndexArray(mergeCellIndexArray);
        return limsSplitHTableRenderData;
    }

    default public LimsSplitHTableRenderData splitHNoHead(LimsTemplateDataList<LimsTemplateDataMap> list, int maxCellCount, String subField, String titleIdField, String title, String value, int ... mergeCellIndexArray) {
        LimsSplitHTableRenderData limsSplitHTableRenderData = new LimsSplitHTableRenderData(list);
        limsSplitHTableRenderData.setMaxCellCount(maxCellCount);
        limsSplitHTableRenderData.setSubFiled(subField);
        limsSplitHTableRenderData.setTitleField(title);
        limsSplitHTableRenderData.setValueField(value);
        limsSplitHTableRenderData.setTitleIdField(titleIdField);
        limsSplitHTableRenderData.setNeedHead(false);
        limsSplitHTableRenderData.setMergeCellIndexArray(mergeCellIndexArray);
        return limsSplitHTableRenderData;
    }

    default public LimsSplitHTableRenderData splitShelfHNoHead(LimsTemplateDataList<LimsTemplateDataMap> list, int maxCellCount, String pivotField, String titleIdField, String titleField, String valueField, int ... mergeCellIndexArray) {
        LimsSplitHTableRenderData limsSplitHTableRenderData = new LimsSplitHTableRenderData(list);
        limsSplitHTableRenderData.setMaxCellCount(maxCellCount);
        limsSplitHTableRenderData.setTitleField(titleField);
        limsSplitHTableRenderData.setValueField(valueField);
        limsSplitHTableRenderData.setPivotField(pivotField);
        limsSplitHTableRenderData.setTitleIdField(titleField);
        limsSplitHTableRenderData.setNeedHead(false);
        limsSplitHTableRenderData.setMergeCellIndexArray(mergeCellIndexArray);
        return limsSplitHTableRenderData;
    }

    default public LimsFixedMultiColumnTemplateRenderData groupAndPartition(LimsTemplateDataList<LimsTemplateDataMap> dataList, int partitionSize, String groupField, String uniqueField, String subUniqueField, String valueField) {
        return new LimsFixedMultiColumnTemplateRenderData(dataList, partitionSize, groupField, uniqueField, subUniqueField, valueField);
    }

    default public LimsFixedMultiColumnTemplateRenderData groupAndPartition(LimsTemplateDataList<LimsTemplateDataMap> dataList, int partitionSize, String groupField, String uniqueField, String subUniqueField, String valueField, String showAvg) {
        return new LimsFixedMultiColumnTemplateRenderData(dataList, partitionSize, groupField, uniqueField, subUniqueField, valueField, showAvg);
    }

    default public LimsExpandTableFirstTemplateRenderData expandTableFirst(LimsTemplateDataList<LimsTemplateDataMap> dataList, String columnGroupField, int rowStartExpandIndex, String rowGroupField) {
        return new LimsExpandTableFirstTemplateRenderData(dataList, columnGroupField, rowStartExpandIndex, rowGroupField);
    }

    default public LimsExpandTableSecondTemplateRenderData expandTableSecond(LimsTemplateDataList<LimsTemplateDataMap> dataList, String columnGroupField, int columnStartExpandIndex, int columnMaxExpandNum, String rowGroupField, int rowStartExpandIndex) {
        return new LimsExpandTableSecondTemplateRenderData(dataList, columnGroupField, columnStartExpandIndex, columnMaxExpandNum, rowGroupField, rowStartExpandIndex);
    }

    default public LimsExpandHVGroupTableRenderData expandHVGroupTable(LimsTemplateDataList<LimsTemplateDataMap> dataList, String groupField, int columnExpandNum, String mergeRowRole) {
        return new LimsExpandHVGroupTableRenderData(dataList, groupField, columnExpandNum, mergeRowRole);
    }

    default public LimsFixedLoopMultiRowTemplateRenderData fixedLoopMultiRow(LimsTemplateDataList<LimsTemplateDataMap> dataList, int loopRowCount, int fixedColumnCount, int dataStartColumnIndex, String rowGroupField, String columnGroupField) {
        return new LimsFixedLoopMultiRowTemplateRenderData(dataList, loopRowCount, fixedColumnCount, dataStartColumnIndex, rowGroupField, columnGroupField);
    }
}
