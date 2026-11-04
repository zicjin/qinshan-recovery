/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.service;

import com.sunwayworld.cloud.module.im.bean.CoreImGroupBean;
import com.sunwayworld.cloud.module.im.bean.CoreImGroupContactsDTO;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;

public interface CoreImGroupService
extends GenericService<CoreImGroupBean, Long> {
    public List<CoreImGroupContactsDTO> selectContactsList();
}
