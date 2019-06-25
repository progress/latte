package io.gitlab.grabl

import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal


import org.gradle.api.GradleException

import java.lang.ProcessBuilder
import java.nio.file.Paths

/**
    Base task (not for source tasks) for some boilerplate stuff related to setting
    dlcHome and similar.

*/
abstract class BaseGrablTask extends DefaultTask {

    /**
        value for OpenEdge installation location.
        defaults to using dlcHome value of extension
        if not specified
    */
    @InputFile @Optional
    File dlcHome = ext.dlcHome

    /**
        get value of DlcHome
    */
    File getDlcHome() {
        return dlcHome
    }    

    @Internal
    protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }

}
