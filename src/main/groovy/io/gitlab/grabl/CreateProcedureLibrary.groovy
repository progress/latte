package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

class CreateProcedureLibrary extends DefaultTask {

    // If there's an easier way to do this WHILE keeping the hard-typing
    // functionality, let aestrada@progress.com know
    @Input @Optional
    String destFile = null

    @Input @Optional
    String sharedFile = null

    @Input @Optional
    String encoding = null

    @Input @Optional
    boolean noCompress = null

    @Input @Optional
    String cpInternal = null

    @Input @Optional
    String cpStream = null

    @Input @Optional
    String cpCase = null

    @Input @Optional
    String cpColl = null
  
    @Input @Optional
    String basedir = null
    
    @Input @Optional
    String includes = null

    @Input @Optional
    String includesFile = null

    @Input @Optional
    String excludes = null

    @Input @Optional
    String excludesFile = null

    @Input @Optional
    boolean defaultExcludes = null
  
    @TaskAction
    def createDB() {
        Map args = [:]

        if (destFile) {
            new File(destFile).mkdirs()
        }

        args.put('destFile', destFile)
        args.put('sharedFile', sharedFile)
        args.put('encoding', encoding)
        args.put('noCompress', noCompress)
        args.put('cpInternal', cpInternal)
        args.put('cpStream', cpStream)
        args.put('cpCase', cpCase)
        args.put('cpColl', cpColl)
        args.put('basedir', basedir)
        args.put('includes', includes)
        args.put('includesFile', includesFile)
        args.put('excludes', excludes)
        args.put('excludesFile', excludesFile)
        args.put('defaultExcludes', defaultExcludes)
        
        // Sort out all the nulls since we wanna leave the defaults to PCT
        def tmp = args.findAll { it.value }

        // This is shorthand for something like:
        //   ant.PCTLibrary(destFile: dbDir, indludes: 'testfoo', noCompress: true)
        // but we use the spread map operator in groovy.
        ant.PCTLibrary(*:tmp)
    }

    protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }
}
