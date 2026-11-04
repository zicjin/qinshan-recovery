/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.data;

import com.sunwayworld.framework.data.ChunkIterator;
import com.sunwayworld.framework.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListChunkIterator<I>
implements ChunkIterator<I> {
    private final List<I> list;
    private final int chunkSize;
    private final int size;
    private int pos;

    private ListChunkIterator(List<I> list, int chunkSize) {
        this.list = Objects.requireNonNull(list);
        this.chunkSize = Objects.requireNonNull(chunkSize);
        this.size = list.size();
        this.pos = 0;
    }

    @Override
    public boolean hasNext() {
        return this.pos < this.size;
    }

    @Override
    public List<I> nextChunk() {
        if (!this.hasNext()) {
            throw new InvalidDataException("No more element.");
        }
        ArrayList<I> result = new ArrayList<I>();
        if (this.pos + this.chunkSize > this.size) {
            for (int i = this.pos; i < this.size; ++i) {
                result.add(this.list.get(i));
            }
            this.pos = this.size;
        } else {
            for (int i = this.pos; i < this.pos + this.chunkSize; ++i) {
                result.add(this.list.get(i));
            }
            this.pos += this.chunkSize;
        }
        return result;
    }

    @Override
    public int getChunkSize() {
        return this.chunkSize;
    }

    public static <I> ListChunkIterator<I> of(List<I> list, int chunkSize) {
        return new ListChunkIterator<I>(list, chunkSize);
    }
}
