package com.sunwayworld.module.sys.menu.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.PageRequest;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.menu.bean.CoreMenuAccessLogBean;
import com.sunwayworld.module.sys.menu.dao.CoreMenuAccessLogDao;
import com.sunwayworld.module.sys.menu.service.CoreMenuAccessLogService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreMenuAccessLogServiceImpl implements CoreMenuAccessLogService {
   @Autowired
   private CoreMenuAccessLogDao menuAccessLogDao;
   @Value("${sunway.shortcut-menu.auto.enabled:false}")
   private boolean shortcutMenuAuto;
   @Value("${sunway.shortcut-menu.auto.qty:5}")
   private int shortcutMenuAutoQty;

   public CoreMenuAccessLogDao getDao() {
      return this.menuAccessLogDao;
   }

   @Override
   public void insertMenuAccessLog(String userId, String menuId, Long accessTimes) {
      GikamConcurrentLocker.blockRun(this.getBlockKey(userId, menuId), () -> {
         CoreMenuAccessLogBean filter = new CoreMenuAccessLogBean();
         filter.setUserId(userId);
         filter.setMenuId(menuId);
         CoreMenuAccessLogBean accessLog = this.getDao().selectFirstIfPresent(filter, new Order[0]);
         if (accessLog == null) {
            accessLog = new CoreMenuAccessLogBean();
            accessLog.setId(ApplicationContextHelper.getNextIdentity());
            accessLog.setUserId(userId);
            accessLog.setMenuId(menuId);
            accessLog.setAccessTimes(accessTimes);
            this.getDao().insert(accessLog);
         } else {
            accessLog.setAccessTimes(accessLog.getAccessTimes() + accessTimes);
            this.getDao().update(accessLog, new String[]{"ACCESSTIMES"});
         }
      });
   }

   @Override
   public List<String> selectHighPriorityMenuIdList() {
      if (!this.shortcutMenuAuto) {
         return CollectionUtils.emptyList();
      } else {
         MapperParameter parameter = new MapperParameter();
         parameter.setFilter(SearchFilter.instance().match("USERID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ));
         parameter.setOrderParam("ACCESSTIMES", "DESC");
         PageRequest pageRequest = new PageRequest();
         pageRequest.setPageNumber(1);
         pageRequest.setPageSize(this.shortcutMenuAutoQty);
         Page<CoreMenuAccessLogBean> accessLogList = this.selectPagination(parameter, new PageRowBounds(pageRequest));
         return accessLogList.getRows().isEmpty()
            ? CollectionUtils.emptyList()
            : accessLogList.getRows().stream().map(CoreMenuAccessLogBean::getMenuId).collect(Collectors.toList());
      }
   }

   private String getBlockKey(String userId, String menuId) {
      return "MENU_ACCESS_LOG$" + userId + "$" + menuId;
   }
}
