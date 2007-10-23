package com.facebook.api;

import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONWriter;

/**
 * Data structure for representing a photo tag.  Required by some API calls.
 */
public class PhotoTag {
  private double _x;
  private double _y;
  private Long _taggedUserId;
  private String _text;

  /**
   * Constructor.
   * 
   * @param text the text/label associated with this tag
   * @param x the 'x' offset for the tegged region
   * @param y the 'y' offset for the tagged region
   */
  //FIXME:  what are the units for 'x' and 'y', and what are the reference points (i.e. is '0,0' the top left of the image, etc.?)
  //        this information should be verified and then added to the javadoc
  public PhotoTag(String text, double x, double y) {
    assert (null != text && !"".equals(text));
    this._text = text;
    this._taggedUserId = null;
    this.setCoordinates(x, y);
  }

  /**
   * Constructor.
   * 
   * @param taggedUserId the UID of the user being tagged in the image
   * @param x the 'x' offset for the tegged region
   * @param y the 'y' offset for the tagged region
   */
  //FIXME:  what are the units for 'x' and 'y', and what are the reference points (i.e. is '0,0' the top left of the image, etc.?)
  //        this information should be verified and then added to the javadoc
  public PhotoTag(long taggedUserId, double x, double y) {
    assert (0 < taggedUserId);
    this._text = null;
    this._taggedUserId = taggedUserId;
    this.setCoordinates(x, y);
  }

  
  /**
   * Constructor.
   * 
   * @param x the 'x' offset for the tegged region
   * @param y the 'y' offset for the tagged region
   */
  //FIXME:  what are the units for 'x' and 'y', and what are the reference points (i.e. is '0,0' the top left of the image, etc.?)
  //        this information should be verified and then added to the javadoc
  private void setCoordinates(double x, double y) {
    assert (0.0 <= x && x <= 00.0);
    assert (0.0 <= y && y <= 100.0);
    this._x = x;
    this._y = y;
  }

  /**
   * Check to see if this PhotoTag is tagging a specific Facebook user
   * 
   * @return true if the tag is referencing a specific Facebook user
   *         false otherwise
   */
  public boolean hasTaggedUser() {
    return null != this._taggedUserId;
  }

  /**
   * @return the X coordinate of the tag
   */
  //FIXME:  relative to what?  what are the units?
  public double getX() {
    return this._x;
  }

  /**
   * @return the Y coordinate of the tag
   */
  //FIXME:  relative to what?  what are the units?  
  public double getY() {
    return this._y;
  }

  /**
   * @return the text/label associated with this tag
   */
  public String getText() {
    return this._text;
  }

  /**
   * @return the id of the associated Facebook user, or null if there isn't one.
   */
  public Long getTaggedUserId() {
    return this._taggedUserId;
  }

  /**
   * Return a JSON-formatted representation of this object.
   * 
   * @param writer the JSONWriter to buffer the result in, may be null.
   * 
   * @return a JSONWriter buffering the results. 
   * 
   * @throws JSONException
   */
  public JSONWriter jsonify(JSONWriter writer)
    throws JSONException {
    JSONWriter ret = (null == writer ? new JSONStringer() : writer)
      .object()
      .key("x").value(Double.toString(getX()))
      .key("y").value(Double.toString(getY()));

    return (hasTaggedUser()) ? ret.key("tag_uid").value(getTaggedUserId()).endObject() :
           ret.key("tag_text").value(getText()).endObject();
  }
}
