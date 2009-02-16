package com.google.code.facebookapi;

/**
 * This class enumerates the various metrics that are available through the admin.getMetrics API call. Typically, you will pass a set containing the metrics you are
 * interested in to the API call.
 * 
 * See http://wiki.developers.facebook.com/index.php/Admin.getMetrics for details.
 */
public enum Metric {

	/** */
	@Deprecated
	DAILY_ACTIVE_USERS("daily_active_users"),
	/** */
	@Deprecated
	UNIQUE_ADDS("unique_adds"),
	/** */
	@Deprecated
	UNIQUE_REMOVES("unique_removes"),

	/** The number of active users. */
	ACTIVE_USERS("active_users"),
	/** Number of API calls made by your app. */
	API_CALLS("api_calls"),
	/** The number of users on whose behalf your application made API calls. */
	UNIQUE_API_CALLS("unique_api_calls"),
	/** Number of canvas page views. */
	CANVAS_PAGE_VIEWS("canvas_page_views"),
	/** The number of users who viewed your application's canvas page. */
	UNIQUE_CANVAS_PAGE_VIEWS("unique_canvas_page_views"),
	/** The average time to fulfill an HTTP request to your application's canvas page. */
	CANVAS_HTTP_REQUEST_TIME_AVG("canvas_http_request_time_avg"),
	/** The average time to render FBML on your application's canvas page. */
	CANVAS_FBML_RENDER_TIME_AVG("canvas_fbml_render_time_avg"),
	/** The number of users who blocked your application. 1-day only. */
	UNIQUE_BLOCKS("unique_blocks"),
	/** The number of users who unblocked your application. 1-day only. */
	UNIQUE_UNBLOCKS("unique_unblocks"),

	/** The number of canvas page views that timed out. */
	REQUEST_TIMEOUT("canvas_page_views_http_code_0"),
	/** The number of canvas page views that returned HTTP code 100 -- Continue. */
	REQUEST_CONTINUE("canvas_page_views_http_code_100"),
	/** The number of canvas page views that returned HTTP code 200 -- OK. */
	REQUEST_OK("canvas_page_views_http_code_200"),
	/** The number of canvas page views that returned HTTP code 200 -- OK -- and no data. */
	REQUEST_OK_NO_DATA("canvas_page_views_http_code_200ND"),
	/** The number of canvas page views that returned HTTP code 301 -- Moved Permanently. */
	REQUEST_ERROR_301("canvas_page_views_http_code_301"),
	/** The number of canvas page views that returned HTTP code 302 -- Found. */
	REQUEST_ERROR_302("canvas_page_views_http_code_302"),
	/** The number of canvas page views that returned HTTP code 303 -- See Other. */
	REQUEST_ERROR_303("canvas_page_views_http_code_303"),
	/** The number of canvas page views that returned HTTP code 400 -- Bad Request. */
	REQUEST_ERROR_400("canvas_page_views_http_code_400"),
	/** The number of canvas page views that returned HTTP code 401 -- Unauthorized. */
	REQUEST_ERROR_401("canvas_page_views_http_code_401"),
	/** The number of canvas page views that returned HTTP code 403 -- Forbidden. */
	REQUEST_ERROR_403("canvas_page_views_http_code_403"),
	/** The number of canvas page views that returned HTTP code 404 -- Not Found. */
	REQUEST_ERROR_404("canvas_page_views_http_code_404"),
	/** The number of canvas page views that returned HTTP code 405 -- Method Not Allowed. */
	REQUEST_ERROR_405("canvas_page_views_http_code_405"),
	/** The number of canvas page views that returned HTTP code 413 -- Request Entity Too Large. */
	REQUEST_ERROR_413("canvas_page_views_http_code_413"),
	/** The number of canvas page views that returned HTTP code 422 -- Unprocessable Entity. */
	REQUEST_ERROR_422("canvas_page_views_http_code_422"),
	/** The number of canvas page views that returned HTTP code 500 -- Internal Server Error. */
	REQUEST_ERROR_500("canvas_page_views_http_code_500"),
	/** The number of canvas page views that returned HTTP code 502 -- Bad Gateway. */
	REQUEST_ERROR_502("canvas_page_views_http_code_502"),
	/** The number of canvas page views that returned HTTP code 503 -- Service Unavailable. */
	REQUEST_ERROR_503("canvas_page_views_http_code_503"),
	/** The number of canvas page views that returned HTTP code 505 -- HTTP Version Not Supported. */
	REQUEST_ERROR_505("canvas_page_views_http_code_505");


	/**
	 * Use in Admin.getMetrics calls to specify a daily time-period.
	 */
	public static final long PERIOD_DAY = 86400l;
	/**
	 * Use in Admin.getMetrics calls to specify a weekly time-period.
	 */
	public static final long PERIOD_WEEK = 604800l;
	/**
	 * Use in Admin.getMetrics calls to specify a monthly time-period.
	 */
	public static final long PERIOD_MONTH = 2592000l;

	private String name;

	private Metric( String name ) {
		this.name = name;
	}

	/**
	 * Get the name by which Facebook refers to this metric.
	 * 
	 * @return the Facebook-supplied name of this metric.
	 */
	public String getName() {
		return this.name;
	}

}
