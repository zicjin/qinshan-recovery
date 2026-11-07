package com.sunwayworld.emc.framework.security;

import com.sunwayworld.module.sys.at.service.CoreAuditTrailRecordService.ReadScope;
import com.sunwayworld.owned.internal.web.OwnedIdentityHttpBoundary;
import java.sql.SQLException;
import javax.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Reuses the existing refreshed audit.read grant and exact principal organization. */
@Component
public final class OriginalAuditTrailReadScope implements ReadScope.Provider {
    private final OwnedIdentityHttpBoundary boundary;
    public OriginalAuditTrailReadScope(OwnedIdentityHttpBoundary boundary) { this.boundary=boundary; }
    public String requireOrganization() {
        ServletRequestAttributes attributes=(ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        HttpSession session=attributes.getRequest().getSession(false);
        try {
            boundary.requireAuditRead(session);
            return boundary.currentPrincipal(session).getOrgId();
        } catch (SQLException failure) {
            throw new AccessDeniedException("audit identity unavailable",failure);
        }
    }
}
