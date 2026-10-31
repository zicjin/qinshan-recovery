package com.sunwayworld.emc.module.bp.report.report.template.table.dioxin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2023年09月15日
 */
public class DioxinGasParameterTable implements DynamicTable {

    private XWPFDocument document;
    private EmcFolderBean folder;
    private List<EmcOrderBean> orderList;
    private List<EmcReportResultBean> reportResultList;
    private boolean endOfReport;

    public DioxinGasParameterTable(XWPFDocument document, EmcFolderBean folder, List<EmcOrderBean> orderList, List<EmcReportResultBean> reportResultList, boolean endOfReport) {
        this.document = document;
        this.folder = folder;
        this.orderList = orderList;
        this.reportResultList = reportResultList;
        this.endOfReport = endOfReport;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return EmcReportConstant.DIOXIN_GAS_PARAMETER_TABLE_TITLE;
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
    public String getTableEndText() {
        return this.endOfReport ? EmcReportConstant.END_OF_REPORT : PfConstant.TABLE_END_TIP_MESSAGE;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList( 0D, 0D, 0D, 0D, 0D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();
        dynamicTitleList.add(String.format(EmcReportConstant.DIOXIN_GAS_PARAMETER_KEYWORDS[0], folder.getMonitorPointName()));
        dynamicTitleList.add(String.format(EmcReportConstant.DIOXIN_GAS_PARAMETER_KEYWORDS[1], reportResultList.get(0).getTestName()));

        List<LocalDate> sampleDateList = orderList.stream().map(EmcOrderBean::getSampleBeginDate).filter(Objects::nonNull).collect(Collectors.toList());
        String sampleDate = WordTemplateUtils.getDate(sampleDateList);
        dynamicTitleList.add(String.format(EmcReportConstant.DIOXIN_GAS_PARAMETER_KEYWORDS[2], sampleDate));

        // 合并列配置
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 0, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 0, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 0, 4));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 1, 3));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, 3, 4));
        mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(4, 3, 4));
        dynamicTableDTO.setMergedRowList(mergedRowList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcReportConstant.DIOXIN_GAS_PARAMETER_TABLE_HEADER_TITLES, EmcReportConstant.DIOXIN_GAS_PARAMETER_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcReportConstant.DIOXIN_GAS_PARAMETER_TABLE_HEADER_TITLES, EmcReportConstant.DIOXIN_GAS_PARAMETER_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        IntStream.range(0, EmcReportConstant.DIOXIN_GAS_PARAMETER_RESULT_KEYWORDS.length).forEach(index -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(EmcReportConstant.DIOXIN_GAS_PARAMETER_RESULT_KEYWORDS[index]);

            String itemKeyword = EmcReportConstant.DIOXIN_GAS_PARAMETER_ITEM_KEYWORDS[index];
            List<String> unitNameList = new ArrayList<>();
            IntStream.range(1, 4).forEach(frequency -> {
                EmcReportResultBean reportResult = sampleReportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemKeyword) && r.getFrequency() == frequency).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(reportResult.getReportResult()));
                unitNameList.add(reportResult.getUnitName());
            });
            rowDataList.add(unitNameList.isEmpty() ? "-" : unitNameList.get(0));
            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }
}
