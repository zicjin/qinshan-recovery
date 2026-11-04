/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.inter.resource;

import com.sunwayworld.module.item.file.bean.CoreFileBean;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

public interface MobileInterResource {
    @RequestMapping(value={"/{method}"}, method={RequestMethod.POST})
    public String request(@PathVariable(value="method") String var1, @RequestBody Map<String, Object> var2);

    @RequestMapping(value={"/push-message"}, method={RequestMethod.POST})
    public void pushMessage(@RequestBody Map<String, String> var1);

    @RequestMapping(value={"/downloadFile"}, method={RequestMethod.POST})
    public CoreFileBean downloadFile(@RequestBody Map<String, Object> var1);

    @RequestMapping(value={"/uploadFile"}, method={RequestMethod.POST})
    public String uploadFile(@RequestParam Map<String, Object> var1, @RequestParam(value="file") MultipartFile var2);
}
