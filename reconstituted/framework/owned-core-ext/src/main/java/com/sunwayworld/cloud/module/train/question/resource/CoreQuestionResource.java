/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.resource;

import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionBean;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.suspendable.resource.GenericSuspendableCloudResource;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/train/questions"})
public interface CoreQuestionResource
extends GenericCloudResource<CoreQuestionBean, Long>,
GenericActivatableCloudResource<CoreQuestionBean, Long>,
GenericSuspendableCloudResource<CoreQuestionBean, Long> {
    @RequestMapping(value={"/{id}/opts"}, method={RequestMethod.POST})
    public void insertOpt(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/opts"}, method={RequestMethod.DELETE})
    public void deleteOpt(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/clone"}, method={RequestMethod.PUT})
    public ArrayList<Long> clone(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/opts/queries"}, method={RequestMethod.POST})
    public Pagination<CoreQuestionOptBean> selectQuestionOptPaginationByQuestionId(@PathVariable Long var1, RestJsonWrapperBean var2);
}
