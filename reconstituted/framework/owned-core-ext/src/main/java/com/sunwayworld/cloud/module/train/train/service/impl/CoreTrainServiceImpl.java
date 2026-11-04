package com.sunwayworld.cloud.module.train.train.service.impl;

import com.sunwayworld.cloud.module.train.support.enums.TrainPlanStatus;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainBean;
import com.sunwayworld.cloud.module.train.train.bean.CoreTrainUserBean;
import com.sunwayworld.cloud.module.train.train.persistent.dao.CoreTrainDao;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainService;
import com.sunwayworld.cloud.module.train.train.service.CoreTrainUserService;
import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanBean;
import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanUserBean;
import com.sunwayworld.cloud.module.train.trainplan.service.CoreTrainPlanService;
import com.sunwayworld.cloud.module.train.trainplan.service.CoreTrainPlanUserService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
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
public class CoreTrainServiceImpl implements CoreTrainService {
   @Autowired
   private CoreTrainDao coreTrainDao;
   @Autowired
   @Lazy
   private CoreTrainPlanUserService coreTrainPlanUserService;
   @Autowired
   @Lazy
   private CoreTrainUserService coreTrainUserService;
   @Autowired
   @Lazy
   private CoreTrainPlanService coreTrainPlanService;

   public CoreTrainDao getDao() {
      return this.coreTrainDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      Long trainId = ApplicationContextHelper.getNextIdentity();
      CoreTrainBean train = wrapper.parseUnique(CoreTrainBean.class);
      train.setId(trainId);
      this.getDao().insert(train);
      List<CoreTrainPlanUserBean> trainPlanUserList = this.coreTrainPlanUserService.getDao().selectListByOneColumnValue(train.getTrainPlanId(), "TRAINPLANID");
      if (!trainPlanUserList.isEmpty()) {
         List<CoreTrainUserBean> trainUserList4Insert = new ArrayList<>();
         List<Long> trainUserIdList = ApplicationContextHelper.getNextIdentityList(trainPlanUserList.size());

         for (int i = 0; i < trainPlanUserList.size(); i++) {
            CoreTrainUserBean trainUser = new CoreTrainUserBean();
            Long trainUserId = trainUserIdList.get(i);
            CoreTrainPlanUserBean trainPlanUser = trainPlanUserList.get(i);
            trainUser.setId(trainUserId);
            trainUser.setUserId(trainPlanUser.getUserId());
            trainUser.setUserName(trainPlanUser.getUserName());
            trainUser.setTrainId(trainId);
            trainUserList4Insert.add(trainUser);
         }

         this.coreTrainUserService.getDao().insert(trainUserList4Insert);
      }

      return trainId;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      List<CoreTrainBean> itemList = wrapper.parse(this.getDao().getType());
      itemList.forEach(i -> this.setActivateProperties(i, true));
      this.getDao().update(itemList, new String[]{"ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME"});
      List<Long> trainPlanIdList = itemList.stream().map(f -> f.getTrainPlanId()).distinct().collect(Collectors.toList());
      List<CoreTrainPlanBean> trainPlanList4Update = this.coreTrainPlanService
         .selectListByIds(trainPlanIdList)
         .stream()
         .filter(f -> !f.getStatus().equals("implemented"))
         .collect(Collectors.toList());
      trainPlanList4Update.forEach(e -> e.setStatus(TrainPlanStatus.IMPLEMENTED.name().toLowerCase()));
      this.coreTrainPlanService.getDao().update(trainPlanList4Update, "STATUS");
   }
}
