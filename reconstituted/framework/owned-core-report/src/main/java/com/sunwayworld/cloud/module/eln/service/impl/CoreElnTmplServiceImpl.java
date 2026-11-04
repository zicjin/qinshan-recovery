package com.sunwayworld.cloud.module.eln.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionDTO;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnTmplDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsFieldService;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplDsService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplVersionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.GzipUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreElnTmplServiceImpl implements CoreElnTmplService {
   private static Logger logger = LogManager.getLogger(CoreElnTmplService.class);
   @Autowired
   private CoreElnTmplDao coreElnTmplDao;
   @Autowired
   private CoreElnTmplDsService coreElnTmplDsService;
   @Autowired
   private CoreElnDsService coreElnDsService;
   @Autowired
   private CoreElnDsFieldService coreElnDsFieldService;
   @Autowired
   private CoreElnTmplVersionService coreElnTmplVersionService;
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private CoreFileService fileService;
   @Autowired
   @Lazy
   private GlobalMapper globalMapper;

   public CoreElnTmplDao getDao() {
      return this.coreElnTmplDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void updateIfChanged(List<CoreElnTmplBean> itemList) {
      this.getDao().updateIfChanged(itemList);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreElnTmplBean elnTmpl = wrapper.parseUnique(CoreElnTmplBean.class);
      elnTmpl.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(elnTmpl);
      return elnTmpl.getId();
   }

   @Override
   public CoreElnTmplVersionDTO selectElnDsDTO(Long versionId, RestJsonWrapperBean wrapper) {
      Map<String, Object> versionMap = this.coreElnTmplVersionService.getDao().selectDetail(versionId);
      if (ObjectUtils.isEmpty(versionMap)) {
         return new CoreElnTmplVersionDTO();
      } else {
         CoreElnTmplVersionDTO dto = PersistableHelper.mapToPersistable(versionMap, CoreElnTmplVersionDTO.class);
         dto.setElnTmplContent(this.getContent(dto.getElnTmplFileId(), dto.getElnTmplContent()));
         List<CoreElnTmplDsBean> privateDsList = this.coreElnTmplDsService
            .selectListByFilter(SearchFilter.instance().match("ELNTMPLID", dto.getElnTmplId()).filter(MatchPattern.SEQ), new Order[]{Order.desc("ID")});
         List<CoreElnDsBean> publicDsList = this.coreElnDsService
            .selectListByFilter(
               SearchFilter.instance().match("SCOPE", "public").filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
               new Order[]{Order.desc("ID")}
            );
         List<CoreElnDsBean> dsList = new ArrayList<>();
         if (!privateDsList.isEmpty()) {
            List<CoreElnDsBean> selectPrivateDsList = this.coreElnDsService
               .selectListByIds(privateDsList.stream().map(e -> e.getElnDsId()).distinct().collect(Collectors.toList()));
            dsList.addAll(selectPrivateDsList);
         }

         if (!publicDsList.isEmpty()) {
            dsList.addAll(publicDsList);
         }

         if (!dsList.isEmpty()) {
            List<String> elnParamList = new ArrayList<>();
            List<CoreElnDsFieldBean> paramDsFieldList = dsList.stream().map(e -> {
               CoreElnDsFieldBean field = new CoreElnDsFieldBean();
               field.setElnDsId(e.getId());
               return field;
            }).collect(Collectors.toList());
            List<CoreElnDsFieldBean> selectDsFieldList = this.coreElnDsFieldService
               .getDao()
               .selectList(paramDsFieldList, Arrays.asList("ELNDSID"), Collections.emptyList(), Order.asc("ID"));
            Map<String, Object> dataSourceMap = new HashMap<>();
            List<String> dataSourceDetailList = new ArrayList<>();
            List<String> dataSourceList = new ArrayList<>();
            List<String> dataSourceDropDownList = new ArrayList<>();
            Map<String, List<String>> dataColumnBean = new HashMap<>();
            dsList.forEach(
               ds -> {
                  List<CoreElnDsFieldBean> dsFieldList = selectDsFieldList.stream()
                     .filter(field -> ds.getId().equals(field.getElnDsId()))
                     .collect(Collectors.toList());
                  List<String> pivotElnShowFieldList = new ArrayList<>();
                  if ("1".equals(ds.getPivotCategory())) {
                     dsFieldList.forEach(e -> {
                        if (!"value".equals(e.getPivotColumnCategory()) && !"column".equals(e.getPivotColumnCategory())) {
                           pivotElnShowFieldList.add(e.getElnColumnName());
                        }
                     });
                     if (!pivotElnShowFieldList.isEmpty()) {
                        pivotElnShowFieldList.add("...");
                        dataColumnBean.put(ds.getElnDsName(), pivotElnShowFieldList);
                     }
                  } else {
                     List<String> elnShowFieldList = selectDsFieldList.stream()
                        .filter(field -> ds.getId().equals(field.getElnDsId()))
                        .map(field -> field.getElnColumnName())
                        .collect(Collectors.toList());
                     if (!elnShowFieldList.isEmpty() && !"dropDown".equals(ds.getDisplayCategory())) {
                        dataColumnBean.put(ds.getElnDsName(), elnShowFieldList);
                     }
                  }

                  if ("list".equals(ds.getDisplayCategory())) {
                     dataSourceList.add(ds.getElnDsName());
                  } else if ("detail".equals(ds.getDisplayCategory())) {
                     dataSourceDetailList.add(ds.getElnDsName());
                  } else if ("dropDown".equals(ds.getDisplayCategory())) {
                     dataSourceDropDownList.add(ds.getElnDsName());
                  }

                  if (!StringUtils.isEmpty(ds.getSqlClause())) {
                     elnParamList.addAll(Arrays.asList(StringUtils.split(ds.getSqlClause(), "\\,")));
                  }
               }
            );
            dataSourceMap.put("$schema", "http://json-schema.org/draft-04/schema#");
            Map<String, Map<String, Object>> dataSourcePropertiesMap = new HashMap<>();
            if (!dataSourceDetailList.isEmpty()) {
               this.splitJointJson(
                  I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DETAIL"), dataSourcePropertiesMap, dataSourceDetailList, dataColumnBean
               );
            }

            if (!dataSourceList.isEmpty()) {
               this.splitJointJson(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.TABLE"), dataSourcePropertiesMap, dataSourceList, dataColumnBean);
            }

            if (!dataSourceDropDownList.isEmpty()) {
               this.splitJointJson(
                  I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DROP_DOWN"), dataSourcePropertiesMap, dataSourceDropDownList, dataColumnBean
               );
            }

            dataSourceMap.put("properties", dataSourcePropertiesMap);
            dataSourceMap.put("type", "object");
            dto.setDataSource(JSON.toJSONString(dataSourceMap, SerializerFeature.WriteDateUseDateFormat));
            if (!elnParamList.isEmpty()) {
               dto.setElnParamList(elnParamList.stream().distinct().collect(Collectors.toList()));
            }
         }

         return dto;
      }
   }

   @Override
   public CoreElnDTO selectElnTestDTO(Long id, Long versionId, RestJsonWrapperBean wrapper) {
      Map<String, Object> versionMap = this.coreElnTmplVersionService.getDao().selectDetail(versionId);
      if (ObjectUtils.isEmpty(versionMap)) {
         return new CoreElnDTO();
      } else {
         CoreElnTmplVersionDTO versionDto = PersistableHelper.mapToPersistable(versionMap, CoreElnTmplVersionDTO.class);
         CoreElnDTO dto = new CoreElnDTO();
         dto.setElnContent(this.getContent(versionDto.getElnTmplFileId(), versionDto.getElnTmplContent()));
         MapperParameter elnParamMap = wrapper.extractMapFilter();
         JSONObject elnParamJson = new JSONObject(elnParamMap);
         new HashMap();
         Map mappingDataMap;
         if ("1".equals("" + elnParamMap.get("previewType"))) {
            mappingDataMap = this.getDataByDs(versionDto.getElnTmplId(), elnParamJson.toJSONString());
         } else {
            mappingDataMap = this.getDataByDs(versionDto.getElnTmplId(), versionDto.getElnTmplParam());
         }

         dto.setData((String)mappingDataMap.get("value"));
         dto.setIdData((String)mappingDataMap.get("id"));
         String elnContent = dto.getElnContent();

         try {
            elnContent = GzipUtils.compress(elnContent);
         } catch (IOException var12) {
            logger.info("ELN JSON字符串压缩失败，" + var12.getMessage(), (Throwable)var12);
         }

         dto.setElnContent(elnContent);
         return dto;
      }
   }

   @Override
   public Map<String, String> getDataByDs(Long tmplId, String jsonParam) {
      Map<String, String> mappingDataMap = new HashMap<>();
      mappingDataMap.put("id", null);
      mappingDataMap.put("value", null);
      List<CoreElnTmplDsBean> privateDsList = this.coreElnTmplDsService
         .selectListByFilter(SearchFilter.instance().match("ELNTMPLID", tmplId).filter(MatchPattern.SEQ), new Order[]{Order.desc("ID")});
      List<CoreElnDsBean> publicDsList = this.coreElnDsService
         .selectListByFilter(
            SearchFilter.instance().match("SCOPE", "public").filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
            new Order[]{Order.desc("ID")}
         );
      List<CoreElnDsBean> dsList = new ArrayList<>();
      if (!privateDsList.isEmpty()) {
         List<CoreElnDsBean> selectPrivateDsList = this.coreElnDsService
            .selectListByIds(privateDsList.stream().map(e -> e.getElnDsId()).distinct().collect(Collectors.toList()));
         dsList.addAll(selectPrivateDsList);
      }

      if (!publicDsList.isEmpty()) {
         dsList.addAll(publicDsList);
      }

      List<CoreElnDsFieldBean> selectedDsFieldList = this.coreElnDsFieldService.getDao().selectList(dsList.stream().map(ds -> {
         CoreElnDsFieldBean dsFieldSearch = new CoreElnDsFieldBean();
         dsFieldSearch.setElnDsId(ds.getId());
         return dsFieldSearch;
      }).collect(Collectors.toList()), Arrays.asList("ELNDSID"), Collections.emptyList(), Order.desc("ID"));
      if (!dsList.isEmpty()) {
         Map<String, List<Map<String, Object>>> dataTableMap = new HashMap<>();
         Map<String, List<List<Map<String, Object>>>> dataKeyValueTableMap = new HashMap<>();
         Map<String, Map<String, Object>> dataFormMap = new HashMap<>();
         Map<String, List<Map<String, Object>>> dropDownDataMap = new HashMap<>();
         Map<String, List<Map<String, Object>>> dataTableIdMap = new HashMap<>();
         Map<String, Map<String, Object>> dataFormIdMap = new HashMap<>();
         dsList.forEach(
            ds -> {
               String sql = ds.getSqlContent();
               Map<String, String> elnParamMap = !StringUtils.isEmpty(jsonParam)
                  ? JSONObject.parseObject(jsonParam, CaseInsensitiveMap.class)
                  : Collections.emptyMap();
               if (!StringUtils.isEmpty(sql)) {
                  if (StringUtils.containsIgnoreCase(sql, " in ")) {
                     Map<String, String> paramMap = new CaseInsensitiveLinkedMap<>(elnParamMap);
                     sql = StringUtils.replaceBetween(sql, "#", "#", paramMap);
                  } else if (ds.getSqlClause() != null) {
                     String[] sqlClauseArray = StringUtils.split(ds.getSqlClause(), ",");

                     for (String sqlClause : sqlClauseArray) {
                        if (!StringUtils.isEmpty(sqlClause)) {
                           if (ObjectUtils.isEmpty(elnParamMap.get(sqlClause))) {
                              throw new InvalidDataException(
                                 I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.TIP.SQL_CLAUSE_MISSING") + ds.getElnDsName() + "\n" + sqlClause
                              );
                           }

                           sql = StringUtils.replaceIgnoreCase(sql, "#" + sqlClause + "#", "'" + elnParamMap.get(sqlClause) + "'");
                        }
                     }
                  }

                  if (StringUtils.substringsBetween(sql, "#", "#").size() > 0) {
                     throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.TIP.SQL_CLAUSE_MISSING") + ds.getElnDsName());
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginUser.id}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.id}", "'" + LocalContextHelper.getLoginUserId() + "'");
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginUser.orgId}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginUser.orgId}", "'" + LocalContextHelper.getLoginUser().getOrgId() + "'");
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginOrgId}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginOrgId}", "'" + LocalContextHelper.getLoginOrgId() + "'");
                  }

                  if (StringUtils.containsIgnoreCase(sql, "{loginRoleId}")) {
                     sql = StringUtils.replaceIgnoreCase(sql, "{loginRoleId}", "" + LocalContextHelper.getLoginRoleId());
                  }

                  List<Map<String, Object>> resultMapList = null;

                  try {
                     logger.info("数据源" + ds.getElnDsName() + "的SQL:\n" + sql);
                     resultMapList = this.globalMapper.selectList(sql);
                  } catch (Exception var26) {
                     throw new InvalidDataException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.TIP.SQL_ERROR") + ds.getElnDsName());
                  }

                  List<Map<String, Object>> dataObjectList = new ArrayList<>();
                  List<Map<String, Object>> dataObjectIdList = new ArrayList<>();
                  if (resultMapList != null && !resultMapList.isEmpty()) {
                     if ("list".equals(ds.getDisplayCategory())) {
                        List<CoreElnDsFieldBean> filterDsFieldList = selectedDsFieldList.stream()
                           .filter(dsField -> ds.getId().equals(dsField.getElnDsId()))
                           .collect(Collectors.toList());
                        List<CoreElnDsFieldBean> groupFieldList = filterDsFieldList.stream()
                           .filter(e -> "group".equals(e.getPivotColumnCategory()))
                           .collect(Collectors.toList());
                        CoreElnDsFieldBean valueField = filterDsFieldList.stream()
                           .filter(e -> "value".equals(e.getPivotColumnCategory()))
                           .findAny()
                           .orElse(null);
                        CoreElnDsFieldBean columnField = filterDsFieldList.stream()
                           .filter(e -> "column".equals(e.getPivotColumnCategory()))
                           .findAny()
                           .orElse(null);
                        boolean pivot = "1".equals(ds.getPivotCategory()) && groupFieldList.size() > 0 && valueField != null && columnField != null;

                        for (Map<String, Object> beanMap : resultMapList) {
                           if (beanMap != null) {
                              List<String> idFieldList = beanMap.keySet().stream().filter(e -> e.toUpperCase().contains("_ID")).collect(Collectors.toList());
                              if (!idFieldList.isEmpty()) {
                                 Map<String, Object> valueMap = new HashMap<>();
                                 Map<String, Object> idValueMap = new HashMap<>();
                                 this.analysisIdAndValue(
                                    beanMap,
                                    idFieldList,
                                    valueMap,
                                    idValueMap,
                                    filterDsFieldList,
                                    ds.getSqlContent(),
                                    pivot ? groupFieldList : CollectionUtils.emptyList()
                                 );
                                 dataObjectList.add(valueMap);
                                 dataObjectIdList.add(idValueMap);
                              } else {
                                 dataObjectList.add(beanMap);
                              }
                           }
                        }

                        dataTableMap.put(ds.getElnDsName(), dataObjectList);
                        List<List<Map<String, Object>>> dataObjectKeyValList = this.convertMapToKeyAndValueMap(dataObjectList);
                        dataKeyValueTableMap.put(ds.getElnDsName(), dataObjectKeyValList);
                        dataTableIdMap.put(ds.getElnDsName(), dataObjectIdList);
                        if (pivot) {
                           Map<Object, List<Map<String, Object>>> sameGroupMap = dataObjectList.stream()
                              .collect(Collectors.groupingBy(e -> getGroupByKey(e, groupFieldList), LinkedHashMap::new, Collectors.toList()));
                           Map<Object, List<Map<String, Object>>> sameGroupIdMap = dataObjectIdList.stream()
                              .collect(Collectors.groupingBy(e -> e.get("_pivot_mark"), LinkedHashMap::new, Collectors.toList()));
                           List<Map<String, Object>> pivotDataObjectList = new ArrayList<>();
                           List<Map<String, Object>> pivotDataObjectIdList = new ArrayList<>();
                           sameGroupMap.forEach((k, v) -> {
                              Map<String, Object> rowMap = new LinkedHashMap<>();
                              Map<String, Object> rowIdMap = new LinkedHashMap<>();
                              List<Map<String, Object>> rowIdList = sameGroupIdMap.get(k);

                              for (int i = 0; i < v.size(); i++) {
                                 Map<String, Object> row = v.get(i);
                                 rowMap.put((String)row.get(columnField.getElnColumnName()), row.get(valueField.getElnColumnName()));
                                 Map<String, Object> idMap = null;
                                 if (rowIdList != null) {
                                    idMap = rowIdList.get(i);
                                 }

                                 if (idMap != null) {
                                    rowIdMap.put((String)row.get(columnField.getElnColumnName()), idMap.get(valueField.getElnColumnName()));
                                    rowIdMap.put("_pivot_mark", idMap.get("_pivot_mark"));
                                 }

                                 Map<String, Object> newIdMap = idMap;
                                 row.forEach((key, value) -> {
                                    if (!key.equals(columnField.getElnColumnName()) && !key.equals(valueField.getElnColumnName())) {
                                       rowMap.put(key, value);
                                       if (newIdMap != null) {
                                          rowIdMap.put(key, newIdMap.get(key));
                                       }
                                    }
                                 });
                              }

                              pivotDataObjectList.add(rowMap);
                              pivotDataObjectIdList.add(rowIdMap);
                           });
                           dataTableMap.put(ds.getElnDsName(), pivotDataObjectList);
                           List<List<Map<String, Object>>> pivotDataObjectKeyValList = this.convertMapToKeyAndValueMap(pivotDataObjectList);
                           dataKeyValueTableMap.put(ds.getElnDsName(), pivotDataObjectKeyValList);
                           dataTableIdMap.put(ds.getElnDsName(), pivotDataObjectIdList);
                        }
                     } else if ("detail".equals(ds.getDisplayCategory())) {
                        Map<String, Object> beanMapx = resultMapList.get(0);
                        if (beanMapx != null) {
                           List<String> idFieldList = beanMapx.keySet().stream().filter(e -> e.toUpperCase().contains("_ID")).collect(Collectors.toList());
                           if (!idFieldList.isEmpty()) {
                              Map<String, Object> valueMap = new HashMap<>();
                              Map<String, Object> idValueMap = new HashMap<>();
                              List<CoreElnDsFieldBean> filterDsFieldList = selectedDsFieldList.stream()
                                 .filter(dsField -> ds.getId().equals(dsField.getElnDsId()))
                                 .collect(Collectors.toList());
                              this.analysisIdAndValue(
                                 beanMapx, idFieldList, valueMap, idValueMap, filterDsFieldList, ds.getSqlContent(), CollectionUtils.emptyList()
                              );
                              dataFormMap.put(ds.getElnDsName(), valueMap);
                              dataFormIdMap.put(ds.getElnDsName(), idValueMap);
                           } else {
                              dataFormMap.put(ds.getElnDsName(), beanMapx);
                           }
                        }
                     } else if ("dropDown".equals(ds.getDisplayCategory())) {
                        for (Map<String, Object> beanMapx : resultMapList) {
                           if (beanMapx != null && !beanMapx.isEmpty()) {
                              dataObjectList.add(this.transformLowerCase(beanMapx));
                           }
                        }

                        dropDownDataMap.put(ds.getElnDsName(), dataObjectList);
                     }
                  } else if ("list".equals(ds.getDisplayCategory())) {
                     dataObjectList.add(new HashMap<>());
                     dataTableMap.put(ds.getElnDsName(), dataObjectList);
                     List<List<Map<String, Object>>> dataObjectKeyValList = this.convertMapToKeyAndValueMap(dataObjectList);
                     dataKeyValueTableMap.put(ds.getElnDsName(), dataObjectKeyValList);
                  }
               }
            }
         );
         Map<String, Object> dataMap = new HashMap<>();
         dataMap.put(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DETAIL"), dataFormMap);
         dataMap.put(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.TABLE"), dataTableMap);
         dataMap.put("dataKeyValueTableMap", dataKeyValueTableMap);
         dataMap.put(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DROP_DOWN"), dropDownDataMap);
         Map<String, Object> dataIdMap = new HashMap<>();
         dataIdMap.put(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DETAIL"), dataFormIdMap);
         dataIdMap.put(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.TABLE"), dataTableIdMap);
         mappingDataMap.put("value", JSON.toJSONString(dataMap, SerializerFeature.WriteMapNullValue));
         mappingDataMap.put("id", JSON.toJSONString(dataIdMap, SerializerFeature.WriteMapNullValue));
      }

      return mappingDataMap;
   }

   @Override
   public CoreElnTmplVersionBean selectTopVersionByTmplId(Long id) {
      List<CoreElnTmplVersionBean> selectVerList = this.coreElnTmplVersionService
         .selectListByFilter(
            SearchFilter.instance().match("ELNTMPLID", id).filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
            new Order[]{Order.desc("ID")}
         );
      if (selectVerList.size() == 1) {
         CoreElnTmplVersionBean tmplVersion = selectVerList.get(0);
         tmplVersion.setElnTmplContent(this.getContent(tmplVersion.getElnTmplFileId(), tmplVersion.getElnTmplContent()));
         return tmplVersion;
      } else {
         return new CoreElnTmplVersionBean();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void copyTmpl(Long id, Long versionId, RestJsonWrapperBean wrapper) {
      CoreElnTmplBean elnTmpl = this.getDao().selectById(id);
      elnTmpl.setId(ApplicationContextHelper.getNextIdentity());
      elnTmpl.setElnTmplName(elnTmpl.getElnTmplName() + "_copy");
      PersistableHelper.removeCreatedUserData(elnTmpl);
      List<CoreElnTmplDsBean> selectElnTmplDsList = this.coreElnTmplDsService
         .selectListByFilter(SearchFilter.instance().match("ELNTMPLID", id).filter(MatchPattern.SEQ), new Order[]{Order.desc("ID")});
      selectElnTmplDsList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setElnTmplId(elnTmpl.getId());
         PersistableHelper.removeCreatedUserData(e);
      });
      CoreElnTmplVersionBean elnTmplVersion = this.coreElnTmplVersionService.selectById(versionId);
      elnTmplVersion.setId(ApplicationContextHelper.getNextIdentity());
      elnTmplVersion.setVersionNo(1);
      elnTmplVersion.setActivatedFlag("1");
      elnTmplVersion.setElnTmplId(elnTmpl.getId());
      elnTmplVersion.setElnTmplContent(this.getContent(elnTmplVersion.getElnTmplFileId(), elnTmplVersion.getElnTmplContent()));
      if (!StringUtils.isBlank(elnTmplVersion.getElnTmplContent())) {
         Long elnTmplFileId = this.coreElnTmplVersionService.uploadElnTempVersion(elnTmplVersion.getElnTmplContent(), elnTmplVersion);
         if (elnTmplFileId != null) {
            elnTmplVersion.setElnTmplFileId(elnTmplFileId);
         }
      }

      PersistableHelper.removeCreatedUserData(elnTmplVersion);
      this.getDao().insert(elnTmpl);
      this.coreElnTmplDsService.getDao().insert(selectElnTmplDsList);
      this.coreElnTmplVersionService.getDao().insert(elnTmplVersion);
   }

   private static String getGroupByKey(Map<String, Object> map, List<CoreElnDsFieldBean> groupFieldList) {
      String key = "";

      for (CoreElnDsFieldBean bean : groupFieldList) {
         key = key + map.get(bean.getElnColumnName()) + "getGroupByKey";
      }

      return key;
   }

   private void analysisIdAndValue(
      Map<String, Object> beanMap,
      List<String> idFieldList,
      Map<String, Object> valueMap,
      Map<String, Object> idValueMap,
      List<CoreElnDsFieldBean> filterDsFieldList,
      String sql,
      List<CoreElnDsFieldBean> groupFieldList
   ) {
      Map<String, Object> idMap = idFieldList.stream().collect(Collectors.toMap(Function.identity(), beanMap::remove));
      idMap.forEach(
         (idField, id) -> {
            String sameIdMark = idField.substring(0, idField.indexOf("_") + 1);
            List<String> fieldList = beanMap.keySet().stream().filter(field -> StringUtils.startsWith(field, sameIdMark)).collect(Collectors.toList());
            if (!fieldList.isEmpty()) {
               fieldList.stream().forEach(field -> {
                  String showField = StringUtils.replace(field, sameIdMark, "");
                  valueMap.put(showField, beanMap.get(field));
                  idValueMap.put(showField, id);
                  if (groupFieldList.size() > 0) {
                     idValueMap.put("_pivot_mark", getGroupByKey(beanMap, groupFieldList));
                  }
               });
            }

            if (!StringUtils.isEmpty(sql)) {
               filterDsFieldList.forEach(
                  filterDsField -> {
                     if (!StringUtils.isEmpty(filterDsField.getElnColumnName())
                        && !idValueMap.keySet().contains(filterDsField.getElnColumnName())
                        && (
                           StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getElnColumnName() + " ")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getElnColumnName() + "\r")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getElnColumnName() + ",")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getElnColumnName() + "\r\n")
                              || StringUtils.containsIgnoreCase(sql, sameIdMark + filterDsField.getElnColumnName() + "\n")
                        )) {
                        idValueMap.put(filterDsField.getElnColumnName(), id);
                        valueMap.put(filterDsField.getElnColumnName(), null);
                     }
                  }
               );
            }
         }
      );
      List<String> noIdFieldList = beanMap.keySet().stream().filter(field -> !field.contains("_")).collect(Collectors.toList());
      if (!noIdFieldList.isEmpty()) {
         noIdFieldList.stream().forEach(field -> valueMap.put(field, beanMap.get(field)));
      }
   }

   private void splitJointJson(
      String key, Map<String, Map<String, Object>> dataSourcePropertiesMap, List<String> dataSourceList, Map<String, List<String>> dataColumnMap
   ) {
      boolean isArray = key.contains(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.TABLE"));
      Map<String, Object> jsonDataMap = new HashMap<>();
      jsonDataMap.put("dataFieldType", "text");
      jsonDataMap.put("type", "string");
      Map<String, Object> dsNameMap = new HashMap<>();

      for (String dsName : dataSourceList) {
         Map<String, Object> fidldsMap = new HashMap<>();
         if (isArray) {
            fidldsMap.put("dataFieldType", "table");
            fidldsMap.put("type", "array");
         } else {
            fidldsMap.put("dataFieldType", "text");
            fidldsMap.put("type", "string");
         }

         List<String> dataColumnList = dataColumnMap.get(dsName);
         if (dataColumnList != null && !dataColumnList.isEmpty()) {
            Map<String, Object> fieldMap = new LinkedHashMap<>();

            for (String fieldName : dataColumnList) {
               Map<String, String> dataSourceColumnKey = new HashMap<>();
               dataSourceColumnKey.put("dataFieldType", "text");
               dataSourceColumnKey.put("type", "string");
               fieldMap.put(fieldName, dataSourceColumnKey);
            }

            if (isArray) {
               Map<String, Object> tableMap = new HashMap<>();
               tableMap.put("type", "object");
               tableMap.put("properties", fieldMap);
               fidldsMap.put("items", tableMap);
            } else {
               fidldsMap.put("properties", fieldMap);
            }
         }

         dsNameMap.put(dsName, fidldsMap);
      }

      jsonDataMap.put("properties", dsNameMap);
      dataSourcePropertiesMap.put(key, jsonDataMap);
   }

   private Map<String, Object> transformLowerCase(Map<String, Object> sourceMap) {
      CollectionUtils.lowerCaseKey(sourceMap);
      Map<String, Object> resultMap = new HashMap<>();
      sourceMap.forEach((k, v) -> resultMap.put(k.toLowerCase(), v));
      return resultMap;
   }

   protected String getContent(Long elnTmplFileId, String elnTmplContent) {
      if (elnTmplFileId != null) {
         CoreFileBean file = this.fileService.selectByIdIfPresent(elnTmplFileId);
         if (file == null) {
            throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.TIP.ELN_TEMP_FILE_NOT_EXIST"));
         } else {
            Path localPath = CoreFileUtils.getLocalPath(file);
            if (!Files.exists(localPath)) {
               this.fileManager.download(file);
            }

            try {
               return new String(Files.readAllBytes(localPath), "utf-8");
            } catch (IOException var6) {
               throw new UnexpectedException(var6);
            }
         }
      } else {
         return elnTmplContent;
      }
   }

   private List<List<Map<String, Object>>> convertMapToKeyAndValueMap(List<Map<String, Object>> originMapList) {
      List<List<Map<String, Object>>> result = new ArrayList<>();
      originMapList.forEach(map -> {
         List<Map<String, Object>> itemList = new ArrayList<>();
         map.forEach((k, v) -> {
            HashMap<String, Object> keyValueMap = new HashMap<>();
            keyValueMap.put("key", k);
            keyValueMap.put("value", v);
            itemList.add(keyValueMap);
         });
         result.add(itemList);
      });
      return result;
   }
}
