/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.gikam.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value={"/secure/gikam/actions"})
public interface GikamActionResource {
    @Log(value="\u5b9e\u65f6\u4fee\u6539", type=LogType.UPDATE)
    @RequestMapping(value={"/instant"}, method={RequestMethod.PUT})
    @ResponseStatus(value=HttpStatus.OK)
    public void instantSave(RestJsonWrapperBean var1);
}
