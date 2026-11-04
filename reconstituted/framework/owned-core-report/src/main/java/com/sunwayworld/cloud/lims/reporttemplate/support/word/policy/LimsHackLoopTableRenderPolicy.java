package com.sunwayworld.cloud.lims.reporttemplate.support.word.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.data.style.TableStyle;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.render.compute.DefaultELRenderDataCompute;
import com.deepoove.poi.render.compute.EnvModel;
import com.deepoove.poi.render.compute.RenderDataCompute;
import com.deepoove.poi.render.processor.DocumentProcessor;
import com.deepoove.poi.render.processor.EnvIterator;
import com.deepoove.poi.resolver.TemplateResolver;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.MetaTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.ReflectionUtils;
import com.deepoove.poi.util.TableTools;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.AbstractMergeTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandHVGroupTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandTableFirstTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsExpandTableSecondTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsFixedLoopMultiRowTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsFixedMultiColumnTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeOrdtaskVTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeTemplateRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeVTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsSplitHTableRenderData;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeTemplateRenderData.MergedColumn;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsMergeTemplateRenderData.MergedRow;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsReportCollectionUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.utils.WordUtil;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.IBody;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;

public class LimsHackLoopTableRenderPolicy extends LoopRowTableRenderPolicy {
   private String prefix;
   private String suffix;
   private static final String TITLE_SPLIT_TAG = ",";
   private static final Logger logger = LogManager.getLogger(LimsHackLoopTableRenderPolicy.class);

   public LimsHackLoopTableRenderPolicy() {
      this("[", "]");
   }

   public LimsHackLoopTableRenderPolicy(String prefix, String suffix) {
      this.prefix = prefix;
      this.suffix = suffix;
   }

   @Override
   public void render(ElementTemplate eleTemplate, Object renderData, XWPFTemplate template) {
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      if (!TableTools.isInsideTable(run)) {
         throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
      } else {
         run.setText("", 0);
         if (renderData != null
            && (!(renderData instanceof AbstractMergeTableRenderData) || !CollectionUtils.isEmpty(((AbstractMergeTableRenderData)renderData).getData()))) {
            if (renderData instanceof AbstractMergeTableRenderData) {
               try {
                  if (!TableTools.isInsideTable(run)) {
                     throw new IllegalStateException("The template tag " + runTemplate.getSource() + " must be inside a table");
                  }

                  if (renderData instanceof LimsMergeVTableRenderData) {
                     this.renderTableWithMergeCellV(run, (LimsMergeVTableRenderData)renderData, template);
                  } else if (renderData instanceof LimsSplitHTableRenderData) {
                     this.renderTableWithSplitCellH(run, (LimsSplitHTableRenderData)renderData, template);
                  } else if (renderData instanceof LimsMergeOrdtaskVTableRenderData) {
                     this.renderTableWithOrdtaskMergeCellV(run, (LimsMergeOrdtaskVTableRenderData)renderData, template);
                  } else if (renderData instanceof LimsMergeTemplateRenderData) {
                     this.renderNormalOrDynamicTable(eleTemplate, (LimsMergeTemplateRenderData)renderData, template);
                  } else if (renderData instanceof LimsFixedMultiColumnTemplateRenderData) {
                     LimsFixedMultiColumnTableRenderPolicy renderPolicy = new LimsFixedMultiColumnTableRenderPolicy();
                     renderPolicy.render(eleTemplate, renderData, template);
                  } else if (renderData instanceof LimsExpandTableFirstTemplateRenderData) {
                     LimsExpandTableFirstRenderPolicy renderPolicy = new LimsExpandTableFirstRenderPolicy();
                     renderPolicy.render(eleTemplate, renderData, template);
                  } else if (renderData instanceof LimsExpandTableSecondTemplateRenderData) {
                     LimsExpandTableSecondRenderPolicy renderPolicy = new LimsExpandTableSecondRenderPolicy();
                     renderPolicy.render(eleTemplate, renderData, template);
                  } else if (renderData instanceof LimsExpandHVGroupTableRenderData) {
                     LimsExpandHVGroupTableRenderPolicy renderPolicy = new LimsExpandHVGroupTableRenderPolicy();
                     renderPolicy.render(eleTemplate, renderData, template);
                  } else if (renderData instanceof LimsFixedLoopMultiRowTemplateRenderData) {
                     LimsFixedLoopMultiRowTableRenderPolicy renderPolicy = new LimsFixedLoopMultiRowTableRenderPolicy();
                     renderPolicy.render(eleTemplate, renderData, template);
                  }
               } catch (Exception var7) {
                  logger.error("渲染word时异常", (Throwable)var7);
                  throw new RenderException("LimsHackLoopTable for " + eleTemplate + "error: " + var7.getMessage(), var7);
               }
            } else {
               this.renderOriginal(eleTemplate, renderData, template);
            }
         } else {
            this.renderNullData(run, eleTemplate, template);
         }
      }
   }

   private void renderTableWithSplitCellH(XWPFRun run, LimsSplitHTableRenderData renderData, XWPFTemplate template) throws CloneNotSupportedException {
      LimsHackLoopTableRenderPolicy.LimsSplitTableDATA splitTableData = this.getSplitTableData(renderData);
      XWPFTableCell tagCell = WordExportUtil.getCurrentCell(run);
      XWPFTableRow titleTemplateRow = tagCell.getTableRow();
      XWPFTable table = titleTemplateRow.getTable();
      int startTitleCellIndex = WordExportUtil.getCellIndex(titleTemplateRow, tagCell);
      int titleRowIndex = WordExportUtil.getRowIndex(table, titleTemplateRow);
      int titleRowLastMergeIndex = WordExportUtil.getTagRowIndex(table, titleTemplateRow, startTitleCellIndex);
      int gridSpanCountOnBefore = WordExportUtil.getGridSpanCountOnBefore(titleTemplateRow, tagCell);
      if (!CollectionUtils.isEmpty(splitTableData.dataList)) {
         TemplateResolver resolver = new TemplateResolver(template.getConfig().copy(this.prefix, this.suffix));
         XWPFTableRow dataTemplateRow = table.getRow(titleRowLastMergeIndex + 1);
         int startDataCellIndex = WordExportUtil.getCellIndexByGridSpanCountOnBefore(dataTemplateRow, gridSpanCountOnBefore);
         Integer maxRowNum = renderData.getMaxCellCount();
         String titleField = renderData.getTitleField();
         String[] titleSplitArray = StringUtils.split(titleField, ",");
         int titleLength = titleSplitArray.length;
         titleRowLastMergeIndex = titleRowLastMergeIndex + titleLength - 1;
         int midRowCount = titleRowLastMergeIndex - titleRowIndex;
         String valueField = renderData.getValueField();
         String titleIdField = renderData.getTitleIdField();
         int headSize = splitTableData.headList.size();
         int dataSize = splitTableData.dataList.size();
         int maxCellSize = maxRowNum == null ? headSize : (headSize >= maxRowNum ? maxRowNum : headSize);
         TableStyle tableStyle = new TableStyle();
         if (renderData.getNeedHead()) {
            WordExportUtil.splitCellsVertically(titleTemplateRow.getCell(startTitleCellIndex), maxCellSize, titleLength);
            WordExportUtil.splitCell(table, dataTemplateRow, startDataCellIndex, maxCellSize, null, tableStyle, false);
         } else {
            WordExportUtil.splitCell(table, dataTemplateRow, startDataCellIndex, maxCellSize, null, tableStyle, true);
         }

         AtomicInteger rowIndex = new AtomicInteger(titleRowLastMergeIndex + 2);
         int repNum = headSize / maxCellSize + (headSize % maxCellSize > 0 ? 1 : 0);
         int cellSize = dataTemplateRow.getTableCells().size();
         int[] mergeCellIndexArray = renderData.getMergeCellIndexArray();

         for (int i = 0; i < repNum; i++) {
            Map<Integer, String> mergeMap = new HashMap<>();
            if (mergeCellIndexArray != null) {
               for (int cellIndex : mergeCellIndexArray) {
                  mergeMap.put(cellIndex, null);
               }
            }

            for (int tmpIndex = 0; tmpIndex <= titleRowLastMergeIndex; tmpIndex++) {
               WordExportUtil.copyTableRowFromTemplateRow(table, table.getRow(tmpIndex), rowIndex.getAndIncrement());
            }

            int newTitleRowIndex = rowIndex.get() - midRowCount - 1;
            XWPFTableRow titleRow = table.getRow(newTitleRowIndex);
            List<XWPFTableRow> copyDataRowList = new ArrayList<>();
            splitTableData.dataList
               .forEach(
                  tableRowData -> {
                     int currentIndex = rowIndex.getAndIncrement();
                     XWPFTableRow dataRowx = WordExportUtil.copyTableRowFromTemplateRow(table, dataTemplateRow, currentIndex);
                     RenderDataCompute dataComputex = template.getConfig()
                        .getRenderDataComputeFactory()
                        .newCompute(EnvModel.of(tableRowData, EnvIterator.makeEnv(currentIndex, currentIndex < dataSize - 1)));

                     for (int cellIndex = 0; cellIndex < cellSize; cellIndex++) {
                        if (startDataCellIndex > cellIndex || cellIndex >= startDataCellIndex + maxCellSize) {
                           XWPFTableCell cell = dataRowx.getCell(cellIndex);
                           WordExportUtil.addCellValue(cell, template, resolver, dataComputex);
                           if (mergeMap.containsKey(cellIndex)) {
                              String newData = this.dealMergeCell(cell, mergeMap.get(cellIndex));
                              if (!StringUtils.isEmpty(newData)) {
                                 mergeMap.put(cellIndex, newData);
                              }
                           }
                        }
                     }

                     copyDataRowList.add(dataRowx);
                  }
               );
            Map<Integer, String> titleMergeMap = new HashMap<>();
            int[] titleIndexArray = new int[titleLength];

            for (int j = 0; j < maxCellSize; j++) {
               int titleCellIndex = j + startTitleCellIndex;
               int dataCellIndex = j + startDataCellIndex;
               int valueIndex = i * maxCellSize + j;
               if (valueIndex >= headSize) {
                  break;
               }

               LimsTemplateDataMap title = splitTableData.headList.get(valueIndex);
               if (renderData.getNeedHead()) {
                  if (titleLength <= 1) {
                     XWPFTableCell titleCell = titleRow.getCell(titleCellIndex);
                     WordExportUtil.addCellValue(titleCell, ObjectUtils.toString(title.get(titleField)));
                  } else {
                     XWPFTableCell upperCell = null;

                     for (int titleIndex = 0; titleIndex < titleLength - 1; titleIndex++) {
                        String titleValue = ObjectUtils.toString(title.get(titleSplitArray[titleIndex]));
                        int currentTitleIndex = titleIndexArray[titleIndex];
                        String existsTitleValue = titleMergeMap.get(titleIndex);
                        XWPFTableCell titleCell = table.getRow(newTitleRowIndex + titleIndex).getCell(currentTitleIndex);
                        if (StringUtils.isEmpty(titleValue)) {
                           WordExportUtil.setCellMerge(titleCell, upperCell == null);
                           if (upperCell == null) {
                              upperCell = titleCell;
                           }

                           titleIndexArray[titleIndex] = currentTitleIndex + 1;
                        } else if (StringUtils.equals(titleValue, existsTitleValue)) {
                           TableTools.mergeCellsHorizonal(table, newTitleRowIndex + titleIndex, currentTitleIndex - 1, currentTitleIndex);
                        } else {
                           titleMergeMap.put(titleIndex, titleValue);
                           if (upperCell != null) {
                              WordExportUtil.setCellMerge(titleCell, false);
                              WordExportUtil.addCellValue(upperCell, titleValue);
                              upperCell = null;
                           } else {
                              WordExportUtil.addCellValue(titleCell, titleValue);
                           }

                           titleIndexArray[titleIndex] = currentTitleIndex + 1;
                        }
                     }

                     XWPFTableCell titleCell = table.getRow(newTitleRowIndex + titleLength - 1).getCell(titleCellIndex);
                     String titleValue = ObjectUtils.toString(title.get(titleSplitArray[titleLength - 1]));
                     if (upperCell != null) {
                        WordExportUtil.addCellValue(upperCell, titleValue);
                        WordExportUtil.setCellMerge(titleCell, false);
                     } else {
                        WordExportUtil.addCellValue(titleCell, titleValue);
                     }
                  }
               }

               for (int dataRowIndex = 0; dataRowIndex < dataSize; dataRowIndex++) {
                  LimsTemplateDataMap crossData = splitTableData.dataList.get(dataRowIndex);
                  LimsTemplateDataMap data = (LimsTemplateDataMap)crossData.get(title.get(titleIdField));
                  XWPFTableRow dataRow = copyDataRowList.get(dataRowIndex);
                  XWPFTableCell dataCell = dataRow.getCell(dataCellIndex);
                  RenderDataCompute dataCompute = template.getConfig()
                     .getRenderDataComputeFactory()
                     .newCompute(EnvModel.of(data, EnvIterator.makeEnv(dataRowIndex, dataRowIndex < dataSize - 1)));
                  List<MetaTemplate> templates = resolver.resolveBodyElements(dataCell.getBodyElements());
                  if (!CollectionUtils.isEmpty(templates)) {
                     WordExportUtil.addCellValue(template, templates, resolver, dataCompute);
                  } else if (data != null) {
                     String pivotValue = ObjectUtils.toString(data.get(valueField));
                     WordExportUtil.addCellValue(dataCell, pivotValue);
                  }
               }
            }
         }
      }

      table.removeRow(titleRowLastMergeIndex + 1);

      for (int tmpIndex = titleRowLastMergeIndex; tmpIndex >= 0; tmpIndex--) {
         table.removeRow(tmpIndex);
      }
   }

   private void setTableRow(XWPFTable table, XWPFTableRow templateRow, int pos) {
      List<XWPFTableRow> rows = (List<XWPFTableRow>)ReflectionUtils.getValue("tableRows", table);
      rows.set(pos, templateRow);
      table.getCTTbl().setTrArray(pos, templateRow.getCtRow());
   }

   private int getRowIndex(XWPFTable table, XWPFTableRow row) {
      List<XWPFTableRow> rows = table.getRows();
      return rows.indexOf(row);
   }

   private void renderTableWithMergeCellV(XWPFRun run, LimsMergeVTableRenderData renderData, XWPFTemplate template) throws CloneNotSupportedException {
      List<LimsTemplateDataMap> data = renderData.getData();
      XWPFTableCell tagCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
      this.extractAndUnifyRunStyle(run, tagCell);
      XWPFTableRow tagRow = tagCell.getTableRow();
      XWPFTable table = tagCell.getTableRow().getTable();
      int templateRowIndex = this.getRowIndex(table, tagRow) + 1;
      if (null != data) {
         Iterator<LimsTemplateDataMap> iterator = data.iterator();
         XWPFTableRow templateRow = table.getRow(templateRowIndex);
         Configure configure = template.getConfig().copy(this.prefix, this.suffix);
         TemplateResolver resolver = new TemplateResolver(configure);
         int index = 1;
         String mergeByField = renderData.getMergeByField();
         String mergeByValue = null;
         Map<Integer, String> mergeMap = new HashMap<Integer, String>() {
            {
               int[] mergeCellIndexArray = renderData.getMergeCellIndexArray();
               if (!ObjectUtils.isEmpty(mergeCellIndexArray)) {
                  for (int cellIndex : mergeCellIndexArray) {
                     this.put(cellIndex, null);
                  }
               }
            }
         };

         boolean hasNext;
         while (hasNext = iterator.hasNext()) {
            int insertPosition = templateRowIndex++;
            XWPFTableRow nextRow = table.insertNewTableRow(insertPosition);
            this.setTableRow(table, templateRow, insertPosition);
            XmlCursor newCursor = templateRow.getCtRow().newCursor();
            newCursor.toPrevSibling();
            XmlObject object = newCursor.getObject();
            nextRow = new XWPFTableRow((CTRow)object, table);
            this.setTableRow(table, nextRow, insertPosition);
            LimsTemplateDataMap rowData = iterator.next();
            RenderDataCompute dataELCompute = new DefaultELRenderDataCompute(EnvModel.of(rowData, EnvIterator.makeEnv(index, hasNext)), false);
            RenderDataCompute dataSPELCompute = template.getConfig()
               .getRenderDataComputeFactory()
               .newCompute(EnvModel.of(rowData, EnvIterator.makeEnv(index++, hasNext)));
            List<XWPFTableCell> cellList = nextRow.getTableCells();
            String checkValue = ObjectUtils.toString(rowData.get(mergeByField));
            boolean sameCheckValue = StringUtils.equalsIgnoreCase(mergeByValue, checkValue);
            mergeByValue = checkValue;

            for (int j = 0; j < cellList.size(); j++) {
               XWPFTableCell cell = cellList.get(j);
               List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());

               try {
                  new DocumentProcessor(template, resolver, dataSPELCompute).process(templates);
               } catch (IllegalStateException var34) {
                  new DocumentProcessor(template, resolver, dataELCompute).process(templates);
               }

               if (mergeMap.containsKey(j)) {
                  List<Integer> notMergeByFieldList = renderData.getNotMergeByFiledIndexArray();
                  if (!StringUtils.isEmpty(mergeByField) && (notMergeByFieldList == null || !notMergeByFieldList.contains(j))) {
                     WordExportUtil.setCellMerge(cell, !sameCheckValue);
                  } else {
                     boolean blankValueMergeFlag = renderData.isBlankValueMergeFlag();
                     if (blankValueMergeFlag) {
                        String text = this.dealMergeCell(cell, mergeMap.get(j));
                        mergeMap.put(j, text);
                     } else {
                        String text = this.dealMergeCellBlankValueNotMerge(cell, mergeMap.get(j));
                        mergeMap.put(j, text);
                     }
                  }
               }
            }
         }
      }

      table.removeRow(templateRowIndex);
      if (!renderData.getNeedHead()) {
         table.removeRow(this.getRowIndex(table, tagRow));
      }
   }

   private void renderTableWithOrdtaskMergeCellV(XWPFRun run, LimsMergeOrdtaskVTableRenderData renderData, XWPFTemplate template) throws CloneNotSupportedException {
      List<LimsTemplateDataMap> data = renderData.getData();
      String mergeByField = renderData.getMergeByField();
      String mergeByValue = null;
      List<List<Integer>> mergeList = renderData.getMergeHIndexList();
      XWPFTableCell tagCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
      XWPFTableRow tagRow = tagCell.getTableRow();
      XWPFTable table = tagCell.getTableRow().getTable();
      int templateRowIndex = this.getRowIndex(table, tagRow) + 1;
      if (!CollectionUtils.isEmpty(data)) {
         Iterator<LimsTemplateDataMap> iterator = data.iterator();
         XWPFTableRow templateRow = table.getRow(templateRowIndex);
         Configure configure = template.getConfig().copy(this.prefix, this.suffix);
         TemplateResolver resolver = new TemplateResolver(configure);
         int index = 1;
         Map<Integer, String> mergeMap = new HashMap<Integer, String>() {
            {
               List<Integer> mergeVIndexList = renderData.getMergeVIndexList();
               if (!mergeVIndexList.isEmpty()) {
                  for (int cellIndex : mergeVIndexList) {
                     this.put(cellIndex, null);
                  }
               }
            }
         };

         boolean hasNext;
         while (hasNext = iterator.hasNext()) {
            int insertPosition = templateRowIndex++;
            XWPFTableRow nextRow = table.insertNewTableRow(insertPosition);
            this.setTableRow(table, templateRow, insertPosition);
            XmlCursor newCursor = templateRow.getCtRow().newCursor();
            newCursor.toPrevSibling();
            XmlObject object = newCursor.getObject();
            nextRow = new XWPFTableRow((CTRow)object, table);
            this.setTableRow(table, nextRow, insertPosition);
            LimsTemplateDataMap rowData = iterator.next();
            RenderDataCompute dataELCompute = new DefaultELRenderDataCompute(EnvModel.of(rowData, EnvIterator.makeEnv(index, hasNext)), false);
            RenderDataCompute dataSPELCompute = template.getConfig()
               .getRenderDataComputeFactory()
               .newCompute(EnvModel.of(rowData, EnvIterator.makeEnv(index++, hasNext)));
            List<XWPFTableCell> cellList = nextRow.getTableCells();
            String checkValue = ObjectUtils.toString(rowData.get(mergeByField));
            boolean sameCheckValue = StringUtils.equalsIgnoreCase(mergeByValue, checkValue);
            mergeByValue = checkValue;

            for (int j = 0; j < cellList.size(); j++) {
               XWPFTableCell cell = cellList.get(j);
               List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());

               try {
                  new DocumentProcessor(template, resolver, dataSPELCompute).process(templates);
               } catch (IllegalStateException var34) {
                  new DocumentProcessor(template, resolver, dataELCompute).process(templates);
               }

               if (mergeMap.containsKey(j)) {
                  if (!StringUtils.isEmpty(mergeByField)) {
                     WordExportUtil.setCellMerge(cell, !sameCheckValue);
                  } else {
                     String text = this.dealMergeCell(cell, mergeMap.get(j));
                     mergeMap.put(j, text);
                  }
               }
            }
         }

         marge(table, 0, mergeList, 0);
      }

      table.removeRow(templateRowIndex);
      if (!renderData.getNeedHead()) {
         table.removeRow(this.getRowIndex(table, tagRow));
      }
   }

   private List<List<Integer>> parseMargetList(List<LimsTemplateDataMap> rows) {
      List<List<Integer>> margeList = new ArrayList<>();
      List<Long> ordtaskIdList = rows.stream().map(e -> ((BigDecimal)e.get("ordtaskid")).longValue()).distinct().collect(Collectors.toList());
      Map<Long, List<LimsTemplateDataMap>> resultMap = rows.stream().collect(Collectors.groupingBy(e -> ((BigDecimal)e.get("ordtaskid")).longValue()));
      int rowNo = 0;

      for (Long index : ordtaskIdList) {
         List<LimsTemplateDataMap> list = resultMap.get(index);
         if (list.size() == 1) {
            LimsTemplateDataMap result = list.get(0);
            String standardRequest = (String)result.get("tname");
            String itemAliasName = (String)result.get("subanalyticitem");
            String testname = (String)result.get("testname");
            String itemname = (String)result.get("itemname");
            if (!StringUtils.isEmpty(standardRequest)) {
               if (StringUtils.isEmpty(itemAliasName)) {
                  if (testname != null && testname.equals(itemname)) {
                     Integer[] arr = new Integer[]{rowNo, 1, rowNo, 3};
                     margeList.add(Arrays.asList(arr));
                  } else {
                     Integer[] arr = new Integer[]{rowNo, 2, rowNo, 3};
                     margeList.add(Arrays.asList(arr));
                  }
               }
            } else if (!StringUtils.isEmpty(itemAliasName)) {
               Integer[] arr = new Integer[]{rowNo, 0, rowNo, 1};
               margeList.add(Arrays.asList(arr));
            } else if (StringUtils.equals(testname, itemname)) {
               Integer[] arr = new Integer[]{rowNo, 0, rowNo, 3};
               margeList.add(Arrays.asList(arr));
            } else {
               Integer[] arr = new Integer[]{rowNo, 2, rowNo, 3};
               margeList.add(Arrays.asList(arr));
               Integer[] arr1 = new Integer[]{rowNo, 0, rowNo, 1};
               margeList.add(Arrays.asList(arr1));
            }
         } else {
            List<String> standardRequestList = list.stream()
               .map(e -> (String)e.get("tname"))
               .filter(f -> !StringUtils.isBlank(f))
               .distinct()
               .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(standardRequestList)) {
               List<String> collect = list.stream().map(item -> (String)item.get("itemname")).distinct().collect(Collectors.toList());
               List<String> collect2 = list.stream()
                  .map(item -> (String)item.get("subanalyticitem"))
                  .filter(sub -> sub != null)
                  .distinct()
                  .collect(Collectors.toList());
               if (collect.size() == 1 && collect.get(0) != null && collect.get(0).equals((String)list.get(0).get("testname")) && !collect2.isEmpty()) {
                  Integer[] arr = new Integer[]{rowNo, 0, rowNo + list.size() - 1, 2};
                  margeList.add(Arrays.asList(arr));
               } else {
                  Integer[] arr = new Integer[]{rowNo, 0, rowNo + list.size() - 1, 1};
                  margeList.add(Arrays.asList(arr));
                  this.margeListFromItem(margeList, rowNo, list);
               }
            } else {
               Integer[] arr = new Integer[]{rowNo, 0, rowNo + list.size() - 1, 0};
               margeList.add(Arrays.asList(arr));
               arr = new Integer[]{rowNo, 1, rowNo + list.size() - 1, 1};
               margeList.add(Arrays.asList(arr));
               this.margeListFromItem(margeList, rowNo, list);
            }
         }

         rowNo += list.size();
      }

      return margeList;
   }

   private List<List<Integer>> parseMargetListBy(List<LimsTemplateDataMap> rows, String mergeByField) {
      List<List<Integer>> margeList = new ArrayList<>();
      List<String> groupIds = rows.stream().map(r -> r.get(mergeByField).toString()).distinct().collect(Collectors.toList());
      Map<String, List<LimsTemplateDataMap>> groupMap = rows.stream().collect(Collectors.groupingBy(e -> e.get(mergeByField).toString()));
      int rowNo = 0;

      for (String index : groupIds) {
         List<LimsTemplateDataMap> list = groupMap.get(index);
         if (list.size() == 1) {
            LimsTemplateDataMap result = list.get(0);
            String standardRequest = (String)result.get("tname");
            String itemAliasName = (String)result.get("subanalyticitem");
            String testname = (String)result.get("testname");
            String itemname = (String)result.get("itemname");
            if (!StringUtils.isEmpty(standardRequest)) {
               if (StringUtils.isEmpty(itemAliasName)) {
                  if (StringUtils.equals(testname, itemname)) {
                     Integer[] arr = new Integer[]{rowNo, 1, rowNo, 3};
                     margeList.add(Arrays.asList(arr));
                  } else {
                     Integer[] arr = new Integer[]{rowNo, 2, rowNo, 3};
                     margeList.add(Arrays.asList(arr));
                  }
               }
            } else if (!StringUtils.isEmpty(itemAliasName)) {
               Integer[] arr = new Integer[]{rowNo, 0, rowNo, 1};
               margeList.add(Arrays.asList(arr));
            } else if (StringUtils.equals(testname, itemname)) {
               Integer[] arr = new Integer[]{rowNo, 0, rowNo, 3};
               margeList.add(Arrays.asList(arr));
            } else {
               Integer[] arr = new Integer[]{rowNo, 2, rowNo, 3};
               margeList.add(Arrays.asList(arr));
               Integer[] arr1 = new Integer[]{rowNo, 0, rowNo, 1};
               margeList.add(Arrays.asList(arr1));
            }
         } else {
            List<String> standardRequestList = list.stream()
               .map(e -> (String)e.get("tname"))
               .filter(f -> !StringUtils.isBlank(f))
               .distinct()
               .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(standardRequestList)) {
               List<String> collect = list.stream().map(item -> (String)item.get("itemname")).distinct().collect(Collectors.toList());
               List<String> collect2 = list.stream()
                  .map(item -> (String)item.get("subanalyticitem"))
                  .filter(sub -> sub != null)
                  .distinct()
                  .collect(Collectors.toList());
               if (collect.size() == 1 && collect.get(0) != null && collect.get(0).equals((String)list.get(0).get("testname")) && !collect2.isEmpty()) {
                  Integer[] arr = new Integer[]{rowNo, 0, rowNo + list.size() - 1, 2};
                  margeList.add(Arrays.asList(arr));
               } else {
                  Integer[] arr = new Integer[]{rowNo, 0, rowNo + list.size() - 1, 1};
                  margeList.add(Arrays.asList(arr));
                  this.margeListFromItem(margeList, rowNo, list);
               }
            } else {
               Integer[] arr = new Integer[]{rowNo, 0, rowNo + list.size() - 1, 0};
               margeList.add(Arrays.asList(arr));
               Integer[] arr1 = new Integer[]{rowNo, 1, rowNo + list.size() - 1, 1};
               margeList.add(Arrays.asList(arr1));
               this.margeListFromItem(margeList, rowNo, list);
            }
         }

         rowNo += list.size();
      }

      return margeList;
   }

   private void margeListFromItem(List<List<Integer>> margeList, int rowNo, List<LimsTemplateDataMap> list) {
      int sameItemRowNo = 0;
      list.forEach(e -> {
         if (e.get("itemname") == null) {
            e.put("itemname", "");
         }
      });
      List<String> itemNameList = list.stream().map(e -> (String)e.get("itemname")).distinct().collect(Collectors.toList());
      Map<String, List<LimsTemplateDataMap>> itemNameMap = list.stream().collect(Collectors.groupingBy(e -> (String)e.get("itemname")));

      for (String itemName : itemNameList) {
         List<LimsTemplateDataMap> sameItemNameList = itemNameMap.get(itemName);
         if (sameItemNameList.size() > 1) {
            Integer[] arr1 = new Integer[]{rowNo + sameItemRowNo, 2, rowNo + sameItemRowNo + sameItemNameList.size() - 1, 2};
            margeList.add(Arrays.asList(arr1));
         } else {
            LimsTemplateDataMap dtoResult = sameItemNameList.get(0);
            if (StringUtils.isEmpty((String)dtoResult.get("subanalyticitem"))) {
               Integer[] arr1 = new Integer[]{rowNo + sameItemRowNo, 2, rowNo + sameItemRowNo, 3};
               margeList.add(Arrays.asList(arr1));
            }
         }

         sameItemRowNo += sameItemNameList.size();
      }
   }

   private static void marge(XWPFTable table, int index, List<List<Integer>> margeList, int heardCol) {
      margeList.forEach(e -> {
         int firstRow = e.get(0) + index;
         int firstColumn = e.get(1) + heardCol;
         int lastRow = e.get(2) + index;
         int lastColumn = e.get(3) + heardCol;
         if (firstColumn == lastColumn) {
            for (int x = firstRow + 1; x <= lastRow; x++) {
               WordUtil.cleanData(table.getRow(x).getCell(firstColumn));
            }

            WordUtil.mergeCellsVertically(table, firstColumn, firstRow, lastRow);
         } else if (firstRow == lastRow) {
            if (e.get(3) != -1) {
               for (int x = firstColumn + 1; x <= lastColumn; x++) {
                  WordUtil.cleanData(table.getRow(firstRow).getCell(x));
               }
            } else {
               for (int x = 1; x <= table.getRow(firstRow).getTableCells().size() - 1; x++) {
                  WordUtil.cleanData(table.getRow(firstRow).getCell(x));
               }

               WordUtil.mergeCellsHorizontal(table, firstRow, 0, table.getRow(firstRow).getTableCells().size() - 1);
            }

            WordUtil.mergeCellsHorizontal(table, firstRow, firstColumn, lastColumn);
         } else {
            for (int j = firstRow; j < lastRow + 1; j++) {
               for (int j2 = firstColumn; j2 < lastColumn + 1; j2++) {
                  if (j != firstRow || j2 != firstColumn) {
                     WordUtil.cleanData(table.getRow(j).getCell(j2));
                  }
               }
            }

            for (int j = firstRow; j < lastRow + 1; j++) {
               WordUtil.mergeCellsHorizontal(table, j, firstColumn, lastColumn);
            }

            for (int j = firstColumn; j < lastColumn + 1; j++) {
               WordUtil.mergeCellsVertically(table, j, firstRow, lastRow);
            }
         }
      });
   }

   private void renderOriginal(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      XWPFTableCell tagCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
      XWPFTableRow tagRow = tagCell.getTableRow();
      XWPFTable table = tagCell.getTableRow().getTable();
      int templateRowIndex = WordExportUtil.getTagRowIndex(table, tagRow, WordExportUtil.getCellIndex(tagRow, tagCell)) + 1;

      try {
         this.doRenderOriginal(templateRowIndex, table, data, template);
      } catch (Exception var11) {
         throw new RenderException("HackLoopTable for " + eleTemplate + "error: " + var11.getMessage(), var11);
      }
   }

   private void doRenderOriginal(int templateRowIndex, XWPFTable table, Object data, XWPFTemplate template) {
      try {
         if (data instanceof Iterable) {
            Iterator<LimsTemplateDataMap> iterator = ((Iterable)data).iterator();
            XWPFTableRow templateRow = table.getRow(templateRowIndex);
            TemplateResolver resolver = new TemplateResolver(template.getConfig().copy(this.prefix, this.suffix));
            int index = 1;

            boolean hasNext;
            while (hasNext = iterator.hasNext()) {
               int insertPosition = templateRowIndex++;
               XWPFTableRow nextRow = table.insertNewTableRow(insertPosition);
               this.setTableRow(table, templateRow, insertPosition);
               XmlCursor newCursor = templateRow.getCtRow().newCursor();
               newCursor.toPrevSibling();
               XmlObject object = newCursor.getObject();
               nextRow = new XWPFTableRow((CTRow)object, table);
               this.setTableRow(table, nextRow, insertPosition);
               LimsTemplateDataMap next = iterator.next();
               RenderDataCompute dataCompute = template.getConfig()
                  .getRenderDataComputeFactory()
                  .newCompute(EnvModel.of(next, EnvIterator.makeEnv(index++, hasNext)));
               List<XWPFTableCell> cells = nextRow.getTableCells();
               cells.forEach(cell -> {
                  List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());
                  new DocumentProcessor(template, resolver, dataCompute).process(templates);
               });
            }
         }

         table.removeRow(templateRowIndex);
         this.afterloop(table, data);
      } catch (Exception var17) {
         throw new RenderException(var17.getMessage(), var17);
      }
   }

   private String dealMergeCell(XWPFTableCell cell, String compare) {
      String text = cell.getText();
      CTTcPr tcPr = WordExportUtil.getTcPr(cell.getCTTc());
      CTVMerge vMerge = tcPr.getVMerge();
      if (vMerge == null) {
         vMerge = tcPr.addNewVMerge();
      }

      WordExportUtil.setCellMerge(cell, !StringUtils.equalsIgnoreCase(text, compare));
      return text;
   }

   private String dealMergeCellBlankValueNotMerge(XWPFTableCell cell, String compare) {
      String text = cell.getText();
      CTTcPr tcPr = WordExportUtil.getTcPr(cell.getCTTc());
      CTVMerge vMerge = tcPr.getVMerge();
      if (vMerge == null) {
         vMerge = tcPr.addNewVMerge();
      }

      WordExportUtil.setCellMerge(cell, StringUtils.isBlank(compare) || StringUtils.isBlank(text) || !StringUtils.equalsIgnoreCase(text, compare));
      return text;
   }

   private void renderNullData(XWPFRun run, ElementTemplate runTemplate, XWPFTemplate template) {
      String tagName = runTemplate.getTagName();
      XWPFTableCell tagCell = WordExportUtil.getCurrentCell(run);
      XWPFTableRow titleTemplateRow = tagCell.getTableRow();
      XWPFTable table = titleTemplateRow.getTable();
      int startRowIndex = WordExportUtil.getTagRowIndex(table, titleTemplateRow, WordExportUtil.getCellIndex(titleTemplateRow, tagCell));
      XWPFTableRow dataRow = table.getRow(startRowIndex + 1);
      if (dataRow != null) {
         try {
            TemplateResolver resolver = new TemplateResolver(template.getConfig().copy(this.prefix, this.suffix));
            RenderDataCompute dataELCompute = new DefaultELRenderDataCompute(EnvModel.of(new LimsTemplateDataMap(), EnvIterator.makeEnv(0, false)), false);
            RenderDataCompute dataSPELCompute = template.getConfig().getRenderDataComputeFactory().newCompute(EnvModel.ofModel(new LimsTemplateDataMap()));
            dataRow.getTableCells().forEach(cell -> {
               List<MetaTemplate> templates = resolver.resolveBodyElements(cell.getBodyElements());

               try {
                  new DocumentProcessor(template, resolver, dataSPELCompute).process(templates);
               } catch (IllegalStateException var7x) {
                  new DocumentProcessor(template, resolver, dataELCompute).process(templates);
               }
            });
         } catch (CloneNotSupportedException var13) {
            var13.printStackTrace();
         }

         if (StringUtils.containsIgnoreCase(tagName, "mergeVNoHead")) {
            table.removeRow(startRowIndex);
         }
      }
   }

   private LimsHackLoopTableRenderPolicy.LimsSplitTableDATA getSplitTableData(LimsSplitHTableRenderData renderData) {
      LimsTemplateDataList<LimsTemplateDataMap> sourceList = renderData.getData();
      LimsHackLoopTableRenderPolicy.LimsSplitTableDATA splitTableDTO = new LimsHackLoopTableRenderPolicy.LimsSplitTableDATA();
      if (!CollectionUtils.isEmpty(sourceList)) {
         String pivotField = renderData.getPivotField();
         String titleIdField = renderData.getTitleIdField();
         String subField = renderData.getSubFiled();
         String[] pivotFieldArray = StringUtils.split(pivotField, ",");
         LimsTemplateDataList<LimsTemplateDataMap> crossDataList;
         List<LimsTemplateDataMap> titleList;
         if (StringUtils.isEmpty(subField)) {
            LinkedHashMap<String, LimsTemplateDataList<LimsTemplateDataMap>> tempList = sourceList.stream().collect(Collectors.groupingBy(map -> {
               StringBuffer sb = new StringBuffer();

               for (String field : pivotFieldArray) {
                  sb.append(Optional.ofNullable(map.get(field)).orElse("-")).append("-");
               }

               return sb.substring(0, sb.length() - 1);
            }, LinkedHashMap::new, Collectors.toCollection(LimsTemplateDataList::new)));
            crossDataList = tempList.values()
               .stream()
               .map(
                  limsTemplateDataMaps -> {
                     LimsTemplateDataMap<Object> dataMap = limsTemplateDataMaps.stream()
                        .collect(
                           Collectors.toMap(map -> ObjectUtils.toString(map.get(titleIdField)), Function.identity(), (ov, nv) -> nv, LimsTemplateDataMap::new)
                        );

                     for (String field : pivotFieldArray) {
                        dataMap.put(field, limsTemplateDataMaps.get(0).get(field));
                     }

                     return dataMap;
                  }
               )
               .collect(Collectors.toCollection(LimsTemplateDataList::new));
            titleList = LimsReportCollectionUtils.distinct(sourceList, map -> map.get(titleIdField));
         } else {
            crossDataList = sourceList.stream()
               .map(
                  source -> {
                     LimsTemplateDataMap dataMap = new LimsTemplateDataMap();
                     LimsTemplateDataList<LimsTemplateDataMap> subList = (LimsTemplateDataList<LimsTemplateDataMap>)source.get(subField);
                     if (!CollectionUtils.isEmpty(subList)) {
                        dataMap = subList.stream()
                           .collect(
                              Collectors.toMap(
                                 subData -> ObjectUtils.toString(subData.get(titleIdField)),
                                 Function.identity(),
                                 (oldValue, newValue) -> newValue,
                                 LimsTemplateDataMap::new
                              )
                           );
                     }

                     dataMap.putAll(source);
                     dataMap.remove(subField);
                     return dataMap;
                  }
               )
               .collect(Collectors.toCollection(LimsTemplateDataList::new));
            List<LimsTemplateDataMap> allSplitDataList = sourceList.stream()
               .map(source -> Optional.ofNullable((Collection<?>)source.get(renderData.getSubFiled())).orElse(new ArrayList<>()))
               .flatMap(values -> values.stream())
               .map(value -> (LimsTemplateDataMap)value)
               .collect(Collectors.toList());
            titleList = LimsReportCollectionUtils.distinct(allSplitDataList, data -> Optional.ofNullable(data.get(titleIdField)).orElse("-"));
         }

         splitTableDTO.dataList = crossDataList;
         splitTableDTO.headList = titleList;
      }

      return splitTableDTO;
   }

   private void renderNormalOrDynamicTable(ElementTemplate eleTemplate, LimsMergeTemplateRenderData renderData, XWPFTemplate template) throws CloneNotSupportedException {
      NiceXWPFDocument document = template.getXWPFDocument();
      RunTemplate runTemplate = (RunTemplate)eleTemplate;
      XWPFRun run = runTemplate.getRun();
      XWPFTableCell tagCell = (XWPFTableCell)((XWPFParagraph)run.getParent()).getBody();
      this.extractAndUnifyRunStyle(run, tagCell);
      XWPFTableRow tagRow = tagCell.getTableRow();
      XWPFTable table = tagCell.getTableRow().getTable();
      int tagRowIndex = table.getRows().indexOf(tagRow);
      int firstDataRowIndex = WordExportUtil.getTagRowIndex(table, tagRow, WordExportUtil.getCellIndex(tagRow, tagCell)) + 1;
      XWPFTableRow firstDataRow = table.getRows().get(firstDataRowIndex);
      int tableWidth = table.getWidth();
      if (tagRowIndex + 1 != firstDataRowIndex) {
         tagRowIndex = firstDataRowIndex - 1;
         tagRow = table.getRow(tagRowIndex);
      }

      Pattern contentPattern = Pattern.compile("\\[(?<content>[^\\[\\]]+)\\]");
      List<String> dynamicCellList = new ArrayList<>();
      List<String> oldCellStrList = new ArrayList<>();
      String modelPattern = "\\[(\\w*)\\]";
      double widthSum = 0.0;
      int maxDynamicCellSize = renderData.getMaxDynamicCellSize();
      List<Integer> overCellIndexList = new ArrayList<>();
      String preTag = null;
      int count = 0;
      int gridSpan = 0;
      int i = 0;

      while (i < firstDataRow.getTableCells().size()) {
         XWPFTableCell curTableCell = this.getTrueCell(tagRow, i);
         String cellText = curTableCell.getText();
         cellText = StringUtils.isEmpty(cellText) ? cellText : cellText.trim();
         CTVMerge vMerge = WordExportUtil.getTcPr(curTableCell).getVMerge();
         XWPFTableCell resultCell = firstDataRow.getTableCells().get(i);
         String resultText = resultCell.getText();
         gridSpan = WordExportUtil.getTcPr(curTableCell).getGridSpan() == null ? 1 : WordExportUtil.getTcPr(curTableCell).getGridSpan().getVal().intValue();
         if (gridSpan > 1) {
            for (int j = 0; j < gridSpan; j++) {
               oldCellStrList.add(null);
            }
         } else if (vMerge != null) {
            oldCellStrList.add(null);
         } else if (StringUtils.isEmpty(cellText) && preTag == null) {
            oldCellStrList.add(null);
         } else if (StringUtils.isEmpty(cellText)) {
            count++;
            widthSum = ArithUtils.add(widthSum, curTableCell.getWidth());
            if (count > maxDynamicCellSize) {
               overCellIndexList.add(i);
               oldCellStrList.add(null);
            } else {
               oldCellStrList.add(preTag);
            }
         } else if (cellText.matches(contentPattern.toString()) && !StringUtils.isEmpty(resultText)) {
            Matcher contentMatcher = contentPattern.matcher(cellText);
            contentMatcher.find();
            String dynamicCell = resultText.replaceFirst(contentPattern.toString(), "$1").trim() + "$" + contentMatcher.group("content");
            dynamicCellList.add(dynamicCell);
            oldCellStrList.add(dynamicCell);
            preTag = dynamicCell;
            count = 1;
            widthSum = ArithUtils.add(widthSum, curTableCell.getWidth());
         } else {
            oldCellStrList.add(null);
         }

         i += gridSpan;
      }

      LimsTemplateDataList<LimsTemplateDataMap> oldDataMapList = renderData.getData() == null ? new LimsTemplateDataList<>() : renderData.getData();
      LimsTemplateDataList<LimsTemplateDataMap> newDataMapList = oldDataMapList;
      Map<String, Deque<String>> dynamicCellMap = new HashMap<>();
      Map<String, LimsTemplateDataMap> dynamicCellValueVerticalDataMap = new HashMap<>();
      Set<String> allColumnKeySet = new HashSet<>();

      for (String dynamicCell : dynamicCellList) {
         String[] dynamicSplits = dynamicCell.split("\\$");
         if (dynamicSplits.length >= 4) {
            String resultField = dynamicSplits[0];
            String dynamicField = dynamicSplits[1];
            String orderField = dynamicSplits[2];
            Integer maxWordLength = oldDataMapList.stream()
               .map(e -> e.getOrDefault(orderField, "") + "")
               .map(e -> e.length())
               .max(Comparator.comparing(e -> (Integer)e))
               .orElse(0);
            Map<String, LimsTemplateDataMap> perDynamicCellValueVerticalDataMap = oldDataMapList.stream()
               .sorted(Comparator.comparing(e -> String.format("%" + (maxWordLength + 1) + "s", e.getOrDefault(orderField, ""))))
               .collect(Collectors.toMap(e -> e.getOrDefault(dynamicField, "") + "", e -> (LimsTemplateDataMap)e, (v1, v2) -> v1));
            dynamicCellValueVerticalDataMap.putAll(perDynamicCellValueVerticalDataMap);
            Deque<String> perDynamicCellQueue = oldDataMapList.stream()
               .sorted(Comparator.comparing(e -> String.format("%" + (maxWordLength + 1) + "s", e.getOrDefault(orderField, ""))))
               .map(e -> e.getOrDefault(dynamicField, "") + "")
               .distinct()
               .collect(Collectors.toCollection(() -> new ArrayDeque<>()));
            dynamicCellMap.put(dynamicCell, perDynamicCellQueue);
            Map<String, List<LimsTemplateDataMap>> oldDataGroup = newDataMapList.stream().collect(Collectors.groupingBy(e -> {
               StringBuilder sb = new StringBuilder();

               for (int ix = 3; ix < dynamicSplits.length; ix++) {
                  sb.append(e.getOrDefault(dynamicSplits[ix].trim(), ""));
               }

               return sb.toString();
            }));
            newDataMapList = oldDataGroup.values().stream().map(vList -> {
               LimsTemplateDataMap res = vList.get(0);

               for (LimsTemplateDataMap v : vList) {
                  String fieldKey = this.replaceSpecialStr(v.getOrDefault(dynamicField, "") + "") + resultField;
                  res.put(fieldKey, v.getOrDefault(resultField, "/"));
                  allColumnKeySet.add(fieldKey);
               }

               return res;
            }).collect(Collectors.toCollection(LimsTemplateDataList::new));
         }
      }

      for (LimsTemplateDataMap newDataMap : newDataMapList) {
         for (String columnKey : allColumnKeySet) {
            newDataMap.computeIfAbsent(columnKey, kx -> "/");
         }
      }

      Map<LimsTemplateDataMap, Integer> oldDataMapIndexMap = new HashMap<>();

      for (int ix = 0; ix < oldDataMapList.size(); ix++) {
         oldDataMapIndexMap.put(oldDataMapList.get(ix), ix);
      }

      newDataMapList.sort(Comparator.comparing(e -> oldDataMapIndexMap.get(e)));
      int totalDynamic = dynamicCellMap.values().stream().map(e -> e.size()).reduce((a, b) -> a + b).orElse(0);
      List<Map<Integer, Pair<String, String>>> titleResultMapList = new ArrayList<>();

      while (totalDynamic > 0) {
         Map<Integer, Pair<String, String>> titleResultMap = new LinkedHashMap<>();

         for (int ix = 0; ix < oldCellStrList.size(); ix++) {
            if (oldCellStrList.get(ix) != null) {
               String copyCellStr = null;
               String copyResultStr = null;
               if (!dynamicCellMap.get(oldCellStrList.get(ix)).isEmpty()) {
                  totalDynamic--;
                  copyCellStr = dynamicCellMap.get(oldCellStrList.get(ix)).pollFirst();
                  copyResultStr = String.format("[~%s%s]", this.replaceSpecialStr(copyCellStr), oldCellStrList.get(ix).split("\\$")[0]);
               }

               titleResultMap.put(ix, Pair.of(copyCellStr, copyResultStr));
            }
         }

         titleResultMapList.add(titleResultMap);
      }

      logger.info("动态列解析：" + titleResultMapList);
      List<XWPFTable> insertTableList = new ArrayList<>();
      insertTableList.add(table);
      String defaultDynamicCellInputStr = "/";
      XWPFTable curTable = table;
      String tableTitle = renderData.getTableTitle();
      XWPFParagraph paragraphBeforeTable = null;
      List<String> tableTitleKeyList = new ArrayList<>();
      if (!StringUtils.isBlank(tableTitle)) {
         tableTitleKeyList = ArrayUtils.asList(StringUtils.split(tableTitle, "$"));
         if (tableTitleKeyList.size() > 1) {
            paragraphBeforeTable = findPrevParagraphWithKeyword(table, tableTitleKeyList.get(0));
         }
      }

      for (int ixx = titleResultMapList.size() - 1; ixx >= 0; ixx--) {
         XWPFTableRow curTagRow = curTable.getRow(tagRowIndex);
         XWPFTableRow curFisrtDataRow = curTable.getRow(firstDataRowIndex);
         Map<Integer, Pair<String, String>> curTitleResultMap = titleResultMapList.get(ixx);
         CTRPr tagRPr = null;
         CTRPr resultRPr = null;
         Integer firstKey = curTitleResultMap.keySet().iterator().next();

         for (Entry<Integer, Pair<String, String>> titleResultMapEntry : curTitleResultMap.entrySet()) {
            Integer curCellIndex = titleResultMapEntry.getKey();
            Pair<String, String> titleResultPair = titleResultMapEntry.getValue();
            XWPFTableCell curTagCell = this.getTrueCell(curTagRow, curCellIndex);
            XWPFTableCell curFisrtDataTableCell = this.getTrueCell(curFisrtDataRow, curCellIndex);
            if (ObjectUtils.equals(curCellIndex, firstKey)) {
               if (curTagCell != null && !curTagCell.getParagraphs().isEmpty() && !curTagCell.getParagraphs().get(0).getRuns().isEmpty()) {
                  XWPFRun tagRun = curTagCell.getParagraphs().get(0).getRuns().get(0);
                  if (tagRun.getCTR().getRPr() != null) {
                     tagRPr = (CTRPr)tagRun.getCTR().getRPr().copy();
                  }
               }

               if (curFisrtDataTableCell != null
                  && !curFisrtDataTableCell.getParagraphs().isEmpty()
                  && !curFisrtDataTableCell.getParagraphs().get(0).getRuns().isEmpty()) {
                  XWPFRun resultRun = curFisrtDataTableCell.getParagraphs().get(0).getRuns().get(0);
                  if (resultRun.getCTR().getRPr() != null) {
                     resultRPr = (CTRPr)resultRun.getCTR().getRPr().copy();
                  }
               }
            }

            this.setTableCellText(curTagCell, Optional.ofNullable(titleResultPair.getFirst()).orElse(defaultDynamicCellInputStr), tagRPr);
            this.setTableCellText(curFisrtDataTableCell, Optional.ofNullable(titleResultPair.getSecond()).orElse(defaultDynamicCellInputStr), resultRPr);
         }

         if (ixx != 0) {
            XWPFParagraph newParagraph = document.insertNewParagraph(curTable.getCTTbl().newCursor());
            if (renderData.isPage()) {
               XWPFRun newRun = newParagraph.createRun();
               newRun.addBreak(BreakType.PAGE);
            }

            if (paragraphBeforeTable != null && tableTitleKeyList.size() > 1) {
               WordExportUtil.copyParagraphWithPre(newParagraph, paragraphBeforeTable, tableTitleKeyList.get(1));
            }

            XWPFTable newTable = document.insertNewTbl(newParagraph.getCTP().newCursor());
            this.copyOldTable(curTable, newTable);
            curTable = newTable;
            insertTableList.add(0, newTable);
         }
      }

      renderData.setData(newDataMapList);
      renderData.reComputeMergeRowAndColumn();

      for (int ixx = 0; ixx < insertTableList.size(); ixx++) {
         XWPFTable insertTable = insertTableList.get(ixx);
         this.doRenderOriginal(firstDataRowIndex, insertTable, newDataMapList, template);
         List<MergedRow> mergedRowList = renderData.getMergedRowList();
         if (!CollectionUtils.isEmpty(mergedRowList)) {
            mergedRowList.forEach(
               mergedRow -> LimsNewHackLoopTableRenderPolicy.mergeTableRows(
                  insertTable, mergedRow.getColumnIndex(), mergedRow.getStartRowIndex() + firstDataRowIndex, mergedRow.getEndRowIndex() + firstDataRowIndex
               )
            );
         }

         List<MergedColumn> mergedColumnList = renderData.getMergedColumnList();
         if (!CollectionUtils.isEmpty(mergedColumnList)) {
            mergedColumnList.forEach(
               mergedColumn -> LimsNewHackLoopTableRenderPolicy.mergeTableColumns(
                  insertTable, mergedColumn.getRowIndex() + firstDataRowIndex, mergedColumn.getStartColumnIndex(), mergedColumn.getEndColumnIndex()
               )
            );
         }

         if (!titleResultMapList.isEmpty()) {
            Map<Integer, Pair<String, String>> titleResultMap = titleResultMapList.get(ixx);

            for (Entry<Integer, Pair<String, String>> titleResultMapEntry : titleResultMap.entrySet()) {
               Integer curCellIndex = titleResultMapEntry.getKey();
               Pair<String, String> titleResultPair = titleResultMapEntry.getValue();
               String verticalKey = Optional.ofNullable(titleResultPair.getFirst()).orElse(defaultDynamicCellInputStr);
               LimsTemplateDataMap curVerticalData = dynamicCellValueVerticalDataMap.get(verticalKey);

               for (int j = 0; j < tagRowIndex; j++) {
                  XWPFTableRow curRow = insertTable.getRow(j);
                  XWPFTableCell curCell = this.getTrueCell(curRow, curCellIndex);
                  String curCellText = curCell.getText();
                  if (!StringUtils.isEmpty(curCellText) && curCellText.matches(modelPattern)) {
                     String curVerticalValue = curVerticalData == null
                        ? defaultDynamicCellInputStr
                        : curVerticalData.getOrDefault(curCellText.substring(1, curCellText.length() - 1), defaultDynamicCellInputStr) + "";
                     this.setTableCellText(curCell, curVerticalValue);
                  }
               }
            }

            XWPFTableRow curTarRow = insertTable.getRows().get(tagRowIndex);
            List<Integer> blankCellIndexList = new ArrayList<>();

            for (Entry<Integer, Pair<String, String>> titleResultMapEntry : titleResultMap.entrySet()) {
               Integer curTitleResultIndex = titleResultMapEntry.getKey();
               Pair<String, String> curTitleResultPair = titleResultMapEntry.getValue();
               if (curTitleResultPair.getFirst() == null && maxDynamicCellSize != Integer.MAX_VALUE) {
                  blankCellIndexList.add(curTitleResultIndex);
               }
            }

            int avgVal = (int)ArithUtils.div(widthSum, titleResultMap.size() - blankCellIndexList.size());
            blankCellIndexList.addAll(overCellIndexList);
            blankCellIndexList.sort((a, b) -> b - a);

            for (int jx = 0; jx <= newDataMapList.size(); jx++) {
               XWPFTableRow tableRow = insertTable.getRows().get(tagRowIndex + jx);

               for (Entry<Integer, Pair<String, String>> titleResultMapEntryx : titleResultMap.entrySet()) {
                  Integer curTitleResultIndex = titleResultMapEntryx.getKey();
                  Pair<String, String> curTitleResultPair = titleResultMapEntryx.getValue();
                  XWPFTableCell curDynamicCell = this.getTrueCell(tableRow, curTitleResultIndex);
                  if (curTitleResultPair.getFirst() != null || maxDynamicCellSize == Integer.MAX_VALUE) {
                     CTTblWidth tcW = WordExportUtil.getTcPr(curDynamicCell).getTcW();
                     if (tcW == null) {
                        tcW = WordExportUtil.getTcPr(curDynamicCell).addNewTcW();
                     }

                     tcW.setW(new BigInteger(avgVal + ""));
                  }
               }

               for (Integer blankCellIndex : blankCellIndexList) {
                  int removeCellIndex = this.getTrueCellIndex(tableRow, blankCellIndex);
                  tableRow.removeCell(removeCellIndex);
                  tableRow.getCtRow().removeTc(removeCellIndex);
               }
            }

            for (int jx = 0; jx < insertTable.getRows().size(); jx++) {
               if (jx < tagRowIndex || jx > tagRowIndex + newDataMapList.size()) {
                  XWPFTableRow tableRow = insertTable.getRows().get(jx);
                  List<Integer> curDeleteCellIndexList = new ArrayList<>();
                  int cellCount = 0;

                  for (int k = 0; k < tableRow.getTableCells().size(); k++) {
                     XWPFTableCell tableCell = tableRow.getCell(k);
                     gridSpan = WordExportUtil.getTcPr(tableCell).getGridSpan() == null
                        ? 1
                        : WordExportUtil.getTcPr(tableCell).getGridSpan().getVal().intValue();
                     int newGridSpan = gridSpan;

                     for (int l = 0; l < gridSpan; l++) {
                        if (blankCellIndexList.contains(cellCount)) {
                           newGridSpan--;
                        }

                        cellCount++;
                     }

                     if (tableCell.getCTTc().getTcPr().getGridSpan() != null) {
                        tableCell.getCTTc().getTcPr().getGridSpan().setVal(new BigInteger(newGridSpan + ""));
                     }

                     if (newGridSpan == 0) {
                        curDeleteCellIndexList.add(k);
                     }
                  }

                  for (int k = curDeleteCellIndexList.size() - 1; k >= 0; k--) {
                     tableRow.removeCell(curDeleteCellIndexList.get(k) + 0);
                     tableRow.getCtRow().removeTc(curDeleteCellIndexList.get(k) + 0);
                  }
               }
            }

            CTTblGrid insertTableGridList = insertTable.getCTTbl().getTblGrid();
            if (insertTableGridList != null) {
               int delWidthSum = 0;

               for (int k = insertTableGridList.getGridColList().size() - 1; k >= 0; k--) {
                  delWidthSum += insertTableGridList.getGridColList().get(k).getW().intValue();
                  insertTableGridList.getGridColList().remove(k);
               }

               tableWidth = tableWidth == 0 ? delWidthSum : tableWidth;
            } else {
               insertTableGridList = insertTable.getCTTbl().addNewTblGrid();
            }

            XWPFTableRow curFisrtDataRow = insertTable.getRow(firstDataRowIndex);
            int curTotalCell = 0;

            for (XWPFTableCell curTableCell : curFisrtDataRow.getTableCells()) {
               curTotalCell += curTableCell.getWidth();
            }

            for (XWPFTableCell curTableCell : curFisrtDataRow.getTableCells()) {
               insertTableGridList.addNewGridCol()
                  .setW(new BigInteger((int)ArithUtils.mul(ArithUtils.div(curTableCell.getWidth(), curTotalCell), tableWidth) + ""));
            }
         }

         this.adaInsertTableSplitTitle(insertTable, firstDataRowIndex);
         String mergeSameColumn = renderData.getMergeSameColumn();
         if (!StringUtils.isBlank(mergeSameColumn)) {
            String[] columnArray = StringUtils.split(mergeSameColumn, "$");
            int startColumn = NumberUtils.parseInt(columnArray[0]);
            int endColumn = NumberUtils.parseInt(columnArray[1]);

            for (int jxx = firstDataRowIndex; jxx < insertTable.getRows().size(); jxx++) {
               XWPFTableRow tableRow = insertTable.getRows().get(jxx);
               XWPFTableCell lastCell = tableRow.getCell(startColumn);
               CTVMerge lastVMerge = lastCell.getCTTc().getTcPr().getVMerge();
               String lastCellV = lastCell.getText();
               int lastColumnIndex = 0;
               if (lastVMerge == null) {
                  for (int k = 0; k <= endColumn; k++) {
                     XWPFTableCell tableCell = tableRow.getCell(k);
                     CTVMerge vMerge = tableCell.getCTTc().getTcPr().getVMerge();
                     String cellV = tableCell.getText();
                     if (vMerge != null) {
                        lastCellV = cellV;
                     } else {
                        if (!cellV.equals(lastCellV) || k == endColumn) {
                           int endTep = !cellV.equals(lastCellV) ? k - 1 : k;
                           if (endTep - lastColumnIndex > 0) {
                              LimsNewHackLoopTableRenderPolicy.mergeTableColumnsUseGridSpan(insertTable, jxx, lastColumnIndex, endTep);
                           }

                           lastColumnIndex = k;
                        }

                        lastCellV = cellV;
                     }
                  }
               }
            }
         }
      }

      if (!StringUtils.isEmpty(renderData.getRepeatHeaderStr())) {
         String repeatHeaderStr = renderData.getRepeatHeaderStr();
         String[] repeatHeaderStrSplits = repeatHeaderStr.split("\\$");
         Function<LimsTemplateDataMap, String> repeatHeaderFunction = dataMap -> {
            StringBuilder sb = new StringBuilder();

            for (int ixx = 0; ixx < repeatHeaderStrSplits.length; ixx++) {
               sb.append(dataMap.getOrDefault(repeatHeaderStrSplits[ixx].trim(), ""));
               sb.append("-");
            }

            return sb.toString();
         };
         Deque<int[]> repeatHeaderQueue = new ArrayDeque<>();
         repeatHeaderQueue.add(new int[]{0, 0});
         String preText = repeatHeaderFunction.apply(newDataMapList.get(0));

         for (int ixx = 1; ixx < newDataMapList.size(); ixx++) {
            String curText = repeatHeaderFunction.apply(newDataMapList.get(ixx));
            if (preText.equals(curText)) {
               int[] poll = repeatHeaderQueue.pollLast();
               repeatHeaderQueue.addLast(new int[]{poll[0], ixx});
            } else {
               repeatHeaderQueue.addLast(new int[]{ixx, ixx});
            }

            preText = curText;
         }

         for (int ixx = 0; ixx < insertTableList.size(); ixx++) {
            XWPFTable insertTablex = insertTableList.get(ixx);
            this.addInsertRepeatHeader(insertTablex, tagRowIndex, repeatHeaderQueue);
         }
      }
   }

   private CTRPr extractAndUnifyRunStyle(XWPFRun targetRun, XWPFTableCell targetCell) {
      CTRPr targetRPr = null;
      if (targetRun != null && targetRun.getCTR() != null) {
         targetRPr = targetRun.getCTR().getRPr();
         if (targetRPr != null) {
            targetRPr = (CTRPr)targetRPr.copy();
         }
      }

      if (targetRPr != null && targetCell != null) {
         for (XWPFParagraph paragraph : targetCell.getParagraphs()) {
            if (paragraph != null) {
               for (XWPFRun currentRun : paragraph.getRuns()) {
                  if (currentRun != null && currentRun.getCTR() != null && !currentRun.equals(targetRun)) {
                     currentRun.getCTR().setRPr((CTRPr)targetRPr.copy());
                  }
               }
            }
         }

         return targetRPr;
      } else {
         return targetRPr;
      }
   }

   private void addInsertRepeatHeader(XWPFTable insertTable, int tagRowIndex, Deque<int[]> repeatHeaderQueue) {
      for (int i = 0; i <= tagRowIndex; i++) {
         insertTable.getRow(i).setRepeatHeader(false);
      }

      repeatHeaderQueue.pollFirst();
      int add = 0;

      while (!repeatHeaderQueue.isEmpty()) {
         int[] curPoll = repeatHeaderQueue.pollFirst();

         for (int j = 0; j <= tagRowIndex; j++) {
            XWPFTableRow tmplRow = insertTable.getRow(j);
            int insertRowIndex = curPoll[0] + add + tagRowIndex + 1;
            XWPFTableRow addTitleRow = new XWPFTableRow((CTRow)tmplRow.getCtRow().copy(), insertTable);
            insertTable.addRow(addTitleRow, insertRowIndex);
            add++;
         }
      }
   }

   private void adaInsertTableSplitTitle(XWPFTable insertTable, int startRowIndex) {
      XWPFTableRow splitTitleRow = null;
      String splitTitleText = null;
      int splitTitleIndex = -1;
      Pattern contentPattern = Pattern.compile("\\[(?<content>\\!?\\d+)\\]");

      for (int i = startRowIndex + 1; i < insertTable.getRows().size(); i++) {
         XWPFTableRow curTableRow = insertTable.getRows().get(i);
         XWPFTableCell curFisrtTableCell = curTableRow.getTableCells().get(0);
         String fisrtCellText = curFisrtTableCell.getText();
         if (!StringUtils.isEmpty(fisrtCellText)) {
            Matcher contecntMatcher = contentPattern.matcher(fisrtCellText);
            if (contecntMatcher.find()) {
               splitTitleRow = curTableRow;
               splitTitleText = contecntMatcher.group("content");
               splitTitleIndex = i;
               this.setTableCellText(curFisrtTableCell, fisrtCellText.replace(contecntMatcher.group(0), ""));
            }
         }
      }

      if (splitTitleRow != null) {
         XWPFTableRow preTemplateRow = insertTable.getRows().get(splitTitleIndex - 1);

         for (int ix = 0; ix < preTemplateRow.getTableCells().size(); ix++) {
            CTTcBorders preTemplateBorders = preTemplateRow.getTableCells().get(ix).getCTTc().getTcPr().getTcBorders();
            preTemplateBorders = preTemplateBorders == null ? preTemplateRow.getTableCells().get(ix).getCTTc().getTcPr().addNewTcBorders() : preTemplateBorders;
            preTemplateBorders.set(
               splitTitleRow.getTableCells().get(ix).getCTTc().getTcPr().getTcBorders() == null
                  ? splitTitleRow.getTableCells().get(ix).getCTTc().getTcPr().addNewTcBorders().copy()
                  : splitTitleRow.getTableCells().get(ix).getCTTc().getTcPr().getTcBorders().copy()
            );
         }

         if ('!' == splitTitleText.charAt(0)) {
            int rowCount = Integer.parseInt(splitTitleText.substring(1, splitTitleText.length()));
            XWPFTableRow newTitleRow = new XWPFTableRow((CTRow)splitTitleRow.getCtRow().copy(), insertTable);
            insertTable.removeRow(splitTitleIndex);
            insertTable.addRow(newTitleRow, startRowIndex + rowCount);

            for (int ix = 0; ix < insertTable.getRows().size(); ix++) {
               CTTrPr trPr = insertTable.getCTTbl().getTrArray(ix).getTrPr() == null
                  ? insertTable.getCTTbl().getTrArray(ix).addNewTrPr()
                  : insertTable.getCTTbl().getTrArray(ix).getTrPr();
               if (ix <= startRowIndex + rowCount) {
                  if (trPr.getTblHeaderList().isEmpty()) {
                     trPr.addNewTblHeader();
                  } else {
                     trPr.getTblHeaderList().get(0).setVal(STOnOff.TRUE);
                  }
               } else if (!trPr.getTblHeaderList().isEmpty()) {
                  trPr.removeTblHeader(0);
               }
            }
         } else {
            int rowCount = Integer.parseInt(splitTitleText);
            XWPFTableRow preTableRow = insertTable.getRows().get(startRowIndex + rowCount - 1);
            List<XWPFTableCell> preTableCells = preTableRow.getTableCells();
            XWPFTableRow nextTableRow = insertTable.getRows().get(startRowIndex + rowCount);
            List<XWPFTableCell> nextTableCells = nextTableRow.getTableCells();

            for (int ixx = 0; ixx < preTableCells.size(); ixx++) {
               CTTcBorders templateCellBorders = splitTitleRow.getTableCells().get(ixx).getCTTc().getTcPr().getTcBorders() == null
                  ? splitTitleRow.getTableCells().get(ixx).getCTTc().getTcPr().addNewTcBorders()
                  : splitTitleRow.getTableCells().get(ixx).getCTTc().getTcPr().getTcBorders();
               CTBorder tmplateBottom = templateCellBorders.getBottom() == null ? templateCellBorders.addNewBottom() : templateCellBorders.getBottom();
               CTTcBorders preCellBorders = preTableCells.get(ixx).getCTTc().getTcPr().getTcBorders() == null
                  ? preTableCells.get(ixx).getCTTc().getTcPr().addNewTcBorders()
                  : preTableCells.get(ixx).getCTTc().getTcPr().getTcBorders();
               CTBorder preBottom = preCellBorders.getBottom() == null ? preCellBorders.addNewBottom() : preCellBorders.getBottom();
               preBottom.set(tmplateBottom.copy());
               CTTcBorders nextCellBorders = nextTableCells.get(ixx).getCTTc().getTcPr().getTcBorders() == null
                  ? nextTableCells.get(ixx).getCTTc().getTcPr().addNewTcBorders()
                  : nextTableCells.get(ixx).getCTTc().getTcPr().getTcBorders();
               CTBorder nextTop = nextCellBorders.getTop() == null ? nextCellBorders.addNewTop() : nextCellBorders.getTop();
               nextTop.set(tmplateBottom.copy());
            }

            insertTable.removeRow(splitTitleIndex);

            for (int ixx = 0; ixx < insertTable.getRows().size(); ixx++) {
               CTTrPr trPr = insertTable.getCTTbl().getTrArray(ixx).getTrPr() == null
                  ? insertTable.getCTTbl().getTrArray(ixx).addNewTrPr()
                  : insertTable.getCTTbl().getTrArray(ixx).getTrPr();
               if (ixx < startRowIndex + rowCount) {
                  if (trPr.getTblHeaderList().isEmpty()) {
                     trPr.addNewTblHeader();
                  } else {
                     trPr.getTblHeaderList().get(0).setVal(STOnOff.TRUE);
                  }
               } else if (!trPr.getTblHeaderList().isEmpty()) {
                  trPr.removeTblHeader(0);
               }
            }
         }
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

   private String replaceSpecialStr(String s) {
      return StringUtils.isEmpty(s) ? s : s.replaceAll("[\r\n]", "");
   }

   private void setTableCellText(XWPFTableCell cell, String cellText, CTRPr baseRPr) {
      if (cell != null) {
         String safeCellText = cellText == null ? "" : cellText;
         CTRPr originalRPr = null;
         List<XWPFParagraph> paragraphs = cell.getParagraphs();
         if (paragraphs != null && !paragraphs.isEmpty()) {
            XWPFParagraph firstPara = paragraphs.get(0);
            if (firstPara != null && !firstPara.getRuns().isEmpty()) {
               XWPFRun originalRun = firstPara.getRuns().get(0);
               if (originalRun != null && originalRun.getCTR() != null) {
                  originalRPr = originalRun.getCTR().getRPr();
                  if (originalRPr != null) {
                     originalRPr = (CTRPr)originalRPr.copy();
                  }
               }
            }
         }

         if (paragraphs != null) {
            for (int i = paragraphs.size() - 1; i >= 1; i--) {
               cell.removeParagraph(i);
            }
         }

         XWPFParagraph newParagraph = paragraphs != null && !paragraphs.isEmpty() ? paragraphs.get(0) : cell.addParagraph();
         List<XWPFRun> runs = newParagraph.getRuns();
         if (runs != null) {
            for (int i = runs.size() - 1; i >= 0; i--) {
               newParagraph.removeRun(i);
            }
         }

         XWPFRun newRun = newParagraph.createRun();
         CTRPr finalRPr = baseRPr != null ? (CTRPr)baseRPr.copy() : originalRPr;
         if (finalRPr != null) {
            newRun.getCTR().setRPr(finalRPr);
         }

         String[] cellTextSplits = safeCellText.split("[\r\n]");

         for (int i = 0; i < cellTextSplits.length; i++) {
            String cellTextSplit = cellTextSplits[i];
            newRun.setText(cellTextSplit);
            if (i + 1 != cellTextSplits.length) {
               newRun.addBreak();
            }
         }
      }
   }

   private void setTableCellText(XWPFTableCell cell, String cellText) {
      if (cell != null) {
         String safeCellText = cellText == null ? "" : cellText;
         CTRPr originalRPr = null;
         List<XWPFParagraph> paragraphs = cell.getParagraphs();
         if (paragraphs != null && !paragraphs.isEmpty()) {
            XWPFParagraph firstPara = paragraphs.get(0);
            if (firstPara != null && !firstPara.getRuns().isEmpty()) {
               XWPFRun originalRun = firstPara.getRuns().get(0);
               if (originalRun != null && originalRun.getCTR() != null) {
                  originalRPr = originalRun.getCTR().getRPr();
                  if (originalRPr != null) {
                     originalRPr = (CTRPr)originalRPr.copy();
                  }
               }
            }
         }

         if (paragraphs != null) {
            for (int i = paragraphs.size() - 1; i >= 1; i--) {
               cell.removeParagraph(i);
            }
         }

         XWPFParagraph newParagraph = paragraphs != null && !paragraphs.isEmpty() ? paragraphs.get(0) : cell.addParagraph();
         List<XWPFRun> runs = newParagraph.getRuns();
         if (runs != null) {
            for (int i = runs.size() - 1; i >= 0; i--) {
               newParagraph.removeRun(i);
            }
         }

         XWPFRun newRun = newParagraph.createRun();
         if (originalRPr != null) {
            newRun.getCTR().setRPr(originalRPr);
         }

         String[] cellTextSplits = safeCellText.split("[\r\n]");

         for (int i = 0; i < cellTextSplits.length; i++) {
            String cellTextSplit = cellTextSplits[i];
            newRun.setText(cellTextSplit);
            if (i + 1 != cellTextSplits.length) {
               newRun.addBreak();
            }
         }
      }
   }

   private void copyOldTable(XWPFTable oldTable, XWPFTable newTable) {
      int oldSize = newTable.getRows().size();

      for (int i = 0; i < oldSize; i++) {
         newTable.removeRow(0);
      }

      newTable.getCTTbl().setTblPr((CTTblPr)Optional.ofNullable(oldTable.getCTTbl().getTblPr()).orElse(oldTable.getCTTbl().addNewTblPr()).copy());
      newTable.getCTTbl().setTblGrid((CTTblGrid)Optional.ofNullable(oldTable.getCTTbl().getTblGrid()).orElse(oldTable.getCTTbl().addNewTblGrid()).copy());

      for (XWPFTableRow oldTableRow : oldTable.getRows()) {
         XWPFTableRow newRow = newTable.createRow();
         newRow.getCtRow().setTrPr((CTTrPr)Optional.ofNullable(oldTableRow.getCtRow().getTrPr()).orElse(oldTableRow.getCtRow().addNewTrPr()).copy());

         for (int i = 0; i < oldTableRow.getTableCells().size(); i++) {
            XWPFTableCell oldTableCell = oldTableRow.getTableCells().get(i);
            XWPFTableCell newTableCell = newRow.getTableCells().size() > i ? newRow.getTableCells().get(i) : newRow.addNewTableCell();
            newTableCell.getCTTc().setTcPr((CTTcPr)Optional.ofNullable(oldTableCell.getCTTc().getTcPr()).orElse(oldTableCell.getCTTc().addNewTcPr()).copy());

            for (int j = 0; j < oldTableCell.getParagraphs().size(); j++) {
               XWPFParagraph oldParagraphs = oldTableCell.getParagraphs().get(j);
               XWPFParagraph newParagraph = newTableCell.getParagraphs().size() > j ? newTableCell.getParagraphs().get(j) : newTableCell.addParagraph();
               newParagraph.getCTP().setPPr((CTPPr)Optional.ofNullable(oldParagraphs.getCTP().getPPr()).orElse(oldParagraphs.getCTP().addNewPPr()).copy());

               for (int k = 0; k < oldParagraphs.getRuns().size(); k++) {
                  XWPFRun oldRun = oldParagraphs.getRuns().get(k);
                  XWPFRun newRun = newParagraph.createRun();
                  newRun.getCTR().setRPr((CTRPr)Optional.ofNullable(oldRun.getCTR().getRPr()).orElse(oldRun.getCTR().addNewRPr()).copy());
                  newRun.setText(oldRun.getText(0));
               }
            }
         }

         int oldCellSize = oldTableRow.getTableCells().size();
         int newCellSize = newRow.getTableCells().size();

         for (int i = 0; i < newCellSize - oldCellSize; i++) {
            newRow.removeCell(oldCellSize);
            newRow.getCtRow().removeTc(oldCellSize);
         }
      }
   }

   public static XWPFParagraph getParagraphBeforeTable(XWPFTable curTable) {
      IBody body = curTable.getBody();
      if (!(body instanceof XWPFDocument)) {
         return null;
      } else {
         XWPFDocument doc = (XWPFDocument)body;
         List<IBodyElement> elements = doc.getBodyElements();
         int tableIndex = -1;

         for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i) instanceof XWPFTable && elements.get(i) == curTable) {
               tableIndex = i;
               break;
            }
         }

         return tableIndex > 0 && elements.get(tableIndex - 1) instanceof XWPFParagraph ? (XWPFParagraph)elements.get(tableIndex - 1) : null;
      }
   }

   public static XWPFParagraph findPrevParagraphWithKeyword(XWPFTable curTable, String keyword) {
      if (curTable != null && keyword != null) {
         XWPFDocument doc = (XWPFDocument)curTable.getBody();
         List<IBodyElement> list = doc.getBodyElements();
         int idx = list.indexOf(curTable);
         String key = keyword.toLowerCase();

         while (--idx >= 0) {
            if (list.get(idx) instanceof XWPFParagraph) {
               XWPFParagraph p = (XWPFParagraph)list.get(idx);
               if (p.getText().toLowerCase().contains(key)) {
                  return p;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private class LimsSplitTableDATA {
      List<LimsTemplateDataMap> headList;
      LimsTemplateDataList<LimsTemplateDataMap> dataList;

      private LimsSplitTableDATA() {
      }
   }
}
