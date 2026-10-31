package com.sunwayworld.owned.internal.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Thread-safe unbounded fixture sink; never selected by runtime auto-configuration. */
public final class InMemoryAuditEventSink implements AuditEventSink {
    private final CopyOnWriteArrayList<AuditEvent> events = new CopyOnWriteArrayList<AuditEvent>();

    @Override public void record(AuditEvent event) {
        if (event == null) throw new IllegalArgumentException("event is required");
        events.add(event);
    }

    public List<AuditEvent> events() {
        return Collections.unmodifiableList(new ArrayList<AuditEvent>(events));
    }

    @Override public String toString() { return events.toString(); }
}
