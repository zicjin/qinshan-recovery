package com.sunwayworld.framework.support.selectable.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.sunwayworld.framework.database.sql.SqlHelper;
import com.sunwayworld.framework.exception.HackingDataException;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean.TargetTableFilterData;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.domain.AbstractAuditable;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.domain.Persistable;
import com.sunwayworld.framework.support.domain.Suspendable;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface GenericSelectableService<T extends Persistable<ID>, ID extends Serializable> {
   <D extends GenericDao<T, ID>> D getDao();

   default List<T> selectSelectableList() {
      MapperParameter parameter = new MapperParameter();
      Map<String, String> requestParamMap = ServletUtils.getCurrentRequestParamMap();
      if (!requestParamMap.isEmpty()) {
         SearchFilter filter = SearchFilter.instance();
         requestParamMap.forEach((k, v) -> {
            if (StringUtils.startsWith(k, "filter_")) {
               parameter.put(k, v);
            } else if ("target_filter".equals(k)) {
               Map<String, String> taMap = JSONObject.parseObject(StringUtils.decodeURL(v), new TypeReference<Map<String, String>>() {}, new Feature[0]);
               TargetTableFilterData filterData = new TargetTableFilterData();
               filterData.setType(taMap.get("type"));
               filterData.setTargetTable(taMap.get("targetTable"));
               filterData.setTargetMatchColumn(Objects.toString(taMap.get("targetMatchColumn"), "ID"));
               filterData.setThisMatchColumn(Objects.toString(taMap.get("thisMatchColumn"), "ID"));
               String filterStr = taMap.get("filter");
               if (!StringUtils.isEmpty(filterStr)) {
                  JSONArray array = JSONObject.parseArray(filterStr);
                  int i = 0;

                  for (int j = array.size(); i < j; i++) {
                     JSONObject jsonObject = (JSONObject)array.get(i);
                     filterData.addTargetFilter(jsonObject.getString("targetFilterColumn"), jsonObject.getString("targetFilterValue"));
                  }
               }

               if (SqlHelper.isRiskySqlSegment(filterData.getTargetTable())) {
                  throw new HackingDataException("targetTable");
               }

               if (SqlHelper.isRiskySqlSegment(filterData.getTargetMatchColumn())) {
                  throw new HackingDataException("targetMatchColumn");
               }

               if (SqlHelper.isRiskySqlSegment(filterData.getThisMatchColumn())) {
                  throw new HackingDataException("thisMatchColumn");
               }

               filterData.getTargetFilter().keySet().forEach(e -> {
                  if (SqlHelper.isRiskySqlSegment(e)) {
                     throw new HackingDataException("targetFilterColumn");
                  }
               });
               parameter.put("targetFilter", filterData);
            } else {
               MatchPattern match = Arrays.stream(MatchPattern.values()).filter(m -> StringUtils.endsWith(k, "_" + m.name())).findAny().orElse(null);
               String key = k;
               if (match == null) {
                  match = MatchPattern.EQ;
               } else {
                  key = StringUtils.removeEnd(k, "_" + match.name());
                  if (MatchPattern.IN.equals(match)) {
                     match = MatchPattern.OR;
                  }
               }

               if (StringUtils.contains(v, ",")) {
                  filter.match(key, Arrays.asList(StringUtils.split(v, ","))).filter(match);
               } else {
                  filter.match(key, v).filter(match);
               }
            }
         });
         parameter.setFilter(filter);
      }

      Class<T> type = this.getDao().getType();
      parameter.setSelectableQueries();
      if (AbstractAuditable.class.isAssignableFrom(this.getDao().getType())) {
         parameter.setProcessStatus("PROCESSSTATUS", ProcessStatus.DONE);
      }

      if (Suspendable.class.isAssignableFrom(type)) {
         parameter.setSuspendedFlag("0");
      }

      if (Activatable.class.isAssignableFrom(type)) {
         parameter.setActivatedFlag("1");
      }

      return this.getDao()
         .selectByCondition(parameter)
         .stream()
         .map(m -> PersistableHelper.mapToPersistable((Map<String, Object>)m, type))
         .collect(Collectors.toList());
   }
}
