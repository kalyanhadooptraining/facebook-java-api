package com.google.code.facebookapi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

public class Issue208StreamAPITest {

	@Test
	public void testStreamGet() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		FacebookSessionTestUtils.pauseForStreamRate();
		JSONObject result = (JSONObject) client.stream_get( null, null, null, null, null, null, null );

		Assert.assertNotNull( result );
		Assert.assertFalse( StringUtils.isEmpty( result.toString() ) );
	}

	@Test
	public void testStreamGetLimit() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		FacebookSessionTestUtils.pauseForStreamRate();
		JSONObject result = (JSONObject) client.stream_get( null, null, null, null, 1, null, null );

		Assert.assertNotNull( result );
		Assert.assertFalse( StringUtils.isEmpty( result.toString() ) );
	}

	@Test
	public void testStreamGetDateRange() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		Date start = DateUtils.addDays( new Date(), -2 );
		Date end = DateUtils.addDays( new Date(), -1 );

		FacebookSessionTestUtils.pauseForStreamRate();
		JSONObject result = (JSONObject) client.stream_get( null, null, start, end, null, null, null );

		Assert.assertNotNull( result );
		Assert.assertFalse( StringUtils.isEmpty( result.toString() ) );
	}

	@Test
	public void testStreamGetMetadata() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		List<String> metadata = new ArrayList<String>( 1 );
		metadata.add( "photo_tags" );

		FacebookSessionTestUtils.pauseForStreamRate();
		JSONObject result = (JSONObject) client.stream_get( null, null, null, null, null, null, metadata );

		Assert.assertNotNull( result );
		Assert.assertFalse( StringUtils.isEmpty( result.toString() ) );
	}

	@Test
	public void testStreamGetSourceIds() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		List<Long> sourceIds = new ArrayList<Long>( 1 );
		sourceIds.add( client.users_getLoggedInUser() );

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_get( null, sourceIds, null, null, null, null, null );

		Assert.assertNotNull( result );
		Assert.assertFalse( StringUtils.isEmpty( result.toString() ) );
	}

	@Test
	public void testStreamPublishAndRemove() throws Exception {
		String postId = streamPublish();
		streamRemove( postId );
	}

	/** Used by various unit tests to create stream item. */
	private String streamPublish() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		String message = "Facebook stream publish test.";

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_publish( message, null, null, null, null );

		Assert.assertNotNull( result );

		return result.toString();
	}

	/** Used by various unit tests to remove stream item. */
	private void streamRemove( final String postId ) throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_remove( postId, null );

		Assert.assertNotNull( result );
		Assert.assertTrue( Boolean.valueOf( result.toString() ) );
	}

	@Test
	public void testStreamComments() throws Exception {
		String postId = streamPublish();
		String commentId = streamAddComment( postId );
		streamGetComments( postId );
		streamRemoveComment( commentId );
		streamRemove( postId );
	}

	private String streamAddComment( final String postId ) throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		String comment = "Unit test comment.";
		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_addComment( postId, comment, null );

		Assert.assertNotNull( result );

		return result.toString();
	}

	private void streamGetComments( final String postId ) throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_getComments( postId );

		Assert.assertNotNull( result );
	}

	private void streamRemoveComment( final String postId ) throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_removeComment( postId, null );

		Assert.assertNotNull( result );
	}

	@Test
	public void testStreamLikes() throws Exception {
		String postId = streamPublish();
		streamAddLike( postId );
		streamRemoveLike( postId );
		streamRemove( postId );
	}

	private String streamAddLike( final String postId ) throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		Object result = client.stream_addLike( postId, null );

		Assert.assertNotNull( result );
		Assert.assertTrue( Boolean.valueOf( result.toString() ) );

		return result.toString();
	}

	private void streamRemoveLike( final String postId ) throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_removeLike( postId, null );

		Assert.assertNotNull( result );
		Assert.assertTrue( Boolean.valueOf( result.toString() ) );
	}

	@Test
	public void testStreamGetFilters() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		List<Long> sourceIds = new ArrayList<Long>( 1 );
		sourceIds.add( client.users_getLoggedInUser() );

		FacebookSessionTestUtils.pauseForStreamRate();
		Object result = client.stream_getFilters( null );

		Assert.assertNotNull( result );
	}

}
