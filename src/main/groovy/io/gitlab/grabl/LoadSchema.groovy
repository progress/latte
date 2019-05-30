package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

class LoadSchema extends DefaultTask {
    
    @Input
    String srcFile

    @Input @Optional
    boolean unfreeze = false

    @Input @Optional
    boolean onlineChanges = false

    @Input @Optional
    boolean commitWhenErrors = false

    @Input @Optional
    String callbackClass = null

    @TaskAction
    def loadSchema() {
        Map args =[:]

        args.put('srcFile', srcFile)
        args.put('unfreeze', unfreeze)
        args.put('onlineChanges', onlineChanges)
        args.put('commitWhenErrors', commitWhenErrors)
        args.put('callbackClass', callbackClass)

        def inpParam = args.findAll {it.value}

        ant.PCant.PCTLoadSchema(*:inpParam)
    }

     protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }    
}
