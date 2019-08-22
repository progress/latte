package oe.espresso.latte

import spock.lang.Specification

class SettingsMapTest extends Specification {
    def "it can be created with a Map of defaults to fall back to"() {
        when: "creating it with a Map parameter"
        def settings = new SettingsMap([:])

        then: "result is an instance of SettingsMap"
        settings instanceof SettingsMap
    }

    /**
     * If falling back to defaults is not needed might as well just use
     * a plain Map.
     */
    def "it fails when no defaults object is given"() {
        when: "creating it with null parameter"
        def settings = new SettingsMap(null)

        then: "a NullPointerException exception is thrown"
        NullPointerException e = thrown()
        e.message.contains('defaults cannot be null')
    }

    def "it delegates read methods to defaults when not available on self"() {
        given: "a SettingsMap object with default value for 'foo'"
        def settings = new SettingsMap([foo: 2])

        expect: "accessing 'foo' will yield default value"
        settings.containsKey('foo')
        settings.containsValue(2)
        settings.foo == 2
        settings.get('foo') == 2
        settings.getOrDefault('foo', 0) == 2
        !settings.isEmpty()
        settings.size() == 1

        settings.entrySet().any { it.key == 'foo' }
        settings.keySet().contains('foo')
        settings.values().contains(2)
    }

    def "it does not affect defaults when using write methods"() {
        given: "a defaults map and a SettingsMap object"
        def defaults = [bar: 4]
        def settings = new SettingsMap(defaults)

        when: "saving settings"
        settings.foo = 1
        settings.bar = 2

        then: "defaults object does not change"
        defaults == [bar: 4]

        when: "clearing settings"
        settings.bar = 2
        settings.clear()

        then: "defaults object does not change"
        defaults == [bar: 4]
    }

    def "it reflects changes to the underlying defaults object"() {
        given: "a defaults map and a SettingsMap object"
        def defaults = [bar: 4]
        def settings = new SettingsMap(defaults)

        expect: "defaults are used"
        settings.bar == 4

        when: "defaults are changed"
        defaults.bar = 8

        then: "changed defaults are reflected in settings"
        settings.bar == 8
    }
}
