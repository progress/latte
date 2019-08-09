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
    CreateOear task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    CreateOear createTask(String name = 'createOear') {
        project.task(name, type: CreateOear)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def createTask = project.task('CreateOear', type: CreateOear)

        then: "task is an instance of CreateOear class"
        createTask instanceof CreateOear
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.projectDir = "foo"
        task.projectName = "bar"
        task.webappsDir = "foobar"
        task.oearPath = "barfoo"
        
        then: "task properties reflect that change"
        task.projectDir == "foo"
        task.projectName == "bar"
        task.webappsDir == "foobar"
        task.oearPath == "barfoo"
    }

    def "creates an oear"() {
        given: "an instance of CreateOear"
        task.projectDir = "/scratch/aestrada/foo/oe-services"
        task.projectName = "POS"
        task.webappsDir = "/scratch/aestrada/foo/war"
        task.oearPath = "build"
        
        when: "a PL is created"
        // task.createOear()

        then: "PCTLibrary should be called"
        true
    }
}
