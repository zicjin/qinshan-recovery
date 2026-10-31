package com.sunwayworld.owned.internal.data;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/** Explicit opt-in for bounded generic resource read implementations. */
public interface OwnedReadDao<T, ID> {
    Page<T> readPage(RestJsonWrapperBean request);
    T readDetail(ID id);
}
