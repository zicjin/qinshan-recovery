package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialUserRoleRecordLineFilter extends AbstractSpecialColumnRecordLineFilter implements SpecialRecordLineFilter {
   @Autowired
   private CoreRoleService coreRoleService;

   @Override
   protected String columnName() {
      return "ROLE";
   }

   @Override
   protected String tableName() {
      return PersistableMetadataHelper.getTableName(CoreUserBean.class);
   }

   @Override
   protected Map<String, String> valueMap(List<String> idList) {
      List<Long> roleIds = idList.stream().filter(NumberUtils::isNumber).map(NumberUtils::parseLong).collect(Collectors.toList());
      List<CoreRoleBean> coreRoleBeans = this.coreRoleService.selectListByIds(roleIds);
      return coreRoleBeans.stream()
         .filter(coreRoleBean -> !StringUtils.isBlank(coreRoleBean.getRoleName()))
         .collect(Collectors.toMap(coreRoleBean -> coreRoleBean.getId().toString(), CoreRoleBean::getRoleName, (a, b) -> a));
   }
}
