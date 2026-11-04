package com.sunwayworld.framework.at.aspect;

import com.sunwayworld.framework.at.AuditTrailHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService;
import com.sunwayworld.module.sys.metadata.bean.CoreParentTableInstanceDTO;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Profile({"audit-trail"})
public class AuditTrailAspect {
   public static final String AT_PRIMARY_SERVICE_KEY = "primaryService";
   public static final String AT_PRIMARY_ID_KEY = "primaryId";
   public static final String AT_COMMENT_KEY = "auditTrailComment";
   private static final ThreadLocal<AuditTrailAspect.AuditTrailEntryData> ENTRY_DATA = new ThreadLocal<>();
   private static final ThreadLocal<Map<String, List<CoreAuditTrailRecordLineBean>>> RECORD_DATA = new ThreadLocal<>();
   @Autowired
   private CoreAuditTrailRecordService recordService;
   @Autowired
   private CoreAuditTrailRecordLineService recordLineService;

   @Around("@annotation(auditTrailEntry)")
   public Object auditTrailService(ProceedingJoinPoint pjp, AuditTrailEntry auditTrailEntry) throws Throwable {
      if (AuditTrailHelper.auditTrail() && GenericService.class.isAssignableFrom(ClassUtils.getRawType(pjp.getTarget().getClass()))) {
         boolean init = false;

         AuditTrailAspect.AuditTrailEntryData entryData;
         try {
            if (ENTRY_DATA.get() == null) {
               HttpServletRequest request = ServletUtils.getCurrentRequest();
               entryData = new AuditTrailAspect.AuditTrailEntryData(auditTrailEntry);
               if (request != null) {
                  String primaryService = request.getHeader("primaryService");
                  if (StringUtils.isEmpty(primaryService)) {
                     entryData.setPrimaryTableName(((GenericService)pjp.getTarget()).getDao().getTable());
                  } else {
                     GenericService<?, ?> service = ApplicationContextHelper.getBean(primaryService);
                     entryData.setPrimaryTableName(service.getDao().getTable());
                  }

                  entryData.setPrimaryId(request.getHeader("primaryId"));
               }

               ENTRY_DATA.set(entryData);
               RECORD_DATA.set(new HashMap<>());
               init = true;
            }

            Object retValue = pjp.proceed();
            if (init) {
               this.insertAuditTrail();
            }

            entryData = (AuditTrailAspect.AuditTrailEntryData)retValue;
         } finally {
            if (init) {
               RECORD_DATA.remove();
               ENTRY_DATA.remove();
            }
         }

         return entryData;
      } else {
         return pjp.proceed();
      }
   }

   public static final AuditTrailAspect.AuditTrailEntryData getAuditTrailEntryData() {
      return ENTRY_DATA.get();
   }

   public static final <ID extends Serializable> String getAncientTargetId(String lineTable, ID lineId) {
      return getAncientTargetMap(lineTable, Arrays.asList(lineId)).values().iterator().next();
   }

   public static final <ID extends Serializable> Map<ID, String> getAncientTargetMap(String lineTable, List<ID> lineIdList) {
      Map<ID, String> map = new HashMap<>();
      AuditTrailAspect.AuditTrailEntryData entryData = Optional.of(getAuditTrailEntryData()).get();
      if (StringUtils.isEmpty(entryData.getPrimaryId())) {
         List<CoreParentTableInstanceDTO> instanceList = ApplicationContextHelper.getAncientTableInstanceList(lineTable, lineIdList);

         for (ID lineId : lineIdList) {
            CoreParentTableInstanceDTO instance = instanceList.stream().filter(i -> i.getSourceId().equals(ObjectUtils.toString(lineId))).findAny().get();
            map.put(lineId, instance.getTable() + "$" + instance.getId());
         }

         return map;
      } else {
         for (ID lineId : lineIdList) {
            map.put(lineId, entryData.getPrimaryTableName() + "$" + entryData.getPrimaryId());
         }

         return map;
      }
   }

   public static final void addRecordLine(String targetId, CoreAuditTrailRecordLineBean recordLine) {
      Map<String, List<CoreAuditTrailRecordLineBean>> map = RECORD_DATA.get();
      List<CoreAuditTrailRecordLineBean> lineList = map.get(targetId);
      if (lineList == null) {
         lineList = new ArrayList<>();
      }

      lineList.add(recordLine);
      map.put(targetId, lineList);
   }

   public static final void addRecordLine(String targetId, List<CoreAuditTrailRecordLineBean> recordLineList) {
      Map<String, List<CoreAuditTrailRecordLineBean>> map = RECORD_DATA.get();
      if (map != null) {
         List<CoreAuditTrailRecordLineBean> lineList = map.get(targetId);
         if (lineList == null) {
            lineList = new ArrayList<>();
         }

         lineList.addAll(recordLineList);
         map.put(targetId, lineList);
      }
   }

   private void insertAuditTrail() {
      AuditTrailAspect.AuditTrailEntryData entryData = ENTRY_DATA.get();
      if (entryData != null) {
         Map<String, List<CoreAuditTrailRecordLineBean>> map = RECORD_DATA.get();
         if (map != null && !map.isEmpty()) {
            List<CoreAuditTrailRecordBean> insertRecordList = new ArrayList<>();
            List<CoreAuditTrailRecordLineBean> insertRecordLineList = new ArrayList<>();
            map.forEach((k, v) -> {
               CoreAuditTrailRecordBean record = new CoreAuditTrailRecordBean();
               record.setId(ApplicationContextHelper.getNextIdentity());
               AuditTrailEntry entry = entryData.getEntry();
               record.setAuditTrailType(entry.value().name());
               record.setAuditTrailName(StringUtils.isEmpty(entry.name()) ? entry.value().getName() : entry.name());
               record.setTargetId(k);
               HttpServletRequest request = ServletUtils.getCurrentRequest();
               if (request != null) {
                  if (StringUtils.isEmpty(request.getHeader("auditTrailComment"))) {
                     record.setComment(request.getHeader("auditTrailComment"));
                  } else {
                     try {
                        record.setComment(URLDecoder.decode(request.getHeader("auditTrailComment"), "utf-8"));
                     } catch (UnsupportedEncodingException var9) {
                        throw new UnexpectedException(var9);
                     }
                  }
               }

               if (!LocalContextHelper.isUserLogin()) {
                  record.setCreatedById("(system)");
                  record.setCreatedByName("(system)");
               }

               insertRecordList.add(record);
               v.forEach(l -> l.setRecordId(record.getId()));
               insertRecordLineList.addAll(v);
            });
            this.recordService.getDao().insert(insertRecordList);
            this.recordLineService.insert(insertRecordLineList);
         }
      }
   }

   public static class AuditTrailEntryData {
      private AuditTrailEntry entry;
      private String primaryTableName;
      private String primaryId;

      public AuditTrailEntryData(AuditTrailEntry entry) {
         this.entry = entry;
      }

      public String getPrimaryId() {
         return this.primaryId;
      }

      public void setPrimaryId(String primaryId) {
         this.primaryId = primaryId;
      }

      public String getPrimaryTableName() {
         return this.primaryTableName;
      }

      public void setPrimaryTableName(String primaryTableName) {
         this.primaryTableName = primaryTableName;
      }

      public AuditTrailEntry getEntry() {
         return this.entry;
      }
   }
}
