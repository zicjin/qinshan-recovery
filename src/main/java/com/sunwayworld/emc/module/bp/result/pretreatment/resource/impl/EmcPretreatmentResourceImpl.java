package com.sunwayworld.emc.module.bp.result.pretreatment.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcExtractMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.bean.EmcPurifyMethodBean;
import com.sunwayworld.emc.module.bp.result.pretreatment.resource.EmcPretreatmentResource;
import com.sunwayworld.emc.module.bp.result.pretreatment.service.EmcPretreatmentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhengxy@sunwayworld.com 2023年08月14日
 */
@LogModule(value = "前处理")
@RestController
@GikamBean
public class EmcPretreatmentResourceImpl implements EmcPretreatmentResource {

    @Autowired
    private EmcPretreatmentService pretreatmentService;

    @Override
    public EmcPretreatmentService getService() {
        return pretreatmentService;
    }

    @Override
    @Log(value = "查询提取方法", type = LogType.SELECT)
    @RequestMapping(value = "/extract-methods/queries", method = RequestMethod.POST)
    public Page<EmcExtractMethodBean> queryExtractMethodByPagination(RestJsonWrapperBean wrapper) {
        return getService().queryExtractMethodByPagination(wrapper);
    }

    @Override
    @Log(value = "添加提取方法", type = LogType.INSERT)
    @RequestMapping(value = "/extract-methods/action/insert", method = RequestMethod.POST)
    public void addExtractMethod(RestJsonWrapperBean wrapper) {
        getService().addExtractMethod(wrapper);
    }

    @Override
    @Log(value = "删除提取方法", type = LogType.DELETE)
    @RequestMapping(value = "/extract-methods/action/delete", method = RequestMethod.DELETE)
    public void deleteExtractMethod(RestJsonWrapperBean wrapper) {
        getService().deleteExtractMethod(wrapper);
    }

    @Override
    @Log(value = "查询净化方法", type = LogType.UPDATE)
    @RequestMapping(value = "/purify-methods/queries", method = RequestMethod.POST)
    public Page<EmcPurifyMethodBean> queryPurifyMethodByPagination(RestJsonWrapperBean wrapper) {
        return getService().queryPurifyMethodByPagination(wrapper);
    }

    @Override
    @Log(value = "添加净化方法", type = LogType.INSERT)
    @RequestMapping(value = "/purify-methods/action/insert", method = RequestMethod.POST)
    public void addPurifyMethod(RestJsonWrapperBean wrapper) {
        getService().addPurifyMethod(wrapper);
    }

    @Override
    @Log(value = "删除净化方法", type = LogType.DELETE)
    @RequestMapping(value = "/purify-methods/action/delete", method = RequestMethod.DELETE)
    public void deletePurifyMethod(RestJsonWrapperBean wrapper) {
        getService().deletePurifyMethod(wrapper);
    }

    @Override
    @Log(value = "完成前处理", type = LogType.UPDATE)
    @RequestMapping(value = "/action/complete", method = RequestMethod.POST)
    public void complete(RestJsonWrapperBean wrapper) {
        getService().complete(wrapper);
    }

    @Override
    @Log(value = "复制到", type = LogType.UPDATE)
    @RequestMapping(value = "/action/copy-to", method = RequestMethod.POST)
    public void copyTo(RestJsonWrapperBean wrapper) {
        getService().copyTo(wrapper);
    }
}
