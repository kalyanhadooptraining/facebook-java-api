### Summary ###

A Facebook API client implemented in Java, originally derived from the official Facebook client.

### Forum ###

If you have any questions, please go use the Forum: http://groups.google.com/group/facebook-java

However, please read [this posting guide](http://groups.google.com/group/facebook-java/web/please-only-post-on-this-forum-if) first and it's certainly worth looking at the **[Frequently Asked Questions](FAQ.md)** page.

### Javadoc ###

The [facebook-java-api Maven Site](http://mrepo.happyfern.com/sites/facebook-java-api) has links to the Javadoc for this project:

  * [Main project Javadoc](http://mrepo.happyfern.com/sites/facebook-java-api/facebook-java-api/apidocs/index.html)
  * [Schema (JAXB) Javadoc](http://mrepo.happyfern.com/sites/facebook-java-api/facebook-java-api-schema/apidocs/index.html)


### News ###

**Dec 2 - Release: 3.0.2 Lots Of Fixes, and changes to be in sync with api ( deprecating methods, adding methods, etc )**

Oct 29 - Release: 3.0.1 lots of minor fixes, deprecated many of the methods that Facebook will remove soon.

Aug 23 - Switched trunk and "composition" branch so that what's now on the trunk is in preparation for a 3.x release (specifically 3.0.1). Please try and switch to 3.0.1-SNAPSHOT in your development environment as soon as possible.

May 1 -
We have the latest release, 2.1.1.  Improved schema.xsd and Desktop mode.

Mar 8 -
We have the latest release, 2.1.0.  Please give it a spin.

Feb 15 -
We have the latest release, 2.0.5.  Please give it a spin.

### Introduction ###

As of May 2008, Facebook has discontinued any support of their official Java client, directing users interested developing Facebook applications in Java to use one of the various third-party clients out there.  As such, the purpose of this project is now to maintain, support, and extend the abandoned code base to provide a high-quality, up to date version of the Facebook API client for Java developers, and to keep the Java client up to date as the Facebook Platform API changes and evolves.

### Releases ###

We are distributing our project through a Maven repository.  So if you use Maven or Ivy or some compatible build tool, please read about the available repositories at: [MavenSupport](MavenSupport.md)

We will be updating the wiki page with the latest Release information too: [Releases](Releases.md)

### Examples ###

The current examples that we have (sorry they are a bit dated) are at the wiki, [Examples](Examples.md).

Mark is leading an effort to port Facebook's PHP example application, [TheRunAround, to Java](http://code.google.com/p/javarunaround/).