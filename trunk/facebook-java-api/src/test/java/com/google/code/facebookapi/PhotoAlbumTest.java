package com.google.code.facebookapi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.code.facebookapi.schema.Album;
import com.google.code.facebookapi.schema.PhotosGetAlbumsResponse;

public class PhotoAlbumTest {

	@Test
	public void test_PhotoAlbumList() throws Exception {
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		xpath.setNamespaceContext( new FacebookNamespaceContext() );

		FacebookXmlRestClient client = FacebookSessionTestUtils.getValidClient( FacebookXmlRestClient.class );
		client.setNamespaceAware( false );
		Long userId = client.users_getLoggedInUser();
		Document doc = client.photos_getAlbums( userId );
		assertNotNull( doc );
		// You'd need //fbapi:album if the FacebookXmlRestClient was set to
		// namespace aware.
		NodeList albums = (NodeList) xpath.evaluate( "//album", doc, XPathConstants.NODESET );
		assertTrue( albums.getLength() > 0 );
		for ( int i = 0; i < albums.getLength(); i++ ) {
			Node albumNode = albums.item( i );
			assertNotSame( "", xpath.evaluate( "aid/text()", albumNode ) );
			assertNotSame( "", xpath.evaluate( "name/text()", albumNode ) );
			String link = xpath.evaluate( "link/text()", albumNode );
			new URL( link );
		}
	}

	@Test
	public void test_PhotoAlbumListJaxb() throws Exception {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );

		Long userId = client.users_getLoggedInUser();
		client.photos_getAlbums( userId );
		PhotosGetAlbumsResponse albumsResponse = (PhotosGetAlbumsResponse) client.getResponsePOJO();
		List<Album> albums = albumsResponse.getAlbum();
		assertTrue( !albums.isEmpty() );
		for ( Album album : albums ) {
			assertNotNull( album.getAid() );
			assertNotNull( album.getName() );
			new URL( album.getLink() );
		}
	}

	@Test
	public void test_PhotoAlbumListJson() throws Exception {
		FacebookJsonRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJsonRestClient.class );

		Long userId = client.users_getLoggedInUser();
		JSONArray albums = (JSONArray) client.photos_getAlbums( userId );
		assertTrue( albums.length() > 0 );
		for ( int i = 0; i < albums.length(); i++ ) {
			JSONObject album = (JSONObject) albums.get( i );
			assertNotNull( album.getString( "aid" ) );
			assertNotNull( album.getString( "name" ) );
			new URL( album.getString( "link" ) );
		}
	}

}
