package com.sunwayworld.cloud.lims.reporttemplate.support.word;

import com.deepoove.poi.config.ConfigureBuilder;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateBean;
import com.sunwayworld.cloud.lims.reporttemplate.bean.LimsReportTemplateSourceBean;
import com.sunwayworld.cloud.lims.reporttemplate.service.LimsReportTemplateSourceService;
import com.sunwayworld.cloud.lims.reporttemplate.support.LimsReportGenerator;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.LimsTemplateDataMap;
import com.sunwayworld.cloud.lims.reporttemplate.support.data.WaterMarkBean;
import com.sunwayworld.cloud.lims.reporttemplate.support.helper.LimsTemplateDataCacheHolder;
import com.sunwayworld.cloud.lims.reporttemplate.support.resolver.LimsSqlDataResolver;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.LimsFileUtils;
import com.sunwayworld.cloud.lims.reporttemplate.support.util.WordExportUtil;
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
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LimsWordReportGenerator implements LimsReportGenerator {
   private static final Logger LOGGER = LoggerFactory.getLogger(LimsWordReportGenerator.class);
   private LimsReportTemplateBean reportTemplateBean;
   private MapperParameter parameter;
   private Boolean updateMenu = true;
   private WaterMarkBean waterMarkBean;

   public LimsWordReportGenerator(LimsReportTemplateBean reportTemplateBean, MapperParameter parameter) {
      this.reportTemplateBean = reportTemplateBean;
      this.parameter = parameter;
   }

   public LimsWordReportGenerator(LimsReportTemplateBean reportTemplateBean, MapperParameter parameter, Boolean updateMenu) {
      this.reportTemplateBean = reportTemplateBean;
      this.parameter = parameter;
      this.updateMenu = updateMenu;
   }

   @Override
   public Path getTemplateFile() {
      Long templateFileId = this.reportTemplateBean.getTemplateFileId();
      if (templateFileId == null) {
         throw new UnexpectedException("LIMS.MODULE.REPORTTEMPLATES.TIP.NO_TEMPLATE_FILE");
      } else {
         CoreFileService coreFileService = ApplicationContextHelper.getBean(CoreFileService.class);
         CoreFileBean templateFileBean = coreFileService.selectById(templateFileId);
         return LimsFileUtils.getFilePath(templateFileBean);
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

      LOGGER.warn("** GET TAG FILE FOR THE TEMPLATE **");
      CoreFileService coreFileService = ApplicationContextHelper.getBean(CoreFileService.class);
      List<CoreFileBean> tagFileList = coreFileService.selectListByFilter(
         SearchFilter.instance()
            .match("targetId", "T_LIMS_REPORT_TEMPLATE$" + this.reportTemplateBean.getId())
            .filter(MatchPattern.EQ)
            .match("bizCategory", "templateTagFile")
            .filter(MatchPattern.EQ),
         new Order[0]
      );
      LOGGER.warn("** FIND TAG FILE FOR THE TEMPLATE [{}]**", tagFileList.size());
      if (!CollectionUtils.isEmpty(tagFileList)) {
         for (CoreFileBean tagFile : tagFileList) {
            if (!StringUtils.isEmpty(tagFile.getExt())) {
               LimsTemplateDataCacheHolder.put(tagFile.getExt(), tagFile);
               dataMap.put(tagFile.getExt(), tagFile);
            }
         }
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
      String fileName = (String)this.parameter
         .getOrDefault("buildFileName", DateTimeUtils.formatLocalDate(LocalDate.now()) + "-" + this.reportTemplateBean.getTemplateFileName());
      FilePathDTO copyFileRepresentation = FilePathDTO.of(FileScope.temp.name(), "" + System.currentTimeMillis(), UUID.randomUUID().toString(), fileName);
      Path copyDirPath = FilePathManager.getLocalDirPath(copyFileRepresentation);
      FileOutputStream out = null;
      InputStream is = null;
      Path absolutePath = FilePathManager.getLocalPath(copyFileRepresentation);

      try {
         Files.createDirectories(copyDirPath);
         out = new FileOutputStream(absolutePath.toFile());
         is = new BufferedInputStream(Files.newInputStream(Paths.get(templateFile.toString())));
         WordExportUtil.build(is, out, this::buildParam, true, this.updateMenu, this.waterMarkBean);
      } catch (Exception var18) {
         LOGGER.error("generate report error", (Throwable)var18);
         throw new FileException(var18);
      } finally {
         try {
            if (out != null) {
               out.flush();
               out.close();
               LOGGER.warn("close report file stream");
            }

            if (is != null) {
               is.close();
               LOGGER.warn("close template report file stream");
            }
         } catch (IOException var17) {
            LOGGER.error("close stream error", (Throwable)var17);
         }

         LimsTemplateDataCacheHolder.clean();
      }

      LOGGER.warn(
         "************************* GENERATE REPORT FINISHED - USED [{}s] *******************", ArithUtils.div(System.currentTimeMillis() - beginTime, 1000.0)
      );
      return copyFileRepresentation;
   }

   public LimsReportTemplateBean getReportTemplateBean() {
      return this.reportTemplateBean;
   }

   public void setReportTemplateBean(LimsReportTemplateBean reportTemplateBean) {
      this.reportTemplateBean = reportTemplateBean;
   }

   public WaterMarkBean getWaterMarkBean() {
      return this.waterMarkBean;
   }

   public void setWaterMarkBean(WaterMarkBean waterMarkBean) {
      this.waterMarkBean = waterMarkBean;
   }
}
