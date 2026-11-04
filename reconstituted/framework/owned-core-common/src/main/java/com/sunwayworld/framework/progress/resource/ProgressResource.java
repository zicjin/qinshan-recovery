/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.resource;

import com.sunwayworld.framework.progress.bean.ProgressDTO;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/progresses"})
public interface ProgressResource {
    @RequestMapping(value={"/{progressId}"}, method={RequestMethod.GET})
    public ProgressDTO selectById(@PathVariable String var1);

    @RequestMapping(value={"/my-progresses/lists"}, method={RequestMethod.GET})
    public List<ProgressDTO> selectMyProgressList();
}
