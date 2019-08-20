/**
  Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
*/

package oe.espresso.latte

import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal


/**
    Base task (not for source tasks) for some boilerplate stuff related to setting
    dlcHome and similar.

*/
abstract class BaseLatteSourceTask extends SourceTask {


    /**
        value for OpenEdge installation location.
        defaults to using dlcHome value of extension
        if not specified
    */
    @InputDirectory @Optional
    File dlcHome = ext.dlcHome

    /**
        get value of DlcHome
    */
    File getDlcHome() {
        return dlcHome
    }

    @Internal
    protected LatteExtension getExt() {
        return project.extensions.getByType(LatteExtension)
    }

}
