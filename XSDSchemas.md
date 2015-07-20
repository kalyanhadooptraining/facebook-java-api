# Introduction #

The schema.xsd file used to generate the Java XML Bindings (JAXB) differs slightly from what Facebook makes available in facebook.xsd. The differences between the files are maintained in facebook.xsd.diff and this file should be updated whenever either facebook.xsd or schema.xsd is updated.

# Comparing our schema.xsd with facebook.xsd #

We can use the tools provided by google code to compare the latest version of our schema.xsd with the facebook.xsd that was downloaded from facebook.com and put in our source repository:

  * [Compare files](http://code.google.com/p/facebook-java-api/source/diff?spec=svn673&r=673&format=side&path=/trunk/facebook-java-api-schema/src/main/resources/schema.xsd&old_path=/trunk/facebook-java-api-schema/facebook.xsd&old=673#)

# Details #

A Maven plugin is used to generate the Java bindings. The details are in facebook-api/facebook-java-api-schema/pom.xml.

facebook.xsd is published at http://api.facebook.com/1.0/facebook.xsd and defines the response formats. Because Facebook provides a REST API, the request parameters for each method call do not have an XML schema or similar; instead we rely on the API documentation at http://wiki.developers.facebook.com/index.php/API

facebook.xsd.diff is kept up to date so that Facebook developers will be able to see at a glance what has had to change from facebook.xsd to get JAXB working. Hopefully bug http://bugs.developers.facebook.com/show_bug.cgi?id=3780 will be worked on and fixed by the Facebook team. Furthermore, if we get a new version of facebook.xsd supplied by Facebook it should be trivial to create a new version of schema.xsd which contains both the updates from Facebook and our fixes on top.