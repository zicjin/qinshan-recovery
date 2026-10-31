package com.sunwayworld.emc.module.mdm.equipt.equipt.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquipCertBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.resource.EmcEquipCertResource;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquipCertService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@RestController
@GikamBean
public class EmcEquipCertResourceImpl extends EmcEquiptResourceImpl implements EmcEquipCertResource{
    
    @Autowired
    private EmcEquipCertService emcEquipCertService;
    
    @Log(value = "新增仪器授权", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/equipcerts", method = RequestMethod.POST)
    public void insertEquipCert(@PathVariable String id, RestJsonWrapperBean wrapper) {
        emcEquipCertService.insertEquipCert(id, wrapper);
    }

    @Log(value = "通过仪器查询授权记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/equipcerts/queries", method = RequestMethod.POST)
    public Page<EmcEquipCertBean> selectEquipCertPagationByEquipId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return emcEquipCertService.selectPaginationByFilter(SearchFilter.instance().match("EQUIPTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "删除仪器授权", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/equipcerts", method = RequestMethod.DELETE)
    public void deleteEquipCert(@PathVariable String id, RestJsonWrapperBean wrapper) {
        emcEquipCertService.delete(wrapper);
    }
    
}
