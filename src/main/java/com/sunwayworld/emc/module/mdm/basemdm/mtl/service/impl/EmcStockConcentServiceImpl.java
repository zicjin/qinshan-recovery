package com.sunwayworld.emc.module.mdm.basemdm.mtl.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcStockConcentDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcStockConcentService;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.NumberUtils;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:47:32
 */
@Repository
public class EmcStockConcentServiceImpl implements EmcStockConcentService {

    @Autowired
    private EmcStockConcentDao emcStockConcentDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcStockConcentDao getDao() {
        return emcStockConcentDao;
    }

    @Override
    public List<EmcStockConcentBean> selectLimitList(List<EmcStockConcentBean> filter) {

        MapperParameter parameter = new MapperParameter();

        parameter.setFilter(filter, "BATCHNO", "ITEMID");

        List<Map<String, Object>> mapList = getDao().selectLimitByCondition(parameter);

        if (mapList.isEmpty()) {
            return Collections.emptyList();
        }

        return mapList.stream().map(m -> PersistableHelper.mapToPersistable(m, getDao().getType())).collect(Collectors.toList());
    }

    @Override
    public void changeResult(RestJsonWrapperBean wrapper) {
        List<EmcStockConcentBean> stockConcentList = wrapper.parse(EmcStockConcentBean.class);
        stockConcentList.forEach(e->{
            e.setLowLimit(NumberUtils.rounding(e.getLowLimit(),e.getRevisionRule()));
            e.setHighLimit(NumberUtils.rounding(e.getHighLimit(),e.getRevisionRule()));
        });
        getDao().update(stockConcentList);
    }
}
