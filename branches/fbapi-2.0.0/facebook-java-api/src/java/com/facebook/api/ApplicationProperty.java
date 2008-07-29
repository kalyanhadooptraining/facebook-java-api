package com.facebook.api;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration for managing the different application properties that Facebook supports. These
 * properties can be managed using the admin.* API calls. For more details, see:
 * http://wiki.developers.facebook.com/index.php/ApplicationProperties
 * 
 * @author aroth
 */
public enum ApplicationProperty {
    /**
     * The name of your app
     */
    APPLICATION_NAME("application_name", "string"),
    /**
     * Your app's callback URL (100 char max)
     */
    CALLBACK_URL("callback_url", "string"),
    /**
     * The URL where a user gets redirected after installing your application. The post-install URL
     * cannot be longer than 100 characters
     */
    POST_INSTALL_URL("post_install_url", "string"),
    /**
     * Facebook won't say.
     */
    EDIT_URL("edit_url", "string"),
    /**
     * Facebook won't say.
     */
    DASHBOARD_URL("dashboard_url", "string"),
    /**
     * The URL where a user gets redirected after removing your application.
     */
    UNINSTALL_URL("uninstall_url", "string"),
    /**
     * List of IP addresses allowed to make requests to Facebook using your API key.
     */
    IP_LIST("ip_list", "string"),
    /**
     * The email address associated with the application; the email address Facebook uses to contact
     * you about your application. (default value is your Facebook email address.)
     */
    EMAIL("email", "string"),
    /**
     * Description of your app.
     */
    DESCRIPTION("description", "string"),
    /**
     * Specifies that your app renders in an iframe
     */
    USE_IFRAME("use_iframe", "bool"),
    /**
     * Specifies that your app is a desktop app
     */
    DESKTOP("desktop", "bool"),
    /**
     * Specifies that your app can support mobile devices
     */
    IS_MOBILE("is_mobile", "bool"),
    /**
     * Default FBML markup to show on the user's profile page
     */
    DEFAULT_FBML("default_fbml", "string"),
    /**
     * Default column on the user's profile page to show the app in
     */
    DEFAULT_COLUMN("default_column", "bool"),
    /**
     * For applications that can create attachments, this is the URL where you store the
     * attachment's content.
     */
    MESSAGE_URL("message_url", "string"),
    /**
     * For applications that can create attachments, this is the label for the action that creates
     * the attachment. It cannot be more than 20 characters.
     */
    MESSAGE_ACTION("message_action", "string"),
    /**
     * This is the URL to your application's About page. About pages are now Facebook Pages.
     */
    ABOUT_URL("about_url", "string"),
    /**
     * Indicates whether you want to disable (1) or enable (0) News Feed and Mini-Feed stories when
     * a user installs your application. (default value is 1)
     */
    PRIVATE_INSTALL("private_install", "bool"),
    /**
     * Indicates whether a user can (1) or cannot (0) install your application. (default value is 1)
     */
    INSTALLABLE("installable", "bool"),
    /**
     * The URL to your application's privacy terms.
     */
    PRIVACY_URL("privacy_url", "string"),
    /**
     * The URL to your application's help page
     */
    HELP_URL("help_url", "string"),
    /**
     * Facebook won't say.
     */
    SEE_ALL_URL("see_all_url", "string"),
    /**
     * The URL to your application's Terms of Service.
     */
    TOS_URL("tos_url", "string"),
    /**
     * Indicates whether developer mode is enabled (1) or disabled (0). Only developers can install
     * applications in developer mode. (default value is 1)
     */
    DEV_MODE("dev_mode", "bool"),
    /**
     * "A preloaded FQL query".
     */
    PRELOAD_FQL("preload_fql", "string"),
    /**
     * canvas_name.
     */
    CANVAS_NAME("canvas_name","string"),
    /**
     * icon_url.
     */
    ICON_URL("icon_url", "string"),
    /**
     * logo_url
     */
    LOGO_URL("logo_url", "string");
    /**
     * A map of property names to their associated ApplicationProperty value
     */
    protected static final Map<String,ApplicationProperty> PROP_TABLE;
    static {
        PROP_TABLE = new HashMap<String,ApplicationProperty>();
        for (ApplicationProperty prop: ApplicationProperty.values()) {
            PROP_TABLE.put(prop.getName(), prop);
        }
    }
    private String name;
    private String type;

    private ApplicationProperty(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Gets the name by which Facebook refers to this property. The name is what is sent in API
     * calls and other requests to Facebook to specify the desired property.
     * 
     * @return the Facebook name given to this property.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type which Facebook assigns to this property. The returned value will be "string"
     * for string-typed properties, and "bool" for boolean typed properties.
     * 
     * @return the type Facebook gives to this property.
     */
    public String getType() {
        return type;
    }

    /**
     * Lookup an ApplicationProperty value by its name.
     * 
     * @param propName the name to lookup
     * @return the ApplicationProperty value that corresponds to the specified name, or null if the
     *         name cannot be found/is not valid.
     */
    public static ApplicationProperty getPropertyForString(String propName) {
        return PROP_TABLE.get(propName);
    }

    public static ApplicationProperty getProperty(String name) {
        return getPropertyForString(name);
    }

    public String propertyName() {
        return this.getName();
    }

    public String toString() {
        return this.getName();
    }

    public boolean isBooleanProperty() {
        return "bool".equals(this.type);
    }

    public boolean isStringProperty() {
        return "string".equals(this.type);
    }

    /**
     * Returns true if this field has a particular name.
     */
    public boolean isName(String name) {
        return toString().equals(name);
    }
}
