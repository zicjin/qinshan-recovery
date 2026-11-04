package com.sunwayworld.cloud.module.lcdp.resource.validator;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.cloud.module.lcdp.resourcelock.bean.LcdpResourceLockBean;
import com.sunwayworld.cloud.module.lcdp.resourcelock.service.LcdpResourceLockService;
import com.sunwayworld.cloud.module.lcdp.support.constant.LcdpConstant;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcpdResourceBatchDeleteableValidator implements DataValidator {
   @Autowired
   private LcdpResourceService resourceService;
   @Autowired
   private LcdpResourceLockService resourceLockService;

   @Override
   public boolean doValid(Object... args) {
      List<LcdpResourceBean> deleteResourceList = ((RestJsonWrapperBean)args[0]).parse(this.resourceService.getDao().getType());
      List<LcdpResourceBean> deleteCategoryList = deleteResourceList.stream()
         .filter(r -> StringUtils.equals("category", r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(deleteCategoryList)) {
         List<Long> categoryIdList = deleteCategoryList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceBean> moduleList = this.resourceService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("PARENTID", categoryIdList)
                  .filter(MatchPattern.OR)
                  .match("RESOURCECATEGORY", "module")
                  .filter(MatchPattern.EQ)
                  .match("DELETEFLAG", "0")
                  .filter(MatchPattern.EQ),
               new Order[0]
            );
         if (!moduleList.isEmpty()) {
            List<Long> moduleIdList = moduleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpResourceBean> pageAndScriptList = this.resourceService
               .selectListByFilter(
                  SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
                  new Order[0]
               );
            if (!pageAndScriptList.isEmpty()) {
               List<String> pageAndScriptIdList = pageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
               List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
                  .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptIdList).filter(MatchPattern.OR), new Order[0]);
               List<LcdpResourceLockBean> otherUserEditResourceList = resourceLockList.stream()
                  .filter(lock -> null != lock.getLockUserId() && !LocalContextHelper.getLoginUserId().equals(lock.getLockUserId()))
                  .collect(Collectors.toList());
               if (!otherUserEditResourceList.isEmpty()) {
                  this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.NOT_ALLOWED_DELETE_OTHER_CHECKOUT"));
                  return false;
               }
            }
         }
      }

      List<LcdpResourceBean> deleteModuleList = deleteResourceList.stream()
         .filter(r -> StringUtils.equals("module", r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(deleteModuleList)) {
         List<Long> moduleIdList = deleteModuleList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceBean> pageAndScriptList = this.resourceService
            .selectListByFilter(
               SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
            );
         if (!pageAndScriptList.isEmpty()) {
            List<String> pageAndScriptIdList = pageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
            List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
               .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptIdList).filter(MatchPattern.OR), new Order[0]);
            List<LcdpResourceLockBean> otherUserEditResourceList = resourceLockList.stream()
               .filter(lock -> null != lock.getLockUserId() && !LocalContextHelper.getLoginUserId().equals(lock.getLockUserId()))
               .collect(Collectors.toList());
            if (!otherUserEditResourceList.isEmpty()) {
               this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.NOT_ALLOWED_DELETE_OTHER_CHECKOUT"));
               return false;
            }
         }
      }

      List<LcdpResourceBean> deletePageAndScriptList = deleteResourceList.stream()
         .filter(r -> LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(r.getResourceCategory()))
         .collect(Collectors.toList());
      if (!ObjectUtils.isEmpty(deletePageAndScriptList)) {
         List<String> pageAndScriptIdList = deletePageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
         List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptIdList).filter(MatchPattern.OR), new Order[0]);
         List<LcdpResourceLockBean> otherUserEditResourceList = resourceLockList.stream()
            .filter(lock -> null != lock.getLockUserId() && !LocalContextHelper.getLoginUserId().equals(lock.getLockUserId()))
            .collect(Collectors.toList());
         if (!otherUserEditResourceList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.NOT_ALLOWED_DELETE_OTHER_CHECKOUT"));
            return false;
         }
      }

      return true;
   }
}
