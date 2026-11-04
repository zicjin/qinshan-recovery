package com.sunwayworld.cloud.module.qms.riskidentificaregist.validator;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaRegistBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaFactorService;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaRegistService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CoreRiskIdentificaRegistSubmitTaskValidator implements DataValidator {
   @Autowired
   private CoreRiskIdentificaRegistService coreRiskIdentificaRegistService;
   @Autowired
   @Lazy
   private CoreRiskIdentificaFactorService coreRiskIdentificaFactorService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = (RestJsonWrapperBean)args[0];
      List<Long> riskIdentificaRegistIdList = wrapper.parseId(Long.class);
      List<CoreRiskIdentificaRegistBean> coreRiskIdentificaRegistList = this.coreRiskIdentificaRegistService.selectListByIds(riskIdentificaRegistIdList);
      boolean requiredValid = coreRiskIdentificaRegistList.stream().noneMatch(e -> StringUtils.isEmpty(e.getRiskDesc()));
      if (!requiredValid) {
         this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.DOVALID.TIP.EXIST_REQUIRED_COLUMN_IS_EMPTY_MODIFY_BEFORE_SUBMIT"));
         return false;
      } else {
         List<CoreRiskIdentificaFactorBean> coreRiskIdentificaFactorList = this.coreRiskIdentificaFactorService
            .selectListByFilter(SearchFilter.instance().match("RISKREGISTID", riskIdentificaRegistIdList).filter(MatchPattern.OR), new Order[0]);
         List<Long> riskRegistIdList = coreRiskIdentificaFactorList.stream().map(e -> e.getRiskRegistId()).collect(Collectors.toList());
         if (!riskRegistIdList.containsAll(riskIdentificaRegistIdList)) {
            this.addConstraintViolation(I18nHelper.getMessage("QMS.MODULE.RISKIDENTIFICAREGIST.TIP.FILL_RISK_FACTOR_BEFORE_COMPLETE"));
            return false;
         } else {
            return true;
         }
      }
   }
}
