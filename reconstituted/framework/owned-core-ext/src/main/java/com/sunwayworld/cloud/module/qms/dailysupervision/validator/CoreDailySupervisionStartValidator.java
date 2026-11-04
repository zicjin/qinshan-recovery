package com.sunwayworld.cloud.module.qms.dailysupervision.validator;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionConService;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionItemService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreDailySupervisionStartValidator implements DataValidator {
   @Autowired
   private CoreDailySupervisionItemService coreDailySupervisionItemService;
   @Autowired
   private CoreDailySupervisionConService coreDailySupervisionConService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];

      for (CoreDailySupervisionBean e : wrapper.parse(CoreDailySupervisionBean.class)) {
         List<CoreDailySupervisionItemBean> coreDailySupervisionItemList = this.coreDailySupervisionItemService
            .selectListByFilter(SearchFilter.instance().match("DAILYSUPID", e.getId()).filter(MatchPattern.EQ), new Order[0]);
         if (coreDailySupervisionItemList.isEmpty()) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.DAILYSUPERVISION.ALERT.SUPERVISION_ITEM_NOT_EMPTY"));
            return false;
         }

         for (CoreDailySupervisionItemBean m : coreDailySupervisionItemList) {
            List<CoreDailySupervisionConBean> coreDailySupervisionConList = this.coreDailySupervisionConService
               .selectListByFilter(SearchFilter.instance().match("DAILYSUPITEMID", m.getId()).filter(MatchPattern.EQ), new Order[0]);
            if (coreDailySupervisionConList.isEmpty()) {
               this.addConstraintViolation(I18nHelper.getMessage("QMS.DAILYSUPERVISION.ALERT.SUPERVISION_CON_NOT_EMPTY"));
               return false;
            }

            if (coreDailySupervisionConList.stream().anyMatch(d -> StringUtils.isEmpty(d.getAuditContent()))) {
               this.addConstraintViolation(I18nHelper.getMessage("QMS.DAILYSUPERVISION.ALERT.ADD_DAILY_SUPERVISION_CON_AUDITCONTENT"));
               return false;
            }
         }
      }

      return true;
   }
}
