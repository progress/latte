package io.gitlab.grabl


/**
 * A class that creates PCT's DBConnection instances for DBs defined in
 * environment.
 * <p>
 * The {@code DBConnection}s will be created as references with ID equal
 * to the database logical name (alias).  This way they can be
 * referenced from within a {@code PCTCompile} task like:
 *
 * <pre>
 * {@code
 *   def dbcm = new DBConnectionManager()
 *   dbcm.addToAntBuilder(ant)
 *
 *   ant.PCTCompile() {
 *     DBConnection(refid: 'dbalias')
 *   }
 * }
 * </pre>
 *
 * The environment variables defining database connection parameters
 * must match the pattern {@code <dbPrefix>_DB<parameter>=<value>}, e.g.
 * {@code FOO_DBDIR=/srv/foo}.
 */
class DBConnectionManager {
    Map<String, Map<String, String>> db
    private Map<String, String> envVarToPropMap = [
        name: 'dbName',
        lname: 'logicalName',
        dir: 'dbDir',
        host: 'hostName',
        port: 'dbPort',
    ].asImmutable()

    DBConnectionManager() {
        this(System.getenv())
    }

    DBConnectionManager(Map<String, String> envVars) {
        db = [:]

        def parseEnvVar = { String var ->
            var.split('_DB')*.toLowerCase()
        }

        envVars.findAll { it.key.contains('_DB') }.groupBy {
            // group by variable prefix (dbVarPrefix) for starters so
            // all variables pertaining to one DB are processed together
            //it.key.split('_DB')[0].toLowerCase()
            parseEnvVar(it.key)[0]
        }.collectEntries(db) { dbVarPrefix, vars ->
            // use DB alias (logical name) as Ant resouce ID if
            // available (falling back to dbVarPrefix which is equal to
            // the DB name)
            def dbAlias = vars.getOrDefault(
                "${dbVarPrefix.toUpperCase()}_DBLNAME".toString(), dbVarPrefix);

            if (db.containsKey(dbAlias))
                throw new IllegalStateException(
                    "Multiple DBs with same alias (logical name): " +
                    db[dbAlias].dbName + ", " + dbVarPrefix);

            vars = vars.collectEntries { var, value ->
                var = parseEnvVar(var)[1]

                // ignore unknown variables
                if (!envVarToPropMap.containsKey(var))
                    return [:]

                [(envVarToPropMap[var]): value]
            }

            [(dbAlias): vars]
        }
    }

    def addToAntBuilder(builder, String nodeName = null) {
        // TODO: make readOnly mode configurable (+ a way to set
        //   defaults per manager instance)
        // NOTE: had to tweak the tests to expect hardcoded
        //   [readOnly: true] for now too
        db.each { dbId, params ->
            builder.DBConnection(id: dbId, readOnly: true, *:params)
        }
    }
}
