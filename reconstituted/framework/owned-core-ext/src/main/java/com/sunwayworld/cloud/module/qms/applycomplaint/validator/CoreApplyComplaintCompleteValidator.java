package com.sunwayworld.cloud.module.qms.applycomplaint.validator;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintStatusDTO;
import com.sunwayworld.cloud.module.qms.applycomplaint.service.CoreApplyComplaintService;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreApplyComplaintCompleteValidator implements DataValidator {
   @Autowired
   private CoreApplyComplaintService coreApplyComplaintService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> idList = wrapper.parseId(Long.class);
      MapperParameter parameter = new MapperParameter();
      parameter.setFilter(SearchFilter.instance().match("ID", idList).filter(MatchPattern.OR));
      List<CoreApplyComplaintStatusDTO> applyComplaintStatusDTOList = this.coreApplyComplaintService
         .selectAuditStatusByCondition(parameter)
         .stream()
         .map(e -> PersistableHelper.mapToPersistable((Map<String, Object>)e, CoreApplyComplaintStatusDTO.class))
         .collect(Collectors.toList());
      boolean flag1 = applyComplaintStatusDTOList.stream()
         .filter(e -> "accept".equals(e.getStatusCode()))
         .anyMatch(e -> StringUtils.isEmpty(e.getInvestigationUser()) || StringUtils.isEmpty(e.getBeAuditOrgName()));
      boolean flag2 = applyComplaintStatusDTOList.stream()
         .filter(e -> "quaOrgAudit".equals(e.getStatusCode()))
         .anyMatch(e -> StringUtils.isEmpty(e.getSuggestion()));
      boolean flag3 = applyComplaintStatusDTOList.stream()
         .filter(e -> "quasuggest".equals(e.getStatusCode()))
         .anyMatch(e -> StringUtils.isEmpty(e.getQmResult()));
      boolean flag4 = applyComplaintStatusDTOList.stream()
         .filter(e -> "feedback".equals(e.getStatusCode()))
         .anyMatch(e -> StringUtils.isEmpty(e.getFeedback()));
      if (!flag1 && !flag2 && !flag3 && !flag4) {
         return true;
      } else {
         this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.APPLYCOMPLAINT.TIP.DATA_NOT_EMPTY"));
         return false;
      }
   }
}
