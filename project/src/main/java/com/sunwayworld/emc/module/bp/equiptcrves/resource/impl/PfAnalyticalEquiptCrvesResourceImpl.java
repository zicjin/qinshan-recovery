package com.sunwayworld.emc.module.bp.equiptcrves.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.equiptcrves.resource.PfAnalyticalEquiptCrvesResource;
import com.sunwayworld.emc.module.bp.equiptcrves.service.PfAnalyticalEquiptCrvesService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author licm@sunwayworld.com 2021年10月24日
 */
@LogModule(value = "equipt-crves")
@RestController
@GikamBean
public class PfAnalyticalEquiptCrvesResourceImpl implements PfAnalyticalEquiptCrvesResource {

    @Autowired
    private PfAnalyticalEquiptCrvesService pfAnalyticalEquiptCrvesService;

    @Override
    public PfAnalyticalEquiptCrvesService getService() {
        return pfAnalyticalEquiptCrvesService;
    }

    @Log(value = "分析仪器曲线添加", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
	public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
	}

    @Log(value = "分析仪器现在曲线复制", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/copy-crves", method = RequestMethod.POST)
    public void copyCrves(RestJsonWrapperBean wrapper) {
        this.getService().copyCrves(wrapper);
    }

    @Log(value = "分析仪器配置曲线复制", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/copy-config-crves", method = RequestMethod.POST)
    public void copyConfigCrves(RestJsonWrapperBean wrapper) {
        this.getService().copyConfigCrves(wrapper);
    }
    
    @Log(value = "分析仪器现在曲线根据条数复制", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/copy-now-crves", method = RequestMethod.POST)
    public void copyNowCrves(RestJsonWrapperBean wrapper) {
        this.getService().copyNowCrves(wrapper);
    }

    @Log(value = "分析仪器配置曲线添加", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/insert_config_curve", method = RequestMethod.POST)
	public void insertConfigCurve(RestJsonWrapperBean wrapper) {
    	 this.getService().insertConfigCurve(wrapper);
	}

    @Log(value = "查询批下的测试名称", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/select-run-testid", method = RequestMethod.POST)
	public String selectRunTestId(RestJsonWrapperBean wrapper) {
        return getService().selectRunTestId(wrapper);
	}

    @Log(value = "比较日期", type = LogType.UPDATE)
	@Override
    @RequestMapping(value = "/compare-end-date", method = RequestMethod.POST)
	public String CompareEndDate(RestJsonWrapperBean wrapper) {
        return getService().CompareEndDate(wrapper);
	}

    @Override
    @Log(value = "设置默认曲线", type = LogType.INSERT)
    @RequestMapping(value = "/set-default-curve", method = RequestMethod.POST)
    public String setDefaultCurve(RestJsonWrapperBean wrapper) {
    	return getService().setDefaultCurve(wrapper);
    }
    
    @Log(value = "查询批下的分析项组", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/select-run-itemgroup", method = RequestMethod.POST)
	public String selectItemGroup(RestJsonWrapperBean wrapper) {
        return getService().selectItemGroup(wrapper);
	}
    
    @Log(value = "改变solId同步明细信息", type = LogType.INSERT)
	@Override
    @RequestMapping(value = "/change-solid", method = RequestMethod.POST)
	public void changSolId(RestJsonWrapperBean wrapper) {
         getService().changSolId(wrapper);
	}

}
