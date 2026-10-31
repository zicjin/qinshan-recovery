package com.sunwayworld.framework.support.activatable.service;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.base.service.GenericService;
/** Recorded signatures. Activation writes require a separately implemented slice. */
public interface GenericActivatableService<T extends Activatable<ID>, ID extends java.io.Serializable> extends GenericService<T, ID> {
    default void activate(RestJsonWrapperBean request) { throw new UnsupportedOperationException("Activation is not implemented"); }
    default void deactivate(RestJsonWrapperBean request) { throw new UnsupportedOperationException("Deactivation is not implemented"); }
    default Page<T> selectActivatablePagination(RestJsonWrapperBean request) { throw new UnsupportedOperationException("Activation query is not implemented"); }
    default void setActivateProperties(T value, boolean activated) { throw new UnsupportedOperationException("Activation properties are not implemented"); }
}
