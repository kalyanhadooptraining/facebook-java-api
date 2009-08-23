package com.google.code.facebookapi;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;

import com.google.code.facebookapi.schema.AdminGetMetricsResponse;
import com.google.code.facebookapi.schema.Metrics;

public class MetricsTest {

	@Test
	public void testNOOP() {
		// empty
	}

	public static void main( String[] args ) throws FacebookException {
		String key = args[0];
		String secret = args[1];
		FacebookJaxbRestClient fb = new FacebookJaxbRestClient( key, secret );

		Calendar cal = Calendar.getInstance( TimeZone.getTimeZone( "PST" ) );
		cal.set( Calendar.HOUR_OF_DAY, 0 );
		cal.set( Calendar.MINUTE, 0 );
		cal.set( Calendar.SECOND, 0 );
		cal.set( Calendar.MILLISECOND, 0 );
		Date d = cal.getTime();

		Set<Metric> metrics = EnumSet.allOf( Metric.class );
		AdminGetMetricsResponse result = fb.admin_getMetrics( metrics, d, d, Metric.PERIOD_DAY );

		Metrics r = result.getMetrics().get( 0 );

		System.out.println( "getApiCalls:" + r.getApiCalls() );
		System.out.println( "getUniqueApiCalls:" + r.getUniqueApiCalls() );
		System.out.println( "getCanvasFbmlRenderTimeAvg:" + r.getCanvasFbmlRenderTimeAvg() );
		System.out.println( "getCanvasHttpRequestTimeAvg:" + r.getCanvasHttpRequestTimeAvg() );
		System.out.println( "getUniqueAdds:" + r.getUniqueAdds() );
		System.out.println( "getUniqueRemoves:" + r.getUniqueRemoves() );
		System.out.println( "getUniqueBlocks:" + r.getUniqueBlocks() );
		System.out.println( "getUniqueUnblocks:" + r.getUniqueUnblocks() );
		long l = r.getEndTime() * 1000L;
		Calendar calendar = Calendar.getInstance( TimeZone.getTimeZone( "PST" ) );
		calendar.setTimeInMillis( l );
		System.out.println( "end time:" + calendar.getTime() );
		System.out.println( "code 0:" + r.getCanvasPageViewsHttpCode0() );
		System.out.println( "code 100:" + r.getCanvasPageViewsHttpCode100() );
		System.out.println( "code 200:" + r.getCanvasPageViewsHttpCode200() );
		System.out.println( "code 200 no data:" + r.getCanvasPageViewsHttpCode200ND() );
		System.out.println( "code 301:" + r.getCanvasPageViewsHttpCode301() );
		System.out.println( "code 302:" + r.getCanvasPageViewsHttpCode302() );
		System.out.println( "code 303:" + r.getCanvasPageViewsHttpCode303() );
		System.out.println( "code 400:" + r.getCanvasPageViewsHttpCode400() );
		System.out.println( "code 401:" + r.getCanvasPageViewsHttpCode401() );
		System.out.println( "code 403:" + r.getCanvasPageViewsHttpCode403() );
		System.out.println( "code 404:" + r.getCanvasPageViewsHttpCode404() );
		System.out.println( "code 405:" + r.getCanvasPageViewsHttpCode405() );
		System.out.println( "code 413:" + r.getCanvasPageViewsHttpCode413() );
		System.out.println( "code 422:" + r.getCanvasPageViewsHttpCode422() );
		System.out.println( "code 500:" + r.getCanvasPageViewsHttpCode500() );
		System.out.println( "code 502:" + r.getCanvasPageViewsHttpCode502() );
		System.out.println( "code 503:" + r.getCanvasPageViewsHttpCode503() );
		System.out.println( "code 505:" + r.getCanvasPageViewsHttpCode505() );
	}

}
