/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.data;

import com.sunwayworld.cloud.lims.reporttemplate.support.data.AbstractMergeTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LimsMergeTemplateRenderData
extends AbstractMergeTableRenderData {
    private String mergeRow;
    private String mergeColumn;
    private List<MergedRow> mergedRowList = new ArrayList<MergedRow>();
    private List<MergedColumn> mergedColumnList = new ArrayList<MergedColumn>();
    private int maxDynamicCellSize;
    private boolean isPage;
    private String tableTitle;
    private String repeatHeaderStr;
    private String mergeSameColumn;

    public String getMergeRow() {
        return this.mergeRow;
    }

    public void setMergeRow(String mergeRow) {
        this.mergeRow = mergeRow;
    }

    public String getMergeColumn() {
        return this.mergeColumn;
    }

    public void setMergeColumn(String mergeColumn) {
        this.mergeColumn = mergeColumn;
    }

    public List<MergedRow> getMergedRowList() {
        return this.mergedRowList;
    }

    public void setMergedRowList(List<MergedRow> mergedRowList) {
        this.mergedRowList = mergedRowList;
    }

    public List<MergedColumn> getMergedColumnList() {
        return this.mergedColumnList;
    }

    public void setMergedColumnList(List<MergedColumn> mergedColumnList) {
        this.mergedColumnList = mergedColumnList;
    }

    public int getMaxDynamicCellSize() {
        return this.maxDynamicCellSize;
    }

    public void setMaxDynamicCellSize(int maxDynamicCellSize) {
        this.maxDynamicCellSize = maxDynamicCellSize;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList) {
        super(dataList);
    }

    public boolean isPage() {
        return this.isPage;
    }

    public void setPage(boolean page) {
        this.isPage = page;
    }

    public String getRepeatHeaderStr() {
        return this.repeatHeaderStr;
    }

    public void setRepeatHeaderStr(String repeatHeaderStr) {
        this.repeatHeaderStr = repeatHeaderStr;
    }

    public String getTableTitle() {
        return this.tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public String getMergeSameColumn() {
        return this.mergeSameColumn;
    }

    public void setMergeSameColumn(String mergeSameColumn) {
        this.mergeSameColumn = mergeSameColumn;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = Integer.MAX_VALUE;
        this.isPage = false;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, String repeatHeaderStr) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = Integer.MAX_VALUE;
        this.isPage = false;
        this.repeatHeaderStr = repeatHeaderStr;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, boolean isPage) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = Integer.MAX_VALUE;
        this.isPage = isPage;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, int maxDynamicCellSize) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = maxDynamicCellSize;
        this.isPage = false;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, int maxDynamicCellSize, boolean isPage) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = maxDynamicCellSize;
        this.isPage = isPage;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, int maxDynamicCellSize, String tableTitle) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = maxDynamicCellSize;
        this.isPage = false;
        this.tableTitle = tableTitle;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, String repeatHeaderStr, String tableTitle) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = Integer.MAX_VALUE;
        this.isPage = false;
        this.repeatHeaderStr = repeatHeaderStr;
        this.tableTitle = tableTitle;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, int maxDynamicCellSize, String tableTitle, String mergeSameColumn) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = maxDynamicCellSize;
        this.isPage = false;
        this.tableTitle = tableTitle;
        this.mergeSameColumn = mergeSameColumn;
    }

    public LimsMergeTemplateRenderData(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow, String mergeColumn, String repeatHeaderStr, String tableTitle, String mergeSameColumn) {
        super(dataList);
        this.mergeRow = mergeRow;
        this.mergeColumn = mergeColumn;
        this.maxDynamicCellSize = Integer.MAX_VALUE;
        this.isPage = false;
        this.tableTitle = tableTitle;
        this.repeatHeaderStr = repeatHeaderStr;
        this.mergeSameColumn = mergeSameColumn;
    }

    public void reComputeMergeRowAndColumn() {
        if (this.getData() != null) {
            this.dealWithMergeRow(this.getData(), this.mergeRow);
            this.dealWithMergeColumn(this.getData(), this.mergeColumn);
        }
    }

    private void dealWithMergeRow(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeRow) {
        Pattern perContentPattern = Pattern.compile("\\((?<content>[^\\(\\)]+?)\\)");
        if (StringUtils.isEmpty(mergeRow)) {
            return;
        }
        Matcher rowMatcher = perContentPattern.matcher(mergeRow);
        while (rowMatcher.find()) {
            String content = rowMatcher.group("content");
            String[] contentSplits = content.split("\\$");
            String mergeColumnStr = contentSplits[0].trim();
            this.mergedRowList.addAll(this.generateMergeRowList(dataList, Integer.parseInt(mergeColumnStr), dataMap -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < contentSplits.length; ++i) {
                    sb.append((Object)dataMap.getOrDefault(contentSplits[i].trim(), ""));
                    sb.append("-");
                }
                return sb.toString();
            }));
        }
    }

    private List<MergedRow> generateMergeRowList(LimsTemplateDataList<LimsTemplateDataMap> dataList, int column, Function<LimsTemplateDataMap, String> mapFunction) {
        ArrayList<MergedRow> mergedRowList = new ArrayList<MergedRow>();
        int dataLength = dataList.size();
        String lastColumnV = mapFunction.apply((LimsTemplateDataMap)dataList.get(0));
        int lastColumnIndex = 0;
        for (int i = 0; i < dataLength; ++i) {
            LimsTemplateDataMap dataMap = (LimsTemplateDataMap)dataList.get(i);
            String columnV = mapFunction.apply(dataMap);
            if (!columnV.equals(lastColumnV) || i == dataLength - 1) {
                int endTep;
                int startTep = lastColumnIndex;
                int n = endTep = !columnV.equals(lastColumnV) ? i - 1 : i;
                if (endTep - startTep > 0) {
                    MergedRow mergedRow = new MergedRow(column, startTep, endTep);
                    mergedRowList.add(mergedRow);
                }
                lastColumnIndex = i;
            }
            lastColumnV = columnV;
        }
        return mergedRowList;
    }

    private void dealWithMergeColumn(LimsTemplateDataList<LimsTemplateDataMap> dataList, String mergeColumn) {
        Pattern perContentPattern = Pattern.compile("\\((?<content>[^\\(\\)]+?)\\)");
        if (StringUtils.isEmpty(mergeColumn)) {
            return;
        }
        Matcher columnMatcher = perContentPattern.matcher(mergeColumn);
        while (columnMatcher.find()) {
            String content = columnMatcher.group("content");
            String[] contentSplits = content.split("\\$");
            if (contentSplits.length != 4) continue;
            this.mergedColumnList.addAll(this.generateMergeColumnList(dataList, contentSplits[0].trim(), contentSplits[1].trim(), Integer.parseInt(contentSplits[2].trim()), Integer.parseInt(contentSplits[3].trim())));
        }
    }

    private List<MergedColumn> generateMergeColumnList(LimsTemplateDataList<LimsTemplateDataMap> dataList, String keyColumn, String keyValue, int startColumn, int endColumn) {
        ArrayList<MergedColumn> mergedColumnList = new ArrayList<MergedColumn>();
        for (int i = 0; i < dataList.size(); ++i) {
            LimsTemplateDataMap dataMap = (LimsTemplateDataMap)dataList.get(i);
            String findKey = dataMap.getOrDefault(keyColumn, "") + "";
            if (!findKey.contains(keyValue)) continue;
            mergedColumnList.add(new MergedColumn(i, startColumn, endColumn));
        }
        return mergedColumnList;
    }

    public static class MergedColumn {
        private int rowIndex;
        private int startColumnIndex;
        private int endColumnIndex;

        public MergedColumn() {
        }

        public MergedColumn(int rowIndex, int startColumnIndex, int endColumnIndex) {
            this.rowIndex = rowIndex;
            this.startColumnIndex = startColumnIndex;
            this.endColumnIndex = endColumnIndex;
        }

        public int getRowIndex() {
            return this.rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getStartColumnIndex() {
            return this.startColumnIndex;
        }

        public void setStartColumnIndex(int startColumnIndex) {
            this.startColumnIndex = startColumnIndex;
        }

        public int getEndColumnIndex() {
            return this.endColumnIndex;
        }

        public void setEndColumnIndex(int endColumnIndex) {
            this.endColumnIndex = endColumnIndex;
        }
    }

    public static class MergedRow {
        private int columnIndex;
        private int startRowIndex;
        private int endRowIndex;

        public MergedRow() {
        }

        public MergedRow(int columnIndex, int startRowIndex, int endRowIndex) {
            this.columnIndex = columnIndex;
            this.startRowIndex = startRowIndex;
            this.endRowIndex = endRowIndex;
        }

        public int getColumnIndex() {
            return this.columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public int getStartRowIndex() {
            return this.startRowIndex;
        }

        public void setStartRowIndex(int startRowIndex) {
            this.startRowIndex = startRowIndex;
        }

        public int getEndRowIndex() {
            return this.endRowIndex;
        }

        public void setEndRowIndex(int endRowIndex) {
            this.endRowIndex = endRowIndex;
        }
    }
}
