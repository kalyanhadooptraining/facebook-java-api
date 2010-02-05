package com.google.code.facebookapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class DashboardAPITest {

	private JUnitProperties properties;
	
	@Before
	public void setUp() {
		properties = new JUnitProperties();
	}
	
	/**
	 * Basic commit operation test.
	 */
	@Test
	public void testMultiAddNews_basic() throws Exception {
		IFacebookRestClient client = FacebookSessionTestUtils.getSessionlessValidClient( FacebookXmlRestClient.class );

		// no exception
		client.dashboard_multiAddNews( null, null );

		// basic
		List<Long> ids = new ArrayList<Long>();
		ids.add( Long.parseLong( properties.getUID() ) );

		DashboardNewsItem newsItem = new DashboardNewsItem();
		newsItem.setMessage( "{*actor*}, this is a test message." );
		newsItem.setActionLink( new BundleActionLink( "open", "http://www.google.com" ) );
		
		List<DashboardNewsItem> newsItems = new ArrayList<DashboardNewsItem>();
		newsItems.add(newsItem);
		
		Set<Long> successfulIds = client.dashboard_multiAddNews( ids, newsItems );
		assert successfulIds != null;
		assert successfulIds.size() == 1;
		
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
