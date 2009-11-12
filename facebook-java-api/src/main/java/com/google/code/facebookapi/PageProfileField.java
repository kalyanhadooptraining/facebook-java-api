package com.google.code.facebookapi;

/**
 * Enum describing the profile fields of Facebook Pages.
 * 
 * @see IFacebookRestClient#pages_getInfo
 */
public enum PageProfileField {

	/** The ID of the Page being queried. */
	PAGE_ID("page_id"),

	/** The name of the Page being queried. */
	NAME("name"),

	/** The URL to the small-sized picture for the Page being queried. The image can have a maximum width of 50px and a maximum height of 150px. This URL may be blank. */
	PIC_SMALL("pic_small"),

	/**
	 * The URL to the large-sized profile picture for the Page being queried. The image can have a maximum width of 200px and a maximum height of 600px. This URL may be
	 * blank.
	 */
	PIC_BIG("pic_big"),

	/** The URL to the square profile picture for the Page being queried. The image can have a maximum width and height of 50px. This URL may be blank. */
	PIC_SQUARE("pic_square"),

	/**
	 * The URL to the medium-sized profile picture for the Page being queried. The image can have a maximum width of 100px and a maximum height of 300px. This URL may be
	 * blank.
	 */
	PIC("pic"),

	/**
	 * The URL to the largest-sized profile picture for the Page being queried. The image can have a maximum width of 396px and a maximum height of 1188px. This URL may
	 * be blank.
	 */
	PIC_LARGE("pic_large"),

	/** The profile URL for the Page being queried. If the Page admin specified a username for the Page, page_url contains the username. */
	PAGE_URL("page_url"),

	/** Contains the type of the page. */
	TYPE("type"),

	/** Contains the website of the page. */
	WEBSITE("website"),

	/** Indicates whether a calling application has been added by the Page being queried. */
	HAS_ADDED_APP("has_added_app"),

	/** The date when the subject of the Page being queried was founded. This field may be blank. */
	FOUNDED("founded"),

	/** Summary of the subject of the Page being queried. This field may be blank. */
	COMPANY_OVERVIEW("company_overview"),

	/** The mission statement of the organization that is the subject of the Page being queried. This field may be blank. */
	MISSION("mission"),

	/** The products offered by the company on the Page being queried. This field may be blank. */
	PRODUCTS("products"),

	/** The location of the Page being queried, including the street, city, state, country and zip (or post code). Some of the fields may be blank. */
	LOCATION("location"),

	/**
	 * Parking options available. <br/>
	 * Contains three children: street, lot, and valet. Each field returned is a boolean value (1 or 0) indicating if the Page has the specified parking option.
	 */
	PARKING("parking"),

	/** Public transit details, e.g. "Take Caltrain to Palo Alto station. Walk down University Ave one block." */
	PUBLIC_TRANSIT("public_transit"),

	/**
	 * Contains the operating hours. Each local business will be allowed to specify up to two sets of operating hours per day. Contains the following children: <br/>
	 * mon_1_open, mon_1_close, tue_1_open, tue_1_close, wed_1_open, wed_1_close, thu_1_open, thu_1_close, fri_1_open, fri_1_close, sat_1_open, sat_1_close, sun_1_open,
	 * sun_1_close, </br> mon_2_open, mon_2_close, tue_2_open, tue_2_close, wed_2_open, wed_2_close, thu_2_open, thu_2_close, fri_2_open, fri_2_close, sat_2_open,
	 * sat_2_close, sun_2_open, sun_2_close. </br>
	 * 
	 * Each field is returned with time (in seconds since epoch). For example, 9:00 AM is represented as 406800
	 */
	HOURS("hours"),

	/** Restaurant recommended attire, may be one of Unspecfied, Casual, or Dressy */
	ATTIRE("attire"),

	/**
	 * Payment options accepted. Contains five children: cash_only, visa, amex, master_card, and discover. Notes on the children:
	 * <ul>
	 * <li>Each field returned is a boolean value (1 or 0) indicating if the Page accepts the given payment option.</li>
	 * <li>Note that if <b>cash_only</b> is set to 1, the others would be set to 0.</li>
	 * </ul>
	 */
	PAYMENT_OPTIONS("payment_options"),

	/** The team of people preparing the food at the restaurant at the Page being queried. This field may be blank. */
	CULINARY_TEAM("culinary_team"),

	/** The general manager of the Page being queried. This field may be blank. */
	GENERAL_MANAGER("general_manager"),

	/** */
	PRICE_RANGE("price_range"),

	/** */
	RESTAURANT_SERVICES("restaurant_services"),

	/** */
	RESTAURANT_SPECIALTIES("restaurant_specialties"),

	/** */
	RELEASE_DATE("release_date"),

	/** */
	GENRE("genre"),

	/** */
	STARRING("starring"),

	/** */
	SCREENPLAY_BY("screenplay_by"),

	/** */
	DIRECTED_BY("directed_by"),

	/** */
	PRODUCED_BY("produced_by"),

	/** */
	STUDIO("studio"),

	/** */
	AWARDS("awards"),

	/** */
	PLOT_OUTLINE("plot_outline"),

	/** */
	NETWORK("network"),

	/** */
	SEASON("season"),

	/** */
	SCHEDULE("schedule"),

	/** */
	WRITTEN_BY("written_by"),

	/** */
	BAND_MEMBERS("band_members"),

	/** */
	HOMETOWN("hometown"),

	/** */
	CURRENT_LOCATION("current_location"),

	/** */
	RECORD_LABEL("record_label"),

	/** Boooking agent, may be blank */
	BOOKING_AGENT("booking_agent"),

	/** */
	PRESS_CONTACT("press_contact"),

	/** */
	ARTISTS_WE_LIKE("artists_we_like"),

	/** influences, may be blank */
	INFLUENCES("influences"),

	/** Band interests, may be blank */
	BAND_INTERESTS("band_interests"),

	/** biography field, may be blank. */
	BIO("bio"),

	/** Affiliation field of person or team, may be blank */
	AFFILIATION("affiliation"),

	/** Birthday field, may be blank. In the format mm/dd/yyyy */
	BIRTHDAY("birthday"),

	/** Personal information of public figure, may be blank */
	PERSONAL_INFO("personal_info"),

	/** Personal interests of public figure, may be blank */
	PERSONAL_INTERESTS("personal_interests"),

	/** members of team, may be blank */
	MEMBERS("members"),

	/** when automotive was built, may be blank */
	BUILT("built"),

	/** features of automotive, may be blank */
	FEATURES("features"),

	/** mpg of automotive, may be blank */
	MPG("mpg"),

	/** general info field, may be blank */
	GENERAL_INFO("general_info"),

	/** */
	FAN_COUNT("fan_count"),

	/** */
	USERNAME("username");

	private String fieldName;

	PageProfileField( String name ) {
		this.fieldName = name;
	}

	public String fieldName() {
		return this.fieldName;
	}

	public String toString() {
		return fieldName();
	}

	/**
	 * Returns true if this field has a particular name.
	 */
	public boolean isName( String name ) {
		return toString().equals( name );
	}
}
