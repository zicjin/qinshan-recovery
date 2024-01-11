package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * 加油站检测方法、依据及使用仪器表（油气回收报告）
 * @author zhengxy@sunwayworld.com 2020年09月13日
 */
public class GasStationMonitorItemDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcEquiptBean> equiptList;

    public GasStationMonitorItemDynamicTable(XWPFDocument document, List<EmcOrderTaskBean> orderTaskList, List<EmcEquiptBean> equiptList) {
        this.document = document;
        this.orderTaskList = orderTaskList;
        this.equiptList = equiptList;
    }

    @Override
    public XWPFDocument getDocument() {
        return this.document;
    }

    @Override
    public String getTableTitle() {
        return null;
    }

    @Override
    public int getTableIndex() {
        return 3;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return 3;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<String> dynamicTitleList = new ArrayList<>();

        // 检测项目
        StringBuilder monitorItemBuilder = new StringBuilder();
        List<EmcOrderTaskBean> validOrderTaskList = orderTaskList.stream().filter(r -> !StringUtils.contains(r.getTestName(), EmcConstant.PARAMETER_TEST_MATCH_KEYWORD)).collect(Collectors.toList());
        validOrderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestName))), ArrayList::new)).forEach(t -> {
            monitorItemBuilder.append(t.getTestName()).append(": ").append(t.getTestMethodBasis()).append("\n");
        });
        dynamicTitleList.add(monitorItemBuilder.toString());

        // 执行标准
        dynamicTitleList.add(validOrderTaskList.get(0).getTestMethodBasis());

        List<WordTemplateDynamicTableDTO.MergedColumn> mergedColumnList = Optional.ofNullable(dynamicTableDTO.getMergedColumnList()).orElseGet(ArrayList::new);
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(0, 1, 2));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(1, 1, 2));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(2, 1, 2));
        mergedColumnList.add(new WordTemplateDynamicTableDTO.MergedColumn(3, 0, 2));
        dynamicTableDTO.setMergedColumnList(mergedColumnList);

        List<List<Object>> headerDataList = buildTableDynamicHeader(EmcConstant.GAS_STATION_MONITOR_ITEM_TABLE_HEADER_TITLES, EmcConstant.GAS_STATION_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY, dynamicTitleList);
        setTableTitle(EmcConstant.GAS_STATION_MONITOR_ITEM_TABLE_HEADER_TITLES, EmcConstant.GAS_STATION_MONITOR_ITEM_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();
        List<Long> equipmentIdList = orderTaskList.stream().map(EmcOrderTaskBean::getEquiptId).distinct().collect(Collectors.toList());

        Map<Long, EmcEquiptBean> equipmentIdMap = equiptList.stream().collect(Collectors.toMap(EmcEquiptBean::getId, Function.identity()));

        equipmentIdList.forEach(equipmentId -> {
            EmcEquiptBean equipment = equipmentIdMap.get(equipmentId);
            if (equipment == null) {
                return;
            }

            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(equipment.getEquiptName());
            rowDataList.add(equipment.getSpecification());
            rowDataList.add(equipment.getEquiptId());
            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }

    @Override
    public WordTemplateDynamicTableDTO buildTable() {
        WordTemplateDynamicTableDTO dynamicTableDTO = new WordTemplateDynamicTableDTO();

        List<List<Object>> tableDataList = new ArrayList<>();
        tableDataList.addAll(buildTableHeader(dynamicTableDTO));
        tableDataList.addAll(buildTableBody(dynamicTableDTO));
        WordTemplateUtils.listListNullToString(tableDataList, EmcConstant.NULL_REPLACED_VALUE);

        dynamicTableDTO.setTableDataList(tableDataList);
        dynamicTableDTO.setReplaceKeyword(EmcConstant.MONITOR_TABLE_PLACEHOLDER);

        return dynamicTableDTO;
    }
}
