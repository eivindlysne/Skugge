package me.lysne.engine.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;


/**
 * TODO: Bitset implementation?
 * @param <T>
 */
public class ObjectPool<T> {

    public static final int DEFAULT_SIZE = 20;

    private final Deque<T> free;
    private final Set<T> claimed;

    public ObjectPool() {

        this(DEFAULT_SIZE);
    }

    public ObjectPool(int size) {

        free = new ArrayDeque<>(size);
        claimed = new HashSet<>(size);
    }

    public T claim() {

        T toClaim = free.remove();
        claimed.add(toClaim);
        return toClaim;
    }

    public void release(T released) {

        claimed.remove(released);
        free.add(released);
    }
}
