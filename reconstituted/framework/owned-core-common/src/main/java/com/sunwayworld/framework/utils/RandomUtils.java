/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class RandomUtils {
    private static final Random RANDOM = new Random();

    public static final <T> T random(T ... items) {
        if (items == null || items.length == 0) {
            return null;
        }
        return items[RANDOM.nextInt(items.length)];
    }

    public static final <T> T random(Collection<T> itemList) {
        if (itemList == null || itemList.isEmpty()) {
            return null;
        }
        int index = RANDOM.nextInt(itemList.size());
        Iterator<T> iterator = itemList.iterator();
        while (index-- > 0) {
            iterator.next();
        }
        return iterator.next();
    }

    public static final <T> List<T> shuffle(List<T> itemList) {
        if (itemList.size() <= 1) {
            return itemList;
        }
        ArrayList<T> cloneList = new ArrayList<T>(itemList);
        ArrayList resultList = new ArrayList();
        while (!cloneList.isEmpty()) {
            int index = RANDOM.nextInt(cloneList.size());
            Object nextItem = cloneList.get(index);
            cloneList.remove(index);
            resultList.add(nextItem);
        }
        return resultList;
    }

    public static final List<Integer> shuffle(int startInclusive, int endExclusive) {
        ArrayList<Integer> intList = new ArrayList<Integer>();
        for (int i = startInclusive; i < endExclusive; ++i) {
            intList.add(i);
        }
        return RandomUtils.shuffle(intList);
    }
}
