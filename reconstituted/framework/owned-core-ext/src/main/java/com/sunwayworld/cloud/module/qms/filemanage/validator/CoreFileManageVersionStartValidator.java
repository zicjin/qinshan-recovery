package com.sunwayworld.cloud.module.qms.filemanage.validator;

import com.sunwayworld.cloud.module.qms.filemanage.bean.CoreFileManageVersionBean;
import com.sunwayworld.cloud.module.qms.filemanage.service.CoreFileManageVersionService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreFileManageVersionStartValidator implements DataValidator {
   @Autowired
   private CoreFileManageVersionService coreFileManageVersionService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> idList = wrapper.parseId(Long.class);

      for (CoreFileManageVersionBean e : this.coreFileManageVersionService.selectListByIds(idList)) {
         if (StringUtils.isEmpty(e.getRefFileName())) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILEMANAGES.ALERT.FILENAME_IS_EMPTY_NOT_SUBMIT"));
            return false;
         }

         if (e.getVersionNo() == null) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILEMANAGES.ALERT.VERSIONNO_IS_EMPTY_NOT_SUBMIT"));
            return false;
         }

         if (e.getAuditState() == null) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILEMANAGES.ALERT.AUDITSTATE_IS_EMPTY_NOT_SUBMIT"));
            return false;
         }

         if (e.getDraftDate() == null) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILEMANAGES.ALERT.DRAFTDATE_IS_EMPTY_NOT_SUBMIT"));
            return false;
         }

         if (StringUtils.isEmpty(e.getEditUserName())) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.FILEMANAGES.ALERT.EDITUSERNAME_IS_EMPTY_NOT_SUBMIT"));
            return false;
         }
      }

      return true;
   }
}
