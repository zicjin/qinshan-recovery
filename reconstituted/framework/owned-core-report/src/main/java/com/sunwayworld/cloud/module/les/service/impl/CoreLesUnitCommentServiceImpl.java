package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.bean.CoreLesUnitCommentBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesUnitCommentDao;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceUnitService;
import com.sunwayworld.cloud.module.les.service.CoreLesUnitCommentService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreLesUnitCommentServiceImpl implements CoreLesUnitCommentService {
   @Autowired
   private CoreLesUnitCommentDao coreLesUnitCommentDao;
   @Autowired
   private CoreLesInstanceUnitService coreLesInstanceUnitService;

   public CoreLesUnitCommentDao getDao() {
      return this.coreLesUnitCommentDao;
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreLesUnitCommentBean coreLesUnitComment = jsonWrapper.parseUnique(CoreLesUnitCommentBean.class);
      coreLesUnitComment.setId(ApplicationContextHelper.getNextIdentity());
      coreLesUnitComment.setCommentUserId(LocalContextHelper.getLoginUserId());
      coreLesUnitComment.setCommentUserName(LocalContextHelper.getLoginUserName());
      coreLesUnitComment.setCommentTime(LocalDateTime.now());
      Long instanceUnitId = coreLesUnitComment.getLesInstanceUnitId();
      CoreLesInstanceUnitBean instanceUnit = this.coreLesInstanceUnitService.selectById(instanceUnitId);
      instanceUnit.setCommentStatus("havingUnreply");
      this.coreLesInstanceUnitService.getDao().updateIfChanged(instanceUnit);
      this.getDao().insert(coreLesUnitComment);
      return coreLesUnitComment.getId();
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void update(RestJsonWrapperBean jsonWrapper) {
      CoreLesUnitCommentBean coreLesUnitComment = jsonWrapper.parseUnique(CoreLesUnitCommentBean.class);
      coreLesUnitComment.setCommentUserId(LocalContextHelper.getLoginUserId());
      coreLesUnitComment.setCommentUserName(LocalContextHelper.getLoginUserName());
      coreLesUnitComment.setCommentTime(LocalDateTime.now());
      this.getDao().update(coreLesUnitComment, new String[0]);
   }

   @Transactional
   @Audit("AUDIT.SAVE")
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Override
   public void reply(RestJsonWrapperBean jsonWrapper) {
      CoreLesUnitCommentBean coreLesUnitComment = jsonWrapper.parseUnique(CoreLesUnitCommentBean.class);
      coreLesUnitComment.setRepliedUserId(LocalContextHelper.getLoginUserId());
      coreLesUnitComment.setRepliedUserName(LocalContextHelper.getLoginUserName());
      coreLesUnitComment.setRepliedTime(LocalDateTime.now());
      if (StringUtils.isEmpty(coreLesUnitComment.getReply())) {
         coreLesUnitComment.setStatus("unreply");
      } else {
         coreLesUnitComment.setStatus("replied");
      }

      this.getDao().update(coreLesUnitComment, new String[0]);
      this.updateInstanceUnitCommentStatus(Arrays.asList(coreLesUnitComment.getLesInstanceUnitId()));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void delete(RestJsonWrapperBean wrapper) {
      CoreLesUnitCommentService.super.delete(wrapper);
      List<CoreLesUnitCommentBean> commentList = wrapper.parse(CoreLesUnitCommentBean.class);
      List<Long> instanceUnitIdList = commentList.stream().map(e -> e.getLesInstanceUnitId()).collect(Collectors.toList());
      this.updateInstanceUnitCommentStatus(instanceUnitIdList);
   }

   private void updateInstanceUnitCommentStatus(List<Long> instanceUnitIdList) {
      List<CoreLesInstanceUnitBean> instanceUnitList4Update = new ArrayList<>();
      List<CoreLesUnitCommentBean> allUnitCommentList = this.selectListByFilter(
         SearchFilter.instance().match("LESINSTANCEUNITID", instanceUnitIdList).filter(MatchPattern.OR), new Order[0]
      );
      instanceUnitIdList.forEach(
         instanceUnitId -> {
            List<CoreLesUnitCommentBean> lesUnitCommentList = allUnitCommentList.stream()
               .filter(e -> e.getLesInstanceUnitId() == instanceUnitId)
               .collect(Collectors.toList());
            CoreLesInstanceUnitBean instanceUnit = new CoreLesInstanceUnitBean();
            instanceUnit.setId(instanceUnitId);
            if (lesUnitCommentList.isEmpty()) {
               instanceUnit.setCommentStatus(null);
            } else if (lesUnitCommentList.stream().allMatch(comment -> "replied".equals(comment.getStatus()))) {
               instanceUnit.setCommentStatus("allReplied");
            } else {
               instanceUnit.setCommentStatus("havingUnreply");
            }

            instanceUnitList4Update.add(instanceUnit);
         }
      );
      this.coreLesInstanceUnitService.getDao().update(instanceUnitList4Update, "COMMENTSTATUS");
   }
}
