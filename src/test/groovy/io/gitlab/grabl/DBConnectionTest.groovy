package io.gitlab.grabl

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class DBConnectionTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    DBConnection task

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createTask()
    }

    DBConnection createTask(String name = 'DBConnection1') {
        project.task(name, type: DBConnection)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def connectTask = project.task('DBConnection', type: DBConnection)

        then: "task is an instance of DBConnection1 class"
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

        then: "DBConnection1 should be called"
        1 *  ant.DBConnection([
            dbName: "sports2020",
            dbDir: "testdb/",
            id: "foodb",
            singleUser: true
        ])
    }
}