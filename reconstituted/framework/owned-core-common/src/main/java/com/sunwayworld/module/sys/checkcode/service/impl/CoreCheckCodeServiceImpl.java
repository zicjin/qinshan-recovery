package com.sunwayworld.module.sys.checkcode.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.admin.config.service.CoreAdminCheckConfigService;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import com.sunwayworld.module.sys.checkcode.dao.CoreCheckCodeDao;
import com.sunwayworld.module.sys.checkcode.service.CoreCheckCodeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCheckCodeServiceImpl implements CoreCheckCodeService {
   @Autowired
   private CoreCheckCodeDao coreCheckCodeDao;
   @Autowired
   private CoreAdminCheckConfigService adminCheckConfigService;

   public CoreCheckCodeDao getDao() {
      return this.coreCheckCodeDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreCheckCodeBean coreCheckCode = jsonWrapper.parseUnique(CoreCheckCodeBean.class);
      coreCheckCode.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreCheckCode);
      this.adminCheckConfigService.insertByCheckCode(coreCheckCode);
      return coreCheckCode.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<Long> idList = wrapper.parseId(Long.class);
      List<CoreCheckCodeBean> checkCodeList = this.getDao().selectListByIds(idList, new Order[0]);
      List<String> codeCategoryList = checkCodeList.stream().map(e -> e.getCodeCategory()).distinct().collect(Collectors.toList());
      CoreCheckCodeService.super.delete(wrapper);
      List<CoreCheckCodeBean> deleteByList = this.getDao().selectListByOneColumnValues(codeCategoryList, "CODECATEGORY", new Order[0]);
      List<String> checkConfigIdList = new ArrayList<>();
      codeCategoryList.stream().forEach(e -> {
         boolean flag = deleteByList.stream().noneMatch(f -> ObjectUtils.equals(e, f.getCodeCategory()));
         if (flag) {
            checkConfigIdList.add(e);
         }
      });
      if (checkConfigIdList.size() > 0) {
         this.adminCheckConfigService.getDao().deleteByIdList(checkConfigIdList);
      }
   }

   @Override
   public List<CoreCheckCodeBean> selectCheckableList() {
      Map<String, String> requestParamMap = ServletUtils.getCurrentRequestParamMap();
      return !CollectionUtils.containsKeyIgnorecase(requestParamMap, "codeCategory")
         ? CollectionUtils.emptyList()
         : this.selectListByFilter(
            SearchFilter.instance().match("CODECATEGORY", requestParamMap.get("codeCategory")).filter(MatchPattern.EQ),
            new Order[]{Order.asc("GROUPNAME"), Order.asc("ORDERNO")}
         );
   }

   @Override
   public List<String> selectCategoryList() {
      List<CoreCheckCodeBean> checkCodeList = this.getDao().selectAll(new String[]{"CODECATEGORY"});
      return checkCodeList.stream().map(e -> e.getCodeCategory()).distinct().collect(Collectors.toList());
   }
}
