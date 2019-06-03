package io.gitlab.grabl

import org.gradle.api.tasks.Input
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Optional

class DBConnection extends DefaultTask {

    @Input @Optional
    String dbName = null 

    @Input @Optional
    String paramFile = null 

    @Input @Optional
    String dbDir = null 

    // Since a port "Bob" works, this should be a string
    @Input @Optional
    String dbPort = null 

    @Input @Optional
    String protocol = null 

    @Input @Optional
    String logicalName = null 

    @Input @Optional
    String cacheFile = null 

    @Input @Optional
    String dataService = null 

    @Input @Optional
    String dbType = null 

    @Input @Optional
    String hostName = null 

    @Input @Optional
    String userName = null 
    
    @Input @Optional
    String password = null 

    @Input @Optional
    Boolean readOnly = null 

    @Input @Optional
    Boolean singleUser = null 

    @Input @Optional 
    String id = null

    @TaskAction
    def connect() {
        Map args =[:]

        args.put('dbName', dbName)
        args.put('paramFile', paramFile)
        args.put('dbDir', dbDir)
        args.put('dbPort', dbPort)
        args.put('protocol', protocol)
        args.put('logicalName', logicalName)
        args.put('cacheFile', cacheFile)
        args.put('dataService', dataService)
        args.put('dbType', dbType)
        args.put('hostName', hostName)
        args.put('userName', userName)
        args.put('password', password)
        args.put('readOnly', readOnly)
        args.put('singleUser', singleUser)
        args.put('id', id)

        def inpParam = args.findAll {it.value != null}

        ant.DBConnection(*:inpParam)
    }

     protected GrablExtension getExt() {
        return project.extensions.getByType(GrablExtension)
    }    
}
