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

import org.json.JSONObject;

/**
 * A utility class for creating and verifying marketplace listings.
 * 
 * For details visit http://wiki.developers.facebook.com/index.php/Marketplace_Listing_Attributes
 */
public class MarketListing {
    /**
     * Specifies a condition of 'any'
     */
    public static final String CONDITION_ANY = "ANY";
    /**
     * Specifies a condition of 'new'
     */
    public static final String CONDITION_NEW = "NEW";
    /**
     * Specified a condition of 'used'
     */
    public static final String CONDITION_USED = "USED";
    
    private JSONObject attribs;
    
    private MarketListing() {
        //empty constructor not allowed
    }
    
    /**
     * Constructor. 
     * 
     * @param title the title of the listing, always required.
     * @param description the listing description, always required.
     * @param category the listing category, always required.
     * @param subcategory the listing subcategory, always required.
     * 
     */
    public MarketListing(String title, String description, MarketListingCategory category, MarketListingSubcategory subcategory) {
        this.attribs = new JSONObject();
        this.setAttribute("title", title);
        this.setAttribute("description", description);
        this.setAttribute("category", category.getName());
        this.setAttribute("subcategory", subcategory.getName());
    }
    
    //package-level access intentional
    MarketListing(String json) {
        try {
            this.attribs = new JSONObject(json);
        }
        catch (Exception e) {
            this.attribs = new JSONObject();
        }
    }
    
    /**
     * Set an attribute for this listing.  Attributes are used to specify optional listing information (for 
     * example, 'price', 'isbn', 'condition', etc.).  The specific attributes required by Facebook vary 
     * depending upon the category of listing being posted.  For more details, visit:
     * 
     * http://wiki.developers.facebook.com/index.php/Marketplace_Listing_Attributes
     * 
     * @param name the name of the attribute to set
     * @param value the value to set
     */
    public void setAttribute(String name, String value) {
        try {
            attribs.put(name, value);
        }
        catch (Exception e) {
            System.out.println("Exception when setting listing attribute!");
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieve the value of the specified attribute.
     * 
     * @param name the name of the attribute to lookup.
     * 
     * @return the value of the specified attribute, or null if it is not set.
     */
    public String getAttribute(String name) {
        String result = null;
        try {
            result = (String)attribs.get(name);
        }
        catch (Exception ignored) {
            //do nothing
        }
        return result;
    }
    
    //package-level access intentional
    String getAttribs() {
        return attribs.toString();
    }
    
    //package-level access intentional
    boolean verify() throws FacebookException {
        if (!checkString("title")) {
            throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'title' attribute may not be null or empty!");
        }
        if (!checkString("description")) {
            throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'description' attribute may not be null or empty!");        
        }
        if (!checkString("category")) {
            throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'category' attribute may not be null or empty!");
        }
        if (!checkString("subcategory")) {
            throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'subcategory' attribute may not be null or empty!");
        }
        //XXX:  uncomment to force strict validation (requires all attributes mentioned in the Facebook docs)
        /*
        String category = this.getAttribute("category");
        String subcat = this.getAttribute("subcategory");
        if (category.equals(MarketListingCategory.FORSALE.getName())) {
            if (!checkString("price")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'price' attribute is required when selling an item!");
            }
            if ((subcat.equals(MarketListingSubcategory.ELECTRONICS)) || (subcat.equals(MarketListingSubcategory.FURNITURE)) 
                    || (subcat.equals(MarketListingSubcategory.AUTOS)) || (subcat.equals(MarketListingSubcategory.BOOKS))) {
                if (!checkString("condition")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'condition' attribute is required whenever selling books, electronics, cars, or furniture!");
                }
            }
            if (subcat.equals(MarketListingSubcategory.BOOKS)) {
                if ((!checkString("isbn")) || (this.getAttribute("isbn").length() != 13)) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'isbn' attribute is required when selling a book, and it must be exactly 13 digits long!");
                }
                
            }
        }
        if ((category.equals(MarketListingCategory.HOUSING)) || (category.equals(MarketListingCategory.HOUSING_WANTED))) {
            //num_beds, num_baths, dogs, cats, smoking, square_footage, street, crossstreet, postal
            if (! checkString("num_beds")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'num_beds' attribute is required for all housing listings!");
            }
            if (! checkString("num_baths")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'num_baths' attribute is required for all housing listings!");
            }
            if (! checkString("dogs")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'dogs' attribute is required for all housing listings!");
            }
            if (! checkString("cats")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'cats' attribute is required for all housing listings!");
            }
            if (! checkString("smoking")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'smoking' attribute is required for all housing listings!");
            }
            if (! checkString("square_footage")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'square_footage' attribute is required for all housing listings!");
            }
            if (! checkString("street")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'street' attribute is required for all housing listings!");
            }
            if (! checkString("crossstreet")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'crossstreet' attribute is required for all housing listings!");
            }
            if (! checkString("postal")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'postal' attribute is required for all housing listings!");
            }
            if ((subcat.equals(MarketListingSubcategory.SUBLETS)) || (subcat.equals(MarketListingSubcategory.RENTALS))) {
                if (!checkString("rent")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'rent' attribute is required for all rentals and sublets!");
                }
            }
            if (subcat.equals(MarketListingSubcategory.REAL_ESTATE)) {
                if (!checkString("price")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'price' attribute is required for all real-estate listings!");
                }
            }
        }
        if (category.equals(MarketListingCategory.JOBS)) {
            //pay, full, intern, summer, nonprofit, pay_type
            if (!checkString("pay")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'pay' attribute is required for all job postings!");
            }
            if (!checkString("full")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'full' attribute is required for all job postings!");
            }
            if (!checkString("intern")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'intern' attribute is required for all job postings!");
            }
            if (!checkString("summer")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'summer' attribute is required for all job postings!");
            }
            if (!checkString("nonprofit")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'nonprofit' attribute is required for all job postings!");
            }
            if (!checkString("pay_type")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'pay_type' attribute is required for all job postings!");
            }
        }
        if (category.equals(MarketListingCategory.FORSALE_WANTED)) {
            if ((subcat.equals(MarketListingSubcategory.BOOKS_WANTED)) || (subcat.equals(MarketListingSubcategory.FURNITURE_WANTED)) 
                || (subcat.equals(MarketListingSubcategory.AUTOS_WANTED)) || (subcat.equals(MarketListingSubcategory.ELECTRONICS_WANTED))) {
                if (!checkString("condition")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'condition' attribute is required whenever seeking books, burniture, electronics, or cars!");
                }
                if (subcat.equals(MarketListingSubcategory.ELECTRONICS_WANTED)) {
                    if (!checkString("isbn")) {
                        throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'isbn' attribute is required when requesting a book!");
                    }
                }
            }
        }
        */
        return true;
    }
    
    private boolean checkString(String attrName) {
        String input = this.getAttribute(attrName);
        if ((input == null) || ("".equals(input))) {
            return false;
        }
        return true;
    }
}
