package com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfReader;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqAreaDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqConfigLineBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqPreprocessBean;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfAcqResultDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.bean.CorePdfPageImageDTO;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.persistent.dao.CorePdfAcqConfigDao;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.CorePdfAcqConfigLineService;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.CorePdfAcqConfigService;
import com.sunwayworld.cloud.module.equipt.pdfacqconfig.service.CorePdfAcqPreprocessService;
import com.sunwayworld.cloud.module.equipt.support.CorePdfHepler;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.FileException;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CorePdfAcqConfigServiceImpl implements CorePdfAcqConfigService {
   private static final Logger logger = LogManager.getLogger(CorePdfAcqConfigService.class);
   @Autowired
   private CorePdfAcqConfigDao pdfAcqConfigDao;
   @Autowired
   private CoreFileService fileService;
   @Autowired
   private CorePdfAcqConfigLineService pdfAcqConfigLineService;
   @Autowired
   private CorePdfAcqPreprocessService corePdfAcqPreprocessService;
   @Autowired
   private CoreFileManager fileManager;

   public CorePdfAcqConfigDao getDao() {
      return this.pdfAcqConfigDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CorePdfAcqConfigBean pdfAcqConfig = wrapper.parseUnique(CorePdfAcqConfigBean.class);
      pdfAcqConfig.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(pdfAcqConfig);
      return pdfAcqConfig.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   public void save(Long id, RestJsonWrapperBean wrapper) {
      List<CorePdfPageDTO> pdfPageDTOList = wrapper.parse(CorePdfPageDTO.class);
      List<CorePdfAcqConfigLineBean> pdfAcqConfigLineList = new ArrayList<>();

      for (CorePdfPageDTO pdfPageDTO : pdfPageDTOList) {
         CorePdfAcqConfigLineBean pdfAcqConfigLine = new CorePdfAcqConfigLineBean();
         BeanUtils.copyProperties(pdfPageDTO, pdfAcqConfigLine);
         List<CorePdfAcqAreaDTO> pdfAcqAreaDTOList = pdfPageDTO.getAreas();
         if (!CollectionUtils.isEmpty(pdfAcqAreaDTOList)) {
            pdfAcqConfigLine.setArea(JSONObject.toJSON(pdfAcqAreaDTOList).toString());
         } else {
            pdfAcqConfigLine.setArea(null);
         }

         pdfAcqConfigLineList.add(pdfAcqConfigLine);
      }

      this.pdfAcqConfigLineService.getDao().update(pdfAcqConfigLineList, "AREA");
   }

   @Override
   public List<CorePdfAcqResultDTO> parse(Long id, Long fileId) {
      List<CorePdfAcqConfigLineBean> pdfAcqConfigLineList = this.pdfAcqConfigLineService
         .selectListByFilter(SearchFilter.instance().match("PDFACQCONFIGID", id).filter(MatchPattern.EQ), new Order[0]);
      CoreFileBean file = this.fileService.selectById(fileId);
      File tempFile = FileUtils.createTempFile();
      this.fileManager.download(file, tempFile.toPath());
      PDDocument document = CorePdfHepler.getPDDocument(tempFile);
      float pdfWidth = this.getPdfWidth(tempFile);
      List<CorePdfAcqAreaDTO> pdfAcqAreaDTOList = new ArrayList<>();

      for (CorePdfAcqConfigLineBean pdfAcqConfigLine : pdfAcqConfigLineList) {
         for (CorePdfAcqAreaDTO configPdfAcqAreaDTO : JsonUtils.parseList(pdfAcqConfigLine.getArea(), CorePdfAcqAreaDTO.class)) {
            Double scale = pdfWidth / 1500.0F / configPdfAcqAreaDTO.getScale();
            configPdfAcqAreaDTO.setPageNumber(pdfAcqConfigLine.getPageNumber());
            Double x1 = configPdfAcqAreaDTO.getX() * scale;
            Double x2 = (configPdfAcqAreaDTO.getX() + configPdfAcqAreaDTO.getWidth()) * scale;
            Double y1 = configPdfAcqAreaDTO.getY() * scale;
            Double y2 = (configPdfAcqAreaDTO.getY() + configPdfAcqAreaDTO.getHeight()) * scale;
            configPdfAcqAreaDTO.setTopXAxis(x1);
            configPdfAcqAreaDTO.setBottomXAxis(x2);
            configPdfAcqAreaDTO.setTopYAxis(y1);
            configPdfAcqAreaDTO.setBottomYAxis(y2);
            pdfAcqAreaDTOList.add(configPdfAcqAreaDTO);
         }
      }

      try {
         List<CorePdfAcqAreaDTO> fileAcqAreaDTOList = CorePdfHepler.extractTableList(document, pdfAcqAreaDTOList);
         List<CorePdfAcqResultDTO> pdfAcqResultList = CorePdfHepler.parse(fileAcqAreaDTOList);
         pdfAcqResultList = this.corePdfAcqPreprocessService.resultPreprocess(id, pdfAcqResultList);
         FileUtils.deleteQuietly(tempFile);
         return pdfAcqResultList;
      } catch (Exception var19) {
         logger.error(var19.getMessage(), (Throwable)var19);
         return CollectionUtils.emptyList();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertLine(Long id, RestJsonWrapperBean wrapper) {
      List<CoreFileBean> fileList = this.fileService.selectFileList(this.getDao().getTable(), id);
      if (fileList.isEmpty()) {
         return null;
      } else {
         CoreFileBean file = fileList.get(0);
         List<CorePdfAcqConfigLineBean> pdfAcqConfigLineList = CorePdfHepler.getPdfAcqConfigLineList(file);
         List<CorePdfPageImageDTO> pdfPageImageDTOList = CorePdfHepler.getPdfPageImageList(file);

         for (int i = 0; i < pdfAcqConfigLineList.size(); i++) {
            CorePdfAcqConfigLineBean pdfAcqConfigLine = pdfAcqConfigLineList.get(i);
            pdfAcqConfigLine.setId(ApplicationContextHelper.getNextIdentity());
            pdfAcqConfigLine.setImageUrl(pdfPageImageDTOList.get(i).getImageName());
            pdfAcqConfigLine.setWidth(pdfPageImageDTOList.get(i).getImageWidth());
            pdfAcqConfigLine.setHeight(pdfPageImageDTOList.get(i).getImageHeight());
            pdfAcqConfigLine.setPdfAcqConfigId(id);
         }

         this.pdfAcqConfigLineService.getDao().insert(pdfAcqConfigLineList);
         return pdfAcqConfigLineList.get(0).getId();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteLine(Long id, RestJsonWrapperBean wrapper) {
      CorePdfAcqConfigLineBean pdfAcqConfigLine = new CorePdfAcqConfigLineBean();
      pdfAcqConfigLine.setPdfAcqConfigId(id);
      this.pdfAcqConfigLineService.getDao().deleteBy(pdfAcqConfigLine, "PDFACQCONFIGID");
   }

   public Pagination<CorePdfPageDTO> selectLinePaginationById(Long id, RestJsonWrapperBean wrapper) {
      List<CorePdfAcqConfigLineBean> pdfAcqConfigLineList = this.pdfAcqConfigLineService
         .selectListByFilter(SearchFilter.instance().match("PDFACQCONFIGID", id).filter(MatchPattern.EQ), wrapper);
      List<CorePdfPageDTO> pdfPageDTOList = new ArrayList<>();

      for (CorePdfAcqConfigLineBean pdfAcqConfigLine : pdfAcqConfigLineList) {
         CorePdfPageDTO pdfPageDTO = new CorePdfPageDTO();
         BeanUtils.copyProperties(pdfAcqConfigLine, pdfPageDTO);
         List<CorePdfAcqAreaDTO> pdfAcqAreaDTOList = new ArrayList<>();
         if (!StringUtils.isEmpty(pdfAcqConfigLine.getArea())) {
            pdfAcqAreaDTOList = JsonUtils.parseList(pdfAcqConfigLine.getArea(), CorePdfAcqAreaDTO.class);
         }

         pdfPageDTO.setAreas(pdfAcqAreaDTOList);
         pdfPageDTOList.add(pdfPageDTO);
      }

      return new Pagination<>(pdfPageDTOList);
   }

   @Override
   public Page<CorePdfAcqPreprocessBean> selectAcqPreprocessPaginationByFileAcqConfigId(Long id, RestJsonWrapperBean wrapper) {
      return this.corePdfAcqPreprocessService.selectPaginationByFilter(SearchFilter.instance().match("PDFACQCONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public Long insertAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
      List<CorePdfAcqPreprocessBean> fileAcqPreprocessList = wrapper.parse(CorePdfAcqPreprocessBean.class);
      fileAcqPreprocessList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setPdfAcqConfigId(id);
      });
      this.corePdfAcqPreprocessService.getDao().insert(fileAcqPreprocessList);
      return fileAcqPreprocessList.get(0).getId();
   }

   @Override
   public void deleteAcqPreprocess(Long id, RestJsonWrapperBean wrapper) {
      this.corePdfAcqPreprocessService.delete(wrapper);
   }

   private float getPdfWidth(File tempFile) {
      float pdfWidth = 0.0F;

      try {
         PdfReader reader = new PdfReader(tempFile.getPath());
         return reader.getPageSize(1).getWidth();
      } catch (IOException var4) {
         throw new FileException(var4);
      }
   }
}
