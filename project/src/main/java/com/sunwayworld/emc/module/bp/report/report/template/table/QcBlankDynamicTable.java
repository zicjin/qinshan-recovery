package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.framework.word.WordTemplateUtils;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemGroupBean;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author zhengxy@sunwayworld.com 2021年08月06日
 * 实验室空白样质控结果
 */
public class QcBlankDynamicTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcOrderTaskBean> orderTaskList;
    private List<EmcOrderTaskResultBean> qcOrderTaskResultList;
    private List<EmcOrderTaskResultBean> orderTaskResultList;
    private List<EmcGroupItemBean> groupItemList;
    private List<EmcItemGroupBean> itemGroupList;
    private int tableIndex;
    private int dynamicTableIndex;
    private static final String WATER_KEYWORD = "水";


    public QcBlankDynamicTable(XWPFDocument document, List<EmcOrderTaskBean> orderTaskList, List<EmcOrderTaskResultBean> qcOrderTaskResultList, List<EmcOrderTaskResultBean> orderTaskResultList,List<EmcGroupItemBean> groupItemList,List<EmcItemGroupBean> itemGroupList, int tableIndex, int dynamicTableIndex) {
        this.document = document;
        this.orderTaskList = orderTaskList;
        this.qcOrderTaskResultList = qcOrderTaskResultList;
        this.orderTaskResultList = orderTaskResultList;
        this.groupItemList = groupItemList;
        this.itemGroupList = itemGroupList;

        this.tableIndex = tableIndex;
        this.dynamicTableIndex = dynamicTableIndex;
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return String.format(PfConstant.QC_BLANK_RESULT_TABLE_TITLE, tableIndex);
    }

    @Override
    public int getTableIndex() {
        return tableIndex;
    }

    @Override
    public boolean isLast() {
        return true;
    }

    @Override
    public int getDynamicTableIndex() {
        return dynamicTableIndex;
    }

    @Override
    public int getTableWidth() {
        return 10500;
    }

    @Override
    public String getReplaceKeyword() {
        return PfConstant.QC_RESULT_TABLE_REPLACE_KEYWORD;
    }

    @Override
    public ParagraphAlignment getTitleAlignment() {
        return ParagraphAlignment.CENTER;
    }

    @Override
    public List<Integer> getColumnWidthList() {
        return WordTemplateUtils.cm2Ints(Arrays.asList(3.7D, 0D, 0D, 2.5D, 2.5D, 2.5D));
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> headerDataList = buildTableDynamicHeader(PfConstant.QC_BLANK_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_BLANK_RESULT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(PfConstant.QC_BLANK_RESULT_TABLE_HEADER_TITLES, PfConstant.QC_BLANK_RESULT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        orderTaskList.sort(Comparator.comparing(EmcOrderTaskBean::getOrderId));
        qcOrderTaskResultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getId));
        Collections.reverse(qcOrderTaskResultList);
        List<EmcOrderTaskResultBean> qcOrderTaskResultNewList = new ArrayList<>(qcOrderTaskResultList);

        qcOrderTaskResultNewList.forEach(r -> {
        	List<EmcOrderTaskBean> orderTaskMatchList = orderTaskList.stream().filter(t ->ObjectUtils.equals(r.getOrderTaskId(), t.getId())).collect(Collectors.toList());
        	List<EmcGroupItemBean> groupItemNewList = groupItemList.stream().filter(g ->ObjectUtils.equals(r.getItemId(),g.getItemId())).collect(Collectors.toList());

        	if (!groupItemNewList.isEmpty()) {
                List<EmcItemGroupBean> itemGroupNewList = itemGroupList.stream().filter(ig ->ObjectUtils.equals(groupItemNewList.get(0).getItemGroupId(),ig.getId())).collect(Collectors.toList());
                r.setExt$Item("orderNo", orderTaskMatchList.get(0).getExt$Item("ORDERNO"));
                r.setExt$Item("itemGroup",itemGroupNewList.get(0).getItemGroup());

                r.setExt$Item("testname",orderTaskMatchList.get(0).getExt$Item("testname"));
                r.setExt$Item("orderCategory",orderTaskMatchList.get(0).getOrderCategory());
            }
        });

        qcOrderTaskResultNewList.stream() .filter(r -> StringUtils.contains(r.getExt$Item("ordercategory"), WATER_KEYWORD))
        .filter(r -> EmcConstant.ND.equals(r.getReportResult())).forEach(r -> r.setReportResult(r.getLowLimit() + "L"));

        Map<Long, List<EmcOrderTaskResultBean>> orderTaskResultMap = qcOrderTaskResultNewList.stream().collect(Collectors.groupingBy(EmcOrderTaskResultBean::getOrderId, LinkedHashMap::new, Collectors.toList()));

        List<String> itemGroupList = qcOrderTaskResultNewList.stream().map(task -> Optional.ofNullable(task.getExt$Item("itemGroup")).orElse(task.getExt$Item("testname"))).distinct().collect(Collectors.toList());

        itemGroupList.forEach(itemGroup -> {
        	orderTaskResultMap.forEach((orderId, curOrderTaskResultList) -> {
            	List<EmcOrderTaskResultBean> curItemGroupResultList = curOrderTaskResultList.stream().filter(res -> ObjectUtils.equals(itemGroup, Optional.ofNullable(res.getExt$Item("itemGroup")).orElse(res.getExt$Item("testname")))).collect(Collectors.toList());
                if (!curItemGroupResultList.isEmpty()) {
                    EmcOrderTaskResultBean finalResult = curItemGroupResultList.stream().filter(r -> Constant.YES.equals(r.getReportDisplay())).findFirst().orElse(null);
                    if (Objects.isNull(finalResult)) {
                        finalResult = curItemGroupResultList.stream().filter(r -> StringUtils.contains(r.getItemName(), EmcReportConstant.RESULT_ITEM_KEYWORD)).findFirst().orElse(null);
                        if (Objects.isNull(finalResult)) {
                            curItemGroupResultList.sort(Comparator.comparing(EmcOrderTaskResultBean::getItemOrderNo).reversed());
                            finalResult = curItemGroupResultList.get(0);
                        }
                    }

                    List<Object> rowDataList = new ArrayList<>();
                    rowDataList.add(finalResult.getExt$Item("ORDERNO"));
                    rowDataList.add(finalResult.getQcName());
                    rowDataList.add(StringUtils.replace(itemGroup, PfConstant.SPECIAL_TEST_NAME, PfConstant.SPECIAL_REPLACED_TEST_NAME));

                    rowDataList.add(finalResult.getUnitName());
                    rowDataList.add(WordTemplateUtils.convertScientificToWordFormat(finalResult.getReportResult()));

                    boolean oos = StringUtils.equals(finalResult.getResultStatus(), "OOS");
                    rowDataList.add(oos ? PfConstant.QC_RESULT_INEFFECTIVE : PfConstant.QC_RESULT_EFFECTIVE);

                    tableBodyDataList.add(rowDataList);
                }
            });
        });

        return tableBodyDataList;
    }
}
