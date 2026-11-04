/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.topology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.topology.bean.CoreTopologyBean;
import com.sunwayworld.cloud.module.topology.persistent.dao.CoreTopologyDao;
import com.sunwayworld.cloud.module.topology.service.CoreTopologyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.CaseInsensitiveLinkedMap;
import com.sunwayworld.framework.mybatis.mapper.GlobalMapper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreTopologyServiceImpl
implements CoreTopologyService {
    @Autowired
    private CoreTopologyDao topologyDao;
    @Autowired
    private GlobalMapper globalMapper;

    @Override
    public CoreTopologyDao getDao() {
        return this.topologyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreTopologyBean topology = wrapper.parseUnique(CoreTopologyBean.class);
        topology.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(topology);
        return topology.getId();
    }

    @Override
    public String selectRevisedJsonByCode(String topologyCode) {
        CoreTopologyBean filter = new CoreTopologyBean();
        filter.setCode(topologyCode);
        String json = this.getDao().selectColumn(filter, "JSON", String.class);
        if (StringUtils.isEmpty(json)) {
            return json;
        }
        JSONObject jo = JSON.parseObject(json);
        JSONArray pens = jo.getJSONArray("pens");
        int size = pens.size();
        Map<String, String> paramMap = ServletUtils.getCurrentRequestParamMap();
        for (int i = 0; i < size; ++i) {
            JSONObject pen = pens.getJSONObject(i);
            JSONArray dataArray = pen.getJSONArray("data");
            if (dataArray == null) continue;
            int dataArraySize = dataArray.size();
            for (int j = 0; j < dataArraySize; ++j) {
                String sql;
                Map<String, Object> map;
                String value;
                JSONObject data = dataArray.getJSONObject(j);
                if (!"config".equals(data.getString("key")) || StringUtils.isBlank(value = data.getString("value")) || null == (map = this.globalMapper.selectOne(sql = StringUtils.replaceBetween(value, "#{", "}", paramMap)))) continue;
                CaseInsensitiveLinkedMap<Object> targetMap = new CaseInsensitiveLinkedMap<Object>();
                targetMap.putAll(map);
                targetMap.entrySet().forEach(e -> {
                    if (null == e.getValue()) {
                        e.setValue("");
                    }
                });
                pen.entrySet().forEach(p -> {
                    if (targetMap.containsKey(p.getKey())) {
                        p.setValue(targetMap.get(p.getKey()));
                    }
                });
            }
        }
        return jo.toJSONString();
    }
}
