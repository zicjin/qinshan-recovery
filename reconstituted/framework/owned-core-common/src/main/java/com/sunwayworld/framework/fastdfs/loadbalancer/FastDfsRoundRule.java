/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.loadbalancer;

import com.sunwayworld.framework.fastdfs.loadbalancer.FastDfsRule;
import com.sunwayworld.framework.fastdfs.properties.FastDfsProperties;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FastDfsRoundRule
implements FastDfsRule {
    private FastDfsProperties dfsProperties;
    private Map<String, AtomicInteger> counter = new ConcurrentHashMap<String, AtomicInteger>();

    public FastDfsRoundRule(FastDfsProperties dfsProperties) {
        this.dfsProperties = dfsProperties;
    }

    @Override
    public FastDfsProperties getFastDfsProperties() {
        return this.dfsProperties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    @Override
    public String getNextFastDfsUrl(String group) {
        AtomicInteger atomicInteger = this.counter.get(group = Optional.ofNullable(group).orElse("group"));
        if (atomicInteger == null) {
            Class<FastDfsRoundRule> clazz = FastDfsRoundRule.class;
            // MONITORENTER : com.sunwayworld.framework.fastdfs.loadbalancer.FastDfsRoundRule.class
            atomicInteger = this.counter.get(group);
            if (atomicInteger == null) {
                atomicInteger = new AtomicInteger(0);
                this.counter.put(group, atomicInteger);
            }
            // MONITOREXIT : clazz
        }
        int next = atomicInteger.addAndGet(1);
        Map<String, List<String>> map = ObjectUtils.clone(this.getFastDfsProperties().getGroups());
        List<String> urlList = map.get(group);
        if (urlList != null) return urlList.get(next % urlList.size());
        urlList = Optional.ofNullable(map.get("group")).orElseThrow(() -> new NullPointerException("Fast dfs requires default group (group)."));
        return urlList.get(next % urlList.size());
    }
}
