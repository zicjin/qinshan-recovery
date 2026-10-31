package com.sunwayworld.framework.io.excel.support;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public class SheetContext {
    public SheetContext(java.lang.String p0, java.lang.String p1, java.util.List<com.sunwayworld.framework.io.excel.support.PropertyContext> p2, java.util.List<com.sunwayworld.framework.data.Pair<java.lang.String, com.sunwayworld.framework.data.Pair<java.lang.Integer, java.lang.Integer>>> p3) {  }
    public SheetContext(java.lang.String p0, java.lang.String p1, java.util.List<com.sunwayworld.framework.io.excel.support.PropertyContext> p2) {  }
    public java.lang.String getTopic() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getTopic is not implemented"); }
    public java.lang.String getSheetName() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getSheetName is not implemented"); }
    public java.util.List<com.sunwayworld.framework.io.excel.support.PropertyContext> getPropertyList() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getPropertyList is not implemented"); }
    public java.util.List<com.sunwayworld.framework.data.Pair<java.lang.String, java.util.function.Function<java.lang.Double, java.lang.String>>> getSumPropertyList() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getSumPropertyList is not implemented"); }
    public java.util.List<com.sunwayworld.framework.data.Pair<java.lang.String, com.sunwayworld.framework.data.Pair<java.lang.Integer, java.lang.Integer>>> getSubTopicList() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getSubTopicList is not implemented"); }
    public void setSumProperty(java.lang.String p0) { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.setSumProperty is not implemented"); }
    public void setSumProperty(java.lang.String p0, java.util.function.Function<java.lang.Double, java.lang.String> p1) { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.setSumProperty is not implemented"); }
    public java.util.List<com.sunwayworld.framework.data.Pair<java.lang.String, java.util.List<java.lang.String>>> getMergePropertyPairList() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getMergePropertyPairList is not implemented"); }
    public void setMergeProperty(java.lang.String p0, java.lang.String... p1) { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.setMergeProperty is not implemented"); }
    public java.util.Map<java.lang.String, java.lang.Number> getTotalMap() { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.getTotalMap is not implemented"); }
    public void setTotalMap(java.util.Map<java.lang.String, java.lang.Number> p0) { throw new UnsupportedOperationException("Owned read-only boundary: SheetContext.setTotalMap is not implemented"); }
}
