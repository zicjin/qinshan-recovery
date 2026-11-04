/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.mapper;

import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

public enum MatchPattern {
    SB(String.class),
    SC(String.class),
    NSC(String.class),
    SE(String.class),
    SEQ(String.class),
    CISB(String.class),
    CISC(String.class),
    CISE(String.class),
    CISEQ(String.class),
    DG(LocalDate.class),
    DGOE(LocalDate.class),
    DL(LocalDate.class),
    DLOE(LocalDate.class),
    DEQ(LocalDate.class),
    DR(LocalDate.class),
    TG(LocalDateTime.class),
    TGOE(LocalDateTime.class),
    TL(LocalDateTime.class),
    TLOE(LocalDateTime.class),
    TEQ(LocalDateTime.class),
    TR(LocalDateTime.class),
    NG(Double.class),
    NGOE(Double.class),
    NL(Double.class),
    NLOE(Double.class),
    NEQ(Double.class),
    NR(Double.class),
    EQ(Void.class),
    DIFFER(Void.class),
    SPECIAL(String.class),
    MULTIPLE(String.class),
    NOR(Double.class),
    DOR(LocalDate.class),
    TOR(LocalDateTime.class),
    OR(String.class),
    ISNULL(Void.class),
    ISNOTNULL(Void.class),
    OSB(String.class),
    OSC(String.class),
    OSE(String.class),
    OCISB(String.class),
    OCISC(String.class),
    OCISE(String.class),
    OCISEQ(String.class),
    CBM(String.class),
    COBM(String.class),
    IN(String.class),
    OIN(String.class);

    private Class<?> type;

    private MatchPattern(Class<?> type) {
        this.type = type;
    }

    public <T> T parse(Object value) {
        if (Void.class.equals(this.type)) {
            return (T)value;
        }
        return (T)ConvertUtils.convert(value, this.type);
    }

    public static final MatchPattern getPatternIfPossible(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return null;
        }
        return Arrays.stream(MatchPattern.values()).filter(p -> p.name().equals(pattern)).findAny().orElse(null);
    }

    public Class<?> getType() {
        return this.type;
    }
}
