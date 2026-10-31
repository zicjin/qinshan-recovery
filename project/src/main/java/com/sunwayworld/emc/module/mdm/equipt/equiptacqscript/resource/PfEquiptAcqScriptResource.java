package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasResultBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2023年09月08日
 */
public interface PfEquiptAcqScriptResource extends EmcEquiptAcqScriptResource {

    @RequestMapping(value = "/eas-results/queries", method = RequestMethod.POST)
    Page<EmcEasResultBean> selectEasResultPagination(RestJsonWrapperBean wrapper);
}
