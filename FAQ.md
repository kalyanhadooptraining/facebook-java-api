  * How do I switch on **debug logging** so that I can see what's going on more clearly?
    * javax.util.logging is far more difficult to get to grips with than log4j, so let's do this with log4j. Firstly, declare a dependency on log4j:log4j:1.2.15 or later in your project by adding a dependency in your pom.xml file. Then, place a [log4j.xml file like this one](http://facebook-java-api.googlecode.com/svn/trunk/facebook-java-api/src/test/resources/log4j.xml) in your classpath. Once that's done, you'll be able to control the root logger and set everything to debug level. Alternatively, you can add other loggers and only debug certain packages or classes. There's loads of information on how to configure log4j on the web.

```
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.15</version>
    </dependency>
```

  * When requesting an XML document from Facebook, I get nothing back. I see **`[#document: null]`** in my debugger.
    * `[#document: null]` isn't nothing! The .toString() method on the Document object is trying to communicate that the Document's root element's "node value" is not defined. Unfortunatly, it chooses to represent that as `[#document: null]` which is just plain confusing. Use the com.google.code.facebookapi.XMLTestUtils.print(Node dom); utility method which is checked into the facebook-java-api source tree to pretty-print your Document and see what's really in there.

  * I'm seeing: com.google.code.facebookapi.FacebookException?: **Invalid parameter**
    * Check whether your Desktop Mode setting is what you expect it to be. If in doubt, you probably want Web. The [Desktop Mode discussion document](DesktopMode.md) has plenty of details about this potential problem.

  * I want to contribute. How do I get my hands on the code for this project and build it in Eclipse?
    * The process has been documented on the [Build Code In Eclipse](BuildCodeInEclipse.md) page.

  * I want to contribute. How do I get commit rights to SVN so that I can check my changes in?
    * The normal procedure (with pretty much all open source projects) is.
      * Please identify a change you'd like to make, either a bug fix or a new feature.
      * [Post an issue](http://code.google.com/p/facebook-java-api/issues/list) for it, if one doesn't already exist, and upload a patch file containing your changes.
      * Alert developers on the mailing list that there's an improvement ready to go in.
      * One of the existing developers will be happy to apply your changes and, if it's well thought out and high quality, give you commit privileges so that you can do it yourself next time.
    * Looking forward to having you onboard! :)

  * Does the Facebook Java API work on J2ME for mobile devices?
    * No. The Facebook Java API uses Java SE 5 Generics and a significant number of other J2SE features like JAXB which we wouldn't want to be without. Making a library compatible with J2ME involves making significant sacrifices in order to get the benefit of the product working on mobile devices. There are therefore no plans to get the Facebook Java API in its current form working with J2ME. A separate project has been proposed on the forum; please search the forum for more details.

  * Does the Facebook Java API run on Google Android phones?
    * This is under investigation on the [AndroidSupport](AndroidSupport.md) page.

  * Why does the Facebook API only work for my account, not with anyone else's username and password?
    * You've most probably got your application settings as set to "Sandbox" which only allows developers that you name to have access using your API\_KEY. Check your application settings.

  * Where are the latest snapshot .jar files held? I just want to get the files and use them rather than using Maven to get them for me.
    * You'd be better off using Maven ;o), but the files are kept on the **[snapshot repository](http://mrepo.happyfern.com/maven2-snapshot/com/google/code/facebookapi/)**.

  * I'm having trouble building the facebook-api-annotation-processor module. It's complaining about not finding com.sun.mirror.`*` classes. What's missing?
    * Java 5 facebook annotation processors depend on you having JDK\_DIR/lib/tools.jar on your build classpath. If you take a look in the pom.xml for the facebook-api-annotation-processor module you'll see:
```
<dependency>
    <groupId>java</groupId>
    <artifactId>tools</artifactId>
    <version>1.5.0</version>
    <scope>system</scope>
    <systemPath>${java.home}/../lib/tools.jar</systemPath>
</dependency>
```
    * Make sure that your Maven is running using a JDK so that ${java.home} so that the relative link to tools.jar works. If all else fails, just hardcode an absolute path to where you know there's definitely a tools.jar.
    * The same sort of hack may be required for allowing the builder to locate apt when building the main project.  Mac OSX users specifically are affected, since Apple decided it needed to totally screw up the way JDKs are installed.
