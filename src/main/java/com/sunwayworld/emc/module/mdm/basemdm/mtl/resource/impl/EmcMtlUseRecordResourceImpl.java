package com.sunwayworld.emc.module.mdm.basemdm.mtl.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.resource.EmcMtlUseRecordResource;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlUseRecordService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("材料使用记录")
@RestController
@GikamBean
public class EmcMtlUseRecordResourceImpl implements EmcMtlUseRecordResource {

    @Autowired
    private EmcMtlUseRecordService emcMtlUseRecordService;

    @Override
    public EmcMtlUseRecordService getService() {
        return emcMtlUseRecordService;
    }

    @Log(value = "新增材料使用记录", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "查询材料领用记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/stocks/logs/queries")
	public Page<EmcStockLogBean> selectStockLogInApply(RestJsonWrapperBean wrapper) {
		return getService().selectStockLogInApply(wrapper);
	}

    @Log(value = "查询使用记录", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/use-record/queries")
    public Page<EmcMtlUseRecordBean> selectMtlUseRecordPagationByApplyId(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return getService().selectPaginationByFilter(SearchFilter.instance().match("APPLYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Log(value = "使用记录", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{id}/action/apply", method = RequestMethod.PUT)
	public void apply(@PathVariable Long id, RestJsonWrapperBean wrapper) {
    	getService().apply(id,wrapper);
	}

}
