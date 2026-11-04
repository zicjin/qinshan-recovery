package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeWriteBackDTO;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.excel.ExcelHelper;
import com.sunwayworld.framework.io.excel.support.ExcelEmcFunction;
import com.sunwayworld.framework.io.excel.support.ExcelEpaFunction;
import com.sunwayworld.framework.io.excel.support.ExcelFdaFunction;
import com.sunwayworld.framework.io.excel.support.ExcelIsoFunction;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.module.item.file.bean.CoreOfficeDTO;
import com.sunwayworld.module.item.file.consumer.CoreOfficeInvokeConsumer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

@Component("coreOfficeELNInvokeConsumer")
public class CoreOfficeELNInvokeConsumer implements CoreOfficeInvokeConsumer {
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   public void accept(CoreOfficeDTO officeDTO) {
      Path filePath = officeDTO.getFilePath();

      try (
         InputStream is = Files.newInputStream(filePath);
         Workbook workbook = WorkbookFactory.create(is);
      ) {
         String[] functionNames = new String[]{"ISO", "EPA", "FDA", "EMC"};
         FreeRefFunction[] functionImpls = new FreeRefFunction[]{
            new ExcelIsoFunction(), new ExcelEpaFunction(), new ExcelFdaFunction(), new ExcelEmcFunction()
         };
         UDFFinder udfs = new DefaultUDFFinder(functionNames, functionImpls);
         UDFFinder udfToolpack = new AggregatingUDFFinder(udfs);
         workbook.addToolPack(udfToolpack);
         List<? extends Name> allNames = workbook.getAllNames();
         List<String> nameRangeList = new ArrayList<>();

         for (int i = 0; i < allNames.size(); i++) {
            Name cellName = allNames.get(i);
            CellReference cellRef = new CellReference(cellName.getRefersToFormula());
            String sheetName = cellRef.getSheetName();
            Sheet sheet = workbook.getSheet(sheetName);
            int rowIdx = cellRef.getRow();
            int colIdx = cellRef.getCol();
            Row row = sheet.getRow(rowIdx);
            Cell cell = row.getCell(colIdx);
            String updateValue = ExcelHelper.getCellValue(cell, String.class).toString();
            nameRangeList.add(cellName.getNameName() + "_updateKey_" + updateValue);
         }

         List<CoreOfficeWriteBackDTO> officeWriteBackList = new ArrayList<>();
         nameRangeList.forEach(e -> {
            String[] updateArray = e.split("_updateKey_");
            if (updateArray.length == 4) {
               String tableName = updateArray[0];
               String columnName = updateArray[1];
               String bizId = updateArray[2];
               String value = updateArray[3];
               CoreOfficeWriteBackDTO officeWriteBackDTO = new CoreOfficeWriteBackDTO(bizId, tableName, columnName, value);
               officeWriteBackList.add(officeWriteBackDTO);
            }
         });
         if (!officeWriteBackList.isEmpty()) {
            List<String> tableList = officeWriteBackList.stream().map(CoreOfficeWriteBackDTO::getTableName).distinct().collect(Collectors.toList());
            tableList.forEach(
               tableName -> {
                  List<CoreOfficeWriteBackDTO> filterWriteBackList = officeWriteBackList.stream()
                     .filter(e -> tableName.equals(e.getTableName()))
                     .collect(Collectors.toList());
                  List<Object> writeBackIdList = filterWriteBackList.stream().map(CoreOfficeWriteBackDTO::getBizId).distinct().collect(Collectors.toList());
                  Set<String> updateColNameList = new HashSet<>();
                  List<Persistable<?>> updateItemList = new ArrayList<>();
                  GenericService<Persistable<?>, ?> updateService = ApplicationContextHelper.getServiceByTable(tableName.toUpperCase());
                  writeBackIdList.forEach(
                     id -> {
                        List<CoreOfficeWriteBackDTO> sameIdWriteBackList = filterWriteBackList.stream()
                           .filter(ex -> id.equals(ex.getBizId()))
                           .collect(Collectors.toList());

                        try {
                           Map<String, Object> map = new HashMap<>();
                           sameIdWriteBackList.forEach(ex -> {
                              map.put(ex.getColumnName(), ex.getVal());
                              updateColNameList.add(ex.getColumnName());
                           });
                           map.put("id", id);
                           Persistable<?> rowx = ClassUtils.newInstance(updateService.getDao().getType());
                           BeanUtils.copyProperties(map, rowx);
                           updateItemList.add(rowx);
                        } catch (Exception var8x) {
                           throw new JdbcException(I18nHelper.getMessage("GIKAM.EXCEPTION.UNEXPECTED"));
                        }
                     }
                  );
                  updateService.getDao().update(updateItemList, updateColNameList.toArray(new String[updateColNameList.size()]));
               }
            );
         }
      } catch (IOException var50) {
         throw new FileException(var50);
      }
   }
}
