// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class DBConnectionTest extends Specification {
    Project project
    AntBuilder ant
    LatteExtension extension
    DBConnection task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(LatteExtension.NAME, LatteExtension, project)
        extension = project.extensions.getByType(LatteExtension)
        task = createTask()
    }

    DBConnection createTask(String name = 'connectDB') {
        project.task(name, type: DBConnection)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def connectTask = project.task('DBConnection', type: DBConnection)

        then: "task is an instance of DBConnection class"
        connectTask instanceof DBConnection
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.dbName = "sports2020"
        task.dbDir = "testdb/"
        task.id = "foodb"
        task.singleUser = true
       
        then: "task properties reflect that change"
        task.dbName == "sports2020"
        task.dbDir == "testdb/"
        task.id == "foodb"
        task.singleUser == true
    }

    def "creates a dbconnection with the given options"() {
        given: "an instance of DBConnection"
        task.dbName = "sports2020"
        task.dbDir = "testdb/"
        task.id = "foodb"
        task.singleUser = true
       
        when: "a db connection is created"
        task.connect()

        then: "ant.DBConnection should be called"
        1 *  ant.DBConnection([
            dbName: "sports2020",
            dbDir: "testdb/",
            id: "foodb",
            singleUser: true
        ], _ as Closure)
    }

    def "create a dbconnection with an alias" () {
        given: "an instanceof DBConnection"
        task.dbName = "sports2000"
        task.dbDir = "testdb/"
        task.id = "aliasdb"
        task.singleUser = true
        task.alias("foo2") {
            noError = true
        }

        when : "an alias is created for a database"
        task.connect()

        then: "DBConnection has an alias configured correctly"
        task.aliases.get(0).name == "foo2"
        task.aliases.get(0).noError == true
        1 *  ant.DBConnection([
            dbName: "sports2000",
            dbDir: "testdb/",
            id: "aliasdb",
            singleUser: true
        ], _ as Closure)

    }
    
}
