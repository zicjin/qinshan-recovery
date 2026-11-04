package com.sunwayworld.cloud.module.office.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.spire.data.table.DataRow;
import com.spire.data.table.DataTable;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Table;
import com.spire.doc.collections.RowCollection;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.TextSelection;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.TextRange;
import com.spire.xls.CellRange;
import com.spire.xls.ExcelPicture;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import com.spire.xls.collections.WorksheetsCollection;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeInstanceBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeInstanceDTO;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeTmplBean;
import com.sunwayworld.cloud.module.office.exception.OfficeException;
import com.sunwayworld.cloud.module.office.persistent.dao.CoreOfficeTmplDao;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsFieldService;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsService;
import com.sunwayworld.cloud.module.office.service.CoreOfficeInstanceService;
import com.sunwayworld.cloud.module.office.service.CoreOfficeTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.utils.TransactionUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreOfficeTmplServiceImpl implements CoreOfficeTmplService {
   private static Logger logger = LogManager.getLogger(CoreOfficeTmplService.class);
   @Autowired
   private CoreOfficeTmplDao officeTmplDao;
   @Autowired
   @Lazy
   private CoreOfficeDsService officeDsService;
   @Autowired
   @Lazy
   private CoreOfficeDsFieldService officeDsFieldService;
   @Autowired
   @Lazy
   private CoreFileService fileService;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;
   @Autowired
   @Lazy
   private GlobalMapper globalMapper;
   @Autowired
   @Lazy
   private CoreOfficeTmplService proxyInstance;
   @Autowired
   @Lazy
   private CoreOfficeInstanceService officeInstanceService;

   public CoreOfficeTmplDao getDao() {
      return this.officeTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreOfficeTmplBean officeTmpl = jsonWrapper.parseUnique(CoreOfficeTmplBean.class);
      officeTmpl.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(officeTmpl);
      return officeTmpl.getId();
   }

   @Override
   public String buildReport(Long id, RestJsonWrapperBean wrapper, boolean isInstance) {
      String officeParam = wrapper.getParamValue("officeparam");
      Map<String, String> officeParamMap = JSONObject.parseObject(officeParam, new TypeReference<Map<String, String>>() {}, new Feature[0]);
      if (CollectionUtils.isEmpty(officeParamMap)) {
         officeParamMap = Collections.emptyMap();
      }

      CoreOfficeInstanceDTO instanceDTO = this.buildReport(id, officeParamMap, isInstance);
      return null != instanceDTO ? instanceDTO.getDownloadUrl() : null;
   }

   @Override
   public CoreOfficeInstanceDTO buildReport(Long tmplId, Map<String, String> officeParamMap, boolean isInstance) {
      Map<String, String> paramMap = new CaseInsensitiveLinkedMap<>(officeParamMap);
      CoreFileBean templateFile = this.fileService
         .selectFirstByFilter(
            SearchFilter.instance().match("TARGETID", CoreFileUtils.getTargetId(this.getDao().getTable(), tmplId)).filter(MatchPattern.SEQ),
            new Order[]{Order.desc("ID")}
         );
      if (ObjectUtils.isEmpty(templateFile)) {
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.TMPL_FILE_MISSING"));
      } else {
         String bizId = paramMap.get("bizId");
         if (isInstance) {
            if (StringUtils.isBlank(bizId)) {
               throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.EXCEPTION.BIZID_IS_EMPTY"));
            }

            List<CoreOfficeInstanceBean> instanceList = this.officeInstanceService
               .selectListByFilter(
                  SearchFilter.instance().match("TMPLID", tmplId).filter(MatchPattern.EQ).match("BIZID", bizId).filter(MatchPattern.EQ),
                  new Order[]{Order.desc("ID")}
               );
            if (!instanceList.isEmpty()) {
               throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.EXCEPTION.REPEAT-GENERATE-INSTANCE"));
            }
         }

         Path templatePath = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(FileScope.temp, templateFile.getName()));
         String fileExt = FileUtils.getFileExtension(templateFile.getName());
         FilePathDTO reportPathDTO = CoreFileUtils.toFilePath(FileScope.temp, tmplId + "_report." + fileExt);
         Path reportPath = FilePathManager.getLocalPath(reportPathDTO);

         CoreOfficeInstanceDTO var27;
         try {
            this.fileManager.download(templateFile, templatePath);
            CoreOfficeTmplBean tmplBean = this.proxyInstance.selectById(tmplId);
            if ("word".equals(tmplBean.getDocumentType())) {
               this.buildReportForWord(tmplId, paramMap, templatePath, reportPathDTO, reportPath);
            } else {
               this.buildReportForExcel(tmplBean, paramMap, templatePath, reportPathDTO, reportPath);
            }

            String downloadUrl = "";
            CoreOfficeInstanceDTO instanceDTO = new CoreOfficeInstanceDTO();
            if (isInstance) {
               CoreOfficeInstanceBean instanceBean = new CoreOfficeInstanceBean();
               Long instanceId = ApplicationContextHelper.getNextIdentity();
               instanceBean.setId(instanceId);
               instanceBean.setTmplId(tmplId);
               instanceBean.setInstanceName(Optional.ofNullable(paramMap.get("instanceName")).orElse(tmplId + "_report"));
               instanceBean.setDocumentType(tmplBean.getDocumentType());
               instanceBean.setBizCategory(tmplBean.getBizCategory());
               instanceBean.setBizId(bizId);
               instanceBean.setBizParam(JSON.toJSONString(paramMap));
               this.officeInstanceService.getDao().insert(instanceBean);
               instanceDTO.setInstanceId(instanceId);
               CoreFileBean fileBean = new CoreFileBean();
               fileBean.setTargetId(CoreFileUtils.getTargetId("T_CORE_OFFICE_INSTANCE", instanceId));
               fileBean.setExt(fileExt);
               Long fileId = this.fileService.upload(fileBean, reportPath.toFile());
               instanceDTO.setFileId(fileId);
               downloadUrl = this.fileManager.getDownloadUrl(this.fileService.selectById(fileId));
            } else {
               this.fileManager.upload(reportPathDTO, reportPath);
               downloadUrl = this.fileManager.getDownloadUrl(reportPathDTO);
            }

            instanceDTO.setDownloadUrl(downloadUrl);
            var27 = instanceDTO;
         } catch (InvalidDataException var22) {
            logger.warn(var22.getMessage(), (Throwable)var22);
            throw new OfficeException(var22.getMessage());
         } catch (Exception var23) {
            logger.warn(var23.getMessage(), (Throwable)var23);
            throw new OfficeException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.EXCEPTION.GENERATE-INSTANCE"));
         } finally {
            FileUtils.deleteQuietly(templatePath.toFile());
         }

         return var27;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertBlankDocument(Long id) {
      String documentType = this.selectById(id).getDocumentType();
      String blankDocumentName = null;
      String targetDocumentName = null;
      if ("word".equals(documentType)) {
         blankDocumentName = "blank-template.docx";
         targetDocumentName = id + "_template.docx";
      } else {
         blankDocumentName = "blank-template.xlsx";
         targetDocumentName = id + "_template.xlsx";
      }

      Path targetPath = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(FileScope.temp, targetDocumentName));

      Long var8;
      try {
         FileUtils.makeDirs(targetPath);
         URL resource = this.getClass().getResource("");
         if (FileUtils.isInJarURL(resource)) {
            InputStream is = this.getClass().getResourceAsStream("templates/" + blankDocumentName);
            Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
         } else {
            Path templatePath = new File(resource.getPath()).toPath().resolve("templates").resolve(blankDocumentName);
            Files.copy(templatePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
         }

         CoreFileBean coreFile = new CoreFileBean();
         coreFile.setTargetId(CoreFileUtils.getTargetId(this.getDao().getTable(), id));
         coreFile.setScope(FileScope.secure.name());
         this.proxyInstance.clearBindDs(id);
         var8 = this.fileService.upload(coreFile, targetPath);
      } catch (IOException var12) {
         throw new FileException(var12);
      } finally {
         FileUtils.deleteQuietly(targetPath.toFile());
      }

      return var8;
   }

   @CacheEvict(
      value = {"T_CORE_OFFICE_TMPL.BIND-DS"},
      key = "#tmplId"
   )
   @Override
   public void clearBindDs(Long tmplId) {
      TransactionUtils.runAfterCompletion(i -> {
         if (0 != i) {
            RedisHelper.evict("T_CORE_OFFICE_TMPL.BIND-DS", "" + tmplId);
         }
      });
   }

   @Cacheable(
      value = {"T_CORE_OFFICE_TMPL.BIND-DS"},
      key = "#tmplId"
   )
   @Override
   public List<String> selectBindDsNameList(Long tmplId, File templateFile) {
      String content = FileUtils.extractTextFromFile(templateFile);
      List<String> dsIdentifierList = StringUtils.substringsBetween(content, "{{", "}}");
      return dsIdentifierList.stream().map(d -> {
         d = StringUtils.replace(d, "@", "");
         return StringUtils.contains(d, ".") ? StringUtils.split(d, ".")[0] : d;
      }).distinct().collect(Collectors.toList());
   }

   @Override
   public String getTestFormFieldByTmplId(Long id, RestJsonWrapperBean wrapper) {
      CoreFileBean templateFile = this.fileService
         .selectFirstByFilter(
            SearchFilter.instance().match("TARGETID", CoreFileUtils.getTargetId(this.getDao().getTable(), id)).filter(MatchPattern.SEQ),
            new Order[]{Order.desc("ID")}
         );
      if (ObjectUtils.isEmpty(templateFile)) {
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.TMPL_FILE_MISSING"));
      } else {
         Path templatePath = FilePathManager.getLocalPath(CoreFileUtils.toFilePath(FileScope.temp, templateFile.getName()));

         String var11;
         try {
            this.fileManager.download(templateFile, templatePath);
            List<String> dsNameList = this.proxyInstance.selectBindDsNameList(id, templatePath.toFile());
            CoreOfficeDsDTO officeDsDTO = this.officeDsService.selectActivatableOfficeDsDTO();
            List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> bindDetailDsList = officeDsDTO.getDetailDsList()
               .stream()
               .filter(d -> dsNameList.contains(d.getFirst().getDsName()))
               .collect(Collectors.toList());
            List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> bindTableDsList = officeDsDTO.getTableDsList()
               .stream()
               .filter(d -> dsNameList.contains(d.getFirst().getDsName()))
               .collect(Collectors.toList());
            List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> dsPairList = CollectionUtils.union(bindDetailDsList, bindTableDsList);
            Set<String> fieldNameList = new HashSet<>();
            if (!dsPairList.isEmpty()) {
               dsPairList.forEach(p -> {
                  CoreOfficeDsBean ds = p.getFirst();
                  String sql = ds.getSqlContent();
                  sql = this.replaceUselessSql(sql);
                  List<String> sqlClauseIdentifierList = StringUtils.substringsBetween(sql, "#", "#");
                  sqlClauseIdentifierList.stream().distinct().collect(Collectors.toList()).forEach(fieldNameList::add);
               });
            }

            var11 = JSON.toJSONString(fieldNameList, SerializerFeature.WriteMapNullValue);
         } catch (Exception var15) {
            logger.warn(var15.getMessage(), (Throwable)var15);
            throw new OfficeException(I18nHelper.getMessage("GIKAM.EXCEPTION.UNEXPECTED"));
         } finally {
            FileUtils.deleteQuietly(templatePath.toFile());
         }

         return var11;
      }
   }

   private Map<String, Object> selectTmplDsResultMapForWord(Long tmplId, File templateFile, Map<String, String> paramMap) {
      Map<String, Object> tmplDsResultMap = new HashMap<>();
      List<String> dsNameList = this.proxyInstance.selectBindDsNameList(tmplId, templateFile);
      CoreOfficeDsDTO officeDsDTO = this.officeDsService.selectActivatableOfficeDsDTO();
      List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> bindDetailDsList = officeDsDTO.getDetailDsList()
         .stream()
         .filter(d -> dsNameList.contains(d.getFirst().getDsName()))
         .collect(Collectors.toList());
      List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> bindTableDsList = officeDsDTO.getTableDsList()
         .stream()
         .filter(d -> dsNameList.contains(d.getFirst().getDsName()))
         .collect(Collectors.toList());
      List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> dsPairList = CollectionUtils.union(bindDetailDsList, bindTableDsList);
      tmplDsResultMap.put("dataSourceList", dsPairList);
      if (!dsPairList.isEmpty()) {
         dsPairList.forEach(p -> {
            CoreOfficeDsBean ds = p.getFirst();
            List<CoreOfficeDsFieldBean> dsFieldList = p.getSecond();
            List<Map<String, Object>> dsResultMapList = this.getDsResult(ds, paramMap);
            if (!CollectionUtils.isEmpty(dsResultMapList)) {
               if ("list".equals(ds.getDisplayCategory())) {
                  List<Map<String, Object>> dataObjectList = new ArrayList<>();

                  for (Map<String, Object> detailMap : dsResultMapList) {
                     if (detailMap != null) {
                        List<String> idFieldList = detailMap.keySet().stream().filter(e -> e.toUpperCase().contains("_ID")).collect(Collectors.toList());
                        if (!idFieldList.isEmpty()) {
                           Map<String, Object> detailWithIdMap = new HashMap<>();
                           this.analysisIdAndValue(detailMap, idFieldList, detailWithIdMap, dsFieldList, ds.getSqlContent());
                           dataObjectList.add(detailWithIdMap);
                        } else {
                           dataObjectList.add(detailMap);
                        }
                     }
                  }

                  tmplDsResultMap.put(ds.getDsName(), dataObjectList);
               } else if ("detail".equals(ds.getDisplayCategory())) {
                  Map<String, Object> detailMapx = dsResultMapList.get(0);
                  if (detailMapx != null) {
                     List<String> idFieldList = detailMapx.keySet().stream().filter(e -> e.toUpperCase().contains("_ID")).collect(Collectors.toList());
                     if (!idFieldList.isEmpty()) {
                        Map<String, Object> detailWithIdMap = new HashMap<>();
                        this.analysisIdAndValue(detailMapx, idFieldList, detailWithIdMap, dsFieldList, ds.getSqlContent());
                        tmplDsResultMap.put(ds.getDsName(), detailWithIdMap);
                     } else {
                        tmplDsResultMap.put(ds.getDsName(), detailMapx);
                     }
                  }
               }
            }
         });
      }

      return tmplDsResultMap;
   }

   private Map<String, Object> selectTmplDsResultMapForExcel(Long tmplId, File templateFile, Map<String, String> paramMap) {
      Map<String, Object> tmplDsResultMap = new HashMap<>();
      List<String> dsNameList = this.proxyInstance.selectBindDsNameList(tmplId, templateFile);
      CoreOfficeDsDTO officeDsDTO = this.officeDsService.selectActivatableOfficeDsDTO();
      List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> bindDetailDsList = officeDsDTO.getDetailDsList()
         .stream()
         .filter(d -> dsNameList.contains(d.getFirst().getDsName()))
         .collect(Collectors.toList());
      List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> bindTableDsList = officeDsDTO.getTableDsList()
         .stream()
         .filter(d -> dsNameList.contains(d.getFirst().getDsName()))
         .collect(Collectors.toList());
      List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> dsPairList = CollectionUtils.union(bindDetailDsList, bindTableDsList);
      tmplDsResultMap.put("dataSourceList", dsPairList);
      if (!dsPairList.isEmpty()) {
         dsPairList.forEach(p -> {
            CoreOfficeDsBean ds = p.getFirst();
            List<CoreOfficeDsFieldBean> dsFieldList = p.getSecond();
            List<Map<String, Object>> dsResultMapList = this.getDsResult(ds, paramMap);
            if (!CollectionUtils.isEmpty(dsResultMapList)) {
               if ("list".equals(ds.getDisplayCategory())) {
                  List<Map<String, Object>> dataObjectList = new ArrayList<>();

                  for (Map<String, Object> detailMap : dsResultMapList) {
                     if (detailMap != null) {
                        List<String> idFieldList = detailMap.keySet().stream().filter(e -> e.toUpperCase().contains("_ID")).collect(Collectors.toList());
                        if (!idFieldList.isEmpty()) {
                           Map<String, Object> detailWithIdMap = new HashMap<>();
                           this.analysisIdAndValue(detailMap, idFieldList, detailWithIdMap, dsFieldList, ds.getSqlContent());
                           dataObjectList.add(detailWithIdMap);
                        } else {
                           dataObjectList.add(detailMap);
                        }
                     }
                  }

                  tmplDsResultMap.put(ds.getDsName(), dataObjectList);
               } else if ("detail".equals(ds.getDisplayCategory())) {
                  Map<String, Object> detailMapx = dsResultMapList.get(0);
                  if (detailMapx != null) {
                     List<String> idFieldList = detailMapx.keySet().stream().filter(e -> e.toUpperCase().contains("_ID")).collect(Collectors.toList());
                     if (!idFieldList.isEmpty()) {
                        Map<String, Object> detailWithIdMap = new HashMap<>();
                        this.analysisIdAndValue(detailMapx, idFieldList, detailWithIdMap, dsFieldList, ds.getSqlContent());
                        tmplDsResultMap.put(ds.getDsName(), detailWithIdMap);
                     } else {
                        tmplDsResultMap.put(ds.getDsName(), detailMapx);
                     }
                  }
               }
            }
         });
      }

      return tmplDsResultMap;
   }

   private void analysisIdAndValue(
      Map<String, Object> beanMap, List<String> idFieldList, Map<String, Object> valueMap, List<CoreOfficeDsFieldBean> dsFieldList, String sql
   ) {
      Map<String, Object> idMap = idFieldList.stream().collect(Collectors.toMap(Function.identity(), beanMap::remove));
      idMap.forEach(
         (idField, id) -> {
            String sameIdMark = idField.substring(0, idField.indexOf("_") + 1);
            List<String> fieldList = beanMap.keySet().stream().filter(field -> StringUtils.startsWith(field, sameIdMark)).collect(Collectors.toList());
            if (!fieldList.isEmpty()) {
               fieldList.stream().forEach(field -> {
                  String showField = StringUtils.replace(field, sameIdMark, "");
                  valueMap.put(showField, beanMap.get(field));
               });
            }

            if (!StringUtils.isEmpty(sql)) {
               dsFieldList.forEach(
                  filterDsField -> {
                     if (!StringUtils.isEmpty(filterDsField.getSqlColumnName())
                        && !valueMap.keySet().contains(filterDsField.getSqlColumnName() + "_ID")
                        && (
                           StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getSqlColumnName() + " ")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getSqlColumnName() + "\r")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getSqlColumnName() + ",")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getSqlColumnName() + "\r\n")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getSqlColumnName() + "\n")
                        )) {
                        valueMap.put(filterDsField.getSqlColumnName() + "_ID", id);
                     }
                  }
               );
            }
         }
      );
      List<String> noIdFieldList = beanMap.keySet().stream().filter(field -> !field.contains("_")).collect(Collectors.toList());
      if (!noIdFieldList.isEmpty()) {
         noIdFieldList.stream().forEach(field -> valueMap.put(field, beanMap.get(field)));
      }
   }

   private InputStream getImageInputStreamByUrl(CoreOfficeDsBean ds, CoreOfficeDsFieldBean field, String pictureUrl) {
      try {
         Path tempFilePath = FileUtils.createTempFile().toPath();
         this.fileManager.download(FilePathManager.toFilePath(pictureUrl), tempFilePath);
         return Files.newInputStream(tempFilePath);
      } catch (Exception var5) {
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.IMAGE_URL_ERROR", ds.getDsName(), field.getSqlColumnName()));
      }
   }

   private List<Map<String, Object>> getDsResult(CoreOfficeDsBean ds, Map<String, String> paramMap) {
      String sql = StringUtils.replaceBetween(ds.getSqlContent(), "#", "#", paramMap);
      if (StringUtils.containsIgnoreCase(sql, "{loginUser.id}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginUser.orgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginOrgId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
      }

      if (StringUtils.containsIgnoreCase(sql, "{loginRoleId}")) {
         sql = StringUtils.replaceIgnoreCase(sql, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
      }

      sql = this.replaceUselessSql(sql);
      if (StringUtils.substringsBetween(sql, "#", "#").size() > 0) {
         throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.SQL_CLAUSE_MISSING") + ds.getDsName());
      } else {
         try {
            logger.info(sql);
            return this.globalMapper.selectList(sql);
         } catch (Exception var5) {
            throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.SQL_ERROR") + ds.getDsName());
         }
      }
   }

   private String replaceUselessSql(String sql) {
      for (String replaceItem : StringUtils.substringsBetween(sql, "/*", "*/")) {
         if (!StringUtils.isBlank(replaceItem)) {
            sql = StringUtils.replace(sql, replaceItem, "");
         }
      }

      return sql;
   }

   private void buildReportForWord(Long tmplId, Map<String, String> paramMap, Path templatePath, FilePathDTO reportPathDTO, Path reportPath) throws IOException, FileNotFoundException {
      Map<String, Object> tmplDsResultMap = this.selectTmplDsResultMapForWord(tmplId, templatePath.toFile(), paramMap);
      FileUtils.createFile(reportPath.toFile());

      try (OutputStream os = new BufferedOutputStream(new FileOutputStream(reportPath.toFile()))) {
         Document document = new Document();
         document.loadFromStream(Files.newInputStream(templatePath), FileFormat.Auto);
         List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> dsPairList = (List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>>)tmplDsResultMap.get(
            "dataSourceList"
         );
         if (!dsPairList.isEmpty()) {
            dsPairList.forEach(
               p -> {
                  CoreOfficeDsBean ds = p.getFirst();
                  List<CoreOfficeDsFieldBean> dsFieldList = p.getSecond();
                  if ("detail".equals(ds.getDisplayCategory())) {
                     Object dsMap = Optional.ofNullable(tmplDsResultMap.get(ds.getDsName())).orElse(new HashMap());
                     Map<String, Object> detailMap = (Map<String, Object>)dsMap;
                     dsFieldList.forEach(
                        fieldx -> {
                           String findDetailKey = "{{" + ds.getDsName() + "." + fieldx.getShowColumnName() + "}}";
                           Object targetValue = Optional.ofNullable(detailMap.get(fieldx.getSqlColumnName())).orElse("");
                           if (!CollectionUtils.isEmpty(detailMap)) {
                              if ("1".equals(fieldx.getIsPicture())) {
                                 String pictureUrlx = ConvertUtils.convert(targetValue, String.class);
                                 if (!StringUtils.isBlank(pictureUrlx)) {
                                    if (NumberUtils.parseInt(fieldx.getPictureWidth()) == 0 || NumberUtils.parseInt(fieldx.getPictureHeight()) == 0) {
                                       throw new InvalidDataException(
                                          I18nHelper.getMessage(
                                             "CORE.MODULE.OFFICE.TMPLS.TIP.IMAGE_WIDTH_OR_HEIGHT_MISSING", ds.getDsName(), fieldx.getShowColumnName()
                                          )
                                       );
                                    }

                                    TextSelection[] selectionsx = document.findAllString(findDetailKey, true, true);
                                    int index = 0;
                                    TextRange range = null;

                                    for (Object obj : selectionsx) {
                                       TextSelection textSelection = (TextSelection)obj;
                                       DocPicture picx = new DocPicture(document);
                                       picx.loadImage(this.getImageInputStreamByUrl(ds, fieldx, pictureUrlx));
                                       picx.setWidth(NumberUtils.parseInt(fieldx.getPictureWidth()));
                                       picx.setHeight(NumberUtils.parseInt(fieldx.getPictureHeight()));
                                       range = textSelection.getAsOneRange();
                                       index = range.getOwnerParagraph().getChildObjects().indexOf(range);
                                       range.getOwnerParagraph().getChildObjects().insert(index, picx);
                                       range.getOwnerParagraph().getChildObjects().remove(range);
                                    }
                                 }
                              } else {
                                 document.replace(findDetailKey, targetValue.toString(), false, true);
                              }
                           } else {
                              document.replace(findDetailKey, "", false, true);
                           }
                        }
                     );
                  } else if ("list".equals(ds.getDisplayCategory())) {
                     String findTableKey = "{{" + ds.getDsName() + "}}";
                     TextSelection[] selections = document.findAllString(findTableKey, true, false);
                     if (selections.length > 0) {
                        for (TextSelection selection : selections) {
                           TextRange textRange = selection.getAsOneRange();
                           Paragraph paragraph = textRange.getOwnerParagraph();
                           if (paragraph.getOwner().getOwner().getOwner() instanceof Table) {
                              Table table = (Table)paragraph.getOwner().getOwner().getOwner();
                              int lastCellIndex = table.getLastCell().getCellIndex() + 1;
                              if (dsFieldList.size() != lastCellIndex) {
                                 throw new InvalidDataException(
                                    I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.TABLE_DS_COLUMN_COUNT_ERROR", ds.getDsName())
                                 );
                              }

                              RowCollection rows = table.getRows();
                              int headerRowCount = rows.getCount() - 1;
                              if ("1".equals(ds.getRepeatHeader())) {
                                 for (int r = 0; r < headerRowCount; r++) {
                                    rows.get(r).isHeader(true);
                                 }
                              }

                              Object dsMap = Optional.ofNullable(tmplDsResultMap.get(ds.getDsName())).orElse(new ArrayList());
                              List<Map<String, Object>> dsResultMapList = (List<Map<String, Object>>)dsMap;
                              if (!CollectionUtils.isEmpty(dsResultMapList)) {
                                 for (int j = 0; j < dsResultMapList.size(); j++) {
                                    table.addRow();
                                    Map<String, Object> map = dsResultMapList.get(j);

                                    for (int k = 0; k < dsFieldList.size(); k++) {
                                       CoreOfficeDsFieldBean field = dsFieldList.get(k);
                                       Object itemValue = Optional.ofNullable(map.get(field.getSqlColumnName())).orElse("");
                                       if ("1".equals(field.getIsPicture())) {
                                          if (NumberUtils.parseInt(field.getPictureWidth()) == 0 || NumberUtils.parseInt(field.getPictureHeight()) == 0) {
                                             throw new InvalidDataException(
                                                I18nHelper.getMessage(
                                                   "CORE.MODULE.OFFICE.TMPLS.TIP.IMAGE_WIDTH_OR_HEIGHT_MISSING", ds.getDsName(), field.getShowColumnName()
                                                )
                                             );
                                          }

                                          String pictureUrl = ConvertUtils.convert(itemValue, String.class);
                                          DocPicture pic = new DocPicture(document);
                                          pic.loadImage(this.getImageInputStreamByUrl(ds, field, pictureUrl));
                                          pic.setWidth(NumberUtils.parseInt(field.getPictureWidth()));
                                          pic.setHeight(NumberUtils.parseInt(field.getPictureHeight()));
                                          table.get(j + headerRowCount + 1, k).addParagraph().getChildObjects().add(pic);
                                       } else {
                                          table.get(j + headerRowCount + 1, k).addParagraph().appendText(itemValue.toString());
                                       }
                                    }
                                 }
                              }

                              table.getRows().removeAt(headerRowCount);
                              table.getTableFormat().isBreakAcrossPages(false);
                           }
                        }
                     }

                     document.replace(findTableKey, "", true, true);
                  }
               }
            );
         }

         document.saveToStream(os, document.getDetectedFormatType());
      } catch (IOException var21) {
         throw new FileException(var21);
      }
   }

   private void buildReportForExcel(CoreOfficeTmplBean officeTmpl, Map<String, String> paramMap, Path templatePath, FilePathDTO reportPathDTO, Path reportPath) throws IOException, FileNotFoundException {
      Map<String, Object> tmplDsResultMap = this.selectTmplDsResultMapForExcel(officeTmpl.getId(), templatePath.toFile(), paramMap);
      FileUtils.createFile(reportPath.toFile());

      try (OutputStream os = new BufferedOutputStream(new FileOutputStream(reportPath.toFile()))) {
         Workbook workbook = new Workbook();
         workbook.loadFromStream(Files.newInputStream(templatePath));
         WorksheetsCollection workSheets = workbook.getWorksheets();
         Set<String> cellNameRangeSet = new HashSet<>();

         for (int i = 0; i < workSheets.size(); i++) {
            Worksheet sheet = workSheets.get(i);
            List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>> dsPairList = (List<Pair<CoreOfficeDsBean, List<CoreOfficeDsFieldBean>>>)tmplDsResultMap.get(
               "dataSourceList"
            );
            if (!dsPairList.isEmpty()) {
               dsPairList.forEach(
                  p -> {
                     CoreOfficeDsBean ds = p.getFirst();
                     List<CoreOfficeDsFieldBean> dsFieldList = p.getSecond();
                     if ("detail".equals(ds.getDisplayCategory())) {
                        Object dsMap = Optional.ofNullable(tmplDsResultMap.get(ds.getDsName())).orElse(new HashMap());
                        Map<String, Object> detailMap = (Map<String, Object>)dsMap;
                        dsFieldList.forEach(
                           fieldx -> {
                              String findDetailKey = "{{" + ds.getDsName() + "." + fieldx.getShowColumnName() + "}}";
                              Object targetValue = Optional.ofNullable(detailMap.get(fieldx.getSqlColumnName())).orElse("");
                              CellRange[] findAllCellRange = sheet.findAllString(findDetailKey, true, true);
                              if (findAllCellRange.length > 0 && !CollectionUtils.isEmpty(detailMap)) {
                                 for (CellRange replaceCell : findAllCellRange) {
                                    if ("1".equals(fieldx.getIsPicture())) {
                                       String pictureUrl = ConvertUtils.convert(targetValue, String.class);
                                       if (!StringUtils.isBlank(pictureUrl)) {
                                          if (NumberUtils.parseInt(fieldx.getPictureWidth()) == 0 || NumberUtils.parseInt(fieldx.getPictureHeight()) == 0) {
                                             throw new InvalidDataException(
                                                I18nHelper.getMessage(
                                                   "CORE.MODULE.OFFICE.TMPLS.TIP.IMAGE_WIDTH_OR_HEIGHT_MISSING", ds.getDsName(), fieldx.getShowColumnName()
                                                )
                                             );
                                          }

                                          ExcelPicture pic = sheet.getPictures()
                                             .add(replaceCell.getRow(), replaceCell.getColumn(), this.getImageInputStreamByUrl(ds, fieldx, pictureUrl));
                                          pic.setWidth(NumberUtils.parseInt(fieldx.getPictureWidth()));
                                          pic.setHeight(NumberUtils.parseInt(fieldx.getPictureHeight()));
                                          replaceCell.clearAll();
                                       }
                                    } else {
                                       replaceCell.setText(targetValue.toString());
                                    }

                                    if ("eln".equalsIgnoreCase(officeTmpl.getDocumentType()) && "1".equals(fieldx.getWriteBack())) {
                                       String tableNamex = fieldx.getUpdateTableName();
                                       String columnNamex = fieldx.getUpdateColumnName();
                                       if (StringUtils.isBlank(tableNamex)) {
                                          throw new InvalidDataException(
                                             I18nHelper.getMessage(
                                                "CORE.MODULE.OFFICE.TMPLS.TIP.WRITE_BACK_TABLE_NAME_MISSING", ds.getDsName(), fieldx.getShowColumnName()
                                             )
                                          );
                                       }

                                       if (StringUtils.isBlank(columnNamex)) {
                                          throw new InvalidDataException(
                                             I18nHelper.getMessage(
                                                "CORE.MODULE.OFFICE.TMPLS.TIP.WRITE_BACK_COLUMN_NAME_MISSING", ds.getDsName(), fieldx.getShowColumnName()
                                             )
                                          );
                                       }

                                       Object updateIdx = detailMap.get(fieldx.getSqlColumnName() + "_ID");
                                       if (ObjectUtils.isEmpty(updateIdx)) {
                                          throw new InvalidDataException(
                                             I18nHelper.getMessage(
                                                "CORE.MODULE.OFFICE.TMPLS.TIP.WRITE_BACK_ID_VALUE_MISSING", ds.getDsName(), fieldx.getShowColumnName()
                                             )
                                          );
                                       }

                                       String nameRangeItemx = tableNamex + "_updateKey_" + columnNamex + "_updateKey_" + updateIdx.toString();
                                       if (cellNameRangeSet.contains(nameRangeItemx)) {
                                          throw new InvalidDataException(
                                             I18nHelper.getMessage(
                                                "CORE.MODULE.OFFICE.TMPLS.TIP.EXIST_REPEAT_WRITE_BACK_DATA", ds.getDsName(), fieldx.getShowColumnName()
                                             )
                                          );
                                       }

                                       workbook.getNameRanges().add(nameRangeItemx).setRefersToRange(replaceCell);
                                       cellNameRangeSet.add(nameRangeItemx);
                                    }
                                 }
                              } else if (findAllCellRange.length > 0) {
                                 for (CellRange replaceCell : findAllCellRange) {
                                    replaceCell.clearAll();
                                 }
                              }
                           }
                        );
                     } else if ("list".equals(ds.getDisplayCategory())) {
                        String findTableKey = "{{" + ds.getDsName() + "}}";
                        CellRange[] findAllTableCellRange = sheet.findAllString(findTableKey, true, true);
                        if (findAllTableCellRange.length > 1) {
                           throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.TABLE_DS_REPEAT_BIND") + ds.getDsName());
                        }

                        if (findAllTableCellRange.length == 1) {
                           CellRange findTableCellRange = findAllTableCellRange[0];
                           int insertTableRow = findTableCellRange.getRow();
                           int insertTableColumn = findTableCellRange.getColumn();
                           Object dsMap = Optional.ofNullable(tmplDsResultMap.get(ds.getDsName())).orElse(new ArrayList());
                           List<Map<String, Object>> dsResultMapList = (List<Map<String, Object>>)dsMap;
                           if (CollectionUtils.isEmpty(dsResultMapList)) {
                              if (findTableCellRange != null) {
                                 String cellText = findTableCellRange.getText();
                                 String newText = StringUtils.replace(cellText, findTableKey, "");
                                 findTableCellRange.setText(newText);
                              }
                           } else {
                              DataTable dataTable = new DataTable();
                              dsFieldList.forEach(fieldx -> dataTable.getColumns().add(fieldx.getShowColumnName(), String.class));
                              DataRow dataRow = dataTable.newRow();

                              for (int j = 0; j < dsResultMapList.size(); j++) {
                                 Map<String, Object> map = dsResultMapList.get(j);
                                 dataRow = dataTable.newRow();

                                 for (int k = 0; k < dsFieldList.size(); k++) {
                                    CoreOfficeDsFieldBean field = dsFieldList.get(k);
                                    Object itemValue = Optional.ofNullable(map.get(field.getSqlColumnName())).orElse("");
                                    dataRow.setString(k, itemValue.toString());
                                 }

                                 try {
                                    dataTable.getRows().add(dataRow);
                                 } catch (Exception var29) {
                                    throw new InvalidDataException(
                                       I18nHelper.getMessage("CORE.MODULE.OFFICE.TMPLS.TIP.TABLE_DS_INSERT_DATA_ERROR") + ds.getDsName()
                                    );
                                 }
                              }

                              List<String> formulaList = new ArrayList<>();

                              for (int k = 0; k < dsFieldList.size(); k++) {
                                 String tableColumnFormula = sheet.getCellRange(insertTableRow + 1, insertTableColumn + k).getFormula();
                                 formulaList.add(tableColumnFormula);
                              }

                              sheet.insertDataTable(dataTable, true, insertTableRow, insertTableColumn);

                              for (int k = 0; k < formulaList.size(); k++) {
                                 CellRange cellRange = sheet.getCellRange(insertTableRow + 1, insertTableColumn + k);
                                 String currentFormula = formulaList.get(k);
                                 if (currentFormula != null) {
                                    cellRange.setFormula(currentFormula);
                                 }
                              }

                              for (int j = 0; j < dsResultMapList.size(); j++) {
                                 Map<String, Object> valueMap = dsResultMapList.get(j);

                                 for (int kx = 0; kx < dsFieldList.size(); kx++) {
                                    CoreOfficeDsFieldBean field = dsFieldList.get(kx);
                                    if ("1".equals(field.getIsPicture())) {
                                       if (NumberUtils.parseInt(field.getPictureWidth()) == 0 || NumberUtils.parseInt(field.getPictureHeight()) == 0) {
                                          throw new InvalidDataException(
                                             I18nHelper.getMessage(
                                                "CORE.MODULE.OFFICE.TMPLS.TIP.IMAGE_WIDTH_OR_HEIGHT_MISSING", ds.getDsName(), field.getShowColumnName()
                                             )
                                          );
                                       }

                                       String pictureUrl = valueMap.get(field.getSqlColumnName()).toString();
                                       ExcelPicture pic = sheet.getPictures()
                                          .add(insertTableRow + j + 1, insertTableColumn + kx, this.getImageInputStreamByUrl(ds, field, pictureUrl));
                                       pic.setWidth(NumberUtils.parseInt(field.getPictureWidth()));
                                       pic.setHeight(NumberUtils.parseInt(field.getPictureHeight()));
                                       sheet.getRange().get(insertTableRow + j + 1, insertTableColumn + kx).clearAll();
                                    }
                                 }

                                 if (j >= 1) {
                                    for (int kxx = 0; kxx < formulaList.size(); kxx++) {
                                       String currentFormula = formulaList.get(kxx);
                                       CellRange sourceRange = sheet.getCellRange(insertTableRow + 1, insertTableColumn + kxx);
                                       CellRange destRange = sheet.getCellRange(insertTableRow + 1 + j, insertTableColumn + kxx);
                                       destRange.setStyle(sourceRange.getCellStyle());
                                       if (currentFormula != null) {
                                          sourceRange.copy(destRange, true, true);
                                       }
                                    }
                                 }
                              }

                              if ("eln".equalsIgnoreCase(officeTmpl.getDocumentType())) {
                                 Map<String, Object> hasIdMap = dsResultMapList.get(0);
                                 List<String> idFieldList = hasIdMap.keySet()
                                    .stream()
                                    .filter(ex -> ex.toUpperCase().contains("_ID"))
                                    .collect(Collectors.toList());
                                 if (!idFieldList.isEmpty()) {
                                    for (int j = 0; j < dsResultMapList.size(); j++) {
                                       Map<String, Object> valueMap = dsResultMapList.get(j);

                                       for (int kxxx = 0; kxxx < dsFieldList.size(); kxxx++) {
                                          CoreOfficeDsFieldBean field = dsFieldList.get(kxxx);
                                          if ("1".equals(field.getWriteBack())) {
                                             String tableName = field.getUpdateTableName();
                                             String columnName = field.getUpdateColumnName();
                                             if (StringUtils.isBlank(tableName)) {
                                                throw new InvalidDataException(
                                                   I18nHelper.getMessage(
                                                      "CORE.MODULE.OFFICE.TMPLS.TIP.WRITE_BACK_TABLE_NAME_MISSING", ds.getDsName(), field.getShowColumnName()
                                                   )
                                                );
                                             }

                                             if (StringUtils.isBlank(columnName)) {
                                                throw new InvalidDataException(
                                                   I18nHelper.getMessage(
                                                      "CORE.MODULE.OFFICE.TMPLS.TIP.WRITE_BACK_COLUMN_NAME_MISSING", ds.getDsName(), field.getShowColumnName()
                                                   )
                                                );
                                             }

                                             Object updateId = valueMap.get(field.getSqlColumnName() + "_ID");
                                             if (ObjectUtils.isEmpty(updateId)) {
                                                throw new InvalidDataException(
                                                   I18nHelper.getMessage(
                                                      "CORE.MODULE.OFFICE.TMPLS.TIP.WRITE_BACK_ID_VALUE_MISSING", ds.getDsName(), field.getShowColumnName()
                                                   )
                                                );
                                             }

                                             String nameRangeItem = tableName + "_updateKey_" + columnName + "_updateKey_" + updateId.toString();
                                             if (cellNameRangeSet.contains(nameRangeItem)) {
                                                throw new InvalidDataException(
                                                   I18nHelper.getMessage(
                                                      "CORE.MODULE.OFFICE.TMPLS.TIP.EXIST_REPEAT_WRITE_BACK_DATA", ds.getDsName(), field.getShowColumnName()
                                                   )
                                                );
                                             }

                                             workbook.getNameRanges()
                                                .add(nameRangeItem)
                                                .setRefersToRange(sheet.getCellRange(insertTableRow + j + 1, insertTableColumn + kxxx));
                                             cellNameRangeSet.add(nameRangeItem);
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               );
            }
         }

         workbook.saveToStream(os);
      } catch (IOException var25) {
         throw new FileException(var25);
      }
   }
}
