package com.sunwayworld.cloud.module.qms.customsurvey.validator;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveyItemService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreCustomSurveyCompleteValidator implements DataValidator {
   @Autowired
   private CoreCustomSurveyItemService coreCustomSurveyItemService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> customSurveyIdList = wrapper.parseId(Long.class);
      List<CoreCustomSurveyItemBean> coreCustomSurveyItemList = this.coreCustomSurveyItemService
         .selectListByFilter(SearchFilter.instance().match("SURVEYID", customSurveyIdList).filter(MatchPattern.OR), new Order[0]);
      if (coreCustomSurveyItemList.stream().anyMatch(e -> e.getEvaluate() == null)) {
         this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.CUSTOM_SURVEY.TIP.CUSTOM_SURVEY_ITEM_EVALUATE_NOT_EMPTY"));
         return false;
      } else {
         return true;
      }
   }
}
