/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.admin.config.bean.CoreAdminCheckConfigBean;
import com.sunwayworld.module.admin.config.bean.CoreAdminCheckConfigDTO;
import com.sunwayworld.module.sys.checkcode.bean.CoreCheckCodeBean;
import java.util.List;

public interface CoreAdminCheckConfigService
extends GenericService<CoreAdminCheckConfigBean, String>,
GenericSelectableService<CoreAdminCheckConfigBean, String> {
    public List<CoreAdminCheckConfigDTO> selectAllConfig();

    public void insertByCheckCode(CoreCheckCodeBean var1);
}
