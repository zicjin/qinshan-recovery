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
   @Autowired(required = false)
   private com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService.ReadScope.Provider auditReadScope;

   private String readOrganization() {
      return com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService.ReadScope.organization(auditReadScope);
   }

   private com.sunwayworld.framework.mybatis.mapper.MapperParameter scoped(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter) {
      String organization=readOrganization();
      if (organization != null) parameter.setFilter(SearchFilter.instance().match("CREATEDBYORGID",organization).filter(MatchPattern.EQ));
      return parameter;
   }

   @Override
   @Transactional(readOnly = true)
   public Page<CoreAuditTrailRecordBean> selectPagination(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter, PageRowBounds bounds) {
      return CoreAuditTrailRecordService.super.selectPagination(scoped(parameter),bounds);
   }

   @Override
   @Transactional(readOnly = true)
   public List<CoreAuditTrailRecordBean> selectList(com.sunwayworld.framework.mybatis.mapper.MapperParameter parameter) {
      return CoreAuditTrailRecordService.super.selectList(scoped(parameter));
   }

   private List<CoreAuditTrailRecordBean> recordsByIds(List<Long> ids,Order... orders) {
      if (readOrganization() == null) return this.getDao().selectListByIds(ids,orders);
      if (ids.isEmpty()) return Collections.emptyList();
      return selectListByFilter(SearchFilter.instance().match("ID",ids).filter(MatchPattern.OIN),orders);
   }

   private boolean visible(Long id) {
      String organization=readOrganization();
      if (organization == null) return true;
      return !selectListByFilter(SearchFilter.instance().match("ID",id).filter(MatchPattern.EQ)
            .match("CREATEDBYORGID",organization).filter(MatchPattern.EQ),new Order[0]).isEmpty();
   }

   @Override
   @Transactional(readOnly = true)
   public CoreAuditTrailRecordBean selectDetail(Long id) {
      return visible(id) ? CoreAuditTrailRecordService.super.selectDetail(id) : null;
   }

   @Override
   @Transactional(readOnly = true)
   public String selectColumnById(Long id,String column) {
      return visible(id) ? CoreAuditTrailRecordService.super.selectColumnById(id,column) : null;
   }

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
   @Transactional(readOnly = true)
   public List<CoreAuditTrailRecordDTO> selectRecordList(String tableName, String id) {
      SearchFilter recordLineFilter = SearchFilter.instance().match("TARGETID", tableName + "$" + id).filter(MatchPattern.EQ);
      List<CoreAuditTrailRecordLineBean> recordLineList = this.recordLineService.selectListByFilter(recordLineFilter, new Order[0]);
      List<Long> recordIdList = recordLineList.stream().map(CoreAuditTrailRecordLineBean::getRecordId).distinct().collect(Collectors.toList());
      List<CoreAuditTrailRecordBean> recordList = recordsByIds(recordIdList, Order.desc("ID"));
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
   @Transactional(readOnly = true)
   public List<CoreAuditTrailRecordBean> selectAtRecordList(String tableName, String id) {
      String targetId = tableName + "$" + id;
      List<CoreAuditTrailRecordBean> recordList = readOrganization() == null
         ? this.getDao().selectListByOneColumnValue(targetId,"TARGETID",Order.desc("ID"))
         : this.selectListByFilter(SearchFilter.instance().match("TARGETID",targetId).filter(MatchPattern.EQ),Order.desc("ID"));
      if (recordList.isEmpty()) {
         List<CoreAuditTrailRecordLineBean> recordLineList = this.recordLineService
            .selectListByFilter(SearchFilter.instance().match("TARGETID", tableName + "$" + id).filter(MatchPattern.EQ), new Order[0]);
         if (!recordLineList.isEmpty()) {
            List<Long> recordIdList = recordLineList.stream().map(e -> e.getRecordId()).distinct().collect(Collectors.toList());
            return recordsByIds(recordIdList);
         } else {
            return null;
         }
      } else {
         return recordList;
      }
   }

   @Override
   @Transactional(readOnly = true)
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
   @Transactional(readOnly = true)
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
   @Transactional(readOnly = true)
   public Page<CoreAuditTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean wrapper) {
      String organization=readOrganization();
      String tableName = wrapper.getFilterValue("tableName");
      this.checkAvailable(tableName);
      String targetId = wrapper.getFilterValue("targetId");
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      String nameField = this.getNameField(tableName);
      return MybatisPageHelper.get(
         rowBounds,
         () -> {
            List<CoreAuditTraceOverviewDTO> overviewDTOList = this.getDao().selectTraceOverview(targetId, tableName, nameField, organization);
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
   @Transactional(readOnly = true)
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
   @Transactional(readOnly = true)
   public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(Long recordId) {
      return this.recordLineService.selectRecordLineTableNameList(recordId);
   }

   @Override
   @Transactional(readOnly = true)
   public List<CoreAuditTrailRecordLineBean> selectRecordLineList(Long recordId, String tableName) {
      return this.recordLineService.selectRecordLineList(recordId, tableName);
   }

   @Override
   @Transactional(readOnly = true)
   public Page<CoreAuditTrailRecordLineBean> selectRecordLinePaginationList(RestJsonWrapperBean wrapper) {
      return this.recordLineService.selectPagination(wrapper);
   }

   @Override
   @Transactional(readOnly = true)
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
