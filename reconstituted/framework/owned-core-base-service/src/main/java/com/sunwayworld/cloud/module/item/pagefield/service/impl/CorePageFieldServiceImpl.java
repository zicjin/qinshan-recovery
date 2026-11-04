package com.sunwayworld.cloud.module.item.pagefield.service.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldDTO;
import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldDTO;
import com.sunwayworld.cloud.module.item.pagefield.persistent.dao.CorePageFieldDao;
import com.sunwayworld.cloud.module.item.pagefield.service.CorePageFieldCompFieldService;
import com.sunwayworld.cloud.module.item.pagefield.service.CorePageFieldCompService;
import com.sunwayworld.cloud.module.item.pagefield.service.CorePageFieldService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CorePageFieldServiceImpl implements CorePageFieldService {
   @Autowired
   private CorePageFieldDao corePageFieldDao;
   @Autowired
   private CorePageFieldCompService corePageFieldCompService;
   @Autowired
   private CorePageFieldCompFieldService corePageFieldCompFieldService;

   public CorePageFieldDao getDao() {
      return this.corePageFieldDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CorePageFieldBean corePageField = jsonWrapper.parseUnique(CorePageFieldBean.class);
      corePageField.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(corePageField);
      return corePageField.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertComp(Long pfId, RestJsonWrapperBean jsonWrapper) {
      return this.corePageFieldCompService.insert(pfId, jsonWrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteComp(Long pfId, RestJsonWrapperBean jsonWrapper) {
      this.corePageFieldCompService.delete(jsonWrapper);
   }

   @Override
   public Page<CorePageFieldCompBean> selectCompPagination(Long pfId, RestJsonWrapperBean jsonWrapper) {
      return this.corePageFieldCompService.selectPaginationByFilter(SearchFilter.instance().match("PFID", pfId).filter(MatchPattern.EQ), jsonWrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertCompField(Long pfcId, RestJsonWrapperBean jsonWrapper) {
      return this.corePageFieldCompFieldService.insert(pfcId, jsonWrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteCompField(Long pfcId, RestJsonWrapperBean jsonWrapper) {
      this.corePageFieldCompFieldService.delete(jsonWrapper);
   }

   @Override
   public Page<CorePageFieldCompFieldBean> selectCompFieldPagination(Long pfcId, RestJsonWrapperBean jsonWrapper) {
      return this.corePageFieldCompFieldService.selectPaginationByFilter(SearchFilter.instance().match("PFCID", pfcId).filter(MatchPattern.EQ), jsonWrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void swapCompField(Long pfcId, Long firstId, Long secondId) {
      this.corePageFieldCompFieldService.swap("ORDERNO", firstId, secondId);
   }

   @Override
   public List<CorePageFieldDTO> selectCompFieldList(Long pfId) {
      List<CorePageFieldCompBean> compList = this.corePageFieldCompService
         .selectListByFilter(SearchFilter.instance().match("PFID", pfId).filter(MatchPattern.EQ), new Order[0]);
      if (compList.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<Long> pfcIdList = compList.stream().map(CorePageFieldCompBean::getId).collect(Collectors.toList());
         List<CorePageFieldCompFieldBean> allFieldList = this.corePageFieldCompFieldService
            .selectListByFilter(SearchFilter.instance().match("PFCID", pfcIdList).filter(MatchPattern.OR), new Order[]{Order.asc("ORDERNO")});
         Map<Long, List<CorePageFieldCompFieldBean>> compFieldMap = allFieldList.stream().collect(Collectors.groupingBy(CorePageFieldCompFieldBean::getPfcId));
         List<CorePageFieldDTO> dtoList = new ArrayList<>();

         for (CorePageFieldCompBean comp : compList) {
            CorePageFieldDTO dto = new CorePageFieldDTO();
            dto.setComp(comp);
            List<CorePageFieldCompFieldDTO> compFieldDTOList = this.transferFieldDTO(compFieldMap.get(comp.getId()));
            dto.setFieldList(compFieldDTOList);
            dtoList.add(dto);
         }

         return dtoList;
      }
   }

   private List<CorePageFieldCompFieldDTO> transferFieldDTO(List<CorePageFieldCompFieldBean> compFieldList) {
      List<CorePageFieldCompFieldDTO> compFieldDTOList = new ArrayList<>();

      for (CorePageFieldCompFieldBean compField : compFieldList) {
         CorePageFieldCompFieldDTO compFieldDTO = new CorePageFieldCompFieldDTO();
         BeanUtils.copyProperties(compField, compFieldDTO);
         compFieldDTO.setType(this.getType(compField.getType()));
         compFieldDTO.setCategory(this.getCategory(compField));
         compFieldDTO.setColspan(compField.getColspan() == null ? null : NumberUtils.parseLong(compField.getColspan()));
         compFieldDTO.setEditor("1".equals(compField.getEditor()) ? true : null);
         compFieldDTO.setReadonly("1".equals(compField.getReadonly()) ? true : null);
         compFieldDTO.setWidth(compField.getWidth() == null ? null : NumberUtils.parseLong(compField.getWidth()));
         compFieldDTOList.add(compFieldDTO);
      }

      return compFieldDTOList;
   }

   private String getCategory(CorePageFieldCompFieldBean compField) {
      if ("SELECT".equals(compField.getType())) {
         return compField.getSelectDs();
      } else {
         return "CHOOSE".equals(compField.getType()) ? compField.getChooseDs() : null;
      }
   }

   private String getType(String type) {
      if ("STRING".equals(type)) {
         return null;
      } else if ("DATETIME".equals(type)) {
         return "dateTime";
      } else {
         return type == null ? null : type.toLowerCase();
      }
   }
}
