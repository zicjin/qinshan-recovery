/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints.resource;

import com.sunwayworld.framework.jdk.hints.CodeHintsClass;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/code-hints"})
public interface CodeHintsResource {
    @GetMapping
    public CodeHintsClass selectByPath(@RequestParam(value="path") String var1);

    @PostMapping(value={"/source-codes/format"})
    public String formatSourceCodes(RestJsonWrapperBean var1);
}
