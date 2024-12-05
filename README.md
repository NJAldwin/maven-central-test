# maven-central-test

A test project to test the publishing of a project to Maven Central.

[![Maven Central Version](https://img.shields.io/maven-central/v/us.aldwin.test/maven-central-test)](https://central.sonatype.com/artifact/us.aldwin.test/maven-central-test)

https://central.sonatype.com/artifact/us.aldwin.test/maven-central-test

https://central.sonatype.com/artifact/us.aldwin.test/maven-central-second-test

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
