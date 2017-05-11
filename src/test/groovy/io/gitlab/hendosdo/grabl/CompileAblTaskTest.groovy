package io.gitlab.hendosdo.grabl

import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.Project
import spock.lang.Specification


class CompileAblTaskTest extends Specification {
    def "it can be added to a project"() {
        given: "a Project instance"
        Project project = ProjectBuilder.builder().build()

        when: "the task is added to the project"
        def task = project.task('compileAbl', type: CompileAblTask)

        then: "task is an instance of CompileAblTask class"
        task instanceof CompileAblTask
    }
}
