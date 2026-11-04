/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.TableTools;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandHVGroupTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordUtil2007;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;

public class LimsExpandHVGroupTableRenderPolicy
extends DynamicTableRenderPolicy {
    @Override
    public void render(XWPFTable xwpfTable, Object o) throws Exception {
    }

    @Override
    public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
        String[] mergeRowRoles;
        LimsExpandHVGroupTableRenderData renderData = (LimsExpandHVGroupTableRenderData)data;
        LimsTemplateDataList<LimsTemplateDataMap> dataList = renderData.getData();
        RunTemplate runTemplate = (RunTemplate)eleTemplate;
        XWPFRun run = runTemplate.getRun();
        if (!TableTools.isInsideTable(run)) {
            throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
        }
        run.setText("", 0);
        XWPFTable table = ((XWPFTableCell)((XWPFParagraph)run.getParent()).getBody()).getTableRow().getTable();
        List<XWPFTableCell> modalCellList = table.getRow(1).getTableCells();
        int modalCellSize = modalCellList.size();
        ArrayList<String> modalKeyList = new ArrayList<String>();
        for (XWPFTableCell cell : modalCellList) {
            modalKeyList.add(cell.getText());
        }
        int columnExpandNum = renderData.getColumnExpandNum();
        CTTbl ctTbl = table.getCTTbl();
        CTTblGrid tblGrid = ctTbl.getTblGrid();
        for (int r = 0; r < table.getNumberOfRows(); ++r) {
            XWPFTableRow row = table.getRow(r);
            List<XWPFTableCell> cells = row.getTableCells();
            XWPFTableCell modalCell = cells.get(cells.size() - 1);
            for (int n = modalCellSize; n < columnExpandNum + modalCellSize - 1; ++n) {
                XWPFTableCell newCell = row.addNewTableCell();
                WordExportUtil.copyTableCell(modalCell, newCell);
                tblGrid.addNewGridCol();
            }
        }
        List<CTTblGridCol> gridColList = tblGrid.getGridColList();
        XWPFTableCell modalCell = table.getRow(0).getCell(modalCellSize - 1);
        int modalCellWidth = modalCell.getWidth();
        for (int i = modalCellSize - 1; i < gridColList.size(); ++i) {
            gridColList.get(i).setW(BigInteger.valueOf(modalCellWidth / columnExpandNum));
        }
        WordUtil2007.mergeCellsHorizontal(table, 0, modalCellSize - 1, columnExpandNum + modalCellSize - 1);
        ArrayList<List<Object>> tableDataList = new ArrayList<List<Object>>();
        String groupField = renderData.getGroupField();
        String[] groupFields = StringUtils.split(groupField, "$");
        Map<String, List<LimsTemplateDataMap>> dataListGroupMap = dataList.stream().collect(Collectors.groupingBy(e -> Arrays.stream(groupFields).map(field -> e.get((String)field) + "").collect(Collectors.joining("$"))));
        for (String key : dataListGroupMap.keySet()) {
            List<LimsTemplateDataMap> theGroupDataList = dataListGroupMap.get(key);
            for (int i = 0; i < theGroupDataList.size(); i += columnExpandNum) {
                List<LimsTemplateDataMap> theDataList = theGroupDataList.subList(i, Math.min(i + columnExpandNum, theGroupDataList.size()));
                ArrayList<Object> rowDataList1 = new ArrayList<Object>();
                ArrayList<Object> rowDataList2 = new ArrayList<Object>();
                for (int j = 0; j < modalCellSize - 1; ++j) {
                    Object obj = theDataList.get(0).get((String)modalKeyList.get(j));
                    rowDataList1.add(obj);
                    rowDataList2.add(obj);
                }
                String modalKey = (String)modalKeyList.get(modalKeyList.size() - 1);
                String[] modalKeys = StringUtils.split(modalKey, "$");
                for (int j = 0; j < columnExpandNum; ++j) {
                    if (j < theDataList.size()) {
                        rowDataList1.add(theDataList.get(j).get(modalKeys[0]));
                        rowDataList2.add(theDataList.get(j).get(modalKeys[1]));
                        continue;
                    }
                    rowDataList1.add("-");
                    rowDataList2.add("-");
                }
                tableDataList.add(rowDataList1);
                tableDataList.add(rowDataList2);
            }
        }
        WordUtil2007.insertDataToTableRow(table, 1, tableDataList);
        String mergeRowRole = renderData.getMergeRowRole();
        mergeRowRole = StringUtils.removeStart(mergeRowRole, "(");
        mergeRowRole = StringUtils.removeEnd(mergeRowRole, ")");
        for (String mergeRole : mergeRowRoles = StringUtils.split(mergeRowRole, ")(")) {
            String[] roles = StringUtils.split(mergeRole, "$");
            int cellIndex = NumberUtils.parseInt(roles[0]);
            int compareIndex = NumberUtils.parseInt(roles[1]);
            int s = 1;
            for (int i = 2; i <= table.getNumberOfRows(); ++i) {
                if (i != table.getNumberOfRows() && ObjectUtils.equals(table.getRow(i).getCell(compareIndex).getText(), table.getRow(i - 1).getCell(compareIndex).getText())) continue;
                WordUtil2007.mergeCellsVertically(table, cellIndex, s, i - 1);
                s = i;
            }
        }
    }
}
