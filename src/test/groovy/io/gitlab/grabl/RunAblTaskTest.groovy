package io.gitlab.grabl

import org.gradle.api.AntBuilder
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class RunAblTaskTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    RunAbl task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    RunAbl createTask(String name = 'RunAbl') {
        project.task(name, type: RunAbl)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def runTask = project.task('RunAblTest', type: RunAbl)

        then: "task is an instance of RunAbl class"
        runTask instanceof RunAbl
    }

    def "it has default values for properties based on extension"() {
        expect: "default values to delegate to extension"
        // from AbstractCompile
        task.procedure == null
        task.propath == extension.propath
        task.dbConnections.isEmpty()
        task.environment.isEmpty()

        when: "extension properties are changed"
        extension.propath = project.files('src')
        extension.dbConnections('foodb')
        extension.environment.put("bob", "mary")

        then: "task properties change too"
        task.propath == extension.propath
        task.dbConnections.contains('foodb')
        task.environment.containsKey("bob")
    }    

    def "task properties can be changed without affecting extension properties"() {
        given: "a fresh instance GrablExtension"
        GrablExtension defExtension = new GrablExtension(project)

        when: "task properties are changed"
        task.propath = project.files('src')
        task.dbConnections << 'foodb'
        task.environment.put("bob", "mary")

        then: "extension properties are not affected"
        extension.propath.files == defExtension.propath.files
        extension.dbConnections == defExtension.dbConnections
        extension.environment == defExtension.environment
    }    

    def "property values on task take precedence over extension"() {
        when: "a task property is reset and same extension property is changed"
        task.propath = project.files('src')
        task.environment.put("bob", "mary")
        extension.propath = project.files('ablsrc')
        extension.environment.put("bob", "alice")

        then: "task property stays with its reset value"
        task.propath.files == project.files('src').files
        task.environment.containsKey("bob")
        task.environment.containsValue("mary")
    }    

    def "selected property values are merged from extension and task"() {
        when: "List or Map properties are modified on both extension and the task"
        task.dbConnections << 'foodb'
        extension.dbConnections('bardb')
        task.environment.put("bob", "mary")
        extension.environment.put("dick", "jane")

        then: "both modifications apply (merge)"
        task.dbConnections.contains('foodb')
        task.dbConnections.contains('bardb')
        task.environment.containsKey("bob")
        task.environment.containsKey("dick")
    }    

    def "PCTRun is called"() {
        given: "an instance of RunAbl with a procedure set"
        task.procedure = "runme"

        when: "run is called"
        task.run()

        then: "PCTCompile is passed the extra args"
        1 * ant.PCTRun(
            [
            procedure: task.procedure],
            _ as Closure
        )
    }   

    def "PCTRun is called is custom dlcHome"() {
        given: "an instance of RunAbl with a procedure set"
        task.procedure = "runme"
        task.dlcHome = new File ('testdlchome')

        when: "run is called"
        task.run()

        then: "PCTCompile is passed the extra args"
        1 * ant.PCTRun(
            [dlcHome: 'testdlchome',
            procedure: task.procedure],
            _ as Closure
        )
    }     

    def "PCTRun is called with a database connection"() {
        given: "an instance of RunAbl with a procedure set"
        task.procedure = "runme"
        task.dbConnections << 'foodb'

        when: "a DB connection reference is added"
        task.run()

        then: "a DBConnection is created"
        // define all expected interactions here so we don't have to repeat the
        // param (closure) processing closure
        1 * ant.PCTRun(
            [procedure: task.procedure],
            _ as Closure
        ) >> { Map params, Closure configClosure ->
            println "PCTRun(${params}) &${configClosure.class}"
            // call configClosure the same way AntBuilder would do (delegating
            // to self) so we can test the closure the compile() method passes
            configClosure.delegate = ant
            configClosure()
            this
        }        
        1 * ant.DBConnection([refid: 'foodb'])
    }    

   def "PCTRun is called with options"() {
        given: "an instance of RunAbl with a procedure set"
        task.procedure = "runme"

        when: "a DB connection reference is added"
        task.options = ["bob" : "marley"]
        task.run()

        then: "an run option is specified "
        // define all expected interactions here so we don't have to repeat the
        // param (closure) processing closure
        1 * ant.PCTRun(
            [procedure: task.procedure],
            _ as Closure
        ) >> { Map params, Closure configClosure ->
            println "PCTRun(${params}) &${configClosure.class}"
            // call configClosure the same way AntBuilder would do (delegating
            // to self) so we can test the closure the compile() method passes
            configClosure.delegate = ant
            configClosure()
            this
        }        
        1 * ant.PCTRunOption(name : "bob", value : "marley")
    }       

   def "PCTRun is called with environment"() {
        given: "an instance of RunAbl with a procedure set"
        task.procedure = "runme"

        when: "a DB connection reference is added"
        task.environment = ["bob" : "marley"]
        task.run()

        then: "a run option is specified "
        // define all expected interactions here so we don't have to repeat the
        // param (closure) processing closure
        1 * ant.PCTRun(
            [procedure: task.procedure],
            _ as Closure
        ) >> { Map params, Closure configClosure ->
            println "PCTRun(${params}) &${configClosure.class}"
            // call configClosure the same way AntBuilder would do (delegating
            // to self) so we can test the closure the compile() method passes
            configClosure.delegate = ant
            configClosure()
            this
        }        
        1 * ant.env(key : "bob", value : "marley")
    }    

}
