package com.sunwayworld.owned.internal.security;

import java.security.Principal;
import java.util.Objects;

/**
 * Immutable identity snapshot kept by the owned HTTP boundary.
 *
 * <p>The provider handle is deliberately opaque.  It lets a provider retain the
 * evidence-backed identity it issued without making a database principal or a
 * password part of a servlet session contract.</p>
 */
public final class OwnedPrincipal implements Principal {
    private final String id;
    private final String orgId;
    private final String orgName;
    private final Object providerHandle;

    /** Fixture and provider-neutral constructor for public seam tests. */
    public OwnedPrincipal(String id, String orgId, String orgName) {
        this(id, orgId, orgName, null);
    }

    OwnedPrincipal(String id, String orgId, String orgName, Object providerHandle) {
        this.id = requireText(id, "id");
        this.orgId = orgId;
        this.orgName = orgName;
        this.providerHandle = providerHandle;
    }

    static OwnedPrincipal issued(String id, String orgId, String orgName, Object providerHandle) {
        return new OwnedPrincipal(id, orgId, orgName, Objects.requireNonNull(providerHandle, "providerHandle"));
    }

    Object providerHandle() { return providerHandle; }
    public String getName() { return id; }
    public String getId() { return id; }
    public String getOrgId() { return orgId; }
    public String getOrgName() { return orgName; }

    @Override public String toString() { return "OwnedPrincipal{id='" + id + "', orgId='" + orgId + "'}"; }

    private static String requireText(String value, String name) {
        if (value == null || value.trim().length() == 0) throw new IllegalArgumentException(name + " is required");
        return value;
    }
}
