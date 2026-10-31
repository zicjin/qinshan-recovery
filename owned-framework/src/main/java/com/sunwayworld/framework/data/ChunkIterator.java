package com.sunwayworld.framework.data;

import java.util.List;

public interface ChunkIterator<I> {
    boolean hasNext();
    List<I> nextChunk();
    int getChunkSize();
}
