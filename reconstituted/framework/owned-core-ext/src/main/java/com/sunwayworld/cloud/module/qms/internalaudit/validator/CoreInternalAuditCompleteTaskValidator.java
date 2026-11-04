package com.sunwayworld.cloud.module.qms.internalaudit.validator;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditService;
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
public class CoreInternalAuditCompleteTaskValidator implements DataValidator {
   @Autowired
   private CoreInternalAuditService coreInternalAuditService;
   @Autowired
   @Lazy
   private CoreBpmnInstanceService coreBpmnInstanceService;
   @Autowired
   @Lazy
   private CoreBpmnInstanceTaskService coreBpmnInstanceTaskService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> internalAuditIdList = wrapper.parseId(Long.class);
      List<CoreInternalAuditBean> coreInternalAuditList = this.coreInternalAuditService.selectListByIds(internalAuditIdList);
      List<String> targetIdList = internalAuditIdList.stream()
         .map(e -> this.coreInternalAuditService.getDao().getTable() + "$" + e)
         .collect(Collectors.toList());
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
            CoreInternalAuditBean coreInternalAudit = coreInternalAuditList.stream()
               .filter(e -> (this.coreInternalAuditService.getDao().getTable() + "$" + e.getId()).equals(coreBpmnInstance.getTargetId()))
               .collect(Collectors.toList())
               .get(0);
            if (StringUtils.isEmpty(coreInternalAudit.getReportContent()) || StringUtils.isEmpty(coreInternalAudit.getAuditResult())) {
               this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
               return false;
            }
         }
      }

      return true;
   }
}
