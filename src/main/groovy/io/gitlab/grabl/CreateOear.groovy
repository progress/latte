// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

import java.util.zip.*

class CreateOear extends BaseGrablTask {

    @Input
    String projectDir = null

    @Input @Optional
    String pfDir = null

    @Input
    String projectName

    @Input
    String[] webappsDirs

    @Input @Optional
    String[] plDirs = null

    @Input
    String oearPath

    // maybe add an ablapp conf input?

    private File oearDir

    @TaskAction
    def createOear() {
        oearDir = new File("build/oear", "$projectName")
        
        oearDir.deleteDir() 

        new File("$oearDir", "conf").mkdirs()
        new File("$oearDir", "openedge").mkdir()
        new File("$oearDir", "tlr").mkdir()
        new File("$oearDir", "webapps").mkdir()

        if (pfDir != null) {
            copyPFFiles("$pfDir", "$oearDir/conf")
        }

        copyTlrFiles("$projectDir/PASOEContent/WEB-INF/tlr", "$oearDir/tlr")
        copySrcFiles("$projectDir/AppServer/", "$oearDir/openedge" )
        copyMapGenFiles("$projectDir/PASOEContent/WEB-INF/openedge", "$oearDir/openedge")

        if (plDirs != null) {
            plDirs.each {
                // weird scoping issue where it can't access the private var oearDir
                copyPL("$it", "build/oear/$projectName/openedge")
            }
        }

        webappsDirs.each {
            // weird scoping issue where it can't access the private var oearDir
            copyWar("$it", "build/oear/$projectName/webapps")
        }

        zipOear()
    }

    // Copy over .pf files
    def copyPFFiles(String srcDir, String targetDir) {
        project.copy {
            from "${srcDir}"
            into "${targetDir}"
            include '*.pf'
        }
    }

    // Copy over the tailoring files 
    def copyTlrFiles(String srcDir, String targetDir) {
        project.copy {
            from "${srcDir}"
            into "${targetDir}"
            include '*.merge'
            include '*.xml'
        }
    }

    // Copy ABL src files 
    def copySrcFiles(String srcDir, String targetDir) {
        project.copy {
            from "${srcDir}"
            into "${targetDir}"
            include '**/*.r'
        }
    }

    // Copy over map/gen files from the OpenEdge directory
    def copyMapGenFiles(String srcDir, String targetDir) {
        project.copy {
            from "${srcDir}"
            into "${targetDir}"
            include '*.gen'
            include '*.map'
        }
    }

    // Copy over procedure libraries, which is provided in an array of 
    // directories where .pl's may reside.
    def copyPL(String srcDir, String targetDir) {
        project.copy {
            from "$srcDir"
            into "$targetDir"
            include "*.pl"
        }
    }

    // Copy over wars
    def copyWar(String srcDir, String targetDir) {
        project.copy {
            from "$srcDir"
            into "$targetDir"
            include "*.war"
        }
    }

    def addToZip(String path, String srcFile, ZipOutputStream zipOut) {        
        def int DEFAULT_BUFFER_SIZE = 1024 * 4

        File file = new File(srcFile)
        String filePath = "".equals(path) ? file.getName() : path + "/" + file.getName()
        if (file.isDirectory()) {
            for (String fileName : file.list()) {             
                addToZip(filePath, srcFile + "/" + fileName, zipOut)
            }
        } else {
            zipOut.putNextEntry(new ZipEntry(filePath))
            FileInputStream myin = new FileInputStream(srcFile)

            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE]
            int len
            while ((len = myin.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len)
            } 
            myin.close()
        }
    }

    def zipOear() {
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream("$oearPath/$projectName" + ".oear"))

        File srcFile = new File("$oearDir")
        for(String fileName : srcFile.list()) {
            addToZip("", "$oearDir" + "/" + fileName, zipOut)
        }

        zipOut.flush()
        zipOut.close()
    }
}
