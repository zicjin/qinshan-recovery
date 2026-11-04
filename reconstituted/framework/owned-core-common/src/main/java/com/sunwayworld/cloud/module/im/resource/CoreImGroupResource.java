/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.resource;

import com.sunwayworld.cloud.module.im.bean.CoreImGroupBean;
import com.sunwayworld.cloud.module.im.bean.CoreImGroupContactsDTO;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/im/groups"})
public interface CoreImGroupResource
extends GenericCloudResource<CoreImGroupBean, Long> {
    @RequestMapping(value={"/contactses/queries"}, method={RequestMethod.GET})
    public List<CoreImGroupContactsDTO> selectContactsList();
}
