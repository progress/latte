// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

import java.util.zip.*

class CreateOear extends BaseLatteTask {

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

    // private File oearDir

    @TaskAction
    def createOear() {
        def oearDir = new File("build/oear", "$projectName")
        oearDir.deleteDir() 

        new File("${oearDir.absolutePath}", "conf").mkdirs()
        new File("${oearDir.absolutePath}", "openedge").mkdir()
        new File("${oearDir.absolutePath}", "tlr").mkdir()
        new File("${oearDir.absolutePath}", "webapps").mkdir()

        if (pfDir != null) {
            copyPFFiles("$pfDir", "${oearDir.absolutePath}/conf")
        }

        copyTlrFiles("$projectDir/PASOEContent/WEB-INF/tlr", "${oearDir.absolutePath}/tlr")
        copySrcFiles("$projectDir/AppServer/", "${oearDir.absolutePath}/openedge" )
        copyMapGenFiles("$projectDir/PASOEContent/WEB-INF/openedge", "${oearDir.absolutePath}/openedge")

        if (plDirs != null) {
            plDirs.each {
                copyPL("$it", "${oearDir.absolutePath}/openedge")
            }
        }

        webappsDirs.each {
            copyWar("$it", "${oearDir.absolutePath}/webapps")
        }

        zipOear(oearDir)
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

    // Recursively adding files to the output stream
    def addToZip(String path, String srcFile, ZipOutputStream zipOut) {        
        def int DEFAULT_BUFFER_SIZE = 1024 * 4

        // Try to recursively get to the bottom-most node of the file structure
        // and add it to the output stream.
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

    // This is the entrypoint of where we zip everything in $oearDir.
    // You build up a ZipOutputStream by adding each file into the stream and recursively
    // add the folder and its contents if its a folder.
    def zipOear(File oearDir) {
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream("$oearPath/$projectName" + ".oear"))

        File srcFile = new File("$oearDir")
        for(String fileName : srcFile.list()) {
            addToZip("", "$oearDir" + "/" + fileName, zipOut)
        }

        zipOut.flush()
        zipOut.close()
    }
}
