[![pipeline status][pipeline-img]][pipeline-target]

# Grabl #

[Gradle] + [(OpenEdge) ABL][OpenEdge] = [grabl][] (powered-by
[Riverside-Software PCT][PCT])

[Grabl] is a plugin for [Gradle] providing language support for
[OpenEdge ABL][OpenEdge].  It provides gradle tasks to compile ABL code
and run unit tests using ABLUnit.  All the hard work is done by [PCT],
thanks to gradle's fantastic integration with [Ant].

## Usage ##

[Grabl] is hosted in the [Gradle Plugin Portal][grportal-grabl] so you
can use it by just adding this to your `build.gradle`:


``` groovy
plugins {
  id "io.gitlab.grabl.grabl" version "0.1.0"
}
```

This will add [PCT][] tasks and types to your project and integrate
[PCT] with [Gradle] lifecycle tasks. It does this by modifying the
[Gradle] project model in the following way:

 - adds new configuration _pct_
 - adds a repository where PCT can be downloaded from; this is
   temporary, we hope [PCT] can be published to Maven Central or
   JCenter in the future
 - adds a dependency on [PCT] 207
 - adds a dependency on Google gson 2.8.0 which is required by PCT
   ABLUnit task
 - loads PCT Ant tasks and types into AntBuilder using loader ref _pct_
 - creates native [Gradle] tasks

## Links ##

- [Home Page, Docs, Guides][grabl]
- [Plugin Portal][grportal-grabl]
- [Plugin Portal (base)][grportal-grabl-base]
- [Examples Repo](https://gitlab.com/grabl/grabl-samples)

## Contributing ##

Want to suggest a feature or report a bug? Head to [issue tracker][issues].

Code contributions are very welcome, please check out [hacking][] notes.

## License ##

grabl is free and open-source software licensed under the
[Apache License 2.0](https://gitlab.com/grabl/grabl/blob/master/LICENSE)



[Gradle]: https://gradle.org/
[OpenEdge]: https://www.progress.com/openedge
[grabl]: https://grabl.gitlab.io/
[PCT]: https://github.com/Riverside-Software/pct
[Ant]: http://ant.apache.org/
[issues]: https://gitlab.com/grabl/grabl/issues
[hacking]: HACKING.md
[pipeline-img]: https://gitlab.com/grabl/grabl/badges/master/pipeline.svg
[pipeline-target]: https://gitlab.com/grabl/grabl/commits/master
[grportal-grabl]: https://plugins.gradle.org/plugin/io.gitlab.grabl.grabl
[grportal-grabl-base]: https://plugins.gradle.org/plugin/io.gitlab.grabl.grabl-base
