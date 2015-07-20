# Introduction #

Google Android uses the [Dalvik Virtual Machine](http://en.wikipedia.org/wiki/Dalvik_virtual_machine) to run Java code. Once you've generated your .class files, another compilation step is required go get to Dalvik bytecode.

This document is not a commitment to support Android. Support will only be given if we can do so without compromising the features enjoyed by J2SE users and by our developers. For example, annotation processors are used to generate some of the API code and reduce maintainability. I don't see why the resulting class files wouldn't work with Dalvik, but that needs to be investigated.

We don't support J2ME because we can't do so without making many compromises.

# Trial run with Android #
