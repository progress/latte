package io.gitlab.hendosdo.grabl

import org.gradle.testfixtures.ProjectBuilder

import java.nio.file.attribute.AclFileAttributeView

import org.gradle.api.AntBuilder
import org.gradle.api.Project
import spock.lang.Specification


class CompileAblTaskTest extends Specification {
    Project project

    void setup() {
        project = ProjectBuilder.builder().build()
    }

    CompileAblTask createTask(String name = 'compileAbl') {
        project.task(name, type: CompileAblTask)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def task = project.task('compileAbl', type: CompileAblTask)

        then: "task is an instance of CompileAblTask class"
        task instanceof CompileAblTask
    }

    def "it has default values for properties"() {
        given: "an instance of the CompileAblTask"
        def compile = createTask()

        expect: "default values"
        // from AbstractCompile
        compile.destinationDir == null
        compile.source.isEmpty()
        compile.propath == null
        compile.dbConnections.isEmpty()
        compile.compileArgs == [:]
    }

    def "compile action creates resources necessary to compile using PCT"() {
        given: "a project with AntBuilder and an instance of CompileAblTask"
        AntBuilder ant = GroovyMock()
        project.ant = ant

        def task = createTask()
        task.destinationDir = project.file('destDir')

        // define all expected interactions here so we don't have to repeat the
        // param (closure) processing closure
        3 * ant.PCTCompile(
            [destDir: task.destinationDir.path],
            _ as Closure
        ) >> { Map params, Closure configClosure ->
            println "PCTCompile(${params}) &${configClosure.class}"
            // call configClosure the same way AntBuilder would do (delegating
            // to self) so we can test the closure the compile() method passes
            configClosure.delegate = ant
            configClosure()
            this
        }

        when: "compile action is called"
        task.compile()

        then: "dest dir is created, PCTCompile Ant task is created but no DBConnections are created"
        task.destinationDir.isDirectory()
        0 * ant.DBConnection(*_)

        when: "a DB connection reference is added"
        task.dbConnections << 'foodb'
        task.compile()

        then: "a DBConnection is created"
        1 * ant.DBConnection([refid: 'foodb'])

        when: "propath property is set"
        task.propath = project.files('src')
        task.compile()

        then: "propath Ant type is configured"
        1 * ant.propath(_ as Closure) >> { Closure cfgr ->
            cfgr.delegate = ant
            cfgr()
            this
        }
        // NOTE: this relies on implementation of
        // FileCollection.addToAntBuilder and specifically
        // AntFileCollectionBuilder which it delegates to
        1 * ant.file([file: project.files('src').getSingleFile().path])

        // TODO: not sure how to test that sources are added to AntBuilder
        //   this is done using FileCollection.addToAntBuilder but to
        //   define expected interaction on our ant mock we'd need to
        //   rely on implementation details of addToAntBuilder()
    }

    def "compiler args are passed to PCTCompile"() {
        given: "a project with AntBuilder and an instance of CompileAblTask"
        AntBuilder ant = GroovyMock()
        project.ant = ant

        def task = createTask()
        task.destinationDir = project.file('destDir')

        when: "compiler args are populated"
        task.compileArgs.listing = true
        task.compileArgs.preprocess = true
        task.compile()

        then: "PCTCompile is passed the extra args"
        1 * ant.PCTCompile(
            [destDir: task.destinationDir.path, listing: true, preprocess: true],
            _ as Closure
        )
    }
}
