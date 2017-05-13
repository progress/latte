package io.gitlab.hendosdo.grabl

import org.junit.Before
import org.junit.Test
import org.spockframework.util.Matchers
import org.gradle.testfixtures.ProjectBuilder
import org.hamcrest.Matcher
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet

import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.is
import static org.hamcrest.CoreMatchers.not
import static org.hamcrest.CoreMatchers.notNullValue
import static org.junit.Assert.*


class GrablPluginTest {
    Project project

    @Before
    void setup() {
        project = ProjectBuilder.builder().build()
        project.repositories { jcenter() }
        project.pluginManager.apply 'io.gitlab.hendosdo.grabl'
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
}
