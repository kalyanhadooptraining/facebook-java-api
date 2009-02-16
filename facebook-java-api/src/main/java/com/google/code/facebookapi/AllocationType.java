package com.google.code.facebookapi;

/**
 * A listing of all allocation types used by the Admin.getAllocation method call.
 * 
 * @see http://wiki.developers.facebook.com/index.php/Admin.getAllocation
 */
public enum AllocationType {

	/** The number of notifications your application can send on behalf of a user per day. These are user-to-user notifications. */
	NOTIFICATIONS_PER_DAY("notifications_per_day"),
	/** The number of notifications your application can send to a user per week. These are application-to-user notifications. */
	ANNOUNCEMENT_NOTIFICATIONS_PER_WEEK("announcement_notifications_per_week"),
	/** The number of requests your application can send on behalf of a user per day. */
	REQUESTS_PER_DAY("requests_per_day"),
	/** The number of email messages your application can send to a user per day. */
	EMAILS_PER_DAY("emails_per_day"),
	/** The location of the disable message within emails sent by your application. '1' is the bottom of the message and '2' is the top of the message. */
	EMAIL_DISABLE_MESSAGE_LOCATION("email_disable_message_location");

	private String name;

	private AllocationType( String name ) {
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
