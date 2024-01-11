package com.sunwayworld.emc.module.mdm.basemdm.unit.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.dao.EmcUnitDao;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitConvertService;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月9日
 */

@Repository
public class EmcUnitServiceImpl implements EmcUnitService {

    @Autowired
    private EmcUnitDao emcUnitDao;

    @Autowired
    private EmcUnitConvertService emcUnitConvertService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcUnitDao getDao() {
        return emcUnitDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        EmcUnitBean emcUnitBean = wrapper.parseUnique(EmcUnitBean.class);
        emcUnitBean.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));

        getDao().insert(emcUnitBean);

        /*List<EmcUnitBean> unitList = this.selectListByFilter(SearchFilter.instance().match("UNITCATEGORY", emcUnitBean.getUnitCategory()).filter(MatchPattern.EQ));

        List<EmcUnitConvertBean> unitConvertList = new ArrayList<>();
        for (EmcUnitBean emcUnit : unitList) {
            EmcUnitConvertBean emcUnitConvertBean = new EmcUnitConvertBean();
            emcUnitConvertBean.setId(ApplicationContextHelper.getNextIdentity());
            emcUnitConvertBean.setConvertUnitId(emcUnit.getId());
            emcUnitConvertBean.setUnitId(emcUnitBean.getId());
            emcUnitConvertBean.setConvertFactor(1d);

            unitConvertList.add(emcUnitConvertBean);

            if (!emcUnit.getId().equals(emcUnitBean.getId())) {
                EmcUnitConvertBean unitConvert = new EmcUnitConvertBean();
                unitConvert.setId(ApplicationContextHelper.getNextIdentity());
                unitConvert.setConvertUnitId(emcUnitBean.getId());
                unitConvert.setUnitId(emcUnit.getId());
                unitConvert.setConvertFactor(1d);
                unitConvertList.add(unitConvert);
            }
        }

        emcUnitConvertService.getDao().insert(unitConvertList);*/
        return emcUnitBean.getId();
    }

    @Override
    public Page<EmcUnitConvertBean> selectConvertPaginationByUnit(String id, RestJsonWrapperBean wrapper) {
        return emcUnitConvertService.selectPaginationByFilter(SearchFilter.instance().match("UNITID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void insertUnitConvert(RestJsonWrapperBean wrapper) {
        List<EmcUnitConvertBean> convertList = wrapper.parse(EmcUnitConvertBean.class);
        convertList.forEach(c -> c.setId(ApplicationContextHelper.getNextIdentity()));
        emcUnitConvertService.getDao().insert(convertList);
    }

    @Override
    public void deleteUnitConvert(RestJsonWrapperBean wrapper) {
        emcUnitConvertService.delete(wrapper);
    }

}
