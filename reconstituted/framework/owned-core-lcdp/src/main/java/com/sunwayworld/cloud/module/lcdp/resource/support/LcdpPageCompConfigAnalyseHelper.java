package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LcdpPageCompConfigAnalyseHelper {
   public static Map<String, List<String>> analyseConfig(List<String> configList) {
      List<String> requiredDataTypeList = Arrays.asList("select");
      List<JSONObject> jsonObjectList = configList.stream().map(config -> JSON.parseObject(config)).collect(Collectors.toList());
      return jsonObjectList.stream()
         .filter(jo -> null != jo.getString("type") && requiredDataTypeList.contains(jo.getString("type").toLowerCase()))
         .collect(Collectors.groupingBy(jo -> jo.getString("type").toLowerCase(), Collectors.mapping(j -> j.getString("category"), Collectors.toList())));
   }
}
