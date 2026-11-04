package com.sunwayworld.module.sys.at.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import com.sunwayworld.module.sys.at.dao.CoreAuditTrailRecordDao;
import com.sunwayworld.module.sys.at.filter.SpecialRecordLineFilter;
import com.sunwayworld.module.sys.at.filter.SpecialRecordLineFilterContext;
import com.sunwayworld.module.sys.at.filter.SpecialRecordLineHelper;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordLineService;
import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreAuditTrailRecordServiceImpl implements CoreAuditTrailRecordService {
   @Autowired
   private CoreAuditTrailRecordDao recordDao;
   @Autowired
   private CoreAuditTrailRecordLineService recordLineService;
   @Autowired(
      required = false
   )
   private List<SpecialRecordLineFilter> specialRecordLineFilters = Collections.emptyList();
   private static final Logger log = LoggerFactory.getLogger(CoreAuditTrailRecordServiceImpl.class);

   public CoreAuditTrailRecordDao getDao() {
      return this.recordDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insert(CoreAuditTrailRecordBean record) {
      record.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(record);
      return record.getId();
   }

   @Override
   public List<CoreAuditTrailRecordDTO> selectRecordList(String tableName, String id) {
      SearchFilter recordLineFilter = SearchFilter.instance().match("TARGETID", tableName + "$" + id).filter(MatchPattern.EQ);
      List<CoreAuditTrailRecordLineBean> recordLineList = this.recordLineService.selectListByFilter(recordLineFilter, new Order[0]);
      List<Long> recordIdList = recordLineList.stream().map(CoreAuditTrailRecordLineBean::getRecordId).distinct().collect(Collectors.toList());
      List<CoreAuditTrailRecordBean> recordList = this.getDao().selectListByIds(recordIdList, new Order[]{Order.desc("ID")});
      return recordList.stream()
         .map(
            r -> new CoreAuditTrailRecordDTO(
               r,
               recordLineList.stream()
                  .filter(l -> l.getRecordId().equals(r.getId()))
                  .sorted((l1, l2) -> l1.getId().compareTo(l2.getId()))
                  .collect(Collectors.toList())
            )
         )
         .collect(Collectors.toList());
   }

   @Override
   public List<CoreAuditTrailRecordBean> selectAtRecordList(String tableName, String id) {
      String targetId = tableName + "$" + id;
      List<CoreAuditTrailRecordBean> recordList = this.getDao().selectListByOneColumnValue(targetId, "TARGETID", new Order[]{Order.desc("ID")});
      if (recordList.isEmpty()) {
         List<CoreAuditTrailRecordLineBean> recordLineList = this.recordLineService
            .selectListByFilter(SearchFilter.instance().match("TARGETID", tableName + "$" + id).filter(MatchPattern.EQ), new Order[0]);
         if (!recordLineList.isEmpty()) {
            List<Long> recordIdList = recordLineList.stream().map(e -> e.getRecordId()).distinct().collect(Collectors.toList());
            return this.getDao().selectListByIds(recordIdList, new Order[0]);
         } else {
            return null;
         }
      } else {
         return recordList;
      }
   }

   @Override
   public List<CoreAuditTrailRecordBean> selectAtRecordList(String tableName, RestJsonWrapperBean wrapper) {
      String recordId = wrapper.getFilterValue("recordId");
      SearchFilter recordFilter = SearchFilter.instance();
      if (StringUtils.isEmpty(recordId)) {
         recordFilter = recordFilter.match("TARGETID", tableName + "$").filter(MatchPattern.SB);
      } else {
         recordFilter = recordFilter.match("TARGETID", tableName + "$" + recordId).filter(MatchPattern.EQ);
      }

      List<CoreAuditTrailRecordBean> recordList = this.selectListByFilter(recordFilter, wrapper);
      return recordList.isEmpty() ? null : recordList;
   }

   @Override
   public Page<CoreAuditTrailRecordBean> selectAtRecordPaginationList(String tableName, RestJsonWrapperBean wrapper) {
      String recordId = wrapper.getFilterValue("recordId");
      SearchFilter recordFilter = SearchFilter.instance();
      if (StringUtils.isEmpty(recordId)) {
         recordFilter = recordFilter.match("TARGETID", tableName + "$").filter(MatchPattern.SB);
      } else {
         recordFilter = recordFilter.match("TARGETID", tableName + "$" + recordId).filter(MatchPattern.EQ);
      }

      return this.selectPaginationByFilter(recordFilter, wrapper);
   }

   @Override
   public Page<CoreAuditTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean wrapper) {
      String tableName = wrapper.getFilterValue("tableName");
      this.checkAvailable(tableName);
      String targetId = wrapper.getFilterValue("targetId");
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      String nameField = this.getNameField(tableName);
      return MybatisPageHelper.get(
         rowBounds,
         () -> {
            List<CoreAuditTraceOverviewDTO> overviewDTOList = this.getDao().selectTraceOverview(targetId, tableName, nameField);
            List<CoreAuditTraceOverviewDTO> deletedOverviewList = overviewDTOList.stream()
               .filter(coreAuditTraceOverviewDTOx -> StringUtils.isBlank(coreAuditTraceOverviewDTOx.getTargetName()))
               .peek(coreAuditTraceOverviewDTOx -> coreAuditTraceOverviewDTOx.setDeleted(true))
               .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(deletedOverviewList)) {
               List<String> targetIds = deletedOverviewList.stream().map(CoreAuditTraceOverviewDTO::getTargetId).distinct().collect(Collectors.toList());
               List<CoreAuditTrailRecordLineNameDTO> deletedNames = this.recordLineService.selectDeletedNames(targetIds, nameField);
               Map<String, String> targetNameMap = deletedNames.stream()
                  .filter(nameDTO -> !StringUtils.isBlank(nameDTO.getName()))
                  .filter(nameDTO -> !StringUtils.isBlank(nameDTO.getTargetId()))
                  .collect(Collectors.toMap(CoreAuditTrailRecordLineNameDTO::getTargetId, CoreAuditTrailRecordLineNameDTO::getName, (a, b) -> a));

               for (CoreAuditTraceOverviewDTO coreAuditTraceOverviewDTO : deletedOverviewList) {
                  String targetName = targetNameMap.get(coreAuditTraceOverviewDTO.getTargetId());
                  coreAuditTraceOverviewDTO.setTargetName(targetName);
               }
            }

            return overviewDTOList;
         }
      );
   }

   @Override
   public List<CoreAuditTrailRecordLineDTO> selectRecordLineDTOByTableNameAndTargetId(String tableName, String bizId) {
      String targetId = tableName + "$" + bizId;
      SearchFilter recordFilter = SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.EQ);
      List<CoreAuditTrailRecordBean> recordBeans = this.selectListByFilter(recordFilter, new Order[0]);
      Map<Long, CoreAuditTrailRecordBean> recordBeanMap = recordBeans.stream()
         .collect(Collectors.toMap(CoreAuditTrailRecordBean::getId, Function.identity(), (a, b) -> a));
      SearchFilter recordLineFilter = SearchFilter.instance().match("targetId", targetId).filter(MatchPattern.EQ);
      List<CoreAuditTrailRecordLineBean> recordLineBeans = this.recordLineService.selectListByFilter(recordLineFilter, new Order[0]);
      List<CoreAuditTrailRecordLineDTO> coreAuditTrailRecordLineDTOList = new ArrayList<>(recordLineBeans.size());

      for (CoreAuditTrailRecordLineBean recordLineBean : recordLineBeans) {
         CoreAuditTrailRecordLineDTO recordLineDTO = new CoreAuditTrailRecordLineDTO();
         BeanUtils.copyProperties(recordLineBean, recordLineDTO);
         CoreAuditTrailRecordBean recordBean = recordBeanMap.get(recordLineBean.getRecordId());
         if (recordBean == null) {
            log.warn("record not found recordId: {} lineId: {}", recordLineBean.getRecordId(), recordLineBean.getId());
         } else {
            recordLineDTO.setAuditTrailType(recordBean.getAuditTrailType());
            recordLineDTO.setAuditTrailName(recordBean.getAuditTrailName());
            recordLineDTO.setCreatedTime(recordBean.getCreatedTime());
            recordLineDTO.setCreatedByName(recordBean.getCreatedByName());
            coreAuditTrailRecordLineDTOList.add(recordLineDTO);
         }
      }

      SpecialRecordLineFilterContext context = new SpecialRecordLineFilterContext(coreAuditTrailRecordLineDTOList);

      for (SpecialRecordLineFilter specialRecordLineFilter : this.specialRecordLineFilters) {
         specialRecordLineFilter.filter(context);
      }

      coreAuditTrailRecordLineDTOList.sort(
         Comparator.comparing(CoreAuditTrailRecordLineBean::getRecordId).reversed().thenComparing(CoreAuditTrailRecordLineBean::getRecordId)
      );
      return coreAuditTrailRecordLineDTOList;
   }

   @Override
   public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(Long recordId) {
      return this.recordLineService.selectRecordLineTableNameList(recordId);
   }

   @Override
   public List<CoreAuditTrailRecordLineBean> selectRecordLineList(Long recordId, String tableName) {
      return this.recordLineService.selectRecordLineList(recordId, tableName);
   }

   @Override
   public Page<CoreAuditTrailRecordLineBean> selectRecordLinePaginationList(RestJsonWrapperBean wrapper) {
      return this.recordLineService.selectPagination(wrapper);
   }

   @Override
   public CoreAuditTrailRecordLineBean selectRecordLineDetail(Long id) {
      return this.recordLineService.selectByIdIfPresent(id);
   }

   private void checkAvailable(String tableName) {
      SpecialRecordLineHelper.checkAvailable(tableName);
   }

   private String getNameField(String tableName) {
      return SpecialRecordLineHelper.getNameColumnByTableName(tableName);
   }
}
