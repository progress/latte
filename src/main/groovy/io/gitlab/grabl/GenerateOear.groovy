// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

class GenerateOear extends BaseGrablTask {

    @Input
    String srcDir = null

    @Input
    String projectName = null

    private File oearDir = new File("build/oear", "$projectName")

    @TaskAction
    def generateOear() {
        oearDir.deleteDir() 
        oearDir.mkdirs()        
    }

    // Copy over the configuration files, e.g. .pf file
    def copyConfigFiles() {
        project.copy(todir:"$oearDir/conf") {
            fileset(dir:"$srcDir/conf") 
        }
    }

    // Copy over the tailoring files 
    def copyTlrFiles() {
        project.copy(todir:"$oearDir/tlr") {
            fileset(dir:"$srcDir/PASOEContent/WEB-INF/tlr") {
                include(name:"properties.merge")
            }
        }
    }

    // Copy ABL src files 
    def copySrcFiles() {
        project.copy(todir:"$oearDir/openedge") {
            fileset(dir:"$srcDir/AppServer") {
                include(name:"**/*.r")
            }
        }
    }

}
