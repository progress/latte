package oe.espresso.latte

import spock.lang.Specification

class SettingsSetTest extends Specification {
    def "it can be created with a List of defaults to fall back to"() {
        when: "creating it with a List parameter"
        def settings = new SettingsSet([] as Set)

        then: "result is an instance of SettingsSet"
        settings instanceof SettingsSet
    }

    /**
     * If falling back to defaults is not needed might as well just use
     * a plain Map.
     */
    def "it fails when no defaults object is given"() {
        when: "creating it with null parameter"
        def settings = new SettingsSet(null)

        then: "a NullPointerException exception is thrown"
        NullPointerException e = thrown()
        e.message.contains('defaults cannot be null')
    }

    /**
     * If falling back to defaults is not needed might as well just use
     * a plain Map.
     */
    def "uniqueness constraints are preseved with defaults"() {
        given: "a set is created with a default containing a value"
        def settings = new SettingsSet(['foo'] as Set)
        def collected = []


        when: "a duplicate entry is added to self, then iterate"
        settings.add('foo')
        settings.each {
            collected << it
        }

        then: "only unique was iterated"
        collected == ['foo']
    }    

    def "it delegates read methods to defaults when not available on self"() {
        given: "a SettingsSet object with default element 'foo'"
        def settings = new SettingsSet(['foo'] as Set)

        expect: "accessing settings will yield default value"
        settings.contains('foo')
        settings.containsAll(['foo'])
        !settings.isEmpty()
        settings.size() == 1
        settings.toArray() == ['foo']
    }

    def "it does not affect defaults when using write methods"() {
        given: "a defaults list and a SettingsSet object"
        def defaults = ['bar'] as Set
        def settings = new SettingsSet(defaults)

        when: "saving settings"
        settings.add('foo')
        settings.add('bar')

        then: "defaults object does not change"
        defaults == ['bar'] as Set

        when: "clearing settings"
        settings.add('bar')
        settings.clear()

        then: "defaults object does not change"
        defaults == ['bar'] as Set
    }

    def "it reflects changes to the underlying defaults object"() {
        given: "a defaults list and a SettingsSet object"
        def defaults = ['bar'] as Set
        def settings = new SettingsSet(defaults)

        expect: "defaults are used"
        settings.contains('bar')

        when: "defaults are changed"
        defaults.remove('bar')
        defaults.add('baz')

        then: "changed defaults are reflected in settings"
        !settings.contains('bar')
        settings.contains('baz')
    }

    def "it combines the results from defaults and self"() {
        given: "a SettingsSet with defaults and own elements"
        def settings = new SettingsSet(['foo'] as Set)
        settings.addAll(['bar', 'baz'])

        expect: "defaults and own elements to be combined like a single set"
        settings.size() == 3
        settings.toArray() == ['foo', 'bar', 'baz']
    }

    def "each iterates the results from defaults and self"() {
        given: "a SettingsSet with defaults and own elements"
        def settings = new SettingsSet(['foo'] as Set)
        def counted = 0
        settings.addAll(['bar', 'baz'])

        expect: "size is 3"
        settings.size() == 3

        when: "each item is counted"
        settings.each { 
            counted++
        }

        then: "the size and the iteration count to be the same"
        counted == settings.size()
    }    
}
