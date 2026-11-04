package com.sunwayworld.module.sys.audit.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import com.sunwayworld.module.sys.role.service.CoreRoleService;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialUserRoleColumnFilter extends AbstractSpecialAuditLogColumnGetNameFilter {
   private static final String COLUMN_NAME = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
   private static final String TABLE_NAME = PersistableMetadataHelper.getTableName(CoreUserBean.class);
   @Autowired
   private CoreRoleService coreRoleService;

   @Override
   protected String columnName() {
      return COLUMN_NAME;
   }

   @Override
   protected String tableName() {
      return TABLE_NAME;
   }

   @Override
   protected Map<String, String> valueMap(List<String> idList) {
      if (CollectionUtils.isEmpty(idList)) {
         return Collections.emptyMap();
      } else {
         List<Long> filteredIdList = idList.stream().filter(NumberUtils::isNumber).map(NumberUtils::parseLong).collect(Collectors.toList());
         List<CoreRoleBean> coreRoleBeans = this.coreRoleService.selectListByIds(filteredIdList);
         return coreRoleBeans.stream().collect(Collectors.toMap(coreRoleBean -> coreRoleBean.getId().toString(), CoreRoleBean::getRoleName, (a, b) -> a));
      }
   }
}
