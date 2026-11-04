package com.sunwayworld.framework.io.excel.impl;

import com.alibaba.fastjson.annotation.JSONField;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyType;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.io.excel.CellStyleData;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.excel.SheetWriter;
import com.sunwayworld.framework.io.excel.support.PropertyContext;
import com.sunwayworld.framework.io.excel.support.SheetContext;
import com.sunwayworld.framework.io.excel.utils.ExcelUtils;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.support.table.bean.TableRowBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

public class DefaultSheetWriter implements SheetWriter {
   private static final long EXCEL_MAX_LONG = 1000000000000000L;
   private final SheetContext sheetContext;
   private final List<? extends Object> items;
   private final Class<? extends Object> itemClass;

   public DefaultSheetWriter(SheetContext sheetContext, List<? extends Object> items) {
      this.sheetContext = sheetContext;
      this.items = items;
      if (items.isEmpty()) {
         this.itemClass = Object.class;
      } else {
         this.itemClass = (Class<? extends Object>)items.get(0).getClass();
      }
   }

   @Override
   public SheetContext getSheetContext() {
      return this.sheetContext;
   }

   public List<? extends Object> getItems() {
      return this.items;
   }

   @Override
   public Class<? extends Object> getItemClass() {
      return this.itemClass;
   }

   @Override
   public void write(Workbook wb) {
      SheetContext sheetContext = this.getSheetContext();
      Sheet sheet = wb.createSheet(ExcelUtils.getWellFormedSheetName(sheetContext.getSheetName()));
      Row row = null;
      Cell cell = null;
      CellStyle csForTopic = ExcelUtils.getTopicCellStyle(wb);
      CellStyle csForTitle = ExcelUtils.getTitleCellStyle(wb);
      CellStyle csForCenterContent = ExcelUtils.getCenterCellStyle(wb);
      CellStyle csForLeftContent = ExcelUtils.getLeftCellStyle(wb);
      CellStyle csForRightContent = ExcelUtils.getRightCellStyle(wb);
      CellStyle csForFooterCenter = ExcelUtils.getFooterCenterCellStyle(wb);
      CellStyle csForFooterRight = ExcelUtils.getFooterRightCellStyle(wb);
      CellStyle csForString = ExcelUtils.getStringLeftCellStyle(wb);
      Map<CellStyleData, CellStyle> styleMap = new HashMap<>();
      int rowNumber = 0;
      List<PropertyContext> propertyContextList = sheetContext.getPropertyList();
      boolean containsSerial = propertyContextList.stream().anyMatch(v -> "coreExportIndex".equals(v.getName()));
      propertyContextList = propertyContextList.stream().filter(v -> !"coreExportIndex".equals(v.getName())).collect(Collectors.toList());
      int serialNumber = containsSerial ? 1 : 0;
      int[] cellMaxWidth = new int[propertyContextList.size() + serialNumber];
      row = sheet.createRow(0);
      cell = row.createCell(0);
      cell.setCellStyle(csForTopic);
      ExcelHelper.setCellStringValue(sheetContext.getTopic(), cell, wb);
      int countTopicColumn = containsSerial ? propertyContextList.size() : propertyContextList.size() - 1;

      for (int i = 1; i <= countTopicColumn; i++) {
         cell = row.createCell(i);
         cell.setCellValue("");
         cell.setCellStyle(csForTopic);
      }

      if (countTopicColumn > 0) {
         sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, countTopicColumn));
      }

      rowNumber++;
      List<Pair<String, Pair<Integer, Integer>>> subTopicList = sheetContext.getSubTopicList();
      if (!subTopicList.isEmpty()) {
         row = sheet.createRow(rowNumber);

         for (int i = 0; i <= propertyContextList.size(); i++) {
            cell = row.createCell(i);
            boolean match = false;

            for (Pair<String, Pair<Integer, Integer>> subTopic : subTopicList) {
               Pair<Integer, Integer> pos = subTopic.getSecond();
               if (pos.getFirst() == i) {
                  ExcelHelper.setCellStringValue(subTopic.getFirst(), cell, wb);
                  match = true;
                  break;
               }
            }

            if (!match) {
               cell.setCellValue("");
            }

            cell.setCellStyle(csForTitle);
         }

         for (Pair<String, Pair<Integer, Integer>> subTopicx : subTopicList) {
            Pair<Integer, Integer> pos = subTopicx.getSecond();
            if (pos.getSecond() > pos.getFirst()) {
               sheet.addMergedRegion(new CellRangeAddress(1, 1, pos.getFirst(), pos.getSecond()));
            }
         }

         rowNumber++;
      }

      row = sheet.createRow(rowNumber);
      if (containsSerial) {
         cell = row.createCell(0);
         cell.setCellValue("序号");
         cell.setCellStyle(csForTitle);
         cellMaxWidth[0] = ExcelUtils.getCellWidth("序号");
      }

      for (int i = 0; i < propertyContextList.size(); i++) {
         PropertyContext propertyContext = propertyContextList.get(i);
         cell = row.createCell(i + serialNumber);
         cell.setCellStyle(csForTitle);
         ExcelHelper.setCellStringValue(propertyContext.getTitle(), cell, wb);
         cellMaxWidth[i + serialNumber] = ExcelUtils.getCellWidth(propertyContext.getTitle());
      }

      if (!subTopicList.isEmpty()) {
         for (int i = 0; i <= propertyContextList.size(); i++) {
            boolean belongsToSubTitle = false;

            for (Pair<String, Pair<Integer, Integer>> subTopicxx : subTopicList) {
               Pair<Integer, Integer> pos = subTopicxx.getSecond();
               if (i >= pos.getFirst() && i <= pos.getSecond()) {
                  belongsToSubTitle = true;
                  break;
               }
            }

            if (!belongsToSubTitle) {
               sheet.getRow(1).getCell(i).setCellValue(sheet.getRow(2).getCell(i).getStringCellValue());
               sheet.addMergedRegion(new CellRangeAddress(1, 2, i, i));
            }
         }
      }

      rowNumber++;
      Map<String, Double> sumMap = new LinkedHashMap<>();
      List<Pair<String, List<String>>> mergePropertyPairList = sheetContext.getMergePropertyPairList();
      Map<String, DefaultSheetWriter.CellMergeInstance> mergeInstanceMap = new HashMap<>();
      int i = 0;

      for (int is = this.getItems().size(); i < is; i++) {
         Object item = this.getItems().get(i);
         row = sheet.createRow(rowNumber);
         if (containsSerial) {
            cell = row.createCell(0);
            cell.setCellValue(i + 1);
            cell.setCellStyle(csForCenterContent);
         }

         int length = ExcelUtils.getCellWidth("" + i);
         if (cellMaxWidth[0] < length) {
            cellMaxWidth[0] = length;
         }

         int j = 0;

         for (int ps = propertyContextList.size(); j < ps; j++) {
            PropertyContext propertyContext = propertyContextList.get(j);
            cell = row.createCell(j + serialNumber);
            Object rawValue = this.getPropertyRawValue(item, propertyContext.getName());
            BeanPropertyDescriptor propertyDescriptor = null;
            if (!Map.class.isAssignableFrom(this.getItemClass())) {
               propertyDescriptor = BeanPropertyDescriptor.of(this.getItemClass(), propertyContext.getName());
            }

            if (propertyDescriptor == null) {
               if (propertyContext.getPropertyType() == null) {
                  if (rawValue == null) {
                     propertyContext.setPropertyType(BeanPropertyType.UNKNOWN);
                  } else {
                     Class<?> clazz = rawValue.getClass();
                     propertyContext.setPropertyType(BeanPropertyType.of(clazz));
                  }
               }
            } else {
               propertyContext.setPropertyType(BeanPropertyType.of(propertyDescriptor.getPropertyType()));
            }

            boolean hasStyle = propertyContext.getStyleConsumer() != null;
            if (!hasStyle) {
               if (BeanPropertyType.DOUBLE.equals(propertyContext.getPropertyType()) || BeanPropertyType.INTEGER.equals(propertyContext.getPropertyType())) {
                  cell.setCellStyle(csForRightContent);
               } else if (HorizontalAlignment.LEFT.equals(propertyContext.getAlignment())) {
                  cell.setCellStyle(csForLeftContent);
               } else if (HorizontalAlignment.RIGHT.equals(propertyContext.getAlignment())) {
                  cell.setCellStyle(csForRightContent);
               } else {
                  cell.setCellStyle(csForCenterContent);
               }
            } else {
               CellStyleData csd = new CellStyleData();
               propertyContext.getStyleConsumer().accept(sheet, csd, item);
               CellStyle cellStyle = styleMap.get(csd);
               if (cellStyle == null) {
                  if (BeanPropertyType.DOUBLE.equals(propertyContext.getPropertyType()) || BeanPropertyType.INTEGER.equals(propertyContext.getPropertyType())) {
                     cellStyle = ExcelUtils.getRightCellStyle(wb);
                  } else if (HorizontalAlignment.LEFT.equals(propertyContext.getAlignment())) {
                     cellStyle = ExcelUtils.getLeftCellStyle(wb);
                  } else if (HorizontalAlignment.RIGHT.equals(propertyContext.getAlignment())) {
                     cellStyle = ExcelUtils.getRightCellStyle(wb);
                  } else {
                     cellStyle = ExcelUtils.getCenterCellStyle(wb);
                  }

                  if (csd.getFillForegroundColor() != null) {
                     cellStyle.setFillForegroundColor(csd.getFillForegroundColor().getIndex());
                  }

                  if (csd.getFillPattern() != null) {
                     cellStyle.setFillPattern(csd.getFillPattern());
                  }

                  if (csd.getFontColor() != null) {
                     int index = cellStyle.getFontIndexAsInt();
                     Font font = sheet.getWorkbook().getFontAt(index);
                     font.setColor(csd.getFontColor().getIndex());
                  }

                  styleMap.put(csd, cellStyle);
               }

               cell.setCellStyle(cellStyle);
            }

            if (propertyContext.getCellRowItemConsumer() != null) {
               propertyContext.getCellRowItemConsumer().accept(cell, item);
            }

            if (rawValue == null) {
               cell.setBlank();
            } else {
               Function<Object, Object> convertFunction = propertyContext.getConvertFunction();
               if (convertFunction != null) {
                  propertyContext.setPropertyType(BeanPropertyType.STRING);
               }

               Object appliedValue = convertFunction == null ? rawValue : convertFunction.apply(rawValue);
               if (BeanPropertyType.INTEGER.equals(propertyContext.getPropertyType())) {
                  if (rawValue instanceof BigDecimal && ConvertUtils.canConvert(rawValue, Double.class)) {
                     cell.setCellValue(ConvertUtils.convert(appliedValue, Double.class));
                  } else {
                     Long convert = ConvertUtils.convert(appliedValue, Long.class);
                     if (convert != null && convert >= 1000000000000000L) {
                        cell.setCellValue(ConvertUtils.convert(appliedValue, String.class));
                        cell.setCellStyle(csForString);
                     } else {
                        cell.setCellValue(convert.longValue());
                     }
                  }
               } else if (BeanPropertyType.DOUBLE.equals(propertyContext.getPropertyType())) {
                  cell.setCellValue(ConvertUtils.convert(appliedValue, Double.class));
               } else if (BeanPropertyType.DATETIME.equals(propertyContext.getPropertyType())) {
                  LocalDateTime dateTimeValue = ConvertUtils.convert(appliedValue, LocalDateTime.class);
                  cell.setCellValue(this.getDateTimePropertyValue(propertyContext.getName(), dateTimeValue));
               } else if (BeanPropertyType.DATE.equals(propertyContext.getPropertyType())) {
                  LocalDate dateValue = ConvertUtils.convert(appliedValue, LocalDate.class);
                  cell.setCellValue(this.getDatePropertyValue(propertyContext.getName(), dateValue));
               } else {
                  ExcelHelper.setCellStringValue(ConvertUtils.convert(appliedValue, String.class), cell, wb);
               }

               length = ExcelUtils.getCellWidth(ConvertUtils.convert(appliedValue, String.class));
               if (cellMaxWidth[j + serialNumber] < length) {
                  cellMaxWidth[j + serialNumber] = length;
               }

               if (this.getSheetContext().getSumPropertyList().stream().anyMatch(n -> propertyContext.getName().equalsIgnoreCase(n.getFirst()))) {
                  double sum = ConvertUtils.convert(sumMap.get(propertyContext.getName()), double.class);
                  sum += ConvertUtils.convert(appliedValue, double.class);
                  sumMap.put(propertyContext.getName(), sum);
               }
            }

            if (!mergePropertyPairList.isEmpty()) {
               Pair<String, List<String>> pair = mergePropertyPairList.stream()
                  .filter(p -> p.getFirst().equalsIgnoreCase(propertyContext.getName()))
                  .findFirst()
                  .orElse(null);
               if (pair != null) {
                  DefaultSheetWriter.CellMergeInstance mergeInstance = mergeInstanceMap.get(pair.getFirst());
                  if (mergeInstance == null) {
                     DefaultSheetWriter.CellMergeInstance instance = new DefaultSheetWriter.CellMergeInstance(
                        propertyContext.getName(), pair.getSecond(), rowNumber, j + 1
                     );
                     List<Object> valueList = new ArrayList<>();

                     for (String name : pair.getSecond()) {
                        valueList.add(this.getPropertyRawValue(item, name));
                     }

                     instance.setComparisionValueList(valueList);
                     mergeInstanceMap.put(pair.getFirst(), instance);
                  } else if (!this.isMergeRow(item, mergeInstance)) {
                     if (mergeInstance.getStartRowIndex() + 1 < rowNumber) {
                        sheet.addMergedRegion(
                           new CellRangeAddress(mergeInstance.getStartRowIndex(), rowNumber - 1, mergeInstance.getColumnIndex(), mergeInstance.getColumnIndex())
                        );
                     }

                     mergeInstance.setStartRowIndex(rowNumber);
                     List<Object> valueList = new ArrayList<>();

                     for (String name : pair.getSecond()) {
                        valueList.add(this.getPropertyRawValue(item, name));
                     }

                     mergeInstance.setComparisionValueList(valueList);
                  }

                  if (rowNumber == is && mergeInstance.getStartRowIndex() != is) {
                     sheet.addMergedRegion(
                        new CellRangeAddress(mergeInstance.getStartRowIndex(), rowNumber + 1, mergeInstance.getColumnIndex(), mergeInstance.getColumnIndex())
                     );
                  }
               }
            }
         }

         rowNumber++;
      }

      if (!sumMap.isEmpty()) {
         row = sheet.createRow(rowNumber);
         cell = row.createCell(0);
         cell.setCellValue("合计：");
         cell.setCellStyle(csForFooterCenter);
         i = -1;
         int ix = 0;

         for (int ps = propertyContextList.size(); ix < ps; ix++) {
            PropertyContext propertyContextx = propertyContextList.get(ix);
            cell = row.createCell(ix + 1);
            cell.setCellStyle(csForFooterRight);
            Double sum = ConvertUtils.convert(CollectionUtils.getValueIgnorecase(sheetContext.getTotalMap(), propertyContextx.getName()), Double.class, null);
            if (sum == null) {
               sum = sumMap.get(propertyContextx.getName());
            }

            if (sum != null) {
               if (i == -1) {
                  i = ix;
               }

               Function<Double, String> function = this.getSheetContext()
                  .getSumPropertyList()
                  .stream()
                  .filter(n -> propertyContextx.getName().equalsIgnoreCase(n.getFirst()))
                  .findFirst()
                  .get()
                  .getSecond();
               String stringValue = function.apply(sum);
               cell.setCellValue(ConvertUtils.convert(stringValue, Double.class));
               int length = ExcelUtils.getCellWidth(stringValue);
               if (cellMaxWidth[ix + serialNumber] < length) {
                  cellMaxWidth[ix + serialNumber] = length;
               }
            } else {
               cell.setBlank();
            }
         }

         sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, i));
      }

      for (int ix = 0; ix < cellMaxWidth.length; ix++) {
         int width = cellMaxWidth[ix] + 512;
         if (width > 15360) {
            width = 15360;
         }

         if (countTopicColumn == 0) {
            width = width > 5120 ? width : 5120;
         }

         sheet.setColumnWidth(ix, width);
      }

      if (subTopicList.isEmpty()) {
         sheet.createFreezePane(serialNumber, 2);
      } else {
         sheet.createFreezePane(serialNumber, 3);
      }
   }

   private Object getPropertyRawValue(Object item, String propertyName) {
      if (item == null || propertyName == null) {
         return null;
      } else if (Map.class.isAssignableFrom(this.getItemClass())) {
         Object o = ((Map<?, ?>)item)
            .entrySet()
            .stream()
            .filter(e -> propertyName.equalsIgnoreCase((String)e.getKey()))
            .map(e -> ObjectUtils.isEmpty(e.getValue()) ? "" : e.getValue())
            .findFirst()
            .orElse(null);
         if (ObjectUtils.isEmpty(o) && TableRowBean.class.isAssignableFrom(this.getItemClass())) {
            o = ((Map<?, ?>)((TableRowBean)item)
               .getExt$())
               .entrySet()
               .stream()
               .filter(e -> propertyName.equalsIgnoreCase((String)e.getKey()))
               .map(e -> ObjectUtils.isEmpty(e.getValue()) ? "" : e.getValue())
               .findFirst()
               .orElse(null);
         }

         return o;
      } else {
         BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(item.getClass(), propertyName);
         if (propertyDescriptor != null) {
            return ReflectionUtils.invokeMethod(propertyDescriptor.getReadMethod(), item);
         } else {
            if (BaseData.class.isAssignableFrom(item.getClass())) {
               Entry<String, String> entry = ((BaseData)item)
                  .getExt$()
                  .entrySet()
                  .stream()
                  .filter(e -> propertyName.equalsIgnoreCase((String)e.getKey()))
                  .findFirst()
                  .orElse(null);
               if (entry != null) {
                  return entry.getValue();
               }
            }

            return null;
         }
      }
   }

   private String getDateTimePropertyValue(String propertyName, LocalDateTime propertyValue) {
      if (propertyValue == null) {
         return null;
      } else {
         BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(this.getItemClass(), propertyName);
         if (propertyDescriptor != null) {
            JSONField jsonField = propertyDescriptor.getProperty().getAnnotation(JSONField.class);
            if (jsonField != null) {
               return DateTimeUtils.formatLocalDateTime(propertyValue, jsonField.format());
            }
         }

         return DateTimeUtils.formatLocalDateTime(propertyValue);
      }
   }

   private String getDatePropertyValue(String propertyName, LocalDate propertyValue) {
      if (propertyValue == null) {
         return null;
      } else {
         BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(this.getItemClass(), propertyName);
         if (propertyDescriptor != null) {
            JSONField jsonField = propertyDescriptor.getProperty().getAnnotation(JSONField.class);
            if (jsonField != null) {
               return DateTimeUtils.formatLocalDate(propertyValue, jsonField.format());
            }
         }

         return DateTimeUtils.formatLocalDate(propertyValue);
      }
   }

   private boolean isMergeRow(Object item, DefaultSheetWriter.CellMergeInstance mergeInstance) {
      List<String> comparisionPropertyList = mergeInstance.getComparisionPropertyList();

      for (int i = 0; i < comparisionPropertyList.size(); i++) {
         Object value = this.getPropertyRawValue(item, comparisionPropertyList.get(i));
         Object comparisionValue = mergeInstance.getComparisionValueList().get(i);
         if (!ObjectUtils.equals(value, comparisionValue)) {
            return false;
         }
      }

      return true;
   }

   public static class CellMergeInstance {
      private String property;
      private List<String> comparisionPropertyList;
      private int startRowIndex;
      private int columnIndex;
      private List<Object> comparisionValueList;

      public CellMergeInstance(String property, List<String> comparisionPropertyList, int startRowIndex, int columnIndex) {
         this.property = property;
         this.comparisionPropertyList = comparisionPropertyList;
         this.startRowIndex = startRowIndex;
         this.columnIndex = columnIndex;
      }

      public int getStartRowIndex() {
         return this.startRowIndex;
      }

      public void setStartRowIndex(int startRowIndex) {
         this.startRowIndex = startRowIndex;
      }

      public int getColumnIndex() {
         return this.columnIndex;
      }

      public void setColumnIndex(int columnIndex) {
         this.columnIndex = columnIndex;
      }

      public String getProperty() {
         return this.property;
      }

      public void setProperty(String property) {
         this.property = property;
      }

      public List<String> getComparisionPropertyList() {
         return this.comparisionPropertyList;
      }

      public void setComparisionPropertyList(List<String> comparisionPropertyList) {
         this.comparisionPropertyList = comparisionPropertyList;
      }

      public List<Object> getComparisionValueList() {
         return this.comparisionValueList;
      }

      public void setComparisionValueList(List<Object> comparisionValueList) {
         this.comparisionValueList = comparisionValueList;
      }
   }
}
