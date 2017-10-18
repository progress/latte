package io.gitlab.grabl

/**
 * A set which combines with a set of defaults
 *
 * The object delegates most calls to an internal settings set, but
 * implements parts of the Set interface to read from both self and
 * the supplied defaults object.
 *
 * @param <E> type of elements in the set
 */
class SettingsSet<E> implements Set<E> {
    @Delegate Set<E> settings = []
    Set<E> defaults

    SettingsSet(Set<E> defaults) {
        if (defaults == null) {
            throw new NullPointerException('defaults cannot be null')
        }
        this.defaults = defaults
    }

    /**
     * Returns true if this set contains the specified element.
     *
     * @param o element whose presence in this set is to be tested
     * @return true if this set contains the specified element
     */
    boolean contains(Object o) {
        this.settings.contains(o) || this.defaults.contains(o)
    }

    /**
     * Returns true if this set contains all of the elements of the specified collection.
     *
     * @param c collection to be checked for containment in this set
     * @return true if this set contains all of the elements of the specified collection
     */
    boolean containsAll(Collection<?> c) {
        this.settings.containsAll(c) || this.defaults.containsAll(c)
    }

    /**
     * Returns true if this set contains no elements.
     *
     * @return true if this set contains no elements
     */
    boolean isEmpty() {
        this.settings.isEmpty() && this.defaults.isEmpty()
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     *
     * @return the number of elements in this set (its cardinality)
     */
    int size() {
        this.settings.size() + this.defaults.size()
    }

    /**
     * Returns an array containing all of the elements in this set.
     *
     * @return an array containing all the elements in this set
     */
    Object[] toArray() {
        this.defaults.toArray() + this.settings.toArray()
    }
}
