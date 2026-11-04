package com.sunwayworld.cloud.module.office.service.impl;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeToolbarLineBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeToolbarDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeToolbarLineService;
import com.sunwayworld.cloud.module.office.service.CoreOfficeToolbarService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreOfficeToolbarServiceImpl implements CoreOfficeToolbarService {
   @Autowired
   private CoreOfficeToolbarDao coreOfficeToolbarDao;
   @Lazy
   @Autowired
   private CoreOfficeToolbarLineService coreOfficeToolbarLineService;

   public CoreOfficeToolbarDao getDao() {
      return this.coreOfficeToolbarDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreOfficeToolbarBean officeToolbar = wrapper.parseUnique(CoreOfficeToolbarBean.class);
      officeToolbar.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(officeToolbar);
      return officeToolbar.getId();
   }

   @Override
   public Page<CoreOfficeToolbarLineBean> selectLinePagination(Long toolbarId, RestJsonWrapperBean wrapper) {
      return this.coreOfficeToolbarLineService.selectPaginationByFilter(SearchFilter.instance().match("TOOLBARID", toolbarId).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertLine(Long toolbarId, RestJsonWrapperBean wrapper) {
      CoreOfficeToolbarLineBean officeToolbarLine = wrapper.parseUnique(CoreOfficeToolbarLineBean.class);
      officeToolbarLine.setId(ApplicationContextHelper.getNextIdentity());
      officeToolbarLine.setToolbarId(toolbarId);
      this.coreOfficeToolbarLineService.getDao().insert(officeToolbarLine);
      return officeToolbarLine.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteLine(RestJsonWrapperBean wrapper) {
      this.coreOfficeToolbarLineService.delete(wrapper);
   }

   @Cacheable(
      value = {"OFFICE-CUSTMIZATION-TOOLBAR"},
      key = "#officeCategory"
   )
   @Override
   public Page<CoreOfficeToolbarDTO> selectToolbarPagination(String officeCategory, RestJsonWrapperBean wrapper) {
      List<CoreOfficeToolbarDTO> officeToolbarList = new ArrayList<>();
      List<CoreOfficeToolbarBean> toolbarList = this.selectListByFilter(
         SearchFilter.instance().match("OFFICECATEGORY", officeCategory).filter(MatchPattern.EQ).match("LASTSUSPENDEDFLAG", "0").filter(MatchPattern.EQ),
         new Order[0]
      );
      if (toolbarList.size() > 0) {
         List<Long> toobarIdList = toolbarList.stream().map(CoreOfficeToolbarBean::getId).distinct().collect(Collectors.toList());
         List<CoreOfficeToolbarLineBean> toolbarLineList = this.coreOfficeToolbarLineService
            .selectListByFilter(SearchFilter.instance().match("TOOLBARID", toobarIdList).filter(MatchPattern.OR), new Order[0]);
         Map<Long, List<CoreOfficeToolbarLineBean>> toolbarLineMapList = toolbarLineList.stream().collect(Collectors.groupingBy(e -> e.getToolbarId()));
         toolbarLineMapList.forEach((k, v) -> {
            CoreOfficeToolbarDTO toolbarDTO = new CoreOfficeToolbarDTO();
            CoreOfficeToolbarBean officeToolbean = toolbarList.stream().filter(f -> k.equals(f.getId())).findFirst().get();
            toolbarDTO.setId(officeToolbean.getId());
            toolbarDTO.setMenuCategoryName(officeToolbean.getMenuCategoryName());
            toolbarDTO.setMenuCategoryCode(officeToolbean.getMenuCategoryCode());
            toolbarDTO.setLastSuspendedFlag(officeToolbean.getLastSuspendedFlag());
            toolbarDTO.setToolbarLineList((List<CoreOfficeToolbarLineBean>)v);
            officeToolbarList.add(toolbarDTO);
         });
      }

      return new Pagination<>(officeToolbarList);
   }

   @AuditTrailEntry(AuditTrailType.SUSPEND)
   @Audit("AUDIT.SUSPEND")
   @Override
   public void suspendLine(RestJsonWrapperBean wrapper) {
      this.coreOfficeToolbarLineService.suspend(wrapper);
   }

   @AuditTrailEntry(AuditTrailType.UNSUSPEND)
   @Audit("AUDIT.UNSUSPEND")
   @Override
   public void unsuspendLine(RestJsonWrapperBean wrapper) {
      this.coreOfficeToolbarLineService.unsuspend(wrapper);
   }
}
