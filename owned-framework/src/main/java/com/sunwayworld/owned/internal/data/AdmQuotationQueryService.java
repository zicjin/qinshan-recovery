package com.sunwayworld.owned.internal.data;

import com.sunwayworld.owned.internal.security.OwnedPrincipal;
import com.sunwayworld.owned.internal.web.QueryRequest;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;

/** Applies trusted identity scope before entering the shared quotation read DAO. */
public final class AdmQuotationQueryService {
    private final AdmQuotationQueryDao dao;

    public AdmQuotationQueryService(AdmQuotationQueryDao dao) {
        if (dao == null) throw new IllegalArgumentException("quotation DAO required");
        this.dao = dao;
    }

    public String query(String resourcePath, OwnedPrincipal principal,
            Set<String> permittedOrganizations, QueryRequest request) throws SQLException {
        if (principal == null) throw new AccessDeniedException("authenticated principal required");
        if (request == null) throw new IllegalArgumentException("query required");
        AdmQuotationQueryDao.Resource resource = AdmQuotationQueryDao.Resource.forPath(resourcePath);
        // UI org_filter and request parameters cannot widen server-owned scope.
        Set<String> scope = new LinkedHashSet<String>();
        if (resource == AdmQuotationQueryDao.Resource.DETECTION_FACTOR) {
            if (permittedOrganizations != null) for (String organization : permittedOrganizations) {
                if (organization != null && !organization.trim().isEmpty()) scope.add(organization);
            }
        } else {
            // Preserve the existing creator/home-org policy until its resource-specific
            // supplier policy has been independently verified; do not widen on parity alone.
            String organization = principal.getOrgId();
            if (organization != null && !organization.trim().isEmpty()) scope.add(organization);
        }
        return dao.readPage(resource, Collections.unmodifiableSet(scope), request);
    }
}
