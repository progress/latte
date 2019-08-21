package oe.espresso.latte

import org.gradle.api.AntBuilder
import spock.lang.Specification


class DBConnectionManagerTest extends Specification {
    def "it uses variables injected into constructor"() {
        when: "DBConnectionManager is instantiated with explicit variables for FOO"
        def dbcm = new DBConnectionManager(
            [FOO_DBNAME: 'foo', FOO_DBDIR: '/srv/foo'])

        then: "parameters for FOO are available under .db.foo property"
        dbcm.db.foo.dbName == 'foo'
        dbcm.db.foo.dbDir == '/srv/foo'
    }

    def "it does not use environment when variables injected into constructor"() {
        given: "variables for BAR exist in the environment"
        GroovySpy(System, global: true)
        0 * System.getenv() >> {
            callRealMethod() + [BAR_DBNAME: bar, BAR_DBDIR: '/srv/bar']
        }

        when: "DBConnectionManager is instantiated with explicit variables"
        def dbcm = new DBConnectionManager(
            [FOO_DBNAME: 'foo', FOO_DBDIR: '/srv/foo'])

        then: "parameters for BAR are NOT available"
        !dbcm.db.containsKey('bar')
    }

    def "it uses variables from environment when not injected into constructor"() {
        given: "variables for BAR exist in the environment"
        GroovySpy(System, global: true)
        1 * System.getenv() >> {
            callRealMethod() + [BAR_DBNAME: 'bar', BAR_DBDIR: '/srv/bar']
        }

        when: "DBConnectionManager is instantiated without explicit variables"
        def dbcm = new DBConnectionManager()

        then: "parameters for BAR are available under .db.bar property"
        dbcm.db.containsKey('bar')
        dbcm.db.bar.dbDir == '/srv/bar'
    }

    def "it should populate all supported DBConnection properties"() {
        given: "an AntBuilder instance and a DBConnectionManager"
        AntBuilder ant = GroovyMock()

        when: "DBConnectionManager is instantiated"
        def dbcm = new DBConnectionManager(env)

        then: "DBConnection nodes containing all defined properties are created"
        dbcm.db.containsKey(id)
        dbcm.db[id] == res

        where:
        [id, env, res] << allVarsTestSample('foo', 'testbar')
    }

    def "it creates DBConnection nodes based on env in AntBuilder"() {
        given: "an AntBuilder instance and a DBConnectionManager"
        AntBuilder ant = GroovyMock()
        def env = [FOO_DBNAME: 'foo', *:allSampleDBVars('testbar')]
        def dbcm = new DBConnectionManager(env)

        when: "addToAntBuilder is called"
        dbcm.addToAntBuilder(ant, null)

        then: "DBConnection nodes are created with ID matching DB alias"
        1 * ant.DBConnection([
            id: 'foo',
            readOnly: true,
            dbName: 'foo'
        ])
        1 * ant.DBConnection([
            id: 'bar',
            readOnly: true,
            dbName: 'testbar',
            logicalName: 'bar',
            dbDir: env['TESTBAR_DBDIR'],
            hostName: env['TESTBAR_DBHOST'],
            dbPort: env['TESTBAR_DBPORT']
        ])
    }

    def "it should ignore any unknown env variables"() {
        when: "DBConnectionManager is instantiated with unknown variables for FOO"
        def dbcm = new DBConnectionManager(
            [FOO_DBNAME: 'foo', FOO_DBUNKNOWN_VAR: '@test_UNKNOWN_value@'])

        then: "unknown FOO variables are not available under .db.foo property"
        !dbcm.db.foo.containsValue('@test_UNKNOWN_value@')
    }

    def "it fails when two databases specify the same alias (logical name)"() {
        given: "an AntBuilder instance and an env with conflicting databases"
        AntBuilder ant = GroovyMock()
        def env = allSampleDBVars('foo') + allSampleDBVars('testfoo')

        when: "DBConnectionManager is instantiated"
        def dbcm = new DBConnectionManager(env)

        then: "IllegalStateException is thrown"
        IllegalStateException e = thrown()
        e.message.contains('foo')
        e.message.contains('testfoo')
    }

    /**
     * Return a map of all supported env variables for a sample DB
     */
    def allSampleDBVars(String dbName) {
        String vpfx = dbName.toUpperCase()
        /* the final collectEntries call is to map all keys to java
         * String, i.e. to prevent using GStrings as map keys as there
         * are numerous issues with that, see:
         *  - http://stackoverflow.com/q/31713359
         *  - https://codedump.io/share/bHS3JbPjVjA0/1/groovy-strings-vs-java-strings
         *  - https://codedump.io/share/zfSc5he3XHmS/1/why-map-does-not-work-for-gstring-in-groovy
         */
        return [
            "${vpfx}_DBNAME": dbName,
            "${vpfx}_DBLNAME": dbName.replaceFirst('test', ''),
            "${vpfx}_DBDIR": "/srv/${dbName}",
            "${vpfx}_DBHOST": "localhost",
            "${vpfx}_DBPORT": (Math.random() * 60000).round(),
        ].collectEntries { k, v -> [(k.toString()): v] }
    }

    /**
     * Return test samples containing all variables for data driven test
     *
     * A sample is returned for each given dbName.
     *
     * Each sample is a List with first item being a DB ID (key in
     * DBConnectionManager.db map), the second a map of env vars as
     * returned by allSampleDBVars and third item being the expected
     * map that should be produced by DBConnectionManager.
     *
     * @return Collection a list of samples
     */
    def allVarsTestSample(String... dbNames) {
        def samples = []

        dbNames.each { dbName ->
            String dbVarPrefix = dbName.toUpperCase()
            def env = allSampleDBVars(dbName)
            def res = [
                dbName: env["${dbVarPrefix}_DBNAME".toString()],
                logicalName: env["${dbVarPrefix}_DBLNAME".toString()],
                dbDir: env["${dbVarPrefix}_DBDIR".toString()],
                hostName: env["${dbVarPrefix}_DBHOST".toString()],
                dbPort: env["${dbVarPrefix}_DBPORT".toString()],
            ]

            samples << [res.logicalName, env, res]
        }

        return samples
    }
}
