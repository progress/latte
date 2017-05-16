package io.gitlab.hendosdo.grabl

import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction


class CompileAblTask extends SourceTask {
    @OutputDirectory
    File destinationDir

    @Classpath
    FileCollection propath

    @Input
    List<String> dbConnections = []

    @TaskAction
    def compile() {
        println 'Hello from GrablPlugin:CompileAblTask.compile'
        this.destinationDir.mkdirs()
        project.ant.PCTCompile(destDir: destinationDir.path) {
            echo(message: 'Hello from GrablPlugin:CompileAblTask.compile:PCTCompile')
            if (this.propath && !this.propath.isEmpty()) {
                this.propath.addToAntBuilder(delegate, 'propath')
            }
            dbConnections.each { DBConnection(refid: it) }
            this.source.addToAntBuilder(delegate, null)
        }
    }
}
