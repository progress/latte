package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

class Run extends DefaultTask {

    // If there's an easier way to do this WHILE keeping the hard-typing
    // functionality, let aestrada@progress.com know
    @Input
    String procedure

    @Input @Optional
    Boolean graphicalMode = null

    @Input @Optional
    String baseDir = null

    @Input @Optional
    Boolean failOnError = null

    @Input @Optional
    String resultProperty = null

    @Input @Optional
    Boolean noErrorOnQuit = null

    @Input @Optional
    String mainCallback = null

    @Input @Optional
    String xCodeSessionKey = null

    @Input @Optional
    Boolean xCodeSessionKey = null

    @TaskAction
    def run() {
        Map args = [:]

        args.put('procedure', procedure)
        args.put('graphicalMode', graphicalMode)          
        args.put('baseDir', baseDir)
        args.put('failOnError', failOnError)
        args.put('resultProperty', resultProperty)
        args.put('noErrorOnQuit', noErrorOnQuit)
        args.put('mainCallback', mainCallback)
        args.put('xCodeSessionKey', xCodeSessionKey)
        args.put('debugPCT', debugPCT)

        // Sort out all the nulls since we wanna leave the defaults to PCT
        def tmp = args.findAll { it.value != null }

        // So we have all of the PCT parameters but we also have stuff like
        // -cpinternal, -iniFile, etc. Are these nested nodes or are they just stuffed inside
        // the PCTRun node? Do we even need to implement those for now?
        // There are also no examples to how to specify this option here.
        ant.PCTRun(*:tmp) {
            // This thing maps to 
            // <DBConnection ...>
            project.ant.PCTConnection("refid": "${refid}")

            this.source.addToAntBuilder(delegate, null, AntType.FileSet)

            // Todo:
            // <DBConnectionSet ...>
            // <propath>
            // <PCTRunOption>
            // <Parameter>
            // <Output Parameter>
            // <Profiler>
            // <DBAlias> <- we can get away without this one

        }
    }

    protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }
}
