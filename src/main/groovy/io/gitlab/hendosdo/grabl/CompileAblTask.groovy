package io.gitlab.hendosdo.grabl

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction


class CompileAblTask extends DefaultTask {
    @TaskAction
    def createCompileTask() {
        println 'Hello from GrablPlugin:CompileAblTask.createCompileTask'
    }
}
