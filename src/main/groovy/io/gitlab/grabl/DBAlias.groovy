package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class DBAlias {

    @Input
    String name = null 

    @Input @Optional
    Boolean noError = null 
}
