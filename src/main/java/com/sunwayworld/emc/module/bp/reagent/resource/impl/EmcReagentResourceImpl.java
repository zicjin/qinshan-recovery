package com.sunwayworld.emc.module.bp.reagent.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.emc.module.bp.reagent.resource.EmcReagentResource;
import com.sunwayworld.emc.module.bp.reagent.service.EmcReagentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("一般试剂配制")
@RestController
@GikamBean
public class EmcReagentResourceImpl implements EmcReagentResource {
    @Autowired
    private EmcReagentService reagentService;

    @Override
    public EmcReagentService getService() {
        return reagentService;
    }

    @Log(value = "新增一般试剂配制", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
    
    @Override
    @Log(value = "查询试剂组分", type = LogType.SELECT)
    @RequestMapping(value = "/{id}/compositions/queries", method = RequestMethod.POST)
    public Page<EmcReagentCompositionBean> selectReagentCompositionPagination(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return reagentService.selectReagentCompositionPagination(id, wrapper);
    }

    @Override
    @Log(value = "新增试剂组分", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/compositions", method = RequestMethod.POST)
    public Long insertComposition(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return reagentService.insertComposition(id, wrapper);
    }

    @Override
    @Log(value = "删除试剂组分", type = LogType.DELETE)
    @RequestMapping(value = "/{id}/compositions", method = RequestMethod.DELETE)
    public void deleteComposition(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        reagentService.deleteComposition(id, wrapper);
    }

    @Override
    @Log(value = "复制一般试剂的配置", type = LogType.INSERT)
    @RequestMapping(value = "/{id}/copyReagent", method = RequestMethod.POST)
    public Long copyReagent(@PathVariable long id, RestJsonWrapperBean wrapper) {
        return reagentService.copyReagent(id, wrapper);
    }
}
