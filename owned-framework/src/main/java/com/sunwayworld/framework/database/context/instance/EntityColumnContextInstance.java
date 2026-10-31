package com.sunwayworld.framework.database.context.instance;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public class EntityColumnContextInstance implements java.io.Serializable {
    private EntityColumnContextInstance() { }
    public static EntityColumnContextInstance of(com.sunwayworld.framework.database.context.EntityColumnContext p0, com.sunwayworld.framework.support.domain.Persistable<?> p1) { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.of is not implemented"); }
    public void setNewValue(java.lang.Object p0) { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.setNewValue is not implemented"); }
    public boolean isActive() { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.isActive is not implemented"); }
    public void setActive(boolean p0) { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.setActive is not implemented"); }
    public com.sunwayworld.framework.database.context.EntityColumnContext getColumnContext() { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.getColumnContext is not implemented"); }
    public java.lang.Object getValue() { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.getValue is not implemented"); }
    public boolean isValueAssigned() { throw new UnsupportedOperationException("Owned read-only boundary: EntityColumnContextInstance.isValueAssigned is not implemented"); }
}
