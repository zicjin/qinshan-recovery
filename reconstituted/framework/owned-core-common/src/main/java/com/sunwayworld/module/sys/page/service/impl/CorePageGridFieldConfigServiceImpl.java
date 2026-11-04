package com.sunwayworld.module.sys.page.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.design.studio.bean.CoreStudioGridDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioGridDTO.CoreStudioGridColumnDTO;
import com.sunwayworld.module.sys.page.bean.CorePageGridFieldConfigBean;
import com.sunwayworld.module.sys.page.dao.CorePageGridFieldConfigDao;
import com.sunwayworld.module.sys.page.service.CorePageGridFieldConfigService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CorePageGridFieldConfigServiceImpl implements CorePageGridFieldConfigService {
   @Autowired
   private CorePageGridFieldConfigDao configDao;
   @Lazy
   @Autowired
   private CorePageGridFieldConfigService proxyInstance;

   public CorePageGridFieldConfigDao getDao() {
      return this.configDao;
   }

   @Transactional
   @Override
   public void insertOrUpdateCurrentGridConfig(RestJsonWrapperBean wrapper) {
      if (LocalContextHelper.isUserLogin()) {
         String pageId = wrapper.getParamValue("pageId");
         String gridId = wrapper.getParamValue("gridId");
         List<CorePageGridFieldConfigBean> configList = wrapper.parse(CorePageGridFieldConfigBean.class);
         List<CorePageGridFieldConfigBean> selectedConfigList = this.proxyInstance.selectCurrentGridConfigList(pageId, gridId);
         if (!selectedConfigList.isEmpty()) {
            this.getDao().deleteBy(selectedConfigList, new String[0]);
         }

         List<CorePageGridFieldConfigBean> insertConfigList = configList.stream().filter(c -> "1".equals(c.getFixed())).collect(Collectors.toList());
         insertConfigList.addAll(configList.stream().filter(c -> !"1".equals(c.getFixed())).collect(Collectors.toList()));
         int i = 0;

         for (int j = insertConfigList.size(); i < j; i++) {
            CorePageGridFieldConfigBean config = insertConfigList.get(i);
            config.setId(ApplicationContextHelper.getNextIdentity());
            config.setPageId(pageId);
            config.setGridId(gridId);
            config.setUserId(LocalContextHelper.getLoginUserId());
            config.setOrderNo(ApplicationContextHelper.getNextOrderNo());
         }

         this.getDao().insert(insertConfigList);
      }
   }

   @Override
   public List<CorePageGridFieldConfigBean> selectCurrentGridConfigList(String pageId, String gridId) {
      CorePageGridFieldConfigBean filter = new CorePageGridFieldConfigBean();
      filter.setPageId(pageId);
      filter.setGridId(gridId);
      filter.setUserId(LocalContextHelper.getLoginUserId());
      return this.getDao().selectList(filter, new Order[]{Order.asc("ORDERNO")});
   }

   @Transactional
   @Override
   public void resetPageGridConfig(List<CoreStudioGridDTO> lastPageGridList, List<CoreStudioGridDTO> pageGridList) {
      this.insertPageGridConfig(lastPageGridList, pageGridList);
      this.deletePageGridConfig(lastPageGridList, pageGridList);
   }

   private void insertPageGridConfig(List<CoreStudioGridDTO> lastPageGridList, List<CoreStudioGridDTO> pageGridList) {
      List<CorePageGridFieldConfigBean> insertFieldConfigList = new ArrayList<>();

      for (CoreStudioGridDTO lastPage : lastPageGridList) {
         List<CoreStudioGridColumnDTO> lastColumnList = lastPage.getColumnList();
         CoreStudioGridDTO page = pageGridList.stream()
            .filter(p -> p.getPageId().equals(lastPage.getPageId()) && p.getGridId().equals(lastPage.getGridId()))
            .findFirst()
            .get();
         List<CoreStudioGridColumnDTO> columnList = page.getColumnList();
         columnList.stream()
            .filter(c -> "0".equals(c.getHidden()) && lastColumnList.stream().noneMatch(lc -> lc.getField().equals(c.getField())))
            .forEach(ic -> {
               CorePageGridFieldConfigBean insertField = new CorePageGridFieldConfigBean();
               insertField.setPageId(page.getPageId());
               insertField.setGridId(page.getGridId());
               insertField.setField(ic.getField());
               insertFieldConfigList.add(insertField);
            });
         columnList.stream()
            .filter(c -> "0".equals(c.getHidden()) && lastColumnList.stream().anyMatch(lc -> lc.getField().equals(c.getField()) && "1".equals(lc.getHidden())))
            .forEach(ic -> {
               CorePageGridFieldConfigBean insertField = new CorePageGridFieldConfigBean();
               insertField.setPageId(page.getPageId());
               insertField.setGridId(page.getGridId());
               insertField.setField(ic.getField());
               insertFieldConfigList.add(insertField);
            });
      }

      if (!insertFieldConfigList.isEmpty()) {
         insertFieldConfigList.stream()
            .collect(Collectors.groupingBy(CorePageGridFieldConfigBean::getPageId))
            .forEach(
               (pageId, gridList) -> {
                  Set<String> userIdSet = new HashSet<>();
                  List<String> insertGridIdList = gridList.stream().map(CorePageGridFieldConfigBean::getGridId).collect(Collectors.toList());
                  List<CorePageGridFieldConfigBean> existedGridFieldConfigList = this.selectListByFilter(
                     SearchFilter.instance().match("PAGEID", pageId).filter(MatchPattern.EQ).match("GRIDID", insertGridIdList).filter(MatchPattern.OR),
                     new Order[0]
                  );
                  existedGridFieldConfigList.stream().collect(Collectors.groupingBy(CorePageGridFieldConfigBean::getGridId)).forEach((gridId, fieldList) -> {
                     Set<String> currUserIdSet = fieldList.stream().map(CorePageGridFieldConfigBean::getUserId).distinct().collect(Collectors.toSet());
                     currUserIdSet.stream().forEach(userId -> gridList.stream().forEach(gc -> {
                        gc.setId(ApplicationContextHelper.getNextIdentity());
                        gc.setUserId(userId);
                        gc.setOrderNo(ApplicationContextHelper.getNextOrderNo());
                     }));
                     userIdSet.addAll(currUserIdSet);
                  });
               }
            );
         this.getDao().insert(insertFieldConfigList);
      }
   }

   private void deletePageGridConfig(List<CoreStudioGridDTO> lastPageGridList, List<CoreStudioGridDTO> pageGridList) {
      List<CorePageGridFieldConfigBean> deleteFieldConfigList = new ArrayList<>();

      for (CoreStudioGridDTO lastPage : lastPageGridList) {
         List<CoreStudioGridColumnDTO> lastColumnList = lastPage.getColumnList();
         CoreStudioGridDTO page = pageGridList.stream()
            .filter(p -> p.getPageId().equals(lastPage.getPageId()) && p.getGridId().equals(lastPage.getGridId()))
            .findFirst()
            .get();
         List<CoreStudioGridColumnDTO> columnList = page.getColumnList();
         lastColumnList.stream().filter(lc -> columnList.stream().noneMatch(c -> c.getField().equals(lc.getField()))).forEach(dc -> {
            CorePageGridFieldConfigBean deleteField = new CorePageGridFieldConfigBean();
            deleteField.setPageId(lastPage.getPageId());
            deleteField.setGridId(lastPage.getGridId());
            deleteField.setField(dc.getField());
            deleteFieldConfigList.add(deleteField);
         });
         lastColumnList.stream()
            .filter(lc -> "0".equals(lc.getHidden()) && columnList.stream().anyMatch(c -> c.getField().equals(lc.getField()) && "1".equals(c.getHidden())))
            .forEach(dc -> {
               CorePageGridFieldConfigBean deleteField = new CorePageGridFieldConfigBean();
               deleteField.setPageId(lastPage.getPageId());
               deleteField.setGridId(lastPage.getGridId());
               deleteField.setField(dc.getField());
               deleteFieldConfigList.add(deleteField);
            });
      }

      if (!deleteFieldConfigList.isEmpty()) {
         deleteFieldConfigList.stream()
            .collect(Collectors.groupingBy(CorePageGridFieldConfigBean::getPageId))
            .forEach(
               (pageId, gridList) -> gridList.stream()
                  .collect(Collectors.groupingBy(CorePageGridFieldConfigBean::getGridId))
                  .forEach(
                     (gridId, fieldList) -> {
                        List<String> deleteFieldList = fieldList.stream().map(CorePageGridFieldConfigBean::getField).collect(Collectors.toList());
                        List<CorePageGridFieldConfigBean> deleteGridFieldConfigList = this.selectListByFilter(
                           SearchFilter.instance()
                              .match("PAGEID", pageId)
                              .filter(MatchPattern.EQ)
                              .match("GRIDID", gridId)
                              .filter(MatchPattern.EQ)
                              .match("FIELD", deleteFieldList)
                              .filter(MatchPattern.OR),
                           new Order[0]
                        );
                        if (!deleteGridFieldConfigList.isEmpty()) {
                           this.getDao().deleteBy(deleteGridFieldConfigList, new String[0]);
                        }
                     }
                  )
            );
      }
   }
}
