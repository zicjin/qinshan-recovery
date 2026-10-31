package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sunwayworld.framework.utils.DateTimeUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 环境空气气象参数（二噁英）
 * @author zhengxy@sunwayworld.com 2021年07月08日
 */
public class DioxinEnvGasParameterDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcReportResultBean> reportResultList;
    private boolean last;

    public DioxinEnvGasParameterDynamicTable(XWPFDocument document, List<EmcReportResultBean> reportResultList, boolean last) {
        this.document = document;
        this.reportResultList = reportResultList;
        this.last = last;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return PfConstant.DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_TITLE;
    }

    @Override
    public int getTableIndex() {
        return 0;
    }

    @Override
    public boolean isLast() {
        return this.last;
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return Arrays.asList(0, 1400, 1400, 1247, 1247, 1134, 1134);
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES, PfConstant.DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_HEADER_TITLES, PfConstant.DIOXIN_ENV_GAS_PARAMETER_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        // 过滤出样品值结果
        List<EmcReportResultBean> sampleReportResultList = reportResultList.stream().filter(result -> StringUtils.equals(result.getDataType(), EmcConstant.PN_SAMPLE)).collect(Collectors.toList());

        Map<Long, List<EmcReportResultBean>> folderResultMap = sampleReportResultList.stream().sorted(Comparator.comparing(EmcReportResultBean::getFolderId))
                .collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
        folderResultMap.forEach((folderId, resultList) -> {
            List<Long> sampleIdList = resultList.stream().map(EmcReportResultBean::getSampleId).distinct().sorted(Comparator.comparing(Function.identity())).collect(Collectors.toList());
            IntStream.range(0, sampleIdList.size()).forEach(index -> {
                List<Object> rowDataList = new ArrayList<>();
                Long sampleId = sampleIdList.get(index);

                EmcReportResultBean result = resultList.stream().filter(r -> ObjectUtils.equals(r.getSampleId(), sampleId)).findFirst().orElseGet(EmcReportResultBean::new);
                rowDataList.add(result.getMonitorPointName());
                rowDataList.add(DateTimeUtils.formatLocalDate(result.getSampleBeginDate()));

                List<EmcReportResultBean> curResultList = resultList.stream().filter(r -> ObjectUtils.equals(r.getSampleId(), sampleId)).collect(Collectors.toList());
                // 风向、风速、气压、气温、湿度
                Arrays.stream(PfConstant.DIOXIN_ENV_GAS_PARAMETER_RESULT_KEYWORDS).forEach(keyword -> rowDataList.add(getSpecifiedItemReportResult(curResultList, keyword)));

                tableBodyDataList.add(rowDataList);
            });
        });

        return tableBodyDataList;
    }

    private String getSpecifiedItemReportResult(List<EmcReportResultBean> reportResultList, String itemName) {
        EmcReportResultBean reportResult = reportResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), itemName)).findFirst().orElseGet(EmcReportResultBean::new);

        return reportResult.getReportResult();
    }

}
