package com.sunwayworld.emc.module.bp.report.report.bean;

import java.util.List;

/**
 * Word模板中动态表格数据（完全通过程序生成的表格）
 * @author zhengxy@sunwayworld.com 2020年06月10日
 */
public class WordTemplateDynamicTableDTO {

    /** 占位符关键字 */
    private String replaceKeyword;
    /** 表格标题 */
    private String tableTitle;
    /** 表格单位 */
    private String tableUnit;
    /** 表格数据 */
    private List<List<Object>> tableDataList;
    /** 表格头数据 */
    private List<List<TableTitle>> tableTitleList;
    /** 表格正文数据 */
    private List<List<Object>> tableBodyList;
    /** 表格宽度 */
    private Integer tableWidth;
    /** 单元格高度 */
    private Integer cellHeight;
    /** 字体大小 */
    private Integer fontSize;
    /** 字体样式 */
    private String fontStyle;
    /** 需要合并的行列表 */
    private List<MergedRow> mergedRowList;
    /** 需要合并的列列表 */
    private List<MergedColumn> mergedColumnList;
    /** 表格图片列表 */
    private List<TableImage> tableImageList;
    /** 分页后重复的表头行索引列表 */
    private List<Integer> repeatHeaderRowIndexList;
    /** 表格列宽度 */
    private List<Integer> columnWidthList;
    /** 表格备注 */
    private List<String> tableRemarkList;

    private Integer tableBorderWidth;
    private Boolean showBorder;
    private Boolean showCellBorder;
    private Boolean repeatHeader;
    private String tableSubTitle;

    public WordTemplateDynamicTableDTO() {
        this.showBorder = true;
        this.showCellBorder = true;
        this.repeatHeader = true;
    }

    public String getReplaceKeyword() {
        return replaceKeyword;
    }

    public void setReplaceKeyword(String replaceKeyword) {
        this.replaceKeyword = replaceKeyword;
    }

    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }

    public String getTableUnit() {
        return tableUnit;
    }

    public void setTableUnit(String tableUnit) {
        this.tableUnit = tableUnit;
    }

    public List<List<Object>> getTableDataList() {
        return tableDataList;
    }

    public void setTableDataList(List<List<Object>> tableDataList) {
        this.tableDataList = tableDataList;
    }

    public List<List<TableTitle>> getTableTitleList() {
        return tableTitleList;
    }

    public void setTableTitleList(List<List<TableTitle>> tableTitleList) {
        this.tableTitleList = tableTitleList;
    }

    public List<List<Object>> getTableBodyList() {
        return tableBodyList;
    }

    public void setTableBodyList(List<List<Object>> tableBodyList) {
        this.tableBodyList = tableBodyList;
    }

    public Integer getTableWidth() {
        return tableWidth;
    }

    public void setTableWidth(Integer tableWidth) {
        this.tableWidth = tableWidth;
    }

    public Integer getCellHeight() {
        return cellHeight;
    }

    public void setCellHeight(Integer cellHeight) {
        this.cellHeight = cellHeight;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public List<MergedRow> getMergedRowList() {
        return mergedRowList;
    }

    public void setMergedRowList(List<MergedRow> mergedRowList) {
        this.mergedRowList = mergedRowList;
    }

    public List<MergedColumn> getMergedColumnList() {
        return mergedColumnList;
    }

    public void setMergedColumnList(List<MergedColumn> mergedColumnList) {
        this.mergedColumnList = mergedColumnList;
    }

    public List<TableImage> getTableImageList() {
        return tableImageList;
    }

    public void setTableImageList(List<TableImage> tableImageList) {
        this.tableImageList = tableImageList;
    }

    public List<Integer> getRepeatHeaderRowIndexList() {
        return repeatHeaderRowIndexList;
    }

    public void setRepeatHeaderRowIndexList(List<Integer> repeatHeaderRowIndexList) {
        this.repeatHeaderRowIndexList = repeatHeaderRowIndexList;
    }

    public List<Integer> getColumnWidthList() {
        return columnWidthList;
    }

    public void setColumnWidthList(List<Integer> columnWidthList) {
        this.columnWidthList = columnWidthList;
    }

    public List<String> getTableRemarkList() {
        return tableRemarkList;
    }

    public void setTableRemarkList(List<String> tableRemarkList) {
        this.tableRemarkList = tableRemarkList;
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
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public int getStartRowIndex() {
            return startRowIndex;
        }

        public void setStartRowIndex(int startRowIndex) {
            this.startRowIndex = startRowIndex;
        }

        public int getEndRowIndex() {
            return endRowIndex;
        }

        public void setEndRowIndex(int endRowIndex) {
            this.endRowIndex = endRowIndex;
        }
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
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getStartColumnIndex() {
            return startColumnIndex;
        }

        public void setStartColumnIndex(int startColumnIndex) {
            this.startColumnIndex = startColumnIndex;
        }

        public int getEndColumnIndex() {
            return endColumnIndex;
        }

        public void setEndColumnIndex(int endColumnIndex) {
            this.endColumnIndex = endColumnIndex;
        }
    }

    public static class TableTitle {
        private String title;
        private boolean bold;

        public TableTitle() {
        }

        public TableTitle(String title) {
            this.title = title;
        }

        public TableTitle(String title, boolean bold) {
            this.title = title;
            this.bold = bold;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isBold() {
            return bold;
        }

        public void setBold(boolean bold) {
            this.bold = bold;
        }
    }

    public static class TableImage {
        private int rowIndex;
        private int columnIndex;
        private String imageUrl;

        public TableImage() {
        }

        public TableImage(int rowIndex, int columnIndex, String imageUrl) {
            this.rowIndex = rowIndex;
            this.columnIndex = columnIndex;
            this.imageUrl = imageUrl;
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public void setColumnIndex(int columnIndex) {
            this.columnIndex = columnIndex;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }

    public Integer getTableBorderWidth() {
        return tableBorderWidth;
    }

    public void setTableBorderWidth(Integer tableBorderWidth) {
        this.tableBorderWidth = tableBorderWidth;
    }

    public Boolean getShowBorder() {
        return showBorder;
    }

    public void setShowBorder(Boolean showBorder) {
        this.showBorder = showBorder;
    }

    public Boolean getShowCellBorder() {
        return showCellBorder;
    }

    public void setShowCellBorder(Boolean showCellBorder) {
        this.showCellBorder = showCellBorder;
    }

    public Boolean getRepeatHeader() {
        return repeatHeader;
    }

    public void setRepeatHeader(Boolean repeatHeader) {
        this.repeatHeader = repeatHeader;
    }

    public String getTableSubTitle() {
        return tableSubTitle;
    }

    public void setTableSubTitle(String tableSubTitle) {
        this.tableSubTitle = tableSubTitle;
    }
}
