/**
  Copyright © 2019,2020 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
*/

package oe.espresso.latte

import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCollection.AntType
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional


class CompileAblTask extends BaseLatteSourceTask {
    /**
     * Directory to put the compiled rcode in
     *
     * If not set, defaults to {@code abl.rcodeDir}.
     */
    @OutputDirectory
    File destinationDir

    /**
     * Paths to add to PROPATH before executing the compilation
     *
     * If not set, defaults to {@code abl.propath}.
     */
    @Classpath
    FileCollection propath

    /**
        True to keep internal temporary files on disk.	
    */
    @Optional
    @Input 
    Boolean debugPCT = null

    /**
     * Databases ({@code refid}s) to connect to before compiling
     *
     * Automatically inherits connections set globally in
     * {@code abl.dbConnections}.
     */
    @Input
    Set<String> dbConnections = []

    @Internal
    Map compileArgs = [:]

    CompileAblTask() {
        setDbConnections([] as Set)
        setCompileArgs([:])
    }

    File getDestinationDir() {
        if (destinationDir != null) return destinationDir
        return ext.rcodeDir
    }

    FileCollection getPropath() {
        if (propath != null) return propath
        return ext.propath
    }

    void setDbConnections(Set<String> connections) {
        this.dbConnections = new SettingsSet<>(ext.dbConnections)
        this.dbConnections.addAll(connections)
    }

    void setCompileArgs(Map compileArgs) {
        this.compileArgs = new SettingsMap<>(ext.pctTaskArgs)
        this.compileArgs.putAll(compileArgs)
    }

    @TaskAction
    def compile() {
        this.destinationDir.mkdirs()

        def args = [:]

        if (dlcHome)
            args.put('dlcHome', dlcHome.path)

        args.put("destDir", destinationDir.path)
        args.put("graphicalMode", graphicalMode)
        args.put("compileUnderscore", compileUnderscore)
        args.put('debugPCT', debugPCT)

        args.putAll(compileArgs)

        // Sort out all the nulls since we wanna leave the defaults to PCT
        def tmp = args.findAll { it.value != null }

        project.ant.PCTCompile(*:tmp) {
            if (this.propath && !this.propath.isEmpty()) {
                this.propath.addToAntBuilder(delegate, 'propath')
            }

            if (dbConnections.size() > 5) {
                option(name: '-h', value: dbConnections.size().toString())
            }
            dbConnections.each { DBConnection(refid: it) }

            this.source.addToAntBuilder(delegate, null, AntType.FileSet)
        }
    }

    @Internal
    protected LatteExtension getExt() {
        return project.extensions.getByType(LatteExtension)
    }
}
