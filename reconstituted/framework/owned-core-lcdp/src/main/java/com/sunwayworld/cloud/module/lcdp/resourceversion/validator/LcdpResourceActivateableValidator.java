package com.sunwayworld.cloud.module.lcdp.resourceversion.validator;

import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.resourceversion.bean.LcdpResourceVersionBean;
import com.sunwayworld.cloud.module.lcdp.resourceversion.service.LcdpResourceVersionService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcdpResourceActivateableValidator implements DataValidator {
   @Autowired
   private LcdpResourceVersionService resourceVersionService;
   @Autowired
   private LcdpResourceLockService resourceLockService;

   @Override
   public boolean doValid(Object... args) {
      List<LcdpResourceVersionBean> resourceVersionList = ((RestJsonWrapperBean)args[0]).parse(this.resourceVersionService.getDao().getType());
      List<String> resourceIdList = resourceVersionList.stream().map(LcdpResourceVersionBean::getResourceId).collect(Collectors.toList());
      List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
         .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
      List<LcdpResourceLockBean> editingResourceList = resourceLockList.stream()
         .filter(lock -> !StringUtils.isEmpty(lock.getLockUserId()))
         .collect(Collectors.toList());
      if (!editingResourceList.isEmpty()) {
         this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.VALIDATOR.RESOURCE.EDIT.NOT.ALLOWD.ACTIVATE"));
         return false;
      } else {
         return true;
      }
   }
}
