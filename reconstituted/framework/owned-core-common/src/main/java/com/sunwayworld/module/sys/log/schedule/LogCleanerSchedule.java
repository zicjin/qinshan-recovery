/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.schedule;

import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.mybatis.mapper.DaoMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.module.sys.log.mapper.CoreLogMapper;
import com.sunwayworld.module.sys.log.service.CoreLogService;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="sunway.logging.auto-clean", name={"enabled"}, havingValue="true")
public class LogCleanerSchedule {
    @Autowired
    private CoreLogService coreLogService;
    @Autowired
    private CoreLogMapper coreLogMapper;
    @Autowired
    private DaoMapper daoMapper;
    @Value(value="${sunway.logging.auto-clean.duration:60}")
    private Long storageDuration;

    @Scheduled(cron=" 0 0 2 * * ?")
    public void scheduleAutoClean() {
        LocalDateTime dateTime = LocalDateTime.now();
        Date serverLogTime = Date.from(dateTime.minusDays(this.storageDuration).atZone(ZoneId.systemDefault()).toInstant());
        SearchFilter filter = SearchFilter.instance().match("CREATEDTIME", serverLogTime).filter(MatchPattern.TLOE);
        MapperParameter parameter = new MapperParameter();
        parameter.setFilter(filter);
        parameter.setSimpleColumns("T.ID");
        List<Map<String, Object>> logList = this.coreLogMapper.selectByCondition(parameter);
        ListChunkIterator<Map<String, Object>> logChunkIterator = ListChunkIterator.of(logList, 999);
        while (logChunkIterator.hasNext()) {
            this.daoMapper.batchDeleteById(this.coreLogService.getDao().getEntityContext(), logChunkIterator.nextChunk().stream().map(log -> log.get("ID")).collect(Collectors.toList()));
        }
    }
}
