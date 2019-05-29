package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Internal

class CreateDatabase extends DefaultTask {

    // If there's an easier way to do this WHILE keeping the hard-typing
    // functionality, let aestrada@progress.com know
    // @Input
    // String dbName

    // @Input 
    // String destDir = null

    // @Input
    // String sourceDb = null

    // @Input 
    // String schemaFile = null

    // @Input
    // String structFile = null

    // @Input
    // int blockSize = null

    // @Input
    // boolean noInit = null

    // @Input
    // String codepage = null

    // @Input
    // String wordRules = null

    // @Input
    // boolean multiTenant = null

    // @Input
    // boolean failOnError = null

    // @Input
    // String collation = null

    // @Input
    // String tempDir = null

    // @Input
    // String cpInternal = null

    // @Input
    // String cpStream = null

    // @Input
    // String cpCase = null

    // @Input
    // String cpColl = null

    // @Input
    // boolean newInstance = null 

    // @Input
    // boolean largeFiles = null

    // @Input
    // boolean relative = null 
        
    // @Input
    // boolean auditing = null 

    // @Input
    // String auditArea = null
    
    // @Input
    // String auditIndexArea = null

    @Input
    Map args

    @Internal
    String help 

    @TaskAction
    def createDB() {
        // Map args = [:]

        // if (destDir) {
        //     new File(destDir).mkdirs()
        // }

        // args.put('dbname', dbName)
        // args.put('destDir', destDir)          
        // args.put('sourceDb', sourceDb)
        // args.put('schemaFile', schemaFile)
        // args.put('structFile', structFile)
        // args.put('blockSize', blockSize)
        // args.put('noInit', noInit)
        // args.put('codepage', codepage)
        // args.put('wordRules', wordRules)
        // args.put('multiTenant', multiTenant)
        // args.put('failOnError', failOnError)
        // args.put('collation', collation)
        // args.put('tempDir', tempDir)
        // args.put('cpInternal', cpInternal)
        // args.put('cpStream', cpStream)
        // args.put('cpCase', cpCase)
        // args.put('cpColl', cpColl)
        // args.put('newInstance', newInstance)
        // args.put('largeFiles', largeFiles)
        // args.put('relative', relative)
        // args.put('auditing', auditing)
        // args.put('auditArea', auditArea)
        // args.put('auditIndexArea', auditIndexArea)

        // Sort out all the nulls since we wanna leave the defaults to PCT
        // def tmp = args.findAll { it.value != null }

        // This is shorthand for something like:
        //   ant.PCTCreateBase(destDir: dbDir, dbName: 'testfoo', largeFiles: true)
        // but we use the spread map operator in groovy.
        // ant.PCTCreateBase(*:tmp)

        ant.PCTCreateBase(*:args)
    }

    protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }
}
