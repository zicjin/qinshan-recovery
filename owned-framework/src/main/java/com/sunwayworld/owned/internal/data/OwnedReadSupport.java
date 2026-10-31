package com.sunwayworld.owned.internal.data;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.framework.support.domain.Persistable;
import java.io.Serializable;

/** Generic facade delegates only to an explicitly configured owned read DAO. */
public final class OwnedReadSupport {
    private OwnedReadSupport() { }
    @SuppressWarnings("unchecked")
    private static <T extends Persistable<ID>, ID extends Serializable> OwnedReadDao<T, ID> reader(GenericDao<T, ID> dao) {
        if (!(dao instanceof OwnedReadDao)) throw new UnsupportedOperationException("No owned read adapter is configured for this DAO");
        return (OwnedReadDao<T, ID>) dao;
    }
    public static <T extends Persistable<ID>, ID extends Serializable> Page<T> page(GenericDao<T, ID> dao, RestJsonWrapperBean request) {
        return reader(dao).readPage(request);
    }
    public static <T extends Persistable<ID>, ID extends Serializable> T detail(GenericDao<T, ID> dao, ID id) {
        return reader(dao).readDetail(id);
    }
}
