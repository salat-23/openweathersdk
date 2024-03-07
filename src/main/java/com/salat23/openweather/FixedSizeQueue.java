package com.salat23.openweather;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class FixedSizeQueue<T> implements Iterable<T> {
    private final LinkedHashSet<T> queue;
    private final int maxSize;

    public FixedSizeQueue(int maxSize) {
        this.queue = new LinkedHashSet<>();
        this.maxSize = maxSize;
    }

    public synchronized void add(T element) {
        if (queue.size() == maxSize) {
            Iterator<T> it = queue.iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        if (queue.contains(element)) {
            queue.remove(element);
            queue.add(element);
        } else queue.add(element);
    }

    public synchronized T peek() {
        return queue.isEmpty() ? null : queue.iterator().next();
    }

    public synchronized T poll() {
        Iterator<T> it = queue.iterator();
        T firstElement = null;
        if (it.hasNext()) {
            firstElement = it.next();
            it.remove();
        }
        return firstElement;
    }

    public synchronized boolean contains(T element) {
        return queue.contains(element);
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
    
    @Override
    public synchronized String toString() {
        return queue.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return queue.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        queue.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return queue.spliterator();
    }
}