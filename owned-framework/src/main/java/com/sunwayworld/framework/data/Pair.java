package com.sunwayworld.framework.data;

import java.io.Serializable;

/** Mutable pair used by mapper ordering and typed constructor arguments. */
public class Pair<S, T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private S first;
    private T second;
    public static <S, T> Pair<S, T> of(S first, T second) { return new Pair<S, T>(first, second); }
    protected Pair(S first, T second) { this.first = first; this.second = second; }
    public S getFirst() { return first; }
    public T getSecond() { return second; }
    public void setFirst(S value) { first = value; }
    public void setSecond(T value) { second = value; }
}
