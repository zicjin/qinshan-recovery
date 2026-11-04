package com.sunwayworld.module.sys.code.validator;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreCodeInsertableValidator implements DataValidator {
   @Autowired
   private CoreCodeService codeService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      CoreCodeBean code = wrapper.parseUnique(CoreCodeBean.class);

      for (CoreCodeBean c : this.codeService
         .selectListByFilter(SearchFilter.instance().match("CODECATEGORYID", code.getCodeCategoryId()).filter(MatchPattern.EQ), new Order[0])) {
         if (code.getCode().equals(c.getCode())) {
            this.addConstraintViolation("CORE.MODULE.SYS.CODES.TIP.CODEDUPLICATED");
            return false;
         }

         if (code.getCodeName().equals(c.getCodeName())) {
            this.addConstraintViolation("CORE.MODULE.SYS.CODES.TIP.CODENAMEDUPLICATED");
            return false;
         }
      }

      return true;
   }
}
