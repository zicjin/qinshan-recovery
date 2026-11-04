package com.sunwayworld.module.sys.dpconfig.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.InvalidDataException;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.io.file.FilePathDTO;
import com.sunwayworld.framework.io.file.FileScope;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.dao.MapDao;
import com.sunwayworld.framework.support.base.service.GenericImportService;
import com.sunwayworld.framework.support.base.service.GenericMapImportService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.tree.TreeDescriptor;
import com.sunwayworld.framework.support.tree.TreeHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.FileUtils;
import com.sunwayworld.framework.utils.JsonUtils;
import com.sunwayworld.framework.utils.ReflectionUtils;
import com.sunwayworld.framework.utils.ReflectionUtils.MethodWrapper;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.bean.CoreFileImportResultDTO;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigBean;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigImportDataDTO;
import com.sunwayworld.module.sys.dpconfig.bean.CoreDpConfigLineBean;
import com.sunwayworld.module.sys.dpconfig.dao.CoreDpConfigDao;
import com.sunwayworld.module.sys.dpconfig.service.CoreDpConfigLineService;
import com.sunwayworld.module.sys.dpconfig.service.CoreDpConfigService;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreDpConfigServiceImpl implements CoreDpConfigService {
   @Autowired
   private CoreFileService fileService;
   @Autowired
   private CoreDpConfigDao dpConfigDao;
   @Autowired
   private CoreDpConfigLineService dpConfigLineService;
   @Autowired
   @Lazy
   private CoreFileManager fileManager;
   @Autowired
   private MapDao mapDao;

   public CoreDpConfigDao getDao() {
      return this.dpConfigDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   public Long insert(RestJsonWrapperBean jsonWrapper) {
      CoreDpConfigBean dpConfig = jsonWrapper.parseUnique(CoreDpConfigBean.class);
      dpConfig.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(dpConfig);
      if (dpConfig.getParentId() != null) {
         String parentTableName = this.selectById(dpConfig.getParentId()).getTableName();
         CoreDpConfigLineBean dpConfigLine = new CoreDpConfigLineBean();
         dpConfigLine.setId(ApplicationContextHelper.getNextIdentity());
         dpConfigLine.setDpConfigId(dpConfig.getId());
         dpConfigLine.setRelateTableName(parentTableName);
         dpConfigLine.setRelateFieldName("id");
         this.dpConfigLineService.getDao().insert(dpConfigLine);
      }

      return dpConfig.getId();
   }

   @Override
   public List<CoreDpConfigDTO> selectTree(MapperParameter parameter) {
      List<CoreDpConfigBean> dpConfigList = this.selectAll();
      TreeDescriptor<CoreDpConfigBean> descriptor = new TreeDescriptor<>("id", "parentId", "dpConfigName", "id");
      List<CoreDpConfigDTO> dpConfigDTOList = TreeHelper.parseTreeNode(dpConfigList, descriptor, CoreDpConfigDTO.class);
      TreeHelper.updateChildQty(dpConfigDTOList);
      return dpConfigDTOList;
   }

   @Override
   public Page<CoreDpConfigLineBean> selectConfigLinePaginationByDpConfigId(Long id, RestJsonWrapperBean wrapper) {
      return this.dpConfigLineService.selectPaginationByFilter(SearchFilter.instance().match("DPCONFIGID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertConfigLine(Long id, RestJsonWrapperBean wrapper) {
      List<CoreDpConfigLineBean> coreDpConfigLineList = wrapper.parse(CoreDpConfigLineBean.class);
      coreDpConfigLineList.forEach(e -> {
         e.setId(ApplicationContextHelper.getNextIdentity());
         e.setDpConfigId(id);
      });
      this.dpConfigLineService.getDao().insert(coreDpConfigLineList);
      return coreDpConfigLineList.get(0).getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteConfigLine(Long id, RestJsonWrapperBean wrapper) {
      this.dpConfigLineService.delete(wrapper);
   }

   @Override
   public String export(RestJsonWrapperBean wrapper) {
      String tableName = wrapper.getParamValue("tableName");
      List<String> idList = wrapper.parseId(String.class);
      CoreDpConfigBean dpConfig = this.selectFirstByFilter(
         SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.EQ).match("PARENTID", null).filter(MatchPattern.EQ), new Order[0]
      );
      if (dpConfig == null) {
         throw new InvalidDataException("CORE.MODULE.SYS.DPCONFIGS.EXCEPTION.NO_DP_CONFIG");
      } else {
         List<?> exportDataList = null;
         GenericService<?, String> exportService = ApplicationContextHelper.getServiceByTable(tableName);
         if (exportService == null) {
            Object lcdpMapDaoImpl = ApplicationContextHelper.getBean("LcdpMapDaoImpl");
            Method getScriptServiceMethod = ReflectionUtils.getUniqueMethodByName(lcdpMapDaoImpl.getClass(), "getScriptService");
            Object lcdpService = ReflectionUtils.invokeMethod(getScriptServiceMethod, lcdpMapDaoImpl, TableContext.of(tableName));
            if (lcdpService != null) {
               Method selectListByIdsMethod = null;
               List<MethodWrapper> selectListByIdsMethodList = ReflectionUtils.getMethodWrapperListByName(lcdpService.getClass(), "selectListByIdList");

               for (int i = 0; i < selectListByIdsMethodList.size(); i++) {
                  Method method = selectListByIdsMethodList.get(i).getMethod();
                  Class<?>[] parameterTypes = method.getParameterTypes();
                  if (parameterTypes.length == 2) {
                     selectListByIdsMethod = method;
                     break;
                  }
               }

               exportDataList = (List<?>)ReflectionUtils.invokeMethod(selectListByIdsMethod, lcdpService, TableContext.of(tableName), idList);
            }
         } else {
            exportDataList = exportService.getDao().selectListByIds(idList);
         }

         long[] orderNo = new long[]{1L};
         List<Map<String, Object>> resultList = new ArrayList<>();
         Map<String, Object> map = new HashMap<>();
         map.put("tableName", tableName);
         map.put("data", exportDataList);
         map.put("orderNo", orderNo[0]);
         this.exportData(exportDataList, dpConfig, map, resultList, orderNo);
         resultList.add(map);
         CoreUserBean coreUser = LocalContextHelper.getLoginUser();
         String fileName = this.getFileName(tableName, coreUser);
         FilePathDTO filePath = CoreFileUtils.toFilePath(FileScope.export, fileName);
         File exportFile = null;

         try {
            exportFile = FileUtils.createTempFile();
            FileUtils.write(exportFile, JSONObject.toJSONString(resultList));
            this.fileManager.upload(filePath, exportFile);
         } finally {
            FileUtils.deleteQuietly(exportFile);
         }

         return this.fileManager.getDownloadUrl(filePath);
      }
   }

   @Override
   public List<CoreDpConfigImportDataDTO> importValidate(Long fileId, RestJsonWrapperBean wrapper) {
      CoreFileBean coreFile = this.fileService.selectDetail(fileId);
      Path path = CoreFileUtils.getLocalPath(coreFile);
      this.fileManager.download(coreFile, path);
      String json = FileUtils.extractTextFromFile(path.toFile());
      List<CoreDpConfigImportDataDTO> repeatDataImportDataDTOList = new ArrayList<>();
      this.importDataValidate(json, repeatDataImportDataDTOList);
      return repeatDataImportDataDTOList.stream()
         .collect(
            Collectors.collectingAndThen(
               Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(CoreDpConfigImportDataDTO::getTableName))), ArrayList::new
            )
         );
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.IMPORT)
   @Audit("AUDIT.IMPORT")
   @Override
   public CoreFileImportResultDTO importData(Long fileId, RestJsonWrapperBean wrapper) {
      Map<String, String> importParam = wrapper.getParamMap();
      CoreFileBean coreFile = this.fileService.selectDetail(fileId);
      CoreFileImportResultDTO importResult = new CoreFileImportResultDTO();
      Path path = CoreFileUtils.getLocalPath(coreFile);
      this.fileManager.download(coreFile, path);
      String json = FileUtils.extractTextFromFile(path.toFile());
      Map<String, Map<String, String>> map = new HashMap<>();
      this.importData(json, map, importParam);
      return importResult;
   }

   private void exportData(List<?> exportDataList, CoreDpConfigBean dpConfig, Map<String, Object> map, List<Map<String, Object>> resultList, long[] orderNo) {
      if (dpConfig.getParentId() == null) {
         List<CoreDpConfigLineBean> currDpConfigLineList = this.dpConfigLineService
            .selectListByFilter(SearchFilter.instance().match("DPCONFIGID", dpConfig.getId()).filter(MatchPattern.EQ), new Order[0]);
         if (!currDpConfigLineList.isEmpty()) {
            this.exportDataByRelateConfig(currDpConfigLineList, exportDataList, resultList, orderNo);
         }
      }

      List<CoreDpConfigBean> childDpConfigList = this.selectListByFilter(
         SearchFilter.instance().match("PARENTID", dpConfig.getId()).filter(MatchPattern.EQ), new Order[0]
      );
      if (!childDpConfigList.isEmpty()) {
         List<CoreDpConfigLineBean> dpConfigLineList = this.dpConfigLineService
            .selectListByFilter(
               SearchFilter.instance().match("DPCONFIGID", childDpConfigList.stream().map(e -> e.getId()).collect(Collectors.toList())).filter(MatchPattern.OR),
               new Order[0]
            );
         List<Map<String, Object>> childrenList = new ArrayList<>();

         for (CoreDpConfigBean childDpConfig : childDpConfigList) {
            List<CoreDpConfigLineBean> childDpConfigLineList = dpConfigLineList.stream()
               .filter(e -> e.getDpConfigId().equals(childDpConfig.getId()))
               .collect(Collectors.toList());
            List<CoreDpConfigLineBean> relateDpConfigLineList = childDpConfigLineList.stream()
               .filter(e -> dpConfig.getTableName().equals(e.getRelateTableName()))
               .collect(Collectors.toList());
            if (!relateDpConfigLineList.isEmpty()) {
               CoreDpConfigLineBean dpConfigLine = relateDpConfigLineList.get(0);
               String relateFieldName = dpConfigLine.getRelateFieldName();
               String fieldName = dpConfigLine.getFieldName();
               GenericService<?, String> relateTableService = ApplicationContextHelper.getServiceByTable(childDpConfig.getTableName());
               GenericService<?, String> tableService = ApplicationContextHelper.getServiceByTable(dpConfig.getTableName());
               List<String> relateFieldValueList;
               if (tableService != null) {
                  relateFieldValueList = exportDataList.stream()
                     .map(e -> ReflectionUtils.getFieldValue(e, relateFieldName).toString())
                     .collect(Collectors.toList());
               } else {
                  relateFieldValueList = exportDataList.stream().map(e -> ((Map)e).get(relateFieldName).toString()).collect(Collectors.toList());
               }

               List<?> relateDataList;
               if (relateTableService != null) {
                  relateDataList = relateTableService.getDao().selectListByOneColumnValues(relateFieldValueList, fieldName, Order.asc("ID"));
               } else {
                  relateDataList = this.mapDao
                     .selectListByColumnValueList(TableContext.of(childDpConfig.getTableName()), relateFieldValueList, fieldName, Order.asc("ID"));
               }

               if (!relateDataList.isEmpty()) {
                  Map<String, Object> childMap = new HashMap<>();
                  childMap.put("tableName", childDpConfig.getTableName());
                  childMap.put("data", relateDataList);
                  this.exportData(relateDataList, childDpConfig, childMap, resultList, orderNo);
                  childrenList.add(childMap);
                  this.exportDataByRelateConfig(childDpConfigLineList, relateDataList, resultList, orderNo);
               }
            }
         }

         if (!childrenList.isEmpty()) {
            map.put("children", childrenList);
         }
      }
   }

   private void exportDataByRelateConfig(
      List<CoreDpConfigLineBean> currDpConfigLineList, List<?> exportDataList, List<Map<String, Object>> resultList, long[] orderNo
   ) {
      for (CoreDpConfigLineBean relateConfigLine : currDpConfigLineList.stream()
         .filter(e -> e.getRelateConfigId() != null && !StringUtils.isEmpty(e.getFieldName()))
         .collect(Collectors.toList())) {
         String relateConfigLineFieldName = relateConfigLine.getFieldName();
         List<String> relateConfigTableIdList = exportDataList.stream()
            .filter(e -> ReflectionUtils.getFieldValue(e, relateConfigLineFieldName) != null)
            .map(e -> ReflectionUtils.getFieldValue(e, relateConfigLineFieldName).toString())
            .collect(Collectors.toList());
         if (!relateConfigTableIdList.isEmpty()) {
            Long relateConfigId = relateConfigLine.getRelateConfigId();
            CoreDpConfigBean relateDpConfig = this.selectById(relateConfigId);
            String relateConfigTableName = relateDpConfig.getTableName();
            GenericService<?, String> releateService = ApplicationContextHelper.getServiceByTable(relateConfigTableName);
            new ArrayList();
            List relateTableDataList;
            if (releateService == null) {
               relateTableDataList = this.mapDao.selectListByIdList(TableContext.of(relateConfigTableName), relateConfigTableIdList);
            } else {
               relateTableDataList = releateService.getDao().selectListByIds(relateConfigTableIdList);
            }

            Map<String, Object> relateConfigMap = new HashMap<>();
            relateConfigMap.put("tableName", relateConfigTableName);
            relateConfigMap.put("data", relateTableDataList);
            orderNo[0]++;
            relateConfigMap.put("orderNo", orderNo[0]);
            this.exportData(relateTableDataList, relateDpConfig, relateConfigMap, resultList, orderNo);
            resultList.add(relateConfigMap);
         }
      }
   }

   private void importDataValidate(String json, List<CoreDpConfigImportDataDTO> repeatDataImportDataDTOList) {
      List<CoreDpConfigImportDataDTO> dpConfigImportDataDTOList = null;

      try {
         dpConfigImportDataDTOList = JsonUtils.parseList(json, CoreDpConfigImportDataDTO.class);
      } catch (Exception var13) {
         throw new UnexpectedException("GIKAM.EXCEPTION.REQUIRE_JSON");
      }

      if (!dpConfigImportDataDTOList.isEmpty()) {
         if (dpConfigImportDataDTOList.get(0).getOrderNo() != null) {
            dpConfigImportDataDTOList = dpConfigImportDataDTOList.stream()
               .sorted((e1, e2) -> e2.getOrderNo().compareTo(e1.getOrderNo()))
               .collect(Collectors.toList());
         }

         for (CoreDpConfigImportDataDTO dpConfigImportDataDTO : dpConfigImportDataDTOList) {
            String tableName = dpConfigImportDataDTO.getTableName();
            GenericService tableService = ApplicationContextHelper.getServiceByTable(tableName);
            String data = dpConfigImportDataDTO.getData();
            List dataList = new ArrayList();
            if (tableService != null) {
               dataList = JsonUtils.parseList(data, tableService.getDao().getType());
            } else {
               for (String objString : JsonUtils.parseList(data, String.class)) {
                  dataList.add(JsonUtils.parse(JSONObject.parseObject(objString), Map.class));
               }
            }

            CoreDpConfigBean dpConfig = this.selectFirstByFilter(SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.EQ), new Order[0]);
            String uniqueFields = dpConfig.getUniqueFields();
            if (!StringUtils.isEmpty(uniqueFields)) {
               new ArrayList();
               List var17;
               if (tableService == null) {
                  var17 = this.mapDao.selectList(TableContext.of(tableName), dataList, Arrays.asList(uniqueFields.split(",")), CollectionUtils.emptyList());
               } else {
                  var17 = tableService.getDao().selectList(dataList, Arrays.asList(uniqueFields.split(",")), CollectionUtils.emptyList());
               }

               if (!var17.isEmpty()) {
                  repeatDataImportDataDTOList.add(dpConfigImportDataDTO);
               }
            }

            if (!StringUtils.isEmpty(dpConfigImportDataDTO.getChildren())) {
               this.importDataValidate(dpConfigImportDataDTO.getChildren(), repeatDataImportDataDTOList);
            }
         }
      }
   }

   private void importData(String json, Map<String, Map<String, String>> map, Map<String, String> importParam) {
      List<CoreDpConfigImportDataDTO> dpConfigImportDataDTOList = JsonUtils.parseList(json, CoreDpConfigImportDataDTO.class);
      if (!dpConfigImportDataDTOList.isEmpty()) {
         if (dpConfigImportDataDTOList.get(0).getOrderNo() != null) {
            dpConfigImportDataDTOList = dpConfigImportDataDTOList.stream()
               .sorted((e1, e2) -> e2.getOrderNo().compareTo(e1.getOrderNo()))
               .collect(Collectors.toList());
         }

         List<String> importTableNameList = dpConfigImportDataDTOList.stream().map(e -> e.getTableName()).collect(Collectors.toList());
         List<CoreDpConfigLineBean> dpConfigLineList = this.dpConfigLineService
            .selectListByFilter(
               SearchFilter.instance().match("TABLENAME", importTableNameList).filter(MatchPattern.OR).match("FIELDNAME", null).filter(MatchPattern.DIFFER),
               new Order[0]
            );

         for (CoreDpConfigImportDataDTO dpConfigImportDataDTO : dpConfigImportDataDTOList) {
            String tableName = dpConfigImportDataDTO.getTableName();
            GenericService tableService = ApplicationContextHelper.getServiceByTable(tableName);
            Object lcdpMapDaoImpl = ApplicationContextHelper.getBean("LcdpMapDaoImpl");
            Method getScriptServiceMethod = ReflectionUtils.getUniqueMethodByName(lcdpMapDaoImpl.getClass(), "getScriptService");
            Object lcdpService = ReflectionUtils.invokeMethod(getScriptServiceMethod, lcdpMapDaoImpl, TableContext.of(tableName));
            String data = dpConfigImportDataDTO.getData();
            List dataList = new ArrayList();
            if (tableService != null) {
               dataList = JsonUtils.parseList(data, tableService.getDao().getType());
            } else {
               for (String objString : JsonUtils.parseList(data, String.class)) {
                  dataList.add(JsonUtils.parse(JSONObject.parseObject(objString), Map.class));
               }
            }

            CoreDpConfigBean dpConfig = this.selectFirstByFilter(SearchFilter.instance().match("TABLENAME", tableName).filter(MatchPattern.EQ), new Order[0]);
            String uniqueFields = dpConfig.getUniqueFields();
            List exsitedDataList = new ArrayList();
            List<String> uniqueFieldList = StringUtils.isEmpty(uniqueFields) ? null : Arrays.asList(uniqueFields.split(","));
            if (!StringUtils.isEmpty(uniqueFields)) {
               if (tableService == null) {
                  exsitedDataList = this.mapDao
                     .selectList(TableContext.of(tableName), dataList, Arrays.asList(uniqueFields.split(",")), CollectionUtils.emptyList());
               } else {
                  exsitedDataList = tableService.getDao().selectList(dataList, Arrays.asList(uniqueFields.split(",")), CollectionUtils.emptyList());
               }
            }

            Class idClass = null;
            if (tableService != null) {
               idClass = BeanPropertyHelper.getBeanPropertyDescriptor(tableService.getDao().getType(), "id").getProperty().getType();
            } else {
               idClass = ((Map)dataList.get(0)).get("ID").getClass();
            }

            Map<String, String> idMap = new HashMap<>();
            final List insertDataList = new ArrayList();
            final List updateDataList = new ArrayList();
            List dataRepeatInsertDataList = new ArrayList();

            for (Object bean : dataList) {
               boolean dataRepeatInsertFlag = false;
               String oldId = "";
               if (tableService != null) {
                  oldId = ReflectionUtils.invokeReadMethod(bean, "id").toString();
               } else {
                  oldId = ((Map)bean).get("ID").toString();
               }

               String newId = null;
               if (!exsitedDataList.isEmpty()) {
                  List filterList = (List)exsitedDataList.stream().filter(e -> {
                     boolean validateFlag = true;

                     for (String uniqueField : uniqueFieldList) {
                        if (tableService != null) {
                           if (ReflectionUtils.invokeReadMethod(e, uniqueField) == null) {
                              if (ReflectionUtils.invokeReadMethod(bean, uniqueField) != null) {
                                 validateFlag = false;
                                 break;
                              }
                           } else if (!ReflectionUtils.invokeReadMethod(e, uniqueField).equals(ReflectionUtils.invokeReadMethod(bean, uniqueField))) {
                              validateFlag = false;
                              break;
                           }
                        } else if (((Map)e).get(uniqueField) != null) {
                           if (((Map)bean).get(uniqueField) != null) {
                              validateFlag = false;
                              break;
                           }
                        } else if (!((Map)bean).get(uniqueField).equals(((Map)bean).get(uniqueField))) {
                           validateFlag = false;
                           break;
                        }
                     }

                     return validateFlag;
                  }).collect(Collectors.toList());
                  if (!filterList.isEmpty()) {
                     if ("override".equals(importParam.get(tableName))) {
                        if (tableService != null) {
                           Persistable existsData = (Persistable)filterList.get(0);
                           newId = existsData.getId().toString();
                           ReflectionUtils.invokeWriteMethod(bean, "id", ConvertUtils.convert(newId + "", idClass));
                           updateDataList.add(bean);
                        } else {
                           Map existsData = (Map)filterList.get(0);
                           newId = existsData.get("ID").toString();
                           ((Map)bean).put("ID", ConvertUtils.convert(newId + "", idClass));
                           updateDataList.add(bean);
                        }
                     }

                     if (!"insert".equals(importParam.get(tableName))) {
                        if (tableService != null) {
                           Persistable existsData = (Persistable)filterList.get(0);
                           newId = existsData.getId().toString();
                           idMap.put(oldId, newId + "");
                        } else {
                           Map existsData = (Map)filterList.get(0);
                           newId = existsData.get("ID").toString();
                           idMap.put(oldId, newId + "");
                        }
                        continue;
                     }

                     dataRepeatInsertFlag = true;
                  }
               }

               for (CoreDpConfigLineBean tableDpConfigLine : dpConfigLineList.stream()
                  .filter(e -> e.getExt$Item("tablename").equals(tableName))
                  .collect(Collectors.toList())) {
                  String fieldName = tableDpConfigLine.getFieldName();
                  String relateTableName = StringUtils.isEmpty(tableDpConfigLine.getRelateTableName())
                     ? tableDpConfigLine.getExt$Item("relateconfigtablename")
                     : tableDpConfigLine.getRelateTableName();
                  String relateFieldName = StringUtils.isEmpty(tableDpConfigLine.getRelateFieldName()) ? "id" : tableDpConfigLine.getRelateFieldName();
                  Class fieldClass = null;
                  if (tableService != null) {
                     fieldClass = BeanPropertyHelper.getBeanPropertyDescriptor(tableService.getDao().getType(), fieldName).getProperty().getType();
                  } else {
                     fieldClass = ((Map)bean).get(fieldName).getClass();
                  }

                  if ("id".equalsIgnoreCase(relateFieldName)) {
                     if (tableService != null) {
                        if (ReflectionUtils.invokeReadMethod(bean, fieldName) != null) {
                           String oldValue = ReflectionUtils.invokeReadMethod(bean, fieldName).toString();
                           if (map.get(relateTableName) != null) {
                              String newValue = map.get(relateTableName).get(oldValue);
                              ReflectionUtils.invokeWriteMethod(bean, fieldName, ConvertUtils.convert(newValue, fieldClass));
                           } else if (tableName.equals(relateTableName)) {
                              String newValue = idMap.get(oldValue);
                              if (StringUtils.isEmpty(newValue)) {
                                 newValue = oldValue;
                              }

                              ReflectionUtils.invokeWriteMethod(bean, fieldName, ConvertUtils.convert(newValue, fieldClass));
                           }
                        }
                     } else if (((Map)bean).get(fieldName) != null) {
                        String oldValue = ((Map)bean).get(fieldName).toString();
                        if (map.get(relateTableName) != null) {
                           String newValue = map.get(relateTableName).get(oldValue);
                           ((Map)bean).put(fieldName, ConvertUtils.convert(newValue, fieldClass));
                        } else if (tableName.equals(relateTableName)) {
                           String newValue = idMap.get(oldValue);
                           if (StringUtils.isEmpty(newValue)) {
                              newValue = oldValue;
                           }

                           ((Map)bean).put(fieldName, ConvertUtils.convert(newValue, fieldClass));
                        }
                     }
                  }
               }

               if (tableService instanceof GenericImportService) {
                  GenericImportService tableImportService = (GenericImportService)tableService;
                  tableImportService.setProperty((Persistable)bean, dataRepeatInsertFlag);
                  if (dataRepeatInsertFlag) {
                     dataRepeatInsertDataList.add(bean);
                  }

                  newId = ((Persistable)bean).getId().toString();
               } else if (lcdpService instanceof GenericMapImportService) {
                  GenericMapImportService tableImportService = (GenericMapImportService)lcdpService;
                  tableImportService.setProperty((Map)bean, dataRepeatInsertFlag);
                  if (dataRepeatInsertFlag) {
                     dataRepeatInsertDataList.add(bean);
                  }

                  newId = ((Map)bean).get("ID").toString();
               } else {
                  newId = ApplicationContextHelper.getNextIdentity() + "";
                  ReflectionUtils.invokeWriteMethod(bean, "id", ConvertUtils.convert(newId + "", idClass));
               }

               idMap.put(oldId, newId + "");
               insertDataList.add(bean);
            }

            Map<String, String> existMap = map.get(tableName);
            if (existMap != null && !existMap.isEmpty()) {
               existMap.putAll(idMap);
               map.put(tableName, existMap);
            } else {
               map.put(tableName, idMap);
            }

            if (tableService != null) {
               tableService.getDao().insert(insertDataList);
               tableService.getDao().updateIfChanged(updateDataList);
            } else {
               this.mapDao.insert(tableName, insertDataList);
               this.mapDao.updateIfChanged(tableName, updateDataList);
            }

            if ((!insertDataList.isEmpty() || !updateDataList.isEmpty()) && tableService instanceof GenericImportService) {
               GenericImportService tableImportService = (GenericImportService)tableService;
               tableImportService.postImport(new ArrayList() {
                  {
                     this.addAll(insertDataList);
                     this.addAll(updateDataList);
                  }
               });
            }

            if ((!insertDataList.isEmpty() || !updateDataList.isEmpty()) && lcdpService instanceof GenericMapImportService) {
               GenericMapImportService tableImportService = (GenericMapImportService)lcdpService;
               tableImportService.postImport(new ArrayList() {
                  {
                     this.addAll(insertDataList);
                     this.addAll(updateDataList);
                  }
               });
            }

            if (!StringUtils.isEmpty(dpConfigImportDataDTO.getChildren())) {
               this.importData(dpConfigImportDataDTO.getChildren(), map, importParam);
            }
         }
      }
   }

   private String getFileName(String topic, CoreUserBean user) {
      return topic + "-" + user.getUserName() + "-" + DateTimeUtils.formatLocalDateTime(LocalDateTime.now(), "yyyyMMddHHmmss") + ".txt";
   }
}
