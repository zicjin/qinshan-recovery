package com.sunwayworld.cloud.module.pdf.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunwayworld.cloud.module.pdf.bean.CorePageAreaDTO;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentBean;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfDocumentDTO;
import com.sunwayworld.cloud.module.pdf.bean.CorePdfImageDTO;
import com.sunwayworld.cloud.module.pdf.core.CorePdfManager;
import com.sunwayworld.cloud.module.pdf.persistent.dao.CorePdfDocumentDao;
import com.sunwayworld.cloud.module.pdf.service.CorePdfDocumentService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.service.CoreFileService;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CorePdfDocumentServiceImpl implements CorePdfDocumentService {
   @Autowired
   private CorePdfDocumentDao coreDocumentPageDao;
   @Autowired
   @Lazy
   private CoreFileService coreFileService;

   public CorePdfDocumentDao getDao() {
      return this.coreDocumentPageDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CorePdfDocumentBean corePdfDocument = wrapper.parseUnique(CorePdfDocumentBean.class);
      corePdfDocument.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(corePdfDocument);
      return corePdfDocument.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void createPdfDocumentInfoByFileId(Long fileId) {
      List<CorePdfDocumentBean> corePdfDocumentList = this.selectListByFilter(
         SearchFilter.instance().match("FILEID", fileId).filter(MatchPattern.EQ), new Order[]{Order.desc("CREATEDTIME")}
      );
      if (corePdfDocumentList.isEmpty()) {
         CoreFileBean corefile = this.coreFileService.selectById(fileId);

         try {
            List<CorePdfDocumentBean> documentPageList = CorePdfManager.getPdfDocumentList(corefile);
            List<CorePdfImageDTO> corePdImageDTOList = CorePdfManager.getPdfImageList(corefile);
            if (documentPageList.size() == corePdImageDTOList.size()) {
               for (int i = 0; i < documentPageList.size(); i++) {
                  CorePdfDocumentBean documentPage = documentPageList.get(i);
                  documentPage.setId(ApplicationContextHelper.getNextIdentity());
                  documentPage.setImageUrl(corePdImageDTOList.get(i).getImageName());
                  documentPage.setWidth(corePdImageDTOList.get(i).getImageWidth());
                  documentPage.setHeight(corePdImageDTOList.get(i).getImageHeight());
                  documentPage.setFileId(fileId);
                  corePdfDocumentList.add(documentPage);
               }

               this.getDao().insert(corePdfDocumentList);
            }
         } catch (Exception var8) {
            throw new UnexpectedException("T_CORE_PDF_DOCUMENT.TIP.PARSING_FAILED");
         }
      }
   }

   @Override
   public Pagination<CorePdfDocumentDTO> selectCorePdfDocumentPagination(Long fileId, RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      parameter.setFilter(SearchFilter.instance().match("FILEID", fileId).filter(MatchPattern.EQ));
      List<CorePdfDocumentDTO> coreDocumentPageDTOList = new ArrayList<>();
      Page<CorePdfDocumentBean> pagination = this.selectPagination(parameter, wrapper.extractPageRowBounds());
      pagination.getRows().stream().forEach(item -> {
         CorePdfDocumentDTO coreDocumentPageDTO = new CorePdfDocumentDTO();
         BeanUtils.copyProperties(item, coreDocumentPageDTO);
         List<CorePageAreaDTO> coreAreaInfoBeanList = new ArrayList<>();
         if (!StringUtils.isEmpty(item.getAreaInfo())) {
            Type targetClassType = (new TypeToken<ArrayList<CorePageAreaDTO>>() {}).getType();
            coreAreaInfoBeanList = new Gson().fromJson(item.getAreaInfo(), targetClassType);
         }

         coreDocumentPageDTO.setAreas(coreAreaInfoBeanList);
         coreDocumentPageDTOList.add(coreDocumentPageDTO);
      });
      return new Pagination<>(pagination.getTotal(), pagination.getPageSize(), pagination.getPageNumber(), coreDocumentPageDTOList, pagination.getTotalMap());
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void savePdfDocumentInfo(RestJsonWrapperBean wrapper) {
      List<CorePdfDocumentBean> pdfDocumentList = new ArrayList<>();
      wrapper.parse(CorePdfDocumentDTO.class).stream().forEach(coreDocumentPageDTO -> {
         CorePdfDocumentBean coreDocumentPage = new CorePdfDocumentBean();
         BeanUtils.copyProperties(coreDocumentPageDTO, coreDocumentPage);
         List<CorePageAreaDTO> corePageAreaInfoList = coreDocumentPageDTO.getAreas();
         if (corePageAreaInfoList != null && !corePageAreaInfoList.isEmpty()) {
            coreDocumentPage.setAreaInfo(JSONObject.toJSON(corePageAreaInfoList).toString());
         } else {
            coreDocumentPage.setAreaInfo(null);
         }

         pdfDocumentList.add(coreDocumentPage);
      });
      this.getDao().update(pdfDocumentList, new String[]{"AREAINFO"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteCorePdfDocumentByFileId(Long fileId) {
      CorePdfDocumentBean corePdfDocument = new CorePdfDocumentBean();
      corePdfDocument.setFileId(fileId);
      this.getDao().deleteBy(corePdfDocument, new String[]{"FILEID"});
   }
}
