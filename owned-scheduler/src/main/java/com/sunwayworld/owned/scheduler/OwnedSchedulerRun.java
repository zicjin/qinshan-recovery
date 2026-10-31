package com.sunwayworld.owned.scheduler;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable observation of one isolated task trigger. */
public final class OwnedSchedulerRun {
    public enum Status { SUCCESS, FAILED, RETRYING, DISABLED }
    private final String id, task, source, scenario, message;
    private final Status status;
    private final int attempt;
    private final Instant startedAt;
    private final Map<String,String> metadata;
    public OwnedSchedulerRun(String id, String task, String source, String scenario, Status status,
            int attempt, String message, Instant startedAt, Map<String,String> metadata) {
        this.id=id; this.task=task; this.source=source; this.scenario=scenario; this.status=status;
        this.attempt=attempt; this.message=message; this.startedAt=startedAt;
        this.metadata=Collections.unmodifiableMap(new LinkedHashMap<String,String>(metadata));
    }
    public String getId(){return id;} public String getTask(){return task;} public String getSource(){return source;}
    public String getScenario(){return scenario;} public Status getStatus(){return status;} public int getAttempt(){return attempt;}
    public String getMessage(){return message;} public Instant getStartedAt(){return startedAt;} public Map<String,String> getMetadata(){return metadata;}
}
