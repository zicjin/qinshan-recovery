package com.sunwayworld.framework.support.activatable.resource;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.support.domain.Activatable;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
/** Source-facing methods and route shapes selected from the archived contract. */
public interface GenericActivatableResource<S extends GenericActivatableService<T, ID>, T extends Activatable<ID>, ID extends java.io.Serializable> {
    S getService();
    @RequestMapping(value="/action/activate", method=RequestMethod.PUT)
    default void activate(RestJsonWrapperBean request) { getService().activate(request); }
    @RequestMapping(value="/action/deactivate", method=RequestMethod.PUT)
    default void deactivate(RestJsonWrapperBean request) { getService().deactivate(request); }
    @RequestMapping(value="/queries/activatable", method=RequestMethod.POST)
    default Page<T> selectActivatablePagination(RestJsonWrapperBean request) { return getService().selectActivatablePagination(request); }
}
