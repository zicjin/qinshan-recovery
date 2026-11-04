package com.sunwayworld.framework.io.excel;

import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.io.excel.impl.DefaultExcelWriter;
import com.sunwayworld.framework.io.excel.impl.DefaultSheetWriter;
import com.sunwayworld.framework.io.excel.support.SheetContext;
import com.sunwayworld.framework.io.excel.utils.ExcelUtils;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.domain.BaseData;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.SpringUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jxls.builder.xls.XlsCommentAreaBuilder;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

public abstract class ExcelHelper {
   private static final String SUP_MARK_START = "<sup>";
   private static final String SUP_MARK_END = "</sup>";
   private static final String SUB_MARK_START = "<sub>";
   private static final String SUB_MARK_END = "</sub>";
   private static final String BR_MARK_START = "<br>";
   private static final String BR_MARK_END = "</br>";
   private static final String NUMBER_FORMAT = "#.####################";

   public static final <T> T getCellValue(final Cell cell, final Class<T> clazz) {
      Object cellValue = "";
      if (cell != null) {
         switch (cell.getCellType()) {
            case STRING:
               cellValue = StringUtils.trim(cell.getRichStringCellValue().getString());
               break;
            case NUMERIC:
               short format = cell.getCellStyle().getDataFormat();
               if (format != 14 && format != 22 && format != 31 && format != 57 && format != 58 && format != 20 && format != 32) {
                  cellValue = cell.getNumericCellValue();
                  DecimalFormat decimalFormat = new DecimalFormat("#.####################");
                  cellValue = decimalFormat.format(cell.getNumericCellValue());
               } else {
                  cellValue = DateUtil.getJavaDate(cell.getNumericCellValue());
               }
               break;
            case BOOLEAN:
               cellValue = ObjectUtils.toString(cell.getBooleanCellValue());
               break;
            case FORMULA:
               FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
               CellValue cv = evaluator.evaluate(cell);
               switch (cv.getCellType()) {
                  case STRING:
                     cellValue = StringUtils.trim(cv.getStringValue());
                     return ConvertUtils.convert(cellValue, clazz);
                  case NUMERIC:
                     cellValue = cv.getNumberValue();
                     return ConvertUtils.convert(cellValue, clazz);
                  case BOOLEAN:
                     cellValue = ObjectUtils.toString(cv.getBooleanValue());
                     return ConvertUtils.convert(cellValue, clazz);
                  default:
                     cellValue = null;
                     return ConvertUtils.convert(cellValue, clazz);
               }
            default:
               cellValue = "";
         }
      }

      return ConvertUtils.convert(cellValue, clazz);
   }

   public static <T> T getCellValue(Cell cell, Class<?> clazz, String propertyName) {
      Object cellValue = "";
      BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(clazz, propertyName);
      Class<?> propertyType = propertyDescriptor == null ? null : propertyDescriptor.getPropertyType();
      if (cell != null) {
         label28:
         switch (cell.getCellType()) {
            case STRING:
               cellValue = StringUtils.trim(cell.getRichStringCellValue().getString());
               break;
            case NUMERIC:
               if (DateUtil.isCellDateFormatted(cell)) {
                  cellValue = cell.getDateCellValue();
               } else {
                  cellValue = cell.getNumericCellValue();
               }
               break;
            case BOOLEAN:
               cellValue = ObjectUtils.toString(cell.getBooleanCellValue());
               break;
            case FORMULA:
               FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
               CellValue cv = evaluator.evaluate(cell);
               switch (cv.getCellType()) {
                  case STRING:
                     cellValue = StringUtils.trim(cv.getStringValue());
                     break label28;
                  case NUMERIC:
                     cellValue = "" + cv.getNumberValue();
                     break label28;
                  case BOOLEAN:
                     cellValue = ObjectUtils.toString(cv.getBooleanValue());
                     break label28;
                  default:
                     cellValue = null;
                     break label28;
               }
            default:
               cellValue = "";
         }
      }

      propertyType = propertyType == null ? String.class : propertyType;
      return ConvertUtils.convert(cellValue, (Class<T>)propertyType);
   }

   public static boolean canConvertCellValue(Cell cell, Class<?> targetType) {
      if (cell != null && targetType != null) {
         Object cellValue = "";
         switch (cell.getCellType()) {
            case STRING:
               cellValue = StringUtils.trim(cell.getRichStringCellValue().getString());
               break;
            case NUMERIC:
               if (DateUtil.isCellDateFormatted(cell)) {
                  cellValue = cell.getDateCellValue();
               } else {
                  cellValue = cell.getNumericCellValue();
               }
               break;
            case BOOLEAN:
               cellValue = ObjectUtils.toString(cell.getBooleanCellValue());
               break;
            case FORMULA:
               FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
               CellValue cv = evaluator.evaluate(cell);
               switch (cv.getCellType()) {
                  case STRING:
                     cellValue = StringUtils.trim(cv.getStringValue());
                     return ConvertUtils.canConvert(cellValue, targetType);
                  case NUMERIC:
                     cellValue = "" + cv.getNumberValue();
                     return ConvertUtils.canConvert(cellValue, targetType);
                  case BOOLEAN:
                     cellValue = ObjectUtils.toString(cv.getBooleanValue());
                     return ConvertUtils.canConvert(cellValue, targetType);
                  default:
                     cellValue = null;
                     return ConvertUtils.canConvert(cellValue, targetType);
               }
            default:
               cellValue = "";
         }

         return ConvertUtils.canConvert(cellValue, targetType);
      } else {
         return true;
      }
   }

   public static void setCellStringValue(String text, Cell cell, Workbook wb) {
      if (text == null) {
         text = "";
      }

      text = text.replaceAll("</?p>", "");
      if (StringUtils.contains(text, "<br>")) {
         CellStyle cellStyle = cell.getCellStyle();
         cellStyle.setWrapText(true);
         text = StringUtils.replace(text, "<br>", "\n");
         text = StringUtils.replace(text, "</br>", "");
         cell.setCellStyle(cellStyle);
      }

      if (containCornerMark(text)) {
         RichTextString richTextString = replaceCornerMarkToText(text, wb);
         cell.setCellValue(richTextString);
      } else {
         cell.setCellValue(text);
      }
   }

   public static boolean containCornerMark(String text) {
      return StringUtils.contains(text, "<sub>") || StringUtils.contains(text, "<sup>");
   }

   public static RichTextString replaceCornerMarkToText(String text, Workbook wb) {
      RichTextString richText = wb.getCreationHelper().createRichTextString(text.replaceAll("</?su[b,p]>", ""));
      Font supFont = wb.createFont();
      supFont.setTypeOffset((short)1);
      String supTmp = text.replaceAll("</?sub>", "");

      for (String sup : StringUtils.substringsBetween(text, "<sup>", "</sup>")) {
         int index = supTmp.indexOf("<sup>" + sup + "</sup>");
         if (index != -1) {
            richText.applyFont(index, index + sup.length(), supFont);
            supTmp = supTmp.replaceFirst("<sup>", "").replaceFirst("</sup>", "");
         }
      }

      Font subFont = wb.createFont();
      subFont.setTypeOffset((short)2);
      String subTmp = text.replaceAll("</?sup>", "");

      for (String sub : StringUtils.substringsBetween(text, "<sub>", "</sub>")) {
         int index = subTmp.indexOf("<sub>" + sub + "</sub>");
         if (index != -1) {
            richText.applyFont(index, index + sub.length(), subFont);
            subTmp = subTmp.replaceFirst("<sub>", "").replaceFirst("</sub>", "");
         }
      }

      return richText;
   }

   public static <T extends BaseData> void writeAsExcel(File file, String name, SheetContext sheetContext, List<T> itemList) {
      SheetWriter writer = new DefaultSheetWriter(sheetContext, itemList);
      ExcelWriter excelWriter = new DefaultExcelWriter(writer);

      try (FileOutputStream fos = new FileOutputStream(file)) {
         excelWriter.write(fos);
      } catch (IOException var19) {
         throw new FileException(var19);
      }
   }

   public static <T extends BaseData> FilePathDTO writeImportFailureListAsExcel(
      String filePath, List<T> itemList, int headerIndex, int validIndex, String errorFileName
   ) {
      FilePathDTO fp = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), UUID.randomUUID().toString(), errorFileName);
      Path path = FilePathManager.getLocalPath(fp);
      FileUtils.makeDirs(path);

      try (
         InputStream is = new BufferedInputStream(new FileInputStream(filePath));
         Workbook wb = WorkbookFactory.create(is);
      ) {
         Sheet sheet = wb.getSheetAt(0);
         Row row = sheet.getRow(headerIndex);
         int lastCellNum = row.getLastCellNum() - 1;
         Cell errorCell = row.getCell(lastCellNum);
         CellStyle cs = errorCell.getCellStyle();
         cs.setBorderTop(BorderStyle.THIN);
         cs.setBorderBottom(BorderStyle.THIN);
         cs.setBorderLeft(BorderStyle.THIN);
         cs.setBorderRight(BorderStyle.THIN);
         errorCell.setCellValue("错误信息");
         int cellMaxWidth = ExcelUtils.getCellWidth("错误信息");

         for (int i = 0; i < itemList.size(); i++) {
            T item = (T)itemList.get(i);
            row = sheet.getRow(i + validIndex);
            errorCell = row.createCell(lastCellNum);
            errorCell.setCellStyle(cs);
            errorCell.setCellValue(item.getExt$().get("validateimpmsg"));
            int length = ExcelUtils.getCellWidth(item.getExt$().get("validateimpmsg"));
            if (cellMaxWidth < length) {
               cellMaxWidth = length;
            }
         }

         sheet.setColumnWidth(lastCellNum, cellMaxWidth);

         try (OutputStream fos = Files.newOutputStream(path)) {
            wb.write(fos);
         }

         return fp;
      } catch (IOException var71) {
         throw new FileException(var71);
      }
   }

   public static <T extends BaseData> List<T> readDataFromExcel(String filePath, int headerRowIndex, int dataFromRowIndex, Class<T> clazz) {
      return readDataFromExcel(filePath, 0, headerRowIndex, dataFromRowIndex, clazz, null);
   }

   public static <T extends BaseData> List<T> readDataFromExcel(
      String filePath, int headerRowIndex, int dataFromRowIndex, Class<T> clazz, String masterPropertyName
   ) {
      return readDataFromExcel(filePath, 0, headerRowIndex, dataFromRowIndex, clazz, masterPropertyName);
   }

   public static <T extends BaseData> List<T> readDataFromExcel(String filePath, int sheetIndex, int headerRowIndex, int dataFromRowIndex, Class<T> clazz) {
      return readDataFromExcel(filePath, sheetIndex, headerRowIndex, dataFromRowIndex, clazz, null);
   }

   public static <T extends BaseData> List<T> readDataFromExcel(String filePath, String sheetName, int headerRowIndex, int dataFromRowIndex, Class<T> clazz) {
      return extractDataFromExcel(filePath, sheetName, headerRowIndex, dataFromRowIndex, clazz, null);
   }

   public static <T extends BaseData> List<T> readDataFromExcel(
      String filePath, int sheetIndex, int headerRowIndex, int dataFromRowIndex, Class<T> clazz, String masterPropertyName
   ) {
      return extractDataFromExcel(filePath, sheetIndex, headerRowIndex, dataFromRowIndex, clazz, masterPropertyName);
   }

   public static FilePathDTO writeData(CoreFileBean coreFile, Map<String, Object> contentMap) {
      CoreFileManager coreFileManager = SpringUtils.getBean(CoreFileManager.class);
      File tempFile = FileUtils.createTempFile();
      coreFileManager.download(coreFile, tempFile.toPath());
      FilePathDTO contentFp = FilePathDTO.of(FileScope.temp.name(), LocalDateTime.now(), UUID.randomUUID().toString(), coreFile.getName());
      Path contentPath = FilePathManager.getLocalPath(contentFp);
      FileUtils.makeDirs(contentPath);

      try (
         InputStream ips = Files.newInputStream(tempFile.toPath());
         OutputStream ops = Files.newOutputStream(contentPath);
      ) {
         XlsCommentAreaBuilder.addCommandMapping("autoRowHeight", AutoRowHightCommand.class);
         JxlsHelper jxls = JxlsHelper.getInstance();
         Context context = new Context();

         for (Entry<String, Object> entry : contentMap.entrySet()) {
            context.putVar(entry.getKey(), entry.getValue());
         }

         jxls.processTemplate(ips, ops, context);
      } catch (IOException var54) {
         throw new FileException(var54);
      } finally {
         FileUtils.deleteQuietly(tempFile);
      }

      coreFileManager.upload(contentFp, contentPath);
      return contentFp;
   }

   public static Path writeDataAndGetLocalPath(CoreFileBean coreFile, Map<String, Object> contentMap) {
      FilePathDTO filePathDTO = writeData(coreFile, contentMap);
      return FilePathManager.getLocalPath(filePathDTO);
   }

   public static String writeDataAndGetDownLoadPath(CoreFileBean coreFile, Map<String, Object> contentMap) {
      FilePathDTO filePathDTO = writeData(coreFile, contentMap);
      CoreFileManager coreFileManager = SpringUtils.getBean(CoreFileManager.class);
      return coreFileManager.getDownloadUrl(filePathDTO);
   }

   private static <T extends BaseData> List<T> extractDataFromExcel(
      String filePath, Object sheetIndexOrSheetName, int headerRowIndex, int dataFromRowIndex, Class<T> clazz, String masterPropertyName
   ) {
      try (Workbook wb = WorkbookFactory.create(new File(filePath))) {
         Sheet sheet = String.class.equals(sheetIndexOrSheetName.getClass())
            ? wb.getSheet((String)sheetIndexOrSheetName)
            : wb.getSheetAt((Integer)sheetIndexOrSheetName);
         Row row = null;
         List<Map<String, Object>> result = new ArrayList<>();
         row = sheet.getRow(headerRowIndex);
         int columnQty = row.getPhysicalNumberOfCells();
         int offset = 0;
         if (!StringUtils.isEmpty(masterPropertyName)) {
            offset = 1;
         }

         String[] columnNames = new String[columnQty];

         for (int i = 1 - offset; i < columnNames.length; i++) {
            if (row.getCell(i).getCellComment() != null) {
               columnNames[i] = row.getCell(i).getCellComment().getString().toString();
            } else if (row.getCell(i).getCellComment() == null) {
               Row previousRow = sheet.getRow(headerRowIndex - 1);
               if (previousRow != null && previousRow.getCell(i) != null && previousRow.getCell(i).getCellComment() != null) {
                  columnNames[i] = previousRow.getCell(i).getCellComment().getString().toString();
               }
            }
         }

         int endRowNum = sheet.getPhysicalNumberOfRows();

         for (int rowNum = dataFromRowIndex; rowNum < endRowNum; rowNum++) {
            row = sheet.getRow(rowNum);
            if (row != null) {
               Map<String, Object> map = new HashMap<>();

               for (int cellIndex = 1; cellIndex < columnNames.length; cellIndex++) {
                  Cell cell = row.getCell(cellIndex);
                  if (columnNames[cellIndex] != null && columnNames[cellIndex].trim().length() > 0) {
                     BeanPropertyDescriptor propertyDescriptor = BeanPropertyDescriptor.of(clazz, columnNames[cellIndex].trim());
                     Class<?> targetType = propertyDescriptor == null ? String.class : propertyDescriptor.getPropertyType();
                     if (canConvertCellValue(cell, targetType)) {
                        Object value = getCellValue(cell, clazz, columnNames[cellIndex].trim());
                        if (!ObjectUtils.isEmpty(value)) {
                           map.put(columnNames[cellIndex].trim(), value);
                        }
                     } else {
                        updateValidationImpMsg(map, "第" + (cellIndex + 1) + "列数据错误，不能转换为数据类型【" + targetType.getSimpleName() + "】");
                     }
                  }
               }

               if (!StringUtils.isEmpty(masterPropertyName)) {
                  map.put(masterPropertyName, columnNames[0]);
               }

               result.add(map);
            }
         }

         boolean isEmpty = true;

         for (int ix = result.size() - 1; ix >= 0; ix--) {
            for (String key : result.get(ix).keySet()) {
               Object value = result.get(ix).get(key);
               if (!ObjectUtils.isEmpty(value)) {
                  isEmpty = false;
               }
            }

            if (isEmpty) {
               result.remove(ix);
            }
         }

         return result.stream().map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, clazz)).collect(Collectors.toList());
      } catch (IOException var32) {
         throw new FileException(var32);
      }
   }

   private static void updateValidationImpMsg(Map<String, Object> map, String msg) {
      String origMsg = (String)map.get("validateimpmsg");
      if (StringUtils.isEmpty(origMsg)) {
         map.put("validateimpmsg", msg);
      } else {
         map.put("validateimpmsg", origMsg + "；" + msg);
      }
   }
}
