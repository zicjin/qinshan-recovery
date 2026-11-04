/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.config.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.item.file.bean.CoreFileBean;
import com.sunwayworld.module.item.file.manager.CoreFileManager;
import com.sunwayworld.module.item.file.service.CoreFileService;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigBean;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigDTO;
import com.sunwayworld.module.sys.config.bean.CoreSystemConfigHelpManualDTO;
import com.sunwayworld.module.sys.config.dao.CoreSystemConfigDao;
import com.sunwayworld.module.sys.config.service.CoreSystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSystemConfigServiceImpl
implements CoreSystemConfigService {
    private static final String SYSTEM_CONFIG_ID = "system-config";
    private static final String FILE_TARGETID = "T_CORE_SYSTEM_CONFIG$system-config";
    @Autowired
    private CoreSystemConfigDao coreSystemConfigDao;
    @Autowired
    private CoreFileService coreFileService;
    @Autowired
    @Lazy
    private CoreFileManager fileManager;

    @Override
    public CoreSystemConfigDao getDao() {
        return this.coreSystemConfigDao;
    }

    @Override
    public CoreSystemConfigDTO selectSystemConfig() {
        CoreSystemConfigBean systemConfig = (CoreSystemConfigBean)this.getDao().selectById(SYSTEM_CONFIG_ID);
        if (null == systemConfig.getSpeedTestPeriod() || 0L == systemConfig.getSpeedTestPeriod()) {
            systemConfig.setSpeedTestPeriod(30L);
        }
        CoreSystemConfigDTO systemConfigDTO = new CoreSystemConfigDTO();
        BeanUtils.copyProperties((Object)systemConfig, (Object)systemConfigDTO, new String[0]);
        CoreFileBean helpManualFile = (CoreFileBean)this.coreFileService.selectFirstByFilter(SearchFilter.instance().match("TARGETID", FILE_TARGETID).filter(MatchPattern.EQ).match("BIZCATEGORY", "HELPMANNUAL").filter(MatchPattern.EQ), Order.desc("ID"));
        if (helpManualFile != null) {
            CoreSystemConfigHelpManualDTO helpManual = new CoreSystemConfigHelpManualDTO();
            helpManual.setFileId(helpManualFile.getId());
            helpManual.setFileName(helpManualFile.getName());
            helpManual.setDownloadUrl(this.fileManager.getDownloadUrl(helpManualFile));
            systemConfigDTO.setHelpManual(helpManual);
        }
        return systemConfigDTO;
    }

    @Override
    public CoreSystemConfigBean selectDetail(String id) {
        return this.selectSystemConfig();
    }
}
