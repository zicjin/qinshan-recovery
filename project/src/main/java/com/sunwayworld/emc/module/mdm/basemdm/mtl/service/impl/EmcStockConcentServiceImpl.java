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
import com.sunwayworld.framework.utils.ArithUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

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
    public void stockConcentCalculate(RestJsonWrapperBean wrapper) {
        List<EmcStockConcentBean> stockConcentList = wrapper.parse(EmcStockConcentBean.class);
        for (EmcStockConcentBean stockConcent : stockConcentList) {
            String stdValue = stockConcent.getStdValue();
            String uncertainty = stockConcent.getUncertainty();
            String uncertaintyPercent = stockConcent.getUncertaintyPercent();
            String revisionRule = stockConcent.getRevisionRule();
            if (!StringUtils.isEmpty(stdValue) && StringUtils.isEmpty(uncertainty) && !StringUtils.isEmpty(uncertaintyPercent)) {
                uncertainty = ArithUtils.div(ArithUtils.mul(stdValue, uncertaintyPercent), 100.0) + "";
            }
            String lowLimit = stdValue;
            String highLimit = stdValue;
            if (!StringUtils.isEmpty(stdValue) && !StringUtils.isEmpty(uncertainty)) {
                lowLimit = ArithUtils.sub(stdValue, uncertainty) + "";
                highLimit = ArithUtils.add(stdValue, uncertainty) + "";
            }
            lowLimit = NumberUtils.rounding(lowLimit, revisionRule);
            highLimit = NumberUtils.rounding(highLimit, revisionRule);
            stockConcent.setLowLimit(lowLimit);
            stockConcent.setHighLimit(highLimit);
        }
        getDao().update(stockConcentList, "lowLimit", "highLimit");
    }
}
