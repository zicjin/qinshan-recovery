package com.sunwayworld.emc.module.bp.report.report.template.table.dioxin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexCategoryBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2023年09月14日
 */
public class DioxinResultTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcReportResultBean> reportResultList;
    private List<EmcIndexCategoryBean> indexCategoryList;
    private boolean sampling;
    private String tableTitle;
    private int subTitleIndex;

    public DioxinResultTable(XWPFDocument document, EmcProjectBean project, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList, List<EmcReportResultBean> reportResultList, List<EmcIndexCategoryBean> indexCategoryList, String tableTitle, int subTitleIndex) {
        this.document = document;
        this.folderList = folderList;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.reportResultList = reportResultList;
        this.indexCategoryList = indexCategoryList;
        this.sampling = Constant.YES.equals(project.getSample());
        this.tableTitle = tableTitle;
        this.subTitleIndex = subTitleIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return tableTitle;
    }

    @Override
    public String getTableSubTitle() {
        return String.format(EmcReportConstant.DIOXIN_RESULT_TABLE_SUB_TITLE, subTitleIndex, folderList.get(0).getOrderCategory());
    }

    @Override
    public int getTableIndex() {
        return 3;
    }

    @Override
    public int getDynamicTableIndex() {
        return 3;
    }

    @Override
    public boolean isLast() {
        return false;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(0D, 0D, 0D, 0D, 2.88D, 2.0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();

        // 过滤出样品值结果，判断是否有设置评价标准
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());
        EmcReportResultBean indexReportResult = sampleReportResultList.stream().filter(result -> !ObjectUtils.isEmpty(result.getIndexId())).findFirst().orElseGet(EmcReportResultBean::new);
        if (Objects.nonNull(indexReportResult.getIndexId())) {
            EmcOrderTaskBean indexOrderTask = orderTaskList.stream().filter(task -> ObjectUtils.equals(task.getId(), indexReportResult.getOrderTaskId())).findFirst().orElseGet(EmcOrderTaskBean::new);
            EmcIndexCategoryBean indexCategory = indexCategoryList.stream().filter(ic -> ObjectUtils.equals(ic.getId(), indexOrderTask.getIndexCategoryId())).findFirst().orElseGet(EmcIndexCategoryBean::new);
            dynamicTitleList.add(indexCategory.getStdName());
        } else {
            dynamicTitleList.add(null);
        }

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        if (!sampling) {
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 1));
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 1));
        }

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(4, 0, 1));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(5, 0, 1));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        String[] headerTitles = sampling ? EmcReportConstant.DIOXIN_RESULT_TABLE_HEADER_TITLES : EmcReportConstant.DIOXIN_RESULT_TABLE_HEADER_TITLES_2;
        List<List<Object>> headerDataList = buildTableDynamicHeader(headerTitles, EmcReportConstant.DIOXIN_RESULT_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(headerTitles, EmcReportConstant.DIOXIN_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        // 添加备注
        dynamicTableDTO.setTableRemarkList(Collections.singletonList(String.format(EmcReportConstant.DIOXIN_RESULT_TABLE_REMARK, subTitleIndex)));

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);

        AtomicInteger rowIndex = new AtomicInteger(1);
        folderList.sort(Comparator.comparing(EmcFolderBean::getId));
        folderList.forEach(folder -> {
            List<EmcOrderBean> curOrderList = orderList.stream().filter(order -> ObjectUtils.equals(order.getFolderId(), folder.getId())).collect(Collectors.toList());
            curOrderList.forEach(order -> {
                List<Object> rowDataList = new ArrayList<>();
                String sampleTime = DateTimeUtils.formatLocalDate(order.getSampleBeginDate()) + (StringUtils.isEmpty(order.getSampleBeginTime()) ? "" : " " + order.getSampleBeginTime());
                rowDataList.add(sampling ? sampleTime : order.getClientOrderName());
                rowDataList.add(sampling ? folder.getMonitorPointName() : "");

                EmcReportResultBean reportResult = sampleReportResultList.stream().filter(result -> ObjectUtils.equals(order.getId(), result.getOrderId())
                        && StringUtils.contains(result.getItemAliasName(), EmcReportConstant.DIOXIN_KEYWORD)).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(reportResult.getTestName());
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                rowDataList.add(reportResult.getIndexHighLimit());
                rowDataList.add(reportResult.getUnitName());
                tableBodyDataList.add(rowDataList);

                if (!sampling) {
                    mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(rowIndex.incrementAndGet(), 0, 1));
                }
            });
        });

        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        return tableBodyDataList;
    }
}
