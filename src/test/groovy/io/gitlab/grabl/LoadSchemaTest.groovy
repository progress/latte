package io.gitlab.grabl

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class LoadSchemaTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    LoadSchema task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    LoadSchema createTask(String name = 'loadSchema') {
        project.task(name, type: LoadSchema)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def createTask = project.task('LoadSchema', type: LoadSchema)

        then: "task is an instance of LoadSchema class"
        createTask instanceof LoadSchema
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.unfreeze = false
        task.callbackClass = true
        task.srcFile = "testfile.df"

        then: "task properties reflect that change"
        task.unfreeze == false
        task.callbackClass == true
        task.srcFile == "testfile.df"
    }

    def "schema file is loaded into database"() {
        given: "a schema file to be loaded"
        task.srcFile = "testfile.df"
        
        when: "a schema is loaded "
        task.loadSchema()

        then: "PCTLoadSchema should be called"
        1 *  ant.PCTCreateBase([
            'srcFile':'testfile.df', 
        ])
    }
}
