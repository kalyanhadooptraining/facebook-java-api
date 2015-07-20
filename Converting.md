by Marc Novakowski.

# Introduction #

Here are some notes about some of the issues I encountered while converting an canvas application from the old Facebook Java API to the facebook-java API.

### Information about the app being converted ###
  * it is served via Jetty servlets
  * since the Facebook Java API didn not provide it, we created our own FacebookClient class to emulate the PHP "facebook.php" functionality
  * we use a relatively small portion of the Facebook API; most of the app logic is pure FBML
  * we ported to the latest version of facebook-java-api at the time, version 2.0.1 ([revision 325](https://code.google.com/p/facebook-java-api/source/detail?r=325))


### Issues we encountered ###

1) GET form bug: http://bugs.developers.facebook.com/show_bug.cgi?id=544

2) If you're going to use getResponsePOJO, make sure to use FacebookJaxbRestClient.  We were using FacebookXmlRestClient at first because that was closest to the original Facebook REST client.  However, we switched to FacebookJaxbRestClient because of the ease of reading the results without having to parse a DOM tree.

3) It wasn't clear what version of profile\_setFBML() to use, I kept getting a profile showing up in the boxes area but not on the main profile itself.  It turns out that I needed to specify the "profileMain" parameter with the 5-arg call (which wasn't in the IFacebookRestClient interface at the time - see [Issue 93](https://code.google.com/p/facebook-java-api/issues/detail?id=93)).  NOTE: profile\_action is now deprecated so it might be good to modify the method signatures to indicate this.

4) I originally had a single profile FBML snippet with a fb:ref in it with a single URL.  After setting the profile FBML or performing an action that would change the profile content, we would call fbml\_refreshRefUrl on the fb:ref URL.  However, with the new profile UI, we actually had two different URLs for the profile FBML (one for the boxes tab and one for the wall tab).  After wondering for a long time why the profile box content wouldn't show up on the "Wall" tab, I realized that I forgot to add an additional  fbml\_refreshRefUrl() call to the second fb:ref URL.

5) We kept our FacebookClient class but removed most of its funtionality and made it a subclass of FacebookWebappHelper.  We subclass FacebookWebappHelper instead of using it directly to add a method to get either the canvas user or logged in user (if available), as well as work around the GET form bug.  It may help others to add the "getCurrentUser" to the class.

Here is the code:
```
/**
 * Gets either the canvas user or logged in user
 * @return Long if there is a known user id, null if not
 */
public Long getCurrentUser() {
	Long user = getUser();
	if (user == null) {
		String canvasUser = (String)fbParams.get(FacebookParam.CANVAS_USER.getSignatureName());
		if (canvasUser != null) {
			user = Long.parseLong(canvasUser);
		}
	}
	return user;
}

@Override
public boolean verifySignature(Map params, String expected_sig) {
	// Work around bug: http://bugs.developers.facebook.com/show_bug.cgi?id=544
	return true;
}

```

### Issues related more to the new UI than to the API migration ###

1) We use fb:ref in our user profile boxes so that we can easily update profile data using fbml\_refreshRefUrl.  However, for app users who created a profile FBML before the new UI/API switchover, they did not have a "profile\_main" profile FBML set.  To fix this, you can either iterate through your entire facebook user list (assuming you keep track of them in your own DB), or add a profile\_setFBML() call to your code before doing the fbml\_refreshRefUrl call(s).

2) In addition to "narrow" and "wide" layouts for the profile box, we also need a "profile\_main" that fits within the limited 250px height.

3) We no longer needed to store an "infinite session key" for the app, since many (most?) API calls no longer require it