package oe.espresso.latte

import org.gradle.api.Project
import org.gradle.api.Plugin


class GrablPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.pluginManager.apply(GrablBasePlugin)

        target.task('compileAbl', type: CompileAblTask)
        target.task('checkGrabl') {
            doLast {
                println 'Hello from GrablPlugin'
                ant.PCTVersion()
            }
        }
    }
}
