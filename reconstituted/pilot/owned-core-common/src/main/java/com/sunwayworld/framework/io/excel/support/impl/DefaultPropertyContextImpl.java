/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.io.excel.support.impl;

import com.sunwayworld.framework.beans.BeanPropertyType;
import com.sunwayworld.framework.data.TriConsumer;
import com.sunwayworld.framework.io.excel.CellStyleData;
import com.sunwayworld.framework.io.excel.support.PropertyContext;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Sheet;

public class DefaultPropertyContextImpl
implements PropertyContext {
    private final String title;
    private final String name;
    private HorizontalAlignment alignment;
    private TriConsumer<Sheet, CellStyleData, Object> styleConsumer;
    private Function<Object, Object> function;
    private BeanPropertyType propertyType;
    private BiConsumer<Cell, Object> cellRowItemConsumer;

    public static PropertyContext of(String title, String name) {
        return new DefaultPropertyContextImpl(title, name, null, null);
    }

    public static PropertyContext of(String title, String name, HorizontalAlignment alignment) {
        return new DefaultPropertyContextImpl(title, name, null, alignment);
    }

    public static PropertyContext of(String title, String name, Function<Object, Object> function) {
        return new DefaultPropertyContextImpl(title, name, function, null);
    }

    public static PropertyContext of(String title, String name, Function<Object, Object> function, HorizontalAlignment alignment) {
        return new DefaultPropertyContextImpl(title, name, function, alignment);
    }

    private DefaultPropertyContextImpl(String title, String name, Function<Object, Object> function, HorizontalAlignment alignment) {
        this.title = title;
        this.name = name;
        this.function = function;
        this.alignment = Optional.ofNullable(alignment).orElse(HorizontalAlignment.CENTER);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Function<Object, Object> getConvertFunction() {
        return this.function;
    }

    @Override
    public HorizontalAlignment getAlignment() {
        return this.alignment;
    }

    @Override
    public BiConsumer<Cell, Object> getCellRowItemConsumer() {
        return this.cellRowItemConsumer;
    }

    @Override
    public void setCellRowItemConsumer(BiConsumer<Cell, Object> cellRowItemConsumer) {
        this.cellRowItemConsumer = cellRowItemConsumer;
    }

    @Override
    public TriConsumer<Sheet, CellStyleData, Object> getStyleConsumer() {
        return this.styleConsumer;
    }

    @Override
    public void setStyleConsumer(TriConsumer<Sheet, CellStyleData, Object> styleConsumer) {
        this.styleConsumer = styleConsumer;
    }

    @Override
    public BeanPropertyType getPropertyType() {
        return this.propertyType;
    }

    @Override
    public void setPropertyType(BeanPropertyType propertyType) {
        this.propertyType = propertyType;
    }
}
