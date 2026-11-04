package com.sunwayworld.module.sys.checkcode.validator;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import com.sunwayworld.module.sys.checkcode.service.CoreCheckCodeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreCheckCodeInsertableValidator implements DataValidator {
   @Autowired
   private CoreCheckCodeService checkCodeService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      CoreCheckCodeBean checkCode = wrapper.parseUnique(CoreCheckCodeBean.class);
      List<CoreCheckCodeBean> sameCodeCategoryList = this.checkCodeService
         .selectListByFilter(SearchFilter.instance().match("CODECATEGORY", checkCode.getCodeCategory()).filter(MatchPattern.EQ), new Order[0]);
      if (sameCodeCategoryList.stream().anyMatch(e -> e.getCode().equals(checkCode.getCode()))) {
         this.addConstraintViolation("CORE.MODULE.SYS.CHECKCODES.TIP.CODEDUPLICATED");
         return false;
      } else if (sameCodeCategoryList.stream().anyMatch(e -> e.getCodeName().equals(checkCode.getCodeName()))) {
         this.addConstraintViolation("CORE.MODULE.SYS.CHECKCODES.TIP.CODENAMEDUPLICATED");
         return false;
      } else {
         return true;
      }
   }
}
