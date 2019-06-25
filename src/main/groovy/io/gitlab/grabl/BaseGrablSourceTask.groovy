package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal


/**
    Base task (not for source tasks) for some boilerplate stuff related to setting
    dlcHome and similar.

*/
abstract class BaseGrablSourceTask extends SourceTask {


    /**
        value for OpenEdge installation location.
        defaults to using dlcHome value of extension
        if not specified
    */
    @Input @Optional
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
