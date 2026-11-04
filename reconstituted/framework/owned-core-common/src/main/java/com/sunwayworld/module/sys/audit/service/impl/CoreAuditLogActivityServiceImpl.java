package com.sunwayworld.module.sys.audit.service.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;
import com.sunwayworld.module.sys.audit.dao.CoreAuditLogActivityDao;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogActivityService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditLogActivityServiceImpl implements CoreAuditLogActivityService {
   @Autowired
   private CoreAuditLogActivityDao logActivityDao;

   public CoreAuditLogActivityDao getDao() {
      return this.logActivityDao;
   }

   @Override
   public Page<CoreAuditLogActivityBean> selectQueryMorePagination(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      Page<Map<String, Object>> auditLogActivityPage = MybatisPageHelper.get(rowBounds, () -> this.getDao().selectAuditLogActivityByCondition(parameter));
      List<CoreAuditLogActivityBean> auditLogActivityList = auditLogActivityPage.getRows()
         .stream()
         .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, CoreAuditLogActivityBean.class))
         .collect(Collectors.toList());
      return new Pagination<>(auditLogActivityPage, auditLogActivityList);
   }
}
