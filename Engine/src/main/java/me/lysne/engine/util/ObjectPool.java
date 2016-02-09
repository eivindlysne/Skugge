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

    private final Class<T> type;
    private final Deque<T> free;
    private final Set<T> claimed;

    public ObjectPool(Class<T> type) {

        this(type, DEFAULT_SIZE);
    }

    public ObjectPool(Class<T> type, int size) {

        this.type = type;

        free = new ArrayDeque<>(size);
        claimed = new HashSet<>(size);

        try {
            for (int i = 0; i < size; i++) {
                free.add(type.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Error: Failed to initialize pool <" + type.getSimpleName() + ">!");
        }
    }

    public Class<T> type() {
        return type;
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
