# Servlet Filter to run for every request #

This servlet filter ensures that the user is logged in, within a frame inside Facebook and obtains the session key for the user.

From your servlet, you just need to call the getUserClient() static method to get hold of the client. You can then make Facebook API calls on the client.

```
import static com.emobus.stuff.LoggerConstants.facebookUserId;
import static com.emobus.stuff.LoggerConstants.ipAddress;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.w3c.dom.Document;

import com.google.code.facebookapi.FacebookException;
import com.google.code.facebookapi.FacebookWebappHelper;
import com.google.code.facebookapi.FacebookXmlRestClient;
import com.google.code.facebookapi.IFacebookRestClient;

/**
 * The Facebook User Filter ensures that a Facebook client that pertains to
 * the logged in user is available in the session object named "facebook.user.client".
 * 
 * The session ID is stored as "facebook.user.session". It's important to get
 * the session ID only when the application actually needs it. The user has to 
 * authorise to give the application a session key.
 * 
 * @author Dave
 */
public class FacebookUserFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(FacebookUserFilter.class);
	
	private String api_key;
	private String secret;
	
	private static final String FACEBOOK_USER_CLIENT = "facebook.user.client";
	
	public void init(FilterConfig filterConfig) throws ServletException {
		api_key = filterConfig.getServletContext().getInitParameter("facebook_api_key");
		secret = filterConfig.getServletContext().getInitParameter("facebook_secret");
		if(api_key == null || secret == null) {
			throw new ServletException("Cannot initialise Facebook User Filter because the " +
					                   "facebook_api_key or facebook_secret context init " +
					                   "params have not been set. Check that they're there " +
					                   "in your servlet context descriptor.");
		} else {
			logger.info("Using facebook API key: " + api_key);
		}
	}
	
	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			MDC.put(ipAddress, req.getRemoteAddr());
			
			HttpServletRequest request = (HttpServletRequest)req;
			HttpServletResponse response = (HttpServletResponse)res;
			
			HttpSession session = request.getSession(true);
			IFacebookRestClient<Document> userClient = getUserClient(session); 
			if(userClient == null) {
			    logger.debug("User session doesn't have a Facebook API client setup yet. Creating one and storing it in the user's session.");
			    userClient = new FacebookXmlRestClient(api_key, secret);
			    session.setAttribute(FACEBOOK_USER_CLIENT, userClient);
			}
			
			logger.trace("Creating a FacebookWebappHelper, which copies fb_ request param data into the userClient");
			FacebookWebappHelper<Document> facebook = new FacebookWebappHelper<Document>(request, response, api_key, secret, userClient);
			String nextPage = request.getRequestURI();
			nextPage = nextPage.substring(nextPage.indexOf("/", 1) + 1); //cut out the first /, the context path and the 2nd /
			logger.trace(nextPage);	
			boolean redirectOccurred = facebook.requireLogin(nextPage);
			if(redirectOccurred) {
				return;
			}
			redirectOccurred = facebook.requireFrame(nextPage);
			if(redirectOccurred) {
				return;
			}
			
			long facebookUserID;
			try {
			    facebookUserID = userClient.users_getLoggedInUser();
			} catch(FacebookException ex) {
			    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while fetching user's facebook ID");
			    logger.error("Error while getting cached (supplied by request params) value " +
			    		     "of the user's facebook ID or while fetching it from the Facebook service " +
			    		     "if the cached value was not present for some reason. Cached value = {}", userClient.getCacheUserId());
			    return;
			}
			
			MDC.put(facebookUserId, String.valueOf(facebookUserID));
			
			chain.doFilter(request, response);
		} finally {
			MDC.remove(ipAddress);
			MDC.remove(facebookUserId);
		}
	}

    public static FacebookXmlRestClient getUserClient(HttpSession session) {
	    return (FacebookXmlRestClient)session.getAttribute(FACEBOOK_USER_CLIENT);
	}
}
```


# Getting a User's Friends #

You select the FacebookXXXRestClient replacing XXX with either Json, Xml or Jaxb depending on how you want the results to be returned to you:
  * Json - Strings, primative types, JSONArray and JSONObject. This is what most users will need.
  * Xml - Return a javax.xml.Document
  * Jaxb - Return a Java Object generated using JAXB which referenced the Facebook .xsd schema.

```
    FacebookJsonRestClient client = new FacebookJsonRestClient("apiKey", "secretKey", "sessionId");
    JSONArray response = (JSONArray)client.friends_get();
```


### PROBABLY DEPRECATED ###

Heads up.  Most of these examples were created against the 1.8.0 version of the library.  But the current latest version is 2.0.x.  So there is no guarantee that these examples will work perfectly.


**Executing a Batch Query**
```
    //set the client to run in batch mode
    client.beginBatch();
            
    //these commands will be batched
    client.users_getLoggedInUser();
    client.friends_get();
            
    //execute the batch (which also terminates batch mode until beginBatch is called again)
    List<? extends Object> batchResponse = client.executeBatch(false);
            
    //the list contains the results of the queries, in the same order they were defined
    Long userId = (Long) batchResponse.get(0);
    Document friends = (Document)batchResponse.get(1);
    NodeList nodes = friends.getElementsByTagName("uid");
            
    //print the results
    System.out.println("USER:  " + userId);
    for (int index = 0; index < nodes.getLength(); index++) {
        System.out.println("FRIEND:  " + nodes.item(index).getFirstChild().getTextContent());
    }
```


**Update a User's Status Message:**
```
    if (client.users_hasAppPermission(Permission.STATUS_UPDATE)) {
        client.users_setStatus("developing Facebook apps in Java because the new Java client kicks the PHP client's ass!", false);
    }
```


**Send SMS to a User:**
```
    FacebookRestClient client = new FacebookRestClient("apiKey", "secretKey", "sessionId");
    if (client.sms_canSend()) {
        client.sms_send("I can send you text messages now!", null, false);
    }
```


**Publishing a Templatized Feed Entry:**
```
     //using the TemplatizedAction utility class helps keep things sane
    TemplatizedAction action = new TemplatizedAction("{actor} recommends {book}");                      //the user has recommended a book
    
    action.addTitleParam("book", "<a href='http://www.amazon.com/Hamlet/dp/0140714545/'>Hamlet</a>");   //specify the specific book
    action.setBodyTemplate("{actor} is using BooksApp!");                                               //set a body template (optional)
    action.setBodyGeneral("100 other people recommend this book!");                                     //set general body content (optional)
    action.addPicture("http://code.google.com/hosting/images/code_sm.png", "http://www.google.com");    //add up to 4 pictures (optional)
    action.addPicture("http://code.google.com/hosting/images/code_sm.png", "http://www.google.com");
    action.addPicture("http://code.google.com/hosting/images/code_sm.png", "http://www.google.com");
    action.addPicture("http://code.google.com/hosting/images/code_sm.png", "http://www.google.com");
    
    client.feed_PublishTemplatizedAction(action);                                                       //publish to feed
```


**Playing With User Preferences:**
```
     FacebookRestClient client = new FacebookRestClient("apiKey", "secretKey", "sessionId");
    Map<Integer, String> prefs = client.data_getUserPreferences();
    
    //show any preferences that are currently set for the user, all at once
    System.out.println("Preferences already set:");
    for (Integer key : prefs.keySet()) {
        System.out.println("\tkey " + key + " = " + prefs.get(key));
    }
        
    //set the values of some preferences, one at a time    
    client.data_setUserPreference(1, "test1");
    client.data_setUserPreference(2, "test2");
    client.data_setUserPreference(3, "0");
    
    //retrieve some of the set values, one at a time    
    System.out.println("Preference 2 is:  " + client.data_getUserPreference(2));
    System.out.println("Preference 1 is:  " + client.data_getUserPreference(1));
        
    //retrieve all the values at once
    System.out.println("All current preferences:");
    prefs = client.data_getUserPreferences();
    for (Integer key : prefs.keySet()) {
        System.out.println("\tkey " + key + " = " + prefs.get(key));
    }
        
    //set several new preference values at once, preserving any existing values    
    Map<Integer, String> vals = new HashMap<Integer, String>();
    vals.put(4, "test4");
    vals.put(5, "test5");
    vals.put(6, "test6");
    client.data_setUserPreferences(vals, false);
    
    //retrieve all the values at once
    System.out.println("All current preferences:");
    prefs = client.data_getUserPreferences();
    for (Integer key : prefs.keySet()) {
        System.out.println("\tkey " + key + " = " + prefs.get(key));
    }
    
    //set several new preference values at once, *removing* any existing values   
    client.data_setUserPreferences(vals, true);
    
    //retrieve all the values at once (to show that anything not in 'vals' is now gone) 
    System.out.println("All current preferences:");
    prefs = client.data_getUserPreferences();
    for (Integer key : prefs.keySet()) {
        System.out.println("\tkey " + key + " = " + prefs.get(key));
    }
```