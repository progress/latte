# Development

## Branch Names

Everything you work on should have an issue in
[GitLab](https://gitlab.com/grabl/grabl/issues).

The git branch name:

1. can be prefixed with issue type, e.g.: `bug/`, `chore/`, `feature-`
   or `refacor-` (either `/` or `-` is allowed)
1. should start with the issue ID, e.g.: `13-`

Building snapshots relies on this convention to produce build metadata
that is included in version string that allows identifying the source
that produced the given build. For example, given branch name
`bug/13-devSnapshot-versions-generated-by-nebula-release-are-incompatible-with-plugin-portal`
it could produce a devSnapshot version like `0.1.0-dev+13.dfc09a7`.

# Release Management

This plugin uses
[Nebula Release Plugin](https://github.com/nebula-plugins/nebula-release-plugin)
for release management. To cut a new release:

1. checkout `master` branch
1. add / update changelog
   ([git-extras changelog command](https://github.com/tj/git-extras/blob/master/Commands.md#git-changelog)
   helps here) and commit
1. run `./gradlew final|candidate|devSnapshot`, this will automatically
   trigger [publishing](#publishing)

# Publishing

This plugin uses
[Gradle Plugin Publish Plugin](https://plugins.gradle.org/docs/publish-plugin),
so publishing is as simple as:

```
./gradlew publishPlugins
```

See also [Plugin Publishing Guide](https://guides.gradle.org/publishing-plugins-to-gradle-plugin-portal/)
