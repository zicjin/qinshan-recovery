package com.sunwayworld.cloud.module.item.customds.service.impl;

import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlBean;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlDTO;
import com.sunwayworld.cloud.module.item.customds.bean.CoreCustomDsSqlFieldBean;
import com.sunwayworld.cloud.module.item.customds.persistent.dao.CoreCustomDsSqlDao;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsSqlFieldService;
import com.sunwayworld.cloud.module.item.customds.service.CoreCustomDsSqlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.context.ColumnContext;
import com.sunwayworld.framework.database.core.DatabaseManager;
import com.sunwayworld.framework.database.dialect.DialectRepository;
import com.sunwayworld.framework.database.sql.ExternalLogicalSql;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomDsSqlServiceImpl implements CoreCustomDsSqlService {
   private static Logger logger = LogManager.getLogger(CoreCustomDsSqlServiceImpl.class);
   @Autowired
   private CoreCustomDsSqlDao coreCustomDsSqlDao;
   @Autowired
   private CoreCustomDsSqlFieldService dsSqlFieldService;
   @Autowired
   @Lazy
   private GlobalMapper globalMapper;

   public CoreCustomDsSqlDao getDao() {
      return this.coreCustomDsSqlDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreCustomDsSqlBean coreCustomDsSql = jsonWrapper.parseUnique(CoreCustomDsSqlBean.class);
      coreCustomDsSql.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreCustomDsSql);
      Long dsSqlId = coreCustomDsSql.getId();
      this.updateDsSqlFieldListBySql(dsSqlId, coreCustomDsSql.getSqlContent());
      return dsSqlId;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   public void save(Long id, RestJsonWrapperBean wrapper) {
      CoreCustomDsSqlBean newDsSqlBean = wrapper.parseUnique(CoreCustomDsSqlBean.class);
      CoreCustomDsSqlBean oldDsSqlBean = this.selectById(id);
      this.getDao().updateIfChanged(newDsSqlBean);
      if (!StringUtils.isBlank(newDsSqlBean.getSqlContent()) && !StringUtils.equals(oldDsSqlBean.getSqlContent(), newDsSqlBean.getSqlContent())) {
         this.updateDsSqlFieldListBySql(id, newDsSqlBean.getSqlContent());
      }
   }

   @Override
   public RestValidationResultBean validateSql(RestJsonWrapperBean wrapper) {
      String sqlContent = wrapper.getParamValue("sqlContent");
      if (StringUtils.isBlank(sqlContent)) {
         return new RestValidationResultBean(true);
      } else {
         sqlContent = this.sqlReplaceParam(sqlContent, wrapper.extractMapFilter());
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT 1 FROM ( ").append(sqlContent).append(" ) T WHERE 1 = 2");

         try {
            this.globalMapper.selectList(sql.toString());
         } catch (Exception var5) {
            return new RestValidationResultBean(false, var5.getCause().getMessage());
         }

         return new RestValidationResultBean(true);
      }
   }

   @Override
   public List<CoreCustomDsSqlDTO> selectAllList(RestJsonWrapperBean wrapper) {
      List<CoreCustomDsSqlDTO> dsSqlDtoList = new ArrayList<>();
      List<CoreCustomDsSqlBean> allDsSqlList = this.getDao().selectAll(new Order[]{Order.desc("CREATEDTIME")});
      List<CoreCustomDsSqlFieldBean> allDsSqlFieldList = this.dsSqlFieldService.getDao().selectAll();
      allDsSqlList.forEach(
         dsSql -> {
            CoreCustomDsSqlDTO dsSqlDTO = new CoreCustomDsSqlDTO();
            List<CoreCustomDsSqlFieldBean> dsSqlFieldList = allDsSqlFieldList.stream()
               .filter(field -> dsSql.getId().equals(field.getDsSqlId()))
               .collect(Collectors.toList());
            dsSqlDTO.setId(dsSql.getId());
            dsSqlDTO.setSqlName(dsSql.getSqlName());
            dsSqlDTO.setSqlContent(dsSql.getSqlContent());
            dsSqlDTO.setColumnList(dsSqlFieldList);
            dsSqlDtoList.add(dsSqlDTO);
         }
      );
      return dsSqlDtoList;
   }

   @Override
   public List<Map<String, Object>> previewSql(Long id, RestJsonWrapperBean wrapper) {
      CoreCustomDsSqlBean dsSqlBean = this.getDao().selectById(id);
      String sqlContent = dsSqlBean.getSqlContent();
      sqlContent = this.sqlReplaceParam(sqlContent, wrapper.extractMapFilter());
      if (DialectRepository.getDialect().getDatabase().equals("sqlserver") && !SqlHelper.hasOrderSegment(sqlContent)) {
         sqlContent = sqlContent + " ORDER BY 1 ";
      }

      sqlContent = DialectRepository.getDialect().getLimitSql(sqlContent, 0, 30);
      new ArrayList();
      List<Map<String, Object>> resultMapListByUpperKey = new ArrayList<>();

      try {
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(sqlContent);
         List<Map<String, Object>> resultMapList = this.globalMapper.selectList(sqlContent);
         resultMapList.forEach(
            map -> {
               Map<String, Object> upperKeyMap = new HashMap<>();
               map.forEach(
                  (key, value) -> {
                     columnContextList.stream()
                        .filter(c -> LocalDateTime.class.equals(c.getType()) || LocalDate.class.equals(c.getType()))
                        .forEach(dataBaseColumn -> {
                           if (dataBaseColumn.getColumnName().equals(key) && value != null) {
                              if (LocalDate.class.equals(dataBaseColumn.getType())) {
                                 map.replace(key, DateTimeUtils.formatLocalDate(ConvertUtils.convert(value, LocalDate.class)));
                              } else if (LocalDateTime.class.equals(dataBaseColumn.getType())) {
                                 map.replace(key, DateTimeUtils.formatLocalDateTime(ConvertUtils.convert(value, LocalDateTime.class)));
                              }
                           }
                        });
                     upperKeyMap.put(key.toUpperCase(), map.get(key));
                  }
               );
               resultMapListByUpperKey.add(upperKeyMap);
            }
         );
         return resultMapListByUpperKey;
      } catch (Exception var8) {
         logger.info("自定义SQL" + dsSqlBean.getSqlName() + "的SQL:\n" + sqlContent);
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_SQLS.TIP.SQL_PREVIEW_ERROR") + dsSqlBean.getSqlName());
      }
   }

   private List<CoreCustomDsSqlFieldBean> updateDsSqlFieldListBySql(Long dsSqlId, String sqlContent) {
      List<CoreCustomDsSqlFieldBean> dsSqlFieldList = new ArrayList<>();
      sqlContent = this.sqlReplaceParam(sqlContent, null);
      List<CoreCustomDsSqlFieldBean> oldDsSqlFieldList = this.dsSqlFieldService
         .selectListByFilter(SearchFilter.instance().match("DSSQLID", dsSqlId).filter(MatchPattern.EQ), new Order[0]);
      List<CoreCustomDsSqlFieldBean> insertFieldList = new ArrayList<>();
      List<CoreCustomDsSqlFieldBean> deleteFieldList = new ArrayList<>();

      try {
         List<ColumnContext> columnContextList = DatabaseManager.selectColumnContextList(sqlContent);
         List<String> columnNameList = columnContextList.stream().map(c -> c.getColumnName().toUpperCase()).collect(Collectors.toList());
         if (!oldDsSqlFieldList.isEmpty()) {
            List<String> dsSqlFieldColumnList = oldDsSqlFieldList.stream().map(d -> d.getColumnName().toUpperCase()).collect(Collectors.toList());
            oldDsSqlFieldList.forEach(e -> {
               if (!columnNameList.contains(e.getColumnName().toUpperCase())) {
                  deleteFieldList.add(e);
               }
            });
            columnContextList.forEach(e -> {
               if (!dsSqlFieldColumnList.contains(e.getColumnName())) {
                  CoreCustomDsSqlFieldBean customDsSqlFieldBean = new CoreCustomDsSqlFieldBean();
                  customDsSqlFieldBean.setDsSqlId(dsSqlId);
                  customDsSqlFieldBean.setId(ApplicationContextHelper.getNextIdentity());
                  customDsSqlFieldBean.setColumnName(e.getColumnName());
                  insertFieldList.add(customDsSqlFieldBean);
               }
            });
         } else {
            columnContextList.forEach(e -> {
               CoreCustomDsSqlFieldBean customDsSqlFieldBean = new CoreCustomDsSqlFieldBean();
               customDsSqlFieldBean.setDsSqlId(dsSqlId);
               customDsSqlFieldBean.setId(ApplicationContextHelper.getNextIdentity());
               customDsSqlFieldBean.setColumnName(e.getColumnName().toUpperCase());
               insertFieldList.add(customDsSqlFieldBean);
            });
         }
      } catch (Exception var10) {
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.CUSTOM_DSES.TIP.SQL_ERROR") + sqlContent);
      }

      if (!insertFieldList.isEmpty()) {
         this.dsSqlFieldService.getDao().insert(insertFieldList);
      }

      if (!deleteFieldList.isEmpty()) {
         this.dsSqlFieldService.getDao().deleteByIdList(deleteFieldList.stream().map(e -> e.getId()).collect(Collectors.toList()));
      }

      return dsSqlFieldList;
   }

   private String sqlReplaceParam(String sqlContent, MapperParameter parameter) {
      sqlContent = sqlContent.replaceAll("('?#)[^#]*(#'?)", "NULL");
      if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.id}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sqlContent, "{loginUser.orgId}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sqlContent, "{loginOrgId}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sqlContent, "{loginRoleId}")) {
         sqlContent = StringUtils.replaceIgnoreCase(sqlContent, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
      }

      if (parameter != null) {
         for (String segment : StringUtils.substringsBetween(sqlContent, "/*", "*/")) {
            List<String> substringList = StringUtils.substringsBetween(segment, "{", "}");
            String sqlSegment = segment;
            boolean allReplaceFlag = true;

            for (String substring : substringList) {
               if (CollectionUtils.containsKeyIgnorecase(parameter, substring)) {
                  Object value = CollectionUtils.getValueIgnorecase(parameter, substring);
                  if (value != null && Number.class.isAssignableFrom(value.getClass())) {
                     sqlSegment = StringUtils.replace(sqlSegment, "${" + substring + "}", ObjectUtils.toString(value));
                  } else if (value == null) {
                     sqlSegment = StringUtils.replace(sqlSegment, "${" + substring + "}", "null");
                  } else if (ObjectUtils.toString(value).contains(",")) {
                     String[] valueList = ObjectUtils.toString(value).split(",");
                     sqlSegment = StringUtils.replace(
                        sqlSegment,
                        "${" + substring + "}",
                        StringUtils.join(Arrays.stream(valueList).map(item -> "'" + item + "'").collect(Collectors.toList()), ",")
                     );
                  } else {
                     sqlSegment = StringUtils.replace(sqlSegment, "${" + substring + "}", "'" + ObjectUtils.toString(value) + "'");
                  }
               } else {
                  allReplaceFlag = false;
               }
            }

            if (!substringList.isEmpty() && allReplaceFlag) {
               sqlContent = StringUtils.replace(sqlContent, "/*" + segment + "*/", ObjectUtils.toString(sqlSegment));
            }
         }
      }

      return ExternalLogicalSql.render(sqlContent);
   }
}
