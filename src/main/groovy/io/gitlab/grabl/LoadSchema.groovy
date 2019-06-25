package io.gitlab.grabl

import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileCollection.AntType
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.Internal

class LoadSchema extends BaseGrablSourceTask {

    @Input
    String refid

    @Input @Optional
    Boolean unfreeze = null

    @Input @Optional
    Boolean onlineChanges = null

    @Input @Optional
    Boolean commitWhenErrors = null

    @Input @Optional
    String callbackClass = null

    @TaskAction
    def loadSchema() {
        Map args =[:]

        if (dlcHome)
            args.put("dlcHome", dlcHome.path)

        args.put('unfreeze', unfreeze)
        args.put('onlineChanges', onlineChanges)
        args.put('commitWhenErrors', commitWhenErrors)
        args.put('callbackClass', callbackClass)

        def inpParam = args.findAll {it.value != null}

        // This call loosely matches a build.xml that looks something like this:
        // <PCTLoadSchema dlcHome="${DLC}">
        //   <PCTConnection refid="something"/>
        //   <fileset dir="schema" includes="*.df" />
        // </PCTLoadSchema>
        project.ant.PCTLoadSchema(*:inpParam) {
            project.ant.PCTConnection("refid": "${refid}")

            this.source.addToAntBuilder(delegate, null, AntType.FileSet)
        }
    }
   
}
