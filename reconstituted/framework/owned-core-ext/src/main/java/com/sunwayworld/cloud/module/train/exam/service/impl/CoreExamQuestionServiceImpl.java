package com.sunwayworld.cloud.module.train.exam.service.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.bean.CoreExamQuestionBean;
import com.sunwayworld.cloud.module.train.exam.persistent.dao.CoreExamQuestionDao;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamQuestionService;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableMetadataHelper;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.metadata.service.CoreTableService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreExamQuestionServiceImpl implements CoreExamQuestionService {
   @Autowired
   private CoreExamQuestionDao coreExamQuestionDao;
   @Autowired
   @Lazy
   private CoreExamService coreExamService;

   public CoreExamQuestionDao getDao() {
      return this.coreExamQuestionDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      List<CoreExamQuestionBean> examQuestionList = wrapper.parse(CoreExamQuestionBean.class);
      if (examQuestionList.isEmpty()) {
         return null;
      } else {
         GikamConcurrentLocker.block(this.getDao().getTable());
         List<Long> examIdList = examQuestionList.stream().map(CoreExamQuestionBean::getExamId).distinct().collect(Collectors.toList());
         List<CoreExamQuestionBean> oriExamQuestionList = this.selectListByFilter(
            SearchFilter.instance().match("EXAMID", examIdList).filter(MatchPattern.OR), new Order[0]
         );
         CoreUserBean currentUser = LocalContextHelper.getLoginUser();
         LocalDateTime currentTime = LocalDateTime.now();
         List<CoreExamBean> examList4Update = new ArrayList<>();
         examIdList.forEach(e -> {
            CoreExamBean exam = new CoreExamBean();
            List<CoreExamQuestionBean> subExamQuestionList = examQuestionList.stream().filter(f -> f.getExamId().equals(e)).collect(Collectors.toList());
            List<CoreExamQuestionBean> subOriExamQuestionList = oriExamQuestionList.stream().filter(f -> f.getExamId().equals(e)).collect(Collectors.toList());
            long singleChoiceQty = subExamQuestionList.stream().filter(f -> "singleChoice".equals(f.getExt$().get("questiontype"))).count();
            long oriSingleChoiceQty = subOriExamQuestionList.stream().filter(f -> "singleChoice".equals(f.getExt$().get("questiontype"))).count();
            long totalSingleChoiceQty = singleChoiceQty + oriSingleChoiceQty;
            long multipleChoiceQty = subExamQuestionList.stream().filter(f -> "multipleChoice".equals(f.getExt$().get("questiontype"))).count();
            long oriMultipleChoiceQty = subOriExamQuestionList.stream().filter(f -> "multipleChoice".equals(f.getExt$().get("questiontype"))).count();
            long totalMultipleChoiceQty = multipleChoiceQty + oriMultipleChoiceQty;
            long completionQty = subExamQuestionList.stream().filter(f -> "completion".equals(f.getExt$().get("questiontype"))).count();
            long oriCompletionQty = subOriExamQuestionList.stream().filter(f -> "completion".equals(f.getExt$().get("questiontype"))).count();
            long totalCompletionQty = completionQty + oriCompletionQty;
            long judgementQty = subExamQuestionList.stream().filter(f -> "judgement".equals(f.getExt$().get("questiontype"))).count();
            long oriJudgementQty = subOriExamQuestionList.stream().filter(f -> "judgement".equals(f.getExt$().get("questiontype"))).count();
            long totalJudgementQty = judgementQty + oriJudgementQty;
            long shortAnswerQty = subExamQuestionList.stream().filter(f -> "shortAnswer".equals(f.getExt$().get("questiontype"))).count();
            long oriShortAnswerQty = subOriExamQuestionList.stream().filter(f -> "shortAnswer".equals(f.getExt$().get("questiontype"))).count();
            long totalShortAnswerQty = shortAnswerQty + oriShortAnswerQty;
            exam.setId(e);
            exam.setLastUpdatedById(currentUser.getId());
            exam.setLastUpdatedByName(currentUser.getUserName());
            exam.setLastUpdatedTime(currentTime);
            exam.setSingleChoiceQty(totalSingleChoiceQty);
            exam.setMultipleChoiceQty(totalMultipleChoiceQty);
            exam.setJudgementQty(totalJudgementQty);
            exam.setShortAnswerQty(totalShortAnswerQty);
            exam.setCompletionQty(totalCompletionQty);
            examList4Update.add(exam);
         });
         List<Long> idList = ApplicationContextHelper.getNextIdentityList(examQuestionList.size());

         for (int i = 0; i < examQuestionList.size(); i++) {
            Long id = idList.get(i);
            examQuestionList.get(i).setId(id);
         }

         this.getDao().insert(examQuestionList);
         this.coreExamService
            .getDao()
            .update(
               examList4Update,
               "LASTUPDATEDBYID",
               "LASTUPDATEDBYNAME",
               "LASTUPDATEDTIME",
               "SINGLECHOICEQTY",
               "MULTIPLECHOICEQTY",
               "JUDGEMENTQTY",
               "SHORTANSWERQTY",
               "COMPLETIONQTY"
            );
         return idList.get(0);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<CoreExamQuestionBean> examQuestionList = wrapper.parse(this.getDao().getType());
      if (!examQuestionList.isEmpty()) {
         GikamConcurrentLocker.block(this.getDao().getTable());
         List<Long> examIdList = examQuestionList.stream().map(CoreExamQuestionBean::getExamId).distinct().collect(Collectors.toList());
         List<CoreExamQuestionBean> oriExamQuestionList = this.selectListByFilter(
            SearchFilter.instance().match("EXAMID", examIdList).filter(MatchPattern.OR), new Order[0]
         );
         CoreUserBean currentUser = LocalContextHelper.getLoginUser();
         LocalDateTime currentTime = LocalDateTime.now();
         List<CoreExamBean> examList4Update = new ArrayList<>();
         examIdList.forEach(e -> {
            CoreExamBean exam = new CoreExamBean();
            List<CoreExamQuestionBean> subExamQuestionList = examQuestionList.stream().filter(f -> f.getExamId().equals(e)).collect(Collectors.toList());
            List<CoreExamQuestionBean> subOriExamQuestionList = oriExamQuestionList.stream().filter(f -> f.getExamId().equals(e)).collect(Collectors.toList());
            long singleChoiceQty = subExamQuestionList.stream().filter(f -> "singleChoice".equals(f.getExt$().get("questiontype"))).count();
            long oriSingleChoiceQty = subOriExamQuestionList.stream().filter(f -> "singleChoice".equals(f.getExt$().get("questiontype"))).count();
            long totalSingleChoiceQty = oriSingleChoiceQty - singleChoiceQty;
            long multipleChoiceQty = subExamQuestionList.stream().filter(f -> "multipleChoice".equals(f.getExt$().get("questiontype"))).count();
            long oriMultipleChoiceQty = subOriExamQuestionList.stream().filter(f -> "multipleChoice".equals(f.getExt$().get("questiontype"))).count();
            long totalMultipleChoiceQty = oriMultipleChoiceQty - multipleChoiceQty;
            long completionQty = subExamQuestionList.stream().filter(f -> "completion".equals(f.getExt$().get("questiontype"))).count();
            long oriCompletionQty = subOriExamQuestionList.stream().filter(f -> "completion".equals(f.getExt$().get("questiontype"))).count();
            long totalCompletionQty = completionQty + oriCompletionQty;
            long judgementQty = subExamQuestionList.stream().filter(f -> "judgement".equals(f.getExt$().get("questiontype"))).count();
            long oriJudgementQty = subOriExamQuestionList.stream().filter(f -> "judgement".equals(f.getExt$().get("questiontype"))).count();
            long totalJudgementQty = oriJudgementQty - judgementQty;
            long shortAnswerQty = subExamQuestionList.stream().filter(f -> "shortAnswer".equals(f.getExt$().get("questiontype"))).count();
            long oriShortAnswerQty = subOriExamQuestionList.stream().filter(f -> "shortAnswer".equals(f.getExt$().get("questiontype"))).count();
            long totalShortAnswerQty = oriShortAnswerQty - shortAnswerQty;
            exam.setId(e);
            exam.setLastUpdatedById(currentUser.getId());
            exam.setLastUpdatedByName(currentUser.getUserName());
            exam.setLastUpdatedTime(currentTime);
            exam.setSingleChoiceQty(totalSingleChoiceQty);
            exam.setMultipleChoiceQty(totalMultipleChoiceQty);
            exam.setJudgementQty(totalJudgementQty);
            exam.setShortAnswerQty(totalShortAnswerQty);
            exam.setCompletionQty(totalCompletionQty);
            examList4Update.add(exam);
         });
         this.coreExamService
            .getDao()
            .update(
               examList4Update,
               "LASTUPDATEDBYID",
               "LASTUPDATEDBYNAME",
               "LASTUPDATEDTIME",
               "SINGLECHOICEQTY",
               "MULTIPLECHOICEQTY",
               "JUDGEMENTQTY",
               "SHORTANSWERQTY",
               "COMPLETIONQTY"
            );
         CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);
         examQuestionList.forEach(t -> tableService.deleteCascade(PersistableMetadataHelper.getTableName(this.getDao().getType()), t.getId()));
      }
   }
}
