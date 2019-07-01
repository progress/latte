/**
  Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
*/

package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class DBAlias {

    @Input
    String name = null 

    @Input @Optional
    Boolean noError = null 
}
