#!/bin/sh

find . -name "*.css" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.gif" | grep -v svn | grep -v target | xargs svn ps svn:mime-type image/gif
find . -name "*.ico" | grep -v svn | grep -v target | xargs svn ps svn:mime-type image/x-icon
find . -name "*.java" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.jdo" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.jpg" | grep -v svn | grep -v target | xargs svn ps svn:mime-type image/jpeg
find . -name "*.js" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.png" | grep -v svn | grep -v target | xargs svn ps svn:mime-type image/png
find . -name "*.properties" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.sh" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.sh" | grep -v svn | grep -v target | xargs svn ps svn:executable
find . -name "*.tml" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.txt" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.xml" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "*.zip" | grep -v svn | grep -v target | xargs svn ps svn:mime-type application/zip
find . -name "CHANGELOG" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
find . -name "README" | grep -v svn | grep -v target | xargs svn ps svn:eol-style native
