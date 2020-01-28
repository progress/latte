// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.Plugin
import org.gradle.api.Project

class LatteBasePlugin implements Plugin<Project> {
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
                url 'https://repo1.maven.org'
            }
        }
    }

    void addPctDependency(Project target) {
        target.dependencies {
            pct 'eu.rssw.pct:pct:214'
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
        target.extensions.create(LatteExtension.NAME, LatteExtension, target)
    }
}
