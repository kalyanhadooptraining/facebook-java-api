package com.google.code.facebookapi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;


public class Issue188TemplateBundleShortStory {

	@Test
	@Ignore //Remove this line, we want this test to work!
	public void testTemplateBundleWithShortStory() throws FacebookException, IOException {
		FacebookJaxbRestClient client = FacebookSessionTestUtils.getValidClient( FacebookJaxbRestClient.class );
		List<String> oneLineTemplates = new ArrayList<String>();
		oneLineTemplates.add("{*actor*} is advancing through {*appName*}");

		List<BundleStoryTemplate> shortStoryTemplates = new
		ArrayList<BundleStoryTemplate>();
		BundleStoryTemplate shortStory = new BundleStoryTemplate("{*actor*} is advancing through {*appName*}", "");
		shortStoryTemplates.add(shortStory);

		List<BundleActionLink> links = new ArrayList<BundleActionLink>();
		BundleActionLink link = new BundleActionLink();
		link.setText("Play {*appName*} Now!");
		link.setHref("http://apps.facebook.com/mindgames/play.htm");
		links.add(link);

		Long templateID = client.feed_registerTemplateBundle(oneLineTemplates, shortStoryTemplates, null, links);
		assertNotNull(templateID);
		assertTrue(templateID > 0);
	}
	
}
