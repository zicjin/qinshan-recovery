package com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.impl;

import com.gexin.fastjson.JSON;
import com.gexin.fastjson.JSONArray;
import com.gexin.fastjson.JSONObject;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CheckGroupDTO;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigFieldBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.CoreCodeGenerateConfigTableBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.SubTableTemplateAttrBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.bean.TemplateAttrBean;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.persistent.dao.CoreCodeGenerateConfigDao;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.CoreCodeGenerateConfigFieldService;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.CoreCodeGenerateConfigService;
import com.sunwayworld.cloud.module.codegenerator.codegenerateconfig.service.CoreCodeGenerateConfigTableService;
import com.sunwayworld.cloud.module.codegenerator.support.helper.AuditSqlHelper;
import com.sunwayworld.cloud.module.codegenerator.support.helper.DbMetaCheckHelper;
import com.sunwayworld.cloud.module.codegenerator.support.helper.SqlHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.SystemUtils;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigDTO;
import com.sunwayworld.module.sys.config.helper.SystemConfigHelper;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Repository
@GikamBean
public class CoreCodeGenerateConfigServiceImpl implements CoreCodeGenerateConfigService {
   private static final String separator = System.getProperty("file.separator");
   @Autowired
   private CoreCodeGenerateConfigDao configDao;
   private static final Log logger = LogFactory.getLog(CoreCodeGenerateConfigServiceImpl.class);
   @Autowired
   private CoreCodeGenerateConfigTableService tableService;
   @Autowired
   private CoreCodeGenerateConfigFieldService fieldService;

   public CoreCodeGenerateConfigDao getDao() {
      return this.configDao;
   }

   public CoreCodeGenerateConfigTableService getTableService() {
      return this.tableService;
   }

   public CoreCodeGenerateConfigFieldService getFieldService() {
      return this.fieldService;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public String insert(RestJsonWrapperBean wrapper) {
      CoreCodeGenerateConfigBean config = wrapper.parseUnique(CoreCodeGenerateConfigBean.class);
      String id = ApplicationContextHelper.getNextSequence(this.getDao().getTable());
      config.setId(id);
      this.getDao().insert(config);
      return id;
   }

   public CoreCodeGenerateConfigBean selectDetail(String id) {
      CoreCodeGenerateConfigBean codeGenerateConfig = CoreCodeGenerateConfigService.super.selectDetail(id);
      if (codeGenerateConfig != null && !StringUtils.isEmpty(codeGenerateConfig.getMenuIcon())) {
         codeGenerateConfig.setExt$Item("menuiconurl", this.getMenuIconUrl(codeGenerateConfig.getMenuIcon()));
      }

      return codeGenerateConfig;
   }

   @Transactional
   @Override
   public String codeGenerate(String id, RestJsonWrapperBean wrapper) {
      SqlHelper sqlHelper = new SqlHelper();
      CoreCodeGenerateConfigBean kbCodeGenerateConfig = this.getDao().selectById(id);
      List<CoreCodeGenerateConfigTableBean> tableList = this.getTableService()
         .selectListByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), new Order[]{Order.asc("id")});
      List<Long> tableIdList = tableList.stream().map(e -> e.getId()).collect(Collectors.toList());
      List<CoreCodeGenerateConfigFieldBean> fieldList = this.getFieldService()
         .selectListByFilter(SearchFilter.instance().match("TABLEID", tableIdList).filter(MatchPattern.OR), new Order[0]);
      this.checkData(kbCodeGenerateConfig, tableList, fieldList);
      String createSqlStr = "";
      String createMysqlStr = "";
      String createSqlServerStr = "";
      String createPgsqlStr = "";
      String i118n = "";
      String insertI118nOracleSqlStr = "";
      String insertI118nMysqlSqlStr = "";
      String insertI118nSqlserverSqlStr = "";
      String insertI118nPgsqlSqlStr = "";
      this.logger("***********************生成创建表语句开始");
      String packageName = (kbCodeGenerateConfig.getPackageName().replace("com.sunwayworld.", "") + "s").toUpperCase();
      String subTableTabI118n = "";
      String subTableTabI118nOracleSqlStr = "";
      String subTableTabI118nMysqlSqlStr = "";
      String subTableTabI118nSqlserverSqlStr = "";
      String subTableTabI118nPgsqlStr = "";
      String lineBreak = "\r\n";

      for (CoreCodeGenerateConfigTableBean table : tableList) {
         Long tableId = table.getId();
         List<CoreCodeGenerateConfigFieldBean> tableFieldList = fieldList.stream().filter(e -> tableId.equals(e.getTableId())).collect(Collectors.toList());
         if (tableFieldList.isEmpty()) {
            throw new CheckedException(table.getTableName() + "表没有添加字段");
         }

         createSqlStr = createSqlStr + sqlHelper.createOracleSql(table, tableFieldList);
         createMysqlStr = createMysqlStr + sqlHelper.createMysqlSql(table, tableFieldList);
         createSqlServerStr = createSqlServerStr + sqlHelper.createSqlServer(table, tableFieldList);
         createPgsqlStr = createPgsqlStr + sqlHelper.createPgsqlSql(table, tableFieldList);
         insertI118nOracleSqlStr = insertI118nOracleSqlStr + sqlHelper.createOracleI118nSql(kbCodeGenerateConfig, table, tableFieldList);
         insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + sqlHelper.createMysqlI118nSql(kbCodeGenerateConfig, table, tableFieldList);
         insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr + sqlHelper.createSqlserverI118nSql(kbCodeGenerateConfig, table, tableFieldList);
         insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + sqlHelper.createPgsqlI118nSql(kbCodeGenerateConfig, table, tableFieldList);
         i118n = i118n + sqlHelper.createI118n(kbCodeGenerateConfig, table, tableFieldList);
         if (!"main".equals(table.getType())) {
            String[] subTableNameStrArray = table.getTableName().toLowerCase().split("_");
            List<String> subTableNameList = ArrayUtils.asList(subTableNameStrArray).subList(3, subTableNameStrArray.length);
            subTableTabI118n = subTableTabI118n
               + packageName
               + ".TAB."
               + StringUtils.join(subTableNameList, "_").toUpperCase()
               + "="
               + table.getTableDesc()
               + lineBreak;
            subTableTabI118nOracleSqlStr = subTableTabI118nOracleSqlStr
               + "CALL INSERTI18N('"
               + packageName
               + ".TAB."
               + StringUtils.join(subTableNameList, "_").toUpperCase()
               + "', '"
               + table.getTableDesc()
               + "', '"
               + table.getTableDesc()
               + "', 'zh-CN', '"
               + table.getTableDesc()
               + "');\r\n";
            subTableTabI118nMysqlSqlStr = subTableTabI118nMysqlSqlStr
               + "CALL INSERTI18N('"
               + packageName
               + ".TAB."
               + StringUtils.join(subTableNameList, "_").toUpperCase()
               + "', '"
               + table.getTableDesc()
               + "', '"
               + table.getTableDesc()
               + "', 'zh-CN', '"
               + table.getTableDesc()
               + "');\r\n";
            subTableTabI118nSqlserverSqlStr = subTableTabI118nSqlserverSqlStr
               + "EXEC  DBO.INSERTI18N  N'"
               + packageName
               + ".TAB."
               + StringUtils.join(subTableNameList, "_").toUpperCase()
               + "', N'"
               + table.getTableDesc()
               + "', N'"
               + table.getTableDesc()
               + "', N'zh-CN',N'"
               + table.getTableDesc()
               + "';\r\n";
            subTableTabI118nPgsqlStr = subTableTabI118nPgsqlStr
               + "CALL INSERTI18N('"
               + packageName
               + ".TAB."
               + StringUtils.join(subTableNameList, "_").toUpperCase()
               + "', '"
               + table.getTableDesc()
               + "', '"
               + table.getTableDesc()
               + "', 'zh-CN', '"
               + table.getTableDesc()
               + "');\r\n";
         }

         StringBuilder oracleAuditSqlBuilder = new StringBuilder();
         StringBuilder mysqlAuditSqlBuilder = new StringBuilder();
         StringBuilder sqlserverAuditSqlBuilder = new StringBuilder();
         StringBuilder pgsqlAuditSqlBuilder = new StringBuilder();
         String tableName = table.getTableName().toUpperCase();
         String tableDesc = table.getTableDesc();
         String author = kbCodeGenerateConfig.getAuthor();
         List<String> fields = tableFieldList.stream().map(CoreCodeGenerateConfigFieldBean::getField).map(String::toUpperCase).collect(Collectors.toList());
         AuditSqlHelper.generateAuditTrailSql(
            tableName,
            author,
            author,
            fields,
            Collections.emptyList(),
            oracleAuditSqlBuilder,
            mysqlAuditSqlBuilder,
            sqlserverAuditSqlBuilder,
            pgsqlAuditSqlBuilder
         );
         AuditSqlHelper.generateAuditTrailConfigSql(
            tableName, tableDesc, author, author, oracleAuditSqlBuilder, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder
         );
         AuditSqlHelper.generateAuditV2Sql(
            tableName, fields, Collections.emptyList(), oracleAuditSqlBuilder, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder
         );
         AuditSqlHelper.generateAuditV2ConfigSql(
            tableName, tableDesc, oracleAuditSqlBuilder, mysqlAuditSqlBuilder, sqlserverAuditSqlBuilder, pgsqlAuditSqlBuilder
         );
         createSqlStr = createSqlStr + oracleAuditSqlBuilder;
         createMysqlStr = createMysqlStr + mysqlAuditSqlBuilder;
         createSqlServerStr = createSqlServerStr + sqlserverAuditSqlBuilder;
         createPgsqlStr = createPgsqlStr + pgsqlAuditSqlBuilder;
      }

      i118n = i118n + packageName + ".MODAL.ADD=新增" + lineBreak;
      i118n = i118n + packageName + ".TAB.BASE_INFO=基本信息" + lineBreak;
      i118n = i118n + subTableTabI118n;
      i118n = i118n + packageName + ".BUTTON.BUTTON=按钮" + lineBreak;
      i118n = i118n + packageName + ".TIP.TIP=提示框" + lineBreak;
      i118n = i118n + packageName + ".MODAL.MODAL=对话框" + lineBreak;
      i118n = i118n + lineBreak;
      insertI118nOracleSqlStr = insertI118nOracleSqlStr + "CALL INSERTI18N('" + packageName + ".MODAL.ADD', '新增', '新增', 'zh-CN', '新增');\r\n";
      insertI118nOracleSqlStr = insertI118nOracleSqlStr + "CALL INSERTI18N('" + packageName + ".TAB.BASE_INFO', '基本信息', '基本信息', 'zh-CN', '基本信息');\r\n";
      insertI118nOracleSqlStr = insertI118nOracleSqlStr + subTableTabI118nOracleSqlStr;
      insertI118nOracleSqlStr = insertI118nOracleSqlStr + "CALL INSERTI18N('" + packageName + ".BUTTON.BUTTON', '按钮', '按钮', 'zh-CN', '按钮');\r\n";
      insertI118nOracleSqlStr = insertI118nOracleSqlStr + "CALL INSERTI18N('" + packageName + ".TIP.TIP', '提示框', '提示框', 'zh-CN', '提示框');\r\n";
      insertI118nOracleSqlStr = insertI118nOracleSqlStr + "CALL INSERTI18N('" + packageName + ".MODAL.MODAL', '对话框', '对话框', 'zh-CN', '对话框');\r\n";
      insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + "CALL INSERTI18N('" + packageName + ".MODAL.ADD', '新增', '新增', 'zh-CN', '新增');\r\n";
      insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + "CALL INSERTI18N('" + packageName + ".TAB.BASE_INFO', '基本信息', '基本信息', 'zh-CN', '基本信息');\r\n";
      insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + subTableTabI118nMysqlSqlStr;
      insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + "CALL INSERTI18N('" + packageName + ".BUTTON.BUTTON', '按钮', '按钮', 'zh-CN', '按钮');\r\n";
      insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + "CALL INSERTI18N('" + packageName + ".TIP.TIP', '提示框', '提示框', 'zh-CN', '提示框');\r\n";
      insertI118nMysqlSqlStr = insertI118nMysqlSqlStr + "CALL INSERTI18N('" + packageName + ".MODAL.MODAL', '对话框', '对话框', 'zh-CN', '对话框');\r\n";
      insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + "CALL INSERTI18N('" + packageName + ".MODAL.ADD', '新增', '新增', 'zh-CN', '新增');\r\n";
      insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + "CALL INSERTI18N('" + packageName + ".TAB.BASE_INFO', '基本信息', '基本信息', 'zh-CN', '基本信息');\r\n";
      insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + subTableTabI118nPgsqlStr;
      insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + "CALL INSERTI18N('" + packageName + ".BUTTON.BUTTON', '按钮', '按钮', 'zh-CN', '按钮');\r\n";
      insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + "CALL INSERTI18N('" + packageName + ".TIP.TIP', '提示框', '提示框', 'zh-CN', '提示框');\r\n";
      insertI118nPgsqlSqlStr = insertI118nPgsqlSqlStr + "CALL INSERTI18N('" + packageName + ".MODAL.MODAL', '对话框', '对话框', 'zh-CN', '对话框');\r\n";
      insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr + "EXEC  DBO.INSERTI18N  N'" + packageName + ".MODAL.ADD', N'新增', N'新增', N'zh-CN',N'新增';\r\n";
      insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr
         + "EXEC  DBO.INSERTI18N  N'"
         + packageName
         + ".TAB.BASE_INFO', N'基本信息', N'基本信息', N'zh-CN',N'基本信息';\r\n";
      insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr + subTableTabI118nSqlserverSqlStr;
      insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr + "EXEC  DBO.INSERTI18N  N'" + packageName + ".BUTTON.BUTTON', N'按钮', N'按钮', N'zh-CN',N'按钮';\r\n";
      insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr + "EXEC  DBO.INSERTI18N  N'" + packageName + ".TIP.TIP', N'提示框', N'提示框', N'zh-CN',N'提示框';\r\n";
      insertI118nSqlserverSqlStr = insertI118nSqlserverSqlStr
         + "EXEC  DBO.INSERTI18N  N'"
         + packageName
         + ".MODAL.MODAL', N'对话框', N'对话框', N'zh-CN',N'对话框';\r\n";
      createSqlStr = createSqlStr + insertI118nOracleSqlStr;
      createMysqlStr = createMysqlStr + insertI118nMysqlSqlStr;
      createSqlServerStr = createSqlServerStr + insertI118nSqlserverSqlStr;
      createPgsqlStr = createPgsqlStr + insertI118nPgsqlSqlStr;
      this.logger("***********************生成创建表语句结束");
      String templatePath = "";
      String templateName = "";
      this.logger("***********************生成文件开始");
      String[] templateArray = new String[]{
         "static\\tmpl\\html\\template-edit-list.html",
         "static\\tmpl\\java\\mapper\\conf\\template-sqlserver-mapper.xml",
         "static\\tmpl\\js\\template-edit-list.js",
         "static\\tmpl\\java\\bean\\templateBean.java",
         "static\\tmpl\\java\\resource\\templateResource.java",
         "static\\tmpl\\js\\template-choose-list.js",
         "static\\tmpl\\i118n\\template-i118n.txt",
         "static\\tmpl\\java\\mapper\\conf\\template-oracle-mapper.xml",
         "static\\tmpl\\js\\template-detail.js",
         "static\\tmpl\\html\\template-choose-list.html",
         "static\\tmpl\\java\\dao\\templateDao.java",
         "static\\tmpl\\java\\service\\impl\\templateServiceImpl.java",
         "static\\tmpl\\js\\template-search-list.js",
         "static\\tmpl\\html\\template-audit-list.html",
         "static\\tmpl\\java\\controller\\templateController.java",
         "static\\tmpl\\js\\template-audit-list.js",
         "static\\tmpl\\sql\\template-sqlserver-sql.sql",
         "static\\tmpl\\html\\template-search-list.html",
         "static\\tmpl\\java\\mapper\\conf\\template-mysql-mapper.xml",
         "static\\tmpl\\sql\\template-oracle-sql.sql",
         "static\\tmpl\\java\\dao\\impl\\templateDaoImpl.java",
         "static\\tmpl\\java\\controller\\impl\\templateControllerImpl.java",
         "static\\tmpl\\java\\mapper\\templateMapper.java",
         "static\\tmpl\\java\\service\\templateService.java",
         "static\\tmpl\\js\\template.js",
         "static\\tmpl\\html\\template-detail.html",
         "static\\tmpl\\java\\resource\\impl\\templateResourceImpl.java",
         "static\\tmpl\\sql\\template-mysql-sql.sql",
         "static\\tmpl\\sql\\template-postgresql-sql.sql",
         "static\\tmpl\\java\\mapper\\conf\\template-postgresql-mapper.xml"
      };
      List<String> templateList = Arrays.asList(templateArray);
      if (SystemUtils.isLinux()) {
         templateList = templateList.stream().map(e -> e.replace("\\", "/")).collect(Collectors.toList());
      }

      List<File> fileList = new ArrayList<>();
      Path tempPath = FilePathManager.getLocalPath(
         FilePathDTO.of(
            FileScope.temp.name(), "" + DateTimeUtils.getDateSerialNumber(LocalDateTime.now()), StringUtils.randomUUID(16), StringUtils.randomUUID(16)
         )
      );
      String rootPath = this.queryPath(
         new String[]{
            tempPath.getParent().toString(), kbCodeGenerateConfig.getCreatedById(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
         }
      );
      List<CoreCodeGenerateConfigTableBean> subTableList = tableList.stream().filter(e -> !"main".equals(e.getType())).collect(Collectors.toList());

      for (String templateAll : templateList) {
         templatePath = templateAll.substring(0, templateAll.lastIndexOf(separator));
         templateName = templateAll.substring(templateAll.lastIndexOf(separator) + 1);
         this.analysisMainWithSub(
            templatePath,
            templateName,
            kbCodeGenerateConfig,
            tableList,
            createSqlStr,
            createMysqlStr,
            createSqlServerStr,
            createPgsqlStr,
            i118n,
            fileList,
            rootPath,
            this.isStartInJar(),
            fieldList,
            subTableList
         );
         this.logger("***********************调用" + templateName + "模板完成");
      }

      FilePathDTO fr = FilePathDTO.of(FileScope.temp.name(), DateTimeUtils.getDateSerialNumber(LocalDateTime.now()), StringUtils.randomUUID(16), "文件打包.zip");
      ArchiveUtils.zip(FilePathManager.getLocalPath(fr).toFile(), new File(rootPath));
      this.logger("***********************生成文件成功");
      String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
      return ServletUtils.getUrl(url, FilePathManager.getUrl(fr));
   }

   private void checkData(
      CoreCodeGenerateConfigBean kbCodeGenerateConfig, List<CoreCodeGenerateConfigTableBean> tableList, List<CoreCodeGenerateConfigFieldBean> fieldList
   ) {
      this.checkLength(tableList, fieldList);
      this.checkAuditField(kbCodeGenerateConfig, tableList, fieldList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertTable(String id, RestJsonWrapperBean wrapper) {
      List<CoreCodeGenerateConfigTableBean> tableList = wrapper.parse(CoreCodeGenerateConfigTableBean.class);
      tableList.forEach(e -> {
         e.setConfigId(id);
         e.setId(ApplicationContextHelper.getNextIdentity());
      });
      this.getTableService().getDao().insert(tableList);
      return tableList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteTable(RestJsonWrapperBean wrapper) {
      this.getTableService().delete(wrapper);
   }

   @Override
   public Page<CoreCodeGenerateConfigTableBean> selectTablePaginationByConfigId(String id, RestJsonWrapperBean wrapper) {
      return this.getTableService().selectPaginationByFilter(SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertField(RestJsonWrapperBean wrapper) {
      List<CoreCodeGenerateConfigFieldBean> fieldList = wrapper.parse(CoreCodeGenerateConfigFieldBean.class);
      fieldList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
      this.getFieldService().getDao().insert(fieldList);
      return fieldList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteField(RestJsonWrapperBean wrapper) {
      this.getFieldService().delete(wrapper);
   }

   @Override
   public Page<CoreCodeGenerateConfigFieldBean> selectFieldPagination(RestJsonWrapperBean wrapper) {
      return this.getFieldService().selectPagination(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Override
   public void swapField(Long firstId, Long secondId) {
      this.getFieldService().swap("ORDERNO", firstId, secondId);
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public String copy(String id) {
      CoreCodeGenerateConfigBean coreCodeGenerateConfigBean = this.selectDetail(id);
      if (coreCodeGenerateConfigBean == null) {
         return null;
      } else {
         SearchFilter tableFilter = SearchFilter.instance().match("CONFIGID", id).filter(MatchPattern.EQ);
         Order tableOrder = Order.asc("id");
         List<CoreCodeGenerateConfigTableBean> tableList = this.getTableService().selectListByFilter(tableFilter, new Order[]{tableOrder});
         List<Long> tableIdList = tableList.stream().map(CoreCodeGenerateConfigTableBean::getId).collect(Collectors.toList());
         SearchFilter fieldFilter = SearchFilter.instance().match("TABLEID", tableIdList).filter(MatchPattern.OR);
         List<CoreCodeGenerateConfigFieldBean> fieldList = this.getFieldService().selectListByFilter(fieldFilter, new Order[0]);
         CoreCodeGenerateConfigBean copyConfigBean = new CoreCodeGenerateConfigBean();
         BeanUtils.copyProperties(coreCodeGenerateConfigBean, copyConfigBean);
         String table = this.getDao().getTable();
         copyConfigBean.setId(ApplicationContextHelper.getNextSequence(table));
         if (copyConfigBean.getMenuName() != null) {
            copyConfigBean.setMenuName(copyConfigBean.getMenuName() + " copy");
         }

         if (copyConfigBean.getPackageName() != null) {
            copyConfigBean.setPackageName(copyConfigBean.getPackageName() + " copy");
         }

         this.getDao().insert(copyConfigBean);
         Map<Long, Long> tableIdCopyTableIdMap = new HashMap<>(tableList.size());
         List<CoreCodeGenerateConfigTableBean> copyTables = tableList.stream().map(tableBean -> {
            CoreCodeGenerateConfigTableBean copyTableBean = new CoreCodeGenerateConfigTableBean();
            BeanUtils.copyProperties(tableBean, copyTableBean);
            copyTableBean.setConfigId(copyConfigBean.getId());
            copyTableBean.setId(ApplicationContextHelper.getNextIdentity());
            tableIdCopyTableIdMap.put(tableBean.getId(), copyTableBean.getId());
            return copyTableBean;
         }).collect(Collectors.toList());
         this.getTableService().getDao().insert(copyTables);
         List<CoreCodeGenerateConfigFieldBean> copyFields = fieldList.stream().map(fieldBean -> {
            CoreCodeGenerateConfigFieldBean copyFieldBean = new CoreCodeGenerateConfigFieldBean();
            BeanUtils.copyProperties(fieldBean, copyFieldBean);
            copyFieldBean.setId(ApplicationContextHelper.getNextIdentity());
            Long copyTableId = tableIdCopyTableIdMap.get(fieldBean.getTableId());
            copyFieldBean.setTableId(copyTableId);
            return copyFieldBean;
         }).collect(Collectors.toList());
         this.getFieldService().getDao().insert(copyFields);
         return copyConfigBean.getId();
      }
   }

   private void analysisMainWithSub(
      String templatePath,
      String templateName,
      CoreCodeGenerateConfigBean kbCodeGenerateConfig,
      List<CoreCodeGenerateConfigTableBean> tableList,
      String createSqlStr,
      String createMysqlStr,
      String createSqlServerStr,
      String createPgsqlStr,
      String i118n,
      List<File> fileList,
      String rootPath,
      boolean isStartInJar,
      List<CoreCodeGenerateConfigFieldBean> fieldList,
      List<CoreCodeGenerateConfigTableBean> subTableList
   ) {
      SqlHelper sqlHelper = new SqlHelper();
      String packageName = kbCodeGenerateConfig.getPackageName();
      List<CheckGroupDTO> checkGroupDTOList = JsonUtils.parseList(kbCodeGenerateConfig.getFunc(), CheckGroupDTO.class);
      List<String> funcList = checkGroupDTOList.stream().filter(e -> "1".equals(e.getChecked())).map(e -> e.getValue()).collect(Collectors.toList());
      String tableName = "";
      String fileName = "";
      String business = "";
      String extension = templateName.substring(templateName.indexOf(".") + 1);
      String filePath = "";
      String xmlPath = "";
      String tableDesc = "";
      String[] arryFont = new String[]{rootPath, "src", "main", "resources"};
      String[] arryJava = new String[]{rootPath, "src", "main", "java"};
      if (!"js".equals(extension) && !"html".equals(extension)
         || !funcList.stream().noneMatch(e -> templateName.contains(e))
         || "template.js".equals(templateName)) {
         if (!tableList.isEmpty()) {
            String frontPathSuffix = "";

            for (int j = 0; j < tableList.size(); j++) {
               Map<String, Object> mapper = new HashMap<>();
               TemplateAttrBean templateAttr = new TemplateAttrBean();
               templateAttr.setAuthor(kbCodeGenerateConfig.getAuthor());
               templateAttr.setCreate("0");
               templateAttr.setAudit("0");
               templateAttr.setDateFlag("0");
               templateAttr.setDateTime("0");
               templateAttr.setDate("0");
               CoreCodeGenerateConfigTableBean table = tableList.get(j);
               Long tableId = table.getId();
               tableDesc = table.getTableDesc();
               tableName = table.getTableName().toUpperCase();
               List<CoreCodeGenerateConfigFieldBean> tableFieldList = fieldList.stream()
                  .filter(e -> tableId.equals(e.getTableId()))
                  .collect(Collectors.toList());
               if (!tableFieldList.isEmpty()) {
                  for (CoreCodeGenerateConfigFieldBean field : tableFieldList) {
                     if ("id".equals(field.getField().toLowerCase())) {
                        templateAttr.setIdFieldName(field.getFieldName());
                        templateAttr.setIdFieldType(field.getFieldType());
                     }

                     if ("processstatus".equals(field.getField().toLowerCase())) {
                        templateAttr.setAudit("1");
                     }

                     if ("createdbyid".equals(field.getField().toLowerCase())) {
                        templateAttr.setCreate("1");
                     }

                     if (StringUtils.containsIgnoreCase(field.getFieldType(), "date")) {
                        templateAttr.setDateFlag("1");
                     }

                     if ("date".equals(field.getFieldType())) {
                        templateAttr.setDate("1");
                     }

                     if ("datetime".equals(field.getFieldType())) {
                        templateAttr.setDateTime("1");
                     }

                     field.setExt$Item("i118n", tableName + "." + field.getField().toUpperCase());
                     this.setFieldValidator(field);
                  }
               }

               templateAttr.setTableName(tableName);
               tableFieldList = tableFieldList.stream().map(field -> {
                  if ("number".equals(field.getFieldType()) && !StringUtils.isEmpty(field.getFieldLength()) && field.getFieldLength().indexOf(",") != -1) {
                     field.setFieldType("Double");
                  }

                  return (CoreCodeGenerateConfigFieldBean)field;
               }).collect(Collectors.toList());
               if ("main".equals(table.getType())) {
                  templateAttr.setFuncList(funcList);
                  if (funcList.contains("detail")) {
                     templateAttr.setLayout("detail");
                  } else {
                     templateAttr.setLayout("leftRight");
                  }

                  String thirdLevelMenuId = kbCodeGenerateConfig.getMenuId().toString();
                  String parentMenuId = kbCodeGenerateConfig.getParentMenuId();
                  business = tableName.toLowerCase().replaceFirst("t_", "");
                  frontPathSuffix = business.replace("_", "-");
                  frontPathSuffix = frontPathSuffix.substring(frontPathSuffix.indexOf("-") + 1);
                  String frontPath = "s";
                  if ("js".equals(extension)) {
                     filePath = this.queryPath(arryFont)
                        + packageName.substring(0, packageName.lastIndexOf(".") + 1)
                           .concat(frontPathSuffix)
                           .toLowerCase()
                           .replace("com.sunwayworld", "static")
                           .replace(".", separator)
                        + frontPath;
                     fileName = templateName.replace("template", filePath + separator + business.replace("_", "-"));
                  } else if ("html".equals(extension)) {
                     filePath = this.queryPath(arryFont)
                        + packageName.substring(0, packageName.lastIndexOf(".") + 1)
                           .concat(frontPathSuffix)
                           .toLowerCase()
                           .replace("com.sunwayworld", "templates")
                           .replace(".", separator)
                        + frontPath;
                     fileName = templateName.replace("template", filePath + separator + business.replace("_", "-"));
                  } else if ("java".equals(extension)) {
                     filePath = this.queryPath(arryJava)
                        + packageName.toLowerCase().replace(".", separator)
                        + templatePath.substring(templatePath.indexOf("java") + 4);
                     fileName = templateName.replace("template", filePath + separator + this.firstUppercase(business)[1]);
                  } else if ("xml".equals(extension)) {
                     filePath = this.queryPath(arryJava)
                        + packageName.toLowerCase().replace(".", separator)
                        + templatePath.substring(templatePath.indexOf("java") + 4);
                     fileName = templateName.replace("template", filePath + separator + business.replace("_", "-"));
                  } else if ("sql".equals(extension)) {
                     filePath = this.queryPath(arryJava) + packageName.toLowerCase().replace(".", separator) + separator + "sql";
                     fileName = templateName.replace("template", filePath + separator + this.firstUppercase(business)[0]);
                  } else if ("txt".equals(extension)) {
                     filePath = this.queryPath(arryJava) + packageName.toLowerCase().replace(".", separator) + separator + "i118n";
                     fileName = templateName.replace("template", filePath + separator + this.firstUppercase(business)[0]);
                  }

                  xmlPath = this.queryPath(arryFont)
                     + (packageName.substring(0, packageName.lastIndexOf(".") + 1) + frontPathSuffix + "s")
                        .replace("com.sunwayworld", "templates")
                        .replace(".", separator);
                  String fourHref = packageName.substring(0, packageName.lastIndexOf(".") + 1)
                        .replace("com.sunwayworld.", "")
                        .concat(frontPathSuffix)
                        .replace(".", "/")
                     + 's';
                  String menuSqlStr = sqlHelper.insertMenu(
                     thirdLevelMenuId,
                     kbCodeGenerateConfig.getMenuName(),
                     "/secure/" + fourHref + "/page/edit-list",
                     parentMenuId,
                     thirdLevelMenuId,
                     kbCodeGenerateConfig.getMenuIcon(),
                     kbCodeGenerateConfig.getMenuName()
                  );
                  if (funcList.stream().anyMatch(e -> "audit".equals(e))) {
                     String auditMenuId = thirdLevelMenuId.substring(0, thirdLevelMenuId.length() - 2) + "02";
                     menuSqlStr = menuSqlStr
                        + sqlHelper.insertMenu(
                           auditMenuId,
                           kbCodeGenerateConfig.getMenuName() + "审核",
                           "/secure/" + fourHref + "/page/audit-list",
                           parentMenuId,
                           auditMenuId,
                           kbCodeGenerateConfig.getMenuIcon(),
                           kbCodeGenerateConfig.getMenuName() + "审核"
                        );
                  }

                  if (funcList.stream().anyMatch(e -> "search".equals(e))) {
                     String searchMenuId = thirdLevelMenuId.substring(0, thirdLevelMenuId.length() - 2) + "03";
                     menuSqlStr = menuSqlStr
                        + sqlHelper.insertMenu(
                           searchMenuId,
                           kbCodeGenerateConfig.getMenuName() + "查询",
                           "/secure/" + fourHref + "/page/search-list",
                           parentMenuId,
                           searchMenuId,
                           kbCodeGenerateConfig.getMenuIcon(),
                           kbCodeGenerateConfig.getMenuName() + "查询"
                        );
                  }

                  menuSqlStr = menuSqlStr + "\r\n" + sqlHelper.insertSysTable(tableList, thirdLevelMenuId, templateAttr.getAudit());
                  if ("1".equals(templateAttr.getAudit())) {
                     menuSqlStr = menuSqlStr
                        + "\r\nINSERT INTO T_CORE_MENU_EXT (ID, SERVICENAME, TABLENAME, TYPE, ENABLE)\r\nVALUES ('"
                        + thirdLevelMenuId
                        + "', '"
                        + kbCodeGenerateConfig.getMenuName()
                        + "', '"
                        + tableName
                        + "', 'audit_def', '1');\r\n";
                  }

                  templateAttr.setCreateMenuSql(menuSqlStr);
                  templateAttr.setEditMenuId(thirdLevelMenuId + "01");
                  templateAttr.setAuditMenuId(thirdLevelMenuId + "02");
                  List<SubTableTemplateAttrBean> subTableTemplateAttrList = subTableList.stream()
                     .map(
                        e -> {
                           SubTableTemplateAttrBean subTableTemplateAttr = new SubTableTemplateAttrBean();
                           String subTableName = e.getTableName();
                           String subTableBusiness = subTableName.toLowerCase().replaceFirst("t_", "");
                           String[] subTableNameStrArray = subTableName.toLowerCase().split("_");
                           List<String> subTableNameList = ArrayUtils.asList(subTableNameStrArray).subList(3, subTableNameStrArray.length);
                           String tableObjectStrx = StringUtils.join(subTableNameList, "_");
                           subTableTemplateAttr.setI118n(tableObjectStrx.toUpperCase());
                           subTableTemplateAttr.setTableObject(this.firstUppercase(tableObjectStrx)[1]);
                           subTableTemplateAttr.setPath(StringUtils.join(subTableNameList, "-"));
                           subTableTemplateAttr.setSubTableName(subTableName);
                           subTableTemplateAttr.setJavaObjectName(this.firstUppercase(subTableBusiness)[1]);
                           subTableTemplateAttr.setForeignField(e.getForeignField());
                           subTableTemplateAttr.setSubTableDesc(e.getTableDesc());
                           subTableTemplateAttr.setTableDesc(table.getTableDesc());
                           List<CoreCodeGenerateConfigFieldBean> subTableFieldList = fieldList.stream()
                              .filter(f -> e.getId().equals(f.getTableId()))
                              .collect(Collectors.toList());
                           if (!subTableFieldList.isEmpty()) {
                              for (CoreCodeGenerateConfigFieldBean subTableField : subTableFieldList) {
                                 subTableField.setExt$Item("i118n", e.getTableName().toUpperCase() + "." + subTableField.getField().toUpperCase());
                                 this.setFieldValidator(subTableField);
                              }
                           }

                           subTableTemplateAttr.setFields(subTableFieldList);
                           return subTableTemplateAttr;
                        }
                     )
                     .collect(Collectors.toList());
                  templateAttr.setSubTableTemplateAttrList(subTableTemplateAttrList);
               } else {
                  String[] templateNameArray = new String[]{
                     "templateBean.java",
                     "templateDao.java",
                     "templateDaoImpl.java",
                     "templateMapper.java",
                     "template-oracle-mapper.xml",
                     "template-mysql-mapper.xml",
                     "template-sqlserver-mapper.xml",
                     "templateService.java",
                     "templateServiceImpl.java"
                  };
                  if (!Arrays.asList(templateNameArray).contains(templateName)) {
                     continue;
                  }

                  business = tableName.toLowerCase().replaceFirst("t_", "");
                  String frontPathx = "s";
                  if ("java".equals(extension)) {
                     filePath = this.queryPath(arryJava)
                        + packageName.toLowerCase().replace(".", separator)
                        + templatePath.substring(templatePath.indexOf("java") + 4);
                     fileName = templateName.replace("template", filePath + separator + this.firstUppercase(business)[1]);
                  } else if ("xml".equals(extension)) {
                     filePath = this.queryPath(arryJava)
                        + packageName.toLowerCase().replace(".", separator)
                        + templatePath.substring(templatePath.indexOf("java") + 4);
                     fileName = templateName.replace("template", filePath + separator + business.replace("_", "-"));
                  } else if ("js".equals(extension)) {
                     filePath = this.queryPath(arryFont)
                        + packageName.substring(0, packageName.lastIndexOf(".") + 1)
                           .concat(frontPathSuffix)
                           .toLowerCase()
                           .replace("com.sunwayworld", "static")
                           .replace(".", separator)
                        + frontPathx;
                     fileName = templateName.replace("template", filePath + separator + business.replace("_", "-"));
                  }

                  xmlPath = this.queryPath(arryFont) + (packageName + "s").replace("com.sunwayworld", "templates").replace(".", separator);
                  templateAttr.setFuncList(Collections.emptyList());
                  templateAttr.setEditMenuId("01");
                  templateAttr.setAuditMenuId("02");
               }

               templateAttr.setHtmlPath(xmlPath.replace(this.queryPath(arryFont) + "templates" + separator, "").replace(separator, "/"));
               templateAttr.setJsPath(
                  packageName.substring(0, packageName.lastIndexOf(".") + 1).replace("com.sunwayworld.", "").concat(frontPathSuffix).replace(".", "/") + 's'
               );
               templateAttr.setJsFileName(business.replace("_", "-"));
               templateAttr.setJsObjectName(this.firstUppercase(business)[0]);
               templateAttr.setResourcePath(
                  packageName.substring(0, packageName.lastIndexOf(".") + 1).replace("com.sunwayworld", "/secure").concat(frontPathSuffix).replace(".", "/")
                     + 's'
               );
               templateAttr.setPackageName(packageName.toLowerCase());
               String[] tableNameStrArray = business.toLowerCase().split("_");
               String tableObjectStr = StringUtils.join(ArrayUtils.asList(tableNameStrArray).subList(1, tableNameStrArray.length), "_");
               templateAttr.setTableObject(this.firstUppercase(tableObjectStr)[1]);
               templateAttr.setJavaObjectName(this.firstUppercase(business)[1]);
               templateAttr.setControllerPath(
                  packageName.substring(0, packageName.lastIndexOf(".") + 1).replace("com.sunwayworld.", "").concat(frontPathSuffix).replace(".", "/") + 's'
               );
               templateAttr.setHtmlFileName(business.replace("_", "-"));
               templateAttr.setCreateOracleTableSql(createSqlStr);
               templateAttr.setCreateMysqlTableSql(createMysqlStr);
               templateAttr.setCreateSqlServerStr(createSqlServerStr);
               templateAttr.setCreatePgsqlTableSql(createPgsqlStr);
               templateAttr.setI118n(i118n);
               templateAttr.setI118nPackageName((packageName.replace("com.sunwayworld.", "") + "s").toUpperCase());
               templateAttr.setNowDate(DateTimeUtils.formatLocalDate(LocalDate.now()));
               templateAttr.setFields(tableFieldList);
               templateAttr.setTableDesc(tableDesc);
               mapper.put("data", templateAttr);
               this.analysisTemplate(templatePath, templateName, fileName, filePath, mapper, fileList, isStartInJar);
            }
         }
      }
   }

   private void setFieldValidator(CoreCodeGenerateConfigFieldBean field) {
      List<String> validatorList = new ArrayList<>();
      if ("0".equals(field.getDataRequire())) {
         validatorList.add("'notEmpty'");
      }

      if ("varchar".equals(field.getFieldType()) && !StringUtils.isEmpty(field.getFieldLength())) {
         validatorList.add("'strLength[0," + NumberUtils.parseLong(field.getFieldLength()) / 3L + "]'");
      }

      if (!validatorList.isEmpty()) {
         field.setExt$Item("validators", "[ " + StringUtils.join(validatorList, ", ") + " ]");
      }
   }

   private void analysisTemplate(
      String templatePath, String templateName, String fileName, String filePath, Map<String, Object> mapper, List<File> fileList, boolean isStartInJar
   ) {
      Configuration config = new Configuration(Configuration.VERSION_2_3_23);
      FileOutputStream fos = null;
      Writer out = null;

      try {
         config.setClassForTemplateLoading(CoreCodeGenerateConfigService.class, "/" + templatePath);
         config.setObjectWrapper(new DefaultObjectWrapper(Configuration.VERSION_2_3_23));
         Template template = config.getTemplate(templateName, "UTF-8");
         File file = new File(filePath);
         if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
         }

         file = new File(fileName);
         if (file.exists() && file.isFile()) {
            file.delete();
         }

         fos = new FileOutputStream(fileName);
         out = new OutputStreamWriter(fos, "UTF-8");
         template.process(mapper, out);
         out.flush();
         fileList.add(file);
      } catch (IOException var24) {
         throw new FileException(var24);
      } catch (TemplateException var25) {
         throw new FileException(var25);
      } finally {
         if (out != null) {
            try {
               out.close();
            } catch (IOException var23) {
               var23.printStackTrace();
            }
         }

         if (fos != null) {
            try {
               fos.close();
            } catch (IOException var22) {
               var22.printStackTrace();
            }
         }
      }
   }

   private void logger(String log) {
      logger.info(log);
   }

   private String queryPath(String[] arry) {
      String path = "";

      for (String str : arry) {
         path = path + str + separator;
      }

      return path;
   }

   private String[] firstUppercase(String tableName) {
      String newName = "";
      String firstNewName = "";

      for (int a = 0; a < tableName.length(); a++) {
         char chr = tableName.charAt(a);
         if (a == 0) {
            newName = newName + chr;
            firstNewName = firstNewName + String.valueOf(chr).toUpperCase();
         } else if ("_".equals(String.valueOf(chr))) {
            newName = newName + String.valueOf(tableName.charAt(a + 1)).toUpperCase();
            firstNewName = firstNewName + String.valueOf(tableName.charAt(a + 1)).toUpperCase();
            a++;
         } else {
            newName = newName + chr;
            firstNewName = firstNewName + chr;
         }
      }

      return new String[]{newName, firstNewName};
   }

   private boolean isStartInJar() {
      String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

      try {
         path = URLDecoder.decode(path, "utf-8");
      } catch (Exception var3) {
         throw new FileException(var3);
      }

      return path.toUpperCase().indexOf(".JAR") != -1;
   }

   private String getMenuIconUrl(String menuIcon) {
      if (StringUtils.isEmpty(menuIcon)) {
         return null;
      } else {
         CoreSystemConfigDTO systemConfig = SystemConfigHelper.getSystemConfig();
         return "/static/icon/menu/" + systemConfig.getPageStyle() + "/" + systemConfig.getPageSkin() + "/" + menuIcon;
      }
   }

   private void checkLength(List<CoreCodeGenerateConfigTableBean> tableList, List<CoreCodeGenerateConfigFieldBean> fieldList) {
      for (CoreCodeGenerateConfigTableBean tableBean : tableList) {
         if (!DbMetaCheckHelper.validate(tableBean.getTableName())) {
            int identifierLen = DbMetaCheckHelper.ERR_IDENTIFIER_LEN;
            throw new ApplicationWarningException("表名长度不能超过" + identifierLen + "个字符");
         }
      }

      for (CoreCodeGenerateConfigFieldBean configFieldBean : fieldList) {
         if (!DbMetaCheckHelper.validate(configFieldBean.getField())) {
            int identifierLen = DbMetaCheckHelper.ERR_IDENTIFIER_LEN;
            throw new ApplicationWarningException("字段名长度不能超过" + identifierLen + "个字符");
         }
      }
   }

   private void checkAuditField(
      CoreCodeGenerateConfigBean kbCodeGenerateConfig, List<CoreCodeGenerateConfigTableBean> tableList, List<CoreCodeGenerateConfigFieldBean> fieldList
   ) {
      String func = kbCodeGenerateConfig.getFunc();
      if (!StringUtils.isBlank(func)) {
         boolean checkAuditField = false;
         JSONArray funcArray = JSON.parseArray(func);

         for (int i = 0; i < funcArray.size(); i++) {
            JSONObject funcObj = funcArray.getJSONObject(i);
            if ("audit".equals(funcObj.getString("value")) && "1".equals(funcObj.getString("checked"))) {
               checkAuditField = true;
               break;
            }
         }

         if (checkAuditField) {
            Map<Long, CoreCodeGenerateConfigTableBean> mainTableMap = tableList.stream()
               .filter(table -> "main".equals(table.getType()))
               .collect(Collectors.toMap(CoreCodeGenerateConfigTableBean::getId, Function.identity(), (a, b) -> a));
            Set<Long> mainTableIdSet = mainTableMap.keySet();
            Map<Long, List<CoreCodeGenerateConfigFieldBean>> tableFieldsMap = fieldList.stream()
               .filter(field -> mainTableIdSet.contains(field.getTableId()))
               .collect(Collectors.groupingBy(CoreCodeGenerateConfigFieldBean::getTableId));

            for (Entry<Long, List<CoreCodeGenerateConfigFieldBean>> tableFields : tableFieldsMap.entrySet()) {
               List<CoreCodeGenerateConfigFieldBean> fields = tableFields.getValue();
               boolean validAudit = fields.stream().anyMatch(field -> "processstatus".equalsIgnoreCase(field.getField()));
               if (!validAudit) {
                  CoreCodeGenerateConfigTableBean tableBean = mainTableMap.get(tableFields.getKey());
                  throw new ApplicationWarningException("表 - " + tableBean.getTableDesc() + "缺失审核字段");
               }
            }
         }
      }
   }
}
