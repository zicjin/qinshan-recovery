package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSpecialColumnRecordLineFilter implements SpecialRecordLineFilter {
   public static final String OLDVALUE = "oldvalue";
   public static final String NEWVALUE = "newvalue";
   private static final Logger log = LoggerFactory.getLogger(AbstractSpecialColumnRecordLineFilter.class);
   @Autowired
   private CoreAuditTrailRecordLineService recordLineService;

   @Override
   public void filter(SpecialRecordLineFilterContext context) {
      List<CoreAuditTrailRecordLineDTO> itemList = context.getItemList();
      String tableName = this.tableName();
      String columnName = this.columnName();
      List<CoreAuditTrailRecordLineDTO> matchedRecordLines = itemList.stream()
         .filter(coreAuditTrailRecordLineDTO -> tableName.equals(coreAuditTrailRecordLineDTO.getTableName()))
         .filter(coreAuditTrailRecordLineDTO -> columnName.equals(coreAuditTrailRecordLineDTO.getColumn()))
         .collect(Collectors.toList());
      if (!matchedRecordLines.isEmpty()) {
         List<String> idList = matchedRecordLines.stream().flatMap(coreAuditTrailRecordLineDTO -> {
            List<String> itemIds = new ArrayList<>(2);
            String oldValue = coreAuditTrailRecordLineDTO.getOldValue();
            if (!StringUtils.isBlank(oldValue)) {
               itemIds.add(oldValue);
            }

            String newValue = coreAuditTrailRecordLineDTO.getNewValue();
            if (!StringUtils.isBlank(newValue)) {
               itemIds.add(newValue);
            }

            return itemIds.stream();
         }).collect(Collectors.toList());
         Map<String, String> targetNameMap = this.valueMap(idList);

         for (CoreAuditTrailRecordLineDTO matchedRecordLine : matchedRecordLines) {
            String oldTargetId = matchedRecordLine.getOldValue();
            String oldvalue = targetNameMap.get(oldTargetId);
            if (oldvalue != null) {
               matchedRecordLine.setExt$Item("oldvalue", oldvalue);
            }

            String newTargetId = matchedRecordLine.getNewValue();
            String newvalue = targetNameMap.get(newTargetId);
            if (newvalue != null) {
               matchedRecordLine.setExt$Item("newvalue", newvalue);
            }
         }

         this.dealDeletedNames(matchedRecordLines);
      }
   }

   private void dealDeletedNames(List<CoreAuditTrailRecordLineDTO> matchedRecordLines) {
      List<CoreAuditTrailRecordLineDTO> deleteTargetRecordLines = matchedRecordLines.stream()
         .filter(
            recordLine -> recordLine.getOldValue() != null && recordLine.getExt$Item("oldvalue") == null
               ? true
               : recordLine.getNewValue() != null && recordLine.getExt$Item("newvalue") == null
         )
         .collect(Collectors.toList());
      Map<String, List<String>> deletedTableTargetIdsMap = deleteTargetRecordLines.stream()
         .filter(recordLine -> {
            String column = recordLine.getColumn();
            return SpecialRecordLineHelper.getTableNameBySpecialFieldName(column) != null;
         })
         .collect(
            Collectors.groupingBy(
               recordLine -> SpecialRecordLineHelper.getTableNameBySpecialFieldName(recordLine.getColumn()), Collectors.mapping(recordLine -> {
                  List<String> targetIds = new ArrayList<>();
                  if (recordLine.getNewValue() != null) {
                     targetIds.add(recordLine.getNewValue());
                  }

                  if (recordLine.getOldValue() != null) {
                     targetIds.add(recordLine.getOldValue());
                  }

                  return targetIds;
               }, Collector.of(ArrayList::new, List::addAll, (a, b) -> a))
            )
         );
      if (!deletedTableTargetIdsMap.isEmpty()) {
         deletedTableTargetIdsMap.forEach(
            (tableName, targetIds) -> {
               String nameField = SpecialRecordLineHelper.getNameColumnByTableName(tableName);
               if (nameField == null) {
                  log.info("fill deleted name, tableName : {} not supported", tableName);
               }

               targetIds = targetIds.stream().map(targetId -> this.getTargetId(tableName, targetId)).collect(Collectors.toList());
               List<CoreAuditTrailRecordLineNameDTO> recordLineNames = this.recordLineService.selectDeletedNames(targetIds, nameField);
               Map<String, CoreAuditTrailRecordLineNameDTO> targetNameMap = recordLineNames.stream()
                  .collect(Collectors.toMap(CoreAuditTrailRecordLineNameDTO::getTargetId, Function.identity(), (a, b) -> a));
               deleteTargetRecordLines.forEach(recordLine -> {
                  if (recordLine.getOldValue() != null && recordLine.getExt$Item("oldvalue") == null) {
                     String targetId = this.getTargetId(tableName, recordLine.getOldValue());
                     CoreAuditTrailRecordLineNameDTO recordLineNameDTO = targetNameMap.get(targetId);
                     if (recordLineNameDTO != null) {
                        recordLine.setExt$Item("oldvalue", recordLineNameDTO.getName());
                     }
                  }

                  if (recordLine.getNewValue() != null && recordLine.getExt$Item("newvalue") == null) {
                     String targetId = this.getTargetId(tableName, recordLine.getNewValue());
                     CoreAuditTrailRecordLineNameDTO recordLineNameDTO = targetNameMap.get(targetId);
                     if (recordLineNameDTO != null) {
                        recordLine.setExt$Item("newvalue", recordLineNameDTO.getName());
                     }
                  }
               });
            }
         );
      }
   }

   private String getTargetId(String tableName, String recordLine) {
      return tableName + "$" + recordLine;
   }

   protected abstract String columnName();

   protected abstract String tableName();

   protected abstract Map<String, String> valueMap(List<String> idList);
}
