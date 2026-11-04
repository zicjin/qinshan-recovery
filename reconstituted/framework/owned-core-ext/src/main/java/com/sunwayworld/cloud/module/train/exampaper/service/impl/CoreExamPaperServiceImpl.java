package com.sunwayworld.cloud.module.train.exampaper.service.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.bean.CoreExamQuestionBean;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamQuestionService;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamService;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerBean;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperBean;
import com.sunwayworld.cloud.module.train.exampaper.persistent.dao.CoreExamPaperDao;
import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperAnswerService;
import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperService;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionService;
import com.sunwayworld.cloud.module.train.support.enums.ExamPaperStatus;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainUserService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.RandomUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreExamPaperServiceImpl implements CoreExamPaperService {
   @Autowired
   private CoreExamPaperDao coreExamPaperDao;
   @Autowired
   @Lazy
   private CoreExamPaperAnswerService coreExamPaperAnswerService;
   @Autowired
   @Lazy
   private CoreExamService coreExamService;
   @Autowired
   @Lazy
   private CoreQuestionService coreQuestionService;
   @Autowired
   @Lazy
   private CoreExamQuestionService coreExamQuestionService;
   @Autowired
   @Lazy
   private CoreTrainUserService coreTrainUserService;

   public CoreExamPaperDao getDao() {
      return this.coreExamPaperDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreExamPaperBean coreExamPaper = wrapper.parseUnique(CoreExamPaperBean.class);
      coreExamPaper.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreExamPaper);
      return coreExamPaper.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public CoreExamPaperBean startExam(RestJsonWrapperBean wrapper) {
      CoreExamPaperBean examPaper = wrapper.parseUnique(CoreExamPaperBean.class);
      LocalDateTime examBeginTime = DateTimeUtils.parseLocalDateTime(examPaper.getExt$Item("exambegintime"));
      LocalDateTime examEndTime = DateTimeUtils.parseLocalDateTime(examPaper.getExt$Item("examendtime"));
      LocalDateTime now = LocalDateTime.now();
      if (now.isBefore(examBeginTime)) {
         throw new InvalidDataException("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.EXAM_NOT_BEGIN");
      } else if (now.isAfter(examEndTime)) {
         throw new InvalidDataException("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.EXAM_DONE");
      } else {
         examPaper.setActBeginTime(now);
         examPaper.setStatus(ExamPaperStatus.DONE.name().toLowerCase());
         this.getDao().update(examPaper, new String[]{"ACTBEGINTIME", "STATUS"});
         StringBuilder countDown = new StringBuilder();
         Integer examDuration = Integer.valueOf(examPaper.getExt$Item("examduration"));
         if (now.plusMinutes(Long.valueOf(examPaper.getExt$Item("examduration"))).isAfter(examEndTime)) {
            int minitus = (int)now.until(examEndTime, ChronoUnit.MINUTES);
            countDown.append(StringUtils.leftPad(String.valueOf(minitus / 60), 2)).append(":");
            countDown.append(StringUtils.leftPad(String.valueOf(minitus % 60), 2));
         } else {
            countDown.append(StringUtils.leftPad(String.valueOf(examDuration / 60), 2)).append(":");
            countDown.append(StringUtils.leftPad(String.valueOf(examDuration % 60), 2));
         }

         examPaper.setExt$Item("countdown", countDown.toString());
         return examPaper;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void endExam(RestJsonWrapperBean wrapper) {
      CoreExamPaperBean coreExamPaper = wrapper.parseUnique(CoreExamPaperBean.class);
      List<CoreExamPaperAnswerBean> examPaperAnswerList = this.coreExamPaperAnswerService
         .selectListByFilter(
            SearchFilter.instance()
               .match("EXAMPAPERID", coreExamPaper.getId())
               .filter(MatchPattern.EQ)
               .match("QUESTIONTYPE", "shortAnswer")
               .filter(MatchPattern.DIFFER),
            new Order[0]
         );
      examPaperAnswerList.forEach(e -> {
         String answer = StringUtils.trim(e.getAnswer());
         if (StringUtils.isEmpty(answer)) {
            e.setScore(0L);
         } else {
            String correctAnswer = StringUtils.trim(e.getExt$Item("correctanswer"));
            if ("multipleChoice".equals(e.getExt$Item("questiontype"))) {
               if (correctAnswer.length() == answer.length()) {
                  char[] correctAnswerArray = correctAnswer.toCharArray();
                  char[] answerArray = answer.toCharArray();
                  Arrays.sort(correctAnswerArray);
                  Arrays.sort(answerArray);
                  if (String.valueOf(correctAnswerArray).equals(String.valueOf(answerArray))) {
                     e.setScore(NumberUtils.parseLong(e.getExt$Item("multiplechoicescore")));
                  }
               }
            } else if (StringUtils.equals(correctAnswer, answer)) {
               if ("singleChoice".equals(e.getExt$Item("questiontype"))) {
                  e.setScore(NumberUtils.parseLong(e.getExt$Item("singlechoicescore")));
               } else if ("judgement".equals(e.getExt$Item("questiontype"))) {
                  e.setScore(NumberUtils.parseLong(e.getExt$Item("judgementscore")));
               } else if ("completion".equals(e.getExt$Item("questiontype"))) {
                  e.setScore(NumberUtils.parseLong(e.getExt$Item("completionscore")));
               }
            }
         }
      });
      long score = examPaperAnswerList.stream().mapToLong(CoreExamPaperAnswerBean::getScore).sum();
      coreExamPaper.setActEndTime(LocalDateTime.now());
      coreExamPaper.setScore(score);
      this.getDao().update(coreExamPaper, new String[]{"ACTENDTIME", "SCORE"});
      this.coreExamPaperAnswerService.getDao().update(examPaperAnswerList, "SCORE");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      CoreExamPaperService.super.activate(wrapper);
      List<CoreExamPaperBean> examPaperList = wrapper.parse(CoreExamPaperBean.class);
      List<Long> trainIdList = examPaperList.stream().map(CoreExamPaperBean::getTrainId).collect(Collectors.toList());
      List<String> userIdList = examPaperList.stream().map(CoreExamPaperBean::getUserId).collect(Collectors.toList());
      List<CoreTrainUserBean> trainUserList = this.coreTrainUserService
         .selectListByFilter(
            SearchFilter.instance().match("TRAINID", trainIdList).filter(MatchPattern.OR).match("USERID", userIdList).filter(MatchPattern.OR), new Order[0]
         );
      trainUserList.forEach(
         e -> {
            CoreExamPaperBean examPaper = examPaperList.stream()
               .filter(f -> f.getTrainId().equals(e.getTrainId()) && f.getUserId().equals(e.getUserId()))
               .findFirst()
               .get();
            e.setScore(String.valueOf(examPaper.getScore()));
         }
      );
      this.coreTrainUserService.getDao().update(trainUserList, "SCORE");
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void generateExamPaper(Long examId, Long trainId, String userId, String userName) {
      CoreExamBean exam = this.coreExamService.selectDetail(examId);
      if ("manual".equals(exam.getQuestionMethod())) {
         this.generateManualExamPaper(exam, trainId, userId, userName);
      } else {
         this.generateRandomExamPaper(exam, trainId, userId, userName);
      }
   }

   private void generateManualExamPaper(CoreExamBean coreExam, Long trainId, String userId, String userName) {
      CoreExamPaperBean coreExamPaper = new CoreExamPaperBean();
      Long id = ApplicationContextHelper.getNextIdentity();
      coreExamPaper.setId(id);
      coreExamPaper.setExamId(coreExam.getId());
      coreExamPaper.setUserId(userId);
      coreExamPaper.setUserName(userName);
      coreExamPaper.setTrainId(trainId);
      Long orderNo = 1L;
      List<CoreExamQuestionBean> examQuestionList = this.coreExamQuestionService
         .selectListByFilter(SearchFilter.instance().match("EXAMID", coreExam.getId()).filter(MatchPattern.EQ), new Order[0]);
      List<CoreExamQuestionBean> singleChoiceExamQuestionList = examQuestionList.stream()
         .filter(f -> "singleChoice".equals(f.getExt$Item("questiontype")))
         .collect(Collectors.toList());
      List<CoreExamQuestionBean> multipleChoiceExamQuestionList = examQuestionList.stream()
         .filter(f -> "multipleChoice".equals(f.getExt$Item("questiontype")))
         .collect(Collectors.toList());
      List<CoreExamQuestionBean> judgementExamQuestionList = examQuestionList.stream()
         .filter(f -> "judgement".equals(f.getExt$Item("questiontype")))
         .collect(Collectors.toList());
      List<CoreExamQuestionBean> shortAnswerExamQuestionList = examQuestionList.stream()
         .filter(f -> "shortAnswer".equals(f.getExt$Item("questiontype")))
         .collect(Collectors.toList());
      List<CoreExamQuestionBean> completionExamQuestionList = examQuestionList.stream()
         .filter(f -> "completion".equals(f.getExt$Item("questiontype")))
         .collect(Collectors.toList());
      List<CoreExamPaperAnswerBean> coreExamPaperAnswerList = new ArrayList<>();
      if (!singleChoiceExamQuestionList.isEmpty()) {
         for (int i = 0; i < singleChoiceExamQuestionList.size(); i++) {
            CoreExamQuestionBean singleChoiceExamQuestion = singleChoiceExamQuestionList.get(i);
            CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
            coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
            coreExamPaperAnswer.setExamQuestionId(singleChoiceExamQuestion.getId());
            coreExamPaperAnswer.setQuestionId(singleChoiceExamQuestion.getQuestionId());
            coreExamPaperAnswer.setExamId(coreExam.getId());
            coreExamPaperAnswer.setExamPaperId(id);
            coreExamPaperAnswer.setOrderNo(orderNo);
            coreExamPaperAnswerList.add(coreExamPaperAnswer);
            orderNo = orderNo + 1L;
         }
      }

      if (!multipleChoiceExamQuestionList.isEmpty()) {
         for (int i = 0; i < multipleChoiceExamQuestionList.size(); i++) {
            CoreExamQuestionBean multipleChoiceExamQuestion = multipleChoiceExamQuestionList.get(i);
            CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
            coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
            coreExamPaperAnswer.setExamQuestionId(multipleChoiceExamQuestion.getId());
            coreExamPaperAnswer.setQuestionId(multipleChoiceExamQuestion.getQuestionId());
            coreExamPaperAnswer.setExamId(coreExam.getId());
            coreExamPaperAnswer.setExamPaperId(id);
            coreExamPaperAnswer.setOrderNo(orderNo);
            coreExamPaperAnswerList.add(coreExamPaperAnswer);
            orderNo = orderNo + 1L;
         }
      }

      if (!completionExamQuestionList.isEmpty()) {
         for (int i = 0; i < completionExamQuestionList.size(); i++) {
            CoreExamQuestionBean completionExamQuestion = completionExamQuestionList.get(i);
            CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
            coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
            coreExamPaperAnswer.setExamQuestionId(completionExamQuestion.getId());
            coreExamPaperAnswer.setQuestionId(completionExamQuestion.getQuestionId());
            coreExamPaperAnswer.setExamId(coreExam.getId());
            coreExamPaperAnswer.setExamPaperId(id);
            coreExamPaperAnswer.setOrderNo(orderNo);
            coreExamPaperAnswerList.add(coreExamPaperAnswer);
            orderNo = orderNo + 1L;
         }
      }

      if (!judgementExamQuestionList.isEmpty()) {
         for (int i = 0; i < judgementExamQuestionList.size(); i++) {
            CoreExamQuestionBean judgementExamQuestion = judgementExamQuestionList.get(i);
            CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
            coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
            coreExamPaperAnswer.setExamQuestionId(judgementExamQuestion.getId());
            coreExamPaperAnswer.setQuestionId(judgementExamQuestion.getQuestionId());
            coreExamPaperAnswer.setExamId(coreExam.getId());
            coreExamPaperAnswer.setExamPaperId(id);
            coreExamPaperAnswer.setOrderNo(orderNo);
            coreExamPaperAnswerList.add(coreExamPaperAnswer);
            orderNo = orderNo + 1L;
         }
      }

      if (!shortAnswerExamQuestionList.isEmpty()) {
         for (int i = 0; i < shortAnswerExamQuestionList.size(); i++) {
            CoreExamQuestionBean shortAnswerExamQuestion = shortAnswerExamQuestionList.get(i);
            CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
            coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
            coreExamPaperAnswer.setExamQuestionId(shortAnswerExamQuestion.getId());
            coreExamPaperAnswer.setQuestionId(shortAnswerExamQuestion.getQuestionId());
            coreExamPaperAnswer.setExamId(coreExam.getId());
            coreExamPaperAnswer.setExamPaperId(id);
            coreExamPaperAnswer.setOrderNo(orderNo);
            coreExamPaperAnswerList.add(coreExamPaperAnswer);
            orderNo = orderNo + 1L;
         }
      }

      this.getDao().insert(coreExamPaper);
      this.coreExamPaperAnswerService.getDao().insert(coreExamPaperAnswerList);
   }

   private void generateRandomExamPaper(CoreExamBean coreExam, Long trainId, String userId, String userName) {
      List<CoreQuestionBean> questionList = this.coreQuestionService
         .selectListByFilter(
            SearchFilter.instance()
               .match("QUESTIONCATEGORY", coreExam.getExamCategory())
               .filter(MatchPattern.EQ)
               .match("ACTIVATEDFLAG", "1")
               .filter(MatchPattern.SEQ)
               .match("LASTSUSPENDEDFLAG", "0")
               .filter(MatchPattern.SEQ),
            new Order[0]
         );
      List<CoreQuestionBean> singleChoiceQuestionList = questionList.stream()
         .filter(f -> "singleChoice".equals(f.getQuestionType()))
         .collect(Collectors.toList());
      List<CoreQuestionBean> multipleChoiceQuestionList = questionList.stream()
         .filter(f -> "multipleChoice".equals(f.getQuestionType()))
         .collect(Collectors.toList());
      List<CoreQuestionBean> completionQuestionList = questionList.stream().filter(f -> "completion".equals(f.getQuestionType())).collect(Collectors.toList());
      List<CoreQuestionBean> judgementQuestionList = questionList.stream().filter(f -> "judgement".equals(f.getQuestionType())).collect(Collectors.toList());
      List<CoreQuestionBean> shortAnswerQuestionList = questionList.stream()
         .filter(f -> "shortAnswer".equals(f.getQuestionType()))
         .collect(Collectors.toList());
      boolean validFlag = true;
      StringBuilder validMessage = new StringBuilder();
      if (coreExam.getTmplSingleChoiceQty() > 0L && coreExam.getTmplSingleChoiceQty() > singleChoiceQuestionList.size()) {
         validMessage.append(I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.SINGLECHOICE_QUESTION_QTY_LESS_THAN_EXAM_SINGLECHOICE_QUESTION_QTY"))
            .append("\n");
         validFlag = false;
      }

      if (coreExam.getTmplMultipleChoiceQty() > 0L && coreExam.getTmplMultipleChoiceQty() > multipleChoiceQuestionList.size()) {
         validMessage.append(I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.MULTIPLECHOICE_QUESTION_QTY_LESS_THAN_EXAM_MULTIPLECHOICE_QUESTION_QTY"))
            .append("\n");
         validFlag = false;
      }

      if (coreExam.getCompletionQty() > 0L && coreExam.getCompletionQty() > completionQuestionList.size()) {
         validMessage.append(I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.COMPLETION_QUESTION_QTY_LESS_THAN_EXAM_COMPLETION_QUESTION_QTY"))
            .append("\n");
         validFlag = false;
      }

      if (coreExam.getTmplJudgementQty() > 0L && coreExam.getTmplJudgementQty() > judgementQuestionList.size()) {
         validMessage.append(I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.JUDGEMENT_QUESTION_QTY_LESS_THAN_EXAM_JUDGEMENT_QUESTION_QTY"))
            .append("\n");
         validFlag = false;
      }

      if (coreExam.getTmplShortAnswerQty() > 0L && coreExam.getTmplShortAnswerQty() > shortAnswerQuestionList.size()) {
         validMessage.append(I18nHelper.getMessage("CORE.MODULE.TRAIN.EXAM_PAPER.TIP.SHORTANSWER_QUESTION_QTY_LESS_THAN_EXAM_SHORTANSWER_QUESTION_QTY"))
            .append("\n");
         validFlag = false;
      }

      if (!validFlag) {
         throw new InvalidDataException(validMessage.toString());
      } else {
         CoreExamPaperBean coreExamPaper = new CoreExamPaperBean();
         Long id = ApplicationContextHelper.getNextIdentity();
         coreExamPaper.setId(id);
         coreExamPaper.setExamId(coreExam.getId());
         coreExamPaper.setUserId(userId);
         coreExamPaper.setUserName(userName);
         coreExamPaper.setTrainId(trainId);
         Long orderNo = 1L;
         List<CoreExamPaperAnswerBean> coreExamPaperAnswerList = new ArrayList<>();
         if (coreExam.getTmplSingleChoiceQty() > 0L) {
            List<CoreQuestionBean> singleChoiceQuestionChoosenList = this.getRandomQuestion(singleChoiceQuestionList, coreExam.getTmplSingleChoiceQty());

            for (int i = 0; i < singleChoiceQuestionChoosenList.size(); i++) {
               CoreQuestionBean singleChoiceQuestion = singleChoiceQuestionChoosenList.get(i);
               CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
               coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
               coreExamPaperAnswer.setQuestionId(singleChoiceQuestion.getId());
               coreExamPaperAnswer.setExamId(coreExam.getId());
               coreExamPaperAnswer.setExamPaperId(id);
               coreExamPaperAnswer.setOrderNo(orderNo);
               coreExamPaperAnswerList.add(coreExamPaperAnswer);
               orderNo = orderNo + 1L;
            }
         }

         if (coreExam.getTmplMultipleChoiceQty() > 0L) {
            List<CoreQuestionBean> multipleChoiceQuestionChoosenList = this.getRandomQuestion(multipleChoiceQuestionList, coreExam.getTmplMultipleChoiceQty());

            for (int i = 0; i < multipleChoiceQuestionChoosenList.size(); i++) {
               CoreQuestionBean multipleChoiceQuestion = multipleChoiceQuestionChoosenList.get(i);
               CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
               coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
               coreExamPaperAnswer.setQuestionId(multipleChoiceQuestion.getId());
               coreExamPaperAnswer.setExamId(coreExam.getId());
               coreExamPaperAnswer.setExamPaperId(id);
               coreExamPaperAnswer.setOrderNo(orderNo);
               coreExamPaperAnswerList.add(coreExamPaperAnswer);
               orderNo = orderNo + 1L;
            }
         }

         if (coreExam.getTmplCompletionQty() > 0L) {
            List<CoreQuestionBean> completionQuestionChoosenList = this.getRandomQuestion(completionQuestionList, coreExam.getTmplCompletionQty());

            for (int i = 0; i < completionQuestionChoosenList.size(); i++) {
               CoreQuestionBean multipleChoiceQuestion = completionQuestionChoosenList.get(i);
               CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
               coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
               coreExamPaperAnswer.setQuestionId(multipleChoiceQuestion.getId());
               coreExamPaperAnswer.setExamId(coreExam.getId());
               coreExamPaperAnswer.setExamPaperId(id);
               coreExamPaperAnswer.setOrderNo(orderNo);
               coreExamPaperAnswerList.add(coreExamPaperAnswer);
               orderNo = orderNo + 1L;
            }
         }

         if (coreExam.getTmplJudgementQty() > 0L) {
            List<CoreQuestionBean> judgementQuestionChoosenList = this.getRandomQuestion(judgementQuestionList, coreExam.getTmplJudgementQty());

            for (int i = 0; i < judgementQuestionChoosenList.size(); i++) {
               CoreQuestionBean judgementChoiceQuestion = judgementQuestionChoosenList.get(i);
               CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
               coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
               coreExamPaperAnswer.setQuestionId(judgementChoiceQuestion.getId());
               coreExamPaperAnswer.setExamId(coreExam.getId());
               coreExamPaperAnswer.setExamPaperId(id);
               coreExamPaperAnswer.setOrderNo(orderNo);
               coreExamPaperAnswerList.add(coreExamPaperAnswer);
               orderNo = orderNo + 1L;
            }
         }

         if (coreExam.getTmplShortAnswerQty() > 0L) {
            List<CoreQuestionBean> shortAnswerChoiceQuestionChoosenList = this.getRandomQuestion(shortAnswerQuestionList, coreExam.getTmplShortAnswerQty());

            for (int i = 0; i < shortAnswerChoiceQuestionChoosenList.size(); i++) {
               CoreQuestionBean shortAnswerQuestion = shortAnswerChoiceQuestionChoosenList.get(i);
               CoreExamPaperAnswerBean coreExamPaperAnswer = new CoreExamPaperAnswerBean();
               coreExamPaperAnswer.setId(ApplicationContextHelper.getNextIdentity());
               coreExamPaperAnswer.setQuestionId(shortAnswerQuestion.getId());
               coreExamPaperAnswer.setExamId(coreExam.getId());
               coreExamPaperAnswer.setExamPaperId(id);
               coreExamPaperAnswer.setOrderNo(orderNo);
               coreExamPaperAnswerList.add(coreExamPaperAnswer);
               orderNo = orderNo + 1L;
            }
         }

         this.getDao().insert(coreExamPaper);
         this.coreExamPaperAnswerService.getDao().insert(coreExamPaperAnswerList);
      }
   }

   private List<CoreQuestionBean> getRandomQuestion(List<CoreQuestionBean> questionList, Long n) {
      List<Integer> randomIndexList = RandomUtils.shuffle(0, questionList.size());
      List<CoreQuestionBean> questionList4Return = new ArrayList<>();

      for (int i = 0; i < n; i++) {
         questionList4Return.add(questionList.get(randomIndexList.get(i)));
      }

      return questionList4Return;
   }
}
