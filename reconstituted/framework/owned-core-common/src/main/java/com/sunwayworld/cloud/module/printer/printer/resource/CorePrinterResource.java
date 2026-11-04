/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printer.resource;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/printer/printers"})
public interface CorePrinterResource {
    @RequestMapping(value={"/action/print"}, method={RequestMethod.POST})
    public void print(RestJsonWrapperBean var1);

    @RequestMapping(value={"/queries"}, method={RequestMethod.GET})
    public List<String> selectPrinterNameList();

    @RequestMapping(value={"/papers/queries"}, method={RequestMethod.GET})
    public List<String> selectPaperNameList();
}
