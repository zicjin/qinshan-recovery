package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialRoleMenuPermissionRecordLineFilter extends AbstractSpecialColumnRecordLineFilter implements SpecialRecordLineFilter {
   @Autowired
   private CoreMenuService coreMenuService;

   @Override
   protected String columnName() {
      return PersistableMetadataHelper.getTableName(CoreMenuBean.class);
   }

   @Override
   protected String tableName() {
      return PersistableMetadataHelper.getTableName(CoreRoleBean.class);
   }

   @Override
   protected Map<String, String> valueMap(List<String> idList) {
      List<CoreMenuBean> coreMenuBeans = this.coreMenuService.selectListByIds(idList);
      return coreMenuBeans.stream()
         .filter(coreMenuBean -> !StringUtils.isBlank(coreMenuBean.getMenuName()))
         .collect(Collectors.toMap(CoreMenuBean::getId, CoreMenuBean::getMenuName, (a, b) -> a));
   }
}
