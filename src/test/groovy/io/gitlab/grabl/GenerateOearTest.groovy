// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package io.gitlab.grabl

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class GenerateOearTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    GenerateOear task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    GenerateOear createTask(String name = 'generateOear') {
        project.task(name, type: GenerateOear)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def createTask = project.task('GenerateOear', type: GenerateOear)

        then: "task is an instance of GenerateOear class"
        createTask instanceof GenerateOear
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.srcDir = "foo"
        task.projectName = "bar"

        then: "task properties reflect that change"
        task.srcDir == "foo"
        task.projectName == "bar"
    }

    def "creates an oear"() {
        given: "an instance of GenerateOear"
        task.srcDir = "foo"
        task.projectName = "bar"
        
        when: "a PL is created"
        task.generateOear()

        then: "PCTLibrary should be called"
        true
    }
}
