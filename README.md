# Grabl #

[Gradle](https://gradle.org/) +
[(OpenEdge) ABL](https://www.progress.com/openedge) = [grabl][] (powered-by
[Riverside-Software PCT](https://github.com/Riverside-Software/pct))

[Grabl] is a plugin for Gradle for working with OpenEdge ABL.  It
provides gradle tasks to compile ABL code and run unit tests using
ABLUnit.  All the hard work is done by PCT, thanks to gradle's
fantastic integration with Ant.

## Usage ##

In your `build.gradle` add:

``` groovy
plugins {
  id "io.gitlab.grabl.grabl" version "0.1.0"
}
```

This will add [PCT](https://github.com/Riverside-Software/pct) tasks and types
to your project by:

 - adding new configuration _pct_
 - adding a repository where PCT can be downloaded from; this is temporary, we
   hope PCT can be published to Maven Central or JCenter in the future
 - adding dependency on PCT 207
 - adding dependency on Google gson 2.8.0 which is required by PCT ABLUnit task
 - loading PCT Ant tasks and types into AntBuilder using loader ref _pct_

## Links ##

- [Home Page][grabl]
- [Plugin Portal](https://plugins.gradle.org/plugin/io.gitlab.grabl.grabl)
- [Plugin Portal (base)](https://plugins.gradle.org/plugin/io.gitlab.grabl.grabl-base)
- [Examples Repo](https://gitlab.com/grabl/grabl-samples)

## Base Plugin ##

The plugin is split into base and convention plugins.

The base plugin (`grabl-base`) only provides **capability**, i.e.
building blocks - the extension, task types, etc. for building ABL
projects.

The **convention** plugin (`grabl`) provides sensible defaults and task
instances so a project that follows the convention can be built with
not much more than the application of the plugin in `build.gradle`.

If you want to provide your own task instances you can apply only the
base plugin and your own configuration:

```groovy
plugins {
    id 'io.gitlab.grabl.grabl-base' version '0.0.0'
}

task compileAbl(type: CompileAblTask) {
    destinationDir = file('output')
    source = fileTree(dir: 'src', include: '**/*.p')
}
```


[grabl]: https://grabl.gitlab.io/
