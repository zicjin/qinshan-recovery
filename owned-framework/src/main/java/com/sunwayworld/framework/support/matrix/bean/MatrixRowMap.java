package com.sunwayworld.framework.support.matrix.bean;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public class MatrixRowMap extends java.util.HashMap<java.lang.String, java.lang.Object> {
    public MatrixRowMap() {  }
    public <V> void putRowId(java.lang.String p0, V p1) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.putRowId is not implemented"); }
    public <V> V getId() { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.getId is not implemented"); }
    public <T> void putItem(T p0) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.putItem is not implemented"); }
    public <T> void putRotateItem(java.lang.String p0, T p1) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.putRotateItem is not implemented"); }
    public <PK, V> void putRotateValue(java.lang.String p0, PK p1, java.lang.String p2, V p3) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.putRotateValue is not implemented"); }
    public <PK, V> void putRotateExtValue(java.lang.String p0, PK p1, java.lang.String p2, java.lang.String p3, V p4) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.putRotateExtValue is not implemented"); }
    public boolean match(com.sunwayworld.framework.mybatis.mapper.FilterParamPattern p0) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.match is not implemented"); }
    public void setPredicate(java.util.function.Predicate<com.sunwayworld.framework.mybatis.mapper.FilterParamPattern> p0) { throw new UnsupportedOperationException("Owned read-only boundary: MatrixRowMap.setPredicate is not implemented"); }
}
