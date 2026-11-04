package com.sunwayworld.cloud.module.item.pvconf.service.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewGridConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewGridConfDao;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewGridConfService;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewMasterConfService;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewSubConfService;
import com.sunwayworld.cloud.module.item.support.helper.CoreQueryConfigHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreProcessViewGridConfServiceImpl implements CoreProcessViewGridConfService {
   @Autowired
   private CoreProcessViewMasterConfService processViewMasterConfService;
   @Autowired
   private CoreProcessViewSubConfService processViewSubConfService;
   @Autowired
   private CoreProcessViewGridConfDao processViewGridConfDao;

   public CoreProcessViewGridConfDao getDao() {
      return this.processViewGridConfDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      String gridSql = wrapper.getParamValue("gridSql");
      String sourceId = wrapper.getParamValue("sourceId");
      String sourceType = wrapper.getParamValue("sourceType");
      if ("master".equals(sourceType)) {
         CoreProcessViewMasterConfBean masterConf = this.processViewMasterConfService.selectDetail(NumberUtils.parseLong(sourceId));
         masterConf.setGridSql(gridSql);
         this.processViewMasterConfService.getDao().update(masterConf, "GRIDSQL");
      } else if ("sub".equals(sourceType)) {
         CoreProcessViewSubConfBean subConf = this.processViewSubConfService.selectDetail(NumberUtils.parseLong(sourceId));
         subConf.setGridSql(gridSql);
         this.processViewSubConfService.getDao().update(subConf, "GRIDSQL");
      }

      if (StringUtils.isEmpty(gridSql)) {
         CoreProcessViewGridConfBean filter = new CoreProcessViewGridConfBean();
         filter.setSourceType(sourceType);
         filter.setSourceId(NumberUtils.parseLong(sourceId));
         List<CoreProcessViewGridConfBean> deleteGridConfList = this.getDao().selectList(filter, new Order[0]);
         if (!deleteGridConfList.isEmpty()) {
            this.getDao().deleteBy(filter, new String[0]);
         }
      } else {
         CoreProcessViewGridConfBean filter = new CoreProcessViewGridConfBean();
         filter.setSourceType(sourceType);
         filter.setSourceId(NumberUtils.parseLong(sourceId));
         List<CoreProcessViewGridConfBean> gridConfList = this.selectList(filter, new Order[0]);
         gridSql = gridSql.replaceAll("\\#\\{.*\\}", "0");
         if (StringUtils.contains(gridSql, "${loginUserId}")) {
            gridSql = StringUtils.replace(gridSql, "${loginUserId}", LocalContextHelper.getLoginUserId());
         }

         if (StringUtils.contains(gridSql, "${loginOrgId}")) {
            gridSql = StringUtils.replace(gridSql, "${loginOrgId}", LocalContextHelper.getLoginOrgId());
         }

         if (StringUtils.contains(gridSql, "${loginRoleId}")) {
            gridSql = StringUtils.replace(gridSql, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
         }

         List<String> columnIdList = SqlHelper.getSqlColumnList(gridSql);
         columnIdList.removeIf(c -> CoreQueryConfigHelper.IGNORE_COLUMN_LIST.contains(c));
         List<CoreProcessViewGridConfBean> deleteGridConfList = gridConfList.stream()
            .filter(c -> columnIdList.stream().noneMatch(i -> i.equalsIgnoreCase(c.getField())))
            .collect(Collectors.toList());
         if (!deleteGridConfList.isEmpty()) {
            this.getDao().deleteBy(deleteGridConfList, new String[0]);
         }

         List<String> insertColumnIdList = columnIdList.stream()
            .filter(i -> gridConfList.stream().noneMatch(c -> i.equalsIgnoreCase(c.getField())))
            .collect(Collectors.toList());
         if (!insertColumnIdList.isEmpty()) {
            List<CoreProcessViewGridConfBean> insertGridConfList = new ArrayList<>();

            for (String columnId : insertColumnIdList) {
               CoreProcessViewGridConfBean gridConf = new CoreProcessViewGridConfBean();
               gridConf.setId(ApplicationContextHelper.getNextIdentity());
               gridConf.setSourceType(sourceType);
               gridConf.setSourceId(NumberUtils.parseLong(sourceId));
               gridConf.setField(columnId);
               gridConf.setWidth(NumberUtils.parseLong(ApplicationContextHelper.getConstantValue("PROCESS_VIEW_DEFAULT_COLWIDTH")));
               gridConf.setActivatedFlag("1");
               insertGridConfList.add(gridConf);
            }

            this.getDao().insert(insertGridConfList);
         }
      }

      return null;
   }
}
