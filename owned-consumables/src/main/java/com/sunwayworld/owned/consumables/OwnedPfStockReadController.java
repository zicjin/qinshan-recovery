package com.sunwayworld.owned.consumables;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.owned.internal.security.OwnedIdentityProvider;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Source-owned aliases for the inherited PF stock read resource. */
@RestController
@ConditionalOnBean({OwnedIdentityHttpBoundary.class, DataSource.class})
public final class OwnedPfStockReadController {
    private final OwnedIdentityHttpBoundary boundary;
    private final OwnedMdmStockReadController delegate;

    public OwnedPfStockReadController(OwnedIdentityHttpBoundary boundary, OwnedMdmStockReadController delegate) {
        if (boundary == null || delegate == null) throw new IllegalArgumentException("PF stock dependencies are required");
        this.boundary = boundary; this.delegate = delegate;
    }

    @PostMapping({
            "/secure/emc/module/mdm/basemdm/stock/queries",
            "/secure/emc/module/mdm/basemdm/stock/queries/raw",
            "/secure/emc/module/mdm/basemdm/stock/queries/searchable"})
    public Page<EmcStockBean> query(HttpServletRequest request,
            @RequestBody(required = false) RestJsonWrapperBean wrapper) {
        return delegate.stocks("0", wrapper);
    }

    @GetMapping("/secure/emc/module/mdm/basemdm/stock/info/status")
    public Map<String, Object> status(HttpServletRequest request) {
        try {
            boundary.requirePermission(request == null ? null : request.getSession(false), OwnedIdentityProvider.CONSUMABLE_READ);
        } catch (java.sql.SQLException failure) {
            throw new IllegalStateException("PF stock identity check failed", failure);
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("ext$", new LinkedHashMap<String, Object>());
        return result;
    }
}
