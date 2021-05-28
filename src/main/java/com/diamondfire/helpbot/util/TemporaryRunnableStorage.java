package com.diamondfire.helpbot.util;

import java.util.*;
import java.util.concurrent.*;

public class TemporaryRunnableStorage<K, V> {
    
    private static final ScheduledExecutorService STORAGE_WORKER = Executors.newScheduledThreadPool(1);
    private static final int DELAY = 5;
    private static final TimeUnit DELAY_UNIT = TimeUnit.MINUTES;
    
    private final Map<K, Container> storage = new HashMap<>();
    
    public void put(K key, V value, Runnable onPurge, boolean persistent) {
        Container removed = this.storage.remove(key);
        if (removed != null) {
            removed.onPurge.run();
        }
        
        this.storage.put(key, new Container(STORAGE_WORKER.schedule(() -> {
            onPurge.run();
            this.storage.remove(key);
        }, DELAY, DELAY_UNIT), value, persistent, onPurge));
    }
    
    public void put(K key, V value, Runnable onPurge) {
        put(key, value, onPurge, false);
    }
    
    public V get(K key) {
        Container container = storage.get(key);
        if (container == null) {
            return null;
        }
        
        return container.value;
    }
    
    public void expireKey(K key) {
        Container container = storage.remove(key);
        
        ScheduledFuture<?> future = container.getFuture();
        future.cancel(true);
        if (container.persistent()) {
            put(key, container.getValue(), container.getOnPurge(), true);
        }
    }
    
    public boolean containsKey(K key) {
        return storage.containsKey(key);
    }
    
    public Set<K> getKeys() {
        return storage.keySet();
    }
    
    private class Container {
        
        private final ScheduledFuture<?> future;
        private final V value;
        
        private final boolean persistent;
        private final Runnable onPurge;
        
        public Container(ScheduledFuture<?> future, V value, boolean persistent, Runnable onPurge) {
            this.future = future;
            this.value = value;
            this.persistent = persistent;
            this.onPurge = onPurge;
        }
        
        public ScheduledFuture<?> getFuture() {
            return future;
        }
        
        public V getValue() {
            return value;
        }
        
        public boolean persistent() {
            return persistent;
        }
        
        public Runnable getOnPurge() {
            return onPurge;
        }
    }
    
}
