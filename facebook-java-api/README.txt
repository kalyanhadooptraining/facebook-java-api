Quick Start (project users):

	1.  Download the latest project JAR file at http://facebook-java-api.googlecode.com/files/facebook-java-api-1.5.jar
	
	2.  Download the required JSON library at http://facebook-java-api.googlecode.com/files/json-1.0.jar
	
	3.  If (and *only* if) you are using Java 5, also download the following libraries:
	
		http://facebook-java-api.googlecode.com/files/jaxb-api-2.1.jar
		http://facebook-java-api.googlecode.com/files/jaxb-impl-2.1.jar
		http://facebook-java-api.googlecode.com/files/jsr173-api-1.0.jar
		
	4.  Deploy all of the downloaded JAR files onto your application server, or otherwise 
		place them on your runtime classpath.  This will allow you to use the Facebook 
		Platform API.
		
	5.  If you need help using the API, consult the project Javadoc at http://64.81.51.104:54321/facebook/javadoc/index.html
	
	
Prerequisites (project developers):

	Java 5 or higher 						http://java.sun.com
	Eclipse 3.0 or higher					http://www.eclipse.org
	Maven 1.x (*not 2.0*)					http://maven.apache.org/maven-1.x/
	
	Subclipse plugin for Eclipse			http://subclipse.tigris.org/install.html
	-- OR --
	SVN 									http://subversion.tigris.org/ 
			
	
Getting Started (project developers):

	0.  Install all of the prerequisites mentioned above.  Be sure that your Java and 
	    maven binaries are on your system path.  Also be sure to define the JAVA_HOME 
	    and MAVEN_REPO environment variables.  You may also need to define MAVEN_REPO 
	    inside of Eclipse by going to Window -> Preferences -> Java -> Build Path -> Classpath Variables
	    and adding the entry there.
	
	1.  Check out the source code from the SVN repo by typing 
		"svn co http://facebook-java-api.googlecode.com/svn/trunk/facebook-java-api"
		at the command line if you are using the command-line version of SVN.  
		
		If you are using Subclipse, open the "SVN Repository Exploring" perspective
		and then right click and select "New -> Repository Location".  Enter 
		"http://facebook-java-api.googlecode.com/svn/" as the URL, and click Finish.  
		You should now see the repository listed in the display on the left.  Expand 
		the repository entry and navigate to /trunk/facebook-java-api.  Right-click 
		on this folder, and then select "Checkout..." to check out the project.
		
		Note that while it is possible to download the entire project as a plain .zip archive, 
		it is generally recommended to checkout from SVN instead if you plan on building from 
		sources.  This will make things much easier should you ever decide you want to commit 
		any changes you have in the future.
		
	2.  At the command line, navigate to the top-level project directory ('/favebook-java-api' 
	    unless you have renamed the project on your system) and type "maven eclipse".  This 
	    will configure the project for use with Eclipse, and download any necessary dependencies.
	    
	3.  Refresh your project in Eclipse (right click on it, and select "Refresh").
	
	4.  Source code is under 'src/java/com/facebook/api'.  Make changes and implement new code 
	    here.
	    
	5.  Unit tests should go under 'src/test/com/facebook/api'.  Add any unit tests you create 
	    here.
	    
	6.  To build the project, go back to the command line where you typed "maven eclipse" and 
	    type "maven clean install".  This will compile the java files and package them inside 
	    of a JAR, which should appear as 'target/facebook-java-api-1.2.jar'.
	    

Using the JAR File:

	Once built, the only thing you need to do to use the JAR file is to deploy it to your 
	application-server of choice, or otherwise include it on your runtime classpath.  Note 
	that you must also deploy json-1.0.jar alongside the Facebook JAR file in order for it 
	to work.  You can find a copy of this file in your maven repository, or alternately, you 
	can download it directly from:
	
	http://facebook-java-api.googlecode.com/files/json-1.0.jar
	
	-- JAVA 5 USERS ONLY --
	All versions of this library from 1.2 onward also depend upon the JAXB libraries.  With 
	Java 6, these libraries are available on the classpath automatically.  With Java 5, however, 
	they are not, so you must manually include them as well (or alternatively you can switch 
	to Java 6).  
	
	You can download the required dependencies at:
	
	http://facebook-java-api.googlecode.com/files/jaxb-api-2.1.jar
	http://facebook-java-api.googlecode.com/files/jaxb-impl-2.1.jar
	http://facebook-java-api.googlecode.com/files/jsr173-api-1.0.jar
	
	These files must all be deployed alongside the Facebook JAR file and the json-1.0 library.
	-- END JAVA 5 SPECIFIC CONTENT -- 
	
		
Committing Changes:

	Committing new code to the project requires a member account and password.  If you 
	are interested in committing your revisions/helping out with this project, please 
	send an e-mail to aroth@bigtribe.com to be allocated a developer account with full 
	access to commit new code.
	
	
Changelog:

	From v1.4 to v1.5
		- Fix various bugs in FacebookJsonRestClient (should actually be usable now).
		- Merge in changes from official Facebook API made on 11/7/2007.
	
	From v1.3 to v1.4
		- Merge in changes from official Facebook API made on 10/30/2007.
		- Refactor changes so that they don't break reverse compatibility for anyone using the official API.
		- Refactor changes so that they don't break reverse compatibility for anyone using a previous version this API.
		- Add a 'FacebookJaxbRestClient' that returns JAXB objects when making API calls.
		- Extend the 'IFacebookRestClient' interface to include sms and data API calls.
		- Refactor any UID's that were expressed as Integers in the new Facebook code to use Longs.
		- Remove dependency on the simple-json library, the previous json library is still used.
		- Add some new utility methods to 'FacebookXmlRestClient' and 'FacebookJsonRestClient'.
		- Cleanup, add documentation, deprecate things where appropriate.

	From v1.2 to v1.3
	    - Add support for all marketplace.* API calls.
	    - Add support for users.hasAppPermission and users.setStatus API calls.
	    - Add support for fbml.setRefHandle API call.
	    - Add support for sms.* API calls.
	    - Add support for data.setUserPreference(s) and data.getUserPreference(s) API calls.
	    - Add JAXB bindings for all marketplace.* and data.* API calls.
	    - Add MarketListing utility class to assist in creating marketplace listings.
	    - Define enum's that can be used for specifying marketplace categories, subcategories, and status codes.
	    - client now supports all "official" API methods as specified at http://wiki.developers.facebook.com/index.php/API

	From v1.1 to v1.2
		- Add JAXB bindings for all FacebookRestClient methods that return a Document.
		- Add utility method to get the JAXB response object corresponding to the last API call made through the client.

	From v1.0 ("official" Facebook release in July) to v1.1:
		- The 'Pair' class has been factored out of FacebookRestClient, and is now public.
		- All object id's use 64-bit longs as their datatype to be compatible with pending Facebook platform changes.
		- 'notifications_sendRequest' is marked as deprecated (it is no longer supported by Facebook).
		- 'feed_publishActionOfUser' is marked as deprecated (it is being replaced with 'feed_publishTemplatizedAction').
		- Add support for 'feed_publishTemplatizedAction' API call.
		- Add 'TemplatizedAction' utility class to assist in creating feed entries through 'feed_publishTemplatizedAction'.
		- Add ability to retrieve the raw XML/text snippet returned by the Facebook API server (note that this can only be called once per API call).
		- Update/add javadoc comments.
		- Provide more useful error messages and debugging output.
		- Removed 'ExampleClient' sample application (removes spurious dependency on BrowserLauncher).
		- Fixed various bugs in 'PhotoTag' class.