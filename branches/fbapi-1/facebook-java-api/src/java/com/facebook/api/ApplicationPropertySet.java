/*
 +---------------------------------------------------------------------------+
 | Facebook Development Platform Java Client                                 |
 +---------------------------------------------------------------------------+
 | Copyright (c) 2007-2008 Facebook, Inc.                                    |
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
package com.facebook.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

/**
 * This utility represents a set of application properties for use in the facebook.admin_setApplicatoinProperty API call.
 */
public class ApplicationPropertySet {

	private Map<ApplicationProperty,Boolean> _attributesBool = null;
	private Map<ApplicationProperty,CharSequence> _attributesString = null;

	public ApplicationPropertySet() {
		// empty
	}

	/**
	 * Initialize the ApplicationPropertySet mapping from its JSON representation
	 * 
	 * @param jsonString
	 *            JSON representing the application properties
	 * @throws ClassCastException
	 *             if the JSON-encoded mapping doesn't conform to expectations
	 */
	public ApplicationPropertySet( String jsonString ) throws ClassCastException {
		Map<ApplicationProperty,String> mappings = ExtensibleClient.parseProperties( jsonString );
		Set<Map.Entry<ApplicationProperty,String>> entries = mappings.entrySet();
		for ( Map.Entry<ApplicationProperty,String> entry : entries ) {
			ApplicationProperty prop = entry.getKey();
			String value = entry.getValue();
			if ( prop.isBooleanProperty() ) {
				this.setBoolProperty( prop, "1".equals( value ) );
			} else if ( prop.isStringProperty() ) {
				this.setStringProperty( prop, value );
			}
		}
	}

	/**
	 * Sets a boolean property value.
	 * 
	 * @param prop
	 *            the property to set, it should be of boolean type.
	 * @param value
	 *            the value to set.
	 */
	public void setBoolProperty( ApplicationProperty prop, boolean value ) {
		if ( null == prop || !prop.isBooleanProperty() ) {
			throw new IllegalArgumentException( "Boolean property expected" );
		}
		if ( null == this._attributesBool ) {
			this._attributesBool = new HashMap<ApplicationProperty,Boolean>();
		}
		this._attributesBool.put( prop, value );
	}

	/**
	 * Gets a boolean property value.
	 * 
	 * @param prop
	 *            the property to get, it should be of boolean type.
	 * 
	 * @return the value of the property.
	 */
	public Boolean getBoolProperty( ApplicationProperty prop ) {
		if ( null == prop || !prop.isBooleanProperty() ) {
			throw new IllegalArgumentException( "Boolean property expected" );
		}
		return ( null == this._attributesBool ) ? null : this._attributesBool.get( prop );
	}

	/**
	 * Sets a String property value.
	 * 
	 * @param prop
	 *            the property to set, it should be of String type.
	 * @param value
	 *            the value to set.
	 */
	public void setStringProperty( ApplicationProperty prop, CharSequence value ) {
		if ( null == prop || !prop.isStringProperty() ) {
			throw new IllegalArgumentException( "String property expected" );
		}
		if ( null == this._attributesString ) {
			this._attributesString = new HashMap<ApplicationProperty,CharSequence>();
		}
		this._attributesString.put( prop, value );
	}

	/**
	 * Gets a String property value.
	 * 
	 * @param prop
	 *            the property to get, it should be of String type.
	 * 
	 * @return the value of the property.
	 */
	public CharSequence getStringProperty( ApplicationProperty prop ) {
		if ( null == prop || !prop.isStringProperty() ) {
			throw new IllegalArgumentException( "String property expected" );
		}
		return ( null == this._attributesString ) ? null : this._attributesString.get( prop );
	}

	/**
	 * Remove a property from the set.
	 * 
	 * @param prop
	 *            the property to remove.
	 */
	public void removeProperty( ApplicationProperty prop ) {
		if ( prop.isBooleanProperty() ) {
			this._attributesBool.remove( prop );
		} else if ( prop.isStringProperty() ) {
			this._attributesString.remove( prop );
		}
	}

	/**
	 * @return true if this set is empty false otherwise
	 */
	public boolean isEmpty() {
		return ( null == this._attributesString || this._attributesString.isEmpty() ) && ( null == this._attributesBool || this._attributesBool.isEmpty() );
	}

	/**
	 * Return a JSON representation of this property set object
	 * 
	 * @return JSONObject
	 */
	public JSONObject jsonify() {
		JSONObject ret = new JSONObject();
		if ( null != this._attributesString ) {
			for ( Map.Entry<ApplicationProperty,CharSequence> entry : this._attributesString.entrySet() ) {
				try {
					ret.put( entry.getKey().propertyName(), entry.getValue().toString() );
				}
				catch ( Exception ignored ) {
					// ignore
				}
			}
		}
		if ( null != this._attributesBool ) {
			for ( Map.Entry<ApplicationProperty,Boolean> entry : this._attributesBool.entrySet() ) {
				try {
					ret.put( entry.getKey().propertyName(), entry.getValue() );
				}
				catch ( Exception ignored ) {
					// ignore
				}
			}
		}
		return ret;
	}

	/**
	 * Return a JSON string representation of this object
	 * 
	 * @return a JSON string
	 */
	public String toJsonString() {
		return this.jsonify().toString();
	}

}
