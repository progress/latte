package io.gitlab.grabl

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

/**
 *
 */
class GrablExtensionTest extends Specification {
    Project project = ProjectBuilder.builder().build()
    GrablExtension extension = new GrablExtension(project)

    def "it provides sane defaults"() {
        expect: "default property values to use sane conventions"
        extension.rcodeDir == new File(
                project.buildDir, GrablExtension.DEFAULT_RCODE_DIR_NAME)
        extension.propath?.files == project.files('src/main/abl').files
        extension.dbConnections == []
        extension.pctTaskArgs == [:]

        when: "project buildDir is changed"
        project.buildDir = project.file('newBuildDir')

        then: "rcodeDir changes with it"
        extension.rcodeDir == new File(
                project.buildDir, GrablExtension.DEFAULT_RCODE_DIR_NAME)

        when: "rcodeDir is reset (set to a static value) and buildDir is changed"
        extension.rcodeDir = new File(project.buildDir, 'newRcode')

        then: "rcodeDir does not change"
        extension.rcodeDir == new File(project.buildDir, 'newRcode')
    }
}
