package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.TableTools;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandTableFirstTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl.Factory;

public class LimsExpandTableFirstRenderPolicy extends DynamicTableRenderPolicy {
   @Override
   public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      run.setText("", 0);

      try {
         if (!TableTools.isInsideTable(run)) {
            throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
         } else {
            XWPFTableCell cell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
            XWPFTable table = cell.getTableRow().getTable();
            LimsExpandTableFirstTemplateRenderData renderData = (LimsExpandTableFirstTemplateRenderData)data;
            LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> splitDataList = renderData.getSplitDataList();
            if (!splitDataList.isEmpty()) {
               XWPFDocument document = template.getXWPFDocument();
               int posOfModelTableInDoc = document.getPosOfTable(table);
               XWPFParagraph nextParagraph = (XWPFParagraph)document.getBodyElements().get(posOfModelTableInDoc + 1);
               List<XWPFTable> xwpfTableList = document.getTables();
               int posOfTable = xwpfTableList.indexOf(table);
               List<XWPFTable> newTableList = this.expandTable(table, splitDataList.size());
               if (!newTableList.isEmpty()) {
                  for (int dataIndex = 1; dataIndex < splitDataList.size(); dataIndex++) {
                     this.render(newTableList.get(dataIndex - 1), splitDataList.get(dataIndex), renderData);
                     XmlCursor xmlCursor = nextParagraph.getCTP().newCursor();
                     document.insertNewParagraph(xmlCursor);
                     document.insertNewTbl(nextParagraph.getCTP().newCursor());
                     document.setTable(posOfTable + dataIndex, newTableList.get(dataIndex - 1));
                  }
               }

               this.render(table, splitDataList.get(0), renderData);
            }
         }
      } catch (Exception var18) {
         throw new RenderException("Dynamic render table error:" + var18.getMessage(), var18);
      }
   }

   @Override
   public void render(XWPFTable table, Object data) throws Exception {
   }

   private void render(XWPFTable table, LimsTemplateDataList<LimsTemplateDataMap> dataList, LimsExpandTableFirstTemplateRenderData renderData) {
      if (!Objects.isNull(dataList) && !dataList.isEmpty()) {
         String columnGroupField = renderData.getColumnGroupField();
         LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> columnGroupDataMap = dataList.stream()
            .collect(
               Collectors.groupingBy(
                  map -> ObjectUtils.toString(map.get(columnGroupField)), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)
               )
            );
         List<LimsTemplateDataList<LimsTemplateDataMap>> columnGroupDataList = new ArrayList<>(columnGroupDataMap.values());
         int columnMaxExpandNum = columnGroupDataList.size();
         int dataStartColumnIndex = renderData.getColumnStartExpandIndex();

         for (int r = 0; r < table.getNumberOfRows(); r++) {
            XWPFTableRow row = table.getRow(r);
            XWPFTableCell modelCell = row.getCell(dataStartColumnIndex);

            for (int c = dataStartColumnIndex + 1; c < columnMaxExpandNum + dataStartColumnIndex; c++) {
               XWPFTableCell newCell = row.addNewTableCell();
               WordExportUtil.copyTableCell(modelCell, newCell);
            }
         }

         CTTbl ctTbl = table.getCTTbl();
         CTTblGrid tblGrid = ctTbl.getTblGrid();

         for (int i = 0; i < columnMaxExpandNum - 1; i++) {
            tblGrid.addNewGridCol();
         }

         List<CTTblGridCol> gridColList = tblGrid.getGridColList();
         XWPFTableCell modelCell = table.getRow(0).getCell(dataStartColumnIndex);
         int modelCellWidth = modelCell.getWidth();

         for (int i = dataStartColumnIndex; i < gridColList.size(); i++) {
            gridColList.get(i).setW(BigInteger.valueOf(modelCellWidth / columnMaxExpandNum));
         }

         for (XWPFTableRow row : table.getRows()) {
            for (int c = dataStartColumnIndex; c < columnMaxExpandNum + dataStartColumnIndex; c++) {
               XWPFTableCell cell = row.getCell(c);
               cell.setWidth(modelCellWidth / columnMaxExpandNum + "");
            }
         }

         int dataStartRowIndex = renderData.getRowStartExpandIndex();

         for (int i = 0; i < dataStartRowIndex; i++) {
            XWPFTableRow row = table.getRow(i);

            for (int c = dataStartColumnIndex; c < columnMaxExpandNum + dataStartColumnIndex; c++) {
               XWPFTableCell cell = row.getCell(c);
               List<LimsTemplateDataMap> curDataMapList = columnGroupDataList.get(c - dataStartColumnIndex);
               setCellValue(cell, getCellValue(curDataMapList, cell.getText()));
            }
         }

         String rowGroupField = renderData.getRowGroupField();
         LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> rowGroupDataMap = dataList.stream()
            .collect(
               Collectors.groupingBy(
                  map -> ObjectUtils.toString(map.get(rowGroupField)), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)
               )
            );
         List<LimsTemplateDataList<LimsTemplateDataMap>> rowGroupDataList = new ArrayList<>(rowGroupDataMap.values());
         int rowExpandNum = rowGroupDataList.size();
         XWPFTableRow dataTemplateRow = table.getRow(dataStartRowIndex);

         for (int r = dataStartRowIndex + 1; r < rowExpandNum + dataStartRowIndex; r++) {
            WordExportUtil.copyTableRowFromTemplateRow(table, dataTemplateRow, r);
         }

         for (int r = dataStartRowIndex; r < rowExpandNum + dataStartRowIndex; r++) {
            XWPFTableRow dataRow = table.getRow(r);
            List<LimsTemplateDataMap> curRowDataMapList = rowGroupDataList.get(r - dataStartRowIndex);

            for (int c = 0; c < dataStartColumnIndex; c++) {
               XWPFTableCell cell = dataRow.getCell(c);
               setCellValue(cell, getCellValue(curRowDataMapList, cell.getText()));
            }

            LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> curRowColumnGroupDataMap = curRowDataMapList.stream()
               .collect(
                  Collectors.groupingBy(
                     map -> ObjectUtils.toString(map.get(columnGroupField)), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)
                  )
               );
            List<LimsTemplateDataList<LimsTemplateDataMap>> curRowColumnGroupDataList = new ArrayList<>(curRowColumnGroupDataMap.values());

            for (int c = dataStartColumnIndex; c < dataStartColumnIndex + columnMaxExpandNum; c++) {
               List<LimsTemplateDataMap> curDataMapList = curRowColumnGroupDataList.get(c - dataStartColumnIndex);
               XWPFTableCell cell = dataRow.getCell(c);
               setCellValue(cell, getCellValue(curDataMapList, cell.getText()));
            }
         }
      }
   }

   private static void setCellValue(XWPFTableCell cell, Object cellValue) {
      try {
         cell.getParagraphs().get(0).getRuns().get(0).setText(ObjectUtils.toString(cellValue), 0);
      } catch (Exception var3) {
         var3.printStackTrace();
      }
   }

   private static Object getCellValue(List<LimsTemplateDataMap> dataMapList, Object text) {
      return dataMapList.isEmpty() ? "/" : Optional.ofNullable(dataMapList.get(0).get(text)).orElse("/");
   }

   private List<XWPFTable> expandTable(XWPFTable table, int partitionSize) {
      CTTbl copiedCtTbl = table.getCTTbl();
      IBody copiedBody = table.getBody();
      List<XWPFTable> newTableList = new ArrayList<>();
      IntStream.range(0, partitionSize - 1).forEach(index -> {
         CTTbl newCtTbl = Factory.newInstance();
         newCtTbl.set(copiedCtTbl);
         newTableList.add(new XWPFTable(newCtTbl, copiedBody));
      });
      return newTableList;
   }
}
