package com.sunwayworld.owned.internal.security;

import com.sunwayworld.owned.internal.security.JdbcPersonnelIdentity.Principal;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import org.springframework.security.access.AccessDeniedException;

/**
 * Adapter from the owned HTTP identity seam to the bounded JDBC personnel
 * identity implementation.  It adds no query or page behavior.
 */
public final class JdbcPersonnelIdentityProvider implements OwnedIdentityProvider {
    private final JdbcPersonnelIdentity delegate;
    private final Object issuer = new Object();

    public JdbcPersonnelIdentityProvider(JdbcPersonnelIdentity delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    @Override public OwnedPrincipal authenticate(String userId, char[] password) throws SQLException {
        Principal principal = delegate.authenticate(userId, password);
        return issued(principal);
    }

    @Override public void authorize(OwnedPrincipal principal, String permission) throws SQLException {
        Principal delegatePrincipal = delegatePrincipal(principal);
        if (PERSONNEL_READ.equals(permission)) delegate.authorizePersonnel(delegatePrincipal);
        else if (EQUIPMENT_READ.equals(permission)) delegate.authorizeEquipment(delegatePrincipal);
        else if (EQUIPMENT_MANAGE.equals(permission)) delegate.authorizeEquipmentManagement(delegatePrincipal);
        else if (CONSUMABLE_READ.equals(permission)) delegate.authorizeConsumable(delegatePrincipal);
        else if (CONSUMABLE_MANAGE.equals(permission)) delegate.authorizeConsumableManagement(delegatePrincipal);
        else if (ATTACHMENT_MANAGE.equals(permission)) delegate.authorizeAttachmentManagement(delegatePrincipal);
        else if (RESULT_READ.equals(permission)) delegate.authorizeResult(delegatePrincipal);
        else if (RESULT_WRITE.equals(permission)) delegate.authorizeResultWrite(delegatePrincipal);
        else if (ORDER_RETAIN_READ.equals(permission)) delegate.authorizeOrderRetain(delegatePrincipal);
        else if (ORDER_RETAIN_WRITE.equals(permission)) delegate.authorizeOrderRetainWrite(delegatePrincipal);
        else if (AUDIT_READ.equals(permission)) delegate.authorizeAudit(delegatePrincipal);
        else if (ROLE_MENU_ADMIN.equals(permission)) delegate.authorizeRoleMenuAdministration(delegatePrincipal);
        else throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    @Override public OwnedPrincipal refresh(OwnedPrincipal principal) throws SQLException {
        return issued(delegate.refreshPrincipal(delegatePrincipal(principal)));
    }

    @Override public Set<String> permittedOrganizationIds(OwnedPrincipal principal) throws SQLException {
        return delegate.permittedOrganizationIds(delegatePrincipal(principal));
    }

    private OwnedPrincipal issued(Principal principal) {
        return OwnedPrincipal.issued(principal.getId(), principal.getOrgId(), principal.getOrgName(),
                new Handle(issuer, principal));
    }

    /** Recover only a principal issued by this adapter, for the owned query service seam. */
    public Principal delegatePrincipal(OwnedPrincipal principal) {
        if (principal == null || !(principal.providerHandle() instanceof Handle)
                || ((Handle) principal.providerHandle()).issuer != issuer) {
            throw new AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
        }
        return ((Handle) principal.providerHandle()).principal;
    }

    private static final class Handle {
        private final Object issuer;
        private final Principal principal;
        private Handle(Object issuer, Principal principal) { this.issuer = issuer; this.principal = principal; }
    }
}
