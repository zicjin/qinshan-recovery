package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.sys.role.bean.CoreRoleBean;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecialRoleOrgPermissionRecordLineFilter extends AbstractSpecialColumnRecordLineFilter implements SpecialRecordLineFilter {
   @Autowired
   private CoreOrgService coreOrgService;

   @Override
   protected String columnName() {
      return PersistableMetadataHelper.getTableName(CoreOrgBean.class);
   }

   @Override
   protected String tableName() {
      return PersistableMetadataHelper.getTableName(CoreRoleBean.class);
   }

   @Override
   protected Map<String, String> valueMap(List<String> idList) {
      List<CoreOrgBean> coreOrgBeans = this.coreOrgService.selectListByIds(idList);
      return coreOrgBeans.stream()
         .filter(coreOrgBean -> !StringUtils.isBlank(coreOrgBean.getOrgName()))
         .collect(Collectors.toMap(CoreOrgBean::getId, CoreOrgBean::getOrgName, (a, b) -> a));
   }
}
