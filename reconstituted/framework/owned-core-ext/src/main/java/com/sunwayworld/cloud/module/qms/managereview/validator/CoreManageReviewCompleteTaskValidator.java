package com.sunwayworld.cloud.module.qms.managereview.validator;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewImproveService;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CoreManageReviewCompleteTaskValidator implements DataValidator {
   @Autowired
   private CoreManageReviewService coreManageReviewService;
   @Autowired
   @Lazy
   private CoreManageReviewImproveService coreManageReviewImproveService;
   @Autowired
   @Lazy
   private CoreBpmnInstanceService coreBpmnInstanceService;
   @Autowired
   @Lazy
   private CoreBpmnInstanceTaskService coreBpmnInstanceTaskService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> manageReviewIdList = wrapper.parseId(Long.class);
      List<CoreManageReviewBean> coreManageReviewList = this.coreManageReviewService.selectListByIds(manageReviewIdList);
      List<String> targetIdList = manageReviewIdList.stream().map(e -> this.coreManageReviewService.getDao().getTable() + "$" + e).collect(Collectors.toList());
      List<CoreBpmnInstanceBean> coreBpmnInstanceList = this.coreBpmnInstanceService
         .selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> bpmnInstanceIdList = coreBpmnInstanceList.stream().map(e -> e.getId()).collect(Collectors.toList());

      for (CoreBpmnInstanceTaskBean b : this.coreBpmnInstanceTaskService
         .selectListByFilter(SearchFilter.instance().match("INSTID", bpmnInstanceIdList).filter(MatchPattern.OR), new Order[0])) {
         if ("editReport".equals(b.getStatusCode())) {
            CoreBpmnInstanceBean coreBpmnInstance = coreBpmnInstanceList.stream()
               .filter(e -> e.getId().equals(b.getInstId()))
               .collect(Collectors.toList())
               .get(0);
            CoreManageReviewBean coreManageReview = coreManageReviewList.stream()
               .filter(e -> (this.coreManageReviewService.getDao().getTable() + "$" + e.getId()).equals(coreBpmnInstance.getTargetId()))
               .collect(Collectors.toList())
               .get(0);
            if (StringUtils.isEmpty(coreManageReview.getReviewResult()) || StringUtils.isEmpty(coreManageReview.getReviewDescription())) {
               this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
               return false;
            }

            List<CoreManageReviewImproveBean> coreManageReviewImproveList = this.coreManageReviewImproveService
               .getDao()
               .selectListByOneColumnValue(coreManageReview.getId(), "manageReviewId");
            if (coreManageReviewImproveList.isEmpty()) {
               this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.MANAGEREVIEW.TIP.FILL_MANAGE_REVIEW_IMPROVE_BEFORE_COMPLETE"));
               return false;
            }
         }
      }

      return true;
   }
}
