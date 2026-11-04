package com.sunwayworld.cloud.module.script.test.service.impl;

import com.sunwayworld.cloud.module.script.test.bean.CoreItemBean;
import com.sunwayworld.cloud.module.script.test.persistent.dao.CoreItemDao;
import com.sunwayworld.cloud.module.script.test.service.CoreItemService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.formula.bean.CoreFormulaRequiredBean;
import com.sunwayworld.module.sys.formula.service.CoreFormulaRequiredService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreItemServiceImpl implements CoreItemService {
   @Autowired
   private CoreItemDao coreItemDao;
   @Autowired
   private CoreFormulaRequiredService formulaRequiredService;

   public CoreItemDao getDao() {
      return this.coreItemDao;
   }

   @Transactional
   @Audit("AUDIT.INSERT")
   @Override
   public void postUpdate(List<String> updatedColumnNameList, List<Long> idList) {
      if (CollectionUtils.containsIgnoreCase(updatedColumnNameList, "script")) {
         for (CoreItemBean item : this.getDao().selectListByIds(idList, new Order[0])) {
            if (!StringUtils.isBlank(item.getScript())) {
               this.insertFormulaRequired(item);
            }
         }
      }
   }

   private void insertFormulaRequired(CoreItemBean item) {
      CoreFormulaRequiredBean deleteFilter = new CoreFormulaRequiredBean();
      deleteFilter.setItemId("" + item.getId());
      this.formulaRequiredService.getDao().deleteBy(deleteFilter, "ITEMID");
      String script = item.getScript();
      List<CoreItemBean> relatedItemList = this.selectRelatedItemList(item, script);
      if (!relatedItemList.isEmpty()) {
         List<CoreFormulaRequiredBean> insertFormulaRequiredList = new ArrayList<>();
         Pattern pattern = Pattern.compile("\\[[^\\[]*\\]");
         Matcher matcher = pattern.matcher(script);

         while (matcher.find()) {
            int end = matcher.end();
            String matchValue = script.substring(matcher.start() + 1, end - 1);
            String[] params = matchValue.split("\\|");
            String firstParam = params[0];
            if (!StringUtils.isEmpty(firstParam)) {
               if (params.length == 1) {
                  String[] itemCondArray = firstParam.split(";");
                  CoreItemBean filterItem = new CoreItemBean();
                  filterItem.setItemName(itemCondArray[0]);
                  filterItem.setTestId(item.getTestId());
                  List<CoreItemBean> dependencyItemList = relatedItemList.stream()
                     .filter(e -> e.getItemName().equals(filterItem.getItemName()) && e.getTestId() == filterItem.getTestId())
                     .collect(Collectors.toList());
                  this.insertFormulaRequiredByDependencyData(item, dependencyItemList, insertFormulaRequiredList);
               } else {
                  String itemSelector = params[params.length - 1];
                  String[] testCondArray = firstParam.split(";");
                  String testName = testCondArray[0];
                  String[] itemCondArray = itemSelector.split(";");
                  String itemName = itemCondArray[0];
                  List<CoreItemBean> dependencyItemList = relatedItemList.stream()
                     .filter(e -> e.getItemName().equals(itemName) && e.getExt$Item("testname").equals(testName))
                     .collect(Collectors.toList());
                  this.insertFormulaRequiredByDependencyData(item, dependencyItemList, insertFormulaRequiredList);
               }
            }
         }

         this.formulaRequiredService.insert(insertFormulaRequiredList);
      }
   }

   private List<CoreItemBean> selectRelatedItemList(CoreItemBean item, String script) {
      Pattern pattern = Pattern.compile("\\[[^\\[]*\\]");
      List<String> filterItemNameList = new ArrayList<>();
      Matcher matcher = pattern.matcher(script);

      while (matcher.find()) {
         int end = matcher.end();
         String matchValue = script.substring(matcher.start() + 1, end - 1);
         String[] params = matchValue.split("\\|");
         String firstParam = params[0];
         if (!StringUtils.isEmpty(firstParam)) {
            String itemSelector = params[params.length - 1];
            String[] itemCondArray = itemSelector.split(";");
            filterItemNameList.add(itemCondArray[0]);
         }
      }

      return !filterItemNameList.isEmpty()
         ? this.selectListByFilter(SearchFilter.instance().match("ITEMNAME", filterItemNameList).filter(MatchPattern.OR), new Order[0])
         : CollectionUtils.emptyList();
   }

   private void insertFormulaRequiredByDependencyData(
      CoreItemBean item, List<CoreItemBean> dependencyItemList, List<CoreFormulaRequiredBean> insertFormulaRequiredList
   ) {
      for (CoreItemBean dependencyItem : dependencyItemList) {
         CoreFormulaRequiredBean required = new CoreFormulaRequiredBean();
         required.setItemId("" + item.getId());
         required.setRequiredItemId(dependencyItem.getId() + "");
         insertFormulaRequiredList.add(required);
      }
   }
}
