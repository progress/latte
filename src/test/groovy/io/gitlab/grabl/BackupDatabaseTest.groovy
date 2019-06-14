package io.gitlab.grabl

import org.gradle.api.AntBuilder
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.GradleException

import spock.lang.Specification


class BackupDatabaseTest extends Specification {
    Project project
    AntBuilder ant
    GrablExtension extension
    BackupDatabase task
    def dlc = "$System.env.DLC"
    def isWin = BackupDatabase.isWindows()
    def targetFile = new File("build/test/db/empty")
    def targetempty = targetFile.toString()
    def sourceempty = new File(dlc + "/empty").toString()
    def targetDir = new File("build/test/db")
    def backup = targetempty + ".bak"
    def backupfile = new File(backup)

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
        project.extensions.create(GrablExtension.NAME, GrablExtension, project)
        extension = project.extensions.getByType(GrablExtension)
        task = createBackupTask()

        // create database we'll use to test backup
        def procopyStr = dlc + "/bin/procopy"
        if (isWin) {
            procopyStr += ".bat"
        }
        def procopy = new File(procopyStr).toString()
        targetDir.mkdirs()
        def cmd = "$procopy $sourceempty $targetempty"
        if (!(new File(targetempty + ".db").exists())) {
            def proc = cmd.execute()
            proc.consumeProcessOutput()
            proc.waitFor()
        }

    }

    BackupDatabase createBackupTask(String name = 'backupDatabase') {
        return project.task(name, type: BackupDatabase)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def backupTask = project.task('BackupDatabase', type: BackupDatabase)

        then: "task is an instance of BackupDatabase class"
        backupTask instanceof BackupDatabase
    }

    def "task properties can be changed"() {
        given: "a fresh instance"

        when: "task properties are changed"
        task.source = "testfoo.db"

        then: "task properties reflect that change"
        task.source == "testfoo.db"
    }

    def "backs up an existing database to a known file"() {

        given: "an instance of BackupDatabase with no source or target specified"
        task.source = targetempty
        task.target = backup
        task.overwrite = true
        
        when: "a db is created and backed up"
        task.backupDB()

        then: "backup should exist"
        backupfile.exists()
    }

    def "ensure .db extension is handled properly"() {

        given: "an instance of BackupDatabase with no source or target specified"
        task.source = targetempty + ".db"
        task.target = backup
        task.overwrite = true
        
        when: "a db is created and backed up"
        task.backupDB()

        then: "backup should exist"
        backupfile.exists()
    }


    def "backs up an existing database to a known file where online is false"() {

        given: "an instance of BackupDatabase with no source or target specified"
        task.source = targetempty
        task.target = backup
        task.online = false
        task.overwrite = true
        
        when: "a db is created and backed up"
        task.backupDB()

        then: "backup should exist"
        backupfile.exists()
    }

    def "expect failure when db is not online, but online specified"() {

        given: "an instance of BackupDatabase with no source or target specified"
        task.source = targetempty
        task.target = backup
        task.online = true
        task.overwrite = true
        
        when: "a db is created and backed up"
        task.backupDB()

        then: "backup should fail"
        final GradleException ex = thrown()
        ex.message.contains("not in use")
    }
    

    def "expect to fail when backup file already exists"() {

        given: 
        task.source = targetempty
        task.target = backup
        task.overwrite = false
        backupfile.text = "test empty file"
        
        when: "a db is created and backed up"
        task.backupDB()        

        then: "backup should fail"
        final GradleException ex = thrown()
        ex.message.contains("overwrite")
    }

    def "expect to fail when source database doesn't exist"() {

        given: 
        task.source = "missing.db"
        task.target = "missingbackup.bak"
        
        when: "a db is created and backed up"
        task.backupDB()        

        then: "backup should fail"
        final GradleException ex = thrown()
        ex.message.contains("does not exist")
    }    

    def "backup to missing dir works"() {

        given:
        task.source = targetempty
        def newdir = new File("build/test/empty/empty2/empty3")
        if (newdir.exists()) {
            newdir.deleteDir()
        }
        task.target = "build/test/empty/empty2/empty3/empty.bak"
        
        when: "a db is created and backed up"
        task.backupDB()

        then: "backup should exist"
        new File(task.target).exists()
    }
    
    def "test incremental"() {

        given:
        task.source = targetempty
        task.target = backup
        task.overwrite = true
        task.incremental = false
        task.backupDB()
        task.incremental = true
        task.overwrite = true
        task.target = "build/test/incr/emptyincr.bak"


        when: "a db is created and backed up"
        task.backupDB()

        then: "incremental backup should exist"
        new File(task.target).exists()
    }

    def "expect backup to work when database server is running"() {

        setup: 
        task.source = targetempty
        task.target = backup
        task.overwrite = true

        def proservestr = dlc + "/bin/proserve"
        if (isWin) {
            proservestr += ".bat"
        }
        def proserve = new File(proservestr).toString()
        def proservcmd = "$proserve $targetempty"
        def proserveproc = proservcmd.execute()
        proserveproc.consumeProcessOutput()
        proserveproc.waitFor()

        when: "a db is started and backed up"
        task.backupDB()        

        then: "backup should succeed"
        new File(task.target).exists()


        cleanup:
        def proshutstr = dlc + "/bin/proshut"
        if (isWin) {
            proshutstr += ".bat"
        }
        def proshut = new File(proshutstr).toString()
        def shutcmd = "$proshut $targetempty -by"
        def shutproc = shutcmd.execute()
        shutproc.consumeProcessOutput()
        shutproc.waitFor()

    }


    def "expect backup to fail when database server is running, but online is false"() {

        setup: 
        task.source = targetempty
        task.target = backup
        task.online = false
        task.overwrite = true

        def proservestr = dlc + "/bin/proserve"
        if (isWin) {
            proservestr += ".bat"
        }
        def proserve = new File(proservestr).toString()
        def proservcmd = "$proserve $targetempty"
        def proserveproc = proservcmd.execute()
        proserveproc.consumeProcessOutput()
        proserveproc.waitFor()

        when: "a db is started and backed up"
        task.backupDB()        

        then: "backup should fail"
        thrown GradleException


        cleanup:
        def proshutstr = dlc + "/bin/proshut"
        if (isWin) {
            proshutstr += ".bat"
        }
        def proshut = new File(proshutstr).toString()
        def shutcmd = "$proshut $targetempty -by"
        def shutproc = shutcmd.execute()
        shutproc.consumeProcessOutput()
        shutproc.waitFor()

    }



}
