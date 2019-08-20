// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class GenerateOearTest extends Specification {
    Project project
    AntBuilder ant
    LatteExtension extension
    CreateOear task

    void setup() {
        project = ProjectBuilder.builder().build()
        project.extensions.create(LatteExtension.NAME, LatteExtension, project)
        extension = project.extensions.getByType(LatteExtension)
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
        task.pfDir = "boofar"
        task.webappsDirs = ["foobar"]
        task.plDirs = ["foo", "bar"]
        task.oearPath = "barfoo"
        
        then: "task properties reflect that change"
        task.projectDir == "foo"
        task.projectName == "bar"
        task.pfDir == "boofar"
        task.webappsDirs == ["foobar"]
        task.plDirs == ["foo", "bar"]
        task.oearPath == "barfoo"
    }

    def "an oear is created"() {
        given: "a PDSOE-like project structure"
        def projDir = new File("/tmp/fakeProj")
        def tlrDir = new File("$projDir", "PASOEContent/WEB-INF/tlr")
        tlrDir.mkdirs()
        def rcodeDir = new File("$projDir", "AppServer")
        rcodeDir.mkdir()
        def mapGenDir = new File("$projDir", "PASOEContent/WEB-INF/openedge")
        mapGenDir.mkdir()
        def plDir = new File("/tmp", "plDir")
        plDir.mkdir()
        def pfDir = new File("/tmp", "pfDir")
        pfDir.mkdir()
        def warDir = new File("/tmp", "warDir")
        warDir.mkdir()

        // Create .pf file
        new File("${pfDir}/foo.pf").createNewFile()

        // Create tailoring files
        new File("$tlrDir/properties.merge").createNewFile()
        new File("$tlrDir/build.xml").createNewFile()

        // Create some rcode 
        new File("$rcodeDir/Customers.r").createNewFile()
        new File("$rcodeDir/Items.r").createNewFile()
        
        // Create a map and a gen file
        new File("$mapGenDir/Customers.map").createNewFile()
        new File("$mapGenDir/Items.gen").createNewFile()

        // Create war files
        new File("$warDir/fake.war").createNewFile()

        // Create war files
        new File("$plDir/dbTriggers.pl").createNewFile()
        new File("$plDir/bar.pl").createNewFile()

        when: "createOear() is called"
        task.projectDir = "$projDir"
        task.projectName = "fakeProj"
        task.pfDir = "$pfDir"
        task.webappsDirs = ["$warDir"]
        task.plDirs = ["$plDir"]
        task.oearPath = "build"
        task.createOear()

        then: "an oear is created"
        def builtOear = new File("build/oear/${task.projectName}")

        // .oear exists
        new File("build/${task.projectName}.oear").exists()

        // tailor files exist
        new File("${builtOear}/tlr/build.xml").exists()
        new File("${builtOear}/tlr/properties.merge").exists()

        // configuration files exist
        new File("${builtOear}/conf/foo.pf").exists()

        // rcode file exists
        new File("${builtOear}/openedge/Customers.r").exists()
        new File("${builtOear}/openedge/Items.r").exists()

        // map and gen files exist
        new File("${builtOear}/openedge/Customers.map").exists()
        new File("${builtOear}/openedge/Items.gen").exists()

        // .pl's exist
        new File("${builtOear}/openedge/dbTriggers.pl").exists()
        new File("${builtOear}/openedge/bar.pl").exists()

        // .war files exist
        new File("${builtOear}/webapps/fake.war").exists()

        cleanup:
        projDir.delete()
        plDir.delete()
        pfDir.deleteDir()
        warDir.deleteDir()
        builtOear.deleteDir()
    }
}
