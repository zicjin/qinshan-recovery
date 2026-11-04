package com.sunwayworld.module.sys.audit.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialRoleOrgPermissionColumnFilter extends AbstractSpecialAuditLogColumnGetNameFilter {
   private static final String COLUMN_NAME = PersistableMetadataHelper.getTableName(CoreOrgBean.class);
   private static final String TABLE_NAME = PersistableMetadataHelper.getTableName(CoreRoleBean.class);
   @Autowired
   private CoreOrgService coreOrgService;

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
         List<CoreOrgBean> coreOrgBeans = this.coreOrgService.selectListByIds(idList);
         return coreOrgBeans.stream().collect(Collectors.toMap(CoreOrgBean::getId, CoreOrgBean::getOrgName, (a, b) -> a));
      }
   }
}
