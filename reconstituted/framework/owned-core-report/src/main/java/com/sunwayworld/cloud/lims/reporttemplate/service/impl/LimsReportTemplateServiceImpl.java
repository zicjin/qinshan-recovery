package com.sunwayworld.cloud.lims.reporttemplate.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateColumnDTO;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateDTO;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceDTO;
import com.sunwayworld.cloud.lims.reporttemplate.persistent.dao.LimsReportTemplateDao;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateColumnService;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateService;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.cloud.lims.reporttemplate.support.LimsReportGenerator;
import com.sunwayworld.cloud.lims.reporttemplate.support.excel.LimsExcelReportGenerator;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.word.LimsWordReportGenerator;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArchiveUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.EncryptUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LimsReportTemplateServiceImpl implements LimsReportTemplateService {
   private static final Logger logger = LoggerFactory.getLogger(LimsReportTemplateServiceImpl.class);
   @Autowired
   private LimsReportTemplateDao limsReportTemplateDao;
   @Autowired
   private LimsReportTemplateSourceService limsReportTemplateSourceService;
   @Autowired
   private LimsReportTemplateColumnService limsReportTemplateColumnService;
   @Autowired
   private CoreFileService coreFileService;
   @Autowired
   private CoreFileManager coreFileManager;

   public LimsReportTemplateDao getDao() {
      return this.limsReportTemplateDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      LimsReportTemplateBean limsReportTemplate = jsonWrapper.parseUnique(LimsReportTemplateBean.class);
      limsReportTemplate.setId(ApplicationContextHelper.getNextIdentity());
      limsReportTemplate.setTemplateCode(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
      limsReportTemplate.setTemplateType("report");
      this.getDao().insert(limsReportTemplate);
      return limsReportTemplate.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void copyTemplate(RestJsonWrapperBean wrapperBean) {
      LimsReportTemplateBean template = wrapperBean.parseUnique(LimsReportTemplateBean.class);
      LimsReportTemplateBean copyTemplate = new LimsReportTemplateBean();
      String[] ignoreFields = new String[]{"id", "createdById", "createdByName", "createdByOrgId", "createdByOrgName", "createdTime"};
      BeanUtils.copyProperties(template, copyTemplate, ignoreFields);
      copyTemplate.setTemplateCode(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
      copyTemplate.setId(ApplicationContextHelper.getNextIdentity());
      copyTemplate.setTemplateFileId(null);
      copyTemplate.setTemplateFileName(null);
      SearchFilter templateFilter = SearchFilter.instance().match("templateId", template.getId()).filter(MatchPattern.EQ);
      List<LimsReportTemplateSourceBean> sourceList = this.limsReportTemplateSourceService.selectListByFilter(templateFilter, new Order[0]);
      List<Long> sourceIdList = sourceList.stream().map(LimsReportTemplateSourceBean::getId).collect(Collectors.toList());
      List<LimsReportTemplateColumnBean> columnList = this.limsReportTemplateColumnService
         .selectListByFilter(SearchFilter.instance().match("templateSourceId", sourceIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, List<LimsReportTemplateColumnBean>> columnMap = columnList.stream()
         .collect(Collectors.groupingBy(LimsReportTemplateColumnBean::getTemplateSourceId));
      List<LimsReportTemplateColumnBean> insertColumnList = new ArrayList<>();
      List<LimsReportTemplateSourceBean> insertSourceList = sourceList.stream().map(source -> {
         LimsReportTemplateSourceBean copySource = new LimsReportTemplateSourceBean();
         BeanUtils.copyProperties(source, copySource, ignoreFields);
         copySource.setId(ApplicationContextHelper.getNextIdentity());
         copySource.setTemplateId(copyTemplate.getId());
         List<LimsReportTemplateColumnBean> columnInSourceList = columnMap.get(source.getId());
         if (!CollectionUtils.isEmpty(columnInSourceList)) {
            List<LimsReportTemplateColumnBean> copyColumnsList = columnInSourceList.stream().map(column -> {
               LimsReportTemplateColumnBean copyColumn = new LimsReportTemplateColumnBean();
               BeanUtils.copyProperties(column, copyColumn, ignoreFields);
               copyColumn.setId(ApplicationContextHelper.getNextIdentity());
               copyColumn.setTemplateSourceId(copySource.getId());
               return copyColumn;
            }).collect(Collectors.toList());
            insertColumnList.addAll(copyColumnsList);
         }

         return copySource;
      }).collect(Collectors.toList());
      this.getDao().insert(copyTemplate);
      this.limsReportTemplateSourceService.getDao().insert(insertSourceList);
      this.limsReportTemplateColumnService.getDao().insert(insertColumnList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long copyTemplateWithFile(RestJsonWrapperBean wrapperBean) {
      LimsReportTemplateBean template = wrapperBean.parseUnique(LimsReportTemplateBean.class);
      LimsReportTemplateBean copyTemplate = new LimsReportTemplateBean();
      String[] ignoreFields = new String[]{"id", "createdById", "createdByName", "createdByOrgId", "createdByOrgName", "createdTime"};
      BeanUtils.copyProperties(template, copyTemplate, ignoreFields);
      copyTemplate.setTemplateCode(ApplicationContextHelper.getNextSequence(this.getDao().getTable()));
      copyTemplate.setId(ApplicationContextHelper.getNextIdentity());
      SearchFilter templateFilter = SearchFilter.instance().match("templateId", template.getId()).filter(MatchPattern.EQ);
      List<LimsReportTemplateSourceBean> sourceList = this.limsReportTemplateSourceService.selectListByFilter(templateFilter, new Order[0]);
      List<Long> sourceIdList = sourceList.stream().map(LimsReportTemplateSourceBean::getId).collect(Collectors.toList());
      List<LimsReportTemplateColumnBean> columnList = this.limsReportTemplateColumnService
         .selectListByFilter(SearchFilter.instance().match("templateSourceId", sourceIdList).filter(MatchPattern.OR), new Order[0]);
      Map<Long, List<LimsReportTemplateColumnBean>> columnMap = columnList.stream()
         .collect(Collectors.groupingBy(LimsReportTemplateColumnBean::getTemplateSourceId));
      List<LimsReportTemplateColumnBean> insertColumnList = new ArrayList<>();
      List<LimsReportTemplateSourceBean> insertSourceList = sourceList.stream().map(source -> {
         LimsReportTemplateSourceBean copySource = new LimsReportTemplateSourceBean();
         BeanUtils.copyProperties(source, copySource, ignoreFields);
         copySource.setId(ApplicationContextHelper.getNextIdentity());
         copySource.setTemplateId(copyTemplate.getId());
         List<LimsReportTemplateColumnBean> columnInSourceList = columnMap.get(source.getId());
         if (!CollectionUtils.isEmpty(columnInSourceList)) {
            List<LimsReportTemplateColumnBean> copyColumnsList = columnInSourceList.stream().map(column -> {
               LimsReportTemplateColumnBean copyColumn = new LimsReportTemplateColumnBean();
               BeanUtils.copyProperties(column, copyColumn, ignoreFields);
               copyColumn.setId(ApplicationContextHelper.getNextIdentity());
               copyColumn.setTemplateSourceId(copySource.getId());
               return copyColumn;
            }).collect(Collectors.toList());
            insertColumnList.addAll(copyColumnsList);
         }

         return copySource;
      }).collect(Collectors.toList());
      Long templateFileId = template.getTemplateFileId();
      if (templateFileId != null) {
         CoreFileBean sourceFile = this.coreFileService.selectByIdIfPresent(templateFileId);
         if (sourceFile != null) {
            CoreFileBean insertFile = new CoreFileBean();
            BeanUtils.copyProperties(sourceFile, insertFile);
            insertFile.setName(sourceFile.getName());
            insertFile.setId(ApplicationContextHelper.getNextIdentity());
            insertFile.setTargetId(this.getDao().getTable() + "$" + copyTemplate.getId());
            insertFile.setMd5Name(EncryptUtils.MD5Encrypt(copyTemplate.getId() + "$" + sourceFile.getName()));
            insertFile.setTimes(0L);
            insertFile.setCreatedTime(LocalDateTime.now());
            this.coreFileService.getDao().insert(insertFile);
            this.coreFileManager.upload(insertFile, CoreFileUtils.getLocalPath(sourceFile));
            copyTemplate.setTemplateFileId(insertFile.getId());
         }
      }

      this.getDao().insert(copyTemplate);
      this.limsReportTemplateSourceService.getDao().insert(insertSourceList);
      this.limsReportTemplateColumnService.getDao().insert(insertColumnList);
      return copyTemplate.getId();
   }

   @Override
   public String exportDataZip(RestJsonWrapperBean wrapperBean) {
      List<LimsReportTemplateBean> templateList = wrapperBean.parse(LimsReportTemplateBean.class);
      if (CollectionUtils.isEmpty(templateList)) {
         templateList = this.selectAll();
      } else {
         List<Long> idList = templateList.stream().map(LimsReportTemplateBean::getId).collect(Collectors.toList());
         templateList = this.selectListByIds(idList);
      }

      if (CollectionUtils.isEmpty(templateList)) {
         return null;
      } else {
         FilePathDTO zipFileDTO = FilePathDTO.of(
            FileScope.temp.name(),
            "" + System.currentTimeMillis(),
            UUID.randomUUID().toString(),
            DateTimeUtils.formatLocalDate(LocalDate.now()) + "-" + "报告模板数据导出.zip"
         );
         Path zipFilePath = FilePathManager.getLocalPath(zipFileDTO);
         Path parentPath = zipFilePath.getParent();
         FileUtils.makeDirs(zipFilePath);
         List<Long> idList = templateList.stream().map(LimsReportTemplateBean::getId).collect(Collectors.toList());
         List<LimsReportTemplateSourceBean> sourceList = this.limsReportTemplateSourceService
            .selectListByFilter(SearchFilter.instance().match("TemplateId", idList).filter(MatchPattern.OR), new Order[0]);
         Map<Long, List<LimsReportTemplateSourceBean>> sourceGroupingByTemplateMap = sourceList.stream()
            .collect(Collectors.groupingBy(LimsReportTemplateSourceBean::getTemplateId));
         List<Long> sourceIdList = sourceList.stream().map(LimsReportTemplateSourceBean::getId).collect(Collectors.toList());
         List<LimsReportTemplateColumnBean> columnList = this.limsReportTemplateColumnService
            .selectListByFilter(SearchFilter.instance().match("TemplateSourceId", sourceIdList).filter(MatchPattern.OR), new Order[0]);
         Map<Long, List<LimsReportTemplateColumnBean>> columnGroupingBySourceMap = columnList.stream()
            .collect(Collectors.groupingBy(LimsReportTemplateColumnBean::getTemplateSourceId));
         String targetFilePrefix = this.getDao().getTable() + "$";
         List<Long> templateFileIdList = templateList.stream()
            .filter(template -> template.getTemplateFileId() != null)
            .map(LimsReportTemplateBean::getTemplateFileId)
            .collect(Collectors.toList());
         List<CoreFileBean> templateFileList = this.coreFileService.selectListByIds(templateFileIdList);
         Map<Long, CoreFileBean> templateFileMap = templateFileList.stream()
            .collect(
               Collectors.toMap(coreFile -> NumberUtils.parseLong(StringUtils.replace(coreFile.getTargetId(), targetFilePrefix, "")), Function.identity())
            );
         List<String> targetIdList = templateList.stream().map(template -> targetFilePrefix + template.getId()).collect(Collectors.toList());
         String bizCategory = "templateTagFile";
         List<CoreFileBean> tagFileList = this.coreFileService
            .selectListByFilter(
               SearchFilter.instance().match("targetId", targetIdList).filter(MatchPattern.OR).match("bizCategory", bizCategory).filter(MatchPattern.EQ),
               new Order[0]
            );
         Map<Long, List<CoreFileBean>> tagFileGroupingByTemplateMap = tagFileList.stream()
            .collect(Collectors.groupingBy(coreFile -> NumberUtils.parseLong(StringUtils.replace(coreFile.getTargetId(), targetFilePrefix, ""))));
         List<Path> zipFilePathList = new ArrayList<>();
         templateList.forEach(template -> {
            Path templatePath = parentPath.resolve(template.getTemplateCode());

            try {
               Files.createDirectory(templatePath);
               zipFilePathList.add(templatePath);
               LimsReportTemplateDTO templateDTO = new LimsReportTemplateDTO();
               BeanUtils.copyProperties(template, templateDTO);
               List<LimsReportTemplateSourceBean> sourceInTemplateList = sourceGroupingByTemplateMap.get(template.getId());
               if (!CollectionUtils.isEmpty(sourceInTemplateList)) {
                  List<LimsReportTemplateSourceDTO> sourceDTOList = sourceInTemplateList.stream().map(source -> {
                     LimsReportTemplateSourceDTO sourceDTO = new LimsReportTemplateSourceDTO();
                     BeanUtils.copyProperties(source, sourceDTO);
                     List<LimsReportTemplateColumnBean> columnInSourceList = columnGroupingBySourceMap.get(source.getId());
                     if (!CollectionUtils.isEmpty(columnInSourceList)) {
                        List<LimsReportTemplateColumnDTO> columnDTOList = columnInSourceList.stream().map(column -> {
                           LimsReportTemplateColumnDTO columnDTO = new LimsReportTemplateColumnDTO();
                           BeanUtils.copyProperties(column, columnDTO);
                           return columnDTO;
                        }).collect(Collectors.toList());
                        sourceDTO.setColumnsList(columnDTOList);
                     }

                     return sourceDTO;
                  }).collect(Collectors.toList());
                  templateDTO.setSourceList(sourceDTOList);
               }

               Path filePath = templatePath.resolve(template.getTemplateName() + ".json");
               FileUtils.write(filePath, JSONObject.toJSONString(templateDTO));
               CoreFileBean templateFile = templateFileMap.get(template.getId());
               Path templateFilePath = LimsFileUtils.getFilePath(templateFile);
               Path copyTemplateFilePath = templatePath.resolve(templateFile.getName());
               Files.copy(templateFilePath, copyTemplateFilePath);
               List<CoreFileBean> tagFileInTempLateList = tagFileGroupingByTemplateMap.get(template.getId());
               if (!CollectionUtils.isEmpty(tagFileInTempLateList)) {
                  Path tagDirPath = templatePath.resolve(bizCategory);
                  Files.createDirectories(tagDirPath);
                  tagFileInTempLateList.forEach(tagFileBean -> {
                     Path tagPath = tagDirPath.resolve(tagFileBean.getExt());

                     try {
                        Files.createDirectories(tagPath);
                        Path copyTagFilePath = tagPath.resolve(tagFileBean.getName());
                        Files.copy(LimsFileUtils.getFilePath(tagFileBean), copyTagFilePath);
                     } catch (IOException var5x) {
                        logger.error("create template {} tag {} dir error", template.getTemplateCode(), tagFileBean.getExt(), var5x);
                     }
                  });
               }
            } catch (IOException var17x) {
               logger.error("create template dir error {}", template.getTemplateCode(), var17x);
            }
         });
         if (!CollectionUtils.isEmpty(zipFilePathList)) {
            ArchiveUtils.zip(zipFilePath, zipFilePathList.toArray(new Path[0]));
            this.coreFileManager.upload(zipFileDTO, FilePathManager.getLocalPath(zipFileDTO));
            return this.coreFileManager.getDownloadUrl(zipFileDTO);
         } else {
            return null;
         }
      }
   }

   @Override
   public Page<LimsReportTemplateSourceBean> selectTemplateSourcePaginationByReportTemplateId(Long id, RestJsonWrapperBean wrapper) {
      return this.limsReportTemplateSourceService.selectPaginationByFilter(SearchFilter.instance().match("templateId", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public Long insertTemplateSource(Long id, RestJsonWrapperBean wrapper) {
      List<LimsReportTemplateSourceBean> limsReportTemplateSourceList = wrapper.parse(LimsReportTemplateSourceBean.class);
      limsReportTemplateSourceList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setTemplateId(id);
      });
      this.limsReportTemplateSourceService.getDao().insert(limsReportTemplateSourceList);
      return limsReportTemplateSourceList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Override
   public void deleteTemplateSource(Long id, RestJsonWrapperBean wrapper) {
      this.limsReportTemplateSourceService.delete(wrapper);
   }

   @Override
   public Page<LimsReportTemplateColumnBean> selectTemplateColumnPaginationByTemplateSourceId(Long id, RestJsonWrapperBean wrapper) {
      return this.limsReportTemplateColumnService
         .selectPaginationByFilter(SearchFilter.instance().match("templateSourceId", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public LimsReportTemplateSourceBean selectTemplateSourceDetail(Long id) {
      return this.limsReportTemplateSourceService.selectById(id);
   }

   @Override
   public RestValidationResultBean validateSql(Long id, String columnName, String columnValue) {
      return this.limsReportTemplateSourceService.validateTemplateSourceSql(columnName, columnValue);
   }

   @Override
   public RestValidationResultBean validateSql(RestJsonWrapperBean wrapperBean) {
      String validateUniqueFieldJson = wrapperBean.getParamValue("vu");
      if (null == validateUniqueFieldJson) {
         return new RestValidationResultBean(true);
      } else {
         Map<String, Object> validateUniqueFieldMap = JSONObject.parseObject(
            validateUniqueFieldJson, new TypeReference<Map<String, Object>>() {}, new Feature[0]
         );
         String sql = ObjectUtils.toString(validateUniqueFieldMap.get("sql"));
         return this.limsReportTemplateSourceService.validateTemplateSourceSql("sql", sql);
      }
   }

   @Transactional
   @Override
   public String generatorReport(Long id, RestJsonWrapperBean wrapperBean) {
      MapperParameter parameter = wrapperBean.extractMapFilter();
      LimsReportGenerator reportGenerator = this.getReportGenerator(id, parameter);
      FilePathDTO templateFile = reportGenerator.buildReport();
      CoreFileBean coreFileBean = new CoreFileBean();
      coreFileBean.setTargetId(this.getDao().getTable() + "$" + id);
      coreFileBean.setBizCategory("tempFile");
      Long fileId = this.coreFileService.upload(coreFileBean, FilePathManager.getLocalPath(templateFile).toFile());
      return fileId.toString();
   }

   private LimsReportGenerator getReportGenerator(Long id, MapperParameter parameter) {
      LimsReportTemplateBean limsReportTemplateBean = this.selectByIdIfPresent(id);
      if (ObjectUtils.isEmpty(limsReportTemplateBean)) {
         throw new InvalidDataException("报告模板未维护！");
      } else {
         Long summaryFileId = limsReportTemplateBean.getTemplateFileId();
         if (ObjectUtils.isEmpty(summaryFileId)) {
            throw new InvalidDataException("请维护【" + limsReportTemplateBean.getTemplateName() + "】报告模板文件！");
         } else {
            CoreFileBean summaryCoreFileBean = this.coreFileService.selectByIdIfPresent(summaryFileId);

            try {
               Path filePath = LimsFileUtils.getFilePath(summaryCoreFileBean);
               CoreFileManager coreFileManager = ApplicationContextHelper.getBean(CoreFileManager.class);

               try {
                  coreFileManager.download(summaryCoreFileBean);
               } catch (Exception var9) {
                  logger.error("some error happened when download file {}", JSONObject.toJSONString(summaryCoreFileBean), var9);
                  throw new FileException(var9);
               }

               if (!filePath.toFile().exists()) {
                  throw new InvalidDataException("请维护【" + limsReportTemplateBean.getTemplateName() + "】报告模板文件！");
               }
            } catch (Exception var10) {
               throw new InvalidDataException("请维护【" + limsReportTemplateBean.getTemplateName() + "】报告模板文件！");
            }

            LimsReportGenerator reportGenerator = null;
            String name = summaryCoreFileBean.getName();
            if (name.toLowerCase().endsWith("xls") || name.toLowerCase().endsWith("xlsx")) {
               reportGenerator = new LimsExcelReportGenerator(limsReportTemplateBean, parameter);
            } else if (name.toLowerCase().endsWith("docx")) {
               reportGenerator = new LimsWordReportGenerator(limsReportTemplateBean, parameter);
            }

            return reportGenerator;
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void refreshSourceColumn(Long id, RestJsonWrapperBean wrapperBean) {
      this.limsReportTemplateColumnService.refreshSourceColumn(id, wrapperBean);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Override
   public CoreFileBean generatorReport(Long id, String dbTable, RestJsonWrapperBean wrapperBean) {
      MapperParameter parameter = wrapperBean.extractMapFilter();
      LimsReportGenerator reportGenerator = this.getReportGenerator(id, parameter);
      FilePathDTO templateFile = reportGenerator.buildReport();
      CoreFileBean coreFileBean = new CoreFileBean();
      coreFileBean.setTargetId(dbTable + "$" + ObjectUtils.toString(Optional.ofNullable(parameter.get("id")).orElse(id)));
      coreFileBean.setBizCategory("wordReport");
      Long fileId = this.coreFileService.upload(coreFileBean, FilePathManager.getLocalPath(templateFile).toFile());
      return this.coreFileService.selectDetail(fileId);
   }
}
