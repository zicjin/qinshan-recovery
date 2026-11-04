package com.sunwayworld.cloud.module.qms.internalaudit.validator;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreInternalAuditSubmitTaskValidator implements DataValidator {
   @Autowired
   private CoreInternalAuditService coreInternalAuditService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> internalAuditIdList = wrapper.parseId(Long.class);
      List<CoreInternalAuditBean> coreInternalAuditList = this.coreInternalAuditService.selectListByIds(internalAuditIdList);
      boolean requiredValid = coreInternalAuditList.stream()
         .noneMatch(
            e -> StringUtils.isEmpty(e.getAuditPurpose())
               || e.getAuditDate() == null
               || StringUtils.isEmpty(e.getAuditContent())
               || StringUtils.isEmpty(e.getAuditRule())
               || StringUtils.isEmpty(e.getAuditSchedule())
               || StringUtils.isEmpty(e.getAuditMethod())
               || StringUtils.isEmpty(e.getOrgName())
               || StringUtils.isEmpty(e.getReportRange())
         );
      if (!requiredValid) {
         this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
         return false;
      } else {
         return true;
      }
   }
}
