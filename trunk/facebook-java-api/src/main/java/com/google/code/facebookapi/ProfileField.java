/*
 +---------------------------------------------------------------------------+
 | Facebook Development Platform Java Client                                 |
 +---------------------------------------------------------------------------+
 | Copyright (c) 2007 Facebook, Inc.                                         |
 | All rights reserved.                                                      |
 |                                                                           |
 | Redistribution and use in source and binary forms, with or without        |
 | modification, are permitted provided that the following conditions        |
 | are met:                                                                  |
 |                                                                           |
 | 1. Redistributions of source code must retain the above copyright         |
 |    notice, this list of conditions and the following disclaimer.          |
 | 2. Redistributions in binary form must reproduce the above copyright      |
 |    notice, this list of conditions and the following disclaimer in the    |
 |    documentation and/or other materials provided with the distribution.   |
 |                                                                           |
 | THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR      |
 | IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES |
 | OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.   |
 | IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,          |
 | INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT  |
 | NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, |
 | DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY     |
 | THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT       |
 | (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF  |
 | THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.         |
 +---------------------------------------------------------------------------+
 | For help with this library, contact developers-help@facebook.com          |
 +---------------------------------------------------------------------------+
 */

package com.google.code.facebookapi;

/**
 * Enum for specifying profile-field names. When making API calls, you can generally just pass a set of literal strings specifying field-names if your prefer.
 */
public enum ProfileField {
	/** The user ID of the user being queried. */
	UID("uid"),
	/** The first name of the user being queried. */
	FIRST_NAME("first_name"),
	/** The last name of the user being queried. */
	LAST_NAME("last_name"),
	/** The full name of the user being queried. */
	NAME("name"),
	/**
	 * The URL to the small-sized profile picture for the user being queried. The image can have a maximum width of 50px and a maximum height of 150px. This URL may be
	 * blank.
	 */
	PIC_SMALL("pic_small"),
	/**
	 * The URL to the largest-sized profile picture for the user being queried. The image can have a maximum width of 200px and a maximum height of 600px. This URL may be
	 * blank.
	 */
	PIC_BIG("pic_big"),
	/** The URL to the square profile picture for the user being queried. The image can have a maximum width and height of 50px. This URL may be blank. */
	PIC_SQUARE("pic_square"),
	/**
	 * The URL to the medium-sized profile picture for the user being queried. The image can have a maximum width of 100px and a maximum height of 300px. This URL may be
	 * blank.
	 */
	PIC("pic"),
	/** The networks to which the user being queried belongs. */
	AFFILIATIONS("affiliations"),
	/** The time the profile of the user being queried was most recently updated. If the user's profile has not been updated in the past three days, this value will be 0. */
	PROFILE_UPDATE_TIME("profile_update_time"),
	/** The time zone where the user being queried is located. */
	TIMEZONE("timezone"),
	/** The religion of the user being queried. */
	RELIGION("religion"),
	/** The birthday of the user being queried. */
	BIRTHDAY("birthday"),
	/** The sex of the user being queried. */
	SEX("sex"),
	/** The home town (and state) of the user being queried. */
	HOMETOWN_LOCATION("hometown_location"),
	/** The sex of the person the user being queried wants to meet. */
	MEETING_SEX("meeting_sex"),
	/** The reason the user being queried wants to meet someone. */
	MEETING_FOR("meeting_for"),
	/** The type of relationship for the user being queried. */
	RELATIONSHIP_STATUS("relationship_status"),
	/** The user ID of the partner (for example, husband, wife, boyfriend, girlfriend) of the user being queried. */
	SIGNIFICANT_OTHER_ID("significant_other_id"),
	/** The political views of the user being queried. */
	POLITICAL("political"),
	/** The current location of the user being queried. */
	CURRENT_LOCATION("current_location"),
	/** The activities of the user being queried. */
	ACTIVITIES("activities"),
	/** The interests of the user being queried. */
	INTERESTS("interests"),
	/** Indicates whether the user being queried has logged in to the current application. */
	IS_APP_USER("is_app_user"),
	/** The favorite music of the user being queried. */
	MUSIC("music"),
	/** The favorite television shows of the user being queried. */
	TV("tv"),
	/** The favorite movies of the user being queried. */
	MOVIES("movies"),
	/** The favorite books of the user being queried. */
	BOOKS("books"),
	/** The favorite quotes of the user being queried. */
	QUOTES("quotes"),
	/** More information about the user being queried. */
	ABOUT_ME("about_me"),
	/** Information about high school of the user being queried. */
	HS_INFO("hs_info"),
	/** Post-high school information for the user being queried. */
	EDUCATION_HISTORY("education_history"),
	/** The work history of the user being queried. */
	WORK_HISTORY("work_history"),
	/** The number of notes from the user being queried. */
	NOTES_COUNT("notes_count"),
	/** The number of wall posts for the user being queried. */
	WALL_COUNT("wall_count"),
	/** The current status of the user being queried. */
	STATUS("status"),
	/** [Deprecated] This value is now equivalent to is_app_user. */
	@Deprecated
	HAS_ADDED_APP("has_added_app"),
	/**
	 * The user's Facebook Chat status. Returns a string, one of active, idle, offline, or error (when Facebook can't determine presence information on the server side).
	 * The query does not return the user's Facebook Chat status when that information is restricted for privacy reasons.
	 */
	ONLINE_PRESENCE("online_presence"),
	/** The two-letter country code for the user's locale. Codes used are the ISO 3166 alpha 2 code list. */
	LOCALE("locale"),
	/** The proxied wrapper for a user's email address. */
	PROXIED_EMAIL("proxied_email"),
	/** The URL to a user's profile. */
	PROFILE_URL("profile_url"),
	/**
	 * An array containing a set of confirmed email hashes for the user. Emails are registered via the connect.registerUsers API call and are only confirmed when the user
	 * adds your application. The format of each email hash is the crc32 and md5 hashes of the email address combined with an underscore (_).
	 */
	EMAIL_HASHES("email_hashes"),
	/**
	 * The URL to the small-sized profile picture for the user being queried. The image can have a maximum width of 50px and a maximum height of 150px, and is overlaid
	 * with the Facebook favicon. This URL may be blank.
	 */
	PIC_SMALL_WITH_LOGO("pic_small_with_logo"),
	/**
	 * The URL to the largest-sized profile picture for the user being queried. The image can have a maximum width of 200px and a maximum height of 600px, and is overlaid
	 * with the Facebook favicon. This URL may be blank.
	 */
	PIC_BIG_WITH_LOGO("pic_big_with_logo"),
	/**
	 * The URL to the square profile picture for the user being queried. The image can have a maximum width and height of 50px, and is overlaid with the Facebook favicon.
	 * This URL may be blank.
	 */
	PIC_SQUARE_WITH_LOGO("pic_square_with_logo"),
	/**
	 * The URL to the medium-sized profile picture for the user being queried. The image can have a maximum width of 100px and a maximum height of 300px, and is overlaid
	 * with the Facebook favicon. This URL may be blank.
	 */
	PIC_WITH_LOGO("pic_with_logo"),
	/** A comma delimited list of Demographic Restrictions types a user is allowed to access. Currently, alcohol is the only type that can get returned. */
	ALLOWED_RESTRICTIONS("allowed_restrictions"),
	/** Indicates whether or not Facebook has verified the user. */
	VERIFIED("verified")

	;

	private String fieldName;

	ProfileField( String name ) {
		this.fieldName = name;
	}

	/**
	 * @return the name of the field
	 */
	public String fieldName() {
		return this.fieldName;
	}

	public String toString() {
		return fieldName();
	}

	/**
	 * @param name
	 *            the name to check against
	 * 
	 * @return true if this field has the specified name.
	 */
	public boolean isName( String name ) {
		return toString().equals( name );
	}

}
