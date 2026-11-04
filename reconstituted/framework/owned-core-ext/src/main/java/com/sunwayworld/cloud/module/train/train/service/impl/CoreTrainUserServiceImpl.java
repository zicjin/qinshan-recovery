package com.sunwayworld.cloud.module.train.train.service.impl;

import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperService;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.cloud.module.train.train.persistent.dao.CoreTrainUserDao;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainUserService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreTrainUserServiceImpl implements CoreTrainUserService {
   @Autowired
   private CoreTrainUserDao coreTrainUserDao;
   @Autowired
   private CoreUserService coreUserService;
   @Autowired
   private CoreExamPaperService coreExamPaperService;

   public CoreTrainUserDao getDao() {
      return this.coreTrainUserDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      List<CoreTrainUserBean> trainUserList = wrapper.parse(CoreTrainUserBean.class);
      if (trainUserList.isEmpty()) {
         return null;
      } else {
         List<Long> idList = ApplicationContextHelper.getNextIdentityList(trainUserList.size());

         for (int i = 0; i < trainUserList.size(); i++) {
            Long id = idList.get(i);
            trainUserList.get(i).setId(id);
         }

         this.getDao().insert(trainUserList);
         return idList.get(0);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void sign(Long id, RestJsonWrapperBean wrapper) {
      CoreTrainBean train = wrapper.parseUnique(CoreTrainBean.class);
      String userId = train.getExt$Item("userid");
      String userName = train.getExt$Item("username");
      this.validateSignUser(userId, userName);
      List<CoreTrainUserBean> trainUserList = this.getDao().selectListByOneColumnValue(id, "TRAINID", new Order[0]);
      if (trainUserList.stream().noneMatch(f -> f.getUserId().equals(userId))) {
         throw new InvalidDataException("CORE.MODULE.TRAIN.TRAIN.TIP.SIGN_UP_VALIDATOR");
      } else {
         CoreTrainUserBean trainUser = trainUserList.stream().filter(f -> f.getUserId().equals(userId)).findFirst().get();
         if ("1".equals(trainUser.getActivatedFlag())) {
            throw new InvalidDataException("CORE.MODULE.TRAIN.TRAIN.TIP.REPEAT_SIGN_UP");
         } else {
            CoreTrainUserBean trainUser4Update = new CoreTrainUserBean();
            trainUser4Update.setId(trainUser.getId());
            trainUser4Update.setActivatedFlag("1");
            trainUser4Update.setActivatedById(userId);
            trainUser4Update.setActivatedByName(userName);
            trainUser4Update.setActivatedTime(LocalDateTime.now());
            this.getDao().update(trainUser4Update, new String[]{"ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME"});
            if ("testOnline".equals(train.getAssessmentMethod())) {
               this.coreExamPaperService.generateExamPaper(train.getExamId(), train.getId(), userId, userName);
            }
         }
      }
   }

   private void validateSignUser(String userId, String userName) {
      CoreUserBean cond = new CoreUserBean();
      cond.setId(userId);
      cond.setUserName(userName);
      CoreUserBean user = this.coreUserService.getDao().selectFirstIfPresent(cond);
      if (user == null) {
         throw new InvalidDataException("CORE.MODULE.TRAIN.TRAIN.TIP.SIGN_UP_INVALID_USER");
      }
   }
}
