package com.sunwayworld.cloud.lims.reporttemplate.service.impl;

import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateSourceDao;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateColumnService;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.database.JdbcException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LimsReportTemplateSourceServiceImpl implements LimsReportTemplateSourceService {
   private static final Logger LOGGER = LoggerFactory.getLogger(LimsReportTemplateSourceServiceImpl.class);
   @Autowired
   @Lazy
   private LimsReportTemplateColumnService limsReportTemplateColumnService;
   @Autowired
   private LimsReportTemplateSourceDao limsReportTemplateSourceDao;

   public LimsReportTemplateSourceDao getDao() {
      return this.limsReportTemplateSourceDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LimsReportTemplateSourceBean limsReportTemplateSource = jsonWrapper.parseUnique(LimsReportTemplateSourceBean.class);
      limsReportTemplateSource.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(limsReportTemplateSource);
      return limsReportTemplateSource.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void refreshSourceColumns(Long sourceId) {
      LimsReportTemplateSourceBean sourceBean = this.selectById(sourceId);
      if (StringUtils.isEmpty(sourceBean.getSql())) {
         LimsReportTemplateColumnBean columnBean = new LimsReportTemplateColumnBean();
         columnBean.setTemplateSourceId(sourceId);
         this.limsReportTemplateColumnService.getDao().deleteBy(columnBean, "templateSourceId");
      } else {
         List<String> sqlColumnList = SqlHelper.getSqlColumnList(this.getReplacedSql(sourceBean.getSql()));
         List<LimsReportTemplateColumnBean> columnList = this.limsReportTemplateColumnService
            .selectListByFilter(SearchFilter.instance().match("templateSourceId", sourceId).filter(MatchPattern.EQ), new Order[0]);
         List<LimsReportTemplateColumnBean> deleteColumnList = columnList.stream()
            .filter(columnBean -> !CollectionUtils.containsIgnoreCase(sqlColumnList, columnBean.getColumnName()))
            .collect(Collectors.toList());
         List<String> existsColumnList = columnList.stream().map(LimsReportTemplateColumnBean::getColumnName).collect(Collectors.toList());
         List<LimsReportTemplateColumnBean> insertColumnList = sqlColumnList.stream()
            .filter(column -> !CollectionUtils.containsIgnoreCase(existsColumnList, column))
            .map(column -> {
               LimsReportTemplateColumnBean columnBean = new LimsReportTemplateColumnBean();
               columnBean.setId(ApplicationContextHelper.getNextIdentity());
               columnBean.setTemplateSourceId(sourceId);
               columnBean.setColumnName(column);
               String columnDesc = I18nHelper.getMessage(sourceBean.getSourceTable().toUpperCase() + "." + column.toUpperCase());
               columnBean.setColumnDesc(columnDesc);
               return columnBean;
            })
            .collect(Collectors.toList());
         if (!deleteColumnList.isEmpty()) {
            this.limsReportTemplateColumnService.getDao().deleteBy(deleteColumnList);
         }

         if (!insertColumnList.isEmpty()) {
            this.limsReportTemplateColumnService.getDao().insert(insertColumnList);
         }
      }
   }

   @Transactional
   @Override
   public void postUpdate(List<String> updatedColumnNameList, List<Long> idList) {
      if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "sql")) {
         Long sourceId = idList.get(0);
         this.refreshSourceColumns(sourceId);
      }
   }

   @Override
   public RestValidationResultBean validateTemplateSourceSql(String column, String value) {
      RestValidationResultBean validation = new RestValidationResultBean(true);
      if (StringUtils.isEmpty(value)) {
         return validation;
      } else {
         String sql = this.getReplacedSql(value);

         try {
            SqlHelper.getSqlColumnList(sql);
         } catch (JdbcException var6) {
            LOGGER.error("sql execute error", (Throwable)var6);
            validation.setValid(false);
            validation.setMessage("LIMS.MODULE.REPORTTEMPLATES.VALIDATE.SQL_ERROR");
         }

         return validation;
      }
   }

   @Override
   public List<Map<String, Object>> selectListBySql(String sql, MapperParameter parameter) {
      sql = this.getReplacedSql(sql);
      if (SqlHelper.hasOrderSegment(sql)) {
         parameter.put("sql", SqlHelper.removeEndingOrderIfPresent(sql));
         String[] orders = StringUtils.split(SqlHelper.getOrderSegment(sql), ",");

         for (String order : orders) {
            String trimedOrder = StringUtils.trim(order);
            if (StringUtils.endsWithIgnoreCase(trimedOrder, "ASC")) {
               parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "ASC")), "ASC");
            } else if (StringUtils.endsWithIgnoreCase(order.trim(), "DESC")) {
               parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "DESC")), "DESC");
            } else {
               parameter.setOrderParam(trimedOrder, "ASC");
            }
         }
      } else {
         parameter.put("sql", sql);
      }

      return this.getDao().selectListBySql(parameter);
   }

   private String getReplacedSql(String sql) {
      if (StringUtils.containsIgnoreCase(sql, "${loginUserId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "${loginUserId}", "'" + LocalContextHelper.getLoginUserId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "${loginOrgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "${loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "${loginRoleId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
      }

      return sql;
   }
}
