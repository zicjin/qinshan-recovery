package com.sunwayworld.cloud.module.qms.applycomplaint.validator;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintBean;
import com.sunwayworld.cloud.module.qms.applycomplaint.service.CoreApplyComplaintService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreApplyComplaintStartValidator implements DataValidator {
   @Autowired
   private CoreApplyComplaintService coreApplyComplaintService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> idList = wrapper.parseId(Long.class);
      List<CoreApplyComplaintBean> coreApplyComplaintList = this.coreApplyComplaintService.getDao().selectListByIds(idList);
      boolean validFlag = coreApplyComplaintList.stream()
         .anyMatch(
            e -> StringUtils.isEmpty(e.getApplyComplaintCorp())
               || StringUtils.isEmpty(e.getDelegateName())
               || StringUtils.isEmpty(e.getDelegatePhone())
               || e.getApplyComplaintDate() == null
               || StringUtils.isEmpty(e.getMatName())
               || StringUtils.isEmpty(e.getProducCorp())
               || StringUtils.isEmpty(e.getSampledCorp())
               || e.getReportPrintDate() == null
               || StringUtils.isEmpty(e.getApplyComplaintType())
         );
      if (validFlag) {
         this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.APPLYCOMPLAINT.TIP.DATA_NOT_EMPTY"));
         return false;
      } else {
         return true;
      }
   }
}
