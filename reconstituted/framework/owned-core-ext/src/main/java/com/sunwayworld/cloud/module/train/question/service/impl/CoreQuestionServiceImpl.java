package com.sunwayworld.cloud.module.train.question.service.impl;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.cloud.module.train.question.persistent.dao.CoreQuestionDao;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionOptService;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
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
public class CoreQuestionServiceImpl implements CoreQuestionService {
   @Autowired
   private CoreQuestionDao coreQuestionDao;
   @Autowired
   @Lazy
   private CoreQuestionOptService coreQuestionOptService;

   public CoreQuestionDao getDao() {
      return this.coreQuestionDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreQuestionBean question = wrapper.parseUnique(CoreQuestionBean.class);
      CoreUserBean currentUser = LocalContextHelper.getLoginUser();
      question.setId(ApplicationContextHelper.getNextIdentity());
      question.setLastUpdatedById(currentUser.getId());
      question.setLastUpdatedByName(currentUser.getUserName());
      question.setLastUpdatedTime(LocalDateTime.now());
      this.getDao().insert(question);
      return question.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void updateIfChanged(List<CoreQuestionBean> itemList) {
      CoreUserBean currentUser = LocalContextHelper.getLoginUser();
      LocalDateTime currentTime = LocalDateTime.now();
      itemList.forEach(e -> {
         e.setLastUpdatedById(currentUser.getId());
         e.setLastUpdatedByName(currentUser.getUserName());
         e.setLastUpdatedTime(currentTime);
      });
      CoreQuestionService.super.updateIfChanged(itemList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public List<Long> clone(RestJsonWrapperBean wrapper) {
      List<CoreQuestionBean> questionList = wrapper.parse(CoreQuestionBean.class);
      if (questionList.isEmpty()) {
         return null;
      } else {
         List<CoreQuestionBean> otherList = questionList.stream().filter(f -> !"shortAnswer".equals(f.getQuestionType())).collect(Collectors.toList());
         List<CoreQuestionBean> shortAnswerList = questionList.stream().filter(f -> "shortAnswer".equals(f.getQuestionType())).collect(Collectors.toList());
         CoreUserBean currentUser = LocalContextHelper.getLoginUser();
         LocalDateTime currentTime = LocalDateTime.now();
         List<CoreQuestionBean> questionList4Insert = new ArrayList<>();
         List<Long> idList = new ArrayList<>();
         if (!shortAnswerList.isEmpty()) {
            shortAnswerList.forEach(e -> {
               CoreQuestionBean question4Insert = new CoreQuestionBean();
               Long id = ApplicationContextHelper.getNextIdentity();
               question4Insert.setId(id);
               question4Insert.setQuestionCategory(e.getQuestionCategory());
               question4Insert.setQuestionType(e.getQuestionType());
               question4Insert.setQuestionContent(e.getQuestionContent());
               question4Insert.setAnswer(e.getAnswer());
               question4Insert.setJudgementAnswer(e.getJudgementAnswer());
               question4Insert.setLastUpdatedById(currentUser.getId());
               question4Insert.setLastUpdatedByName(currentUser.getUserName());
               question4Insert.setLastUpdatedTime(currentTime);
               idList.add(id);
               questionList4Insert.add(question4Insert);
            });
         }

         if (!otherList.isEmpty()) {
            List<Long> otherIdList = otherList.stream().map(CoreQuestionBean::getId).collect(Collectors.toList());
            List<CoreQuestionOptBean> otherOptList = this.coreQuestionOptService.getDao().selectListByOneColumnValues(otherIdList, "QUESTIONID");
            List<CoreQuestionOptBean> questionOptList4Insert = new ArrayList<>();
            otherList.forEach(e -> {
               CoreQuestionBean question4Insert = new CoreQuestionBean();
               Long id = ApplicationContextHelper.getNextIdentity();
               question4Insert.setId(id);
               question4Insert.setQuestionCategory(e.getQuestionCategory());
               question4Insert.setAnswer(e.getAnswer());
               question4Insert.setJudgementAnswer(e.getJudgementAnswer());
               question4Insert.setQuestionType(e.getQuestionType());
               question4Insert.setQuestionContent(e.getQuestionContent());
               question4Insert.setLastUpdatedById(currentUser.getId());
               question4Insert.setLastUpdatedByName(currentUser.getUserName());
               question4Insert.setLastUpdatedTime(currentTime);
               List<CoreQuestionOptBean> subOtherOptList = otherOptList.stream().filter(f -> f.getQuestionId().equals(e.getId())).collect(Collectors.toList());
               if (!subOtherOptList.isEmpty()) {
                  subOtherOptList.forEach(item -> {
                     CoreQuestionOptBean questionOpt4Insert = new CoreQuestionOptBean();
                     Long optId = ApplicationContextHelper.getNextIdentity();
                     questionOpt4Insert.setId(optId);
                     questionOpt4Insert.setQuestionId(id);
                     questionOpt4Insert.setOptCode(item.getOptCode());
                     questionOpt4Insert.setOptContent(item.getOptContent());
                     questionOptList4Insert.add(questionOpt4Insert);
                  });
               }

               idList.add(id);
               questionList4Insert.add(question4Insert);
            });
            if (!questionOptList4Insert.isEmpty()) {
               this.coreQuestionOptService.getDao().insert(questionOptList4Insert);
            }
         }

         this.coreQuestionDao.insert(questionList4Insert);
         return idList;
      }
   }
}
