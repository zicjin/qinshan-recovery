package com.sunwayworld.owned.internal.web;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable security audit record; it never carries a credential or session object. */
public final class AuditEvent {
    public enum Outcome { ALLOWED, DENIED }

    private final Instant occurredAt;
    private final String action;
    private final String actorId;
    private final String organizationId;
    private final String resource;
    private final Outcome outcome;
    private final String reason;
    private final String source;
    private final Map<String, String> attributes;

    public AuditEvent(Instant occurredAt, String action, String actorId, String resource,
            Outcome outcome, String reason, Map<String, String> attributes) {
        this(occurredAt, action, actorId, null, resource, outcome, reason, "owned-http", attributes);
    }

    public AuditEvent(Instant occurredAt, String action, String actorId, String organizationId,
            String resource, Outcome outcome, String reason, String source,
            Map<String, String> attributes) {
        this.occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        this.action = require(action, "action");
        this.actorId = actorId;
        this.organizationId = organizationId;
        this.resource = resource;
        this.outcome = outcome == null ? Outcome.DENIED : outcome;
        this.reason = reason;
        this.source = source == null || source.length() == 0 ? "owned-http" : source;
        this.attributes = immutable(attributes);
    }

    public Instant getOccurredAt() { return occurredAt; }
    public String getAction() { return action; }
    public String getActorId() { return actorId; }
    public String getOrganizationId() { return organizationId; }
    public String getResource() { return resource; }
    public Outcome getOutcome() { return outcome; }
    public String getReason() { return reason; }
    public String getSource() { return source; }
    public Map<String, String> getAttributes() { return attributes; }

    @Override public String toString() {
        return "AuditEvent{action='" + action + "', actorId='" + actorId + "', resource='" + resource
                + "', outcome=" + outcome + ", reason='" + reason + "'}";
    }

    private static String require(String value, String name) {
        if (value == null || value.length() == 0) throw new IllegalArgumentException(name + " is required");
        return value;
    }
    private static Map<String, String> immutable(Map<String, String> values) {
        if (values == null || values.isEmpty()) return Collections.emptyMap();
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(values));
    }
}
