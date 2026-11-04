/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpModulePageCompBean;
import com.sunwayworld.cloud.module.lcdp.resource.bean.LcdpResourceHistoryBean;
import com.sunwayworld.cloud.module.lcdp.resource.persistent.dao.LcdpModulePageCompDao;
import com.sunwayworld.cloud.module.lcdp.resource.service.LcdpModulePageCompService;
import com.sunwayworld.framework.data.ListChunkIterator;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpModulePageCompServiceImpl
implements LcdpModulePageCompService {
    @Autowired
    private LcdpModulePageCompDao lcdpModulePageCompDao;

    @Override
    public LcdpModulePageCompDao getDao() {
        return this.lcdpModulePageCompDao;
    }

    @Override
    @Transactional
    public void dealGridSroll(List<LcdpResourceHistoryBean> resourceHistoryList) {
        List resourceHistortyIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
        List<LcdpModulePageCompBean> gridCompList = this.selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistortyIdList).filter(MatchPattern.OR).match("type", "Grid").filter(MatchPattern.EQ), new Order[0]);
        ArrayList dealPageCompList = new ArrayList();
        gridCompList.forEach(grid -> {
            String gridConfigStr = StringUtils.isEmpty(grid.getConfigExtend()) ? grid.getConfig() : grid.getConfig() + grid.getConfigExtend();
            JSONObject gridConfig = JSON.parseObject(gridConfigStr);
            if (!ObjectUtils.isEmpty(gridConfig.getString("scroll"))) {
                gridConfig.remove("scroll");
                this.cutConfig(gridConfig.toJSONString(), (LcdpModulePageCompBean)grid);
                dealPageCompList.add(grid);
            }
        });
        ListChunkIterator chunkIterator = ListChunkIterator.of(dealPageCompList, 200);
        while (chunkIterator.hasNext()) {
            List chunkItemList = chunkIterator.nextChunk();
            this.getDao().update(chunkItemList, new String[]{"CONFIG", "CONFIGEXTEND"});
        }
    }

    @Override
    @Transactional
    public void dealGridBadgeCount(List<LcdpResourceHistoryBean> resourceHistoryList) {
        List resourceHistortyIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
        List<LcdpModulePageCompBean> gridCompList = this.selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistortyIdList).filter(MatchPattern.OR).match("type", "Grid").filter(MatchPattern.EQ), new Order[0]);
        ArrayList dealPageCompList = new ArrayList();
        gridCompList.forEach(grid -> {
            String gridConfigStr = StringUtils.isEmpty(grid.getConfigExtend()) ? grid.getConfig() : grid.getConfig() + grid.getConfigExtend();
            JSONObject gridConfig = JSON.parseObject(gridConfigStr);
            if (!ObjectUtils.isEmpty(gridConfig.getString("badgeOverflowCount"))) {
                gridConfig.remove("badgeOverflowCount");
                this.cutConfig(gridConfig.toJSONString(), (LcdpModulePageCompBean)grid);
                dealPageCompList.add(grid);
            }
        });
        ListChunkIterator chunkIterator = ListChunkIterator.of(dealPageCompList, 400);
        while (chunkIterator.hasNext()) {
            List chunkItemList = chunkIterator.nextChunk();
            this.getDao().update(chunkItemList, new String[]{"CONFIG", "CONFIGEXTEND"});
        }
    }

    @Override
    @Transactional
    public void dealGridShowCheckedNum(List<LcdpResourceHistoryBean> resourceHistoryList) {
        List resourceHistortyIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
        List<LcdpModulePageCompBean> gridCompList = this.selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistortyIdList).filter(MatchPattern.OR).match("type", "Grid").filter(MatchPattern.EQ), new Order[0]);
        ArrayList dealPageCompList = new ArrayList();
        gridCompList.forEach(grid -> {
            String gridConfigStr = StringUtils.isEmpty(grid.getConfigExtend()) ? grid.getConfig() : grid.getConfig() + grid.getConfigExtend();
            JSONObject gridConfig = JSON.parseObject(gridConfigStr);
            if (!ObjectUtils.isEmpty(gridConfig.getString("showCheckedNum"))) {
                gridConfig.put("showCheckedNum", (Object)true);
                this.cutConfig(gridConfig.toJSONString(), (LcdpModulePageCompBean)grid);
                dealPageCompList.add(grid);
            }
        });
        ListChunkIterator chunkIterator = ListChunkIterator.of(dealPageCompList, 400);
        while (chunkIterator.hasNext()) {
            List chunkItemList = chunkIterator.nextChunk();
            this.getDao().update(chunkItemList, new String[]{"CONFIG", "CONFIGEXTEND"});
        }
    }

    @Override
    public void dealHistoryGridCheckContinuous(List<LcdpResourceHistoryBean> resourceHistoryList) {
        List resourceHistortyIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
        List<LcdpModulePageCompBean> gridCompList = this.selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistortyIdList).filter(MatchPattern.OR).match("type", "Grid").filter(MatchPattern.EQ), new Order[0]);
        ArrayList dealPageCompList = new ArrayList();
        gridCompList.forEach(grid -> {
            String gridConfigStr = StringUtils.isEmpty(grid.getConfigExtend()) ? grid.getConfig() : grid.getConfig() + grid.getConfigExtend();
            JSONObject gridConfig = JSON.parseObject(gridConfigStr);
            if (!ObjectUtils.isEmpty(gridConfig.getString("checkContinuous"))) {
                gridConfig.remove("checkContinuous");
                this.cutConfig(gridConfig.toJSONString(), (LcdpModulePageCompBean)grid);
                dealPageCompList.add(grid);
            }
        });
        ListChunkIterator chunkIterator = ListChunkIterator.of(dealPageCompList, 400);
        while (chunkIterator.hasNext()) {
            List chunkItemList = chunkIterator.nextChunk();
            this.getDao().update(chunkItemList, new String[]{"CONFIG", "CONFIGEXTEND"});
        }
    }

    @Override
    @Transactional
    public void dealHistoryShowCheckedNum2Null(List<LcdpResourceHistoryBean> resourceHistoryList) {
        List resourceHistortyIdList = resourceHistoryList.stream().map(LcdpResourceHistoryBean::getId).collect(Collectors.toList());
        List<LcdpModulePageCompBean> gridCompList = this.selectListByFilter(SearchFilter.instance().match("modulePageHistoryId", resourceHistortyIdList).filter(MatchPattern.OR).match("type", "Grid").filter(MatchPattern.EQ), new Order[0]);
        ArrayList dealPageCompList = new ArrayList();
        gridCompList.forEach(grid -> {
            String gridConfigStr = StringUtils.isEmpty(grid.getConfigExtend()) ? grid.getConfig() : grid.getConfig() + grid.getConfigExtend();
            JSONObject gridConfig = JSON.parseObject(gridConfigStr);
            if (!ObjectUtils.isEmpty(gridConfig.getString("showCheckedNum"))) {
                gridConfig.remove("showCheckedNum");
                this.cutConfig(gridConfig.toJSONString(), (LcdpModulePageCompBean)grid);
                dealPageCompList.add(grid);
            }
        });
        ListChunkIterator chunkIterator = ListChunkIterator.of(dealPageCompList, 400);
        while (chunkIterator.hasNext()) {
            List chunkItemList = chunkIterator.nextChunk();
            this.getDao().update(chunkItemList, new String[]{"CONFIG", "CONFIGEXTEND"});
        }
    }

    private void cutConfig(String sourceConfig, LcdpModulePageCompBean page) {
        if (sourceConfig.length() > 3000) {
            page.setConfig(sourceConfig.substring(0, 3000));
            page.setConfigExtend(sourceConfig.substring(3000));
        } else {
            page.setConfig(sourceConfig);
            page.setConfigExtend(null);
        }
    }
}
