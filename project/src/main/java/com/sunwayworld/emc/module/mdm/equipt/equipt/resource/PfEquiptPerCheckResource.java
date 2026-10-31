package com.sunwayworld.emc.module.mdm.equipt.equipt.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.bean.EmcEquiptPerCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service.EmcEquiptPerCheckService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RestController
@GikamBean
@RequestMapping("/secure/emc/module/mdm/equipt/equipt/equipt-per-check")
public interface PfEquiptPerCheckResource extends GenericResource<EmcEquiptPerCheckService, EmcEquiptPerCheckBean, Long> {
}