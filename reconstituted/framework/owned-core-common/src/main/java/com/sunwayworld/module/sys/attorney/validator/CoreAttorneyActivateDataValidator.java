package com.sunwayworld.module.sys.attorney.validator;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyBean;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyMenuService;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyService;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreAttorneyActivateDataValidator implements DataValidator {
   @Autowired
   private CoreAttorneyService attorneyService;
   @Autowired
   private CoreAttorneyMenuService attorneyMenuService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> idList = wrapper.parseId(Long.class);

      for (CoreAttorneyBean attorney : this.attorneyService.selectListByIds(idList)) {
         if (attorney.getEndDate() != null) {
            boolean valid = true;
            if (LocalDate.now().isAfter(attorney.getEndDate())) {
               this.addConstraintViolation("CORE.MODULE.SYS.ATTORNEYS.TIP.ENDDATE_BEFORE_TODAY");
               valid = false;
            }

            if (attorney.getStartDate() != null && attorney.getStartDate().isAfter(attorney.getEndDate())) {
               this.addConstraintViolation("CORE.MODULE.SYS.ATTORNEYS.TIP.ENDDATE_BEFORE_STARTDATE");
               valid = false;
            }

            if (!valid) {
               return false;
            }
         }

         idList.add(attorney.getId());
      }

      Map<Long, Long> map = this.attorneyMenuService.getDao().groupCountByValueList(idList, "ATTORNEYID");
      if (map.values().contains(0L)) {
         this.addConstraintViolation("CORE.MODULE.SYS.ATTORNEYS.TIP.MENU_REQUIRED");
         return false;
      } else {
         return true;
      }
   }
}
