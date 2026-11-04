/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data;

import java.util.List;

public interface ChunkIterator<E> {
    public boolean hasNext();

    public List<E> nextChunk();

    public int getChunkSize();
}
