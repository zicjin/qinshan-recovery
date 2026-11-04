/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.id;

import java.time.Instant;

class SnowflakeIdWorker {
    private final long workerIdBits = 8L;
    private final long sequenceBits = 5L;
    private final long maxSequence = 31L;
    private final long maxWorkerUd = 255L;
    private final long timestampShift = 13L;
    private final long workerIdShift = 5L;
    private final long epoch = 1546214400000L;
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private long lastId = 0L;

    public SnowflakeIdWorker(long workerId) {
        if (workerId > 255L) {
            workerId = 0L;
        }
        this.workerId = workerId == 0L ? Instant.now().toEpochMilli() % 255L : workerId;
    }

    public synchronized long nextId() {
        long currTimestamp = this.timestampGen();
        if (currTimestamp < this.lastTimestamp) {
            long diff = this.lastTimestamp - currTimestamp;
            if (diff > 1L) {
                this.sleep(diff);
            }
            currTimestamp = this.timestampGen();
            while (currTimestamp < this.lastTimestamp) {
                currTimestamp = this.timestampGen();
            }
        }
        if (currTimestamp == this.lastTimestamp) {
            this.sequence = this.sequence + 1L & 0x1FL;
            if (this.sequence == 0L) {
                currTimestamp = this.waitNextMillis(currTimestamp);
            }
        } else {
            this.sequence = 0L;
        }
        this.lastTimestamp = currTimestamp;
        long nextId = currTimestamp - 1546214400000L << 13 | this.workerId << 5 | this.sequence;
        if (nextId <= this.lastId) {
            this.sleep(1L);
            return this.nextId();
        }
        this.lastId = nextId;
        return this.lastId;
    }

    protected long waitNextMillis(long currTimestamp) {
        while (currTimestamp <= this.lastTimestamp) {
            currTimestamp = this.timestampGen();
        }
        return currTimestamp;
    }

    protected long timestampGen() {
        return System.currentTimeMillis();
    }

    public long getEpoch() {
        return this.epoch;
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }
}
