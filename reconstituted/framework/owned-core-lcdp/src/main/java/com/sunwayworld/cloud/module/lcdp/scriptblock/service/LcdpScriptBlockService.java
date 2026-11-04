/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.scriptblock.service;

import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockBean;
import com.sunwayworld.cloud.module.lcdp.scriptblock.bean.LcdpScriptBlockTreeNodeDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface LcdpScriptBlockService
extends GenericService<LcdpScriptBlockBean, Long> {
    public List<LcdpScriptBlockTreeNodeDTO> selectTree(RestJsonWrapperBean var1);

    public void drag(RestJsonWrapperBean var1);
}
