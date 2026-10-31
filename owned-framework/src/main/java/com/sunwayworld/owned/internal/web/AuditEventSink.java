package com.sunwayworld.owned.internal.web;

/** Destination for security events; deployments provide a sink or configure bounded file auditing. */
public interface AuditEventSink {
    void record(AuditEvent event);
}
