package io.gitlab.hendosdo.grabl

import org.junit.Test
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import static org.junit.Assert.*


class GrablPluginTest {
    @Test
    public void grablPluginAddsTasksToProject() {
        Project project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'io.gitlab.hendosdo.grabl'

        assertTrue(project.tasks.compileAbl instanceof CompileAblTask)
        assertTrue(project.tasks.checkGrabl instanceof DefaultTask)
    }
}
