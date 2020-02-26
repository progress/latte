// Copyright © 2019,2020 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
package oe.espresso.latte

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

class CreateProcedureLibrary extends BaseLatteTask {

    // If there's an easier way to do this WHILE keeping the hard-typing
    // functionality, let aestrada@progress.com know
    @OutputFile
    @Optional
    String destFile = null

    @Input @Optional
    String sharedFile = null

    @Input @Optional
    String encoding = null

    @Input @Optional
    Boolean noCompress = null

    @Input @Optional
    String cpInternal = null

    @Input @Optional
    String cpStream = null

    @Input @Optional
    String cpCase = null

    @Input @Optional
    String cpColl = null
  
    @Input @Optional
    String baseDir = null
    
    @Input @Optional
    String includes = null

    @Input @Optional
    String includesFile = null

    @Input @Optional
    String excludes = null

    @Input @Optional
    String excludesFile = null

    @Input @Optional
    Boolean defaultExcludes = null

    @TaskAction
    def createPL() {
        Map args = [:]
        // TODO - not sure we need to create the basedir as it is where the code is coming from
        if (baseDir && !(new File(baseDir).exists())) {
            new File(baseDir).mkdirs()
        }
        // in the destination folder does not exist - create it
        File destF = new File(destFile)
        File parent = destF.getParentFile();
        if (parent && !parent.exists()) {
            parent.mkdirs()
        }

        if (dlcHome) {
            args.put("dlcHome", dlcHome.path)
        }

        args.put('destFile', destFile)
        args.put('sharedFile', sharedFile)
        args.put('encoding', encoding)
        args.put('noCompress', noCompress)
        args.put('cpInternal', cpInternal)
        args.put('cpStream', cpStream)
        args.put('cpCase', cpCase)
        args.put('cpColl', cpColl)
        args.put('basedir', baseDir)
        args.put('includes', includes)
        args.put('includesFile', includesFile)
        args.put('excludes', excludes)
        args.put('excludesFile', excludesFile)
        args.put('defaultExcludes', defaultExcludes)

        // Sort out all the nulls since we wanna leave the defaults to PCT
        def tmp = args.findAll { it.value != null }

        // This is shorthand for something like:
        //   ant.PCTLibrary(destFile: mylib.pl, includes: 'testfoo.p', noCompress: true)
        // but we use the spread map operator in groovy.
        ant.PCTLibrary(*:tmp)
    }

}
