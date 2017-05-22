package io.gitlab.grabl

import org.gradle.testfixtures.ProjectBuilder

import java.nio.file.attribute.AclFileAttributeView

import org.gradle.api.AntBuilder
import org.gradle.api.Project
import spock.lang.Specification


class CompileAblTaskTest extends Specification {
    Project project
    AntBuilder ant

    void setup() {
        project = ProjectBuilder.builder().build()
        ant = GroovyMock()
        project.ant = ant
    }

    CompileAblTask createTask(String name = 'compileAbl') {
        project.task(name, type: CompileAblTask)
    }

    def "it can be added to a project"() {
        when: "the task is added to the project"
        def task = project.task('compileAbl', type: CompileAblTask)

        then: "task is an instance of CompileAblTask class"
        task instanceof CompileAblTask
    }

    def "it has default values for properties"() {
        given: "an instance of the CompileAblTask"
        def compile = createTask()

        expect: "default values"
        // from AbstractCompile
        compile.destinationDir == null
        compile.source.isEmpty()
        compile.propath == null
        compile.dbConnections.isEmpty()
        compile.compileArgs == [:]
    }

    def "compile action creates resources necessary to compile using PCT"() {
        given: "a project with AntBuilder and an instance of CompileAblTask"
        def task = createTask()
        task.destinationDir = project.file('destDir')

        // define all expected interactions here so we don't have to repeat the
        // param (closure) processing closure
        3 * ant.PCTCompile(
            [destDir: task.destinationDir.path],
            _ as Closure
        ) >> { Map params, Closure configClosure ->
            println "PCTCompile(${params}) &${configClosure.class}"
            // call configClosure the same way AntBuilder would do (delegating
            // to self) so we can test the closure the compile() method passes
            configClosure.delegate = ant
            configClosure()
            this
        }

        when: "compile action is called"
        task.compile()

        then: "dest dir is created, PCTCompile Ant task is created but no DBConnections are created"
        task.destinationDir.isDirectory()
        0 * ant.DBConnection(*_)

        when: "a DB connection reference is added"
        task.dbConnections << 'foodb'
        task.compile()

        then: "a DBConnection is created"
        1 * ant.DBConnection([refid: 'foodb'])

        when: "propath property is set"
        task.propath = project.files('src')
        task.compile()

        then: "propath Ant type is configured"
        1 * ant.propath(_ as Closure) >> { Closure cfgr ->
            cfgr.delegate = ant
            cfgr()
            this
        }
        // NOTE: this relies on implementation of
        // FileCollection.addToAntBuilder and specifically
        // AntFileCollectionBuilder which it delegates to
        1 * ant.file([file: project.files('src').getSingleFile().path])

        // TODO: not sure how to test that sources are added to AntBuilder
        //   this is done using FileCollection.addToAntBuilder but to
        //   define expected interaction on our ant mock we'd need to
        //   rely on implementation details of addToAntBuilder()
    }

    def "compiler args are passed to PCTCompile"() {
        given: "a project with AntBuilder and an instance of CompileAblTask"
        def task = createTask()
        task.destinationDir = project.file('destDir')

        when: "compiler args are populated"
        task.compileArgs.listing = true
        task.compileArgs.preprocess = true
        task.compile()

        then: "PCTCompile is passed the extra args"
        1 * ant.PCTCompile(
            [destDir: task.destinationDir.path, listing: true, preprocess: true],
            _ as Closure
        )
    }

    /**
     * Although PCTCompile supports <resources> (which is how a FileCollection
     * is added to AntBuilder by default), the collection created by
     * FileCollection.addToAndBuilder does not work right - it causes e.g.
     * 'path/to/file.p' to be received as
     * [baseDir: 'path/to', file: 'to/file.p'] by PCT for some reason.
     *
     * It does seem to work a lot better when adding the collection as a
     * FileSet so make sure this is the way it's done.
     */
    def "compile adds fileset resources to PCTCompile task"() {
        given: "a project with AntBuilder, some sources and an instance of CompileAblTask"
        project.files('src', 'src/mod1', 'src/mod2').files*.mkdir()
        project.files('src/top.p', 'src/mod1/foo.p', 'src/mod2/bar.p').
            files*.write('')

        def task = createTask()
        task.destinationDir = project.file('destDir')

        1 * ant.PCTCompile(_, _ as Closure) >> { p, Closure configClosure ->
            println delegate
            configClosure.delegate = ant
            configClosure()
            this
        }

        1 * ant.fileset([dir: project.file('src').absolutePath], _ as Closure) >> { p, Closure c ->
            println delegate
            c.delegate = ant; c(); this
        }

        /* when adding a FileCollection to ant as a FileSet, it gets
         * converted to a DirectoryFileTree + PatternSet which in turn
         * is added as an <and> node containing a series of <or> and
         * <not> nodes containing <filename name=""> elements.
         *
         * That itself uses a lot of dynamic method calls and
         * unfortunately mocking gets lost somewhere after the first
         * <and> and not even getting the <or>s.
         */
//        1 * ant.and(_ as Closure) >> { Closure c ->
//            println delegate
//            c.delegate = ant; c(); this
//        }

        when: "compiling everything"
        task.source('src')
        task.compile()

        then: "all expectations already setup in 'given' block"
        true
    }

    /* OpenEdge limits the number of databases that can be connected
     * during an OpenEdge session to 5 by default.
     *
     * If more are needed {@code -h NUM} option needs to be passed.
     */
    def "compile handles cases with >5 databases"() {
        given: "an instance of CompileAblTask"
        def task = createTask()
        task.destinationDir = project.file('destDir')

        1 * ant.PCTCompile(_, _ as Closure) >> { p, Closure c ->
            c.delegate = ant; c(); this
        }

        when: "more than 5 database connections are used"
        task.dbConnections.addAll([
            'foodb', 'bardb', 'bazdb', 'quxdb', 'quuxdb', 'quuzdb',
        ])
        task.compile()

        then: "option -h is automatically added to progress command"
        1 * ant.option([name: '-h', value: '6'])
    }
}
