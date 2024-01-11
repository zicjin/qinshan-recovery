package com.sunwayworld.emc.module.bp.voc.resource.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.voc.resource.PfVocWeightingResource;
import com.sunwayworld.emc.module.bp.voc.service.PfVocWeightingService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2021年09月24日
 */
@LogModule(value = "VOC")
@RestController
@GikamBean
public class PfVocWeightingResourceImpl implements PfVocWeightingResource {

    @Autowired
    private PfVocWeightingService pfVocWeightingService;

    @Override
    public PfVocWeightingService getService() {
        return pfVocWeightingService;
    }
    
    @Log(value = "增加VOC空瓶", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return pfVocWeightingService.insert(wrapper);
    }
    
    @Log(value = "根据瓶数量增加VOC空瓶", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/add-voc", method = RequestMethod.POST)
    public String addVoc(RestJsonWrapperBean wrapper) {
        return pfVocWeightingService.addVoc(wrapper);
    }
    
 /*   @Log(value = "VOC空瓶页面", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/voc-page", method = RequestMethod.POST)
    public Page<PfVocWeightingBean> vocPage(RestJsonWrapperBean wrapper) {
        return pfVocWeightingService.vocPage(wrapper);
    }*/
    
    @Log(value = "增加VOC送样空瓶", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/action/insert-deliver-bottle", method = RequestMethod.POST)
    public void insertDeliverBottle(RestJsonWrapperBean wrapper) {
        pfVocWeightingService.insertDeliverBottle(wrapper);
    }
    
}