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
            ivy {
                name 'rssw'

                // NOTE: PCT is available for download only from GitHub
                // releases; however Gradle cannot currently use that
                // as a repository directly because of:
                // - Gradle fails when HTTP HEAD is not supported
                //   https://discuss.gradle.org/t/use-github-releases-as-dependency-repository-plugin/15944/7
                // - HTTP status 403 stops ivy resolver
                //   https://github.com/gradle/gradle/issues/1880
                // - S3 Signed URL (which is how GitHub provides
                //   released artifacts) breaks HTTP HEAD op, because
                //   it includes HTTP verb in the signature which means
                //   HEAD will not work on the same URL where GET will
                //   succeed
                //   http://stackoverflow.com/a/27473301
                //
                // NOTE: also tried using https://jitpack.io/ but it
                // wants to re-pack artifacts and ends up producing a
                // jar that doesn't include the bundled OpenEdge code
                //
                // TODO: see about publishing PCT to Maven Central or JCenter
                artifactPattern 'https://s3-eu-west-1.amazonaws.com/com-henderson-group-dist/openedge/pct/[artifact]-[revision].[ext]'
            }
        }
    }

    void addPctDependency(Project target) {
        target.dependencies {
            pct 'eu.rssw.pct:PCT:207'
            pct 'com.google.code.gson:gson:2.8.0'  // required by pct.ABLUnit
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
