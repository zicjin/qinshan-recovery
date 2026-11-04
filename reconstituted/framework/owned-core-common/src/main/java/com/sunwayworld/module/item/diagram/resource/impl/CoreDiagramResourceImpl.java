/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.diagram.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.diagram.bean.CoreDiagramBean;
import com.sunwayworld.module.item.diagram.resource.CoreDiagramResource;
import com.sunwayworld.module.item.diagram.service.CoreDiagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u56fe\u8868")
@RestController
@GikamBean
public class CoreDiagramResourceImpl
implements CoreDiagramResource {
    @Autowired
    private CoreDiagramService diagramService;

    @Override
    public CoreDiagramService getService() {
        return this.diagramService;
    }

    @Override
    @Log(value="\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/diagrams/{table}/{itemId}"}, method={RequestMethod.GET})
    public <ID> CoreDiagramBean selectOne(@PathVariable String table, @PathVariable ID itemId) {
        return this.getService().selectOne(table, itemId);
    }

    @Override
    @Log(value="\u4fdd\u5b58", type=LogType.UPDATE)
    @RequestMapping(value={"/diagrams/{id}"}, method={RequestMethod.PUT})
    public void save(Long id, RestJsonWrapperBean wrapper) {
        this.getService().save(id, wrapper);
    }
}
