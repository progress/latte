/**
  Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
*/

package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class ProfilerSpec {

    /**
        Enables or disables profiler	
    */
    @Input @Optional
    Boolean enabled = null

    /**
        Description of profiler session	
    */
    @Input @Optional
    String description = null

    /**
        Generates a profiler output in this directory, with a unique name	
    */
    @Input @Optional
    String outputDir = null

    /**
        Profiler output file name	
    */
    @Input @Optional
    String outputFile = null

    /**
        Enables code coverage	
    */
    @Input @Optional
    Boolean coverage = null

    /**
        Enables statistics	
    */
    @Input @Optional
    Boolean statistics = null

    /**
        Generates debug listing files in this directory	
    */
    @Input @Optional
    String listings = null

}
