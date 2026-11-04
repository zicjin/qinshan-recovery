package com.sunwayworld.framework.audit;

import com.sunwayworld.framework.audit.event.AuditLogDTO;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogInsertDTO;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogService;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
   prefix = "sunway.audit",
   name = {"async"},
   havingValue = "true",
   matchIfMissing = false
)
public class AuditLogConsumer {
   private static final Logger logger = LogManager.getLogger(AuditLogConsumer.class);
   private static final String ISOLATED_KEY = "AUDIT_LOG";
   private static long MAX_LOOP = 50L;
   @Value("${sunway.audit.async-process-data-limit:500}")
   private int processDataCount;
   @Autowired
   private CoreAuditLogService auditLogService;
   @Autowired
   private TenantManager tenantManager;

   @Scheduled(
      fixedDelay = 1000L
   )
   public void consume() {
      if (this.processDataCount != 0) {
         this.tenantManager.call("AUDIT_LOG", () -> GikamConcurrentLocker.isolatedRun("AUDIT_LOG#" + TenantContext.getTenant(), () -> {
            int counter = 0;
            List<AuditLogDTO> logList = RedisHelper.getList("AUDIT_DATA", "AUDIT_LOG_LIST", this.processDataCount);

            while (!CollectionUtils.isEmpty(logList) && counter++ < MAX_LOOP) {
               try {
                  List<CoreAuditLogInsertDTO> auditLogInsertList = logList.stream().map(l -> {
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
                  this.auditLogService.insertAuditLogActivity(auditLogInsertList);
               } catch (Exception var7) {
                  logger.error("com.sunwayworld.framework.audit.AuditLogConsumer.consume audit log exception: {}", (Throwable)var7);
               } finally {
                  RedisHelper.removeList("AUDIT_DATA", "AUDIT_LOG_LIST", logList.size());
                  logList = RedisHelper.getList("AUDIT_DATA", "AUDIT_LOG_LIST", this.processDataCount);
               }
            }
         }));
      }
   }
}
