/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.config.service.impl;

import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpConfigCompareDTO;
import com.sunwayworld.cloud.module.lcdp.config.bean.LcdpGlobalConfigBean;
import com.sunwayworld.cloud.module.lcdp.config.dao.LcdpGlobalConfigDao;
import com.sunwayworld.cloud.module.lcdp.config.service.LcdpGlobalConfigService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class LcdpGlobalConfigServiceImpl
implements LcdpGlobalConfigService {
    @Autowired
    private LcdpGlobalConfigDao lcdpGlobalConfigDao;

    @Override
    public LcdpGlobalConfigDao getDao() {
        return this.lcdpGlobalConfigDao;
    }

    @Override
    public LcdpGlobalConfigBean selectConfigContent(String configCode) {
        LcdpGlobalConfigBean lcdpGlobalConfigBean = (LcdpGlobalConfigBean)this.selectFirstByFilter(SearchFilter.instance().match("CONFIGCODE", configCode).filter(MatchPattern.SEQ).match("EFFECTFLAG", "1").filter(MatchPattern.SEQ), new Order[0]);
        return lcdpGlobalConfigBean == null ? new LcdpGlobalConfigBean() : lcdpGlobalConfigBean;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public void submit(RestJsonWrapperBean wrapper) {
        LcdpGlobalConfigBean submitConfig = (LcdpGlobalConfigBean)wrapper.parseUnique(this.getDao().getType());
        this.submit(submitConfig);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    public void submit(LcdpGlobalConfigBean submitConfig) {
        List<LcdpGlobalConfigBean> effectiveConfigList = this.selectListByFilter(SearchFilter.instance().match("CONFIGCODE", submitConfig.getConfigCode()).filter(MatchPattern.SEQ).match("EFFECTFLAG", "1").filter(MatchPattern.SEQ), new Order[0]);
        LcdpGlobalConfigBean version = (LcdpGlobalConfigBean)this.selectFirstByFilter(SearchFilter.instance().match("CONFIGCODE", submitConfig.getConfigCode()).filter(MatchPattern.SEQ), Order.desc("VERSION"));
        effectiveConfigList.forEach(config -> config.setEffectFlag("0"));
        this.getDao().update(effectiveConfigList, new String[]{"EFFECTFLAG"});
        submitConfig.setId(ApplicationContextHelper.getNextIdentity());
        submitConfig.setEffectFlag("1");
        submitConfig.setVersion(ObjectUtils.isEmpty(version.getVersion()) ? 1L : version.getVersion() + 1L);
        this.getDao().insert(submitConfig);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.ACTIVATE")
    public void activate(RestJsonWrapperBean wrapper) {
        LcdpGlobalConfigBean activateConfig = (LcdpGlobalConfigBean)wrapper.parseUnique(this.getDao().getType());
        activateConfig = (LcdpGlobalConfigBean)this.selectById(activateConfig.getId());
        List<LcdpGlobalConfigBean> effectiveConfig = this.selectListByFilter(SearchFilter.instance().match("CONFIGCODE", activateConfig.getConfigCode()).filter(MatchPattern.SEQ).match("EFFECTFLAG", "1").filter(MatchPattern.SEQ), new Order[0]);
        effectiveConfig.forEach(config -> config.setEffectFlag("0"));
        this.updateIfChanged(effectiveConfig);
        activateConfig.setEffectFlag("1");
        this.updateIfChanged(activateConfig);
    }

    @Override
    public LcdpConfigCompareDTO compare(RestJsonWrapperBean wrapper) {
        Long id = Long.valueOf(wrapper.getParamMap().get("resourceId"));
        LcdpConfigCompareDTO compareDTO = new LcdpConfigCompareDTO();
        LcdpGlobalConfigBean currentConfig = (LcdpGlobalConfigBean)this.selectById(id);
        LcdpGlobalConfigBean condition = new LcdpGlobalConfigBean();
        condition.setConfigCode(currentConfig.getConfigCode());
        List<LcdpGlobalConfigBean> historyConfigList = this.getDao().selectList(condition, ArrayUtils.asList("VERSION", "ID"), new Order[0]);
        LcdpGlobalConfigBean previousConfig = historyConfigList.stream().filter(config -> config.getVersion() == currentConfig.getVersion() - 1L).findFirst().orElse(null);
        compareDTO.setCurrentVersionHistory(currentConfig);
        compareDTO.setHistoryList(historyConfigList);
        if (!ObjectUtils.isEmpty(previousConfig)) {
            compareDTO.setPreviousVersionHistory((LcdpGlobalConfigBean)this.selectById(previousConfig.getId()));
        }
        return compareDTO;
    }
}
