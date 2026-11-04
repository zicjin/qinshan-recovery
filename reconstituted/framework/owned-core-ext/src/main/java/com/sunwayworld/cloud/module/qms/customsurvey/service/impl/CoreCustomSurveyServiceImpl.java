package com.sunwayworld.cloud.module.qms.customsurvey.service.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumRetBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveyDao;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveyItemService;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveyService;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveySumRetService;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveySumService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.bean.CoreBpmnTaskStatusDTO;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreCustomSurveyServiceImpl implements CoreCustomSurveyService {
   @Autowired
   private CoreCustomSurveyDao coreCustomSurveyDao;
   @Autowired
   private CoreCustomSurveyItemService coreCustomSurveyItemService;
   @Autowired
   private CoreCustomSurveySumService coreCustomSurveySumService;
   @Autowired
   private CoreCustomSurveySumRetService coreCustomSurveySumRetService;
   @Autowired
   private CoreCodeService coreCodeService;

   public CoreCustomSurveyDao getDao() {
      return this.coreCustomSurveyDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreCustomSurveyBean coreCustomSurvey = jsonWrapper.parseUnique(CoreCustomSurveyBean.class);
      coreCustomSurvey.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(coreCustomSurvey);
      List<CoreCodeBean> coreCodeList = this.coreCodeService.getDao().selectListByOneColumnValue("customSurveyItem", "CODECATEGORYID");
      if (!coreCodeList.isEmpty()) {
         List<CoreCustomSurveyItemBean> coreCustomSurveyItemList = coreCodeList.stream().map(e -> {
            CoreCustomSurveyItemBean coreCustomSurveyItem = new CoreCustomSurveyItemBean();
            coreCustomSurveyItem.setId(ApplicationContextHelper.getNextIdentity());
            coreCustomSurveyItem.setSurveyId(coreCustomSurvey.getId());
            coreCustomSurveyItem.setSurveyContent(e.getCode());
            return coreCustomSurveyItem;
         }).collect(Collectors.toList());
         this.coreCustomSurveyItemService.getDao().insert(coreCustomSurveyItemList);
      }

      return coreCustomSurvey.getId();
   }

   @Override
   public Page<CoreCustomSurveyItemBean> selectSurveyItemPaginationByCustomSurveyId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveyItemService.selectPaginationByFilter(SearchFilter.instance().match("SURVEYID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public Page<CoreCustomSurveySumBean> selectSurveySumPagination(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.selectPagination(wrapper);
   }

   @Override
   public Page<CoreCustomSurveySumBean> selectSurveySumAuditablePagination(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.selectAuditablePagination(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertSurveySum(RestJsonWrapperBean wrapper) {
      CoreCustomSurveySumBean coreCustomSurveySum = wrapper.parseUnique(CoreCustomSurveySumBean.class);
      coreCustomSurveySum.setId(ApplicationContextHelper.getNextIdentity());
      coreCustomSurveySum.setStatisNo(ApplicationContextHelper.getNextSequence(this.coreCustomSurveySumService.getDao().getTable(), "DCTJ"));
      List<CoreCodeBean> coreCodeList = this.coreCodeService.getDao().selectListByOneColumnValue("customSurveyItem", "CODECATEGORYID", Order.asc("ORDERNO"));
      if (coreCodeList.isEmpty()) {
         return coreCustomSurveySum.getId();
      } else {
         SearchFilter searchFilter = SearchFilter.instance()
            .match("SURVEYDATE", coreCustomSurveySum.getStartDate())
            .filter(MatchPattern.TGOE)
            .match("SURVEYDATE", coreCustomSurveySum.getEndDate())
            .filter(MatchPattern.TLOE)
            .match("PROCESSSTATUS", "done")
            .filter(MatchPattern.EQ);
         if (!StringUtils.isEmpty(coreCustomSurveySum.getCustomerName())) {
            searchFilter.match("CUSTOMERNAME", coreCustomSurveySum.getCustomerName()).filter(MatchPattern.EQ);
         }

         List<CoreCustomSurveyBean> coreCustomSurveyList = this.selectListByFilter(searchFilter, new Order[0]);
         if (coreCustomSurveyList.isEmpty()) {
            return -1L;
         } else {
            List<Long> customSurveyIdList = coreCustomSurveyList.stream().map(CoreCustomSurveyBean::getId).collect(Collectors.toList());
            List<CoreCustomSurveyItemBean> coreCustomSurveyItemList = this.coreCustomSurveyItemService
               .selectListByFilter(SearchFilter.instance().match("SURVEYID", customSurveyIdList).filter(MatchPattern.OR), new Order[0]);
            List<CoreCustomSurveySumRetBean> coreCustomSurveySumRetList = coreCodeList.stream()
               .map(
                  e -> {
                     CoreCustomSurveySumRetBean coreCustomSurveySumRet = new CoreCustomSurveySumRetBean();
                     coreCustomSurveySumRet.setId(ApplicationContextHelper.getNextIdentity());
                     coreCustomSurveySumRet.setSurveySumId(coreCustomSurveySum.getId());
                     coreCustomSurveySumRet.setSurveyContent(e.getCode());
                     long total = coreCustomSurveyItemList.stream().filter(f -> f.getSurveyContent().equals(e.getCode())).count();
                     List<CoreCustomSurveyItemBean> subCustomSurveyItemList = coreCustomSurveyItemList.stream()
                        .filter(f -> f.getSurveyContent().equals(e.getCode()))
                        .collect(Collectors.toList());
                     Long verySatisfiedCount = subCustomSurveyItemList.stream().filter(f -> String.valueOf(f.getEvaluate()).equals("5")).count();
                     Long satisfiedCount = subCustomSurveyItemList.stream().filter(f -> String.valueOf(f.getEvaluate()).equals("4")).count();
                     Long commonlyCount = subCustomSurveyItemList.stream().filter(f -> String.valueOf(f.getEvaluate()).equals("3")).count();
                     Long needsImprovedCount = subCustomSurveyItemList.stream().filter(f -> String.valueOf(f.getEvaluate()).equals("2")).count();
                     Long dissatisfiedCount = subCustomSurveyItemList.stream().filter(f -> String.valueOf(f.getEvaluate()).equals("1")).count();
                     coreCustomSurveySumRet.setTotal(total);
                     coreCustomSurveySumRet.setVerySatisfiedCount(verySatisfiedCount);
                     coreCustomSurveySumRet.setSatisfiedCount(satisfiedCount);
                     coreCustomSurveySumRet.setCommonlyCount(commonlyCount);
                     coreCustomSurveySumRet.setNeedsImprovedCount(needsImprovedCount);
                     coreCustomSurveySumRet.setDissatisfiedCount(dissatisfiedCount);
                     return coreCustomSurveySumRet;
                  }
               )
               .collect(Collectors.toList());
            this.coreCustomSurveySumService.getDao().insert(coreCustomSurveySum);
            this.coreCustomSurveySumRetService.getDao().insert(coreCustomSurveySumRetList);
            return coreCustomSurveySum.getId();
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteSurveySum(RestJsonWrapperBean wrapper) {
      this.coreCustomSurveySumService.delete(wrapper);
   }

   @Override
   public Page<CoreCustomSurveySumRetBean> selectSurveySumRetPaginationByCustomSurveyId(Long id, RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumRetService.selectPaginationByFilter(SearchFilter.instance().match("SURVEYSUMID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void completeCustomSurvey(RestJsonWrapperBean wrapper) {
      List<CoreCustomSurveyBean> coreCustomSurveyList = wrapper.parse(CoreCustomSurveyBean.class);
      coreCustomSurveyList.forEach(e -> e.setProcessStatus(ProcessStatus.DONE.name().toLowerCase()));
      this.getDao().update(coreCustomSurveyList, new String[]{"processStatus"});
   }

   @Override
   public CoreCustomSurveySumBean selectCustomSurveySumDetail(Long id) {
      return this.coreCustomSurveySumService.selectDetail(id);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_START)
   @Audit("AUDIT.PROCESS_START")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.startProcess(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_WITHDRAW)
   @Audit("AUDIT.PROCESS_WITHDRAW")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> withdrawProcess(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.withdrawProcess(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
   @Audit("AUDIT.PROCESS_COMPLETE")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.completeTask(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_END)
   @Audit("AUDIT.PROCESS_END")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> endTask(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.endTask(wrapper);
   }

   @Override
   public CoreBpmnTaskStatusDTO selectBpmnTaskStatus(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.selectBpmnTaskStatus(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_REJECT")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.rejectTask(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_REJECT)
   @Audit("AUDIT.PROCESS_ODD_REJECT")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> oddRejectTask(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.oddRejectTask(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.PROCESS_UNDO)
   @Audit("AUDIT.PROCESS_UNDO")
   @Override
   public List<CoreBpmnInstanceStatusDTO<Long>> undo(RestJsonWrapperBean wrapper) {
      return this.coreCustomSurveySumService.undo(wrapper);
   }

   @Override
   public CoreServiceStatusDTO getBpmnInstanceTaskById(Long id) {
      return this.coreCustomSurveySumService.selectStatus(id, null);
   }
}
