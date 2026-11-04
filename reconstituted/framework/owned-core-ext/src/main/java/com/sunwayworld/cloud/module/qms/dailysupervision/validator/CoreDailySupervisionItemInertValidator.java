package com.sunwayworld.cloud.module.qms.dailysupervision.validator;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionItemService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreDailySupervisionItemInertValidator implements DataValidator {
   @Autowired
   private CoreDailySupervisionItemService coreDailySupervisionItemService;

   @Override
   public boolean doValid(Object... args) {
      Long dailySupId = (Long)args[0];
      List<CoreDailySupervisionItemBean> coreDailySupervisionItemList = this.coreDailySupervisionItemService
         .selectListByFilter(SearchFilter.instance().match("DAILYSUPID", dailySupId).filter(MatchPattern.EQ), new Order[0]);
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[1];
      CoreDailySupervisionItemBean coreDailySupervisionItem = wrapper.parseUnique(CoreDailySupervisionItemBean.class);

      for (CoreDailySupervisionItemBean e : coreDailySupervisionItemList) {
         if (e.getAuditName().equals(coreDailySupervisionItem.getAuditName())) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.DAILYSUPERVISION.ALERT.ONLY_ADD_SAME_ITEM_ONCE"));
            return false;
         }
      }

      return true;
   }
}
