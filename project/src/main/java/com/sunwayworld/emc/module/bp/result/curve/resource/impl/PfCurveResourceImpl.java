package com.sunwayworld.emc.module.bp.result.curve.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcEquiptCurveConfigBean;
import com.sunwayworld.emc.module.bp.result.curve.resource.PfCurveResource;
import com.sunwayworld.emc.module.bp.result.curve.service.EmcEquiptCurveConfigService;
import com.sunwayworld.emc.module.bp.result.curve.service.PfCurveService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月23日
 */
@RestController
@GikamBean
public class PfCurveResourceImpl extends EmcCurveResourceImpl implements PfCurveResource {

    @Autowired
    private PfCurveService curveService;

    @Autowired
    private EmcEquiptCurveConfigService emcEquiptCurveConfigService;

    @Log(value = "查询仪器曲线配置", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/equipt-curves-config/queries")
    public Page<EmcEquiptCurveConfigBean> selectEquiptCurveConfigPagationByCurve(RestJsonWrapperBean wrapper) {
        return emcEquiptCurveConfigService.selectPagination(wrapper);
    }

    @Log(value = "新增仪器曲线配置", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/{id}/equipt-curves-config", method = RequestMethod.POST)
    public Long insertEquiptCurveconfig(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return emcEquiptCurveConfigService.insert(id, wrapper);
    }

    @Log(value = "删除仪器曲线配置", type = LogType.DELETE)
    @Override
    @RequestMapping(value = "/equipt-curves-config", method = RequestMethod.DELETE)
    public void deleteEquiptCurveconfig(RestJsonWrapperBean wrapper) {
        emcEquiptCurveConfigService.delete(wrapper);
    }

    @Log(value = "新增仪器曲线", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/equipt-curves", method = RequestMethod.POST)
    public Long insertEquiptCurve(RestJsonWrapperBean wrapper) {
        return curveService.insertEquiptCurve(wrapper);
    }

    @Override
    @Log(value = "复制仪器曲线配置", type = LogType.INSERT)
    @RequestMapping(value = "/copy/equipt-curves-config", method = RequestMethod.POST)
    public void copyEquiptCurveConfig(RestJsonWrapperBean wrapper) {
        emcEquiptCurveConfigService.copyEquiptCurveConfig(wrapper);
    }
}
