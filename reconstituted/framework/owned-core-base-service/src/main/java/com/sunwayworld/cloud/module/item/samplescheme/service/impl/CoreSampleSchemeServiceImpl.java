/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.samplescheme.service.impl;

import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleQtyBean;
import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeBean;
import com.sunwayworld.cloud.module.item.samplescheme.bean.CoreSampleSchemeDTO;
import com.sunwayworld.cloud.module.item.samplescheme.persistent.dao.CoreSampleSchemeDao;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleQtyCodeService;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleQtyService;
import com.sunwayworld.cloud.module.item.samplescheme.service.CoreSampleSchemeService;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreSampleSchemeServiceImpl
implements CoreSampleSchemeService {
    @Autowired
    private CoreSampleSchemeDao coreSampleSchemeDao;
    @Autowired
    private CoreSampleQtyCodeService coreSampleQtyCodeService;
    @Autowired
    private CoreSampleQtyService coreSampleQtyService;

    @Override
    public CoreSampleSchemeDao getDao() {
        return this.coreSampleSchemeDao;
    }

    @Override
    public CoreSampleSchemeDTO selectSampleScheme(Long batchQty, String inspection, Double aql) {
        CoreSampleSchemeDTO sampleSchemeDTO = new CoreSampleSchemeDTO();
        sampleSchemeDTO.setAql(aql);
        String sampleQtyCode = this.coreSampleQtyCodeService.selectSampleQtyCode(batchQty, inspection);
        sampleSchemeDTO.setSampleQtyCode(sampleQtyCode);
        if (sampleQtyCode == null) {
            return sampleSchemeDTO;
        }
        Long sampleQty = ((CoreSampleQtyBean)this.coreSampleQtyService.selectById(sampleQtyCode)).getSampleQty();
        sampleSchemeDTO.setSampleQty(sampleQty);
        List sampleSchemeList = this.selectListByFilter(SearchFilter.instance().match("SAMPLEQTYCODE", sampleQtyCode).filter(MatchPattern.EQ).match("AQL", aql).filter(MatchPattern.NEQ), new Order[0]);
        if (!sampleSchemeList.isEmpty()) {
            CoreSampleSchemeBean sampleScheme = (CoreSampleSchemeBean)sampleSchemeList.get(0);
            sampleSchemeDTO.setAcceptQty(sampleScheme.getAcceptQty());
            sampleSchemeDTO.setRefuseQty(sampleScheme.getRefuseQty());
        }
        return sampleSchemeDTO;
    }
}
