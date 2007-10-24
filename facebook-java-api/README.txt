Prerequisites:

	Java 5 or higher 						http://java.sun.com
	Eclipse 3.0 or higher					http://www.eclipse.org
	Maven 1.x (*not 2.0*)					http://maven.apache.org/maven-1.x/
	
	Subclipse plugin for Eclipse			http://subclipse.tigris.org/install.html
	-- OR --
	SVN 									http://subversion.tigris.org/ 	
	
	
Getting Started:

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
	    of a JAR, which should appear as 'target/facebook-java-api-1.1.jar'.
	    

Using the JAR File:

	Once built, the only thing you need to do to use the JAR file is to deploy it to your 
	application-server of choice, or otherwise include it on your runtime classpath.  Note 
	that you must also deploy json-1.0.jar alongside the Facebook JAR file in order for it 
	to work.  You can find a copy of this file in your maven repository, or alternately, you 
	can download it directly from:
	
	http://facebook-java-api.googlecode.com/files/json-1.0.jar
	
		
Committing Changes:

	Committing new code to the project requires a member account and password.  If you 
	are interested in committing your revisions/helping out with this project, please 
	send an e-mail to aroth@bigtribe.com to be allocated a developer account with full 
	access to commit new code.