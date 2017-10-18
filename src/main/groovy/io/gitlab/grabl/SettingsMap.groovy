package io.gitlab.grabl

/**
 * A map which falls back to defaults for any elements not available on self
 *
 * The object delegates most calls to an internal settings map, but
 * implements parts of the Map interface to read from both self and
 * the supplied defaults object.
 *
 * @param <K> type of keys in the map
 * @param <V> type of values in the map
 */
class SettingsMap<K, V> implements Map<K, V> {
    @Delegate Map<K, V> settings = [:]
    Map<K, V> defaults

    SettingsMap(Map<K, V> defaults) {
        if (defaults == null) {
            throw new NullPointerException('defaults cannot be null')
        }
        this.defaults = defaults
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    boolean containsKey(Object key) {
        this.settings.containsKey(key) || this.defaults.containsKey(key)
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    boolean containsValue(Object value) {
        this.settings.containsValue(value) ||
                this.defaults.containsValue(value)
    }

    /**
     * Returns a Set view of the mappings contained in this map.
     *
     * @return a set view of the mappings contained in this map
     */
    Set<Map.Entry<K, V>> entrySet() {
        this.defaults.entrySet() + this.settings.entrySet()
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    V get(Object key) {
        this.settings.getOrDefault(key, this.defaults.get(key))
    }

    /**
     * Returns the value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or defaultValue if this map contains no mapping for the key
     */
    V getOrDefault(Object key, V defaultValue) {
        this.settings.getOrDefault(
                key, this.defaults.getOrDefault(key, defaultValue))
    }

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return true if this map contains no key-value mappings
     */
    boolean isEmpty() {
        this.settings.isEmpty() && this.defaults.isEmpty()
    }

    /**
     * Returns a Set view of the keys contained in this map.
     *
     * @return a set view of the keys contained in this map
     */
    Set<K> keySet() {
        this.defaults.keySet() + this.settings.keySet()
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    int size() {
        this.settings.size() + this.defaults.size()
    }

    /**
     * Returns a Collection view of the values contained in this map.
     *
     * @return a collection view of the values contained in this map
     */
    Collection<V> values() {
        this.defaults.values() + this.settings.values()
    }
}
