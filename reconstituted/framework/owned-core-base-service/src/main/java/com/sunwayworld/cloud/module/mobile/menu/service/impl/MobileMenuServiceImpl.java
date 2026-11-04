package com.sunwayworld.cloud.module.mobile.menu.service.impl;

import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuBean;
import com.sunwayworld.cloud.module.mobile.menu.bean.MobileMenuIcon;
import com.sunwayworld.cloud.module.mobile.menu.persistent.dao.MobileMenuDao;
import com.sunwayworld.cloud.module.mobile.menu.service.MobileMenuService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class MobileMenuServiceImpl implements MobileMenuService {
   @Autowired
   private MobileMenuDao mobileMenuDao;

   public MobileMenuDao getDao() {
      return this.mobileMenuDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      MobileMenuBean menuBean = jsonWrapper.parseUnique(MobileMenuBean.class);
      menuBean.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(menuBean);
      return menuBean.getId();
   }

   @Override
   public ArrayList<MobileMenuIcon> selectIconList() {
      ArrayList<Resource> iconResourceList = (ArrayList<Resource>)ClassUtils.getResourceList("/static/icon/todo/");
      return iconResourceList.stream().map(i -> {
         MobileMenuIcon icon = new MobileMenuIcon();
         icon.setIconName(i.getFilename());
         icon.setIconUrl("/static/icon/todo/" + i.getFilename());
         return icon;
      }).collect(Collectors.toCollection(ArrayList::new));
   }

   public MobileMenuBean selectDetail(Long id) {
      Map<String, Object> map = this.getDao().selectDetail(id);
      if (map != null && !map.isEmpty()) {
         MobileMenuBean menuBean = PersistableHelper.mapToPersistable(map, MobileMenuBean.class);
         menuBean.setExt$Item("menuIconUrl", this.getMenuIconUrl(menuBean.getIcon()));
         return menuBean;
      } else {
         return null;
      }
   }

   private String getMenuIconUrl(String menuIcon) {
      return StringUtils.isEmpty(menuIcon) ? null : "/static/icon/todo/" + menuIcon;
   }
}
