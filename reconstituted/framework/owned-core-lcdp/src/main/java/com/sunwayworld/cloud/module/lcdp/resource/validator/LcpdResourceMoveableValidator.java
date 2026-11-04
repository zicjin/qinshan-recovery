package com.sunwayworld.cloud.module.lcdp.resource.validator;

import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceHistoryService;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpResourceService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.DataValidator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class LcpdResourceMoveableValidator implements DataValidator {
   @Autowired
   private LcdpResourceService resourceService;
   @Autowired
   private LcdpResourceHistoryService resourceHistoryService;

   @Override
   public boolean doValid(Object... args) {
      List<LcdpResourceBean> resourceList = ((RestJsonWrapperBean)args[1]).parse(this.resourceService.getDao().getType());
      if (resourceList.isEmpty()) {
         return false;
      } else {
         LcdpResourceBean resource = resourceList.get(0);
         if ("module".equals(resource.getResourceCategory())) {
            Long moduleId = resource.getId();
            List<LcdpResourceBean> childrenList = this.resourceService
               .selectListByFilter(
                  SearchFilter.instance().match("PARENTID", moduleId).filter(MatchPattern.SEQ).match("DELETEFLAG", "0").filter(MatchPattern.EQ), new Order[0]
               );
            if (childrenList.isEmpty()) {
               return true;
            }

            List<Long> childIdList = childrenList.stream().map(LcdpResourceBean::getId).collect(Collectors.toList());
            List<LcdpResourceHistoryBean> childHistoryList = this.resourceHistoryService
               .selectListByFilter(SearchFilter.instance().match("RESOURCEID", childIdList).filter(MatchPattern.OR), new Order[0]);
            List<LcdpResourceHistoryBean> unSubmitList = childHistoryList.stream()
               .filter(child -> "no".equals(child.getSubmitFlag()))
               .collect(Collectors.toList());
            if (!unSubmitList.isEmpty()) {
               this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.RESOURCE_NOT_SUBMITTED"));
               return false;
            }
         } else {
            List<LcdpResourceHistoryBean> childHistoryList = this.resourceHistoryService
               .selectListByFilter(SearchFilter.instance().match("RESOURCEID", resource.getId()).filter(MatchPattern.SEQ), new Order[0]);
            List<LcdpResourceHistoryBean> unSubmitList = childHistoryList.stream()
               .filter(child -> "no".equals(child.getSubmitFlag()))
               .collect(Collectors.toList());
            if (!unSubmitList.isEmpty()) {
               this.addConstraintViolation(I18nHelper.getMessage("LCDP.MODULE.RESOUCES.TIP.RESOURCE_NOT_SUBMITTED"));
               return false;
            }
         }

         return true;
      }
   }
}
