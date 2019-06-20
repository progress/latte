package io.gitlab.grabl

import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCollection.AntType

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Classpath

import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

import org.gradle.api.tasks.Internal
import org.gradle.api.DefaultTask

import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Environment.Variable;


/**
    Invoke PCT to run a procedure.  The argument names (and the descriptions for them) come from PCT wiki:
        https://github.com/Riverside-Software/pct/wiki/PCTRun
*/
class RunAbl extends DefaultTask {

    /**
        Procedure to execute
    */
    @Input
    String procedure

    /**
        True if you want to execute procedure using prowin32 (or prowin on 64 bits platforms), _progres otherwise.	
    */
    @Input @Optional
    Boolean graphicalMode = null

    /**
        The directory in which the OpenEdge runtime should be executed. File attributes (such as paramFile or iniFile) are still resolved against the project base directory.	
    */
    @Input @Optional
    String baseDir = null

    /**
        True to throw a build exception if the OpenEdge procedure exits with a return value other than 0.	
    */
    @Input @Optional
    Boolean failOnError = null

    /**
        The name of a property in which the return code of the Progress procedure should be stored. Only of interest if failOnError is set to false.	
    */
    @Input @Optional
    String resultProperty = null

    /**
        Set to true to prevent PCT from returning an error (code 66) when a QUIT statement is executed	
    */
    @Input @Optional
    Boolean noErrorOnQuit = null

    /**
        Callback class (implementation of rssw.pct.IMainCallback)	
    */
    @Input @Optional
    String mainCallback = null

    /**
        SECURITY-POLICY:XCODE-SESSION-KEY attribute	
    */
    @Input @Optional
    String xCodeSessionKey = null

    /**
        True to keep internal temporary files on disk.	
    */
    @Input @Optional
    Boolean debugPCT = null

    /**
        INI file (adds -basekey INI -ininame ...). Attribute is skipped if file can't be found	
    */
    @Input @Optional
    String iniFile = null

    /**
        Internal code page (-cpinternal parameter)	
    */
    @Input @Optional
    String cpInternal = null

    /**
        Stream code page (-cpstream parameter)	
    */
    @Input @Optional
    String cpStream = null

    /**
        Collation table (-cpcoll parameter)	
    */
    @Input @Optional
    String cpColl = null

    /**
        Case table (-cpcase parameter)	
    */
    @Input @Optional
    String cpCase = null

    /**
        Thousands separator. Can be either a numeric value or a character, e.g. numsep="44" or numsep=","	
    */
    @Input @Optional
    String numSep = null

    /**
        Fractional separator. Can be either a numeric value or a character, e.g. numdec="46" or numdec="."	
    */
    @Input @Optional
    String numDec = null

    /**
        Century Year Offset (-yy parameter)	
    */
    @Input @Optional
    Integer centuryYearOffset = null 

    /**
        Parameter (-param parameter)	
    */
    @Input @Optional
    String parameter = null

    /**
        The number of compiled procedure directory entries (-D parameter)	
    */
    @Input @Optional
    Integer dirSize = null

    /**
        The number of characters allowed in a single statement (-inp parameter)	
    */
    @Input @Optional
    Integer inputChars = null

    /**
        The amount of memory allocated for r-code segments (-mmax parameter)	
    */
    @Input @Optional
    Integer maximumMemory = null

    /**
        Stack size in 1KB units (-s parameter)	
    */
    @Input @Optional
    Integer stackSize = null

    /**
        The number of tokens allowed in a 4GL statement (-tok parameter)	
    */
    @Input @Optional
    Integer token = null

    /**
        Buffer Size for Temporary Tables (-Bt attribute)	
    */
    @Input @Optional
    Integer ttBufferSize = null

    /**
        Message buffer size (-Mm attribute)	
    */
    @Input @Optional
    Integer msgBufferSize = null

    /**
        COMPILE statement allows underscores (-zn parameter)	
    */
    @Input @Optional
    Boolean compileUnderscore = null

    /**
        Parameter file (-pf parameter). -pf is always the first argument on the command line.	
    */
    @Input @Optional
    String paramFile = null

    /**
        Port on which debugger should connect (disabled by default)	
    */
    @Input @Optional
    Integer debugReady = null

    /**
        Temporary directory for Progress runtime (-T parameter)	
    */
    @Input @Optional
    String tempDir = null

    /**
        .Net assemblies directory (-assemblies parameter). Attribute is skipped if file can't be found	
    */
    @Input @Optional
    String assemblies = null

    /**
        Quick request (-q parameter)	
    */
    @Input @Optional
    Boolean quickRequest = null

    /**
     * Paths to add to PROPATH before executing the compilation
     *
     * If not set, defaults to {@code abl.propath}.
     */
    @Classpath
    FileCollection propath

    /**
     * Databases ({@code refid}s) to connect to before compiling
     *
     * Automatically inherits connections set globally in
     * {@code abl.dbConnections}.
     */
    @Input
    Set<String> dbConnections = []

    @Internal
    Map environment = [:]

    @Internal
    ProfilerSpec profilerSpec = null

    RunAbl() {
        setDbConnections([] as Set)
    }    

    FileCollection getPropath() {
        if (propath != null) return propath
        return ext.propath
    }  

    Map getEnvironment() {
        if (!this.environment) {
            this.environment = new SettingsMap<>(ext.environment)
        }
        return environment;
    }

    Map setEnvironment(Map environment) {
        this.environment = new SettingsMap<>(ext.environment)
        this.environment.addAll(environment)
    }

    /**
     set propath via function call
    */
    FileCollection setPropath(FileCollection propath) {
        this.propath = propath
        return propath
    }

    void setDbConnections(Set<String> connections) {
        this.dbConnections = new SettingsSet<>(ext.dbConnections)
        this.dbConnections.addAll(connections)
    }

    /**
        add an environment variable to the ABL Run
    */
    public void env(String name, String value) {
        environment.put(name, value)
    }

    /**
        provide closure to configure the profiler
    */
    @Input
    public void profiler(Closure profilerConfigure) {
        if (!profilerSpec) {
            profilerSpec = prepareProfilerSpec()
        }

        profilerSpec.with(profilerConfigure)

    }
  
    @Input
    public ProfilerSpec getProfiler() {
        if (!profilerSpec) {
            profilerSpec = prepareProfilerSpec()
        }

        return profilerSpec
    }

    def prepareProfilerSpec() {
        def profilerSpec = new ProfilerSpec()

        profilerSpec.description = "Profile for ${project.group}.${project.name}.${project.version}" 
        profilerSpec.outputDir = new File(project.buildDir, "profiler").path
        profilerSpec.enabled = true

        return profilerSpec
    }

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
        args.put('iniFile', iniFile)
        args.put('cpInternal', cpInternal)
        args.put('cpColl', cpColl)
        args.put('cpCase', cpCase)
        args.put('NumSep', numSep)
        args.put('NumDec', numDec)
        args.put('centuryYearOffset', centuryYearOffset)
        args.put('parameter', parameter)
        args.put('dirSize', dirSize)
        args.put('inputChars', inputChars)
        args.put('maximumMemory', maximumMemory)
        args.put('stackSize', stackSize)
        args.put('token', token)
        args.put('ttBufferSize', ttBufferSize)
        args.put('msgBufferSize', msgBufferSize)
        args.put('compileUnderscore', compileUnderscore)
        args.put('paramFile', paramFile)
        args.put('debugReady', debugReady)
        args.put('tempDir', tempDir)
        args.put('quickRequest', quickRequest)

        // Sort out all the nulls since we wanna leave the defaults to PCT
        def tmp = args.findAll { it.value != null }

        // So we have all of the PCT parameters but we also have stuff like
        // -cpinternal, -iniFile, etc. Are these nested nodes or are they just stuffed inside
        // the PCTRun node? Do we even need to implement those for now?
        // There are also no examples to how to specify this option here.
        ant.PCTRun(*:tmp) {

            if (this.propath && !this.propath.isEmpty()) {
                this.propath.addToAntBuilder(delegate, 'propath', AntType.FileSet)
            }

            if (dbConnections.size() > 5) {
                option(name: '-h', value: dbConnections.size().toString())
            }
            dbConnections.each { DBConnection(refid: it) }

            if (environment && !environment.isEmpty()) {
                environment.each {
                    ant.env([key : it.key, value : it.value])
                }
            }

            if (profiler) {

                def profArgs = [
                    enabled : profilerSpec.enabled, 
                    description : profilerSpec.description, 
                    outputDir : profilerSpec.outputDir,
                    outputFile : profilerSpec.outputFile,
                    coverage : profilerSpec.coverage,
                    statistics : profilerSpec.statistics,
                    listings : profilerSpec.listings
                    ]

                def profTmp = profArgs.findAll { it.value != null }
                ant.Profiler(profTmp)
            }

            // Todo:
            // <DBConnectionSet ...>
            // <PCTRunOption>
            // <Parameter>
            // <Output Parameter>
            // <Profiler>
            // <DBAlias> <- we can get away without this one

        }
    }

    @Internal
    protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }

}
