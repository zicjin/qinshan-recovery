package com.sunwayworld.cloud.module.train.examtmpl.service.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamService;
import com.sunwayworld.cloud.module.train.examtmpl.bean.CoreExamTmplBean;
import com.sunwayworld.cloud.module.train.examtmpl.persistent.dao.CoreExamTmplDao;
import com.sunwayworld.cloud.module.train.examtmpl.service.CoreExamTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreExamTmplServiceImpl implements CoreExamTmplService {
   @Autowired
   private CoreExamTmplDao coreExamTmplDao;
   @Autowired
   @Lazy
   private CoreExamService coreExamService;

   public CoreExamTmplDao getDao() {
      return this.coreExamTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreExamTmplBean examTmpl = wrapper.parseUnique(CoreExamTmplBean.class);
      examTmpl.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(examTmpl);
      return examTmpl.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public List<Long> clone(RestJsonWrapperBean wrapper) {
      List<CoreExamTmplBean> examTmplList = wrapper.parse(CoreExamTmplBean.class);
      if (examTmplList.isEmpty()) {
         return null;
      } else {
         List<CoreExamTmplBean> examTmplList4Insert = new ArrayList<>();
         List<Long> idList = new ArrayList<>();
         examTmplList.forEach(e -> {
            CoreExamTmplBean examTmpl4Insert = new CoreExamTmplBean();
            Long id = ApplicationContextHelper.getNextIdentity();
            examTmpl4Insert.setId(id);
            examTmpl4Insert.setTmplName(e.getTmplName());
            examTmpl4Insert.setSingleChoiceQty(e.getSingleChoiceQty());
            examTmpl4Insert.setSingleChoiceScore(e.getSingleChoiceScore());
            examTmpl4Insert.setMultipleChoiceQty(e.getMultipleChoiceQty());
            examTmpl4Insert.setMultipleChoiceScore(e.getMultipleChoiceScore());
            examTmpl4Insert.setCompletionQty(e.getCompletionQty());
            examTmpl4Insert.setCompletionScore(e.getCompletionScore());
            examTmpl4Insert.setJudgementQty(e.getJudgementQty());
            examTmpl4Insert.setJudgementScore(e.getJudgementScore());
            examTmpl4Insert.setShortAnswerQty(e.getShortAnswerQty());
            examTmpl4Insert.setShortAnswerScore(e.getShortAnswerScore());
            examTmpl4Insert.setTotalScore(e.getTotalScore());
            idList.add(id);
            examTmplList4Insert.add(examTmpl4Insert);
         });
         this.getDao().insert(examTmplList4Insert);
         return idList;
      }
   }

   @Override
   public RestValidationResultBean deleteValidate(RestJsonWrapperBean wrapper) {
      List<CoreExamTmplBean> examTmplList = wrapper.parse(CoreExamTmplBean.class);
      boolean validateFlag = true;
      StringBuilder messageBuilder = new StringBuilder();
      List<Long> examTmplIdList = examTmplList.stream().map(f -> f.getId()).collect(Collectors.toList());
      List<CoreExamBean> examList = this.coreExamService
         .selectListByFilter(SearchFilter.instance().match("EXAMTMPLID", examTmplIdList).filter(MatchPattern.OR), new Order[0]);

      for (CoreExamTmplBean examTmpl : examTmplList) {
         if (examList.stream().anyMatch(f -> f.getExamTmplId().equals(examTmpl.getId()))) {
            validateFlag = false;
            messageBuilder.append(
               examTmpl.getTmplName() + ":" + I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_TMPL.TIP.QUOTED_BY_EXAM_CANNOT_DELETE") + "<br />"
            );
         }
      }

      return new RestValidationResultBean(validateFlag, messageBuilder.toString());
   }
}
