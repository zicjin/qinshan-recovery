package com.sunwayworld.module.mdm.user.validator;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.mdm.user.bean.CoreModifiedUserBean;
import com.sunwayworld.module.mdm.user.service.CoreModifiedUserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreModifiedUserActivateDataValidator implements DataValidator {
   @Autowired
   private CoreModifiedUserService modifiedUserService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<CoreModifiedUserBean> itemList = wrapper.parse(CoreModifiedUserBean.class);
      List<CoreModifiedUserBean> selectedItemList = this.modifiedUserService
         .selectListByIds(itemList.stream().map(CoreModifiedUserBean::getId).collect(Collectors.toList()));
      if (selectedItemList.stream().anyMatch(i -> i.getScheduledTime() == null || i.getScheduledTime().isBefore(LocalDateTime.now()))) {
         this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.MODIFICATION_USER.TIP.SCHEDULEDTIME_INVALID"));
         return false;
      } else {
         return true;
      }
   }
}
