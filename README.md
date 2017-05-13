# Grabl #

[Gradle](https://gradle.org/) +
[(OpenEdge) ABL](https://www.progress.com/openedge) = grabl (using
[Riverside-Software PCT](https://github.com/Riverside-Software/pct))

Grabl is a plugin for Gradle for working with OpenEdge ABL.  It provides gradle
tasks to compile ABL code and run unit tests using ABLUnit.  All the hard work
is done by PCT, thanks to gradle's fantastic integration with Ant.

## Usage ##

In your `build.gradle` add:

    plugins {
        id 'io.gitlab.hendosdo.grabl' version '0.0.0-SNAPSHOT'
    }

This will add [PCT](https://github.com/Riverside-Software/pct) tasks and types
to your project by:

 - adding new configuration _pct_
 - adding a repository where PCT can be downloaded from; this is temporary, we
   hope PCT can be published to Maven Central or JCenter in the future
 - adding dependency on PCT 207
 - adding dependency on Google gson 2.8.0 which is required by PCT ABLUnit task
 - loading PCT Ant tasks and types into AntBuilder using loader ref _pct_
