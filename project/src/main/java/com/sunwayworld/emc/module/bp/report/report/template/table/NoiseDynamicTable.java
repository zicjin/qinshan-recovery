package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年08月30日
 */
public class NoiseDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private EmcReportBean report;
    private List<EmcFolderBean> folderList;
    private List<EmcReportResultBean> reportResultList;
    private int tableIndex;
    private boolean isFirst;
    private boolean isLast;

    public NoiseDynamicTable(XWPFDocument document, EmcReportBean report, List<EmcFolderBean> folderList, List<EmcReportResultBean> reportResultList, int tableIndex, boolean isFirst, boolean isLast) {
        this.document = document;
        this.report = report;
        this.folderList = folderList;
        this.reportResultList = reportResultList;
        this.tableIndex = tableIndex;
        this.isFirst = isFirst;
        this.isLast = isLast;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(isFirst ? PfConstant.WATER_RESULT_TABLE_TITLE : "续" + PfConstant.WATER_RESULT_TABLE_TITLE, tableIndex, folderList.get(0).getOrderCategory());
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
        return 9598;
    }

    @Override
    public WordTemplateDynamicTableDTO getDynamicTableDto() {
        // 设置表格字体为 Times New Roman，字号为 10
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();
        dynamicTableDTO.setFontStyle("Times New Roman");
        dynamicTableDTO.setFontSize(10);

        return dynamicTableDTO;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        // 是否显示执行标准
        boolean displayStd = StringUtils.equals(Constant.YES, report.getExt$Item("displayStd"));
        String[] tableTitles = displayStd ? PfConstant.NOISE_RESULT_TABLE_HEADER_TITLES : PfConstant.NOISE_RESULT_TABLE_HEADER_TITLES_2;
        int tableColumnQty = displayStd ? PfConstant.NOISE_RESULT_TABLE_HEADER_COLUMN_QTY : PfConstant.NOISE_RESULT_TABLE_HEADER_COLUMN_QTY_2;

        List<List<Object>> headerDataList = buildTableDynamicHeader(tableTitles, tableColumnQty, null);
        setTableTitle(tableTitles, tableColumnQty, headerDataList, dynamicTableDTO);

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

        Map<Long, EmcFolderBean> folderMap = folderList.stream().collect(Collectors.toMap(EmcFolderBean::getId, Function.identity()));

        // 是否显示执行标准
        boolean displayStd = StringUtils.equals(Constant.YES, report.getExt$Item("displayStd"));
        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);

        sampleReportResultList.sort(Comparator.comparing(EmcReportResultBean::getFolderId));
        Map<LocalDate, List<EmcReportResultBean>> dateResultMap = sampleReportResultList.stream().filter(r -> !ObjectUtils.isEmpty(r.getSampleBeginDate())).collect(Collectors.groupingBy(EmcReportResultBean::getSampleBeginDate, LinkedHashMap::new, Collectors.toList()));
        //合并行配置
        List<WordTemplateDynamicTableDTO.MergedRow> mergedRowList = Optional.ofNullable(dynamicTableDTO.getMergedRowList()).orElseGet(ArrayList::new);
        List<Integer> cycle =new ArrayList<>();
        List<Integer> cycleTwo =new ArrayList<>();
        dateResultMap.forEach((date, curResultList) -> {
            Map<Long, List<EmcReportResultBean>> folderResultMap = curResultList.stream().collect(Collectors.groupingBy(EmcReportResultBean::getFolderId, LinkedHashMap::new, Collectors.toList()));
            //每个日期下几个点位/
            //MergedRow（哪一列，开始行，结束行）
            if(cycle.isEmpty()){
                cycle.add(1);
            }
            //开始行
            int startOne = cycle.get(cycle.size()-1);

            List<Integer> foldercycle =new ArrayList<>();
            folderResultMap.forEach((folderId, resultList) -> {
                // 获取昼间和夜间的开始时间
                List<EmcReportResultBean> samplingTimeResultList = resultList.stream().filter(r -> Arrays.asList(PfConstant.NOISE_TIME_ITEMS).contains(r.getItemAliasName())).collect(Collectors.toList());
                List<Long> samplingTimeResultIdList = samplingTimeResultList.stream().map(EmcReportResultBean::getId).collect(Collectors.toList());

                List<EmcReportResultBean> validResultList = resultList.stream().filter(r -> !samplingTimeResultIdList.contains(r.getId())).collect(Collectors.toList());
                Arrays.stream(PfConstant.NOISE_RESULT_KEYWORDS).forEach(keyword -> {
                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(DateTimeUtils.formatLocalDate(date));
                    Optional.ofNullable(folderMap.get(folderId)).ifPresent(folder -> {
                        rowDataList.add("▲"+Optional.ofNullable(folder.getMonitorPointNo()).orElse(""));
                        rowDataList.add(folder.getMonitorPointName());
                    });
                    EmcReportResultBean curTimeResult = samplingTimeResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), keyword)).findFirst().orElseGet(EmcReportResultBean::new);
                    rowDataList.add(String.join("", keyword, "(", Optional.ofNullable(curTimeResult.getReportResult()).orElse(""), ")"));
                    EmcReportResultBean curResult = validResultList.stream().filter(r -> StringUtils.contains(r.getItemAliasName(), keyword)).findFirst().orElseGet(EmcReportResultBean::new);
                    rowDataList.add(curResult.getReportResult());
                    // 需要显示执行标准
                    if (displayStd) {
                        rowDataList.add(null);
                    }

                    tableBodyDataList.add(rowDataList);
                });
                if(cycleTwo.isEmpty()){
                	cycleTwo.add(1);
                }
                //开始行
                int startTwo = cycleTwo.get(cycleTwo.size()-1);//
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(1, startTwo, startTwo +validResultList.size()-1));
                mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(2, startTwo, startTwo +validResultList.size()-1));
                cycleTwo.add(validResultList.size()+startTwo);
                foldercycle.add(validResultList.size());
            });
            //每个点位下所有
            int[] arrays = foldercycle.stream().filter(integer -> integer!=null).mapToInt(i->i).toArray();
            int sum = Arrays.stream(arrays).sum();
            mergedRowList.add(new WordTemplateDynamicTableDTO.MergedRow(0, startOne, startOne +sum-1));//1,7,13
            cycle.add(startOne+sum);
        });

        // 合并行配置        //哪一列从第几行到第几行
        dynamicTableDTO.setMergedRowList(mergedRowList);

        if (displayStd) {
            // 添加备注行
            tableBodyDataList.add(buildDynamicRowDataList(PfConstant.EXECUTIVE_STANDARD, report.getRemark(), PfConstant.NOISE_RESULT_TABLE_HEADER_COLUMN_QTY));
            // 合并备注列
            mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(tableBodyDataList.size(), 1, PfConstant.NOISE_RESULT_TABLE_HEADER_COLUMN_QTY - 1));
            dynamicTableDTO.setMergedColumnList(mergedColumnList);
        }

        return tableBodyDataList;
    }
}
