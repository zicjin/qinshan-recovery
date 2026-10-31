package com.sunwayworld.emc.module.bp.result.equiptcurve.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.equiptcurve.resource.EmcEquiptCurveResource;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@LogModule("仪器曲线表")
@RestController
@GikamBean
public class EmcEquiptCurveResourceImpl implements EmcEquiptCurveResource {

    @Autowired
    private EmcEquiptCurveService emcEquiptCurveService;

    @Override
    public EmcEquiptCurveService getService() {
        return emcEquiptCurveService;
    }

    @Log(value = "新增仪器曲线表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "复制仪器曲线", type = LogType.INSERT)
    @RequestMapping(value = "/action/copy", method = RequestMethod.POST)
    public void copy(RestJsonWrapperBean wrapper) {
        getService().copy(wrapper);
    }

    @Override
    @Log(value = "批量录入仪器曲线单位", type = LogType.INSERT)
    @RequestMapping(value = "/{ids}/batchunit", method = RequestMethod.POST)
    public void batchUnit(@PathVariable String ids, RestJsonWrapperBean wrapper) {
        getService().batchUnit(ids,wrapper);
    }

    @Override
    @Log(value = "新增仪器曲线标液配置", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/equipt-curves-standard", method = RequestMethod.POST)
    public Long insertStandard(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return getService().insertStandard(id,wrapper);
    }

    @Override
    @Log(value = "根据仪器id关联新增仪器曲线", type = LogType.INSERT)
    @RequestMapping(value = "/{equiptId}/add", method = RequestMethod.POST)
    public Long insertByEquiptId(@PathVariable Long equiptId, RestJsonWrapperBean wrapper) {
        return getService().insertByEquiptId(equiptId,wrapper);
    }
}
