/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.design.studio.support;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.design.studio.bean.CoreStudioFieldDTO;
import com.sunwayworld.module.design.studio.bean.CoreStudioGridDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CoreStudioPageHelper {
    private static final String ID_KEY = "id";
    private static final String TYPE_KEY = "type";
    private static final String TYPE_GRID_KEY = "grid";
    private static final String DBTABLE_KEY = "dbTable";
    private static final String COLUMNS_KEY = "columns";
    private static final String FIELDS_KEY = "fields";
    private static final String HIDDENFIELDS_KEY = "hiddenFields";
    private static final String FIELD_KEY = "field";
    private static final String TITLE_KEY = "title";

    public static List<CoreStudioFieldDTO> getFieldList(String pageJson) {
        return CoreStudioPageHelper.getFieldList(JSONObject.parseObject(pageJson));
    }

    public static List<CoreStudioGridDTO> getPageGridList(String pageJson) {
        if (StringUtils.isEmpty(pageJson)) {
            return CollectionUtils.emptyList();
        }
        return CoreStudioPageHelper.getPageGridList(JSONObject.parseObject(pageJson));
    }

    private static List<CoreStudioFieldDTO> getFieldList(JSONObject jsonObject) {
        if (jsonObject.containsKey(DBTABLE_KEY)) {
            String table = jsonObject.getString(DBTABLE_KEY);
            return CoreStudioPageHelper.getFieldList(table, jsonObject);
        }
        ArrayList<CoreStudioFieldDTO> fieldList = new ArrayList<CoreStudioFieldDTO>();
        Set<String> keySet = jsonObject.keySet();
        for (String key : keySet) {
            Object object = jsonObject.get(key);
            if (object == null) continue;
            if (JSONObject.class.equals(object.getClass())) {
                fieldList.addAll(CoreStudioPageHelper.getFieldList((JSONObject)object));
                continue;
            }
            if (!JSONArray.class.equals(object.getClass())) continue;
            JSONArray ja = (JSONArray)object;
            int j = ja.size();
            for (int i = 0; i < j; ++i) {
                Object value = ja.get(i);
                if (value == null || !JSONObject.class.equals(value.getClass())) continue;
                fieldList.addAll(CoreStudioPageHelper.getFieldList((JSONObject)value));
            }
        }
        return fieldList;
    }

    private static List<CoreStudioFieldDTO> getFieldList(String table, JSONObject jsonObject) {
        ArrayList<CoreStudioFieldDTO> fieldList = new ArrayList<CoreStudioFieldDTO>();
        for (String key : jsonObject.keySet()) {
            Object object = jsonObject.get(key);
            if (object == null) continue;
            if (JSONObject.class.equals(object.getClass())) {
                fieldList.addAll(CoreStudioPageHelper.getFieldList(table, (JSONObject)object));
                continue;
            }
            if (!JSONArray.class.equals(object.getClass())) continue;
            JSONArray ja = (JSONArray)object;
            if (key.equals(COLUMNS_KEY) || key.equals(FIELDS_KEY) || key.equals(HIDDENFIELDS_KEY)) {
                if (StringUtils.isBlank(table)) continue;
                boolean form = key.equals(FIELDS_KEY) || key.equals(HIDDENFIELDS_KEY);
                ArrayList<CoreStudioFieldDTO> toFieldList = new ArrayList<CoreStudioFieldDTO>();
                int j = ja.size();
                for (int i = 0; i < j; ++i) {
                    JSONObject jo = ja.getJSONObject(i);
                    CoreStudioFieldDTO studioField = jo.toJavaObject(CoreStudioFieldDTO.class);
                    studioField.setForm(form);
                    studioField.setJson(jo.toJSONString());
                    toFieldList.add(studioField);
                }
                toFieldList.forEach(f -> {
                    f.setTable(table);
                    if (!f.isDefault()) {
                        f.setField(f.getFieldConfig().getField());
                    }
                });
                fieldList.addAll(toFieldList);
                continue;
            }
            int j = ja.size();
            for (int i = 0; i < j; ++i) {
                Object value = ja.get(i);
                if (value == null || !JSONObject.class.equals(value.getClass())) continue;
                fieldList.addAll(CoreStudioPageHelper.getFieldList(table, (JSONObject)value));
            }
        }
        return fieldList;
    }

    private static List<CoreStudioGridDTO> getPageGridList(JSONObject jsonObject) {
        String pageId = (String)jsonObject.keySet().stream().findFirst().get();
        ArrayList<CoreStudioGridDTO> gridList = new ArrayList<CoreStudioGridDTO>();
        JSONObject pageJson = (JSONObject)jsonObject.get(pageId);
        for (String compKey : pageJson.keySet()) {
            JSONObject compJson;
            Object compObj = pageJson.get(compKey);
            if (compObj == null || !JSONObject.class.equals(compObj.getClass()) || !TYPE_GRID_KEY.equals((compJson = (JSONObject)compObj).getString(TYPE_KEY))) continue;
            CoreStudioGridDTO grid = new CoreStudioGridDTO();
            String gridId = compJson.getString(ID_KEY);
            grid.setGridId(gridId);
            grid.setPageId(pageId);
            JSONArray columnArray = (JSONArray)compJson.get(COLUMNS_KEY);
            int j = columnArray.size();
            for (int i = 0; i < j; ++i) {
                JSONObject column = (JSONObject)columnArray.get(i);
                if (!column.containsKey(FIELD_KEY) || !column.containsKey(TITLE_KEY)) continue;
                String field = column.getString(FIELD_KEY);
                String title = column.getString(TITLE_KEY);
                grid.addColumn(CoreStudioGridDTO.CoreStudioGridColumnDTO.of(field, title, "0"));
            }
            JSONArray hiddenColumnArray = (JSONArray)compJson.get(HIDDENFIELDS_KEY);
            int j2 = hiddenColumnArray.size();
            for (int i = 0; i < j2; ++i) {
                JSONObject column = (JSONObject)hiddenColumnArray.get(i);
                if (!column.containsKey(FIELD_KEY) || !column.containsKey(TITLE_KEY)) continue;
                String field = column.getString(FIELD_KEY);
                String title = column.getString(TITLE_KEY);
                grid.addColumn(CoreStudioGridDTO.CoreStudioGridColumnDTO.of(field, title, "1"));
            }
            gridList.add(grid);
        }
        return gridList;
    }
}
