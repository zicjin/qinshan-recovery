package com.sunwayworld.cloud.module.equipt.support;

import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqAreaDTO;
import com.sunwayworld.cloud.module.equipt.support.constant.PdfAcqParseType;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.TextElement;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.ExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class CorePdfTableResolve {
   private PDDocument document;
   private List<CorePdfAcqAreaDTO> pdfAreaDTOList;
   private List<CorePdfAcqAreaDTO> singleColPdfAreaDtoList;
   private CorePdfAcqAreaDTO pdfAreaDTO;
   private boolean underlay;
   private ExtractionAlgorithm tableExtractor;
   private ObjectExtractor extractor;
   private List<String> tableTitleList;
   private String split;
   private Map<Integer, Table> tableMap;
   private Double pageMinY;
   private Double pageMaxY;
   private Integer leastColNum = 2;
   private List<Float> columnRight;

   public CorePdfTableResolve(PDDocument document, CorePdfAcqAreaDTO pdfAreaDTO, List<CorePdfAcqAreaDTO> corePdfAreaDTOList, ObjectExtractor extractor) {
      this.document = document;
      this.pdfAreaDTO = pdfAreaDTO;
      this.pdfAreaDTOList = corePdfAreaDTOList;
      this.extractor = extractor;
      this.underlay = pdfAreaDTO.getUnderlay() != null && !PdfAcqParseType.SAMPLE_NO.name().equals(pdfAreaDTO.getParseType())
         ? pdfAreaDTO.getUnderlay()
         : false;
      this.init();
   }

   private void init() {
      this.singleColPdfAreaDtoList = new ArrayList<>();
      this.tableMap = new LinkedHashMap<>();
      this.columnRight = new ArrayList<>();
      this.split = "space".equals(this.pdfAreaDTO.getSplit()) ? "[\\s]+" : "@#$%@#$";
      this.tableTitleList = new ArrayList<>();
      this.pdfAreaDTO.setExtractMethod("original");
      if (PdfAcqParseType.RESULT_TABLE.name().equals(this.pdfAreaDTO.getParseType())) {
         this.singleColPdfAreaDtoList = this.pdfAreaDTOList
            .stream()
            .filter(f -> PdfAcqParseType.RESULT_COLUMN.name().equals(f.getParseType()) && this.pdfAreaDTO.getGroup().equals(f.getGroup()))
            .sorted(new Comparator<CorePdfAcqAreaDTO>() {
               public int compare(CorePdfAcqAreaDTO CorePdfAreaPre, CorePdfAcqAreaDTO CorePdfAreaNext) {
                  return Double.compare(CorePdfAreaPre.getTopXAxis(), CorePdfAreaNext.getTopXAxis());
               }
            })
            .collect(Collectors.toList());
      }

      this.singleColPdfAreaDtoList.forEach(e -> e.setExtractMethod("original"));
      Page page = this.extractor.extract(this.pdfAreaDTO.getPageNumber());
      SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
      BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();
      boolean useSpreadsheetExtractionMethod;
      if (!this.pdfAreaDTO.getExtractMethod().equals("spreadsheet") && !this.pdfAreaDTO.getExtractMethod().equals("original")) {
         useSpreadsheetExtractionMethod = sea.isTabular(page);
      } else {
         useSpreadsheetExtractionMethod = this.pdfAreaDTO.getExtractMethod().equals("spreadsheet");
      }

      this.tableExtractor = (ExtractionAlgorithm)(useSpreadsheetExtractionMethod ? sea : bea);
      CorePdfAcqAreaDTO headerPdfArea = this.pdfAreaDTOList
         .stream()
         .filter(f -> PdfAcqParseType.PAGE_HEADER.name().equals(f.getParseType()))
         .findFirst()
         .orElse(null);
      this.pageMinY = headerPdfArea == null ? page.getMinY() : headerPdfArea.getBottomYAxis();
      CorePdfAcqAreaDTO footerPdfArea = this.pdfAreaDTOList
         .stream()
         .filter(f -> PdfAcqParseType.PAGE_FOOTER.name().equals(f.getParseType()))
         .findFirst()
         .orElse(null);
      this.pageMaxY = footerPdfArea == null ? page.getMaxY() : footerPdfArea.getTopYAxis();
      this.getHoleTableInfo();
   }

   private void getHoleTableInfo() {
      Page page = this.extractor.extract(this.pdfAreaDTO.getPageNumber());
      double bottomYAxis = this.underlay ? this.pageMaxY : this.pdfAreaDTO.getBottomYAxis();
      Page area = page.getArea(
         NumberUtils.parseFloat(this.pdfAreaDTO.getTopYAxis()),
         NumberUtils.parseFloat(this.pdfAreaDTO.getTopXAxis()),
         NumberUtils.parseFloat(bottomYAxis),
         NumberUtils.parseFloat(this.pdfAreaDTO.getBottomXAxis())
      );

      for (Table table : this.tableExtractor.extract(area)) {
         this.parseMasterTableToList(table, this.pdfAreaDTO.getPageNumber(), true);
      }

      if (this.underlay) {
         for (int m = page.getPageNumber() + 1; m <= this.document.getNumberOfPages(); m++) {
            Page nextPage = this.extractor.extract(m);
            Page nextArea = nextPage.getArea(
               NumberUtils.parseFloat(this.pageMinY),
               NumberUtils.parseFloat(this.pdfAreaDTO.getTopXAxis()),
               NumberUtils.parseFloat(this.pageMaxY),
               NumberUtils.parseFloat(this.pdfAreaDTO.getBottomXAxis())
            );

            for (Table nextTable : this.tableExtractor.extract(nextArea)) {
               int rowCount = this.parseMasterTableToList(nextTable, m, false);
               if (rowCount < nextTable.getRowCount()) {
                  m = this.document.getNumberOfPages();
                  break;
               }
            }
         }
      }
   }

   public List<List<String>> getParseResult() {
      return this.singleColPdfAreaDtoList.size() > 0 ? this.tabulaParseResult1() : this.customParseResult();
   }

   public List<List<String>> tabulaParseResult1() {
      List<CorePdfTableResolve.VerticalArea> verticalAreaList = new ArrayList<>();
      this.singleColPdfAreaDtoList
         .forEach(
            e -> {
               verticalAreaList.add(
                  new CorePdfTableResolve.VerticalArea(
                     false,
                     verticalAreaList.size() == 0 ? this.pdfAreaDTO.getTopXAxis() : verticalAreaList.get(verticalAreaList.size() - 1).getBottomX(),
                     e.getTopXAxis()
                  )
               );
               verticalAreaList.add(new CorePdfTableResolve.VerticalArea(true, e.getTopXAxis(), e.getBottomXAxis()));
            }
         );
      verticalAreaList.add(
         new CorePdfTableResolve.VerticalArea(
            false,
            verticalAreaList.size() == 0 ? this.pdfAreaDTO.getTopXAxis() : verticalAreaList.get(verticalAreaList.size() - 1).getBottomX(),
            this.pdfAreaDTO.getBottomXAxis()
         )
      );
      this.tableExtractor = new BasicExtractionAlgorithm();
      List<List<String>> rowDataList = new ArrayList<>();
      this.tableMap
         .forEach(
            (pageNum, table) -> {
               Page page = this.extractor.extract(pageNum);
               table.getRows()
                  .forEach(
                     r -> {
                        List<String> valueList = new ArrayList<>();

                        for (int i = 0; i < verticalAreaList.size(); i++) {
                           boolean single = verticalAreaList.get(i).getSingle();
                           double topX = verticalAreaList.get(i).getTopX();
                           double bottomX = verticalAreaList.get(i).getBottomX();
                           double topY = getMinY((List<RectangularTextContainer>)r) - 0.01;
                           topY = topY < 0.0 ? 0.0 : topY;
                           double bottomY = getMaxY((List<RectangularTextContainer>)r) + getMaxHeight((List<RectangularTextContainer>)r) + 0.02;
                           if (topX != bottomX) {
                              Page area = page.getArea(
                                 NumberUtils.parseFloat(topY), NumberUtils.parseFloat(topX), NumberUtils.parseFloat(bottomY), NumberUtils.parseFloat(bottomX)
                              );
                              List<? extends Table> tableList = this.tableExtractor.extract(area);
                              tableList.forEach(t -> {
                                 if (t.getRowCount() == 0) {
                                    valueList.add("");
                                 } else {
                                    t.getRows().forEach(f -> {
                                       List<String> cellList = f.stream()
                                          .map(g -> g.getText().split(this.split))
                                          .flatMap(Arrays::stream)
                                          .collect(Collectors.toList());
                                       if (single) {
                                          valueList.add(String.join("", cellList));
                                       } else {
                                          valueList.addAll(cellList);
                                       }
                                    });
                                 }
                              });
                           }
                        }

                        rowDataList.add(valueList);
                     }
                  );
            }
         );
      List<Integer> emptyColumn = new ArrayList<>();
      if (rowDataList.size() > 0) {
         for (int i = 0; i < rowDataList.get(0).size(); i++) {
            boolean empty = true;

            for (int j = 0; j < rowDataList.size(); j++) {
               if (!StringUtils.isBlank(rowDataList.get(j).get(i))) {
                  empty = false;
               }
            }

            if (empty) {
               emptyColumn.add(i);
            }
         }
      }

      for (int i = emptyColumn.size() - 1; i > -1; i--) {
         for (List<String> rowData : rowDataList) {
            rowData.remove(emptyColumn.get(i).intValue());
         }
      }

      return rowDataList.stream().filter(f -> !this.isTitleRow(String.join("", f))).collect(Collectors.toList());
   }

   private int parseMasterTableToList(Table table, int pageNum, boolean firstTable) {
      ArrayList<Integer> deleteRow = new ArrayList<>();
      List<List<String>> dataList = new ArrayList<>();
      table.getRows()
         .forEach(
            f -> dataList.add(
               f.stream()
                  .filter(k -> !StringUtils.isBlank(k.getText()))
                  .map(g -> g.getText().split(this.split))
                  .flatMap(Arrays::stream)
                  .collect(Collectors.toList())
            )
         );
      if (dataList.size() == 0) {
         return 0;
      } else {
         Long titleNum = this.pdfAreaDTO.getTableHeaderRowNum();
         if (PdfAcqParseType.RESULT_TABLE.name().equals(this.pdfAreaDTO.getParseType()) && this.tableTitleList.size() == 0) {
            for (int i = 0; i < titleNum; i++) {
               this.tableTitleList.add(String.join("", dataList.get(i)));
            }
         }

         List<Integer> columnQtyList = dataList.stream().map(m -> m.size()).collect(Collectors.toList());
         if (firstTable) {
            this.leastColNum = columnQtyList.subList(NumberUtils.parseInt(titleNum), columnQtyList.size() - 1).stream().sorted().findFirst().orElse(2);
         }

         int tableRowCount = dataList.size();

         for (int i = 0; i < columnQtyList.size() - 1; i++) {
            if (columnQtyList.get(i) == 0) {
               deleteRow.add(i);
            }

            if (!this.isTitleRow(String.join("", dataList.get(i))) && columnQtyList.get(i) < this.leastColNum) {
               tableRowCount = i;
               break;
            }
         }

         while (table.getRows().size() > tableRowCount) {
            table.getRows().remove(table.getRows().size() - 1);
         }

         if (table.getRows().size() > 0) {
            for (int i = deleteRow.size() - 1; i > -1; i--) {
               table.getRows().remove(deleteRow.get(i).intValue());
            }

            if (!this.tableMap.containsKey(pageNum)) {
               this.tableMap.put(pageNum, table);
            } else {
               this.tableMap.get(pageNum).getRows().addAll(table.getRows().subList(0, tableRowCount));
            }
         }

         return tableRowCount;
      }
   }

   private boolean isTitleRow(String rowData) {
      return this.tableTitleList.contains(StringUtils.trimAllWhitespace(rowData));
   }

   private static Double getMinY(List<RectangularTextContainer> textList) {
      Double minY = null;

      for (RectangularTextContainer text : textList) {
         if (!(text.getY() <= 0.0)) {
            if (minY == null) {
               minY = text.getY();
            } else if (minY > text.getY()) {
               minY = text.getY();
            }
         }
      }

      return minY;
   }

   private static Double getMaxY(List<RectangularTextContainer> textList) {
      Double maxY = null;

      for (RectangularTextContainer text : textList) {
         if (!(text.getY() <= 0.0)) {
            if (maxY == null) {
               maxY = text.getY();
            } else if (maxY < text.getY()) {
               maxY = text.getY();
            }
         }
      }

      return maxY;
   }

   private static Double getMaxHeight(List<RectangularTextContainer> textList) {
      Double maxHeight = null;

      for (RectangularTextContainer text : textList) {
         if (!(text.getHeight() <= 0.0)) {
            if (maxHeight == null) {
               maxHeight = text.getHeight();
            } else if (maxHeight < text.getHeight()) {
               maxHeight = text.getHeight();
            }
         }
      }

      return maxHeight;
   }

   public List<List<String>> customParseResult() {
      List<List<String>> dataList = new ArrayList<>();
      this.tableMap
         .forEach(
            (k, v) -> {
               Page page = this.extractor.extract(k);
               double topY = v.getRows().get(0).stream().filter(f -> f.getY() > 0.0).sorted(new Comparator<RectangularTextContainer>() {
                  public int compare(RectangularTextContainer o1, RectangularTextContainer o2) {
                     return Double.compare(o1.getY(), o2.getY());
                  }
               }).findFirst().get().getY() - 0.01;
               double bottomY = v.getRows().get(v.getRows().size() - 1).stream().sorted(new Comparator<RectangularTextContainer>() {
                  public int compare(RectangularTextContainer o1, RectangularTextContainer o2) {
                     return Double.compare(o2.getBottom(), o1.getBottom());
                  }
               }).findFirst().get().getBottom() + 0.01;
               Page area = page.getArea(
                  NumberUtils.parseFloat(topY),
                  NumberUtils.parseFloat(this.pdfAreaDTO.getTopXAxis()),
                  NumberUtils.parseFloat(bottomY),
                  NumberUtils.parseFloat(this.pdfAreaDTO.getBottomXAxis())
               );
               List<TextElement> textList = area.getText();
               dataList.addAll(this.textToTableData(textList));
            }
         );
      return dataList.stream().filter(f -> !this.isTitleRow(String.join("", f))).collect(Collectors.toList());
   }

   private List<List<String>> textToTableData(List<TextElement> textList) {
      double minWidth = textList.stream().sorted(new Comparator<TextElement>() {
         public int compare(TextElement o1, TextElement o2) {
            return Double.compare(o1.getWidth(), o2.getWidth());
         }
      }).findFirst().get().getWidth();
      double maxRight = textList.stream().sorted(new Comparator<TextElement>() {
         public int compare(TextElement o1, TextElement o2) {
            return Double.compare(o2.getRight(), o1.getRight());
         }
      }).findFirst().get().getRight();
      TextElement preText = textList.get(0);
      List<List<TextElement>> rowList = new ArrayList<>();
      List<TextElement> rows = new ArrayList<>();

      for (int i = 0; i < textList.size(); i++) {
         TextElement text = textList.get(i);
         if (text.getY() - preText.getY() < preText.getHeight() / 2.0) {
            rows.add(text);
         } else {
            rowList.add(rows);
            rows = new ArrayList<>();
            rows.add(text);
         }

         preText = text;
      }

      rowList.add(rows);
      if (this.columnRight.isEmpty()) {
         CorePdfTableResolve.PosValue posValue = new CorePdfTableResolve.PosValue(0.0F, 0.0F);
         Integer cycles = 500;

         while (posValue.getRight().floatValue() < maxRight && cycles > 0) {
            cycles = cycles - 1;
            List<CorePdfTableResolve.PosValue> tempList = rowList.stream().map(m -> {
               Iterator<TextElement> it = m.iterator();
               TextElement pre = null;
               Double left = -1.0;

               while (it.hasNext()) {
                  TextElement cur = it.next();
                  if (pre == null) {
                     pre = cur;
                  } else if (posValue.getRight() > 0.0F && pre.getRight() <= posValue.getRight()) {
                     pre = cur;
                  } else {
                     if (left < 0.0) {
                        left = pre.getX();
                     }

                     if (cur.getX() - pre.getRight() > minWidth / 2.0) {
                        return new CorePdfTableResolve.PosValue(NumberUtils.parseFloat(left), pre.getRight());
                     }

                     pre = cur;
                  }
               }

               return new CorePdfTableResolve.PosValue(NumberUtils.parseFloat(left), m.get(m.size() - 1).getRight());
            }).collect(Collectors.toList());

            for (int i = tempList.size() - 1; i > -1; i--) {
               for (int j = tempList.size() - 1; j > -1; j--) {
                  if (i != j && tempList.get(i).getLeft() > tempList.get(j).getRight()) {
                     tempList.remove(i);
                     break;
                  }
               }
            }

            tempList.sort(new Comparator<CorePdfTableResolve.PosValue>() {
               public int compare(CorePdfTableResolve.PosValue o1, CorePdfTableResolve.PosValue o2) {
                  return Float.compare(o2.getRight(), o1.getRight());
               }
            });
            Float right = tempList.get(0).getRight();
            if (!this.columnRight.isEmpty() && right <= this.columnRight.get(this.columnRight.size() - 1)) {
               right = (float)maxRight;
            }

            this.columnRight.add(right);
            posValue.setRight(right);
         }
      }

      return rowList.stream().map(m -> {
         List<String> columnValueList = new ArrayList<>();
         CorePdfTableResolve.PosValue left = new CorePdfTableResolve.PosValue(0.0F, 0.0F);
         this.columnRight.forEach(e -> {
            List<TextElement> columnTextList = m.stream().filter(f -> f.getX() > left.getLeft().floatValue() && f.getRight() <= e).collect(Collectors.toList());
            List<String> texts = columnTextList.stream().map(t -> t.getText()).collect(Collectors.toList());
            columnValueList.add(String.join("", texts));
            left.setLeft(e);
         });
         return columnValueList;
      }).collect(Collectors.toList());
   }

   class PosValue {
      private Float left;
      private Float right;

      public PosValue(Float left, Float right) {
         this.left = left;
         this.right = right;
      }

      public Float getRight() {
         return this.right;
      }

      public void setRight(Float right) {
         this.right = right;
      }

      public Float getLeft() {
         return this.left;
      }

      public void setLeft(Float left) {
         this.left = left;
      }
   }

   class VerticalArea {
      private Boolean single;
      private Double topX;
      private Double bottomX;

      public VerticalArea(Boolean single, Double topX, Double bottomX) {
         this.single = single;
         this.topX = topX;
         this.bottomX = bottomX;
      }

      public Boolean getSingle() {
         return this.single;
      }

      public void setSingle(Boolean single) {
         this.single = single;
      }

      public Double getTopX() {
         return this.topX;
      }

      public void setTopX(Double topX) {
         this.topX = topX;
      }

      public Double getBottomX() {
         return this.bottomX;
      }

      public void setBottomX(Double bottomX) {
         this.bottomX = bottomX;
      }
   }
}
