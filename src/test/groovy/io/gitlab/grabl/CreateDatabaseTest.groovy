// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class CreateDatabaseTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    CreateDatabase task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    CreateDatabase createTask(String name = 'createDatabase') {
        project.task(name, type: CreateDatabase)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def createTask = project.task('CreateDatabase', type: CreateDatabase)

        then: "task is an instance of CreateDatabase class"
        createTask instanceof CreateDatabase
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.dbName = "testfoo.db"

        then: "task properties reflect that change"
        task.dbName == "testfoo.db"
    }

    def "creates an empty database as a default"() {
        given: "an instance of CreateDatabase with no sourceDb specified"
        task.dbName = 'emptydb.db'
        task.destDir = 'build'
        task.auditIndexArea = 'fakeIndexArea'
        task.blockSize = 8
        
        when: "a db is created"
        task.createDB()

        then: "PCTCreateBase should be called"
        1 *  ant.PCTCreateBase([
            'dlcHome' : "${extension.dlcHome}",
            'dbname':'emptydb.db', 
            'destDir':'build', 
            'auditIndexArea': 'fakeIndexArea',
            'blockSize': 8
        ])
    }
}
