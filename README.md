[![pipeline status][pipeline-img]][pipeline-target] [![coverage report][coverage-img]][coverage-target]

# Latte #

This is a Progress fork of the [Grabl](https://gitlab.com/grabl) project to enhance and support package updates.

[Latte] = Progress fork of [Grabl][]

[Gradle] + [(OpenEdge) ABL][OpenEdge] = [grabl][] (powered-by [Riverside-Software PCT][PCT])

[Latte] is a plugin for [Gradle] providing language support for
[OpenEdge ABL][OpenEdge].  It provides gradle tasks to compile ABL code
and run unit tests using ABLUnit.  All the hard work is done by [PCT],
thanks to gradle's fantastic integration with [Ant].

## Usage ##

[Latte] is hosted in the [Gradle Plugin Portal][grportal-grabl] so you
can use it by just adding this to your `build.gradle`:


``` groovy
plugins {
  id "oe.espresso.latte.latte" version "0.2.0"
}
```

This will add [PCT][] tasks and types to your project and integrate
[PCT] with [Gradle] lifecycle tasks. It does this by modifying the
[Gradle] project model.  

The following are a list of enhancements beyond the original [grabl] plugin

 - adds a dependency on [PCT] 211
 - adds a dependency on Google gson 2.8.0 which is required by PCT ABLUnit task
 - adds a backup database task
 - adds the ability to run ABL code as a task
 - adds the ability to create a database
 - adds the ability to create procedure libraries
 - adds the ability to create .oear's
 
## Links ##

# NEEDS TO BE UPDATED WHEN 
- [Home Page, Docs, Guides][grabl]
- [Plugin Portal][grportal-grabl]
- [Plugin Portal (base)][grportal-grabl-base]
- [Examples Repo](https://gitlab.com/grabl/grabl-samples)

## Contributing ##

Want to suggest a feature or report a bug? Head to [issue tracker][issues].

Code contributions are very welcome, please check out [hacking][] notes.

## License ##

latte and grabl are free and open-source software licensed under the
[Apache License 2.0](https://github.com/progress/latte/LICENSE)



[Gradle]: https://gradle.org/
[OpenEdge]: https://www.progress.com/openedge
[latte]: https://github.com/progress/latte
[grabl]: https://grabl.gitlab.io/
[PCT]: https://github.com/Riverside-Software/pct
[Ant]: http://ant.apache.org/
[issues]: https://github.com/progress/latte/issues
[hacking]: HACKING.md
[pipeline-img]: https://gitlab.com/grabl/grabl/badges/master/pipeline.svg
[pipeline-target]: https://gitlab.com/grabl/grabl/commits/master
[coverage-img]: https://gitlab.com/grabl/grabl/badges/master/coverage.svg
[coverage-target]: https://grabl.gitlab.io/grabl/reports/clover/html/
[grportal-grabl]: https://plugins.gradle.org/plugin/oe.espresso.latte.grabl
[grportal-grabl-base]: https://plugins.gradle.org/plugin/oe.espresso.latte.grabl-base
