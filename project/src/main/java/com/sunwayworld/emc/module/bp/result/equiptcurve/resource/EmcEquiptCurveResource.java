package com.sunwayworld.emc.module.bp.result.equiptcurve.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/bp/result/equipt-curves")
public interface EmcEquiptCurveResource extends GenericResource<EmcEquiptCurveService, EmcEquiptCurveBean, Long>, GenericChoosableResource<EmcEquiptCurveService, EmcEquiptCurveBean, Long> {

    void copy(RestJsonWrapperBean wrapper);

    void batchUnit(String ids,RestJsonWrapperBean wrapper);

    Long insertStandard(Long id,RestJsonWrapperBean wrapper);

//    void changeSelectStatus(RestJsonWrapperBean wrapper);

    Long insertByEquiptId(Long equiptId,RestJsonWrapperBean wrapper);
}
