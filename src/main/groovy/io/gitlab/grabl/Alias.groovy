package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

class Alias extends DefaultTask {

    @Input
    String name

    @Input @Optional
    Boolean noError = null

    @TaskAction
    def alias() {
        Map args =[:]

        args.put('name', name)
        args.put('noError', noError)

        def inpParam = args.findAll {it.value != null}

        // ant.PCTAlias(*:inpParam) 
    }

     protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }    
}
