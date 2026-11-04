package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialRoleUserRecordLineFilter extends AbstractSpecialColumnRecordLineFilter implements SpecialRecordLineFilter {
   @Autowired
   private CoreUserService coreUserService;

   @Override
   protected String columnName() {
      return "USER";
   }

   @Override
   protected String tableName() {
      return PersistableMetadataHelper.getTableName(CoreRoleBean.class);
   }

   @Override
   protected Map<String, String> valueMap(List<String> idList) {
      List<CoreUserBean> coreUserBeans = this.coreUserService.selectListByIds(idList);
      return coreUserBeans.stream()
         .filter(coreUserBean -> !StringUtils.isBlank(coreUserBean.getUserName()))
         .collect(Collectors.toMap(CoreUserBean::getId, CoreUserBean::getUserName, (a, b) -> a));
   }
}
