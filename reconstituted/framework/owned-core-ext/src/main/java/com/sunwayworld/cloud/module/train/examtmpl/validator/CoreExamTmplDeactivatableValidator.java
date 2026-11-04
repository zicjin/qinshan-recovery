package com.sunwayworld.cloud.module.train.examtmpl.validator;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreExamTmplDeactivatableValidator implements DataValidator {
   @Autowired
   private CoreExamService coreExamService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean warpper = (RestJsonWrapperBean)args[0];
      List<Long> examTmplIdList = warpper.parseId(Long.class);
      List<CoreExamBean> examList = this.coreExamService
         .selectListByFilter(SearchFilter.instance().match("EXAMTMPLID", examTmplIdList).filter(MatchPattern.OR), new Order[0]);
      boolean flag = true;
      if (!examList.isEmpty()) {
         List<String> tmplNameList = examList.stream().map(e -> e.getExt$Item("tmplname")).distinct().collect(Collectors.toList());
         tmplNameList.forEach(e -> this.addConstraintViolation(e + I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_TMPL.TIP.CANNOT_DEACTIVATE")));
         flag = false;
      }

      return flag;
   }
}
