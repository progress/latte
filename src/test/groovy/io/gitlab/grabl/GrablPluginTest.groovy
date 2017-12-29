package io.gitlab.grabl

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertTrue

class GrablPluginTest {
    Project project

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'io.gitlab.grabl.grabl'
    }

    @Test
    void canBeApplied() {
        assertTrue(project.plugins.hasPlugin(GrablPlugin))
    }

    @Test
    void appliesBasePlugin() {
        assertTrue(project.plugins.hasPlugin(GrablBasePlugin))
    }

    @Test
    public void grablPluginAddsTasksToProject() {
        assertTrue(project.tasks.compileAbl instanceof CompileAblTask)
        assertTrue(project.tasks.checkGrabl instanceof DefaultTask)
    }
}
