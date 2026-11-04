package com.sunwayworld.cloud.module.item.pvconf.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewConfDTO;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewGridConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfDTO;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfDTO;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewConfDao;
import com.sunwayworld.cloud.module.item.pvconf.persistent.mapper.CoreProcessViewGridConfMapper;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewConfService;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewGridConfService;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewMasterConfService;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewSubConfService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.MybatisPageHelper;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.DateTimeUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreProcessViewConfServiceImpl implements CoreProcessViewConfService {
   @Autowired
   private CoreProcessViewMasterConfService processViewMasterConfService;
   @Autowired
   private CoreProcessViewSubConfService processViewSubConfService;
   @Autowired
   private CoreProcessViewGridConfService processViewGridConfService;
   @Autowired
   private CoreProcessViewConfDao processViewConfDao;
   @Autowired
   private GlobalMapper globalMapper;
   @Autowired
   private CoreProcessViewGridConfMapper processViewGridConfMapper;

   public CoreProcessViewConfDao getDao() {
      return this.processViewConfDao;
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   public Long insert(RestJsonWrapperBean wrapper) {
      CoreProcessViewConfBean corePvConf = wrapper.parseUnique(CoreProcessViewConfBean.class);
      corePvConf.setId(ApplicationContextHelper.getNextIdentity());
      this.getDao().insert(corePvConf);
      return corePvConf.getId();
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activate(RestJsonWrapperBean wrapper) {
      List<CoreProcessViewConfBean> updateConfList = wrapper.parse(CoreProcessViewConfBean.class);
      CoreProcessViewConfBean pvConf = updateConfList.get(0);
      List<CoreProcessViewConfBean> confList = this.selectAll();
      confList.forEach(e -> {
         if (ObjectUtils.equals(e.getId(), pvConf.getId())) {
            this.setActivateProperties(e, true);
         } else {
            this.setActivateProperties(e, false);
         }
      });
      this.getDao().update(confList, new String[]{"ACTIVATEDFLAG", "ACTIVATEDBYID", "ACTIVATEDBYNAME", "ACTIVATEDTIME"});
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertMasterConf(RestJsonWrapperBean wrapper) {
      return this.processViewMasterConfService.insert(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteMasterConf(RestJsonWrapperBean wrapper) {
      this.processViewMasterConfService.delete(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void swapMasterConf(String string, Long firstId, Long secondId) {
      this.processViewMasterConfService.swap("ORDERNO", firstId, secondId);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public Long insertSubConf(RestJsonWrapperBean wrapper) {
      return this.processViewSubConfService.insert(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DELETE)
   @Audit("AUDIT.DELETE")
   @Override
   public void deleteSubConf(RestJsonWrapperBean wrapper) {
      this.processViewSubConfService.delete(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.SWAP)
   @Audit("AUDIT.SWAP")
   @Override
   public void swapSubConf(String string, Long firstId, Long secondId) {
      this.processViewSubConfService.swap("ORDERNO", firstId, secondId);
   }

   @Override
   public Page<CoreProcessViewMasterConfBean> selectMasterConfPagationByPvConf(Long id, RestJsonWrapperBean wrapper) {
      return this.processViewMasterConfService.selectPaginationByFilter(SearchFilter.instance().match("CONFID", id).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public CoreProcessViewMasterConfBean selectMasterConfDetail(Long masterConfId) {
      return this.processViewMasterConfService.selectDetail(masterConfId);
   }

   @Override
   public Page<CoreProcessViewSubConfBean> selectSubConfPagationByMasterConf(Long masterConfId, RestJsonWrapperBean wrapper) {
      return this.processViewSubConfService
         .selectPaginationByFilter(SearchFilter.instance().match("MASTERCONFID", masterConfId).filter(MatchPattern.EQ), wrapper);
   }

   @Override
   public CoreProcessViewSubConfBean selectSubConfDetail(Long subConfId) {
      return this.processViewSubConfService.selectDetail(subConfId);
   }

   @Override
   public String selectGridSql(Long sourceId, String sourceType) {
      if ("master".equals(sourceType)) {
         CoreProcessViewMasterConfBean masterConf = this.processViewMasterConfService.selectDetail(sourceId);
         return masterConf.getGridSql();
      } else {
         CoreProcessViewSubConfBean subConf = this.processViewSubConfService.selectDetail(sourceId);
         return subConf.getGridSql();
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.INSERT)
   @Audit("AUDIT.INSERT")
   @Override
   public void insertGridConf(RestJsonWrapperBean wrapper) {
      this.processViewGridConfService.insert(wrapper);
   }

   @Override
   public Page<CoreProcessViewGridConfBean> selectGridConfPagination(RestJsonWrapperBean wrapper) {
      return this.processViewGridConfService.selectPagination(wrapper);
   }

   @Override
   public CoreProcessViewConfDTO selectProcessView(RestJsonWrapperBean wrapper) {
      CoreProcessViewConfBean pvConfFilter = new CoreProcessViewConfBean();
      pvConfFilter.setActivatedFlag("1");
      List<CoreProcessViewConfBean> pvConfList = this.selectList(pvConfFilter, new Order[0]);
      if (pvConfList.isEmpty()) {
         throw new CheckedException("CORE.MODULE.ITEM.PVCONFS.EXCEPTION.NO_ACTIVATED_CONF");
      } else {
         Map<String, String> param = wrapper.getParamMap();
         CoreProcessViewConfBean pvConf = pvConfList.get(0);
         Long confId = pvConf.getId();
         CoreProcessViewConfDTO processViewConfDTO = new CoreProcessViewConfDTO();
         processViewConfDTO.setId(confId);
         processViewConfDTO.setConfName(pvConf.getConfName());
         List<CoreProcessViewMasterConfBean> masterConfList = new ArrayList<>();
         if ("1".equals(pvConf.getMasterFilter())) {
            String masterFilterConfSql = pvConf.getMasterFilterConf();
            if (!StringUtils.isEmpty(masterFilterConfSql)) {
               List<Map<String, Object>> mapList = this.globalMapper.selectList(this.replaceSqlByParam(masterFilterConfSql, param));
               masterConfList = mapList.stream()
                  .map(p -> PersistableHelper.mapToPersistable((Map<String, Object>)p, CoreProcessViewMasterConfBean.class))
                  .collect(Collectors.toList());
            }
         } else {
            masterConfList = this.processViewMasterConfService.getDao().selectListByOneColumnValue(confId, "CONFID", Order.asc("ORDERNO"), Order.asc("ID"));
         }

         List<Long> masterConfIdList = masterConfList.stream().map(e -> e.getId()).collect(Collectors.toList());
         List<CoreProcessViewSubConfBean> allSubConfList = this.processViewSubConfService
            .getDao()
            .selectListByOneColumnValues(masterConfIdList, "MASTERCONFID", Order.asc("ORDERNO"), Order.asc("ID"));
         List<CoreProcessViewMasterConfDTO> masterConfDTOList = new ArrayList<>();

         for (CoreProcessViewMasterConfBean masterConf : masterConfList) {
            CoreProcessViewMasterConfDTO processViewMasterConfDTO = new CoreProcessViewMasterConfDTO();
            BeanUtils.copyProperties(masterConf, processViewMasterConfDTO);
            String statusSql = masterConf.getStatusSql();
            if (!StringUtils.isEmpty(statusSql)) {
               Map<String, Object> statusMap = this.globalMapper.selectOne(this.replaceSqlByParam(statusSql, param));
               String status = (String)this.getFirstValue(statusMap, "notstart");
               processViewMasterConfDTO.setStatus(status);
            } else {
               processViewMasterConfDTO.setStatus("notstart");
            }

            if ("1".equals(masterConf.getDataSearch())) {
               String totalSql = masterConf.getTotalSql();
               String doneSql = masterConf.getDoneSql();
               if (!StringUtils.isEmpty(totalSql) && !StringUtils.isEmpty(doneSql)) {
                  Map<String, Object> totalMap = this.globalMapper.selectOne(this.replaceSqlByParam(totalSql, param));
                  Map<String, Object> doneMap = this.globalMapper.selectOne(this.replaceSqlByParam(doneSql, param));
                  Long totalCount = ConvertUtils.convert(this.getFirstValue(totalMap, 0L), Long.class);
                  Long doneCount = ConvertUtils.convert(this.getFirstValue(doneMap, 0L), Long.class);
                  Long undoneCount = totalCount - doneCount;
                  processViewMasterConfDTO.setTotalCount(totalCount);
                  processViewMasterConfDTO.setDoneCount(doneCount);
                  processViewMasterConfDTO.setUndoneCount(undoneCount);
                  if (totalCount > 0L) {
                     double doneNum = NumberUtils.toDouble(ArithUtils.mul(ArithUtils.div(doneCount.longValue(), totalCount.longValue()), 100.0), 2);
                     String donePercent = doneNum + "%";
                     String undonePercent = 100.0 - doneNum + "%";
                     processViewMasterConfDTO.setDonePercent(donePercent);
                     processViewMasterConfDTO.setUndonePercent(undonePercent);
                  }
               }
            }

            List<CoreProcessViewSubConfBean> subConfList = new ArrayList<>();
            if ("1".equals(masterConf.getSubFilter())) {
               String subFilterConfSql = masterConf.getSubFilterConf();
               if (!StringUtils.isEmpty(subFilterConfSql)) {
                  List<Map<String, Object>> mapList = this.globalMapper.selectList(this.replaceSqlByParam(subFilterConfSql, param));
                  subConfList = mapList.stream()
                     .map(p -> PersistableHelper.mapToPersistable((Map<String, Object>)p, CoreProcessViewSubConfBean.class))
                     .collect(Collectors.toList());
               }
            } else {
               subConfList = allSubConfList.stream().filter(f -> ObjectUtils.equals(masterConf.getId(), f.getMasterConfId())).collect(Collectors.toList());
            }

            List<CoreProcessViewSubConfDTO> subConfDTOList = new ArrayList<>();

            for (CoreProcessViewSubConfBean subConf : subConfList) {
               CoreProcessViewSubConfDTO processViewSubConfDTO = new CoreProcessViewSubConfDTO();
               BeanUtils.copyProperties(subConf, processViewSubConfDTO);
               if ("1".equals(subConf.getDataSearch())) {
                  String totalSql = subConf.getTotalSql();
                  String doneSql = subConf.getDoneSql();
                  if (!StringUtils.isEmpty(totalSql) && !StringUtils.isEmpty(doneSql)) {
                     Map<String, Object> totalMap = this.globalMapper.selectOne(this.replaceSqlByParam(totalSql, param));
                     Map<String, Object> doneMap = this.globalMapper.selectOne(this.replaceSqlByParam(doneSql, param));
                     Long totalCount = (Long)this.getFirstValue(totalMap, 0L);
                     Long doneCount = (Long)this.getFirstValue(doneMap, 0L);
                     Long undoneCount = totalCount - doneCount;
                     processViewSubConfDTO.setTotalCount(totalCount);
                     processViewSubConfDTO.setDoneCount(doneCount);
                     processViewSubConfDTO.setUndoneCount(undoneCount);
                     if (totalCount > 0L) {
                        double doneNum = NumberUtils.toDouble(ArithUtils.mul(ArithUtils.div(doneCount.longValue(), totalCount.longValue()), 100.0), 2);
                        String donePercent = doneNum + "%";
                        String undonePercent = 100.0 - doneNum + "%";
                        processViewSubConfDTO.setDonePercent(donePercent);
                        processViewSubConfDTO.setUndonePercent(undonePercent);
                     }
                  }
               }

               subConfDTOList.add(processViewSubConfDTO);
            }

            processViewMasterConfDTO.setSubConfList(subConfDTOList);
            masterConfDTOList.add(processViewMasterConfDTO);
         }

         processViewConfDTO.setMasterConfList(masterConfDTOList);
         return processViewConfDTO;
      }
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.ACTIVATE)
   @Audit("AUDIT.ACTIVATE")
   @Override
   public void activateGridConf(RestJsonWrapperBean wrapper) {
      this.processViewGridConfService.activate(wrapper);
   }

   @Transactional
   @AuditTrailEntry(AuditTrailType.DEACTIVATE)
   @Audit("AUDIT.DEACTIVATE")
   @Override
   public void deactivateGridConf(RestJsonWrapperBean wrapper) {
      this.processViewGridConfService.deactivate(wrapper);
   }

   @Override
   public List<CoreProcessViewGridConfBean> selectGridConfColumnListByConf(Long sourceId, RestJsonWrapperBean wrapper) {
      CoreProcessViewGridConfBean filter = new CoreProcessViewGridConfBean();
      filter.setSourceId(sourceId);
      filter.setActivatedFlag("1");
      return this.processViewGridConfService.getDao().selectList(filter, Order.asc("ID"));
   }

   @Override
   public Page<Map<String, Object>> selectGridDataPagination(Long sourceId, String sourceType, RestJsonWrapperBean wrapper) {
      String sql = null;
      if ("master".equals(sourceType)) {
         CoreProcessViewMasterConfBean masterConf = this.processViewMasterConfService.selectDetail(sourceId);
         sql = masterConf.getGridSql();
      } else {
         CoreProcessViewSubConfBean subConf = this.processViewSubConfService.selectDetail(sourceId);
         sql = subConf.getGridSql();
      }

      if (StringUtils.isEmpty(sql)) {
         return new Pagination<>();
      } else {
         List<CoreProcessViewGridConfBean> gridConfList = this.processViewGridConfService.getDao().selectListByOneColumnValue(sourceId, "SOURCEID");
         if (gridConfList.isEmpty()) {
            return new Pagination<>();
         } else {
            MapperParameter parameter = wrapper.extractMapFilter();
            String filterJson = wrapper.getParamValue("f");
            if (!StringUtils.isEmpty(filterJson)) {
               Map<String, String> param = JSONObject.parseObject(filterJson, new TypeReference<Map<String, String>>() {}, new Feature[0]);
               sql = this.replaceSqlByParam(sql, param);
            }

            if (SqlHelper.hasOrderSegment(sql)) {
               parameter.put("sql", SqlHelper.removeEndingOrderIfPresent(sql));
               String[] orders = StringUtils.split(SqlHelper.getOrderSegment(sql), ",");

               for (String order : orders) {
                  String trimedOrder = StringUtils.trim(order);
                  if (StringUtils.endsWithIgnoreCase(trimedOrder, "ASC")) {
                     parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "ASC")), "ASC");
                  } else if (StringUtils.endsWithIgnoreCase(order.trim(), "DESC")) {
                     parameter.setOrderParam(StringUtils.trim(StringUtils.removeEndIgnoreCase(trimedOrder, "DESC")), "DESC");
                  } else {
                     parameter.setOrderParam(trimedOrder, "ASC");
                  }
               }
            } else {
               parameter.put("sql", sql);
               parameter.setOrderParam(gridConfList.get(0).getField(), "ASC");
            }

            PageRowBounds rowBounds = wrapper.extractPageRowBounds();
            Page<Map<String, Object>> page = MybatisPageHelper.get(rowBounds, () -> this.processViewGridConfMapper.selectSqlDataByCondition(parameter));

            for (Map<String, Object> map : page.getRows()) {
               CollectionUtils.lowerCaseKey(map);
               gridConfList.stream().filter(c -> "DATE".equals(c.getType()) || "DATETIME".equals(c.getType())).forEach(c -> {
                  String key = c.getField().toLowerCase();
                  Object value = map.get(key);
                  if (value != null) {
                     if ("DATE".equals(c.getType())) {
                        map.replace(key, DateTimeUtils.formatLocalDate(ConvertUtils.convert(value, LocalDate.class)));
                     } else if ("DATETIME".equals(c.getType())) {
                        map.replace(key, DateTimeUtils.formatLocalDateTime(ConvertUtils.convert(value, LocalDateTime.class)));
                     }
                  }
               });
            }

            return page;
         }
      }
   }

   private String replaceSqlByParam(String sql, Map<String, String> param) {
      for (Entry<String, String> entry : param.entrySet()) {
         sql = StringUtils.replace(sql, "#{" + entry.getKey() + "}", entry.getValue());
      }

      if (StringUtils.contains(sql, "${loginUserId}")) {
         sql = StringUtils.replace(sql, "${loginUserId}", LocalContextHelper.getLoginUserId());
      }

      if (StringUtils.contains(sql, "${loginOrgId}")) {
         sql = StringUtils.replace(sql, "${loginOrgId}", LocalContextHelper.getLoginOrgId());
      }

      if (StringUtils.contains(sql, "${loginRoleId}")) {
         sql = StringUtils.replace(sql, "${loginRoleId}", LocalContextHelper.getLoginRoleId());
      }

      return sql;
   }

   private Object getFirstValue(Map<String, Object> map, Object defaultValue) {
      if (map == null) {
         return defaultValue;
      } else {
         Iterator var4 = map.entrySet().iterator();
         if (var4.hasNext()) {
            Entry<String, Object> entry = (Entry<String, Object>)var4.next();
            return entry.getValue();
         } else {
            return defaultValue;
         }
      }
   }
}
