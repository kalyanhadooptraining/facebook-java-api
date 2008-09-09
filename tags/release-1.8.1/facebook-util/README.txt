   ---  Facebook Companion Utility (non-desktop apps only)  ---
   
Introduction:

	This project provides an add-on utility to the Facebook Java API that enables 
	various enhanced functionality, such as automatic signature verification, and 
	a simplified login process.   	
	
	The add-on utility makes use of the Servlet API, which means that it can only be 
	used by iframe and FBML-based apps (unless your desktop app happens to bundle a 
	webserver with it for some reason).  
	
	For full details, visit http://groups.google.com/group/facebook-java/browse_thread/thread/7ac86a468697e5f7.
	
	
Quick Start (project users):

	1.  Configure the Facebook Java API by following the instructions at http://code.google.com/p/facebook-java-api
	
	2.  Download the latest companion JAR file at http://facebook-java-api.googlecode.com/files/facebook-util-1.7.4.jar
	
	3.  Download the required servlet-api library at http://facebook-java-api.googlecode.com/files/servlet-api-2.4.jar
		
	4.  Deploy all of the downloaded JAR files onto your application server, or otherwise 
		place them on your runtime classpath.  This will allow you to use the companion utility.
		
	5.  If you need help using the API, consult the project Javadoc at http://64.81.51.104:54321/facebook/helper/javadoc/index.html
	
	
Changelog:
	
	From 1.7.1 to 1.7.2
		- Use cookies to keep track of state, as the PHP version of this utility does.
	
	From 1.7.1 to 1.7.2
		- Better handling of stale auth_token.
	
	From v1.6 to v1.7.1
		- Added an isLogin() method, similar to how isAdd() works.
	
	v1.6
		- Initial version.