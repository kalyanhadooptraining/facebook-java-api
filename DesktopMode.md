# Introduction #

The [Desktop Authentication information on the official Facebook Wiki](http://wiki.developers.facebook.com/index.php/Login_Desktop_App) really doesn't explain what it means to authenticate a desktop application and why there's a slightly different process in place.

# Do I really need server part for my desktop application? #

If you have hundreds of users (and you don't want to provide your SECRET) then yes, you do need a server component which will have a very simple job. The desktop application will need to contact your server on startup:
  * Your server connects to Facebook using your SECRET.
  * Your server generates a session secret for the user and passes it back to the desktop application.
  * The desktop application uses this "session secret" to connect directly to Facebook.com. It never gets to know your real SECRET.

**If you're not worried about giving your SECRET away then don't bother using desktop mode!**

# Is your application set to Desktop? #

It's very important that your application is set to the correct mode. If it isn't set to desktop mode, the Facebook server won't understand when you try to pass a Session Secret. You'll get an "Invalid Signature" error because Facebook is not expecting a Session Secret. Conversely, if you're passing a normal SECRET through and your application is set to Desktop, you'll see errors.

![http://facebook-java-api.googlecode.com/svn/wiki/images/DesktopModeSettings.png](http://facebook-java-api.googlecode.com/svn/wiki/images/DesktopModeSettings.png)

# Share your API\_KEY but not your SECRET #

Imagine you have a desktop application written in Java Swing (the same principles apply to C# and other clients, but this is a page on the Facebook Java API Wiki, after all) which you want to connect to the Facebook API. If you want to retrieve information using even the sessionless methods, you'll still need to provide your Java Facebook Client with:

  * API\_KEY of your application
  * SECRET of your application

There's no problem including your SECRET on a web application because your server is doing the talking to Facebook and users will never get to see the SECRET being sent over the wire; they just see the HTML that your server produces.

Big problem with a Desktop client. If you're going to connect directly from the Desktop to Facebook then your SECRET has got to be in available on the user's PC somewhere. Whether you include it in plaintext in a properties file or make it hard to find hardcoded in the Java code, at the end of the day a hacker can get hold of it. Then, if they really wanted too, the hacker could log into Facebook with their own code using your credentials and execute whatever commands they wanted. This may include spamming people, removing your data from the Data Store API, posting inappropriate photos and other things that are unlikely to keep you in good favour with Facebook.

Facebook came up with the concept of a TEMPORARY SECRET which has limited rights and you can distribute safely to a Desktop client to solve this issue.

## What can't you do with a TEMPORARY SECRET? ##

Having been allocated a TEMPORARY SECRET, a user cannot call methods such as [Fbml.setRefHandle](http://wiki.developers.facebook.com/index.php/Fbml.setRefHandle). Any method that configures the application itself is out of bounds. The user retains access to the full range of methods to query or update their Facebook information.

## Producing your TEMPORARY SECRET on your server ##

What the Facebook Wiki assumes is obvious (it really isn't) is that you'll need a server to produce the TEMPORARY SECRET for you. It would work like this if you were using Java Webstart:

  * User hits your website and clicks on your "Start desktop application" .jnlp hyperlink.
  * Your website creates a connection to facebook using:
    * API\_KEY
    * SECRET - You're on the website, remember, so you can keep this properly secret.
  * Your website generates an auth token using [Auth.createToken](http://wiki.developers.facebook.com/index.php/Auth.createToken)
  * Your website forwards the user to the Facebook login page to ensure that the user is logged in
  * Facebook redirects the user back to your website after they've logged in
  * Now that the auth token is verified, your website generates a session token using [Auth.getSession](http://wiki.developers.facebook.com/index.php/Auth.getSession) passing the auth token as a parameter.
    * Crucially, the **generate\_session\_secret parameter** is set
  * Your website receives a response which contains:
    * SESSION KEY
    * TEMPORARY SECRET - This is what you're going to deliver to the Desktop client

Then your website dynamically produces a .jnlp file and puts in properties of:
  * SESSION KEY
  * API\_KEY
  * TEMPORARY SECRET - The user will never see your real SECRET

The user's local Java downloads the client code and starts executing it. When the user wants to query information from Facebook it uses the API\_KEY and TEMPORARY SECRET to get hold of the data.

# What does this mean for the Facebook Java API? #

ExtensibleClient currently has a method called setIsDesktop(boolean) which is used to signify that we want to run in Desktop mode. When auth\_getSession(token) gets called, the `_`isDesktop boolean is consulted to work out whether we want to add the generate\_session\_secret parameter and get a TEMPORARY SECRET value. Assuming that `_`isDesktop has been set, the cacheSessionSecret ends up getting set to the value of the TEMPORARY SECRET.

This cacheSessionSecret variable is never used for anything. That's absolutely the correct behaviour. The client continues to generate signatures using the _secret value which was used to setup the client in the first place._

As described above, the server code can use getCacheSessionSecret() to get hold of this value and ultimately give it to the client application.

## Should the Facebook Java API be changed? ##

The ExtensibleClient class currently confuses the use case outlined in this document. There are 2 completely separate use cases:

  * Server calls auth\_getSession to get hold of a TEMPORARY SECRET.
    * The server is in no way "in desktop mode".
  * Desktop Client instantiates Facebook Java API using the TEMPORARY SECRET.
    * The client can auto-dectect that it is in desktop mode because the TEMPORARY SECRET conveniently is a different length (24 chars) to the normal secret (32 chars) and always ends with 2 underscores 
    * The Desktop Client is restricted as to which methods it can call.

The meaning of getIsDesktop() is now clarified in that it means "a TEMPORARY SECRET has been used when creating this Facebook Client; please call Facebook Server methods accordingly". We don't need setIsDesktop() any more so it has ben removed.

Dave Boden's suggested change is raised in [issue 179](http://code.google.com/p/facebook-java-api/issues/detail?id=179).

We could go further, to create an overloaded version of auth\_getSession with a boolean createTemporarySecret parameter. The method would return a tuple of the SESSION\_KEY and the TEMPORARY SECRET. That would allow us to get rid of the cacheSessionSecret which shouldn't really be a member variable of ExtensibleClient, it should be short lived. The server should be able to use the same ExtensibleClient object to generate thousands of authentication tokens and session keys; the generated TEMPORARY SECRET doesn't need to be linked to the ExtensibleClient that created it. However, this is too much change for now and doesn't fit with the way that auth\_getSession() already caches the user ID that Facebook provides.

## Does the Facebook Java API need to know which methods are out of bounds for TEMPORARY SECRETs? ##

Yes. Some of the methods, for example setFBML, must be called differently if we're using a TEMPORARY SECRET. For those that are not allowed whatsoever, it's perfectly acceptable to have no special code for Desktop mode and let the Facebook server deny access and provide a suitable error message. We don't want to uselessly "optimise" things to avoid a call to the Facebook server in this situation. Calling a function that you're not allowed to call from a Desktop client is a programmer error that will never make it into a production-quality Desktop client.