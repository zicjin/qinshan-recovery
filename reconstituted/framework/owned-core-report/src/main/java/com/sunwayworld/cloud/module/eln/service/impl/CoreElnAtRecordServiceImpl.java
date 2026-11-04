package com.sunwayworld.cloud.module.eln.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.eln.bean.CoreElnAtRecordBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnAtRecordDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnAtRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreElnAtRecordServiceImpl implements CoreElnAtRecordService {
   @Autowired
   private CoreElnAtRecordDao coreElnChangeRecordDao;

   public CoreElnAtRecordDao getDao() {
      return this.coreElnChangeRecordDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insert(Long elnId, String dirtyRecords) {
      List<CoreElnAtRecordBean> changeRecordList = new ArrayList<>();
      Map<String, JSONObject> sheetNumMap = JSONObject.parseObject(dirtyRecords, Map.class);

      for (String sheetNum : sheetNumMap.keySet()) {
         Map<String, JSONObject> rowUuidMap = JSONObject.toJavaObject(sheetNumMap.get(sheetNum), Map.class);

         for (String rowUuid_rowCount : rowUuidMap.keySet()) {
            Map<String, Object> atRecordMap = JSONObject.toJavaObject(rowUuidMap.get(rowUuid_rowCount), Map.class);
            Object newValue = atRecordMap.get("new");
            Object oldValue = atRecordMap.get("old");
            Object row = atRecordMap.get("row");
            CoreElnAtRecordBean changeRecord = new CoreElnAtRecordBean();
            String[] rowUuidRowCountArray = rowUuid_rowCount.split(",");
            if (rowUuidRowCountArray.length != 2) {
               throw new UnexpectedException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.ELNS.TIP.CELL_UNIQUELY_EXCEPTION") + rowUuid_rowCount);
            }

            changeRecord.setRowUuid(rowUuidRowCountArray[0]);
            changeRecord.setRowNo(row + "");
            changeRecord.setColNo(rowUuidRowCountArray[1]);
            changeRecord.setRowUuidCol(rowUuidRowCountArray[0] + "," + rowUuidRowCountArray[1]);
            changeRecord.setElnId(elnId);
            changeRecord.setNewValue(newValue + "");
            changeRecord.setOldValue(oldValue + "");
            changeRecord.setSheetNo(sheetNum);
            changeRecord.setId(ApplicationContextHelper.getNextIdentity());
            changeRecordList.add(changeRecord);
         }
      }

      if (changeRecordList.size() > 0) {
         this.getDao().insert(changeRecordList);
      }
   }
}
