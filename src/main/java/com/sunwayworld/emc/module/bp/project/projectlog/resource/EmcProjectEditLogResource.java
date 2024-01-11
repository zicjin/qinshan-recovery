package com.sunwayworld.emc.module.bp.project.projectlog.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.project.projectlog.bean.EmcProjectEditLogBean;
import com.sunwayworld.emc.module.bp.project.projectlog.service.EmcProjectEditLogService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/project/project-edit-logs")
public interface EmcProjectEditLogResource extends GenericResource<EmcProjectEditLogService, EmcProjectEditLogBean, Long> {
}
