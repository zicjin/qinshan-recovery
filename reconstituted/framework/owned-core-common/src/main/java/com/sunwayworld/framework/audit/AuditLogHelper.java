package com.sunwayworld.framework.audit;

import com.sunwayworld.framework.audit.event.AuditLogDTO;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogInsertDTO;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigService;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuditLogHelper {
   public static final String AUDIT_DATA_CACHE_NAME = "AUDIT_DATA";
   public static final String AUDIT_DATA_CACHE_KEY = "AUDIT_LOG_LIST";
   public static final String AUDIT_KEY = "AUDIT_KEY";
   public static final String AUDIT_ACTIVITY_STACK_KEY = "AUDIT_ACTIVITY_STACK_KEY";
   public static final String AUDIT_ACTIVITY_KEY = "AUDIT_ACTIVITY_KEY";
   public static final String AUDIT_COMMENT_KEY = "AUDIT_COMMENT_KEY";
   public static final String AUDIT_SIGNATURE_KEY = "AUDIT_SIGNATURE_KEY";
   public static final String AUDIT_WITNESS_ID_KEY = "AUDIT_WITNESS_ID_KEY";
   public static final String AUDIT_WITNESS_NAME_KEY = "AUDIT_WITNESS_NAME_KEY";
   public static final String AUDIT_LOG_LIST_KEY = "AUDIT_LOG_LIST_KEY";
   public static final List<String> IGNORE_TABLE_LIST = Arrays.asList(
      "T_CORE_AUDIT_LOG", "T_CORE_AUDIT_LOG_ACTIVITY", "T_CORE_AUDIT_LOG_COLUMN", "T_CORE_LOG", "T_CORE_ESB_LOG", "T_CORE_EXPORT_LOG", "T_CORE_LOGIN_LOG"
   );
   public static final String AUDIT_TABLE_CONFIG = "AUDIT_TABLE_CONFIG";

   public static <ID> void insertAuditLog(String table, Persistable<ID> oldItem, Persistable<ID> newItem) {
      insertAuditLog(table, new CaseInsensitiveLinkedMap<>(BeanUtils.deeplyToMap(oldItem)), new CaseInsensitiveLinkedMap<>(BeanUtils.deeplyToMap(newItem)));
   }

   public static <ID> void insertAuditLog(String table, Map<String, Object> oldItem, Map<String, Object> newItem, String... activity) {
      insertAuditLog(
         table,
         new CaseInsensitiveLinkedMap<>((Map<? extends String, ? extends Object>)(oldItem == null ? new HashMap<>() : oldItem)),
         new CaseInsensitiveLinkedMap<>((Map<? extends String, ? extends Object>)(newItem == null ? new HashMap<>() : newItem)),
         activity
      );
   }

   public static void insertAuditLog(
      String table, CaseInsensitiveLinkedMap<Object> oldValueMap, CaseInsensitiveLinkedMap<Object> newValueMap, String... activity
   ) {
      if (!IGNORE_TABLE_LIST.contains(table.toUpperCase()) && !table.toUpperCase().startsWith("T_LCDP")) {
         AuditLogDTO auditLog = new AuditLogDTO(
            table,
            !ArrayUtils.isEmpty(activity) ? activity[0] : SunwayAopContext.getCache("AUDIT_ACTIVITY_KEY"),
            SunwayAopContext.getCache("AUDIT_COMMENT_KEY"),
            SunwayAopContext.getCache("AUDIT_SIGNATURE_KEY"),
            LocalContextHelper.getLoginUserId(),
            SunwayAopContext.getCache("AUDIT_WITNESS_ID_KEY"),
            SunwayAopContext.getCache("AUDIT_WITNESS_NAME_KEY"),
            oldValueMap,
            newValueMap
         );
         List<AuditLogDTO> auditLogList = SunwayAopContext.getCache("AUDIT_LOG_LIST_KEY");
         AuditLogDTO matchAuditDTO = auditLogList.stream()
            .filter(e -> e.getTable().equals(auditLog.getTable()) && e.getId().equals(auditLog.getId()))
            .findFirst()
            .orElse(null);
         if (matchAuditDTO != null && !insertOrDelete(auditLog)) {
            matchAuditDTO.setNewValueMap(auditLog.getNewValueMap());
         } else {
            auditLogList.add(auditLog);
         }
      }
   }

   public static void flush() {
      List<AuditLogDTO> auditLogList = SunwayAopContext.getCache("AUDIT_LOG_LIST_KEY");
      boolean auditAsync = ApplicationContextHelper.getEnvironment().getProperty("sunway.audit.async", Boolean.class, false);
      if (auditAsync) {
         RedisHelper.pushListAll("AUDIT_DATA", "AUDIT_LOG_LIST", auditLogList);
      } else {
         CoreAuditLogService auditLogService = ApplicationContextHelper.getBean(CoreAuditLogService.class);
         List<CoreAuditLogInsertDTO> auditLogInsertList = auditLogList.stream().map(l -> {
            CoreAuditLogInsertDTO auditLogInsert = new CoreAuditLogInsertDTO();
            auditLogInsert.setTable(l.getTable());
            auditLogInsert.setActivity(l.getActivity());
            auditLogInsert.setComment(l.getComment());
            auditLogInsert.setSignature(l.getSignature());
            auditLogInsert.setUserId(l.getUserId());
            auditLogInsert.setWitnessId(l.getWitnessId());
            auditLogInsert.setWitnessName(l.getWitnessName());
            auditLogInsert.setOldValueMap(l.getOldValueMap());
            auditLogInsert.setNewValueMap(l.getNewValueMap());
            auditLogInsert.setCreatedTime(l.getCreatedTime());
            return auditLogInsert;
         }).collect(Collectors.toList());
         auditLogService.insertAuditLogActivity(auditLogInsertList);
      }
   }

   public static boolean auditable(AuditDatabaseOperation auditDatabaseOperation) {
      if (SunwayAopContext.getCache("AUDIT_KEY") == null) {
         return false;
      } else {
         return !ApplicationContextHelper.getEnvironment().acceptsProfiles(p -> p.test("audit"))
            ? false
            : ApplicationContextHelper.getEnvironment().getProperty(auditDatabaseOperation.getPropertyKey(), Boolean.class);
      }
   }

   private static boolean insertOrDelete(AuditLogDTO auditLog) {
      return "AUDIT.DELETE".equals(auditLog.getActivity()) || "AUDIT.INSERT".equals(auditLog.getActivity());
   }

   public static CoreAuditConfigBean getAuditConfig(String tableName) {
      CoreAuditConfigBean config = RedisHelper.get("AUDIT_TABLE_CONFIG", tableName);
      if (null == config) {
         CoreAuditConfigService configService = ApplicationContextHelper.getBean(CoreAuditConfigService.class);
         config = configService.selectByIdIfPresent(tableName);
         if (null == config) {
            config = new CoreAuditConfigBean();
            config.setAuditable("0");
         }

         RedisHelper.put("AUDIT_TABLE_CONFIG", tableName, config);
      }

      return config;
   }
}
