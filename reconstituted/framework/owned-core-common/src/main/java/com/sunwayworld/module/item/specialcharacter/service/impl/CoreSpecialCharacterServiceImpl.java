package com.sunwayworld.module.item.specialcharacter.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.item.specialcharacter.bean.CoreCommonCharacterBean;
import com.sunwayworld.module.item.specialcharacter.bean.CoreSpecialCharacterBean;
import com.sunwayworld.module.item.specialcharacter.dao.CoreSpecialCharacterDao;
import com.sunwayworld.module.item.specialcharacter.service.CoreCommonCharacterService;
import com.sunwayworld.module.item.specialcharacter.service.CoreSpecialCharacterService;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreSpecialCharacterServiceImpl implements CoreSpecialCharacterService {
   private static final String SPECIAL_CHARACTER_CATEGORY_CODE_CATEGORY_ID = "specialCharacterCategory";
   private static final String SPECIAL_CHARACTER_CATEGORY_CUSTOM = "custom";
   @Autowired
   private CoreSpecialCharacterDao coreSpecialCharacterDao;
   @Autowired
   private CoreCommonCharacterService coreCommonCharacterService;
   @Autowired
   private CoreCodeService coreCodeService;

   public CoreSpecialCharacterDao getDao() {
      return this.coreSpecialCharacterDao;
   }

   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Transactional
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreSpecialCharacterBean specialCharacter = wrapper.parseUnique(CoreSpecialCharacterBean.class);
      specialCharacter.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(specialCharacter);
      return specialCharacter.getId();
   }

   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Transactional
   @Override
   public void updateOrInsertCommonCharacterById(Long id, RestJsonWrapperBean wrapper) {
      CoreCommonCharacterBean commonCharacter = new CoreCommonCharacterBean();
      commonCharacter.setSpecialId(id);
      commonCharacter.setCreatedById(LocalContextHelper.getLoginUserId());
      CoreCommonCharacterBean commonCharacterByFilter = this.coreCommonCharacterService.getDao().selectOneIfPresent(commonCharacter);
      if (BeanUtils.isEmpty(commonCharacterByFilter)) {
         commonCharacter.setId(ApplicationContextHelper.getNextIdentity());
         commonCharacter.setChooseQty(1L);
         this.coreCommonCharacterService.getDao().insert(commonCharacter);
      } else {
         commonCharacterByFilter.setChooseQty(commonCharacterByFilter.getChooseQty() + 1L);
         this.coreCommonCharacterService.getDao().update(commonCharacterByFilter, "CHOOSEQTY");
      }
   }

   @Override
   public Page<CoreSpecialCharacterBean> selectPagationByChooseQtyDesc(RestJsonWrapperBean wrapper) {
      MapperParameter parameter = wrapper.extractMapFilter();
      PageRowBounds rowBounds = wrapper.extractPageRowBounds();
      return this.selectPagination(() -> this.getDao().selectByChooseQtyDesc(parameter), rowBounds);
   }

   @Override
   public List<CoreCodeBean> selectCategoryList() {
      List<CoreCodeBean> codeList = this.coreCodeService
         .selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", "specialCharacterCategory").filter(MatchPattern.EQ), new Order[0]);
      CoreSpecialCharacterBean customSpecialCharacterFilter = new CoreSpecialCharacterBean();
      customSpecialCharacterFilter.setCategory("custom");
      int customSpecialCharacterQty = this.getDao().countBy(customSpecialCharacterFilter, new String[]{"CATEGORY"});
      return customSpecialCharacterQty == 0 ? codeList.stream().filter(e -> !"custom".equals(e.getCode())).collect(Collectors.toList()) : codeList;
   }
}
