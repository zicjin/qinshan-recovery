package com.sunwayworld.framework.support.selectable.service;

/** Recorded source contract. Unimplemented operations fail explicitly. */
public interface GenericSelectableService<T extends com.sunwayworld.framework.support.domain.Persistable<ID>, ID extends java.io.Serializable> {
    public abstract <D extends com.sunwayworld.framework.support.base.dao.GenericDao<T, ID>> D getDao();
    public default java.util.List<T> selectSelectableList() { throw new UnsupportedOperationException("Owned read-only boundary: GenericSelectableService.selectSelectableList is not implemented"); }
}
