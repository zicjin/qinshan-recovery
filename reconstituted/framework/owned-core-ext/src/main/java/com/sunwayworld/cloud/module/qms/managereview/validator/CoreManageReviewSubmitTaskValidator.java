package com.sunwayworld.cloud.module.qms.managereview.validator;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreManageReviewSubmitTaskValidator implements DataValidator {
   @Autowired
   private CoreManageReviewService coreManageReviewService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> manageReviewIdList = wrapper.parseId(Long.class);
      List<CoreManageReviewBean> coreManageReviewList = this.coreManageReviewService.selectListByIds(manageReviewIdList);
      boolean requiredValid = coreManageReviewList.stream()
         .noneMatch(
            e -> StringUtils.isEmpty(e.getReviewMethod())
               || e.getReviewDate() == null
               || StringUtils.isEmpty(e.getChairUserName())
               || StringUtils.isEmpty(e.getReviewPlace())
               || StringUtils.isEmpty(e.getReviewPurpose())
               || StringUtils.isEmpty(e.getReviewStandard())
               || StringUtils.isEmpty(e.getReviewContent())
               || StringUtils.isEmpty(e.getReviewUserName())
               || StringUtils.isEmpty(e.getReviewReq())
         );
      if (!requiredValid) {
         this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
         return false;
      } else {
         return true;
      }
   }
}
