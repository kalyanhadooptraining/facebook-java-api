# Maven Support #

If you are maintaining your dependencies through Maven Repositories,
this page tells you what repositories to point to.  Please let us know
if you have any questions or feedback!

## Releases Repository ##

All of our releases will be deployed to the Maven Central Repository, starting with the 2.0.1 release.  So all you have to do is add the appropriate dependency to your pom:

```
    <dependency>
      <groupId>com.google.code.facebookapi</groupId>
      <artifactId>facebook-java-api</artifactId>
      <version>2.0.5</version>
    </dependency>
```

## Snapshots Repository ##

### What's a snapshot Maven release ###

A Snapshot is what Maven calls a development build.  Snapshot releases are identified clearly with version numbers ending in _-SNAPSHOT_. Version 3.0.0-SNAPSHOT means "this version is a candidate to eventually become version 3.0.0". For example, trunk is at version "2.0.6-SNAPSHOT".  And we can do builds and changes against that snapshot version number as often as required.  Once we make an official release, "2.0.6", then trunk would become "2.0.7-SNAPSHOT".  This states that a Snapshot release is a development release, and can change from under you.  An actual release is final and can never change after it's been released, so the "2.0.5" jar file will never change, and if we have to make a small fix or something we might release "2.0.5.1" or tell everyone to avoid "2.0.5" and use the next available release.

Your Maven client handles snapshot releases differently to full releases; it tries to download a new version by default every day (this can be controlled by the `<snapshots><updatePolicy/></snapshots>` flag).

### Why would I want to use a snapshot release ###

If a bug that you've reported has just been fixed by a developer, the developer may have pushed a snapshot release to the snapshot repository. By switching from your full release to the snapshot, you'll be able to take advantage of the bug fix or new feature before a full release is cut.

Furthermore, you're being a good citizen and helping to identify and issues that may have crept into the codebase due to recent code changes. Hopefully the JUnit tests will catch any "regressions", but then hopefully I'll win £1million this week in the lottery.

### Repository details ###

The Maven Central Repository will not hold Snapshot releases, so we
have to maintain our own repository for this purpose.

Add the following snippet to your project's pom.xml. It points to the /maven-snapshot file store that Fernando hosts at his happyfern.com site:

```
  <repositories>
    ...
    ...
    <repository>
      <id>mrepo-happyfern-snapshot</id>
      <name>Facebook Java API Snapshot repository</name>
      <url>http://mrepo.happyfern.com/maven2-snapshot/</url>
      <snapshots>
        <enabled>true</enabled>
      </snapshots>
    </repository>
  </repositories>
```

Then, in your dependencies just name the specific snapshot release that you want. For example:

```
    <dependency>
      <groupId>com.google.code.facebookapi</groupId>
      <artifactId>facebook-java-api</artifactId>
      <version>2.0.6-SNAPSHOT</version>
    </dependency>
```