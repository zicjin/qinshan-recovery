package com.sunwayworld.framework.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Function;
import java.util.function.BiFunction;
import com.sunwayworld.framework.exception.CloneException;

/** Java 8 case-insensitive map used by recovered extension fields. */
public class CaseInsensitiveLinkedMap<V> extends LinkedHashMap<String, V> {
    private static final long serialVersionUID = 9078487774509185313L;
    public CaseInsensitiveLinkedMap() { super(); }
    public CaseInsensitiveLinkedMap(int initialCapacity) { super(initialCapacity); }
    public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor) { super(initialCapacity, loadFactor); }
    public CaseInsensitiveLinkedMap(int initialCapacity, float loadFactor, boolean accessOrder) { super(initialCapacity, loadFactor); }
    public CaseInsensitiveLinkedMap(Map<? extends String, ? extends V> values) { super(normalize(values)); }
    @SuppressWarnings("unchecked")
    private static <V> Map<String, V> normalize(Map<? extends String, ? extends V> values) {
        Map<String, V> copy;
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(values); }
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
                copy = (Map<String, V>) in.readObject();
            }
        } catch (java.io.IOException | ClassNotFoundException e) {
            throw new CloneException(values, e);
        }
        for (String original : new HashSet<String>(copy.keySet())) {
            String normalized = key(original);
            if (!original.equals(normalized)) {
                V value = values.get(original);
                copy.remove(original);
                copy.put(normalized, value);
            }
        }
        return copy;
    }
    private static String key(Object key) { return key == null ? null : key instanceof String ? ((String) key).toLowerCase() : key.toString(); }
    @Override public V get(Object key) { return super.get(key(key)); }
    @Override public V getOrDefault(Object key, V fallback) { return super.getOrDefault(key(key), fallback); }
    @Override public boolean containsKey(Object key) { return super.containsKey(key(key)); }
    @Override public V put(String key, V value) { return super.put(key(key), value); }
    @Override public void putAll(Map<? extends String, ? extends V> values) {
        super.putAll(normalize(values));
    }
    @Override public V remove(Object key) { return super.remove(key(key)); }
    @Override public V putIfAbsent(String key, V value) { return super.putIfAbsent(key(key), value); }
    @Override public boolean remove(Object key, Object value) { return super.remove(key(key), value); }
    @Override public boolean replace(String key, V oldValue, V value) { return super.replace(key(key), oldValue, value); }
    @Override public V replace(String key, V value) { return super.replace(key(key), value); }
    @Override public V computeIfAbsent(String key, Function<? super String, ? extends V> function) { return super.computeIfAbsent(key(key), function); }
    @Override public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> function) { return super.computeIfPresent(key(key), function); }
    @Override public V compute(String key, BiFunction<? super String, ? super V, ? extends V> function) { return super.compute(key(key), function); }
    @Override public V merge(String key, V value, BiFunction<? super V, ? super V, ? extends V> function) { return super.merge(key(key), value, function); }
}
