package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesDsBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesDsFieldBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesDsDao;
import com.sunwayworld.cloud.module.les.service.CoreLesDsFieldService;
import com.sunwayworld.cloud.module.les.service.CoreLesDsService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLesDsServiceImpl implements CoreLesDsService {
   @Autowired
   private CoreLesDsDao coreLesDsDao;
   @Autowired
   private CoreLesDsFieldService dsFieldService;

   public CoreLesDsDao getDao() {
      return this.coreLesDsDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreLesDsBean lesDs = wrapper.parseUnique(CoreLesDsBean.class);
      lesDs.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(lesDs);
      return lesDs.getId();
   }

   @Transactional
   @Override
   public RestValidationResultBean updateSqlIfValid(Long id, RestJsonWrapperBean wrapper) {
      CoreLesDsBean lesDs = wrapper.parseUnique(CoreLesDsBean.class);
      String sqlContent = lesDs.getSqlContent();
      List<CoreLesDsFieldBean> dsFieldList = this.dsFieldService
         .selectListByFilter(SearchFilter.instance().match("LESDSID", id).filter(MatchPattern.EQ), new Order[0]);
      if (StringUtils.isBlank(sqlContent)) {
         this.getDao().update(lesDs, new String[]{"SQLCONTENT"});
         if (!dsFieldList.isEmpty()) {
            this.dsFieldService.getDao().deleteByIdList(dsFieldList.stream().map(e -> e.getId()).collect(Collectors.toList()));
         }

         return new RestValidationResultBean(true);
      } else {
         sqlContent = sqlContent.replaceAll("('?#)[^#]*(#'?)", "NULL");
         List<CoreLesDsFieldBean> insertFieldList = new ArrayList<>();
         List<CoreLesDsFieldBean> deleteFieldList = new ArrayList<>();

         try {
            if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.id}")) {
               sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.id}", "''");
            }

            if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.orgId}")) {
               sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.orgId}", "''");
            }

            if (StringUtils.containsIgnoreCase(sqlContent, "{loginOrgId}")) {
               sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginOrgId}", "''");
            }

            if (StringUtils.containsIgnoreCase(sqlContent, "{loginRoleId}")) {
               sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginRoleId}", "''");
            }

            List<ColumnContext> sqlColumnList = DatabaseManager.selectColumnContextList(sqlContent);
            List<String> columnNameList = sqlColumnList.stream().map(e -> e.getColumnName().toUpperCase()).collect(Collectors.toList());
            if (!dsFieldList.isEmpty()) {
               List<String> dsFieldColumnList = dsFieldList.stream().map(e -> e.getColumnName().toUpperCase()).collect(Collectors.toList());
               dsFieldList.forEach(e -> {
                  if (!columnNameList.contains(e.getColumnName().toUpperCase())) {
                     deleteFieldList.add(e);
                  }
               });
               columnNameList.forEach(e -> {
                  if (!dsFieldColumnList.contains(e)) {
                     CoreLesDsFieldBean lesDsField = new CoreLesDsFieldBean();
                     lesDsField.setId(ApplicationContextHelper.getNextIdentity());
                     lesDsField.setLesDsId(id);
                     lesDsField.setColumnName(e.toUpperCase());
                     lesDsField.setOrderNo(lesDsField.getId());
                     insertFieldList.add(lesDsField);
                  }
               });
            } else {
               columnNameList.forEach(e -> {
                  CoreLesDsFieldBean lesDsField = new CoreLesDsFieldBean();
                  lesDsField.setId(ApplicationContextHelper.getNextIdentity());
                  lesDsField.setLesDsId(id);
                  lesDsField.setColumnName(e.toUpperCase());
                  lesDsField.setOrderNo(lesDsField.getId());
                  insertFieldList.add(lesDsField);
               });
            }
         } catch (JdbcException var11) {
            return new RestValidationResultBean(false, var11.getCause().getMessage());
         }

         if (!insertFieldList.isEmpty()) {
            this.dsFieldService.getDao().insert(insertFieldList);
         }

         if (!deleteFieldList.isEmpty()) {
            this.dsFieldService.getDao().deleteByIdList(deleteFieldList.stream().map(e -> e.getId()).collect(Collectors.toList()));
         }

         this.getDao().update(lesDs, new String[]{"SQLCONTENT"});
         return new RestValidationResultBean(true);
      }
   }
}
