package com.sunwayworld.framework.data;

import com.sunwayworld.framework.exception.InvalidDataException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListChunkIterator<I> implements ChunkIterator<I> {
    private final List<I> list;
    private final int chunkSize;
    private final int size;
    private int pos;

    private ListChunkIterator(List<I> list, int chunkSize) {
        this.list = Objects.requireNonNull(list);
        this.chunkSize = Integer.valueOf(chunkSize);
        this.size = list.size();
        this.pos = 0;
    }

    public boolean hasNext() { return pos < size; }

    public List<I> nextChunk() {
        if (!hasNext()) throw new InvalidDataException("No more element.");
        List<I> result = new ArrayList<I>();
        if (pos + chunkSize > size) {
            int i = pos;
            while (i < size) result.add(list.get(i++));
            pos = size;
        } else {
            int i = pos;
            while (i < pos + chunkSize) result.add(list.get(i++));
            pos += chunkSize;
        }
        return result;
    }

    public int getChunkSize() { return chunkSize; }

    public static <I> ListChunkIterator<I> of(List<I> list, int chunkSize) {
        return new ListChunkIterator<I>(list, chunkSize);
    }
}
