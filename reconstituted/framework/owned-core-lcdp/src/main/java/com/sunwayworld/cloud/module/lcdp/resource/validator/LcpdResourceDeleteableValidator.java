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
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcpdResourceDeleteableValidator implements DataValidator {
   @Autowired
   private LcdpResourceService resourceService;
   @Autowired
   private LcdpResourceLockService resourceLockService;
   @Autowired
   private CoreUserService coreUserService;

   @Override
   public boolean doValid(Object... args) {
      boolean deleteFlag = true;
      List<LcdpResourceBean> deleteResourceList = ((RestJsonWrapperBean)args[0]).parse(this.resourceService.getDao().getType());
      LcdpResourceBean deleteResource = deleteResourceList.get(0);
      List<LcdpResourceBean> resourceList = this.resourceService
         .selectListByFilter(
            SearchFilter.instance().match("PARENTID", deleteResource.getId()).filter(MatchPattern.EQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ),
            new Order[0]
         );
      LcdpResourceBean resource = this.resourceService.selectById(deleteResource.getId());
      if (StringUtils.equals("category", resource.getResourceCategory())) {
         List<Long> moduleIdList = resourceList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
         List<LcdpResourceBean> pageAndScriptList = this.resourceService
            .selectListByFilter(
               SearchFilter.instance().match("PARENTID", moduleIdList).filter(MatchPattern.OR).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
            );
         List<String> pageAndScriptIdList = pageAndScriptList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
         Map<Long, String> resourceId2ResourceNameMap = pageAndScriptList.stream()
            .collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getResourceName));
         List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", pageAndScriptIdList).filter(MatchPattern.OR), new Order[0]);
         List<LcdpResourceLockBean> otherUserEditResourceList = resourceLockList.stream()
            .filter(lock -> null != lock.getLockUserId() && !LocalContextHelper.getLoginUserId().equals(lock.getLockUserId()))
            .collect(Collectors.toList());
         if (!otherUserEditResourceList.isEmpty()) {
            List<String> lockUserIdList = otherUserEditResourceList.stream().map(LcdpResourceLockBean::getLockUserId).collect(Collectors.toList());
            List<CoreUserBean> userList = this.coreUserService.selectListByIds(lockUserIdList);
            Map<String, String> userId2UserNameMap = userList.stream().collect(Collectors.toMap(CoreUserBean::getId, CoreUserBean::getUserName));
            otherUserEditResourceList.forEach(lock -> {
               String userName = userId2UserNameMap.get(lock.getLockUserId()) == null ? lock.getLockUserId() : userId2UserNameMap.get(lock.getLockUserId());
               String resourceName = resourceId2ResourceNameMap.get(Long.valueOf(lock.getResourceId()));
               this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.OTHER_CHECKOUT", "【" + userName + "】", resourceName) + ";###");
            });
            return false;
         }
      }

      if (StringUtils.equals("module", resource.getResourceCategory()) && !resourceList.isEmpty()) {
         List<String> resourceIdList = resourceList.stream().map(LcdpResourceBean::getId).map(String::valueOf).collect(Collectors.toList());
         List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resourceIdList).filter(MatchPattern.OR), new Order[0]);
         Map<Long, String> resourceId2ResourceNameMap = resourceList.stream()
            .collect(Collectors.toMap(LcdpResourceBean::getId, LcdpResourceBean::getResourceName));
         List<LcdpResourceLockBean> otherUserEditResourceList = resourceLockList.stream()
            .filter(lock -> null != lock.getLockUserId() && !LocalContextHelper.getLoginUserId().equals(lock.getLockUserId()))
            .collect(Collectors.toList());
         if (!otherUserEditResourceList.isEmpty()) {
            List<String> lockUserIdList = otherUserEditResourceList.stream().map(LcdpResourceLockBean::getLockUserId).collect(Collectors.toList());
            List<CoreUserBean> userList = this.coreUserService.selectListByIds(lockUserIdList);
            Map<String, String> userId2UserNameMap = userList.stream().collect(Collectors.toMap(CoreUserBean::getId, CoreUserBean::getUserName));
            otherUserEditResourceList.forEach(lock -> {
               String userName = userId2UserNameMap.get(lock.getLockUserId()) == null ? lock.getLockUserId() : userId2UserNameMap.get(lock.getLockUserId());
               String resourceName = resourceId2ResourceNameMap.get(Long.valueOf(lock.getResourceId()));
               this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.OTHER_CHECKOUT", "【" + userName + "】", resourceName) + ";###");
            });
            return false;
         }
      }

      if (LcdpConstant.RESOURCE_SCRIPT_CATEGORY_LIST.contains(resource.getResourceCategory())) {
         List<LcdpResourceLockBean> resourceLockList = this.resourceLockService
            .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resource.getId().toString()).filter(MatchPattern.EQ), new Order[0]);
         List<String> userIdList = resourceLockList.stream().map(LcdpResourceLockBean::getLockUserId).collect(Collectors.toList());
         List<CoreUserBean> userList = this.coreUserService.selectListByIds(userIdList);
         Map<String, String> userId2UserNameMap = userList.stream().collect(Collectors.toMap(CoreUserBean::getId, CoreUserBean::getUserName));
         List<LcdpResourceLockBean> editingResourceList = resourceLockList.stream()
            .filter(lock -> !StringUtils.isEmpty(lock.getLockUserId()) && !LocalContextHelper.getLoginUserId().equals(lock.getLockUserId()))
            .collect(Collectors.toList());
         if (null != resource.getEffectVersion() && !editingResourceList.isEmpty()) {
            editingResourceList.forEach(lock -> {
               String userName = userId2UserNameMap.get(lock.getLockUserId()) == null ? lock.getLockUserId() : userId2UserNameMap.get(lock.getLockUserId());
               String resourceName = resource.getResourceName();
               this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.OTHER_CHECKOUT", "【" + userName + "】", resourceName) + ";###");
            });
            deleteFlag = false;
         }
      }

      return deleteFlag;
   }
}
