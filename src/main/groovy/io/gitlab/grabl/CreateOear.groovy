// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

class CreateOear extends BaseGrablTask {

    @Input
    String projectDir = null

    @Input @Optional
    String pfDir = null

    @Input
    String projectName = null

    @Input @Optional
    String[] procLibs = null

    private File oearDir = new File("build/oear", "$projectName")

    @TaskAction
    def createOear() {
        oearDir.deleteDir() 
        oearDir.mkdirs()        
    }

    // Copy over .pf files
    def copyPFFiles() {
        project.copy(todir:"$oearDir/conf") {
            fileset(dir:"$pfDir") {
                include(name:"*.pf")
            }
        }
    }

    // Copy over the tailoring files 
    def copyTlrFiles() {
        project.copy(todir:"$oearDir/tlr") {
            fileset(dir:"$projectDir/PASOEContent/WEB-INF/tlr") {
                include(name:"properties.merge")
            }
        }
    }

    // Copy ABL src files 
    def copySrcFiles() {
        project.copy(todir:"$oearDir/openedge") {
            fileset(dir:"$projectDir/AppServer") {
                include(name:"**/*.r")
            }
        }
    }

    // Copy over map/gen files from the OpenEdge directory
    def copyMapGenFiles() {
        project.copy(todir:"$oearDir/openedge") {
            fileset(dir:"$projectDir/PASOEContent/WEB-INF/openedge") {
                include(name:"*.gen")
                include(name:"*.map")
            }
        }
    }

    // Copy over procedure libraries, which is provided in an array of 
    // directories where .pl's may reside.
    def copyPLs() {
        if (procLibs != null) {
            procLibs.each {
                project.copy(todir:"$oearDir/openedge") {
                    fileset(dir:"${it}") {
                        include(name:"*.pl")
                    }
                }
            }
        }
    }

}
