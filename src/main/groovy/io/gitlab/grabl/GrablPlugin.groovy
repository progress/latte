package oe.espresso.latte

import org.gradle.api.Project
import org.gradle.api.Plugin


class LattePlugin implements Plugin<Project> {
    void apply(Project target) {
        target.pluginManager.apply(LatteBasePlugin)

        target.task('compileAbl', type: CompileAblTask)
        target.task('checkLatte') {
            doLast {
                println 'Hello from LattePlugin'
                ant.PCTVersion()
            }
        }
    }
}
