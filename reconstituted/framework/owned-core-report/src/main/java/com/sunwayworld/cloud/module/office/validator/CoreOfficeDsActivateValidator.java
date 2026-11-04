package com.sunwayworld.cloud.module.office.validator;

import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsBean;
import com.sunwayworld.cloud.module.office.bean.CoreOfficeDsFieldBean;
import com.sunwayworld.cloud.module.office.service.CoreOfficeDsFieldService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CoreOfficeDsActivateValidator implements DataValidator {
   @Autowired
   private CoreOfficeDsFieldService dsFieldService;

   @Override
   public boolean doValid(Object... args) {
      RestJsonWrapperBean wrapper = ArrayUtils.getUniqueTypeValue(args, RestJsonWrapperBean.class);
      List<CoreOfficeDsBean> dsList = wrapper.parse(CoreOfficeDsBean.class);
      List<Long> dsIdList = dsList.stream().map(CoreOfficeDsBean::getId).distinct().collect(Collectors.toList());
      List<CoreOfficeDsFieldBean> dsFieldList = this.dsFieldService
         .selectListByFilter(SearchFilter.instance().match("DSID", dsIdList).filter(MatchPattern.OR), new Order[0]);

      for (CoreOfficeDsFieldBean field : dsFieldList) {
         if (StringUtils.isBlank(field.getShowColumnName())) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.OFFICE.DSES.TIP.EXIST_EMPTY_FIELD_NAME"));
            return false;
         }

         if (isConSpeChar(field.getShowColumnName())) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.OFFICE.DSES.TIP.EXIST_SPECIAL_CHARACTER_FIELD_NAME"));
            return false;
         }
      }

      for (Long dsId : dsIdList) {
         List<CoreOfficeDsFieldBean> dsFieldItemList = dsFieldList.stream().filter(e -> dsId.equals(e.getDsId())).collect(Collectors.toList());
         List<String> repeatColumnList = dsFieldItemList.stream().map(e -> e.getShowColumnName()).distinct().collect(Collectors.toList());
         if (repeatColumnList.size() != dsFieldItemList.size()) {
            this.addConstraintViolation(I18nHelper.getMessage("CORE.MODULE.OFFICE.DSES.TIP.EXIST_REPEAT_FIELD_NAME"));
            return false;
         }
      }

      return true;
   }

   private static boolean isConSpeChar(String str) {
      String regEx = "[`~!@#$%^&*()+=|{}':;'\\[\\]<>/?~！@#￥%……&*——+|{}【】‘；：”“’。，、？]";
      Pattern p = Pattern.compile(regEx);
      Matcher m = p.matcher(str);
      return m.find();
   }
}
