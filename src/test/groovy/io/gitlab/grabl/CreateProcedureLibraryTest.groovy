package io.gitlab.grabl

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class CreateProcedureLibraryTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    CreateProcedureLibrary task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    CreateProcedureLibrary createTask(String name = 'createProcedureLibrary') {
        project.task(name, type: CreateProcedureLibrary)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def createTask = project.task('CreateProcedureLibrary', type: CreateProcedureLibrary)

        then: "task is an instance of CreateProcedureLibrary class"
        createTask instanceof CreateProcedureLibrary
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.destFile = "myPL.pl"
        task.encoding = "testEncoding"
        task.noCompress = false
        task.basedir = "src"
        task.excludes = "testexcludes"
        task.defaultExcludes = true

        then: "task properties reflect that change"
        task.destFile == "myPL.pl"
        task.encoding == "testEncoding"
        task.noCompress == false
        task.basedir == "src"
        task.excludes == "testexcludes"
        task.defaultExcludes == true
    }

    def "creates a .pl with the given options"() {
        given: "an instance of CreateProcedureLibrary"
        task.destFile = 'this.pl'
        task.cpInternal = 'UTF-8'
        task.defaultExcludes = false
        task.cpStream = 'UTF-8'
        
        when: "a PL is created"
        task.createPL()

        then: "PCTLibrary should be called"
        1 *  ant.PCTLibrary([
            'destFile':'this.pl', 
            'cpInternal':'UTF-8', 
            'defaultExcludes': false,
            'cpStream': 'UTF-8'
        ])
    }
}
