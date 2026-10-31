package com.sunwayworld.framework.support.choosable.resource;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericChoosableResource<S extends com.sunwayworld.framework.support.choosable.service.GenericChoosableService<T, ID>, T extends com.sunwayworld.framework.support.domain.Persistable<ID>, ID extends java.io.Serializable> {
    public abstract S getService();
    public default com.sunwayworld.framework.data.page.Page<T> selectChoosablePagination(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericChoosableResource.selectChoosablePagination is not implemented"); }
}
