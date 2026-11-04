/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.printer.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import java.util.List;

public interface CorePrinterService {
    public List<String> selectPrinterNameList();

    public void print(RestJsonWrapperBean var1);

    public List<String> selectPaperNameList();
}
