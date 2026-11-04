package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.CellRenderData;
import com.deepoove.poi.data.Cells;
import com.deepoove.poi.data.Paragraphs;
import com.deepoove.poi.data.style.CellStyle;
import com.deepoove.poi.data.style.ParagraphStyle;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.policy.DynamicTableRenderPolicy;
import com.deepoove.poi.policy.TableRenderPolicy.Helper;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.StyleUtils;
import com.deepoove.poi.util.TableTools;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsFixedLoopMultiRowTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.utils.WordUtil;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl.Factory;

public class LimsFixedLoopMultiRowTableRenderPolicy extends DynamicTableRenderPolicy {
   private static final Pattern FIELD_PATTERN = Pattern.compile("\\[\\S+\\]");

   @Override
   public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      run.setText("", 0);

      try {
         if (!TableTools.isInsideTable(run)) {
            throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
         } else {
            NiceXWPFDocument document = template.getXWPFDocument();
            XWPFTableCell cell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
            XWPFTableRow templateRow = cell.getTableRow();
            XWPFTable table = templateRow.getTable();
            int templateStartRowIndex = table.getRows().indexOf(templateRow);
            int tablePosOfDocument = document.getPosOfTable(table);
            XWPFParagraph nextParagraph = (XWPFParagraph)document.getBodyElements().get(tablePosOfDocument + 1);
            LimsFixedLoopMultiRowTemplateRenderData renderData = (LimsFixedLoopMultiRowTemplateRenderData)data;
            LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> groupDataList = renderData.getGroupDataList();
            List<XWPFTable> newTableList = this.splitTable(table, groupDataList.size());
            List<XWPFTable> xwpfTableList = document.getTables();
            int templateTableIndex = xwpfTableList.indexOf(table);
            if (!newTableList.isEmpty()) {
               for (int dataIndex = 1; dataIndex < groupDataList.size(); dataIndex++) {
                  this.render(newTableList.get(dataIndex - 1), groupDataList.get(dataIndex), renderData, templateStartRowIndex);
                  XmlCursor xmlCursor = nextParagraph.getCTP().newCursor();
                  document.insertNewParagraph(xmlCursor);
                  document.insertNewTbl(nextParagraph.getCTP().newCursor());
                  document.setTable(templateTableIndex + dataIndex, newTableList.get(dataIndex - 1));
               }
            }

            if (!groupDataList.isEmpty()) {
               this.render(table, groupDataList.get(0), renderData, templateStartRowIndex);
            }
         }
      } catch (Exception var20) {
         throw new RenderException("Dynamic render table error:" + var20.getMessage(), var20);
      }
   }

   @Override
   public void render(XWPFTable xwpfTable, Object o) throws Exception {
   }

   private void render(
      XWPFTable table, LimsTemplateDataList<LimsTemplateDataMap> dataList, LimsFixedLoopMultiRowTemplateRenderData renderData, int templateStartRowIndex
   ) {
      if (!Objects.isNull(dataList) && !dataList.isEmpty()) {
         int loopRowCount = renderData.getLoopRowCount();
         int startDataColumnIndex = renderData.getDataStartColumnIndex();
         int fixedColumnCount = renderData.getFixedColumnCount();
         String rowGroupField = renderData.getRowGroupField();
         String[] rowGroupSplits = rowGroupField.split("-");
         Map<Object, List<LimsTemplateDataMap>> pivotDataMap = dataList.stream().collect(Collectors.groupingBy(map -> {
            StringBuilder sb = new StringBuilder();

            for (String rowGroupSplit : rowGroupSplits) {
               sb.append(Optional.ofNullable(map.get(rowGroupSplit)).orElse(""));
            }

            return sb.toString();
         }, LinkedHashMap::new, Collectors.toList()));
         AtomicInteger pivotIndex = new AtomicInteger();
         List<String> columnFieldNameList = new ArrayList<>(loopRowCount);
         IntStream.range(templateStartRowIndex, loopRowCount + templateStartRowIndex).forEach(rowIndex -> {
            XWPFTableRow templateRow = table.getRow(rowIndex);
            XWPFTableCell cell = Objects.isNull(templateRow) ? null : this.getTrueCell(templateRow, startDataColumnIndex);
            columnFieldNameList.add(Objects.isNull(cell) ? "" : cell.getText());
         });
         IntStream.range(0, pivotDataMap.size())
            .forEach(
               curIndex -> IntStream.range(0, loopRowCount)
                  .forEach(
                     count -> {
                        int rowIndex = curIndex * loopRowCount + count + templateStartRowIndex;
                        Optional.ofNullable(table.getRow(rowIndex))
                           .orElseGet(() -> WordExportUtil.copyTableRowFromTemplateRow(table, table.getRow(count + templateStartRowIndex), rowIndex));
                     }
                  )
            );
         Map<Integer, Integer> mergeRowMap = new HashMap<>();
         pivotDataMap.forEach(
            (pivotValue, pivotDataList) -> {
               Map<Object, List<LimsTemplateDataMap>> subPivotDataMap = pivotDataList.stream()
                  .collect(
                     Collectors.groupingBy(
                        map -> Optional.ofNullable(map.get(renderData.getColumnGroupField())).orElse(""), LinkedHashMap::new, Collectors.toList()
                     )
                  );
               List<Object> subPivotKeyList = new ArrayList<>(subPivotDataMap.keySet());
               IntStream.range(0, loopRowCount)
                  .forEach(
                     count -> {
                        int rowIndex = pivotIndex.get() * loopRowCount + count + templateStartRowIndex;
                        XWPFTableRow dataRow = Optional.ofNullable(table.getRow(rowIndex))
                           .orElseGet(() -> WordExportUtil.copyTableRowFromTemplateRow(table, table.getRow(count + templateStartRowIndex), rowIndex));
                        String fieldName = StringUtils.replace(columnFieldNameList.get(count), "[", "").replace("]", "");
                        boolean mergeColumnFlag = fieldName.length() > 0 && fieldName.charAt(0) == '!';
                        fieldName = mergeColumnFlag ? fieldName.substring(1, fieldName.length()) : fieldName;
                        final String valueFieldName = fieldName;
                        IntStream.range(0, startDataColumnIndex).forEach(columnIndex -> {
                           XWPFTableCell dataCell = this.getTrueCell(dataRow, columnIndex);
                           String cellText = dataCell.getText();
                           if (FIELD_PATTERN.matcher(cellText).matches()) {
                              String cellFieldName = StringUtils.replace(cellText, "[", "").replace("]", "");
                              boolean mergeRowFlag = cellFieldName.length() > 0 && cellFieldName.charAt(0) == '*';
                              cellFieldName = mergeRowFlag ? cellFieldName.substring(1, cellFieldName.length()) : cellFieldName;
                              Object cellValue = ((LimsTemplateDataMap)pivotDataList.get(0)).get(cellFieldName);
                              this.renderCell(dataCell, cellValue);
                              if (mergeRowFlag) {
                                 mergeRowMap.computeIfAbsent(columnIndex, k -> rowIndex);
                              }
                           }
                        });
                        IntStream.range(0, fixedColumnCount).forEach(columnIndex -> {
                           XWPFTableCell dataCell = this.getTrueCell(dataRow, startDataColumnIndex + columnIndex);
                           Object subPivotKey = subPivotKeyList.size() <= columnIndex ? "/" : subPivotKeyList.get(columnIndex);
                           List<LimsTemplateDataMap> curDataMapList = Optional.ofNullable(subPivotDataMap.get(subPivotKey)).orElseGet(ArrayList::new);
                           Object cellValue = "/";
                           if (!curDataMapList.isEmpty()) {
                              if (FIELD_PATTERN.matcher(columnFieldNameList.get(count)).matches()) {
                                 cellValue = Optional.ofNullable(curDataMapList.get(0).get(valueFieldName)).orElse("/");
                              } else {
                                 cellValue = columnFieldNameList.get(count);
                              }
                           }

                           this.renderCell(dataCell, cellValue);
                        });
                        if (mergeColumnFlag && subPivotKeyList.size() > 1) {
                           int mergeStartCellIndex = this.getTrueCellIndex(dataRow, startDataColumnIndex);
                           int mergeEndCellIndex = mergeStartCellIndex + Math.min(fixedColumnCount, subPivotKeyList.size()) - 1;
                           WordUtil.mergeCellsHorizontal(table, rowIndex, mergeStartCellIndex, mergeEndCellIndex);
                        }
                     }
                  );
               pivotIndex.incrementAndGet();
            }
         );
         mergeRowMap.forEach((c, r) -> {
            Deque<int[]> mergeQueue = new ArrayDeque<>();
            mergeQueue.add(new int[]{r, r});
            String preText = this.getTrueCell(table.getRow(r), c).getText();

            for (int rStart = r + 1; rStart < table.getRows().size(); rStart++) {
               XWPFTableCell curCell = this.getTrueCell(table.getRow(rStart), c);
               String curText = curCell.getText();
               if (preText.equals(curText)) {
                  int[] poll = mergeQueue.pollLast();
                  mergeQueue.addLast(new int[]{poll[0], rStart});
               } else {
                  mergeQueue.addLast(new int[]{rStart, rStart});
               }

               preText = curText;
            }

            while (!mergeQueue.isEmpty()) {
               int[] poll = mergeQueue.pollFirst();
               if (poll[0] != poll[1]) {
                  WordUtil.mergeCellsVertically(table, c, poll[0], poll[1]);
               }
            }
         });
      }
   }

   private XWPFTableCell getTrueCell(XWPFTableRow row, int cellIndex) {
      int index = -1;

      for (int i = 0; i < row.getTableCells().size(); i++) {
         XWPFTableCell cell = row.getTableCells().get(i);
         index += WordExportUtil.getTcPr(cell).getGridSpan() == null ? 1 : WordExportUtil.getTcPr(cell).getGridSpan().getVal().intValue();
         if (cellIndex <= index) {
            return cell;
         }
      }

      return null;
   }

   private int getTrueCellIndex(XWPFTableRow row, int cellIndex) {
      int index = -1;

      for (int i = 0; i < row.getTableCells().size(); i++) {
         XWPFTableCell cell = row.getTableCells().get(i);
         index += WordExportUtil.getTcPr(cell).getGridSpan() == null ? 1 : WordExportUtil.getTcPr(cell).getGridSpan().getVal().intValue();
         if (cellIndex <= index) {
            return i;
         }
      }

      return -1;
   }

   private List<XWPFTable> splitTable(XWPFTable table, int tableSize) {
      CTTbl copiedCtTbl = table.getCTTbl();
      IBody copiedBody = table.getBody();
      List<XWPFTable> newTableList = new ArrayList<>();
      IntStream.range(0, tableSize - 1).forEach(index -> {
         CTTbl newCtTbl = Factory.newInstance();
         newCtTbl.set(copiedCtTbl);
         newTableList.add(new XWPFTable(newCtTbl, copiedBody));
      });
      return newTableList;
   }

   private void renderCell(XWPFTableCell cell, Object value) {
      CellStyle cellStyle = this.buildCellStyle(cell);
      ParagraphStyle paragraphStyle = this.buildParagraphStyle(cell);
      CellRenderData cellRenderData = Cells.of().addParagraph(Paragraphs.of(Objects.toString(value)).paraStyle(paragraphStyle).create()).create();

      try {
         Helper.renderCell(cell, cellRenderData, cellStyle);
      } catch (Exception var7) {
         var7.printStackTrace();
      }
   }

   private CellStyle buildCellStyle(XWPFTableCell cell) {
      if (Objects.isNull(cell)) {
         return null;
      } else {
         CellStyle cellStyle = new CellStyle();
         cellStyle.setVertAlign(cell.getVerticalAlignment());
         CTTc ctTc = cell.getCTTc();
         CTTcPr pr = ctTc.isSetTcPr() ? ctTc.getTcPr() : ctTc.addNewTcPr();
         CTShd shd = pr.isSetShd() ? pr.getShd() : pr.addNewShd();
         if (shd.isSetFill()) {
            cellStyle.setBackgroundColor(shd.getFill().toString());
         }

         return cellStyle;
      }
   }

   private ParagraphStyle buildParagraphStyle(XWPFTableCell cell) {
      if (Objects.isNull(cell)) {
         return null;
      } else {
         List<XWPFParagraph> paragraphs = cell.getParagraphs();
         return Objects.nonNull(paragraphs) && !paragraphs.isEmpty() ? StyleUtils.retriveParagraphStyle(paragraphs.get(0)) : null;
      }
   }
}
