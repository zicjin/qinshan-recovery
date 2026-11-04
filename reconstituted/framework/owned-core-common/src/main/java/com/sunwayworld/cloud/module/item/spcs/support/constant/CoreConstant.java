/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.spcs.support.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoreConstant {
    public static final String DV_SPC_SINGLE = "spcSingle";
    public static final String DV_SPC_RANGE = "spcRange";
    public static final String DV_SPC_MEANRANGE = "spcMeanRange";
    public static final String DV_SPC_MEANBYRANGE = "spcMeanByRange";
    public static final String DV_SPC_MEANBYSTANDARD = "spcMeanByStandard";
    public static final String DV_SPC_STANDARD = "spcStandard";
    public static final String DV_SPC_DEFECT = "spcDefect";
    public static final String DV_SPC_UNQUALIFIED = "spcUnqualified";
    public static final List<String> DV_DATE_NATURAL_SORT = new ArrayList<String>();

    static {
        DV_DATE_NATURAL_SORT.addAll(Arrays.asList("\u661f\u671f\u4e00", "\u661f\u671f\u4e8c", "\u661f\u671f\u4e09", "\u661f\u671f\u56db", "\u661f\u671f\u4e94", "\u661f\u671f\u516d", "\u661f\u671f\u65e5"));
        for (int i = 1; i < 61; ++i) {
            DV_DATE_NATURAL_SORT.add(String.valueOf(i));
            if (i < 5) {
                DV_DATE_NATURAL_SORT.add(i + "\u5b63\u5ea6");
            }
            if (i >= 13) continue;
            DV_DATE_NATURAL_SORT.add(i + "\u6708");
        }
    }
}
