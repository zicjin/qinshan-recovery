package com.sunwayworld.module.sys.audit.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialRoleMenuPermissionColumnFilter extends AbstractSpecialAuditLogColumnGetNameFilter {
   private static final String COLUMN_NAME = PersistableMetadataHelper.getTableName(CoreMenuBean.class);
   private static final String TABLE_NAME = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
   @Autowired
   private CoreMenuService coreMenuService;

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
         List<CoreMenuBean> coreMenuBeans = this.coreMenuService.selectListByIds(idList);
         return coreMenuBeans.stream().collect(Collectors.toMap(CoreMenuBean::getId, CoreMenuBean::getMenuName, (a, b) -> a));
      }
   }
}
