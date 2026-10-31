package com.sunwayworld.framework.io.excel.support;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface PropertyContext {
    public abstract java.lang.String getTitle();
    public abstract java.lang.String getName();
    public abstract org.apache.poi.ss.usermodel.HorizontalAlignment getAlignment();
    public abstract java.util.function.Function<java.lang.Object, java.lang.Object> getConvertFunction();
    public abstract void setCellRowItemConsumer(java.util.function.BiConsumer<org.apache.poi.ss.usermodel.Cell, java.lang.Object> p0);
    public abstract java.util.function.BiConsumer<org.apache.poi.ss.usermodel.Cell, java.lang.Object> getCellRowItemConsumer();
    public abstract void setStyleConsumer(com.sunwayworld.framework.data.TriConsumer<org.apache.poi.ss.usermodel.Sheet, com.sunwayworld.framework.io.excel.CellStyleData, java.lang.Object> p0);
    public abstract com.sunwayworld.framework.data.TriConsumer<org.apache.poi.ss.usermodel.Sheet, com.sunwayworld.framework.io.excel.CellStyleData, java.lang.Object> getStyleConsumer();
    public abstract com.sunwayworld.framework.beans.BeanPropertyType getPropertyType();
    public abstract void setPropertyType(com.sunwayworld.framework.beans.BeanPropertyType p0);
}
