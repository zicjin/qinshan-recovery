/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sunwayworld.framework.utils.DateTimeUtils
 *  com.sunwayworld.framework.utils.ObjectUtils
 *  com.sunwayworld.framework.utils.StringUtils
 *  org.apache.poi.xwpf.usermodel.XWPFDocument
 *  org.apache.poi.xwpf.usermodel.XWPFParagraph
 *  org.apache.poi.xwpf.usermodel.XWPFTable
 *  org.apache.poi.xwpf.usermodel.XWPFTableCell
 */
package com.sunwayworld.emc.module.bp.report.report.template.table;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.PfWord2007Utils;
import com.sunwayworld.emc.framework.word.Word2007Utils;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class NonSamplingSoilDynamicTable
implements DynamicTable {
    private static final int SMALL_FONT_SIZE = 8;
    private static final int[] FIXED_COLUMN_WIDTHS = new int[]{2368, 1297};
    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcItemGroupBean> itemGroupList;
    private List<EmcGroupItemBean> groupItemList;
    private List<EmcIndexCategoryBean> indexCategoryList;
    private int tableIndex;
    private boolean isFirst;
    private boolean isLast;
    private boolean mergeColumn;

    public NonSamplingSoilDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcReportResultBean> reportResultList, List<EmcIndexCategoryBean> indexCategoryList, int tableIndex, boolean isFirst, boolean isLast) {
        this.report = report;
        this.folderList = folderList;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.reportResultList = reportResultList;
        this.indexCategoryList = indexCategoryList;
        this.tableIndex = tableIndex;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.document = document;
        this.mergeColumn = orderList.size() < 4;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(this.isFirst ? "\u8868%d %s\u68c0\u6d4b\u7ed3\u679c" : "\u7eed\u8868%d %s\u68c0\u6d4b\u7ed3\u679c", this.tableIndex, this.folderList.get(0).getOrderCategory());
    }

    @Override
    public int getTableIndex() {
        return this.tableIndex;
    }

    @Override
    public boolean isLast() {
        return this.isLast;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public int getTableWidth() {
        return 10204;
    }

    @Override
    public WordTemplateDynamicTableDTO getDynamicTableDto() {
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();
        dynamicTableDTO.setFontStyle("Times New Roman");
        dynamicTableDTO.setFontSize(10);
        return dynamicTableDTO;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<String>();
        List<String> orderNoList = this.orderList.stream().map(EmcOrderBean::getOrderNo).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        int minResultColumnQty = Math.min(4, orderNoList.size());
        int marginColumnQty = Math.max(0, 4 - orderNoList.size());
        IntStream.range(0, minResultColumnQty).forEach(index -> {
            EmcOrderBean curOrder = this.orderList.get(index);
            dynamicTitleList.add(Objects.isNull(curOrder.getAcceptedDate()) ? null : DateTimeUtils.formatLocalDateTime((LocalDateTime)curOrder.getAcceptedDate(), (String)"yyyy.MM.dd"));
        });
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        List indexCategoryIdList = this.orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty((Object)task.getIndexCategoryId())).map(EmcOrderTaskBean::getIndexCategoryId).distinct().collect(Collectors.toList());
        if (indexCategoryIdList.isEmpty()) {
            dynamicTitleList.add(null);
        } else {
            EmcIndexCategoryBean indexCategory = this.indexCategoryList.stream().filter(ic -> indexCategoryIdList.contains(ic.getId())).findFirst().orElseGet(EmcIndexCategoryBean::new);
            dynamicTitleList.add(indexCategory.getStdName());
        }
        IntStream.range(0, minResultColumnQty).forEach(index -> {
            EmcOrderBean curOrder = this.orderList.get(index);
            String folderName = Optional.ofNullable(curOrder.getClientOrderName()).orElse(curOrder.getLabelNo());
            dynamicTitleList.add(folderName);
        });
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(orderNoList.get(index)));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        Map<String, String> orderNoDescMap = this.orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, this::concatOrderDesc, (v1, v2) -> v1));
        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(orderNoDescMap.get(this.orderList.get(index).getOrderNo())));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        List mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList());
        if (this.mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 4 - marginColumnQty, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 4 - marginColumnQty, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 4 - marginColumnQty, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 4 - marginColumnQty, 4));
        }
        List mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(5, 0, 4));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, 0, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(4, 1, 4));
        dynamicTableDTO.setMergedRowList(mergedRowList);
        dynamicTableDTO.setMergedColumnList(mergedColumnList);
        List<List<Object>> headerDataList = this.buildTableDynamicHeader(EmcReportConstant.NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_TITLES, 7, dynamicTitleList);
        this.setTableTitle(EmcReportConstant.NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_TITLES, 7, headerDataList, dynamicTableDTO);
        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<List<Object>>();
        List<EmcReportResultBean> sampleReportResultList = this.reportResultList.stream().filter(result -> StringUtils.equals((String)result.getDataType(), (String)"sample")).collect(Collectors.toList());
        boolean showLowLimit = "lowLimit".equalsIgnoreCase(this.report.getNdDisplayMode());
        sampleReportResultList.stream().filter(r -> "ND".equals(r.getReportResult())).forEach(r -> r.setReportResult(showLowLimit ? r.getLowLimit() + "L" : r.getReportResult()));
        List<String> orderNoList = sampleReportResultList.stream().map(EmcReportResultBean::getOrderNo).distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        int minResultColumnQty = Math.min(4, orderNoList.size());
        int marginColumnQty = Math.max(0, 4 - orderNoList.size());
        sampleReportResultList.stream().filter(r -> StringUtils.isEmpty((String)r.getExt$Item("analysisOrgId"))).forEach(r -> r.setExt$Item("analysisOrgId", ""));
        sampleReportResultList.stream().filter(r -> StringUtils.isEmpty((String)r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getTestName()));
        WordTemplateUtils.sortedResultByAnalysisOrg(sampleReportResultList);
        Map<String, List<EmcReportResultBean>> reportResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup"), LinkedHashMap::new, Collectors.toList()));
        AtomicInteger rowIndex = new AtomicInteger(4);
        List mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        Map<String, List<Long>> groupItemMap = this.convertGroupItem();
        reportResultMap.forEach((testName, testResultList) -> {
            List<Object> rowDataList = new ArrayList<Object>();
            List<Long> curItemIdList = Optional.ofNullable(groupItemMap.get(testName)).orElseGet(ArrayList::new);
            String itemGroup = testName.replaceAll("\u5c48", "\u44db");
            EmcReportResultBean unitResult = testResultList.stream().filter(r -> curItemIdList.contains(r.getItemId()) && r.getItemName().contains("\u6837\u54c1\u6d53\u5ea6")).findFirst().orElseGet(EmcReportResultBean::new);
            if (ObjectUtils.isEmpty((Object)unitResult.getId())) {
                unitResult = testResultList.stream().filter(r -> curItemIdList.contains(r.getItemId())).findFirst().orElseGet(EmcReportResultBean::new);
            }
            String unitName = unitResult.getUnitName();
            String showName = itemGroup + "(" + (StringUtils.isEmpty((String)unitName) ? "\u65e0\u91cf\u7eb2" : unitName) + ")";
            rowDataList.add(showName);
            List<String> indexHighLimitList = new ArrayList<>();
            List<EmcReportResultBean> lowLimitList = new ArrayList<>();
            IntStream.range(0, minResultColumnQty).forEach(i -> {
                EmcReportResultBean reportResult = testResultList.stream().filter(r -> StringUtils.equals(r.getOrderNo(), orderNoList.get(i)) && curItemIdList.contains(r.getItemId())).filter(r -> r.getItemName().contains("\u6837\u54c1\u6d53\u5ea6")).findFirst().orElseGet(EmcReportResultBean::new);
                if (ObjectUtils.isEmpty((Object)reportResult.getId())) {
                    reportResult = testResultList.stream().filter(r -> StringUtils.equals(r.getOrderNo(), orderNoList.get(i)) && curItemIdList.contains(r.getItemId())).findFirst().orElseGet(EmcReportResultBean::new);
                }
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                indexHighLimitList.add(StringUtils.isEmpty((String)reportResult.getIndexHighLimit()) ? null : reportResult.getIndexHighLimit());
                lowLimitList.add(reportResult);
            });
            IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));
            rowDataList.add(indexHighLimitList.isEmpty() ? null : indexHighLimitList.get(0));
            List<EmcReportResultBean> lowLimitNotEmptyList = lowLimitList.stream().filter(t -> !StringUtils.isEmpty(t.getLowLimit())).collect(Collectors.toList());
            EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : (EmcReportResultBean)((Object)((Object)lowLimitNotEmptyList.get(0)));
            String limitValue = StringUtils.isBlank((String)limitResult.getLowLimit()) ? "-" : limitResult.getLowLimit();
            rowDataList.add(limitValue);
            tableBodyDataList.add(rowDataList);
            if (this.mergeColumn) {
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(rowIndex.incrementAndGet(), 4 - marginColumnQty, 4));
            }
        });
        dynamicTableDTO.setMergedColumnList(mergedColumnList);
        return tableBodyDataList;
    }

    @Override
    public void create() {
        WordTemplateDynamicTableDTO dynamicTableDTO = this.buildTable();
        dynamicTableDTO.setTableWidth(this.getTableWidth());
        dynamicTableDTO.setCellHeight(this.getCellHeight());
        dynamicTableDTO.setColumnWidthList(this.getColumnWidthList());
        PfWord2007Utils.createDynamicTable(this.getDocument(), dynamicTableDTO);
        XWPFTable table = (XWPFTable)this.getDocument().getTables().get(this.getDynamicTableIndex());
        Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).ifPresent(list -> list.forEach(c -> Word2007Utils.mergeTableColumns(table, c.getRowIndex(), c.getStartColumnIndex(), c.getEndColumnIndex())));
        Optional.ofNullable(dynamicTableDTO.getMergedRowList()).ifPresent(list -> list.forEach(r -> Word2007Utils.mergeTableRows(table, r.getColumnIndex(), r.getStartRowIndex(), r.getEndRowIndex())));
        Optional.ofNullable(dynamicTableDTO.getTableImageList()).ifPresent(tableImageList -> tableImageList.forEach(i -> Word2007Utils.insertImageToTableCell(table, i.getRowIndex(), i.getColumnIndex(), i.getImageUrl())));
        if (!this.isLast()) {
            this.createTableEndText(this.getDocument(), table, 0);
        }
        this.resizeTableColumnWidth(table);
        if (!this.mergeColumn) {
            this.resizeTableFontSize(table, Arrays.asList(2, 3), 1, 5);
        }
    }

    public void setItemGroupList(List<EmcItemGroupBean> itemGroupList) {
        this.itemGroupList = itemGroupList;
    }

    public void setGroupItemList(List<EmcGroupItemBean> groupItemList) {
        this.groupItemList = groupItemList;
    }

    private void resizeTableColumnWidth(XWPFTable table) {
        int leftFixedColumnQty = 1;
        int rightFixedColumnQty = 2;
        int newAvgWidth = (this.getTableWidth() - (leftFixedColumnQty * FIXED_COLUMN_WIDTHS[0] + rightFixedColumnQty * FIXED_COLUMN_WIDTHS[1])) / this.orderList.size();
        List<Integer> columnWidthList = new ArrayList<>();
        IntStream.range(0, leftFixedColumnQty).forEach(index -> columnWidthList.add(FIXED_COLUMN_WIDTHS[0]));
        IntStream.range(0, this.orderList.size()).forEach(index -> columnWidthList.add(newAvgWidth));
        int mergedColumnQty = 4 - this.orderList.size();
        IntStream.range(0, mergedColumnQty).forEach(index -> columnWidthList.add(0));
        IntStream.range(0, rightFixedColumnQty).forEach(index -> columnWidthList.add(FIXED_COLUMN_WIDTHS[1]));
        List<XWPFTableRow> tableRows = table.getRows();
        tableRows.forEach(row -> {
            List<XWPFTableCell> tableCells = row.getTableCells();
            IntStream.range(0, tableCells.size()).forEach(index -> ((XWPFTableCell)tableCells.get(index)).setWidth(String.valueOf(columnWidthList.get(index))));
        });
    }

    private void resizeTableFontSize(XWPFTable table, List<Integer> rowIndexList, int startColumnIndex, int endColumnIndex) {
        List<XWPFTableRow> targetRows = new ArrayList<>(rowIndexList.size());
        rowIndexList.forEach(rowIndex -> targetRows.add(table.getRow(rowIndex.intValue())));
        targetRows.forEach(row -> {
            List<XWPFTableCell> tableCells = row.getTableCells();
            IntStream.range(startColumnIndex, endColumnIndex).forEach(index -> {
                XWPFTableCell targetCell = (XWPFTableCell)tableCells.get(index);
                XWPFParagraph paragraph = (XWPFParagraph)targetCell.getParagraphs().get(0);
                paragraph.getRuns().forEach(run -> run.setFontSize(8));
            });
        });
    }

    private String concatOrderDesc(EmcOrderBean order) {
        if (!StringUtils.isEmpty((String)order.getOrderDesc())) {
            return order.getOrderDesc();
        }
        if (this.isSolidWaste(order)) {
            return "\u56fa\u4f53";
        }
        List<String> orderDescList = new ArrayList<>();
        Arrays.asList("color", "texture", "humidity").forEach(property -> {
            if (!StringUtils.isEmpty((String)order.getExt$Item((String)property))) {
                orderDescList.add(order.getExt$Item((String)property));
            }
        });
        return String.join((CharSequence)"\u3001", orderDescList);
    }

    private boolean isSolidWaste(EmcOrderBean order) {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains((String)order.getOrderCategory(), (String)keyword));
    }

    private Map<String, List<Long>> convertGroupItem() {
        Map<String, List<Long>> convertedMap = new HashMap<String, List<Long>>(this.itemGroupList.size());
        List<Long> reportItemIdList = this.reportResultList.stream().filter(r -> "1".equals(r.getReportDisplay())).map(EmcReportResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcGroupItemBean> validGroupItemList = this.groupItemList.stream().filter(item -> reportItemIdList.contains(item.getItemId())).collect(Collectors.toList());
        Map<Long, List<EmcGroupItemBean>> groupItemMap = validGroupItemList.stream().collect(Collectors.groupingBy(EmcGroupItemBean::getItemGroupId));
        Map<Long, String> itemGroupMap = this.itemGroupList.stream().filter(group -> !StringUtils.isEmpty((String)group.getItemGroup())).collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));
        groupItemMap.forEach((itemGroupId, curItemList) -> convertedMap.put(Optional.ofNullable(itemGroupMap.get(itemGroupId)).orElse(""), curItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList())));
        return convertedMap;
    }
}
