# Caveat #

The old BuildCodeInEclipse page is now old.  So I am trying to create new instructions here, but this is a work in progress.  So please bear with me.

# Introduction #

The first thing to recognize is that this is a project that is built using Maven, and leverages Maven's Dependency management.  I highly recommend Maven.  Also Maven Central Repository is becoming a large place to easily find dependent libraries and lots of good meta-data; that is why I think that every Java developer has to become familiar with it, if not actively use it.  In fact, the same can be said for Subversion.

If you're familiar with Maven, have at the project, if you're not familiar with Maven, we'll give you a few pointers to get you started using our code, but recommend that you go and learn about Maven fully.

From here, the concept is the same as any other project: 1) check out source, 2) build source.  But with maven projects you can either do that from the command-line (by installing subversion and maven), or if you are going to be developing on the project within Eclipse might be better (by installing the Subclipse and M2Eclipse plugins).  I of course prefer the command-line subversion and maven, but since I develop in eclipse M2Eclipse is required; because M2Eclipse integrates some parts of maven into eclipse so that it behaves much like a native project.

# Command-Line: Subversion and Maven #

For this method you must install:
  * Subversion (http://subversion.apache.org/packages.html)
  * Maven 2 (http://maven.apache.org/download.html)

The Subversion installation should be straight forward, since there should be packages available for any and all operating systems.  Maven2 is pretty simple as well, just download the tarball, extract somewhere, and add it to your path.

Once you have Subversion installed, you can go and fetch the latest source code:

**svn checkout http://facebook-java-api.googlecode.com/svn/trunk/ facebook-java-api-read-only**

Then you can go into that directory, and have maven build the project:

cd facebook-java-read-only
mvn install -DskipTests=true

Please be patient the first time you run it; it will take a while.  The reason is that Maven will go and download a lot of libraries and dependencies and bring them into your local machine ({HOME}/.m2/repository }.  The bad news is that it takes a while to download, the good news is that once they are local, it won't have to re-download them and they are easily re-used whenever any other maven project requires those libraries; so it gets faster.

Normally, maven would run the unit tests during the build; the "-DskipTests=true" disables that to make your build go faster, and because you have to do a few more things to be able to setup for the UnitTests.

If you use maven and just wanted to build the trunk, then you're done.  If you don't use maven then you're wondering where the .zip file with all of the jar files you need is.  To generate that you have to do a few more things, which I'll describe below.  Essentially, we have to ask maven to collect not only the project jar files, but all of the dependencies as well.