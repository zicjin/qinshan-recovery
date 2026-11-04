/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDTO;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionBean;
import com.sunwayworld.cloud.module.eln.bean.CoreElnTmplVersionDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import java.util.Map;

public interface CoreElnTmplService
extends GenericService<CoreElnTmplBean, Long>,
GenericActivatableService<CoreElnTmplBean, Long>,
GenericChoosableService<CoreElnTmplBean, Long>,
GenericSelectableService<CoreElnTmplBean, Long> {
    public CoreElnTmplVersionDTO selectElnDsDTO(Long var1, RestJsonWrapperBean var2);

    public CoreElnDTO selectElnTestDTO(Long var1, Long var2, RestJsonWrapperBean var3);

    public CoreElnTmplVersionBean selectTopVersionByTmplId(Long var1);

    public Map<String, String> getDataByDs(Long var1, String var2);

    public void copyTmpl(Long var1, Long var2, RestJsonWrapperBean var3);
}
