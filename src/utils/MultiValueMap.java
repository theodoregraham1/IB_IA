package utils;

import java.util.*;

public class MultiValueMap<K, V>
        implements Map<K, V> {
    private final HashMap<K, ArrayList<V>> map;

    public MultiValueMap() {
        map = new HashMap<>();
    }

    @Override
    public int size() {
        return this.entrySet().size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @Deprecated
    public V get(Object key) {
        return map.get(key).get(0);
    }

    @Override
    public V put(K key, V value) {
        if (!map.containsKey(key)) {
            map.put(key, new ArrayList<>());
        }
        map.get(key).add(value);

        return value;
    }

    @Override
    @Deprecated
    public V remove(Object key) {
        return map.remove(key).get(0);
    }

    @Override
    public boolean remove(Object key, Object value) {
        ArrayList<V> values = map.get(key);

        if (values == null) return false;

        return values.remove(value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> e: m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        ArrayList<V> values = new ArrayList<>();

        for (K key: keySet()) {
            values.addAll(map.get(key));
        }

        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> entries = new HashSet<>();

        for (Entry<K, ArrayList<V>> entry: map.entrySet()) {
            for (V value: entry.getValue()) {
                entries.add(Map.entry(entry.getKey(), value));
            }
        }
        return entries;
    }
}
