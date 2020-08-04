package oe.espresso.latte

import org.gradle.api.AntBuilder

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

/**
 *
 */
class LatteExtensionTest extends Specification {
    Project project
    AntBuilder ant
    LatteExtension extension


    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(LatteExtension.NAME, LatteExtension, project)
        extension = project.extensions.getByType(LatteExtension)
    }

    def "it provides sane defaults"() {
        expect: "default property values to use sane conventions"
        extension.rcodeDir == new File(
                project.buildDir, LatteExtension.DEFAULT_RCODE_DIR_NAME)
        extension.propath?.files == project.files('src/main/abl').files
        extension.dbConnections?.isEmpty()
        extension.pctTaskArgs == [:]
        extension.graphicalMode == false

        when: "project buildDir is changed"
        project.buildDir = project.file('newBuildDir')

        then: "rcodeDir changes with it"
        extension.rcodeDir == new File(
                project.buildDir, LatteExtension.DEFAULT_RCODE_DIR_NAME)

        when: "rcodeDir is reset (set to a static value) and buildDir is changed"
        extension.rcodeDir = new File(project.buildDir, 'newRcode')

        then: "rcodeDir does not change"
        extension.rcodeDir == new File(project.buildDir, 'newRcode')
    }

    def "it provides configuration DSL to the project"() {
        given: "extension is added to the project"

        when: "configuration DSL is used"
        project.configure(project) {
            abl {
                rcodeDir "${buildDir}/newRcode"
                propath 'src'
                dbConnections('foodb', 'bardb')
                pctTaskArgs {
                    preprocess = true
                }
                pctTaskArgs.listing = true
            }
        }

        then: "values are changed"
        extension.rcodeDir == new File(project.buildDir, 'newRcode')
        extension.propath?.files == project.files('src').files
        extension.dbConnections.containsAll(['foodb', 'bardb'])
        extension.pctTaskArgs == [preprocess: true, listing: true]
    }

    def "it sets DlcHome "() {
        given: "extension is added to the project"

        when: "configuration DSL is used"
        project.configure(project) {
            abl {
                dlcHome = new File("testdlchome")
            }
        }

        then: "values are changed"
        extension.dlcHome == new File("testdlchome")
    }    

    def "it has version info"() {
        given: "extension is added to the project"


        when: "dlc is set"
        project.configure(project) {
            abl {
                dlcHome = new File("${System.env.DLC}")
            }
        }

        then: "extension version has values"
        extension.version != null

    }

}
