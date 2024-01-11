package com.sunwayworld.emc.module.mdm.purchapply.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchDetailBean;
import com.sunwayworld.emc.module.mdm.purchapply.resource.EmcPurchApplyResource;
import com.sunwayworld.emc.module.mdm.purchapply.service.EmcPurchApplyService;
import com.sunwayworld.emc.module.mdm.purchapply.service.EmcPurchDetailService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@LogModule("采购申请")
@RestController
@GikamBean
public class EmcPurchApplyResourceImpl implements EmcPurchApplyResource {

	@Autowired
	private EmcPurchApplyService dinganPurchApplyService;
	
	@Autowired
	private EmcPurchDetailService dinganPurchDetailService;
	
	@Override
	public EmcPurchApplyService getService() {
		return dinganPurchApplyService;
	}

    @Log(value = "查询采购明细", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/{id}/queries", method = RequestMethod.POST)
	public Page<EmcPurchDetailBean> selectPurchDetail(@PathVariable String id, RestJsonWrapperBean wrapper) {
		return dinganPurchDetailService.selectPaginationByFilter(SearchFilter.instance().match("PURCHID", id).filter(MatchPattern.EQ), wrapper);
	}
    
    @Log(value = "新增采购明细", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/insert", method = RequestMethod.POST)
    public Long insertPurchDetail(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return dinganPurchDetailService.insertPurchDetail(id, wrapper);
    }

    @Log(value = "删除采购明细", type = LogType.DELETE)
	@Override
	@RequestMapping(value = "/deletePurchDetail", method = RequestMethod.DELETE)
	public void deletePurchDetail(RestJsonWrapperBean wrapper) {
    	dinganPurchDetailService.deletePurchDetail(wrapper);
	}
}
