package com.sunwayworld.framework.support.selectable.resource;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericSelectableResource<S extends com.sunwayworld.framework.support.selectable.service.GenericSelectableService<T, ID>, T extends com.sunwayworld.framework.support.domain.Persistable<ID>, ID extends java.io.Serializable> {
    public abstract S getService();
    public default java.util.List<T> selectSelectableList() { throw new UnsupportedOperationException("Owned read-only boundary: GenericSelectableResource.selectSelectableList is not implemented"); }
    public default java.util.List<T> selectPostSelectableList() { throw new UnsupportedOperationException("Owned read-only boundary: GenericSelectableResource.selectPostSelectableList is not implemented"); }
}
