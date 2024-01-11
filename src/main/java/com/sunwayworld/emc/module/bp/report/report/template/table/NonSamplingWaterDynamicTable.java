package com.sunwayworld.emc.module.bp.report.report.template.table;

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

import com.sunwayworld.emc.framework.constant.EmcConstant;
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
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 送样水检测结果表（综合报告）
 * @author zhengxy@sunwayworld.com 2020年09月03日
 */
public class NonSamplingWaterDynamicTable implements DynamicTable {

    private static final int SMALL_FONT_SIZE = 8;
    private static final int[] FIXED_COLUMN_WIDTHS = {2368, 1297};

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

    public NonSamplingWaterDynamicTable(XWPFDocument document,
                                        EmcReportBean report,
                                        List<EmcFolderBean> folderList,
                                        List<EmcOrderBean> orderList,
                                        List<EmcOrderTaskBean> orderTaskList,
                                        List<EmcReportResultBean> reportResultList,
                                        List<EmcIndexCategoryBean> indexCategoryList,
                                        int tableIndex,
                                        boolean isFirst,
                                        boolean isLast) {
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
        this.mergeColumn = orderList.size() < PfConstant.MAX_COLUMN_QTY;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return String.format(isFirst ? PfConstant.SOIL_RESULT_TABLE_TITLE : "续" + PfConstant.SOIL_RESULT_TABLE_TITLE, tableIndex, folderList.get(0).getOrderCategory());
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
        // 默认表格宽度为 18cm
        return 10204;
    }

    @Override
    public WordTemplateDynamicTableDTO getDynamicTableDto() {
        // 设置表格字体为 Times New Roman，字号为 10
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();
        dynamicTableDTO.setFontStyle(EmcReportConstant.DEFAULT_FONT_FAMILY);
        dynamicTableDTO.setFontSize(10);

        return dynamicTableDTO;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();

        List<String> orderNoList = orderList.stream().map(EmcOrderBean::getOrderNo).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        int minResultColumnQty = Math.min(PfConstant.MAX_COLUMN_QTY, orderNoList.size());
        int marginColumnQty = Math.max(0, PfConstant.MAX_COLUMN_QTY - orderNoList.size());

        // 获取采样日期，如果采样日期不一致，则取最小采样日期和最大的采样日期
        IntStream.range(0, minResultColumnQty).forEach(index -> {
            EmcOrderBean curOrder = orderList.get(index);
            dynamicTitleList.add(Objects.isNull(curOrder.getAcceptedDate()) ? null : DateTimeUtils.formatLocalDateTime(curOrder.getAcceptedDate(), EmcConstant.TABLE_DATE_PATTERN));
        });
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));

        List<Long> indexCategoryIdList = orderTaskList.stream().filter(task -> !ObjectUtils.isEmpty(task.getIndexCategoryId())).map(EmcOrderTaskBean::getIndexCategoryId).distinct().collect(Collectors.toList());
        if (indexCategoryIdList.isEmpty()) {
            dynamicTitleList.add(null);
        } else {
            EmcIndexCategoryBean indexCategory = indexCategoryList.stream().filter(ic -> indexCategoryIdList.contains(ic.getId())).findFirst().orElseGet(EmcIndexCategoryBean::new);
            dynamicTitleList.add(indexCategory.getStdName());
        }

        IntStream.range(0, minResultColumnQty).forEach(index -> {
            EmcOrderBean curOrder = orderList.get(index);
            String folderName = Optional.ofNullable(curOrder.getClientOrderName()).orElse(curOrder.getLabelNo());
            dynamicTitleList.add(folderName);
        });
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));

        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(orderNoList.get(index)));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));

        // 样品描述
        Map<String, String> orderNoDescMap = orderList.stream().collect(Collectors.toMap(EmcOrderBean::getOrderNo, this::concatOrderDesc, (v1, v2) -> v1));
        IntStream.range(0, minResultColumnQty).forEach(index -> dynamicTitleList.add(orderNoDescMap.get(orderList.get(index).getOrderNo())));
        IntStream.range(0, marginColumnQty).forEach(index -> dynamicTitleList.add(null));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElse(new ArrayList<>());
        if (mergeColumn) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, PfConstant.MAX_COLUMN_QTY - marginColumnQty, PfConstant.MAX_COLUMN_QTY));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, PfConstant.MAX_COLUMN_QTY - marginColumnQty, PfConstant.MAX_COLUMN_QTY));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, PfConstant.MAX_COLUMN_QTY - marginColumnQty, PfConstant.MAX_COLUMN_QTY));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, PfConstant.MAX_COLUMN_QTY - marginColumnQty, PfConstant.MAX_COLUMN_QTY));
        }

        // 合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(PfConstant.MAX_COLUMN_QTY + 1, 0, 4));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(PfConstant.MAX_COLUMN_QTY + 2, 0, 4));

        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(4, 1, PfConstant.MAX_COLUMN_QTY));

        dynamicTableDTO.setMergedRowList(mergedRowList);
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcReportConstant.NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_TITLES, EmcReportConstant.NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcReportConstant.NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_TITLES, EmcReportConstant.NON_SAMPLING_SOIL_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        // 未检出表示方式
        boolean showLowLimit = EmcReportConstant.LOW_LIMIT_DISPLAY_MODE.equalsIgnoreCase(report.getNdDisplayMode());
        sampleReportResultList.stream().filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(showLowLimit ? r.getLowLimit() + "L" : r.getReportResult()));

        List<String> orderNoList = sampleReportResultList.stream().map(EmcReportResultBean::getOrderNo).distinct().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        int minResultColumnQty = Math.min(PfConstant.MAX_COLUMN_QTY, orderNoList.size());
        int marginColumnQty = Math.max(0, PfConstant.MAX_COLUMN_QTY - orderNoList.size());

        // 先根据分析科室、监测方案和测试对结果数据进行排序，然后再按分析项组进行分组
        sampleReportResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("analysisOrgId"))).forEach(r -> r.setExt$Item("analysisOrgId", ""));
        sampleReportResultList.sort(Comparator.comparing(result -> result.getExt$Item("testSchemeName") + result.getTestId()));

        sampleReportResultList.stream().filter(r -> StringUtils.isEmpty(r.getExt$Item("itemGroup"))).forEach(r -> r.setExt$Item("itemGroup", r.getTestName()));
        WordTemplateUtils.sortedResultByAnalysisOrg(sampleReportResultList);

        Map<String, List<EmcReportResultBean>> reportResultMap = sampleReportResultList.stream().collect(Collectors.groupingBy(r -> r.getExt$Item("itemGroup"), LinkedHashMap::new, Collectors.toList()));

        AtomicInteger rowIndex = new AtomicInteger(4);
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        Map<String, List<Long>> groupItemMap = convertGroupItem();

        reportResultMap.forEach((testName, testResultList) -> {
            List<Object> rowDataList = new ArrayList<>();
            String itemGroup = testName.replaceAll(PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME);
            rowDataList.add(itemGroup);

            List<String> indexHighLimitList = new ArrayList<>();
            List<EmcReportResultBean> lowLimitList = new ArrayList<>();
            IntStream.range(0, minResultColumnQty).forEach(i -> {
                // 对点位下的所有样品的报出结果求均值
                List<Long> curItemIdList = Optional.ofNullable(groupItemMap.get(testName)).orElseGet(ArrayList::new);
                //如果没有样品浓度就随便取，没有取取第一个
                EmcReportResultBean reportResult = new EmcReportResultBean();
                reportResult = testResultList.stream().filter(r -> StringUtils.equals(r.getOrderNo(), orderNoList.get(i)) && curItemIdList.contains(r.getItemId()))
                        .filter(r ->r.getItemName().contains(PfConstant.SAMPLECONCENTRATION)).findFirst().orElseGet(EmcReportResultBean::new);
                if(ObjectUtils.isEmpty(reportResult.getId())){
                    reportResult = testResultList.stream().filter(r -> StringUtils.equals(r.getOrderNo(), orderNoList.get(i)) && curItemIdList.contains(r.getItemId()))
                            .findFirst().orElseGet(EmcReportResultBean::new);
                }

                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                indexHighLimitList.add(StringUtils.isEmpty(reportResult.getIndexHighLimit()) ? null : reportResult.getIndexHighLimit() + reportResult.getUnitName());
                lowLimitList.add(reportResult);
            });
            IntStream.range(0, marginColumnQty).forEach(i -> rowDataList.add(null));

            // 执行标准
            rowDataList.add(indexHighLimitList.isEmpty() ? null : indexHighLimitList.get(0));

            // 方法检出限
            List<EmcReportResultBean> lowLimitNotEmptyList = lowLimitList.stream().filter(t ->!StringUtils.isEmpty(t.getLowLimit())).collect(Collectors.toList());
            EmcReportResultBean limitResult = lowLimitNotEmptyList.isEmpty() ? new EmcReportResultBean() : lowLimitNotEmptyList.get(0);
            String limitValue = StringUtils.isBlank(limitResult.getLowLimit()) ? "-" : limitResult.getLowLimit() + Optional.ofNullable(limitResult.getExt$Item("limitUnitName")).orElse(limitResult.getUnitName());
            rowDataList.add(limitValue);

            tableBodyDataList.add(rowDataList);

            if (mergeColumn) {
                mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(rowIndex.incrementAndGet(), PfConstant.MAX_COLUMN_QTY - marginColumnQty, PfConstant.MAX_COLUMN_QTY));
            }
        });

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }

    @Override
    public void create() {
        WordTemplateDynamicTableDTO dynamicTableDTO = buildTable();
        dynamicTableDTO.setTableWidth(getTableWidth());
        dynamicTableDTO.setCellHeight(getCellHeight());
        dynamicTableDTO.setColumnWidthList(getColumnWidthList());
        PfWord2007Utils.createDynamicTable(getDocument(), dynamicTableDTO);

        XWPFTable table = getDocument().getTables().get(getDynamicTableIndex());
        Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).ifPresent(list -> list.forEach(c -> Word2007Utils.mergeTableColumns(table, c.getRowIndex(), c.getStartColumnIndex(), c.getEndColumnIndex())));
        Optional.ofNullable(dynamicTableDTO.getMergedRowList()).ifPresent(list -> list.forEach(r -> Word2007Utils.mergeTableRows(table, r.getColumnIndex(), r.getStartRowIndex(), r.getEndRowIndex())));

        // 插入图片
        Optional.ofNullable(dynamicTableDTO.getTableImageList()).ifPresent(tableImageList -> tableImageList.forEach(i -> Word2007Utils.insertImageToTableCell(table, i.getRowIndex(), i.getColumnIndex(), i.getImageUrl())));

        if (!isLast()) {
            createTableEndText(getDocument(), table, 0);
        }

        // 重新调整表格列宽
        resizeTableColumnWidth(table);

        // 如果存在5列样品结果，则调整字体大小
        if (!mergeColumn) {
            // 样品编号和样品描述行，分别位于第3行和第4行，索引为2、3
            resizeTableFontSize(table, Arrays.asList(2, 3), 1, PfConstant.MAX_COLUMN_QTY + 1);
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

        int newAvgWidth = (getTableWidth() - (leftFixedColumnQty * FIXED_COLUMN_WIDTHS[0] + rightFixedColumnQty * FIXED_COLUMN_WIDTHS[1])) / orderList.size();

        List<Integer> columnWidthList = new ArrayList<>();
        IntStream.range(0, leftFixedColumnQty).forEach(index -> columnWidthList.add(FIXED_COLUMN_WIDTHS[0]));
        IntStream.range(0, orderList.size()).forEach(index -> columnWidthList.add(newAvgWidth));

        int mergedColumnQty = PfConstant.MAX_COLUMN_QTY - orderList.size();
        IntStream.range(0, mergedColumnQty).forEach(index -> columnWidthList.add(0));

        IntStream.range(0, rightFixedColumnQty).forEach(index -> columnWidthList.add(FIXED_COLUMN_WIDTHS[1]));

        List<XWPFTableRow> tableRows = table.getRows();
        tableRows.forEach(row -> {
            List<XWPFTableCell> tableCells = row.getTableCells();
            IntStream.range(0, tableCells.size()).forEach(index -> tableCells.get(index).setWidth(String.valueOf(columnWidthList.get(index))));
        });
    }

    private void resizeTableFontSize(XWPFTable table, List<Integer> rowIndexList, int startColumnIndex, int endColumnIndex) {
        List<XWPFTableRow> targetRows = new ArrayList<>(rowIndexList.size());
        rowIndexList.forEach(rowIndex -> targetRows.add(table.getRow(rowIndex)));

        targetRows.forEach(row -> {
            List<XWPFTableCell> tableCells = row.getTableCells();
            IntStream.range(startColumnIndex, endColumnIndex).forEach(index -> {
                XWPFTableCell targetCell = tableCells.get(index);
                XWPFParagraph paragraph = targetCell.getParagraphs().get(0);
                paragraph.getRuns().forEach(run -> run.setFontSize(SMALL_FONT_SIZE));
            });
        });
    }

    private String concatOrderDesc(EmcOrderBean order) {
        // 对于送样的，样品描述会存于orderDesc字段
        if (!StringUtils.isEmpty(order.getOrderDesc())) {
            return order.getOrderDesc();
        }

        // 对于固体废物类型，采样的样品描述统一为固定值：固体
        if (isSolidWaste(order)) {
            return PfConstant.DIOXIN_SOLID_WASTE_ORDER_DESC;
        }

        List<String> orderDescList = new ArrayList<>();
        Arrays.asList("color", "texture", "humidity").forEach(property -> {
            if (!StringUtils.isEmpty(order.getExt$Item(property))) {
                orderDescList.add(order.getExt$Item(property));
            }
        });

        return String.join("、", orderDescList);
    }

    private boolean isSolidWaste(EmcOrderBean order) {
        return Arrays.stream(PfConstant.DIOXIN_SOLID_WASTE_ORDER_CATEGORIES).anyMatch(keyword -> StringUtils.contains(order.getOrderCategory(), keyword));
    }

    private Map<String, List<Long>> convertGroupItem() {
        Map<String, List<Long>> convertedMap = new HashMap<>(itemGroupList.size());
        List<Long> reportItemIdList = reportResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).map(EmcReportResultBean::getItemId).distinct().collect(Collectors.toList());
        List<EmcGroupItemBean> validGroupItemList = groupItemList.stream().filter(item -> reportItemIdList.contains(item.getItemId())).collect(Collectors.toList());
        Map<Long, List<EmcGroupItemBean>> groupItemMap = validGroupItemList.stream().collect(Collectors.groupingBy(EmcGroupItemBean::getItemGroupId));

        Map<Long, String> itemGroupMap = itemGroupList.stream().filter(group -> !StringUtils.isEmpty(group.getItemGroup())).collect(Collectors.toMap(EmcItemGroupBean::getId, EmcItemGroupBean::getItemGroup, (v1, v2) -> v1));

        groupItemMap.forEach((itemGroupId, curItemList) -> convertedMap.put(Optional.ofNullable(itemGroupMap.get(itemGroupId)).orElse(""), curItemList.stream().map(EmcGroupItemBean::getItemId).collect(Collectors.toList())));

        return convertedMap;
    }

}
