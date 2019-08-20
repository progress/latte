// Copyright © 2019 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved. 
package oe.espresso.latte

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

class CreateDatabase extends BaseLatteTask {

    // If there's an easier way to do this WHILE keeping the hard-typing
    // functionality, let aestrada@progress.com know
    @Input
    String dbName

    @Input @Optional
    String destDir = null

    @Input @Optional
    String sourceDb = null

    @Input @Optional
    String schemaFile = null

    @Input @Optional
    String structFile = null

    @Input @Optional
    int blockSize = 0

    @Input @Optional
    Boolean noInit = null

    @Input @Optional
    String codepage = null

    @Input @Optional
    String wordRules = null

    @Input @Optional
    Boolean multiTenant = null

    @Input @Optional
    Boolean failOnError = null

    @Input @Optional
    String collation = null

    @Input @Optional
    String tempDir = null

    @Input @Optional
    String cpInternal = null

    @Input @Optional
    String cpStream = null

    @Input @Optional
    String cpCase = null

    @Input @Optional
    String cpColl = null

    @Input @Optional
    Boolean newInstance = null 

    @Input @Optional
    Boolean largeFiles = null

    @Input @Optional
    Boolean relative = null 
        
    @Input @Optional
    Boolean auditing = null 

    @Input @Optional
    String auditArea = null
    
    @Input @Optional
    String auditIndexArea = null


    @TaskAction
    def createDB() {
        Map args = [:]

        if (destDir) {
            new File(destDir).mkdirs()
        }


        if (dlcHome)
            args.put("dlcHome", "${dlcHome}")

        args.put('dbname', dbName)
        args.put('destDir', destDir)          
        args.put('sourceDb', sourceDb)
        args.put('schemaFile', schemaFile)
        args.put('structFile', structFile)
        args.put('blockSize', blockSize)
        args.put('noInit', noInit)
        args.put('codepage', codepage)
        args.put('wordRules', wordRules)
        args.put('multiTenant', multiTenant)
        args.put('failOnError', failOnError)
        args.put('collation', collation)
        args.put('tempDir', tempDir)
        args.put('cpInternal', cpInternal)
        args.put('cpStream', cpStream)
        args.put('cpCase', cpCase)
        args.put('cpColl', cpColl)
        args.put('newInstance', newInstance)
        args.put('largeFiles', largeFiles)
        args.put('relative', relative)
        args.put('auditing', auditing)
        args.put('auditArea', auditArea)
        args.put('auditIndexArea', auditIndexArea)

        // Sort out all the nulls since we wanna leave the defaults to PCT
        def tmp = args.findAll { it.value != null }

        // This is shorthand for something like:
        //   ant.PCTCreateBase(destDir: dbDir, dbName: 'testfoo', largeFiles: true)
        // but we use the spread map operator in groovy.
        ant.PCTCreateBase(*:tmp)
    }

}
