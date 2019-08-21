package oe.espresso.latte

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import static org.hamcrest.CoreMatchers.instanceOf
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertTrue

class LattePluginTest {
    Project project

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        project.pluginManager.apply 'oe.espresso.latte.latte'
    }

    @Test
    void canBeApplied() {
        assertTrue(project.plugins.hasPlugin(LattePlugin))
    }

    @Test
    void appliesBasePlugin() {
        assertTrue(project.plugins.hasPlugin(LatteBasePlugin))
    }

    @Test
    void addsTasksToProject() {
        assertThat(project.tasks.compileAbl, instanceOf(CompileAblTask))
        assertThat(project.tasks.checkLatte, instanceOf(DefaultTask))
    }
}
