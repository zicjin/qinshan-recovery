package com.sunwayworld.emc.module.item.sql.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.sql.bean.EmcSqlBean;
import com.sunwayworld.emc.module.item.sql.service.EmcSqlService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/sqls")
public interface EmcSqlResource extends GenericResource<EmcSqlService, EmcSqlBean, Long> {
}
