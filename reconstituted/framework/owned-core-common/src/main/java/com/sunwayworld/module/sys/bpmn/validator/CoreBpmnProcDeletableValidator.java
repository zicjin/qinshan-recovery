package com.sunwayworld.module.sys.bpmn.validator;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreBpmnProcDeletableValidator implements DataValidator {
   @Autowired
   private CoreBpmnProcService procService;
   @Autowired
   private CoreBpmnInstanceService instanceService;

   @Override
   public boolean doValid(Object... args) {
      List<CoreBpmnProcBean> deleteProcList = ((RestJsonWrapperBean)args[0]).parse(CoreBpmnProcBean.class);
      List<Long> procIdList = deleteProcList.stream().map(CoreBpmnProcBean::getId).collect(Collectors.toList());
      List<CoreBpmnInstanceBean> instanceList = this.instanceService.getDao().selectListByOneColumnValues(procIdList, "PROCID");
      if (!instanceList.isEmpty()) {
         List<Long> usedProcIdList = instanceList.stream().map(CoreBpmnInstanceBean::getProcId).collect(Collectors.toList());
         List<CoreBpmnProcBean> usedProcList = this.procService.selectListByIds(usedProcIdList);
         StringBuilder sb = new StringBuilder();

         for (CoreBpmnProcBean usedProc : usedProcList) {
            if (sb.length() > 0) {
               sb.append(",");
            }

            sb.append(usedProc.getProcCode());
         }

         this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.SYS.T_CORE_BPMN_PROC.TIP.UNABLE_DELETE_INUSE_PROC", sb.toString()));
         return false;
      } else {
         return true;
      }
   }
}
