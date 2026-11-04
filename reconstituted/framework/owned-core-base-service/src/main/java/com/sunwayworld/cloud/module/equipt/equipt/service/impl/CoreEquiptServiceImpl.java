package com.sunwayworld.cloud.module.equipt.equipt.service.impl;

import com.sunwayworld.cloud.module.equipt.equipt.bean.CoreEquiptBean;
import com.sunwayworld.cloud.module.equipt.equipt.persistent.dao.CoreEquiptDao;
import com.sunwayworld.cloud.module.equipt.equipt.service.CoreEquiptService;
import com.sunwayworld.cloud.module.equipt.usagerecord.bean.CoreEquiptUsageRecordBean;
import com.sunwayworld.cloud.module.equipt.usagerecord.service.CoreEquiptUsageRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreEquiptServiceImpl implements CoreEquiptService {
   @Autowired
   private CoreEquiptDao equiptDao;
   @Autowired
   private CoreEquiptUsageRecordService equiptUsageRecordService;

   public CoreEquiptDao getDao() {
      return this.equiptDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreEquiptBean equipt = jsonWrapper.parseUnique(CoreEquiptBean.class);
      equipt.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(equipt);
      return equipt.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void updateEquiptSwitchStatus(String code, String switchStatus) {
      CoreEquiptBean coreKeyEquipt = this.selectFirstByFilter(SearchFilter.instance().match("MONITORCODE", code).filter(MatchPattern.EQ), new Order[0]);
      coreKeyEquipt.setSwitchStatus(switchStatus);
      this.getDao().update(coreKeyEquipt, new String[]{"SWITCHSTATUS"});
   }

   @Override
   public String selectLastWorkStatusByCode(String code) {
      CoreEquiptBean equipt = this.selectFirstByFilter(SearchFilter.instance().match("MONITORCODE", code).filter(MatchPattern.EQ), new Order[0]);
      CoreEquiptUsageRecordBean coreEquiptUsageRecord = this.equiptUsageRecordService
         .selectFirstByFilter(SearchFilter.instance().match("EQUIPTID", equipt.getId()).filter(MatchPattern.EQ), new Order[]{Order.desc("CREATEDTIME")});
      return coreEquiptUsageRecord != null && coreEquiptUsageRecord.getUpTime() != null && coreEquiptUsageRecord.getDownTime() == null ? "1" : "0";
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertUsageRecordByCode(String code) {
      CoreEquiptBean equipt = this.selectFirstByFilter(SearchFilter.instance().match("MONITORCODE", code).filter(MatchPattern.EQ), new Order[0]);
      if (equipt == null) {
         return null;
      } else {
         CoreEquiptUsageRecordBean equiptUsageRecord = new CoreEquiptUsageRecordBean();
         equiptUsageRecord.setId(ApplicationContextHelper.getNextIdentity());
         equiptUsageRecord.setEquiptId(equipt.getId());
         equiptUsageRecord.setEquiptNo(equipt.getEquiptNo());
         equiptUsageRecord.setEquiptName(equipt.getEquiptName());
         equiptUsageRecord.setEquiptCategory(equipt.getEquiptCategory());
         equiptUsageRecord.setUpTime(LocalDateTime.now());
         this.equiptUsageRecordService.getDao().insert(equiptUsageRecord);
         return equiptUsageRecord.getId();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void updateUsageRecordByCode(String code) {
      CoreEquiptBean equipt = this.selectFirstByFilter(SearchFilter.instance().match("MONITORCODE", code).filter(MatchPattern.EQ), new Order[0]);
      CoreEquiptUsageRecordBean coreEquiptUsageRecord = this.equiptUsageRecordService
         .selectFirstByFilter(SearchFilter.instance().match("EQUIPTID", equipt.getId()).filter(MatchPattern.EQ), new Order[]{Order.desc("CREATEDTIME")});
      if (coreEquiptUsageRecord != null) {
         coreEquiptUsageRecord.setDownTime(LocalDateTime.now());
         Duration duration = Duration.between(coreEquiptUsageRecord.getUpTime(), coreEquiptUsageRecord.getDownTime());
         coreEquiptUsageRecord.setUsageTime(duration.toMinutes());
         this.equiptUsageRecordService.getDao().update(coreEquiptUsageRecord, "DOWNTIME", "USAGETIME");
      }
   }

   @Override
   public List<CoreEquiptBean> selectEquiptCategorySelectableList() {
      return this.selectAll()
         .stream()
         .filter(e -> !StringUtils.isEmpty(e.getEquiptCategory()))
         .collect(
            Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CoreEquiptBean::getEquiptCategory))), ArrayList::new)
         );
   }

   @Override
   public List<String> selectEquiptLocationSelectableList() {
      return this.selectAll().stream().filter(e -> !StringUtils.isEmpty(e.getLocation())).map(e -> e.getLocation()).distinct().collect(Collectors.toList());
   }

   @Override
   public List<Map<String, Object>> selectFixedAssetList() {
      List<Map<String, Object>> resultList = new ArrayList<>();
      List<CoreEquiptBean> equiptList = this.selectAll().stream().filter(e -> e.getCost() != null).collect(Collectors.toList());
      double keyEquiptCost = equiptList.stream().filter(e -> "1".equals(e.getKeyEquipt())).mapToDouble(e -> e.getCost()).sum();
      Map<String, Object> keyEquiptMap = new HashMap<>();
      keyEquiptMap.put("name", "关键仪器设备");
      keyEquiptMap.put("value", keyEquiptCost);
      resultList.add(keyEquiptMap);
      double otherEquiptCost = equiptList.stream().filter(e -> "0".equals(e.getKeyEquipt())).mapToDouble(e -> e.getCost()).sum();
      Map<String, Object> otherEquiptMap = new HashMap<>();
      otherEquiptMap.put("name", "其他仪器设备");
      otherEquiptMap.put("value", otherEquiptCost);
      resultList.add(otherEquiptMap);
      return resultList;
   }

   @Override
   public List<Map<String, Object>> selectEquiptCategoryStatisticsData() {
      List<Map<String, Object>> resultList = new ArrayList<>();
      List<CoreEquiptBean> equiptList = this.selectAll().stream().filter(e -> e.getEquiptCategory() != null).collect(Collectors.toList());
      Map<String, Long> equiptCategoryStatisticsMap = equiptList.stream()
         .collect(Collectors.groupingBy(CoreEquiptBean::getEquiptCategory, Collectors.counting()));
      equiptCategoryStatisticsMap.forEach((key, value) -> {
         Map<String, Object> resultMap = new HashMap<>();
         resultMap.put("name", key);
         resultMap.put("value", value);
         resultList.add(resultMap);
      });
      return resultList;
   }
}
