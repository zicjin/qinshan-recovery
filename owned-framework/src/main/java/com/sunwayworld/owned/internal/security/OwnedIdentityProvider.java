package com.sunwayworld.owned.internal.security;

import java.sql.SQLException;
import java.util.Set;

/** Public identity, permission, and organization-scope seam used by the HTTP layer. */
public interface OwnedIdentityProvider {
    /** The first permission backed by the observed LIMS personnel menu contract. */
    String PERSONNEL_READ = "personnel.read";
    String EQUIPMENT_READ = "equipment.read";
    String EQUIPMENT_MANAGE = "equipment.manage";
    String CONSUMABLE_READ = "consumable.read";
    String CONSUMABLE_MANAGE = "consumable.manage";
    String ATTACHMENT_MANAGE = "attachment.manage";
    String RESULT_READ = "result.read";
    String RESULT_WRITE = "result.write";
    String ORDER_RETAIN_READ = "order-retain.read";
    String ORDER_RETAIN_WRITE = "order-retain.write";
    /** Explicitly configured menu grant for the source-owned audit-trail read seam. */
    String AUDIT_READ = "audit.read";
    /** Explicitly configured menu grant for the bounded global role/menu matrix seam. */
    String ROLE_MENU_ADMIN = "role-menu.admin";

    OwnedPrincipal authenticate(String userId, char[] password) throws SQLException;

    /**
     * Adapts an identity already authenticated by the original project's
     * Spring Security/remember-me chain. The provider must re-read and validate
     * the user before issuing a principal; this is not a passwordless login.
     */
    default OwnedPrincipal issueAuthenticated(String userId) throws SQLException {
        throw new org.springframework.security.access.AccessDeniedException("GIKAM.PERMISSION.UNAUTHORIZED");
    }

    /** Revalidates a session snapshot before protected work. */
    OwnedPrincipal refresh(OwnedPrincipal principal) throws SQLException;

    void authorize(OwnedPrincipal principal, String permission) throws SQLException;

    Set<String> permittedOrganizationIds(OwnedPrincipal principal) throws SQLException;

    /** Safe DTO inputs for the current identity; never include credentials or grants cached at login. */
    java.util.Map<String, Object> accountProfile(OwnedPrincipal principal) throws SQLException;
}
