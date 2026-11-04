package com.sunwayworld.cloud.module.train.question.service.impl;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.cloud.module.train.question.persistent.dao.CoreQuestionOptDao;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionOptService;
import com.sunwayworld.cloud.module.train.question.service.CoreQuestionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
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
public class CoreQuestionOptServiceImpl implements CoreQuestionOptService {
   @Autowired
   private CoreQuestionOptDao coreQuestionOptDao;
   @Autowired
   @Lazy
   private CoreQuestionService coreQuestionService;

   public CoreQuestionOptDao getDao() {
      return this.coreQuestionOptDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreQuestionOptBean questionOpt = wrapper.parseUnique(CoreQuestionOptBean.class);
      questionOpt.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(questionOpt);
      CoreUserBean currentUser = LocalContextHelper.getLoginUser();
      CoreQuestionBean question4Update = new CoreQuestionBean();
      question4Update.setId(questionOpt.getQuestionId());
      question4Update.setLastUpdatedById(currentUser.getId());
      question4Update.setLastUpdatedByName(currentUser.getUserName());
      question4Update.setLastUpdatedTime(LocalDateTime.now());
      this.coreQuestionService.getDao().update(question4Update, "LASTUPDATEDBYID", "LASTUPDATEDBYNAME", "LASTUPDATEDTIME");
      return questionOpt.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      List<CoreQuestionOptBean> deleteList = wrapper.parse(this.getDao().getType());
      if (!deleteList.isEmpty()) {
         List<Long> questionOptIdList = deleteList.stream().map(CoreQuestionOptBean::getId).collect(Collectors.toList());
         List<CoreQuestionOptBean> questionOptList = this.getDao().selectListByIds(questionOptIdList, new Order[0]);
         List<Long> questionIdList = questionOptList.stream().map(CoreQuestionOptBean::getQuestionId).distinct().collect(Collectors.toList());
         CoreUserBean currentUser = LocalContextHelper.getLoginUser();
         LocalDateTime currentTime = LocalDateTime.now();
         List<CoreQuestionBean> questionList4Update = new ArrayList<>();
         questionIdList.forEach(e -> {
            CoreQuestionBean question = new CoreQuestionBean();
            question.setId(e);
            question.setLastUpdatedById(currentUser.getId());
            question.setLastUpdatedByName(currentUser.getUserName());
            question.setLastUpdatedTime(currentTime);
            questionList4Update.add(question);
         });
         this.coreQuestionService.getDao().update(questionList4Update, "LASTUPDATEDBYID", "LASTUPDATEDBYNAME", "LASTUPDATEDTIME");
         CoreTableService tableService = ApplicationContextHelper.getBean(CoreTableService.class);
         deleteList.forEach(t -> tableService.deleteCascade(PersistableMetadataHelper.getTableName(this.getDao().getType()), t.getId()));
      }
   }
}
