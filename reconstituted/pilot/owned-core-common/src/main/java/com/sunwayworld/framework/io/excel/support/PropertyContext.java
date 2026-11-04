/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.support;

import com.sunwayworld.framework.beans.BeanPropertyType;
import com.sunwayworld.framework.data.TriConsumer;
import com.sunwayworld.framework.io.excel.CellStyleData;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;

public interface PropertyContext {
    public String getTitle();

    public String getName();

    public HorizontalAlignment getAlignment();

    public Function<Object, Object> getConvertFunction();

    public void setCellRowItemConsumer(BiConsumer<Cell, Object> var1);

    public BiConsumer<Cell, Object> getCellRowItemConsumer();

    public void setStyleConsumer(TriConsumer<Sheet, CellStyleData, Object> var1);

    public TriConsumer<Sheet, CellStyleData, Object> getStyleConsumer();

    public BeanPropertyType getPropertyType();

    public void setPropertyType(BeanPropertyType var1);
}
