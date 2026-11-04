/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptblock.resource;

import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockBean;
import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockTreeNodeDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/lcdp/script-blocks"})
public interface LcdpScriptBlockResource
extends GenericCloudResource<LcdpScriptBlockBean, Long> {
    @RequestMapping(value={"/tree"}, method={RequestMethod.POST})
    public List<LcdpScriptBlockTreeNodeDTO> selectTree(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/drag"}, method={RequestMethod.PUT})
    public void drag(RestJsonWrapperBean var1);
}
