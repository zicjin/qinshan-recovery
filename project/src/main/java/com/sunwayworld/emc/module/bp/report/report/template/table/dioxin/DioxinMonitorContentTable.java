package com.sunwayworld.emc.module.bp.report.report.template.table.dioxin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.report.report.constant.EmcReportConstant;
import com.sunwayworld.emc.module.bp.report.report.template.DynamicTable;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author zhengxy@sunwayworld.com 2023年09月14日
 */
public class DioxinMonitorContentTable implements DynamicTable {

    private XWPFDocument document;
    private List<EmcFolderBean> folderList;
    private List<EmcOrderBean> orderList;
    private List<EmcOrderTaskBean> orderTaskList;
    private boolean sampling;

    public DioxinMonitorContentTable(XWPFDocument document, EmcProjectBean project, List<EmcFolderBean> folderList, List<EmcOrderBean> orderList, List<EmcOrderTaskBean> orderTaskList) {
        this.document = document;
        this.folderList = folderList;
        this.orderList = orderList;
        this.orderTaskList = orderTaskList;
        this.sampling = Constant.YES.equals(project.getSample());
    }

    @Override
    public XWPFDocument getDocument() {
        return document;
    }

    @Override
    public String getTableTitle() {
        return EmcReportConstant.MONITOR_CONTENT_TABLE_TITLE;
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
    public List<Integer> getColumnWidthList() {
        return Arrays.asList(1531, 0, 0, 0);
    }

    @Override
    public int getDynamicTableIndex() {
        return 3;
    }

    @Override
    public List<List<Object>> buildTableHeader(WordTemplateDynamicTableDTO dynamicTableDTO) {
        String[] headerTitles = sampling ? EmcReportConstant.MONITOR_CONTENT_TABLE_HEADER_TITLES : EmcReportConstant.MONITOR_CONTENT_TABLE_HEADER_TITLES_2;
        List<List<Object>> headerDataList = buildTableDynamicHeader(headerTitles, EmcReportConstant.MONITOR_CONTENT_TABLE_HEADER_COLUMN_QTY, null);
        setTableTitle(headerTitles, EmcReportConstant.MONITOR_CONTENT_TABLE_HEADER_COLUMN_QTY, headerDataList, dynamicTableDTO);

        return headerDataList;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = new ArrayList<>();

        folderList.forEach(folder -> {
            List<Object> rowDataList = new ArrayList<>();
            rowDataList.add(folder.getOrderCategory());
            EmcOrderBean curOrder = orderList.stream().filter(order -> ObjectUtils.equals(folder.getId(), order.getFolderId())).findFirst().orElseGet(EmcOrderBean::new);
            rowDataList.add(sampling ? folder.getMonitorPointName() : curOrder.getClientOrderName());

            EmcOrderTaskBean basicTask = orderTaskList.stream().filter(task -> ObjectUtils.equals(folder.getId(), task.getFolderId())).findFirst().orElseGet(EmcOrderTaskBean::new);
            rowDataList.add(sampling ? basicTask.getTestName() : curOrder.getOrderDesc());
            long orderCount = orderList.stream().filter(order -> ObjectUtils.equals(folder.getId(), order.getFolderId())).count();
            rowDataList.add(sampling ? orderCount : curOrder.getExt$Item("packing"));
            tableBodyDataList.add(rowDataList);
        });

        return tableBodyDataList;
    }
}
