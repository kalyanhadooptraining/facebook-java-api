/*
 * Copyright 2007, BigTribe Corporation. All rights reserved.
 *
 * This software is an unpublished work subject to a confidentiality agreement
 * and protected by copyright and trade secret law.  Unauthorized copying,
 * redistribution or other use of this work is prohibited.  All copies must
 * retain this copyright notice.  Any use or exploitation of this work without
 * authorization could subject the perpetrator to criminal and civil liability.
 * 
 * Redistribution and use in source and binary forms, with or without        
 * modification, are permitted provided that the following conditions        
 * are met:                                                                  
 *                                                                           
 * 1. Redistributions of source code must retain the above copyright         
 *    notice, this list of conditions and the following disclaimer.          
 * 2. Redistributions in binary form must reproduce the above copyright      
 *    notice, this list of conditions and the following disclaimer in the    
 *    documentation and/or other materials provided with the distribution.   
 *
 * The information in this software is subject to change without notice
 * and should not be construed as a commitment by BigTribe Corporation.
 *
 * The above copyright notice does not indicate actual or intended publication
 * of this source code.
 *
 * $Id: bigtribetemplates.xml 5524 2006-04-06 09:40:52 -0700 (Thu, 06 Apr 2006) greening $
 */
package com.facebook.api;


/**
 * Enum for managing the different permission-types used by Facebook.  These 
 * are opt-in permissions that the user must explicitly grant, and can only 
 * be requested one at a time.  To request that a user grant you a permission, 
 * direct them to a URL of the form:
 * 
 * http://www.facebook.com/authorize.php?api_key=[YOUR_API_KEY]&v=1.0&ext_perm=[PERMISSION NAME]
 * 
 * You can query to see if the user has granted your application a given permission using the 
 * 'users.hasAppPermission' API call.
 */
public enum Permission {
    /**
     * Permission to send SMS messages to a user
     */
    SMS_SEND("sms"),
    /**
     * Permission to update a user's status message
     */
    STATUS_UPDATE("status_update"),
    /**
     * Permission to create marketplac elistings for the user
     */
    MARKETPLACE_CREATE("create_listing"),
    /**
     * Enahnced photo-uploading permissions
     */
    PHOTO_UPLOAD("photo_upload");
    
    /**
     * The unchanging part of the URL to use when authorizing permissions.
     */
    public static final String PERM_AUTHORIZE_ADDR = "http://www.facebook.com/authorize.php";
    
    private String name;
    
    private Permission(String name) {
        this.name = name;
    }
    
    /**
     * Gets the name by which Facebook refers to this permission.  The name is what is sent in API calls 
     * and other requests to Facebook to specify the desired premission.
     * 
     * @return the Facebook name given to this permission.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Compute the URL to which to send the user to request the extended permission.
     * 
     * @param apiKey your application's API key.
     * @param permission the permission you want the grant URL for.
     * 
     * @return a String that specifies the URL to direct users to in order to grant this permission to the application.
     */
    public static String authorizationUrl(String apiKey, Permission permission) {
      return authorizationUrl(apiKey, permission.getName());
    }

    private static String authorizationUrl(String apiKey, CharSequence permission) {
      return String.format("%s?api_key=%s&v=1.0&ext_perm=%s", PERM_AUTHORIZE_ADDR, apiKey, permission);
    }
}
