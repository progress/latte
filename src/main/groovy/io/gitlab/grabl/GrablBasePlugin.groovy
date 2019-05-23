package io.gitlab.grabl

import org.gradle.api.Plugin
import org.gradle.api.Project

class GrablBasePlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        addPctConfiguration(target)
        addRsswRepository(target)
        addPctDependency(target)
        addPctTasksAndTypes(target)
        addExtension(target)
    }

    void addPctConfiguration(Project target) {
        target.configurations {
            pct {
                description = 'Progress Compilation Tools'
            }
        }
    }

    void addRsswRepository(Project target) {
        target.repositories {
            maven {
                url 'http://central.maven.org/maven2/'
            }
        }
    }

    void addPctDependency(Project target) {
        target.dependencies {
            pct 'eu.rssw.pct:pct:211'
        }
    }

    void addPctTasksAndTypes(Project target) {
        target.afterEvaluate { project ->
            project.ant.taskdef(resource: 'PCT.properties',
                    classpath: target.configurations.pct.asPath,
                    loaderRef: 'pct')
            project.ant.typedef(resource: 'types.properties',
                    classpath: target.configurations.pct.asPath,
                    loaderRef: 'pct')
        }
    }

    void addExtension(Project target) {
        target.extensions.create(GrablExtension.NAME, GrablExtension, target)
    }
}
