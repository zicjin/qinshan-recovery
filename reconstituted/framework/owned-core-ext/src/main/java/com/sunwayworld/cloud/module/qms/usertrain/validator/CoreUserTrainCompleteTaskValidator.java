package com.sunwayworld.cloud.module.qms.usertrain.validator;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.cloud.module.qms.usertrain.service.CoreUserTrainService;
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
public class CoreUserTrainCompleteTaskValidator implements DataValidator {
   @Autowired
   private CoreUserTrainService coreUserTrainService;
   @Autowired
   @Lazy
   private CoreBpmnInstanceService coreBpmnInstanceService;
   @Autowired
   @Lazy
   private CoreBpmnInstanceTaskService coreBpmnInstanceTaskService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> userTrainIdList = wrapper.parseId(Long.class);
      List<CoreUserTrainBean> coreUserTrainList = this.coreUserTrainService.selectListByIds(userTrainIdList);
      List<String> targetIdList = userTrainIdList.stream().map(e -> this.coreUserTrainService.getDao().getTable() + "$" + e).collect(Collectors.toList());
      List<CoreBpmnInstanceBean> coreBpmnInstanceList = this.coreBpmnInstanceService
         .selectListByFilter(SearchFilter.instance().match("TARGETID", targetIdList).filter(MatchPattern.OR), new Order[0]);
      List<Long> bpmnInstanceIdList = coreBpmnInstanceList.stream().map(e -> e.getId()).collect(Collectors.toList());

      for (CoreBpmnInstanceTaskBean b : this.coreBpmnInstanceTaskService
         .selectListByFilter(SearchFilter.instance().match("INSTID", bpmnInstanceIdList).filter(MatchPattern.OR), new Order[0])) {
         if ("evaluate".equals(b.getStatusCode())) {
            CoreBpmnInstanceBean coreBpmnInstance = coreBpmnInstanceList.stream()
               .filter(e -> e.getId().equals(b.getInstId()))
               .collect(Collectors.toList())
               .get(0);
            CoreUserTrainBean coreUserTrain = coreUserTrainList.stream()
               .filter(e -> (this.coreUserTrainService.getDao().getTable() + "$" + e.getId()).equals(coreBpmnInstance.getTargetId()))
               .collect(Collectors.toList())
               .get(0);
            if (StringUtils.isEmpty(coreUserTrain.getTrainEvaluation())) {
               this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.QMS.USERTRAIN.TIP.TRAIN_EVLUATION_NOT_EMPTY"));
               return false;
            }
         }
      }

      return true;
   }
}
