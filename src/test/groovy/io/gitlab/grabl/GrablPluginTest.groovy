package io.gitlab.grabl

import static org.hamcrest.CoreMatchers.instanceOf
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.CoreMatchers.notNullValue
import static org.junit.Assert.*

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySet
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test


class GrablPluginTest {
    Project project

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        project.repositories { jcenter() }
        project.pluginManager.apply 'io.gitlab.grabl.grabl'
    }

    @Test
    void canBeApplied() {
        assertTrue(project.plugins.hasPlugin(GrablPlugin))
    }

    @Test
    public void grablPluginAddsTasksToProject() {
        assertTrue(project.tasks.compileAbl instanceof CompileAblTask)
        assertTrue(project.tasks.checkGrabl instanceof DefaultTask)
    }

    @Test
    void addsPctConfiguration() {
        assertThat(project.configurations.pct, notNullValue())
    }

    @Test
    void addsRsswRepository() {
        assertThat(project.repositories.rssw, notNullValue())
    }

    @Test
    void addsPctDependency() {
        DependencySet deps = project.configurations.pct.dependencies
        assertThat(deps.size(), not(0))
        assertThat(deps.findAll { it.name == 'PCT' }.size(), is(1))
    }

    @Test
    void addsPctTasksAndTypes() {
        // TODO: not quite sure how to actually test that; can't seem
        //   to check if types or tasks were added to AntBuilder as
        //   they are implemented using dynamic calls; the only
        //   property it seems to affect is 'references' where it puts
        //   the 'loaderRef' with value of the class loader used to
        //   load the tasks/types; alternatively just mock AntBuilder
        //   and test that it called 'taskdef' / 'typedef'
        println "ant: ${project.ant.references}"
        assertThat(project.ant.references.pct, notNullValue())
    }

    @Test
    void addsGrablExtension() {
        assertThat(project.abl, instanceOf(GrablExtension))
    }
}
