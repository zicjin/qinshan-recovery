package com.sunwayworld.module.sys.audit.filter;

import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogColumnService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractSpecialAuditLogColumnGetNameFilter implements SpecialAuditLogColumnFilter {
   public static final String OLDVALUE = "oldvalue";
   public static final String NEWVALUE = "newvalue";
   private static final Logger log = LoggerFactory.getLogger(AbstractSpecialAuditLogColumnGetNameFilter.class);
   @Autowired
   private CoreAuditLogColumnService coreAuditLogColumnService;

   @Override
   public void filter(SpecialAuditLogColumnFilterContext context) {
      List<CoreAuditLogColumnDTO> itemList = context.getItemList();
      String tableName = this.tableName();
      String columnName = this.columnName();
      List<CoreAuditLogColumnDTO> matchedAuditLogColumns = itemList.stream()
         .filter(coreAuditLogColumnDTO -> tableName.equals(coreAuditLogColumnDTO.getTargetTable()))
         .filter(coreAuditLogColumnDTO -> columnName.equals(coreAuditLogColumnDTO.getColumn()))
         .collect(Collectors.toList());
      if (!matchedAuditLogColumns.isEmpty()) {
         List<String> idList = matchedAuditLogColumns.stream()
            .flatMap(coreAuditLogColumnDTO -> {
               List<String> itemIds = new ArrayList<>(2);
               String oldValue = coreAuditLogColumnDTO.getOldValue();
               if (!StringUtils.isBlank(oldValue)) {
                  itemIds.add(oldValue);
               }

               String newValue = coreAuditLogColumnDTO.getNewValue();
               if (!StringUtils.isBlank(newValue)) {
                  itemIds.add(newValue);
               }

               return itemIds.stream();
            })
            .flatMap(idStr -> JsonUtils.isArray(idStr) ? JsonUtils.parseList(idStr, String.class).stream() : Stream.of((String)idStr))
            .collect(Collectors.toList());
         Map<String, String> targetNameMap = this.valueMap(idList);

         for (CoreAuditLogColumnDTO matchedAuditLogColumn : matchedAuditLogColumns) {
            String oldTargetId = matchedAuditLogColumn.getOldValue();
            String oldvalue = this.getValue(targetNameMap, oldTargetId);
            if (oldvalue != null) {
               matchedAuditLogColumn.setExt$Item("oldvalue", oldvalue);
            }

            String newTargetId = matchedAuditLogColumn.getNewValue();
            String newvalue = this.getValue(targetNameMap, newTargetId);
            if (newvalue != null) {
               matchedAuditLogColumn.setExt$Item("newvalue", newvalue);
            }
         }

         this.dealDeletedNames(matchedAuditLogColumns);
      }
   }

   private String getValue(Map<String, String> targetNameMap, String idStr) {
      if (JsonUtils.isArray(idStr)) {
         List<String> idList = JsonUtils.parseList(idStr, String.class);
         return idList.stream().map(targetNameMap::get).filter(Objects::nonNull).collect(Collectors.joining(","));
      } else {
         return targetNameMap.get(idStr);
      }
   }

   private void dealDeletedNames(List<CoreAuditLogColumnDTO> matchedAuditLogColumns) {
      List<CoreAuditLogColumnDTO> deleteTargetAuditLogColumns = matchedAuditLogColumns.stream()
         .filter(
            auditLogColumn -> auditLogColumn.getOldValue() != null && auditLogColumn.getExt$Item("oldvalue") == null
               ? true
               : auditLogColumn.getNewValue() != null && auditLogColumn.getExt$Item("newvalue") == null
         )
         .collect(Collectors.toList());
      Map<String, List<String>> deletedTableTargetIdsMap = deleteTargetAuditLogColumns.stream()
         .filter(auditLogColumn -> {
            String column = auditLogColumn.getColumn();
            return SpecialAuditLogColumnHelper.getTableNameBySpecialFieldName(column) != null;
         })
         .collect(
            Collectors.groupingBy(
               auditLogColumn -> SpecialAuditLogColumnHelper.getTableNameBySpecialFieldName(auditLogColumn.getColumn()), Collectors.mapping(auditLogColumn -> {
                  List<String> targetIds = new ArrayList<>();
                  if (auditLogColumn.getNewValue() != null) {
                     targetIds.add(auditLogColumn.getNewValue());
                  }

                  if (auditLogColumn.getOldValue() != null) {
                     targetIds.add(auditLogColumn.getOldValue());
                  }

                  return targetIds;
               }, Collector.of(ArrayList::new, List::addAll, (a, b) -> a))
            )
         );
      if (!deletedTableTargetIdsMap.isEmpty()) {
         deletedTableTargetIdsMap.forEach(
            (tableName, targetIds) -> {
               String nameField = SpecialAuditLogColumnHelper.getNameColumnByTableName(tableName);
               if (nameField == null) {
                  log.info("fill deleted name, tableName : {} not supported", tableName);
               }

               List<CoreAuditLogColumnNameDTO> logColumnNameDTOList = this.coreAuditLogColumnService
                  .selectDeletedNames((List<String>)targetIds, tableName, nameField);
               Map<String, CoreAuditLogColumnNameDTO> targetNameMap = logColumnNameDTOList.stream()
                  .collect(Collectors.toMap(CoreAuditLogColumnNameDTO::getTargetId, Function.identity(), (a, b) -> a));
               deleteTargetAuditLogColumns.forEach(auditLogColumn -> {
                  if (auditLogColumn.getOldValue() != null && auditLogColumn.getExt$Item("oldvalue") == null) {
                     String targetId = auditLogColumn.getOldValue();
                     CoreAuditLogColumnNameDTO auditLogColumnNameDTO = targetNameMap.get(targetId);
                     if (auditLogColumnNameDTO != null) {
                        auditLogColumn.setExt$Item("oldvalue", auditLogColumnNameDTO.getName());
                     }
                  }

                  if (auditLogColumn.getNewValue() != null && auditLogColumn.getExt$Item("newvalue") == null) {
                     String targetId = auditLogColumn.getNewValue();
                     CoreAuditLogColumnNameDTO auditLogColumnNameDTO = targetNameMap.get(targetId);
                     if (auditLogColumnNameDTO != null) {
                        auditLogColumn.setExt$Item("newvalue", auditLogColumnNameDTO.getName());
                     }
                  }
               });
            }
         );
      }
   }

   protected abstract String columnName();

   protected abstract String tableName();

   protected abstract Map<String, String> valueMap(List<String> idList);
}
