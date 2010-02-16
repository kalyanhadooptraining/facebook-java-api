package com.google.code.facebookapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.google.code.facebookapi.schema.DashboardMultiAddNewsResponse;
import com.google.code.facebookapi.schema.DashboardMultiClearNewsResponse;
import com.google.code.facebookapi.schema.IdPair;
import com.google.code.facebookapi.schema.IdToBooleanPair;
import com.google.code.facebookapi.schema.DashboardMultiClearNewsResponse.DashboardMultiClearNewsResponseElt;

public class DashboardAPITest {

	private JUnitProperties properties;
	
	@Before
	public void setUp() {
		properties = new JUnitProperties();
	}
	
	/**
	 * Basic clear operation test.
	 */
	@Test
	public void testMultiClearNews_basic() throws Exception {
		
		// seed data
		Long userId = Long.parseLong( properties.getUID() );
		List<Long> ids = new ArrayList<Long>();
		ids.add( userId );

		DashboardNewsItem newsItem1 = new DashboardNewsItem();
		newsItem1.setMessage( "{*actor*}, this is a test message." );
		newsItem1.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		DashboardNewsItem newsItem2 = new DashboardNewsItem();
		newsItem2.setMessage( "{*actor*}, this is a test message 2." );
		newsItem2.setActionLink( new BundleActionLink( "open", "http://www.google.com/2" ) );

		List<DashboardNewsItem> newsItems = new ArrayList<DashboardNewsItem>();
		newsItems.add(newsItem1);
		newsItems.add(newsItem2);
		
		// submit seed data
		IFacebookRestClient jaxbClient = FacebookSessionTestUtils.getSessionlessValidClient( FacebookJaxbRestClient.class );
		DashboardMultiAddNewsResponse addResponse = (DashboardMultiAddNewsResponse) jaxbClient.dashboard_multiAddNews( ids, newsItems );
		Long newsId = addResponse.getDashboardMultiAddNewsResponseElt().get( 0 ).getValue();
		
		assert newsId > 0;
		
		// build parameters
		Collection<Long> newsIds = new ArrayList<Long>();
		newsIds.add( newsId );
		
		Map<Long, Collection<Long>> parameter = new HashMap<Long,Collection<Long>>();
		parameter.put(userId, newsIds);

		// invoke api
		DashboardMultiClearNewsResponse clearResponse = (DashboardMultiClearNewsResponse) jaxbClient.dashboard_multiClearNews(parameter);
		assert clearResponse != null;

		// validate
		List<DashboardMultiClearNewsResponseElt> responseElts = clearResponse.getDashboardMultiClearNewsResponseElt();
		assert responseElts != null;
		assert responseElts.size() == 1;
		
		DashboardMultiClearNewsResponseElt userRecords = responseElts.get( 0 );
		assert userRecords != null;
		assert userRecords.getKey().equals(userId);
		
		List<IdToBooleanPair> idPairs = userRecords.getDashboardMultiClearNewsResponseEltElt();
		assert idPairs != null;
		assert idPairs.size() == 1;
		assert idPairs.get( 0 ).getKey().equals(newsId);
		assert idPairs.get( 0 ).isValue();
	}
	
	/**
	 * Basic clear operation test without specifying news ids.
	 */
	@Test
	public void testMultiClearNews_basicClearAll() throws Exception {
		
		// seed data
		Long userId = Long.parseLong( properties.getUID() );
		List<Long> ids = new ArrayList<Long>();
		ids.add( userId );

		DashboardNewsItem newsItem1 = new DashboardNewsItem();
		newsItem1.setMessage( "{*actor*}, this is a test message." );
		newsItem1.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		DashboardNewsItem newsItem2 = new DashboardNewsItem();
		newsItem2.setMessage( "{*actor*}, this is a test message 2." );
		newsItem2.setActionLink( new BundleActionLink( "open", "http://www.google.com/2" ) );

		List<DashboardNewsItem> newsItems = new ArrayList<DashboardNewsItem>();
		newsItems.add(newsItem1);
		newsItems.add(newsItem2);
		
		// submit seed data
		IFacebookRestClient jaxbClient = FacebookSessionTestUtils.getSessionlessValidClient( FacebookJaxbRestClient.class );
		DashboardMultiAddNewsResponse addResponse = (DashboardMultiAddNewsResponse) jaxbClient.dashboard_multiAddNews( ids, newsItems );
		Long newsId = addResponse.getDashboardMultiAddNewsResponseElt().get( 0 ).getValue();
		
		assert newsId > 0;
		
		// build parameters
		Collection<Long> parameter = new ArrayList<Long>();
		parameter.add( userId );
		
		// invoke api
		DashboardMultiClearNewsResponse clearResponse = (DashboardMultiClearNewsResponse) jaxbClient.dashboard_multiClearNews(parameter);
		assert clearResponse != null;

		// validate
		List<DashboardMultiClearNewsResponseElt> responseElts = clearResponse.getDashboardMultiClearNewsResponseElt();
		assert responseElts != null;
		assert responseElts.size() == 1;
		
		DashboardMultiClearNewsResponseElt userRecords = responseElts.get( 0 );
		assert userRecords != null;
		assert userRecords.getKey().equals(userId);
		
		List<IdToBooleanPair> idPairs = userRecords.getDashboardMultiClearNewsResponseEltElt();
		assert idPairs != null;
		assert idPairs.size() >= 1;
		assert idPairs.get( 0 ).getKey() != 0;
		assert idPairs.get( 0 ).isValue();
	}
	
	/**
	 * Basic commit operation test.
	 */
	@Test
	public void testMultiAddNews_basic() throws Exception {
		
		// basic
		Long userId = Long.parseLong( properties.getUID() );
		List<Long> ids = new ArrayList<Long>();
		ids.add( userId );

		DashboardNewsItem newsItem = new DashboardNewsItem();
		newsItem.setMessage( "{*actor*}, this is a test message." );
		newsItem.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		DashboardNewsItem newsItem2 = new DashboardNewsItem();
		newsItem2.setMessage( "{*actor*}, this is a test message 2." );
		newsItem2.setActionLink( new BundleActionLink( "open", "http://www.google.com/2" ) );

		List<DashboardNewsItem> newsItems = new ArrayList<DashboardNewsItem>();
		newsItems.add(newsItem);
		newsItems.add(newsItem2);
		
		// validate jaxb response
		IFacebookRestClient jaxbClient = FacebookSessionTestUtils.getSessionlessValidClient( FacebookJaxbRestClient.class );
		jaxbClient.dashboard_multiAddNews( null, null );
		DashboardMultiAddNewsResponse jaxbResponse = (DashboardMultiAddNewsResponse) jaxbClient.dashboard_multiAddNews( ids, newsItems );

		assert jaxbResponse != null;
		assert jaxbResponse.getDashboardMultiAddNewsResponseElt().size() == 1;
		IdPair keyValuePair = jaxbResponse.getDashboardMultiAddNewsResponseElt().get( 0 );
		assert keyValuePair.getKey().equals(userId);
		assert keyValuePair.getValue() > 0;
		
		// validate json response
		IFacebookRestClient jsonClient = FacebookSessionTestUtils.getSessionlessValidClient( FacebookJsonRestClient.class );
		jsonClient.dashboard_multiAddNews( null, null );
		JSONArray jsonResponse = (JSONArray) jsonClient.dashboard_multiAddNews( ids, newsItems );
		
		assert jsonResponse != null;
		assert jsonResponse.length() == 1;
		assert ((JSONObject)jsonResponse.get( 0 )).getLong( userId.toString() ) > 0;
	}

	/**
	 * Basic commit operation test.
	 */
	@Test
	public void testAddGlobalNews_basic() throws Exception {
		IFacebookRestClient client = FacebookSessionTestUtils.getSessionlessValidClient( FacebookXmlRestClient.class );

		// commit basic
		DashboardNewsItem newsItem = new DashboardNewsItem();
		newsItem.setMessage( "{*actor*}, this is a global test message." );
		newsItem.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		List<DashboardNewsItem> newsItems = new ArrayList<DashboardNewsItem>();
		newsItems.add(newsItem);
		
		String imageUrl = "http://www.somesite.com/some.gif";
		
		Long newsId = client.dashboard_addGlobalNews( newsItems, imageUrl );
		assert newsId != null;
	}

	/**
	 * Basic commit operation test.
	 */
	@Test
	public void testPublishActivity_basic() throws Exception {
		IFacebookRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );

		// commit basic
		DashboardActivityItem activityItem = new DashboardActivityItem();
		activityItem.setMessage( "{*actor*} is playing your game!." );
		activityItem.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		String imageUrl = "http://www.somesite.com/some.gif";
		
		Long newsId = client.dashboard_publishActivity( activityItem, imageUrl );
		assert newsId != null;
	}
	
	/**
	 * Basic commit operation test.
	 */
	@Test
	public void testMultiIncrementCount_basic() throws Exception {
		IFacebookRestClient client = FacebookSessionTestUtils.getSessionlessValidClient( FacebookXmlRestClient.class );

		// no exception
		client.dashboard_multiIncrementCount( null );

		// basic
		List<Long> ids = new ArrayList<Long>();
		ids.add( Long.parseLong( properties.getUID() ) );

		Set<Long> successfulIds = client.dashboard_multiIncrementCount( ids );
		assert successfulIds != null;
		assert successfulIds.size() == 1;
	}
	
	/**
	 * Basic commit operation test.
	 */
	@Test
	public void testClearGlobalNews_basic() throws Exception {
		IFacebookRestClient client = FacebookSessionTestUtils.getSessionlessValidClient( FacebookXmlRestClient.class );

		// initialize to nothing
		client.dashboard_clearGlobalNews();

		// add news to clear
		DashboardNewsItem newsItem = new DashboardNewsItem();
		newsItem.setMessage( "{*actor*}, this is a global test message." );
		newsItem.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		List<DashboardNewsItem> newsItems = new ArrayList<DashboardNewsItem>();
		newsItems.add(newsItem);
		
		String imageUrl = "http://www.somesite.com/some.gif";

		Long newsId = client.dashboard_addGlobalNews( newsItems, imageUrl );

		assert newsId != null;
		
		// make sure it clears
		Collection<Long> newsIdsToClear = new ArrayList<Long>();
		newsIdsToClear.add( newsId );
		boolean success = client.dashboard_clearGlobalNews( newsIdsToClear );
		assert success;
	}
}
