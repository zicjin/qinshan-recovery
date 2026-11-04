package com.sunwayworld.cloud.module.eln.service.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnFieldBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnFieldDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnFieldService;
import com.sunwayworld.cloud.module.eln.service.CoreElnService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreElnFieldServiceImpl implements CoreElnFieldService {
   @Autowired
   private CoreElnFieldDao coreElnFieldDao;
   @Lazy
   @Autowired
   private CoreElnService coreElnService;

   public CoreElnFieldDao getDao() {
      return this.coreElnFieldDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void writeBack(String table, String bizId) {
      List<CoreElnBean> selectedElnList = this.coreElnService
         .selectListByFilter(
            SearchFilter.instance().match("BIZCATEGORY", table).filter(MatchPattern.EQ).match("BIZID", bizId).filter(MatchPattern.EQ),
            new Order[]{Order.desc("ID")}
         );
      if (selectedElnList.size() == 1) {
         CoreElnBean eln = selectedElnList.get(0);
         List<CoreElnFieldBean> selectedFieldValueList = this.selectListByFilter(
            SearchFilter.instance().match("ELNID", eln.getId()).filter(MatchPattern.EQ), new Order[]{Order.desc("ID")}
         );
         List<String> tableList = selectedFieldValueList.stream().map(CoreElnFieldBean::getTableName).distinct().collect(Collectors.toList());
         tableList.forEach(
            tableName -> {
               List<CoreElnFieldBean> filterFieldList = selectedFieldValueList.stream()
                  .filter(fieldValue -> tableName.equals(fieldValue.getTableName()))
                  .collect(Collectors.toList());
               List<Object> idList = filterFieldList.stream().map(CoreElnFieldBean::getBizId).distinct().collect(Collectors.toList());
               Set<String> updateFieldList = new HashSet<>();
               GenericService<Persistable<?>, ?> service = ApplicationContextHelper.getServiceByTable(tableName.toUpperCase());
               List<Persistable<?>> updateList = new ArrayList<>();
               idList.forEach(
                  id -> {
                     List<CoreElnFieldBean> sameIdFieldValueList = filterFieldList.stream()
                        .filter(fieldValue -> id.equals(fieldValue.getBizId()))
                        .collect(Collectors.toList());

                     try {
                        Map<String, Object> map = new HashMap<>();
                        sameIdFieldValueList.stream().forEach(v -> map.put(v.getColumnName(), v.getVal()));
                        map.put("id", id);
                        Persistable<?> row = ClassUtils.newInstance(service.getDao().getType());
                        BeanUtils.copyProperties(map, row);
                        sameIdFieldValueList.forEach(ex -> updateFieldList.add(ex.getColumnName()));
                        updateList.add(row);
                     } catch (Exception var8) {
                        throw new UnsupportedException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.TIP.UNKNOWN_CLASS"));
                     }
                  }
               );
               service.getDao().update(updateList, updateFieldList.toArray(new String[updateFieldList.size()]));
            }
         );
      }
   }
}
