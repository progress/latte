// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.tasks.Input
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
    Custom gradle task to create a backup of a database.  This requires a source databsae
    and a target output file.  Optional switches are provided for incremental, and online backups, and
    overwriting output file if desired 

*/
class BackupDatabase extends BaseGrablTask {

    /**
    string path to backup file. required. may be absolute or relative
    */
    @OutputFile
    String target = null

    /**
    default is to not overwrite file
    */
    @Input @Optional
    Boolean overwrite = Boolean.FALSE

    /*
    string path to where database to backup is located. required. may be relative or absolute.  may include .db file extension or leave it off
    */
    @InputFile
    String source = null

    /**
    string path to backup file.  may be relative or absolute.
    */
    @Input @Optional
    Boolean online = null

    /**
    specify incremental switch on backup.  default is to perform a full backup
    */
    @Input @Optional
    Boolean incremental = Boolean.FALSE

    @TaskAction
    def backupDB() {

        def targetFile = new File(target)

        def backupDir = targetFile.getParentFile()

        if (targetFile.exists() && !overwrite) {
            throw new GradleException("backup $targetFile exists, but overwrite was not specified.")
        }

        if (backupDir) {
            if (!backupDir.exists()) {
                backupDir.mkdirs()
            }
        }

        def sourceFile = new File(source)

        if (!sourceFile.exists() ) {
            def altSourceFile = new File(source.toString() + ".db")
            if (altSourceFile.exists()) {
                sourceFile = altSourceFile
            } else {
                throw new GradleException( "$source database does not exist")
            }
        }

        if (!dlcHome) {
            throw new GradleException("dlcHome or System.env.DLC must be set")
        }

        def dlc = dlcHome.path
        
        List<String> cmd = []

        def probkup = Paths.get(dlc, "bin/probkup")

        if (isWindows()) {
            probkup = Paths.get(dlc, "bin/probkup.bat")
        }

        cmd.add(probkup.toString())

        def dbHolderStatus = checkDbHolder(sourceFile)

        if (dbHolderStatus == 14) {
            throw new GradleException("Database $sourceFile is in single user mode and cannot be backed up. Database must either be not in use, or in multi-user mode")
        }

        if (online == null) {
            if (dbHolderStatus == 16) {
                cmd.add("online")
            }
        } else if (online == true) {

            if (dbHolderStatus == 0) {
                throw new GradleException("Database $sourceFile is not in use but online property for backup is set to true. Leave out the online property to allow the task to guess.")
            }
        } else if (online == false) {
            if (dbHolderStatus == 16) {
                throw new GradleException("Database $sourceFile is in multi-user mode but online property for backup is set to false. Leave out the online property to allow the task to guess.")
            }
        }

        cmd.add(sourceFile.toString())

        if (incremental) {
            cmd.add("incremental")
        }

        cmd.add(targetFile.toString())

        project.logger.debug("executing probkup: {}", cmd.toString())

        ProcessBuilder pb = new ProcessBuilder(cmd)
        
        def out = new StringBuilder()
        def err = new StringBuilder()


        def proc = pb.start()

        proc.waitForProcessOutput(out, err)

        def result = proc.waitFor()

        project.logger.debug("probkup exited with {}", result)

        project.logger.info(out.toString())

        if (result != 0) {
            throw new GradleException("probkup failed with exit code $result")
        }

    }

    public static boolean isWindows() {
        return System.properties['os.name'].toLowerCase().contains('windows')
    }

    // probkup will fail with a 0 exit code if database is not online
    // but 'online' was specified.  we don't want it to fail, so we use
    // proutil dbholder command check check if database is online and the
    // online flag is set appropriately
    // if not set, then we'll "guess" and add the online if needed
    protected int checkDbHolder(File sourceFile) {

        def dlc = project.file(dlcHome).path

        List<String> cmd = []

        def probkup = Paths.get(dlc, "bin/proutil")

        if (isWindows()) {
            probkup = Paths.get(dlc, "bin/proutil.bat")
        }

        cmd.add(probkup.toString())

        cmd.add(sourceFile.toString())
        cmd.add("-C")
        cmd.add("holder")

        ProcessBuilder pb = new ProcessBuilder(cmd)
        
        def proc = pb.start()

        proc.consumeProcessOutput()

        return proc.waitFor()

    }

}
