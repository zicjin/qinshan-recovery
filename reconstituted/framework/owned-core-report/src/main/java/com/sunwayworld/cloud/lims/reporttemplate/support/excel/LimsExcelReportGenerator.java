package com.sunwayworld.cloud.lims.reporttemplate.support.excel;

import com.deepoove.poi.config.ConfigureBuilder;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.cloud.lims.reporttemplate.support.LimsReportGenerator;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataList;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.helper.LimsTemplateDataCacheHolder;
import com.sunwayworld.cloud.lims.reporttemplate.support.resolver.LimsSqlDataResolver;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.JxlsUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FilePathManager;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.jxls.common.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimsExcelReportGenerator implements LimsReportGenerator {
   private static final Logger LOGGER = LoggerFactory.getLogger(LimsExcelReportGenerator.class);
   private LimsReportTemplateBean reportTemplateBean;
   private MapperParameter parameter;

   public LimsExcelReportGenerator(LimsReportTemplateBean reportTemplateBean, MapperParameter parameter) {
      this.reportTemplateBean = reportTemplateBean;
      this.parameter = parameter;
   }

   @Override
   public Path getTemplateFile() {
      Long templateFileId = this.reportTemplateBean.getTemplateFileId();
      if (templateFileId == null) {
         throw new UnexpectedException("LIMS.MODULE.REPORTTEMPLATES.TIP.NO_TEMPLATE_FILE");
      } else {
         CoreFileService coreFileService = ApplicationContextHelper.getBean(CoreFileService.class);
         CoreFileBean templateFileBean = coreFileService.selectByIdIfPresent(templateFileId);
         if (templateFileBean == null) {
            throw new UnexpectedException("LIMS.MODULE.REPORTTEMPLATES.TIP.NO_TEMPLATE_FILE");
         } else {
            return LimsFileUtils.getFilePath(templateFileBean);
         }
      }
   }

   @Override
   public LimsTemplateDataMap buildParam(ConfigureBuilder builder) {
      LOGGER.warn("** BEGIN TO INIT DATA FOR THE REPORT **");
      LimsTemplateDataMap<Object> dataMap = new LimsTemplateDataMap<>();
      long startGetParamTime = System.currentTimeMillis();
      LimsReportTemplateSourceService reportTemplateSourceService = ApplicationContextHelper.getBean(LimsReportTemplateSourceService.class);
      List<LimsReportTemplateSourceBean> sourceList = reportTemplateSourceService.selectListByFilter(
         SearchFilter.instance().match("templateId", this.reportTemplateBean.getId()).filter(MatchPattern.EQ), new Order[0]
      );
      if (!CollectionUtils.isEmpty(sourceList)) {
         dataMap = LimsSqlDataResolver.getResolver(this.parameter, sourceList).resolve();
      }

      LOGGER.warn("** INT DATA FOR THE REPORT FINISHED - USED [{}s] **", ArithUtils.div(System.currentTimeMillis() - startGetParamTime, 1000.0));
      return dataMap;
   }

   @Override
   public FilePathDTO buildReport() {
      long beginTime = System.currentTimeMillis();
      LOGGER.warn("************************* START TO GENERATE REPORT *******************");
      Path templateFile = this.getTemplateFile();
      LOGGER.warn("**GENERATE REPORT WITH TEMPLATE [{}]**", this.reportTemplateBean.getTemplateName());
      FilePathDTO copyFileRepresentation = FilePathDTO.of(
         FileScope.temp.name(),
         "" + System.currentTimeMillis(),
         UUID.randomUUID().toString(),
         DateTimeUtils.formatLocalDate(LocalDate.now()) + "-" + this.reportTemplateBean.getTemplateFileName()
      );
      Path copyDirPath = FilePathManager.getLocalDirPath(copyFileRepresentation);
      Path absolutePath = FilePathManager.getLocalPath(copyFileRepresentation);

      try {
         Files.createDirectories(copyDirPath);
         Context context = new Context(this.buildParam(null));
         JxlsUtils.exportExcel(templateFile.toFile(), absolutePath.toFile(), context);
         copyFileRepresentation = this.mergeSubExcelFile(context, copyFileRepresentation);
      } catch (Exception var11) {
         LOGGER.error("generate report error", (Throwable)var11);
         throw new FileException(var11);
      } finally {
         LimsTemplateDataCacheHolder.clean();
      }

      LOGGER.warn(
         "************************* GENERATE REPORT FINISHED - USED [{}s] *******************", ArithUtils.div(System.currentTimeMillis() - beginTime, 1000.0)
      );
      return copyFileRepresentation;
   }

   private FilePathDTO mergeSubExcelFile(Context context, FilePathDTO mainFileRepresentation) {
      Object excelSheetTemplate = context.getVar("excelsheettemplate");
      if (ObjectUtils.isEmpty(excelSheetTemplate)) {
         return mainFileRepresentation;
      } else {
         Path mainPath = FilePathManager.getLocalPath(mainFileRepresentation);
         List<File> allFiles = new ArrayList<>();
         allFiles.add(mainPath.toFile());

         for (CoreFileBean coreFileBean : (LimsTemplateDataList<CoreFileBean>)excelSheetTemplate) {
            Path subtemplateFilePath = LimsFileUtils.getFilePath(coreFileBean);
            FilePathDTO outFilePathDTO = FilePathDTO.of(
               FileScope.temp.name(),
               "" + System.currentTimeMillis(),
               UUID.randomUUID().toString(),
               DateTimeUtils.formatLocalDate(LocalDate.now()) + "-" + coreFileBean.getName()
            );
            Path outSubAbsolutePath = FilePathManager.getLocalPath(outFilePathDTO);
            LOGGER.warn("**RENDER SUB-EXCEL-REPORT WITH TEMPLATE START [{}]**", coreFileBean.getName());
            long start = System.currentTimeMillis();

            try {
               Files.createDirectories(FilePathManager.getLocalDirPath(outFilePathDTO));
               JxlsUtils.exportExcel(subtemplateFilePath.toFile(), outSubAbsolutePath.toFile(), context);
               LOGGER.warn("**RENDER SUB-EXCEL-REPORT WITH TEMPLATE END,耗时={} [{}]**", System.currentTimeMillis() - start, coreFileBean.getName());
               allFiles.add(outSubAbsolutePath.toFile());
            } catch (Exception var16) {
               LOGGER.error("**RENDER SUB-EXCEL-REPORT WITH TEMPLATE 失败={},耗时={} [{}]**", var16, System.currentTimeMillis() - start, coreFileBean.getName());
            }
         }

         if (allFiles.size() > 1) {
            FilePathDTO mergeFilePathDTO = FilePathDTO.of(
               FileScope.temp.name(),
               "" + System.currentTimeMillis(),
               UUID.randomUUID().toString(),
               DateTimeUtils.formatLocalDate(LocalDate.now()) + "-merge-" + this.reportTemplateBean.getTemplateFileName()
            );
            Path mergePath = FilePathManager.getLocalPath(mergeFilePathDTO);
            long start = System.currentTimeMillis();

            try {
               Files.createDirectories(FilePathManager.getLocalDirPath(mergeFilePathDTO));
               LOGGER.warn("**RENDER MERGE-EXCEL-REPORT WITH TEMPLATE START **");
               JxlsUtils.mergeExcelFile(allFiles, mergePath.toFile());
               mainFileRepresentation = mergeFilePathDTO;
               LOGGER.warn("**RENDER MERGE-EXCEL-REPORT WITH TEMPLATE END,耗时={},mergePath={} **", System.currentTimeMillis() - start, mergePath);
            } catch (Exception var15) {
               LOGGER.warn("**RENDER MERGE-EXCEL-REPORT WITH TEMPLATE 失败={},耗时={},mergePath={} **", var15, System.currentTimeMillis() - start, mergePath);
            }
         }

         return mainFileRepresentation;
      }
   }

   public LimsReportTemplateBean getReportTemplateBean() {
      return this.reportTemplateBean;
   }

   public void setReportTemplateBean(LimsReportTemplateBean reportTemplateBean) {
      this.reportTemplateBean = reportTemplateBean;
   }
}
