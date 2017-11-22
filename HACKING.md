# Development

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
