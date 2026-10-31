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
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class SoilDynamicTable
implements DynamicTable {
    private static final int SMALL_FONT_SIZE = 8;
    private static final int[] FIXED_COLUMN_WIDTHS = new int[]{2368, 1297};
    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcItemGroupBean> itemGroupList;
    private List<EmcGroupItemBean> groupItemList;
    private int tableIndex;
    private boolean isFirst;
    private boolean isLast;
    private boolean mergeColumn;
    private boolean sampling;
    private boolean displayStd;

    public SoilDynamicTable(XWPFDocument document, EmcProjectBean project, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, int tableIndex, boolean isFirst, boolean isLast) {
        this.report = report;
        this.folderList = folderList;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.tableIndex = tableIndex;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.document = document;
        this.mergeColumn = orderList.size() < 4;
        this.sampling = "1".equals(project.getSample());
        this.displayStd = StringUtils.equals((String)"1", (String)report.getExt$Item("displayStd"));
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
        List<Object> dynamicTitleList = new ArrayList<Object>();
        List<String> orderNoList = this.orderList.stream().map(EmcOrderBean::getOrderNo).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        int minResultColumnQty = Math.min(4, orderNoList.size());
        int marginColumnQty = Math.max(0, 4 - orderNoList.size());
        if (this.sampling) {
            IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(DateTimeUtils.formatLocalDate((LocalDate)this.orderList.get(index).getSampleBeginDate(), (String)"yyyy.MM.dd")));
            IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
            Map folderIdNameMap = this.folderList.stream().filter(folder -> !StringUtils.isEmpty((String)folder.getMonitorPointName())).collect(Collectors.toMap(EmcFolderBean::getId, EmcFolderBean::getMonitorPointName));
            IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(folderIdNameMap.get(this.orderList.get(index).getFolderId())));
            IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        } else {
            IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(DateTimeUtils.formatLocalDate((LocalDate)this.orderList.get(index).getSendDate(), (String)"yyyy.MM.dd")));
            IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
            IntStream.range(0, minResultColumnQty).forEach(index -> {
                EmcOrderBean curOrder = this.orderList.get(index);
                String folderName = Optional.ofNullable(curOrder.getClientOrderName()).orElse(curOrder.getLabelNo());
                dynamicTitleList.add(folderName);
            });
            IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        }
        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(orderNoList.get(index)));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        if (this.sampling) {
            Map<String, String> samplingDepthMap = this.orderList.stream().filter(order -> !StringUtils.isEmpty((String)order.getExt$Item("samplingDepth"))).collect(Collectors.toMap(EmcOrderBean::getOrderNo, order -> order.getExt$Item("samplingDepth"), (v1, v2) -> v1));
            IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(samplingDepthMap.get(orderNoList.get(index))));
            IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        }
        Map<String, String> orderNoDescMap = this.orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, this::concatOrderDesc, (v1, v2) -> v1));
        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(orderNoDescMap.get(this.orderList.get(index).getOrderNo())));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));
        List mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList());
        if (this.mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 4 - marginColumnQty, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 4 - marginColumnQty, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 4 - marginColumnQty, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 4 - marginColumnQty, 4));
            if (this.sampling) {
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(4, 4 - marginColumnQty, 4));
            }
        }
        List mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        if (this.sampling) {
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, 0, 5));
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(7, 0, 5));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(5, 1, 4));
        } else {
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(6, 0, 4));
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(7, 0, 4));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(4, 1, 4));
        }
        dynamicTableDTO.setMergedRowList(mergedRowList);
        dynamicTableDTO.setMergedColumnList(mergedColumnList);
        String[] tableTitles = this.lookupTableTitle();
        int tableColumnQty = this.displayStd ? 8 : 7;
        List<List<Object>> headerDataList = this.buildTableDynamicHeader(tableTitles, tableColumnQty, (List) dynamicTitleList);
        this.setTableTitle(tableTitles, tableColumnQty, headerDataList, dynamicTableDTO);
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
        AtomicInteger rowIndex = new AtomicInteger(this.sampling ? 5 : 4);
        List mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        Map<String, List<Long>> groupItemMap = this.convertGroupItem();
        reportResultMap.forEach((testName, testResultList) -> {
            List<Object> rowDataList = new ArrayList<Object>();
            String itemGroup = testName.replaceAll("\u5c48", "\u44db");
            if (((EmcReportResultBean)((Object)((Object)testResultList.get(0)))).getUnitName() != null && ((EmcReportResultBean)((Object)((Object)testResultList.get(0)))).getUnitName() != "") {
                rowDataList.add(itemGroup + "(" + Optional.ofNullable(((EmcReportResultBean)((Object)((Object)testResultList.get(0)))).getUnitName()).orElse("\u65e0\u91cf\u7eb2") + ")");
            } else {
                rowDataList.add(itemGroup);
            }
            List<EmcReportResultBean> lowLimitList = new ArrayList<>();
            IntStream.range(0, minResultColumnQty).forEach(i -> {
                List<Long> curItemIdList = Optional.ofNullable(groupItemMap.get(testName)).orElseGet(ArrayList::new);
                EmcReportResultBean reportResult = new EmcReportResultBean();
                reportResult = testResultList.stream().filter(r -> StringUtils.equals(r.getOrderNo(), orderNoList.get(i)) && curItemIdList.contains(r.getItemId())).filter(r -> r.getItemName().contains("\u6837\u54c1\u6d53\u5ea6")).findFirst().orElseGet(EmcReportResultBean::new);
                if (ObjectUtils.isEmpty((Object)reportResult.getId())) {
                    reportResult = testResultList.stream().filter(r -> StringUtils.equals(r.getOrderNo(), orderNoList.get(i)) && curItemIdList.contains(r.getItemId())).findFirst().orElseGet(EmcReportResultBean::new);
                }
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                lowLimitList.add(reportResult);
            });
            IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));
            List<EmcReportResultBean> lowLimitNotEmptyList = lowLimitList.stream().filter(t -> !StringUtils.isEmpty(t.getLowLimit())).collect(Collectors.toList());
            EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : (EmcReportResultBean)((Object)((Object)lowLimitNotEmptyList.get(0)));
            String limitValue = StringUtils.isBlank((String)limitResult.getLowLimit()) ? "-" : limitResult.getLowLimit() + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
            rowDataList.add(limitValue);
            if (this.displayStd) {
                rowDataList.add(null);
            }
            tableBodyDataList.add(rowDataList);
            if (this.mergeColumn) {
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(rowIndex.incrementAndGet(), 4 - marginColumnQty, 4));
            }
        });
        if (this.displayStd) {
            tableBodyDataList.add(this.buildDynamicRowDataList("\u6267\u884c\u6807\u51c6", this.report.getRemark(), 8));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size() + (this.sampling ? 5 : 4), 1, 7));
        }
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
            if (!this.sampling) {
                this.resizeTableFontSize(table, Arrays.asList(2, 3), 1, 5);
            } else {
                this.resizeTableFontSize(table, Arrays.asList(2, 4), 1, 5);
            }
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
        int rightFixedColumnQty = this.displayStd ? 2 : 1;
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
            if (tableCells.size() > columnWidthList.size()) {
                return;
            }
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

    private String[] lookupTableTitle() {
        if (this.sampling && this.displayStd) {
            return PfConstant.SOIL_RESULT_TABLE_HEADER_TITLES;
        }
        if (this.sampling) {
            return PfConstant.SOIL_RESULT_TABLE_HEADER_TITLES_2;
        }
        if (this.displayStd) {
            return PfConstant.SOIL_RESULT_TABLE_HEADER_TITLES_3;
        }
        return PfConstant.SOIL_RESULT_TABLE_HEADER_TITLES_4;
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
