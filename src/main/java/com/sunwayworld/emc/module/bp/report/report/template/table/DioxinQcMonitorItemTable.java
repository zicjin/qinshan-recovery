package com.sunwayworld.emc.module.bp.report.report.template.table;

import java.util.List;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.sunwayworld.emc.framework.constant.PfConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportBean;
import com.sunwayworld.emc.module.bp.report.report.bean.EmcReportResultBean;
import com.sunwayworld.emc.module.bp.report.report.bean.WordTemplateDynamicTableDTO;
import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;

/**
 * @author zhengxy@sunwayworld.com 2022年02月25日
 */
public class DioxinQcMonitorItemTable extends MonitorItemDynamicTable {

    public DioxinQcMonitorItemTable(XWPFDocument document, EmcProjectBean project, EmcReportBean report, List<EmcOrderTaskBean> orderTaskList, List<EmcReportResultBean> reportResultList, List<EmcEquiptBean> equiptList, List<EmcRunBean> runList, List<CoreCodeBean> coreCodeList, int tableIndex) {
        super(document, project, report, orderTaskList, reportResultList, equiptList, runList, coreCodeList, tableIndex);
    }

    @Override
    public int getDynamicTableIndex() {
        return 2;
    }

    @Override
    public String getTableTitle() {
        return PfConstant.DIOXIN_QC_MONITOR_ITEM_TABLE_TITLE;
    }

    @Override
    public List<List<Object>> buildTableBody(WordTemplateDynamicTableDTO dynamicTableDTO) {
        List<List<Object>> tableBodyDataList = super.buildTableBody(dynamicTableDTO);
        dynamicTableDTO.setTableRemarkList(null);

        return tableBodyDataList;
    }
}
