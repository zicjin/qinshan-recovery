package com.sunwayworld.cloud.module.qms.dailysupervision.validator;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionConService;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionItemService;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CoreDailySupervisionCompleteValidator implements DataValidator {
   @Autowired
   private CoreDailySupervisionService coreDailySupervisionService;
   @Autowired
   @Lazy
   private CoreDailySupervisionItemService coreDailySupervisionItemService;
   @Autowired
   @Lazy
   private CoreDailySupervisionConService coreDailySupervisionConService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];

      for (Long id : wrapper.parseId(Long.class)) {
         CoreServiceStatusDTO serviceStatusDTO = this.coreDailySupervisionService.selectStatus(id, null);
         String statusCode = serviceStatusDTO.getAuditStatus().getStatusCode();
         if ("supervise".equals(statusCode)) {
            List<CoreDailySupervisionItemBean> coreDailySupervisionItemList = this.coreDailySupervisionItemService
               .getDao()
               .selectListByOneColumnValue(id, "DAILYSUPID");
            List<Long> itemIdList = coreDailySupervisionItemList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<CoreDailySupervisionConBean> coreDailySupervisionConList = this.coreDailySupervisionConService
               .selectListByFilter(SearchFilter.instance().match("DAILYSUPITEMID", itemIdList).filter(MatchPattern.OR), new Order[0]);
            if (coreDailySupervisionConList.stream().anyMatch(e -> e.getAuditResult() == null)) {
               this.addConstraintViolation(I18nHelper.getMessage("QMS.DAILYSUPERVISION.ALERT.AUDIT_RESULT_NOT_EMPTY"));
               return false;
            }
         }
      }

      return true;
   }
}
