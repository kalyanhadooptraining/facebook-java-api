/*
 * Copyright 2007, BigTribe Corporation. All rights reserved.
 *
 * This software is an unpublished work subject to a confidentiality agreement
 * and protected by copyright and trade secret law.  Unauthorized copying,
 * redistribution or other use of this work is prohibited.  All copies must
 * retain this copyright notice.  Any use or exploitation of this work without
 * authorization could subject the perpetrator to criminal and civil liability.
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
     * Category to specify for a for-sale listing
     */
    public static final String CATEGORY_FORSALE = "FORSALE";
    /**
     * Category to specify for a housing listing
     */
    public static final String CATEGORY_HOUSING = "HOUSING";
    /**
     * Category to specify for a job posting
     */
    public static final String CATEGORY_JOBS = "JOBS";
    /**
     * Category for any listing that doesn't fit in any of the other categories
     */
    public static final String CATEGORY_OTHER = "OTHER";
    /**
     * Category for a listing advertising free goods/services
     */
    public static final String CATEGORY_FREE = "FREE";
    /**
     * Category for a listing seeking items for sale
     */
    public static final String CATEGORY_FORSALE_WANTED = "FORSALE_WANTED";
    /**
     * Category for a listing seeking housing
     */
    public static final String CATEGORY_HOUSING_WANTED = "HOUSING_WANTED";
    /**
     * Category for a listing seeking employment
     */
    public static final String CATEGORY_JOBS_WANTED = "JOBS_WANTED";
    /**
     * Category for a listing seeking anything that doesn't fit in any other category
     */
    public static final String CATEGORY_OTHER_WANTED = "OTHER_WANTED";
    
    /**
     * Subcategory for listings involving books
     */
    public static final String SUBCATEGORY_BOOKS = "BOOKS";
    /**
     * Subcategory for listings involving furniture
     */
    public static final String SUBCATEGORY_FURNITURE = "FURNITURE";
    /**
     * Subcategory for listings involving event tickets
     */
    public static final String SUBCATEGORY_TICKETS = "TICKETS";
    /**
     * Subcategory for listings involving electronics
     */
    public static final String SUBCATEGORY_ELECTRONICS = "ELECTRONICS";
    /**
     * Subcategory for listings involving cars
     */
    public static final String SUBCATEGORY_AUTOS = "AUTOS";
    /**
     * Subcategory for listings involving things not specified by any of the other subcategories
     */
    public static final String SUBCATEGORY_GENERAL = "GENERAL";
    /**
     * Subcategory for listings involving rentals
     */
    public static final String SUBCATEGORY_RENTALS = "RENTALS";
    /**
     * Subcategory for listings involving sublets
     */
    public static final String SUBCATEGORY_SUBLETS = "SUBLETS";
    /**
     * Subcategory for listings involving real-estate
     */
    public static final String SUBCATEGORY_REAL_ESTATE = "REALESTATE";
    /**
     * Subcategory for listings seeking books
     */
    public static final String SUBCATEGORY_BOOKS_WANTED = "BOOKS_WANTED";
    /**
     * Subcategory for listings seeking furniture
     */
    public static final String SUBCATEGORY_FURNITURE_WANTED = "FURNITURE_WANTED";
    /**
     * Subcategory for listings seeking electronics
     */
    public static final String SUBCATEGORY_ELECTRONICS_WANTED = "ELECTRONICS_WANTED";
    /**
     * Subcategory for listings seeking cars
     */
    public static final String SUBCATEGORY_AUTOS_WANTED = "AUTOS_WANTED";
    /**
     * Subcategory for listings seeking things not specified by any of the other subcategories
     */
    public static final String SUBCATEGORY_GENERAL_WANTED = "GENERAL_WANTED";
    /**
     * Subcategory for listings seeking sublets
     */
    public static final String SUBCATEGORY_SUBLETS_WANTED = "SUBLETS_WANTED";
    /**
     * Subcategory for listings seeking real-estate
     */
    public static final String SUBCATEGORY_REAL_ESTATE_WANTED = "REALESTATE_WANTED";
    
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
    public MarketListing(String title, String description, String category, String subcategory) {
        this.attribs = new JSONObject();
        this.setAttribute("title", title);
        this.setAttribute("description", description);
        this.setAttribute("category", category);
        this.setAttribute("subcategory", subcategory);
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
        //XXX:  uncomment to force strict validation (requires all attributes mentioned in the docs)
        /* 
        String category = this.getAttribute("category");
        String subcat = this.getAttribute("subcategory");
        if (category.equals(CATEGORY_FORSALE)) {
            if (!checkString("price")) {
                throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'price' attribute is required when selling an item!");
            }
            if ((subcat.equals(SUBCATEGORY_ELECTRONICS)) || (subcat.equals(SUBCATEGORY_FURNITURE)) 
                    || (subcat.equals(SUBCATEGORY_AUTOS)) || (subcat.equals(SUBCATEGORY_BOOKS))) {
                if (!checkString("condition")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'condition' attribute is required whenever selling books, electronics, cars, or furniture!");
                }
            }
            if (subcat.equals(SUBCATEGORY_BOOKS)) {
                if ((!checkString("isbn")) || (this.getAttribute("isbn").length() != 13)) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'isbn' attribute is required when selling a book, and it must be exactly 13 digits long!");
                }
                
            }
        }
        if ((category.equals(CATEGORY_HOUSING)) || (category.equals(CATEGORY_HOUSING_WANTED))) {
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
            if ((subcat.equals(SUBCATEGORY_SUBLETS)) || (subcat.equals(SUBCATEGORY_RENTALS))) {
                if (!checkString("rent")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'rent' attribute is required for all rentals and sublets!");
                }
            }
            if (subcat.equals(SUBCATEGORY_REAL_ESTATE)) {
                if (!checkString("price")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'price' attribute is required for all real-estate listings!");
                }
            }
        }
        if (category.equals(CATEGORY_JOBS)) {
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
        if (category.equals(CATEGORY_FORSALE_WANTED)) {
            if ((subcat.equals(SUBCATEGORY_BOOKS_WANTED)) || (subcat.equals(SUBCATEGORY_FURNITURE_WANTED)) 
                || (subcat.equals(SUBCATEGORY_AUTOS_WANTED)) || (subcat.equals(SUBCATEGORY_ELECTRONICS_WANTED))) {
                if (!checkString("condition")) {
                    throw new FacebookException(ErrorCode.GEN_INVALID_PARAMETER, "The 'condition' attribute is required whenever seeking books, burniture, electronics, or cars!");
                }
                if (subcat.equals(SUBCATEGORY_ELECTRONICS_WANTED)) {
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
