package com.sunwayworld.cloud.module.train.exampaper.service.impl;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerBean;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerCardDTO;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperBean;
import com.sunwayworld.cloud.module.train.exampaper.persistent.dao.CoreExamPaperAnswerDao;
import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperAnswerService;
import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperService;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionOptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreExamPaperAnswerServiceImpl implements CoreExamPaperAnswerService {
   @Autowired
   private CoreExamPaperAnswerDao coreExamPaperAnswerDao;
   @Autowired
   @Lazy
   private CoreQuestionOptService coreQuestionOptService;
   @Autowired
   @Lazy
   private CoreExamPaperService coreExamPaperService;

   public CoreExamPaperAnswerDao getDao() {
      return this.coreExamPaperAnswerDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreExamPaperAnswerBean coreExamPaperAnswer = jsonWrapper.parseUnique(CoreExamPaperAnswerBean.class);
      coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreExamPaperAnswer);
      return coreExamPaperAnswer.getId();
   }

   @Override
   public Pagination<CoreExamPaperAnswerCardDTO> selectExamPaperAnswerCardByExamPaperId(Long examPaperId) {
      List<CoreExamPaperAnswerBean> examPaperAnswerList = this.coreExamPaperAnswerDao
         .selectListByOneColumnValue(examPaperId, "EXAMPAPERID", new Order[]{Order.asc("ORDERNO")});
      int totalQty = examPaperAnswerList.size();
      int lineNum = totalQty % 4 == 0 ? totalQty / 4 : totalQty / 4 + 1;
      List<CoreExamPaperAnswerCardDTO> answerCardList = new ArrayList<>();

      for (int i = 0; i < lineNum; i++) {
         List<CoreExamPaperAnswerBean> subExamPaperAnswerList = i * 4 + 4 > totalQty
            ? examPaperAnswerList.subList(i * 4, totalQty)
            : examPaperAnswerList.subList(i * 4, i * 4 + 4);
         CoreExamPaperAnswerCardDTO answerCard = new CoreExamPaperAnswerCardDTO();
         subExamPaperAnswerList.get(0).getExamQuestionId();

         for (int j = 0; j < subExamPaperAnswerList.size(); j++) {
            String columnValueProperty = "columnValue" + (j + 1);
            String answeredProperty = "answered" + (j + 1);
            CoreExamPaperAnswerBean examPaperAnswer = subExamPaperAnswerList.get(j);
            ReflectionUtils.invokeWriteMethod(answerCard, columnValueProperty, examPaperAnswer.getOrderNo());
            if (StringUtils.isEmpty(examPaperAnswer.getAnswer())) {
               ReflectionUtils.invokeWriteMethod(answerCard, answeredProperty, "0");
            } else {
               ReflectionUtils.invokeWriteMethod(answerCard, answeredProperty, "1");
            }
         }

         answerCardList.add(answerCard);
      }

      return new Pagination<>(answerCardList);
   }

   @Override
   public CoreExamPaperAnswerBean selectExamPaperAnswerCardByExamPaperId(Long examPaperId, Integer orderNo) {
      return this.selectFirstByFilter(
         SearchFilter.instance().match("EXAMPAPERID", examPaperId).filter(MatchPattern.EQ).match("ORDERNO", orderNo).filter(MatchPattern.EQ), new Order[0]
      );
   }

   @Override
   public CoreExamPaperAnswerBean selectExamPaperAnswerById(Long id) {
      return this.selectDetail(id);
   }

   @Override
   public Pagination<CoreQuestionOptBean> selectQuestionOptPaginationByAnswerId(Long answerId, RestJsonWrapperBean wrapper) {
      CoreExamPaperAnswerBean examPaperAnswer = this.selectDetail(answerId);
      List<CoreQuestionOptBean> questionOptList = this.coreQuestionOptService
         .selectListByFilter(SearchFilter.instance().match("QUESTIONID", examPaperAnswer.getQuestionId()).filter(MatchPattern.EQ), wrapper);
      return new Pagination<>(questionOptList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void updateIfChanged(List<CoreExamPaperAnswerBean> itemList) {
      CoreExamPaperAnswerService.super.updateIfChanged(itemList);
      List<Long> scoreChangedIdList = itemList.stream()
         .filter(f -> !ObjectUtils.isEmpty(f.getScore()))
         .map(CoreExamPaperAnswerBean::getId)
         .collect(Collectors.toList());
      if (!scoreChangedIdList.isEmpty()) {
         List<CoreExamPaperAnswerBean> scoreChangedList = this.getDao().selectListByIds(scoreChangedIdList, new Order[0]);
         List<Long> scoreChangedExamPaperIdList = scoreChangedList.stream().map(e -> e.getExamPaperId()).distinct().collect(Collectors.toList());
         List<CoreExamPaperAnswerBean> examPaperAnswerList = this.getDao()
            .selectListByOneColumnValues(scoreChangedExamPaperIdList, "EXAMPAPERID", new Order[0]);
         List<CoreExamPaperBean> examPaperList4Update = new ArrayList<>();
         scoreChangedExamPaperIdList.forEach(e -> {
            long totalScore = examPaperAnswerList.stream().filter(f -> f.getExamPaperId().equals(e)).mapToLong(CoreExamPaperAnswerBean::getScore).sum();
            CoreExamPaperBean examPaper4Update = new CoreExamPaperBean();
            examPaper4Update.setId(e);
            examPaper4Update.setScore(totalScore);
            examPaperList4Update.add(examPaper4Update);
         });
         this.coreExamPaperService.getDao().update(examPaperList4Update, "SCORE");
      }
   }
}
