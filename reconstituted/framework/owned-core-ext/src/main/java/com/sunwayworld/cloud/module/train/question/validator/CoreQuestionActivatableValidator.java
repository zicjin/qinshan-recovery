package com.sunwayworld.cloud.module.train.question.validator;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionOptService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreQuestionActivatableValidator implements DataValidator {
   @Autowired
   private CoreQuestionOptService coreQuestionOptService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean warpper = (RestJsonWrapperBean)args[0];
      List<CoreQuestionBean> questionList = warpper.parse(CoreQuestionBean.class);
      List<CoreQuestionBean> questionAnswerEmptyList = questionList.stream().filter(f -> StringUtils.isBlank(f.getAnswer())).collect(Collectors.toList());
      if (!questionAnswerEmptyList.isEmpty()) {
         questionAnswerEmptyList.forEach(
            e -> this.addConstraintViolation(e.getQuestionContent() + I18nHelper.getMessage("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_ANSWER_CANNOT_EMPTY"))
         );
         return false;
      } else {
         boolean flag = true;
         List<CoreQuestionBean> choiceQustionList = questionList.stream()
            .filter(f -> "singleChoice".equals(f.getQuestionType()) || "multipleChoice".equals(f.getQuestionType()))
            .collect(Collectors.toList());
         if (!choiceQustionList.isEmpty()) {
            List<Long> choiceQustionIdList = choiceQustionList.stream().map(CoreQuestionBean::getId).collect(Collectors.toList());
            List<CoreQuestionOptBean> questionOptList = this.coreQuestionOptService
               .selectListByFilter(SearchFilter.instance().match("QUESTIONID", choiceQustionIdList).filter(MatchPattern.OR), new Order[0]);

            for (CoreQuestionBean question : choiceQustionList) {
               if (!questionOptList.stream().anyMatch(f -> f.getQuestionId().equals(question.getId()))) {
                  this.addConstraintViolation(question.getQuestionContent() + I18nHelper.getMessage("CORE.MODULE.TRAIN.QUESTION.TIP.QUESTION_OPT_CANNOT_EMPTY"));
                  flag = false;
               }
            }
         }

         return flag;
      }
   }
}
