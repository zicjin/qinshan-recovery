package com.sunwayworld.emc.module.bp.result.curve.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcEquiptCurveConfigBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月23日
 */
@RequestMapping("/secure/emc/module/bp/result/curves")
public interface PfCurveResource extends EmcCurveResource {

    Page<EmcEquiptCurveConfigBean> selectEquiptCurveConfigPagationByCurve(RestJsonWrapperBean wrapper);

    Long insertEquiptCurveconfig(Long id, RestJsonWrapperBean wrapper);

    void deleteEquiptCurveconfig(RestJsonWrapperBean wrapper);

    Long insertEquiptCurve(RestJsonWrapperBean wrapper);

    void copyEquiptCurveConfig(RestJsonWrapperBean wrapper);
}
