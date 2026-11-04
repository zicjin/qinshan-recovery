package com.sunwayworld.cloud.module.eln.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.eln.bean.CoreElnBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnFieldBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnFieldDTO;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplDsBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionDTO;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnAtRecordService;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsService;
import com.sunwayworld.cloud.module.eln.service.CoreElnFieldService;
import com.sunwayworld.cloud.module.eln.service.CoreElnService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplDsService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplService;
import com.sunwayworld.cloud.module.eln.service.CoreElnTmplVersionService;
import com.sunwayworld.cloud.module.eln.support.constant.CoreElnHelper;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ClassUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.GzipUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.item.file.utils.CoreFileUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreElnServiceImpl implements CoreElnService {
   private static Logger logger = LogManager.getLogger(CoreElnTmplService.class);
   @Autowired
   private CoreElnDao coreElnDao;
   @Lazy
   @Autowired
   private CoreElnTmplService coreElnTmplService;
   @Lazy
   @Autowired
   private CoreElnAtRecordService coreElnAtRecordService;
   @Lazy
   @Autowired
   private CoreElnService coreElnService;
   @Autowired
   private CoreElnTmplDsService coreElnTmplDsService;
   @Autowired
   private CoreElnDsService coreElnDsService;
   @Autowired
   private CoreElnFieldService coreElnFieldService;
   @Autowired
   private CoreElnTmplVersionService coreElnTmplVersionService;
   @Autowired
   private CoreFileManager fileManager;
   @Autowired
   private CoreFileService fileService;
   private static final List<String> removeElnContentList = new ArrayList<>();

   public CoreElnDao getDao() {
      return this.coreElnDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public CoreElnDTO selectElnDTO(Long tmplId, Long elnId, RestJsonWrapperBean wrapper) {
      CoreElnBean eln = null;
      if (null == elnId) {
         CoreElnTmplBean elnTmpl = this.coreElnTmplService.selectById(tmplId);
         MapperParameter elnParam = wrapper.extractMapFilter();
         Object bizId = elnParam.get("bizId");
         Object bizCategory = elnParam.get("bizCategory");
         GikamConcurrentLocker.block(this.coreElnService.getDao().getTable());
         List<CoreElnBean> elnList = this.coreElnService
            .selectListByFilter(
               SearchFilter.instance()
                  .match("ELNTMPLID", tmplId)
                  .filter(MatchPattern.EQ)
                  .match("BIZCATEGORY", bizCategory)
                  .filter(MatchPattern.EQ)
                  .match("BIZID", bizId)
                  .filter(MatchPattern.EQ),
               new Order[]{Order.desc("ID")}
            );
         if (elnList.isEmpty()) {
            eln = new CoreElnBean();
            elnId = ApplicationContextHelper.getNextIdentity();
            eln.setId(elnId);
            eln.setBizId(bizId.toString());
            eln.setElnTmplId(tmplId);
            eln.setElnParam(JSON.toJSONString(elnParam));
            eln.setBizCategory(elnTmpl.getBizCategory());
            String elnName = elnTmpl.getElnTmplName() == null
               ? ""
               : elnTmpl.getElnTmplName() + " " + LocalDate.now().format(DateTimeFormatter.ofPattern("uuuu年MM月d日"));
            eln.setElnName(elnName);
            this.getDao().insert(eln);
         } else {
            eln = elnList.get(0);
            elnId = eln.getId();
            eln.setElnParam(JSON.toJSONString(elnParam));
            this.getDao().update(eln, new String[]{"ELNPARAM"});
         }
      } else {
         eln = this.coreElnService.selectById(elnId);
      }

      CoreElnDTO elnDto = new CoreElnDTO();
      elnDto.setId(elnId);
      elnDto.setElnTmplId(eln.getElnTmplId());
      elnDto.setElnContent(this.getReduceElnContent(this.getContent(eln.getElnContentFileId(), eln.getElnContent())));
      elnDto.setElnParam(eln.getElnParam());
      elnDto.setElnName(eln.getElnName());
      if (StringUtils.isEmpty(elnDto.getElnContent())) {
         List<CoreElnTmplVersionBean> selectVerList = this.coreElnTmplVersionService
            .selectListByFilter(
               SearchFilter.instance().match("ELNTMPLID", elnDto.getElnTmplId()).filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
               new Order[]{Order.desc("ID")}
            );
         if (selectVerList.size() == 1) {
            CoreElnTmplVersionDTO verDto = this.coreElnTmplService.selectElnDsDTO(selectVerList.get(0).getId(), wrapper);
            elnDto.setElnContent(this.getReduceElnContent(verDto.getElnTmplContent()));
         }
      }

      Map<String, String> mappingDataMap = this.coreElnTmplService.getDataByDs(elnDto.getElnTmplId(), elnDto.getElnParam());
      elnDto.setData(mappingDataMap.get("value"));
      elnDto.setIdData(mappingDataMap.get("id"));
      String elnContent = this.getReduceElnContent(elnDto.getElnContent());

      try {
         elnContent = GzipUtils.compress(elnContent);
      } catch (IOException var11) {
         logger.info("ELN JSON字符串压缩失败，" + var11.getMessage(), (Throwable)var11);
      }

      elnDto.setElnContent(elnContent);
      return elnDto;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.UPDATE)
   @Audit("AUDIT.SAVE")
   @Override
   public void updateEln(Long id, RestJsonWrapperBean wrapper) {
      CoreElnDTO dto = wrapper.parseUnique(CoreElnDTO.class);
      Long elnTmplId = this.selectColumnById(id, "ELNTMPLID", Long.class);
      dto.setElnTmplId(elnTmplId);
      dto.setWriteBackCategory(this.coreElnTmplService.selectById(elnTmplId).getWriteBackCategory());
      CoreElnBean updateEln = new CoreElnBean();
      updateEln.setId(id);
      String elnContent = dto.getElnContent();

      try {
         elnContent = GzipUtils.unCompress(elnContent);
      } catch (IOException var20) {
         logger.info("ELN JSON字符串解压失败，" + var20.getMessage(), (Throwable)var20);
      }

      try (InputStream is = new ByteArrayInputStream(Optional.ofNullable(elnContent).orElse("").getBytes())) {
         CoreFileBean file = CoreElnHelper.getInsertElnContentFile(id);
         this.fileManager.upload(file, is);
         this.fileService.getDao().insert(file);
         updateEln.setElnContentFileId(file.getId());
         updateEln.setElnContent(null);
      } catch (IOException var22) {
         throw new UnexpectedException(var22);
      }

      this.getDao().update(updateEln, new String[]{"ELNCONTENT", "ELNCONTENTFILEID"});
      String dirtyRecords = dto.getDirtyRecords();
      if (!StringUtils.isEmpty(dto.getElnContent()) && !StringUtils.isEmpty(dirtyRecords)) {
         this.coreElnAtRecordService.insert(id, dirtyRecords);
      }

      String data = dto.getData();
      if (!StringUtils.isEmpty(data) && !"{}".equals(data)) {
         this.coreElnService.insertElnFieldOrWriteBack(dto);
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertElnFieldOrWriteBack(CoreElnDTO dto) {
      Map<String, JSONObject> dataMap = JSONObject.parseObject(dto.getData(), Map.class);
      Map<String, JSONObject> dataIdMap = new HashMap<>();
      if (!StringUtils.isEmpty(dto.getIdData())) {
         dataIdMap = JSONObject.parseObject(dto.getIdData(), Map.class);
      }

      if (!dataMap.isEmpty()) {
         JSONObject detailJsonObject = dataMap.get(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DETAIL"));
         JSONObject tableJsonObject = dataMap.get(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.TABLE"));
         JSONObject detailIdJsonObject = new JSONObject();
         JSONObject tableIdJsonObject = new JSONObject();
         if (!dataIdMap.isEmpty()) {
            detailIdJsonObject = dataIdMap.get(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.DETAIL"));
            tableIdJsonObject = dataIdMap.get(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.DATA.KEY.TABLE"));
         }

         Long elnId = dto.getId();
         List<CoreElnDsFieldBean> selectedDsFieldList = this.getDsFieldListByTmplId(dto.getElnTmplId());
         List<CoreElnFieldDTO> fieldList = new ArrayList<>();
         String elnContent = dto.getElnContent();

         try {
            elnContent = GzipUtils.unCompress(elnContent);
         } catch (IOException var15) {
            logger.info("ELN JSON字符串解压失败");
         }

         Set<String> bindDsNameSet = getBindDsNameList(elnContent);
         if (!ObjectUtils.isEmpty(detailJsonObject)) {
            Map<String, JSONObject> dataSourceMap = JSONObject.toJavaObject(detailJsonObject, Map.class);
            Map<String, JSONObject> dataSourceIdMap = (Map<String, JSONObject>)(!ObjectUtils.isEmpty(detailIdJsonObject) && !detailIdJsonObject.isEmpty()
               ? JSONObject.toJavaObject(detailIdJsonObject, Map.class)
               : new HashMap<>());
            dataSourceMap.forEach(
               (key, value) -> {
                  if (!ObjectUtils.isEmpty(value) && bindDsNameSet.contains(key)) {
                     Map<String, Object> dataSourceFiledMap = JSONObject.toJavaObject(value, Map.class);
                     Map<String, Object> dataSourceFiledIdMap = JSONObject.toJavaObject(dataSourceIdMap.get(key), Map.class);
                     dataSourceFiledMap.forEach(
                        (field, fieldValue) -> {
                           CoreElnDsFieldBean dsField = selectedDsFieldList.stream()
                              .filter(e -> key.equals(e.getExt$Item("elndsname")) && field.equals(e.getElnColumnName()) && "1".equals(e.getWriteBack()))
                              .findAny()
                              .orElse(null);
                           if (!ObjectUtils.isEmpty(dsField)
                              && !StringUtils.isEmpty(dsField.getTableName())
                              && !StringUtils.isEmpty(dsField.getColumnName())
                              && !ObjectUtils.isEmpty(dataSourceFiledIdMap.get(field))) {
                              CoreElnFieldDTO fieldDto = new CoreElnFieldDTO();
                              fieldDto.setBizId(dataSourceFiledIdMap.get(field));
                              fieldDto.setColumnName(dsField.getColumnName());
                              fieldDto.setTableName(dsField.getTableName());
                              fieldDto.setVal(fieldValue);
                              fieldList.add(fieldDto);
                           }
                        }
                     );
                  }
               }
            );
         }

         if (!ObjectUtils.isEmpty(tableJsonObject)) {
            Map<String, JSONArray> dataSourceMap = JSONObject.toJavaObject(tableJsonObject, Map.class);
            Map<String, JSONArray> dataSourceIdMap = (Map<String, JSONArray>)(!ObjectUtils.isEmpty(tableIdJsonObject) && !tableIdJsonObject.isEmpty()
               ? JSONObject.toJavaObject(tableIdJsonObject, Map.class)
               : new HashMap<>());
            dataSourceMap.forEach(
               (key, value) -> {
                  if (!ObjectUtils.isEmpty(value) && bindDsNameSet.contains(key)) {
                     List<Map<String, Object>> dataList = JSONArray.toJavaObject(value, List.class);
                     List<Map<String, Object>> dataIdList = JSONArray.toJavaObject(dataSourceIdMap.get(key), List.class);

                     for (int i = 0; i < dataList.size(); i++) {
                        Map<String, Object> map = dataList.get(i);
                        Map<String, Object> idMap = (Map<String, Object>)(!ObjectUtils.isEmpty(dataIdList) && dataIdList.size() > i
                           ? dataIdList.get(i)
                           : new HashMap<>());
                        map.forEach(
                           (field, fieldValue) -> {
                              CoreElnDsFieldBean dsField = selectedDsFieldList.stream()
                                 .filter(e -> key.equals(e.getExt$Item("elndsname")) && field.equals(e.getElnColumnName()) && "1".equals(e.getWriteBack()))
                                 .findAny()
                                 .orElse(null);
                              if (null == dsField
                                 && null
                                    == selectedDsFieldList.stream()
                                       .filter(e -> key.equals(e.getExt$Item("elndsname")) && field.equals(e.getElnColumnName()))
                                       .findAny()
                                       .orElse(null)) {
                                 dsField = selectedDsFieldList.stream()
                                    .filter(
                                       e -> key.equals(e.getExt$Item("elndsname"))
                                          && "1".equals(e.getExt$Item("pivotcategory"))
                                          && "1".equals(e.getWriteBack())
                                          && "value".equals(e.getPivotColumnCategory())
                                    )
                                    .findAny()
                                    .orElse(null);
                              }

                              if (!ObjectUtils.isEmpty(dsField)
                                 && !StringUtils.isEmpty(dsField.getTableName())
                                 && !StringUtils.isEmpty(dsField.getColumnName())
                                 && !ObjectUtils.isEmpty(idMap.get(field))) {
                                 CoreElnFieldDTO fieldDto = new CoreElnFieldDTO();
                                 fieldDto.setBizId(idMap.get(field));
                                 fieldDto.setColumnName(dsField.getColumnName());
                                 fieldDto.setTableName(dsField.getTableName());
                                 fieldDto.setVal(fieldValue);
                                 fieldList.add(fieldDto);
                              }
                           }
                        );
                     }
                  }
               }
            );
         }

         if (!fieldList.isEmpty()) {
            if ("realtime".equals(dto.getWriteBackCategory())) {
               List<String> tableList = fieldList.stream().map(CoreElnFieldDTO::getTableName).distinct().collect(Collectors.toList());
               tableList.forEach(
                  tableName -> {
                     List<CoreElnFieldDTO> filterFieldList = fieldList.stream()
                        .filter(fieldValue -> tableName.equals(fieldValue.getTableName()))
                        .collect(Collectors.toList());
                     List<Object> idList = filterFieldList.stream().map(CoreElnFieldDTO::getBizId).distinct().collect(Collectors.toList());
                     Set<String> updateFieldList = new HashSet<>();
                     GenericService<Persistable<?>, ?> service = ApplicationContextHelper.getServiceByTable(tableName.toUpperCase());
                     List<Persistable<?>> updateList = new ArrayList<>();
                     idList.forEach(
                        id -> {
                           List<CoreElnFieldDTO> sameIdFieldValueList = filterFieldList.stream()
                              .filter(fieldValue -> id.equals(fieldValue.getBizId()))
                              .collect(Collectors.toList());

                           try {
                              Map<String, Object> map = new HashMap<>();
                              sameIdFieldValueList.stream().forEach(v -> {
                                 Object val = v.getVal();
                                 if (StringUtils.containsIgnoreCase(ObjectUtils.toString(val), "OADate")) {
                                    val = convertFromOADate(val);
                                    String dateStr = DateTimeUtils.formatDateTime((Date)val);
                                    String uselessStr = "1899-12-30 ";
                                    if (StringUtils.startsWith(dateStr, uselessStr)) {
                                       val = StringUtils.removeStart(dateStr, uselessStr);
                                    }
                                 }

                                 map.put(v.getColumnName(), val);
                              });
                              map.put("id", id);
                              Persistable<?> row = ClassUtils.newInstance(service.getDao().getType());
                              BeanUtils.copyProperties(map, row);
                              sameIdFieldValueList.forEach(ex -> updateFieldList.add(ex.getColumnName()));
                              updateList.add(row);
                           } catch (Exception var8x) {
                              throw new UnsupportedException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TMPLS.TIP.UNKNOWN_CLASS"));
                           }
                        }
                     );
                     service.getDao().update(updateList, updateFieldList.toArray(new String[updateFieldList.size()]));
                  }
               );
            } else if ("trigger".equals(dto.getWriteBackCategory())) {
               List<CoreElnFieldBean> writeBackList = fieldList.stream().map(e -> {
                  CoreElnFieldBean writeBack = new CoreElnFieldBean();
                  writeBack.setId(ApplicationContextHelper.getNextIdentity());
                  writeBack.setVal(e.getVal() == null ? null : "" + e.getVal());
                  writeBack.setBizId(e.getBizId() == null ? null : "" + e.getBizId());
                  writeBack.setTableName(e.getTableName());
                  writeBack.setColumnName(e.getColumnName());
                  writeBack.setElnId(elnId);
                  return writeBack;
               }).collect(Collectors.toList());
               CoreElnFieldBean search = new CoreElnFieldBean();
               search.setElnId(elnId);
               this.coreElnFieldService.getDao().deleteBy(search, "ELNID");
               this.coreElnFieldService.getDao().insert(writeBackList);
            }
         }
      }
   }

   private List<CoreElnDsFieldBean> getDsFieldListByTmplId(Long tmplId) {
      List<CoreElnTmplDsBean> privateDsList = this.coreElnTmplDsService
         .selectListByFilter(SearchFilter.instance().match("ELNTMPLID", tmplId).filter(MatchPattern.SEQ), new Order[]{Order.desc("ID")});
      List<CoreElnDsBean> publicDsList = this.coreElnDsService
         .selectListByFilter(
            SearchFilter.instance().match("SCOPE", "public").filter(MatchPattern.SEQ).match("ACTIVATEDFLAG", "1").filter(MatchPattern.SEQ),
            new Order[]{Order.desc("ID")}
         );
      ArrayList<CoreElnDsFieldBean> dsFieldFilterList = new ArrayList<>();
      if (!privateDsList.isEmpty()) {
         privateDsList.forEach(e -> {
            CoreElnDsFieldBean dsFieldFilter = new CoreElnDsFieldBean();
            dsFieldFilter.setElnDsId(e.getElnDsId());
            dsFieldFilterList.add(dsFieldFilter);
         });
      }

      if (!publicDsList.isEmpty()) {
         publicDsList.forEach(e -> {
            CoreElnDsFieldBean dsFieldFilter = new CoreElnDsFieldBean();
            dsFieldFilter.setElnDsId(e.getId());
            dsFieldFilterList.add(dsFieldFilter);
         });
      }

      List<CoreElnDsFieldBean> dsFieldList = new ArrayList<>();
      privateDsList.forEach(tmplDs -> {
         List<CoreElnDsFieldBean> selectedDsFieldList = this.coreElnDsService.selectDsFieldListById(tmplDs.getElnDsId());
         selectedDsFieldList.stream().forEach(dsField -> {
            dsField.setExt$Item("elndsname", tmplDs.getExt$Item("elndsname"));
            dsField.setExt$Item("pivotcategory", tmplDs.getExt$Item("pivotcategory"));
         });
         dsFieldList.addAll(selectedDsFieldList);
      });
      publicDsList.forEach(ds -> {
         List<CoreElnDsFieldBean> selectedDsFieldList = this.coreElnDsService.selectDsFieldListById(ds.getId());
         selectedDsFieldList.stream().forEach(dsField -> {
            dsField.setExt$Item("elndsname", ds.getElnDsName());
            dsField.setExt$Item("pivotcategory", ds.getPivotCategory());
         });
         dsFieldList.addAll(selectedDsFieldList);
      });
      return dsFieldList;
   }

   public String getReduceElnContent(String rawElnContent) {
      if (StringUtils.isBlank(rawElnContent)) {
         return rawElnContent;
      } else {
         rawElnContent = rawElnContent.replaceAll("\"conditionalFormats\":\\{.+?\\}\\]\\}\\]\\},?", "");

         for (String removeElnContent : removeElnContentList) {
            rawElnContent = StringUtils.replace(rawElnContent, removeElnContent, "");
         }

         return StringUtils.replace(rawElnContent, ",}", "}");
      }
   }

   private static Date convertFromOADate(Object time) {
      double d = NumberUtils.parseDouble(StringUtils.substringsBetween(ObjectUtils.toString(time), "(", ")").get(0));
      double mantissa = d - (long)d;
      double hour = mantissa * 24.0;
      double min = (hour - (long)hour) * 60.0;
      double sec = (min - (long)min) * 60.0;
      SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
      Date baseDate = null;

      try {
         baseDate = myFormat.parse("30 12 1899");
      } catch (ParseException var14) {
         logger.info("日期转换失败");
      }

      Calendar c = Calendar.getInstance();
      c.setTime(baseDate);
      c.add(5, (int)d);
      c.add(10, (int)hour);
      c.add(12, (int)min);
      c.add(13, (int)sec);
      return c.getTime();
   }

   private static Set<String> getBindDsNameList(String elnContent) {
      Set<String> bindDsNameSet = new HashSet<>();
      String regex = "bindingPath['\"]:(.+?),";
      Matcher matcher = Pattern.compile(regex).matcher(elnContent);

      while (matcher.find()) {
         String bindDsName = StringUtils.replace(matcher.group(1), "\"", "");
         String[] split = bindDsName.split("\\.");
         if (split.length >= 2) {
            bindDsNameSet.add(split[1]);
         }
      }

      return bindDsNameSet;
   }

   protected String getContent(Long elnContentFileId, String elnContent) {
      if (elnContentFileId != null) {
         CoreFileBean file = this.fileService.selectByIdIfPresent(elnContentFileId);
         if (file == null) {
            throw new CheckedException(I18nHelper.getMessage("CORE.MODULE.ITEM.ELN.TIP.ELN_INSTANCE_FILE_NOT_EXIST"));
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
         return elnContent;
      }
   }

   static {
      removeElnContentList.add("\"backColor\":null,");
      removeElnContentList.add("\"backColor\":null");
      removeElnContentList.add("\"foreColor\":null,");
      removeElnContentList.add("\"foreColor\":null");
      removeElnContentList.add("\"font\":\"13.3333px 宋体\",");
      removeElnContentList.add("\"font\":\"13.3333px 宋体\"");
      removeElnContentList.add("\"locked\":false,");
      removeElnContentList.add("\"locked\":false");
      removeElnContentList.add("\"borderLeft\":null,");
      removeElnContentList.add("\"borderLeft\":null");
      removeElnContentList.add("\"borderTop\":null,");
      removeElnContentList.add("\"borderTop\":null");
      removeElnContentList.add("\"borderRight\":null,");
      removeElnContentList.add("\"borderRight\":null");
      removeElnContentList.add("\"borderBottom\":null,");
      removeElnContentList.add("\"borderBottom\":null");
      removeElnContentList.add("\"textIndent\":0,");
      removeElnContentList.add("\"textIndent\":0");
      removeElnContentList.add("\"imeMode\":1,");
      removeElnContentList.add("\"imeMode\":1");
      removeElnContentList.add("\"watermark\":null,");
      removeElnContentList.add("\"watermark\":null");
      removeElnContentList.add("\"cellPadding\":null,");
      removeElnContentList.add("\"cellPadding\":null");
      removeElnContentList.add("\"labelOptions\":null,");
      removeElnContentList.add("\"labelOptions\":null");
      removeElnContentList.add("\"diagonalDown\":null,");
      removeElnContentList.add("\"diagonalDown\":null");
      removeElnContentList.add("\"diagonalUp\":null,");
      removeElnContentList.add("\"diagonalUp\":null");
      removeElnContentList.add("\"wordWrap\":false,");
      removeElnContentList.add("\"wordWrap\":false");
      removeElnContentList.add("\"labelOptions\":null,");
      removeElnContentList.add("\"labelOptions\":null");
      removeElnContentList.add("\"textDecoration\":0,");
      removeElnContentList.add("\"textDecoration\":0");
      removeElnContentList.add("\"cellPadding\":\"0 0 0 0\",");
      removeElnContentList.add("\"cellPadding\":\"0 0 0 0\"");
      removeElnContentList.add("\"watermark\":\"\",");
      removeElnContentList.add("\"watermark\":\"\"");
   }
}
