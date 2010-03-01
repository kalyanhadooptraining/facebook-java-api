package com.google.code.facebookapi;

/**
 * Static class for organizing the error codes used by the Facebook API.
 * 
 * Full details are here: http://wiki.developers.facebook.com/index.php/Error_codes
 */
public class ErrorCode {

	public static final int API_EC_UNKNOWN = 1;
	public static final int API_EC_SERVICE = 2;
	public static final int API_EC_METHOD = 3;
	public static final int GAPI_EC_TOO_MANY_CALLS = 4;
	public static final int API_EC_BAD_IP = 5;
	public static final int API_EC_HOST_API = 6;
	public static final int API_EC_HOST_UP = 7;
	public static final int API_EC_SECURE = 8;
	public static final int API_EC_RATE = 9;
	public static final int API_EC_PERMISSION_DENIED = 10;
	public static final int API_EC_DEPRECATED = 11;
	public static final int API_EC_VERSION = 12;
	public static final int API_EC_INTERNAL_FQL_ERROR = 13;
	public static final int API_EC_HOST_PUP = 14;
	public static final int API_EC_SESSION_SECRET_NOT_ALLOWED = 15;
	public static final int API_EC_HOST_READONLY = 16;

	/**
	 * A specified API parameter was invalid, or a required parameter was missing
	 */
	public static final int GEN_INVALID_PARAMETER = 100;
	/**
	 * Session key invalid. This could be because the session key has an incorrect format, or because the user has revoked this session
	 */
	public static final int SESSION_INVALID = 102;
	/**
	 * Specified user not valid
	 */
	public static final int PHOTO_INVALID_USER_ID = 110;
	/**
	 * Specified album not valid
	 */
	public static final int PHOTO_INVALID_ALBUM_ID = 120;
	/**
	 * Specified photo not valid
	 */
	public static final int PHOTO_INVALID_PHOTO_ID = 121;
	/**
	 * Feed priority value not valid
	 */
	public static final int FEED_INVALID_PRIORITY = 130;
	/**
	 * The requested action generated a permissions error
	 */
	public static final int GEN_PERMISSIONS_ERROR = 200;
	/**
	 * Desktop app tried to set FBML for an invalid user
	 */
	public static final int FBML_DESKTOP_FBML_RESTRICTED = 240;
	/**
	 * Updating a user's status requires extended permissions
	 */
	public static final int PERM_REQUIRED_STATUS = 250;
	/**
	 * Editing existing photos requires extended permissions
	 */
	public static final int PERM_REQUIRED_PHOTOS = 260;
	/**
	 * The specified album is full
	 */
	public static final int PHOTOS_ALBUM_FULL = 321;
	/**
	 * The specified photo tag target is invalid
	 */
	public static final int PHOTOS_INVALID_TAG_SUBJECT = 322;
	/**
	 * The specified photo cannot be tagged
	 */
	public static final int PHOTOS_TAG_NOT_ALLOWED = 323;
	/**
	 * The photo file is invalid or missing
	 */
	public static final int PHOTOS_BAD_IMAGE = 324;
	/**
	 * Too many photos are pending
	 */
	public static final int PHOTOS_TOO_MANY_PHOTOS = 325;
	/**
	 * Too many tags are pending
	 */
	public static final int PHOTOS_TOO_MANY_TAGS = 326;
	/**
	 * Failed to set FBML parkup
	 */
	public static final int FBML_MARKUP_NOT_SET = 330;
	/**
	 * Feed publication limit reached
	 */
	public static final int FEED_LIMIT_REACHED = 340;
	/**
	 * Feed action limit reached
	 */
	public static final int FEED_ACTION_LIMIT_REACHED = 341;
	/**
	 * Feed title contains too many links
	 */
	public static final int FEED_TOO_MANY_LINKS = 342;
	/**
	 * Feed title is too long
	 */
	public static final int FEED_TITLE_LENGTH_EXCEEDED = 343;
	/**
	 * Too many fb:userLink tags in title, or like points to an invalid user
	 */
	public static final int FEED_INCORRECT_USERLINK = 344;
	/**
	 * Feed title is blank
	 */
	public static final int FEED_BLANK_TITLE = 345;
	/**
	 * Feed body is too long
	 */
	public static final int FEED_BODY_LENGTH_EXCEEDED = 346;
	/**
	 * Could not find photo to include in feed
	 */
	public static final int FEED_PHOTO_NOT_FOUND = 347;
	/**
	 * Specified photo URL for feed is invalid
	 */
	public static final int FEED_PHOTO_LINK_INVALID = 348;
	/**
	 * Session key specified has passed its expiration time
	 */
	public static final int SESSION_TIMED_OUT = 450;
	/**
	 * Session key specified cannot be used to call this method
	 */
	public static final int SESSION_METHOD_NOT_ALLOWED = 451;
	/**
	 * A session key is required for calling this method
	 */
	public static final int SESSION_REQUIRED = 453;
	/**
	 * A session key must be specified when request is signed with a session secret
	 */
	public static final int SESSION_REQUIRED_FOR_SECRET = 454;
	/**
	 * A session secret is not permitted to be used with this type of session key
	 */
	public static final int SESSION_CANNOT_USE_SESSION_SECRET = 455;
	/**
	 * An unknown error occured when processing FQL
	 */
	public static final int FQL_UNKNOWN_ERROR = 600;
	/**
	 * FQL query fails to parse
	 */
	public static final int FQL_PARSE_ERROR = 601;
	/**
	 * Field referenced in FQL was not found
	 */
	public static final int FQL_FIELD_NOT_FOUND = 602;
	/**
	 * Table referenced in FQL was not found
	 */
	public static final int FQL_TABLE_NOT_FOUND = 603;
	/**
	 * FQL query cannot be indexed
	 */
	public static final int FQL_NOT_INDEXABLE = 604;
	/**
	 * The requested FQL function was not found
	 */
	public static final int FQL_INVALID_FUNCTION = 605;
	/**
	 * The FQL query includes an invalid parameter
	 */
	public static final int FQL_INVALID_PARAMETER = 606;
	/**
	 * Unknown error, please try the request again
	 */
	public static final int GEN_REF_SET_FAILED = 700;

}
