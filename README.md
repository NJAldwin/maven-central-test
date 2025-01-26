# maven-central-test

A test project to test the publishing of a project to Maven Central.

[![Maven Central Version](https://img.shields.io/maven-central/v/us.aldwin.test/maven-central-test)](https://central.sonatype.com/artifact/us.aldwin.test/maven-central-test)
[![javadoc](https://javadoc.io/badge2/us.aldwin.test/maven-central-test/javadoc.svg)](https://javadoc.io/doc/us.aldwin.test/maven-central-test)

https://central.sonatype.com/artifact/us.aldwin.test/maven-central-test

https://central.sonatype.com/artifact/us.aldwin.test/maven-central-second-test

Docs (javadoc.io):
 - https://javadoc.io/doc/us.aldwin.test/maven-central-test
 - https://javadoc.io/doc/us.aldwin.test/maven-central-second-test

Docs (GH pages): https://njaldwin.github.io/maven-central-test/

```maven
<dependency>
    <groupId>us.aldwin.test</groupId>
    <artifactId>maven-central-test</artifactId>
    <version>VERSION</version>
</dependency>
<dependency>
    <groupId>us.aldwin.test</groupId>
    <artifactId>maven-central-second-test</artifactId>
    <version>VERSION</version>
</dependency>
```

```gradle
implementation("us.aldwin.test:maven-central-test:VERSION")
implementation("us.aldwin.test:maven-central-second-test:VERSION")
```

## Repository/Publishing Setup

- set up and publish a GPG key
- add GPG key information to GitHub (`Secrets and variables -> Actions`) in `JRELEASER_GPG_PASSPHRASE`, `JRELEASER_GPG_PUBLIC_KEY`, and `JRELEASER_GPG_SECRET_KEY`
- set up a Maven Central account
- perform DNS TXT verification in Maven Central for the group ID's domain
- create a token in Maven Central
- add the Maven credentials to GitHub (`Secrets and variables -> Actions`) in `JRELEASER_MAVENCENTRAL_TOKEN` and `JRELEASER_MAVENCENTRAL_USERNAME`
- set up the `gh-pages` branch for github-pages (`Environments`)

## Publishing

To publish a release, update the version number in `build.gradle.kts`, then create a new version tag pointing to the latest commit in `master`.  Tags must be in [semver](https://semver.org/) format with a `v` prefix (i.e. `vMAJOR.MINOR.PATCH`).

The GitHub action will automatically build and publish the release to Maven Central and GitHub Pages, then create a GitHub Release.

Tags with a prerelease version (e.g. `-alpha.1`) will be marked as prereleases in GitHub and will not be linked from the `/stable` link in the docs.

## Etc

Template to create a library like this: https://github.com/NJAldwin/jvm-library-template
