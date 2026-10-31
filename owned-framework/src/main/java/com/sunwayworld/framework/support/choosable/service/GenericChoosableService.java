package com.sunwayworld.framework.support.choosable.service;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericChoosableService<T extends com.sunwayworld.framework.support.domain.Persistable<ID>, ID extends java.io.Serializable> extends com.sunwayworld.framework.support.base.service.GenericService<T, ID> {
    public default com.sunwayworld.framework.data.page.Page<T> selectChoosablePagination(com.sunwayworld.framework.restful.data.RestJsonWrapperBean p0) { throw new UnsupportedOperationException("Owned read-only boundary: GenericChoosableService.selectChoosablePagination is not implemented"); }
}
