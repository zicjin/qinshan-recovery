package com.sunwayworld.cloud.module.qms.usertrain.validator;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.cloud.module.qms.usertrain.service.CoreUserTrainService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreUserTrainSubmitTaskValidator implements DataValidator {
   @Autowired
   private CoreUserTrainService coreUserTrainService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> userTrainIdList = wrapper.parseId(Long.class);
      List<CoreUserTrainBean> coreUserTrainList = this.coreUserTrainService.selectListByIds(userTrainIdList);
      boolean requiredValid = coreUserTrainList.stream()
         .noneMatch(
            e -> StringUtils.isEmpty(e.getTrainCategory())
               || StringUtils.isEmpty(e.getCourseName())
               || e.getStartDate() == null
               || e.getEndDate() == null
               || e.getClassHour() == null
               || StringUtils.isEmpty(e.getSpeakerName())
               || StringUtils.isEmpty(e.getSpeakerOrgName())
               || StringUtils.isEmpty(e.getTrainPlace())
               || StringUtils.isEmpty(e.getTraineeName())
               || StringUtils.isEmpty(e.getTrainContent())
               || StringUtils.isEmpty(e.getTrainMethod())
         );
      if (!requiredValid) {
         this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
         return false;
      } else {
         return true;
      }
   }
}
