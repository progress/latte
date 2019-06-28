// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
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
        project.files('src').files*.mkdir()
        project.files('src/delta1.df', 'src/delta2.df').files*.write('')

        when: "task properties are changed"
        task.unfreeze = false
        task.callbackClass = 'fakeClass'
        task.source('src')

        then: "task properties reflect that change"
        task.unfreeze == false
        task.callbackClass == 'fakeClass'

        println "Source: ${task.source.getFiles()}"
        task.source.getFiles().size() == 2
        task.source.getFiles().toArray()[0].getName().contains("delta1.df")
        task.source.getFiles().toArray()[1].getName().contains("delta2.df")

    }

    def "schema file is loaded into database"() {
        given: "a schema file to be loaded"
        task.source('src')
        task.unfreeze = true
        task.refid = 'foodb'
        
        when: "a schema is loaded "
        task.loadSchema()

        then: "PCTLoadSchema should be called"
        1 * ant.PCTLoadSchema(
            ['dlcHome' : extension.dlcHome.path,
            'unfreeze': true], 
            _ as Closure 
        ) >> { Map params, Closure configClosure ->
            println "PCTLoadSchema(${params}) &${configClosure.class}"
            // call configClosure the same way AntBuilder would do (delegating
            // to self) so we can test the closure the compile() method passes
            configClosure.delegate = ant
            configClosure()
            this
        }
        
        1 * ant.PCTConnection(['refid': 'foodb'])
    }
}
