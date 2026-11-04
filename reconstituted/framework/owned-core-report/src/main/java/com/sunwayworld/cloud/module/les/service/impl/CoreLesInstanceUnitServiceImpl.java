package com.sunwayworld.cloud.module.les.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.les.bean.CoreLesInstanceUnitBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesInstanceUnitDao;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceService;
import com.sunwayworld.cloud.module.les.service.CoreLesInstanceUnitService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLesInstanceUnitServiceImpl implements CoreLesInstanceUnitService {
   @Autowired
   private CoreLesInstanceUnitDao unitDao;
   @Autowired
   private CoreLesInstanceService coreLesInstanceService;

   public CoreLesInstanceUnitDao getDao() {
      return this.unitDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void dedentSection(Long unitId, RestJsonWrapperBean wrapper) {
      CoreLesInstanceUnitBean unit = this.getDao().selectById(unitId);
      if (unit.getParentId() != null) {
         Long parentId = unit.getParentId();
         CoreLesInstanceUnitBean parentUnit = this.getDao().selectById(unit.getParentId());
         unit.setParentId(parentUnit.getParentId());
         unit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
         this.getDao().update(unit, new String[]{"PARENTID", "ORDERNO"});
         List<CoreLesInstanceUnitBean> itemList = wrapper.parse(CoreLesInstanceUnitBean.class);
         if (!itemList.isEmpty()) {
            List<CoreLesInstanceUnitBean> updatableUnitList = this.getDao()
               .selectListByIds(itemList.stream().map(e -> e.getId()).collect(Collectors.toList()), new Order[0]);
            List<CoreLesInstanceUnitBean> updateUnitList = updatableUnitList.stream()
               .filter(e -> !parentId.equals(e.getParentId()))
               .collect(Collectors.toList());
            updateUnitList.forEach(e -> e.setOrderNo(ApplicationContextHelper.getNextOrderNo()));
            this.getDao().update(updateUnitList, new String[]{"ORDERNO"});
         }
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void indentSection(Long unitId, Long parentId, RestJsonWrapperBean wrapper) {
      CoreLesInstanceUnitBean unit = this.getDao().selectById(unitId);
      unit.setParentId(parentId);
      unit.setOrderNo(ApplicationContextHelper.getNextOrderNo());
      this.getDao().update(unit, new String[]{"PARENTID", "ORDERNO"});
      List<CoreLesInstanceUnitBean> itemList = wrapper.parse(CoreLesInstanceUnitBean.class);
      if (!itemList.isEmpty()) {
         itemList.forEach(e -> e.setOrderNo(ApplicationContextHelper.getNextOrderNo()));
         this.getDao().update(itemList, new String[]{"ORDERNO"});
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void refreshUnit(Long unitId, RestJsonWrapperBean wrapper) {
      CoreLesInstanceUnitBean unit = this.getDao().selectById(unitId);
      this.refreshUnit(Arrays.asList(unit));
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void refreshUnit(List<CoreLesInstanceUnitBean> unitList) {
      List<CoreLesInstanceUnitBean> unitList4Update = new ArrayList<>();
      List<CoreLesInstanceUnitBean> ckeditorUnitList = unitList.stream().filter(f -> "ckeditor".equals(f.getUnitCategory())).collect(Collectors.toList());
      if (!ckeditorUnitList.isEmpty()) {
         this.refreshCkeditorUnit(ckeditorUnitList);
         unitList4Update.addAll(ckeditorUnitList);
      }

      List<CoreLesInstanceUnitBean> formOrQueryListUnitList = unitList.stream()
         .filter(f -> "formOrQueryList".equals(f.getUnitCategory()))
         .collect(Collectors.toList());
      if (!formOrQueryListUnitList.isEmpty()) {
         this.refreshFormOrQueryListUnit(formOrQueryListUnitList);
         unitList4Update.addAll(formOrQueryListUnitList);
      }

      if (!unitList4Update.isEmpty()) {
         this.getDao().updateIfChanged(unitList4Update);
      }
   }

   protected void refreshFormOrQueryListUnit(List<CoreLesInstanceUnitBean> formOrQueryListUnitList) {
      formOrQueryListUnitList.forEach(e -> {
         if (!StringUtils.isBlank(e.getUnitBody()) && !StringUtils.isBlank(e.getConfig())) {
            JSONObject unitBodyJson = (JSONObject)JSONObject.parse(e.getUnitBody());
            if (unitBodyJson != null) {
               JSONObject configJson = (JSONObject)JSONObject.parse(e.getConfig());
               if (configJson != null) {
                  if (configJson.getJSONObject("dsConfig") != null) {
                     Long unitLesDsId = configJson.getJSONObject("dsConfig").getLong("lesDsId");
                     Map<String, String> unitParamMap = null;
                     String unitParamStr = e.getUnitParam();
                     if (!StringUtils.isBlank(unitParamStr)) {
                        unitParamMap = JSONObject.parseObject(unitParamStr, Map.class);
                     }

                     List<Map<String, Object>> resultMapList = this.coreLesInstanceService.getDataByDs(unitLesDsId, unitParamMap);
                     JSONArray dataArray = new JSONArray();
                     resultMapList.forEach(m -> dataArray.add(m));
                     unitBodyJson.put("data", dataArray);
                     e.setUnitBody(unitBodyJson.toJSONString());
                  }
               }
            }
         }
      });
   }

   protected void refreshCkeditorUnit(List<CoreLesInstanceUnitBean> ckeditorUnitList) {
      ckeditorUnitList.forEach(e -> {
         if (!StringUtils.isBlank(e.getUnitBody())) {
            JSONObject unitBodyJson = (JSONObject)JSONObject.parse(e.getUnitBody());
            String content = unitBodyJson.getString("content");
            if (!StringUtils.isEmpty(content)) {
               if (StringUtils.contains(content, "lesdsid")) {
                  Map<String, String> unitParamMap = null;
                  String unitParamStr = e.getUnitParam();
                  if (!StringUtils.isBlank(unitParamStr)) {
                     unitParamMap = JSONObject.parseObject(unitParamStr, Map.class);
                  }

                  Document doc = Jsoup.parse(content);

                  for (Element element : doc.getElementsByAttribute("lesDsId")) {
                     Long lesDsId = NumberUtils.parseLong(element.attr("lesDsId"));
                     String bindDsField = element.attr("bindDsField");
                     List<Map<String, Object>> resultMapList = this.coreLesInstanceService.getDataByDs(lesDsId, unitParamMap);
                     Map<String, Object> beanMap = resultMapList.get(0);
                     if (beanMap != null) {
                        element.text(ObjectUtils.isEmpty(beanMap.get(bindDsField.toUpperCase())) ? "" : beanMap.get(bindDsField.toUpperCase()).toString());
                     }
                  }

                  unitBodyJson.put("content", doc.body().html());
                  e.setUnitBody(JSON.toJSONString(unitBodyJson));
               }
            }
         }
      });
   }
}
