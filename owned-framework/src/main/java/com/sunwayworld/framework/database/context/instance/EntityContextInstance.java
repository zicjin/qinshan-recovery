package com.sunwayworld.framework.database.context.instance;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public class EntityContextInstance implements java.io.Serializable {
    private EntityContextInstance() { }
    public static <T extends com.sunwayworld.framework.support.domain.Persistable<?>> EntityContextInstance updatable(com.sunwayworld.framework.database.context.EntityContext p0, T p1, java.lang.String... p2) { throw new UnsupportedOperationException("Owned read-only boundary: EntityContextInstance.updatable is not implemented"); }
    public static <T extends com.sunwayworld.framework.support.domain.Persistable<?>> EntityContextInstance insertable(com.sunwayworld.framework.database.context.EntityContext p0, T p1, java.lang.String... p2) { throw new UnsupportedOperationException("Owned read-only boundary: EntityContextInstance.insertable is not implemented"); }
    public static <T extends com.sunwayworld.framework.support.domain.Persistable<?>> EntityContextInstance instance(com.sunwayworld.framework.database.context.EntityContext p0, T p1, java.lang.String... p2) { throw new UnsupportedOperationException("Owned read-only boundary: EntityContextInstance.instance is not implemented"); }
    public com.sunwayworld.framework.database.context.EntityContext getContext() { throw new UnsupportedOperationException("Owned read-only boundary: EntityContextInstance.getContext is not implemented"); }
    public com.sunwayworld.framework.database.context.instance.EntityColumnContextInstance getIdInstance() { throw new UnsupportedOperationException("Owned read-only boundary: EntityContextInstance.getIdInstance is not implemented"); }
    public java.util.List<com.sunwayworld.framework.database.context.instance.EntityColumnContextInstance> getColumnContextInstanceList() { throw new UnsupportedOperationException("Owned read-only boundary: EntityContextInstance.getColumnContextInstanceList is not implemented"); }
}
