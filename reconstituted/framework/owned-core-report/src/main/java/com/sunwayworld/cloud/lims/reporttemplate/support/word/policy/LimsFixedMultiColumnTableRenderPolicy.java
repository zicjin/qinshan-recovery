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
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsFixedMultiColumnTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.utils.ScienNumberChangeUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.utils.WordUtil;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
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

public class LimsFixedMultiColumnTableRenderPolicy extends DynamicTableRenderPolicy {
   @Override
   public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
      NiceXWPFDocument document = template.getXWPFDocument();
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      run.setText("", 0);

      try {
         if (!TableTools.isInsideTable(run)) {
            throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
         } else {
            XWPFTableCell cell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
            XWPFTableRow row = cell.getTableRow();
            XWPFTable table = row.getTable();
            int posOfTable = document.getTables().indexOf(table);
            int dataStartRowIndex = WordExportUtil.getTagRowIndex(table, row, WordExportUtil.getCellIndex(row, cell)) + 1;
            int tablePosOfDocument = document.getPosOfTable(table);
            XWPFParagraph nextParagraph = (XWPFParagraph)document.getBodyElements().get(tablePosOfDocument + 1);
            LimsFixedMultiColumnTemplateRenderData renderData = (LimsFixedMultiColumnTemplateRenderData)data;
            LimsTemplateDataList<LimsTemplateDataList<LimsTemplateDataMap>> partitionList = renderData.getGroupPartitionList();
            List<XWPFTable> newTableList = this.splitTable(table, partitionList.size());
            if (!newTableList.isEmpty()) {
               for (int dataIndex = 1; dataIndex < partitionList.size(); dataIndex++) {
                  this.render(newTableList.get(dataIndex - 1), dataStartRowIndex, partitionList.get(dataIndex), renderData);
                  XmlCursor xmlCursor = nextParagraph.getCTP().newCursor();
                  document.insertNewParagraph(xmlCursor);
                  document.insertNewTbl(nextParagraph.getCTP().newCursor());
                  template.getXWPFDocument().setTable(posOfTable + dataIndex, newTableList.get(dataIndex - 1));
               }
            }

            if (!partitionList.isEmpty()) {
               this.render(table, dataStartRowIndex, partitionList.get(0), renderData);
            }
         }
      } catch (Exception var19) {
         throw new RenderException("Dynamic render table error:" + var19.getMessage(), var19);
      }
   }

   @Override
   public void render(XWPFTable table, Object data) throws Exception {
   }

   private void render(
      XWPFTable table, int dataStartRowIndex, LimsTemplateDataList<LimsTemplateDataMap> dataList, LimsFixedMultiColumnTemplateRenderData renderData
   ) {
      if (!Objects.isNull(dataList) && !dataList.isEmpty()) {
         ScienNumberChangeUtil scienNumberChangeUtil = ApplicationContextHelper.getBean(ScienNumberChangeUtil.class);
         XWPFTableRow titleRow = table.getRow(dataStartRowIndex - 1);
         XWPFTableRow dataModelRow = table.getRow(dataStartRowIndex);
         int rowCellSize = dataModelRow.getTableCells().size();
         int cellStartIndex = -1;
         String findKey = String.format("[%s]", renderData.getGroupField());

         for (int i = 0; i < rowCellSize; i++) {
            XWPFTableCell cell = titleRow.getCell(i);
            String cellText = cell.getText();
            if (findKey.equals(cellText) && cellStartIndex == -1) {
               cellStartIndex = i;
               break;
            }
         }

         String groupField = renderData.getGroupField();
         Map<Object, List<LimsTemplateDataMap>> groupDataMap = dataList.stream()
            .filter(map -> !ObjectUtils.isEmpty(map.get(groupField)))
            .collect(Collectors.groupingBy(map -> map.get(groupField), LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)));
         List<Object> groupKeyList = new ArrayList<>(groupDataMap.keySet());
         int finalCellStartIndex = cellStartIndex;
         IntStream.range(0, renderData.getPartitionSize()).forEach(groupColumnIndex -> {
            String title = groupKeyList.size() > groupColumnIndex ? String.valueOf(groupKeyList.get(groupColumnIndex)) : "/";
            XWPFTableCell titleCell = titleRow.getCell(finalCellStartIndex + groupColumnIndex * 2);
            List<XWPFParagraph> paragraphs = titleCell.getParagraphs();
            if (!paragraphs.isEmpty()) {
               paragraphs.forEach(p -> p.getRuns().forEach(r -> r.setText("", 0)));
            }

            titleCell.setText(title);
         });
         String uniqueField = renderData.getUniqueField();
         String subUniqueField = renderData.getSubUniqueField();
         String valueField = renderData.getValueField();
         List<String> rowKeyList = dataList.stream()
            .filter(map -> !ObjectUtils.isEmpty(map.get(uniqueField)))
            .map(map -> map.get(uniqueField).toString())
            .distinct()
            .collect(Collectors.toList());
         String modelPattern = "\\[(\\w*)\\]";
         AtomicInteger dataIndexCount = new AtomicInteger(dataStartRowIndex + 1);
         IntStream.range(0, rowKeyList.size())
            .forEach(
               rowIndex -> {
                  int startRowIndex = dataIndexCount.get();
                  String rowKey = rowKeyList.get(rowIndex);
                  Map<Object, List<String>> groupSubKeyMap = groupDataMap.entrySet()
                     .stream()
                     .collect(
                        Collectors.toMap(
                           Entry::getKey,
                           entry -> entry.getValue()
                              .stream()
                              .filter(
                                 map -> StringUtils.equals(ObjectUtils.toString(map.get(uniqueField)), rowKey) && !ObjectUtils.isEmpty(map.get(subUniqueField))
                              )
                              .map(map -> ObjectUtils.toString(map.get(subUniqueField)))
                              .distinct()
                              .collect(Collectors.toList())
                        )
                     );
                  Integer maxRowKeyCount = groupSubKeyMap.values().stream().max(Comparator.comparing(List::size)).map(List::size).orElse(0);
                  if (maxRowKeyCount != 0) {
                     Map<String, List<String>> allCellValueListMap = new HashMap<>();
                     AtomicBoolean existYqMethod = new AtomicBoolean(false);
                     IntStream.range(0, maxRowKeyCount)
                        .forEach(
                           countIndex -> {
                              XWPFTableRow insertNewTableRow = table.insertNewTableRow(dataIndexCount.getAndIncrement());

                              for (int ixx = 0; ixx < rowCellSize; ixx++) {
                                 WordExportUtil.copyTableCell(dataModelRow.getCell(ixx), insertNewTableRow.createCell());
                              }

                              for (int ix = 0; ix < renderData.getPartitionSize(); ix++) {
                                 int cellIndex = finalCellStartIndex + ix * 2;
                                 String groupKey = groupKeyList.size() > ix ? String.valueOf(groupKeyList.get(ix)) : null;
                                 String subCellValue = "/";
                                 String cellValue = "/";
                                 if (Objects.nonNull(groupKey)) {
                                    List<LimsTemplateDataMap> groupRowList = groupDataMap.get(groupKey);
                                    List<String> subKeyList = groupSubKeyMap.get(groupKey);
                                    String subKey = subKeyList.size() > countIndex ? subKeyList.get(countIndex) : "/";
                                    LimsTemplateDataMap curRowMap = groupRowList.stream()
                                       .filter(
                                          groupRow -> StringUtils.equals(rowKey, String.valueOf(groupRow.get(uniqueField)))
                                             && StringUtils.equals(groupKey, String.valueOf(groupRow.get(groupField)))
                                             && StringUtils.equals(subKey, ObjectUtils.toString(groupRow.get(subUniqueField)))
                                       )
                                       .findFirst()
                                       .orElse(null);
                                    if (Objects.nonNull(curRowMap)) {
                                       subCellValue = subKey;
                                       cellValue = String.valueOf(Optional.ofNullable(curRowMap.get(valueField)).orElse("/"));
                                       existYqMethod.set(String.valueOf(curRowMap.getOrDefault("methodname", "")).contains("园区"));
                                       allCellValueListMap.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(cellValue);

                                       for (int j = 0; j < finalCellStartIndex - 1; j++) {
                                          XWPFTableCell preCell = insertNewTableRow.getTableCells().get(j);
                                          String preCellText = preCell.getText();
                                          if (!StringUtils.isEmpty(preCellText) && preCellText.matches(modelPattern)) {
                                             preCellText = preCellText.replaceAll(modelPattern, "$1");
                                             this.renderCell(preCell, curRowMap.getOrDefault(preCellText, "/"));
                                          }
                                       }
                                    }
                                 }

                                 this.renderCell(insertNewTableRow.getTableCells().get(cellIndex - 1), subCellValue);
                                 this.renderCell(insertNewTableRow.getTableCells().get(cellIndex), cellValue);
                              }
                           }
                        );
                     List<String> allAvgValueList = new ArrayList<>();
                     if ("1".equals(renderData.getShowAvg())) {
                        XWPFTableRow avgTableRow = table.insertNewTableRow(dataIndexCount.getAndIncrement());

                        for (int ix = 0; ix < rowCellSize; ix++) {
                           WordExportUtil.copyTableCell(dataModelRow.getCell(ix), avgTableRow.createCell());
                        }

                        for (int ix = 0; ix < renderData.getPartitionSize(); ix++) {
                           int cellIndex = finalCellStartIndex + ix * 2;
                           String groupKey = groupKeyList.size() > ix ? String.valueOf(groupKeyList.get(ix)) : "";
                           List<String> valueList = allCellValueListMap.getOrDefault(groupKey, new ArrayList<>());
                           String avgVStr = "";
                           if (valueList.stream().allMatch(e -> "ND".equals(e))) {
                              avgVStr = "/";
                              allAvgValueList.add("ND");
                           } else {
                              Double sumV = valueList.stream()
                                 .filter(e -> NumberUtils.isNumber(e))
                                 .map(e -> Double.valueOf(e))
                                 .reduce((a, b) -> ArithUtils.add(a, b))
                                 .orElse(null);
                              int radix = this.getRadix(valueList.stream().filter(e -> NumberUtils.isNumber(e)).findFirst().orElse("0"));
                              avgVStr = sumV == null ? "/" : NumberUtils.rounding(ArithUtils.div(sumV, valueList.size()) + "", "FDA," + radix);
                              allAvgValueList.add(avgVStr);
                           }

                           this.renderCell(avgTableRow.getTableCells().get(cellIndex - 1), "均值");
                           this.renderCell(avgTableRow.getTableCells().get(cellIndex), avgVStr);
                        }

                        for (int j = 0; j < finalCellStartIndex - 1; j++) {
                           XWPFTableCell preCell = avgTableRow.getTableCells().get(j);
                           this.renderCell(preCell, "");
                        }
                     }

                     if (allAvgValueList.isEmpty()) {
                        allAvgValueList = allCellValueListMap.values().stream().flatMap(e -> e.stream()).collect(Collectors.toList());
                     }

                     String maxVStr = "";
                     if (allAvgValueList.stream().allMatch(e -> "ND".equals(e))) {
                        if (existYqMethod.get()) {
                           maxVStr = "0";
                        } else {
                           maxVStr = "ND";
                        }
                     } else {
                        maxVStr = allAvgValueList.stream()
                           .map(e -> scienNumberChangeUtil.upBackScienNumberShow(e))
                           .filter(e -> NumberUtils.isNumber(e))
                           .max(Comparator.comparing(e -> NumberUtils.parseDouble(e)))
                           .orElse("/");
                        maxVStr = scienNumberChangeUtil.changeUpScienNumberShow(maxVStr);
                     }

                     for (int ix = startRowIndex; ix < dataIndexCount.get(); ix++) {
                        XWPFTableRow row = table.getRow(ix);
                        XWPFTableCell lastCell = row.getCell(rowCellSize - 1);
                        this.renderCell(lastCell, maxVStr);
                     }

                     WordUtil.mergeCellsVertically(table, 0, startRowIndex, dataIndexCount.get() - 1);
                     WordUtil.mergeCellsVertically(table, rowCellSize - 1, startRowIndex, dataIndexCount.get() - 1);
                  }
               }
            );
         table.removeRow(dataStartRowIndex);
      }
   }

   private int getRadix(String number) {
      String num = number;
      boolean scientificNumber = NumberUtils.isScientificNumber(number);
      if (scientificNumber) {
         num = new BigDecimal(number).toPlainString();
      }

      return !num.contains(".") ? 0 : num.length() - num.indexOf(".") - 1;
   }

   private List<XWPFTable> splitTable(XWPFTable table, int partitionSize) {
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
