# Summary #

You're going to need tools:
  * Eclipse (use at least 3.4.x Ganymede)
  * Subclipse SVN plugin for Eclipse
  * m2eclipse Maven2 plugin for Eclipse

There are two distinct projects to build, the annotation processor and the main API.

# Install Eclipse Plugins #

Using Eclipse's Help --> Software Updates... menu, under the "Available Software" tab add locations:
  * http://subclipse.tigris.org/update_1.4.x     (Further instructions are at http://subclipse.tigris.org/install.html)
  * http://m2eclipse.sonatype.org/update/

# Checkout latest source code #

Now that you have Subclipse installed, you'll be able to navigate to the "SVN Repositories" view, which is most easily got at through the "SVN Repository Exploring" perspective. The aim is to get a read-only copy of the code checked out.

Right-click and select New --> Repository Location... then enter the following URL:

  * http://facebook-java-api.googlecode.com/svn

Note: the connection details are maintained at http://code.google.com/p/facebook-java-api/source/checkout should you ever want to lookup how to get a writeable connection rather than just a read-only one.

Expand the + sign for this new location and you'll see a few directories:
  * archive
  * branches
  * **facebook-api-annotation processor**
  * tags
  * **trunk**
  * wiki

## The annotation processor - facebook-api-annotation-processor project ##

Right-click on **facebook-api-annotation-processor** and select Checkout...
In the Checkout from SVN dialog box, just check out as a "project in the workspace" and give it the name "facebook-api-annotation-processor".

Now use your command line or Eclipse plugin to run "mvn install" on this project. The reason that this must be done before anything else is that Java 5 annotation processors work using a fully built binary .jar of the annotation processor.

The main API depends on the annotation processor so the annotation processor must be available as a .jar both in the Eclipse workspace and in your local maven repository before doing anything else.

You should see that a file has been produced which contains the annotation processor:
target/facebook-api-annotation-processor.jar

## The main API - facebook-java-api project ##

Right-click on **trunk** and select Checkout...
In the Checkout from SVN dialog box, just check out as a "project in the workspace" and give it the name "facebook-java-api".

![http://facebook-java-api.googlecode.com/svn/wiki/images/facebook-java-api-checkout.png](http://facebook-java-api.googlecode.com/svn/wiki/images/facebook-java-api-checkout.png)

## Summary so far ##

You'll now see 2 new projects in your Java perspective Package Explorer view called facebook-api-annotation-processor and facebook-java-api. The facebook-java-api directory contains two subprojects, one for the main Java source code and the other for the JAXB schema Java code auto generation.

## Enable Maven ##

This is most important for the facebook-java-api project, because that's what you'll be making changes to mainly:

  * Right-click on the project and select Maven --> Enable Dependency Management
  * Again, right-click on the project and select Maven -->  Update Project Configuration
  * You might have to repeat the Update Project Configuration a few times because each step introduces new source folders. Hopefully the Maven plugin will be improved a little to avoid the fact that it misses new items introduced on each step.

You should now have a successfully compiled set of source folders for you to explore.

## facebook-java-api is not building. What can I check? ##

If Eclipse is reporting that it can't find class FacebookJaxbRestClient or the Xml or Json versions then the annotation processor isn't doing its job.

You can either rely on Maven to handle the annotation processor code generation for you (there's an "apt" plugin configured in the pom.xml) or, if you want the Eclipse partial compiler to alert you of errors while you type you can choose to configure Eclipse with details of the annotation processor:

![http://facebook-java-api.googlecode.com/svn/wiki/images/annotation-processor.png](http://facebook-java-api.googlecode.com/svn/wiki/images/annotation-processor.png)

![http://facebook-java-api.googlecode.com/svn/wiki/images/annotation-processor-factorypath.png](http://facebook-java-api.googlecode.com/svn/wiki/images/annotation-processor-factorypath.png)

## Wait! I want a tagged version not the latest code... ##

Instead of navigating to trunk and checking that out as "facebook-java-api", go to tags and check out release-2.0.3 as "facebook-java-api", or any other name like "facebook-java-api-2.0.3". There's nothing to stop you having 5 different versions of the Java Facebook API checked out into differently named projects in your Eclipse environment.

# Run JUnit tests #

In the Package Explorer view, open up the facebook-java-api/src/test/java folder. Then open up the package and right click on one of the tests; select Run As --> JUnit Test.

The tests need a properties file called junit.properties to be located on the classpath. You'll probably want to put it in facebook-java-api/src/test/java. The file must not be checked into SVN (you don't want to give away your secrets). To help with this, junit.properties is checked in as svn:ignore so that by default SVN won't bother you to commit it. The file needs to contain:

```
APIKEY=98xxxxxxxxxxxxxxxxxxxxxxxxxxx
SECRET=60cxxxxxxxxxxxxxxxxxxxxxxxxx
DESKTOP_APIKEY=32xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
DESKTOP_SECRET=22xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
EMAIL=my@email.com
PASS=mypassword
```

The APIKEY and SECRET values should be yours as found on your http://www.facebook.com/apps/ page. The corresponding DESKTOP\_APIKEY AND DESKTOP\_SECRET should point to a project setup as a Desktop project to test the Desktop workflow. There are only a couple of tests on Desktop so you may choose to not bother with these ones. Your EMAIL and PASS are required to authenticate as you (not merely as your application) when running the JUnit tests. The tests use Apache HttpClient to imitate a browser and send the required authentication details to facebook.com securely.

# Point your project at your copy of facebook-java-api #

So, the whole point of you doing all this was to debug why your application doesn't work, right? You can point your project at your newly checked out version in two ways. If you're using Maven, edit your pom.xml so that instead of fetching version 2.0.3 you're fetching 2.0.4-SNAPSHOT of the java-facebook-api. You have to match what's in the pom.xml file in your checked out directory. You need to ensure that Maven --> Workspace Resolution is "Enabled" for this to work.

The other way, which is arguably simpler, is to remove any other dependencies (maven or otherwise) from your project to facebook-java-api then use the project preferences to add a direct dependency on your facebook-java-api project.

# Branching the code - don't bother reading unless you're a committer #

When creating a branch to try out code ideas, it's important to follow a standard procedure that allows for easy merging later.

Check out the level above trunk to your Eclipse. This will give you the entire directory structure including /trunk and /branches. If you already have this checked out make sure you SVN update so that you're branching from the very latest code. The SVN update command will report the latest version of the repository, and it insures that there's a good consistent version number reported at the level of the whole repository. Make a note of the current repository version.

Then run the following commands to create your branch directory:
```
svn cp trunk branches/BRANCHNAME
svn ci -m "CREATE: created branches/BRANCHNAME from trunk@VERSION"
```
You can see that the SVN commit log now has a record of the VERSION number that you branched from. That'll be useful later...

The svn commands above are simply copying (svn cp) trunk into a new branch directory, then committing (svn ci) the new branch mentioning what version number of trunk we copied over.  That's it.  That way when we do merges from trunk to branch, or collapsing of the branch into trunk, that version number is pivotal to make sure we don't miss anything during the merge.