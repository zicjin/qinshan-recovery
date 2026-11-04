package com.sunwayworld.cloud.module.office.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeDsDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsFieldService;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Pagination;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreOfficeDsServiceImpl implements CoreOfficeDsService {
   @Autowired
   private CoreOfficeDsDao officeDsDao;
   @Autowired
   private CoreOfficeDsFieldService dsFieldService;
   @Lazy
   @Autowired
   private CoreOfficeDsService proxyInstance;

   public CoreOfficeDsDao getDao() {
      return this.officeDsDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreOfficeDsBean coreOfficeDs = jsonWrapper.parseUnique(CoreOfficeDsBean.class);
      coreOfficeDs.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreOfficeDs);
      return coreOfficeDs.getId();
   }

   @Transactional
   @Override
   public RestValidationResultBean updateSqlIfValid(Long id, RestJsonWrapperBean wrapper) {
      CoreOfficeDsBean officeDs = wrapper.parseUnique(CoreOfficeDsBean.class);
      String sqlContent = officeDs.getSqlContent();
      List<CoreOfficeDsFieldBean> dsFieldList = this.dsFieldService
         .selectListByFilter(SearchFilter.instance().match("DSID", id).filter(MatchPattern.EQ), new Order[0]);
      if (StringUtils.isBlank(sqlContent)) {
         this.getDao().update(officeDs, new String[]{"SQLCONTENT"});
         if (!dsFieldList.isEmpty()) {
            this.dsFieldService.getDao().deleteByIdList(dsFieldList.stream().map(e -> e.getId()).collect(Collectors.toList()));
         }

         return new RestValidationResultBean(true);
      } else {
         sqlContent = sqlContent.replaceAll("('?#)[^#]*(#'?)", "NULL");
         List<CoreOfficeDsFieldBean> insertFieldList = new ArrayList<>();
         List<CoreOfficeDsFieldBean> deleteFieldList = new ArrayList<>();

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
            List<CoreOfficeDsFieldBean> oldFieldList = this.dsFieldService
               .selectListByFilter(SearchFilter.instance().match("DSID", id).filter(MatchPattern.SEQ), new Order[]{Order.desc("ID")});
            sqlColumnList.stream()
               .filter(
                  c -> !StringUtils.endsWith(c.getRawColumnName().toUpperCase(), "_ID")
                     && !StringUtils.equals(c.getRawColumnName().toUpperCase(), "ID")
                     && oldFieldList.stream().noneMatch(f -> {
                        String columnName = c.getRawColumnName();
                        if (columnName.indexOf("_") > 0) {
                           columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.length());
                        }

                        return f.getSqlColumnName().equalsIgnoreCase(columnName);
                     })
               )
               .forEach(c -> {
                  CoreOfficeDsFieldBean officeDsField = new CoreOfficeDsFieldBean();
                  officeDsField.setId(ApplicationContextHelper.getNextIdentity());
                  officeDsField.setDsId(id);
                  officeDsField.setOrderNo(officeDsField.getId());
                  String columnName = c.getRawColumnName();
                  if (columnName.indexOf("_") > 0) {
                     columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.length());
                  }

                  officeDsField.setSqlColumnName(columnName);
                  insertFieldList.add(officeDsField);
               });
            deleteFieldList.addAll(oldFieldList.stream().filter(f -> sqlColumnList.stream().noneMatch(c -> {
               String columnName = c.getRawColumnName();
               if (columnName.indexOf("_") > 0) {
                  columnName = columnName.substring(columnName.indexOf("_") + 1, columnName.length());
               }

               return columnName.equalsIgnoreCase(f.getSqlColumnName());
            })).collect(Collectors.toList()));
         } catch (JdbcException var10) {
            return new RestValidationResultBean(false, var10.getCause().getMessage());
         }

         this.dsFieldService.getDao().deleteBy(deleteFieldList);
         this.dsFieldService.getDao().insert(insertFieldList);
         this.getDao().updateIfChanged(officeDs);
         return new RestValidationResultBean(true);
      }
   }

   @Override
   public String selectTree() {
      CoreOfficeDsDTO officeDsDTO = this.proxyInstance.selectActivatableOfficeDsDTO();
      return JSON.toJSONString(officeDsDTO, SerializerFeature.WriteMapNullValue);
   }

   @Cacheable(
      value = {"T_CORE_OFFICE_DS.ACTIVATABLE-TREE"},
      key = "'ACTIVATABLE'"
   )
   @Override
   public CoreOfficeDsDTO selectActivatableOfficeDsDTO() {
      CoreOfficeDsDTO dsDto = new CoreOfficeDsDTO();
      List<CoreOfficeDsBean> dsList = this.selectListByFilter(
         SearchFilter.instance().match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ), new Order[]{Order.desc("ID")}
      );
      if (!dsList.isEmpty()) {
         List<Long> dsIdList = dsList.stream().map(CoreOfficeDsBean::getId).distinct().collect(Collectors.toList());
         List<CoreOfficeDsFieldBean> selectedDsFieldList = this.dsFieldService
            .selectListByFilter(SearchFilter.instance().match("DSID", dsIdList).filter(MatchPattern.OR), new Order[]{Order.asc("ORDERNO")});
         dsList.forEach(
            ds -> {
               List<CoreOfficeDsFieldBean> dsFieldList = selectedDsFieldList.stream()
                  .filter(field -> ds.getId().equals(field.getDsId()))
                  .collect(Collectors.toList());
               dsDto.addDs(ds, dsFieldList);
            }
         );
      }

      return dsDto;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertField(RestJsonWrapperBean wrapper) {
      return this.dsFieldService.insert(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteField(RestJsonWrapperBean wrapper) {
      this.dsFieldService.delete(wrapper);
   }

   @Override
   public Pagination<CoreOfficeDsFieldBean> selectFieldPaginationByDsId(Long id, RestJsonWrapperBean wrapper) {
      return (Pagination<CoreOfficeDsFieldBean>)this.dsFieldService
         .selectPaginationByFilter(SearchFilter.instance().match("DSID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void swapField(String string, Long firstId, Long secondId) {
      this.dsFieldService.swap(string, firstId, secondId);
   }
}
