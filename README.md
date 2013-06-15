fastinfoset-util
================

Minimal utilities for working with Fast Infoset binary XML encoding. These utilities are
intended for development, testing, debugging, and operational support. They are specifically
NOT concerned with

  - runtime performance,
  - robustness,
  - fault tolerance,
  - threadsafety, or
  - low memory footprint

and in general are not intended as examples of proper production code. These are just to 
help you work manually with Fast Infoset, which is not only very difficult for a human to
read, but usually unsupported by XML editors.

FAST INFOSET CONVERTER
======================
package gov.usgs.cida.fastinfoset

One file: FastInfosetConverter.java

OVERVIEW
--------

This is a simple utility with two core methods:

  - fiStream2xmlStream
  - xmlStream2fiStream
  
which are stream-to-stream reserialization methods that take two parameters, a java.io.InputStream
and a java.io.OutputStream. They are thus very generally usable.

There are also convenience methods:

  - fiFile2xmlFile
  - xmlFile2fiFile
  
which accept file pathnames, reserializing the parsed content of the first and writing it to 
the second. All of the usual file I/O caveats apply.

SETUP AND USE
-------------

There is one dependency jarfile, org.apache.servicemix.bundles.fastinfoset-1.2.2_1.jar, available
from various mirrors.

Currently the use would be to instantiate the file in java, and execute its methods from there.
(quick&dirty example: put your code in main(String[]) and run the file either from the commandline or
your IDE.)

Planned extensions are 
1) to fix the main method to accept proper args, e.g.
  "java -c gov/usgs/cida/fastinfoset/FastinfosetConverter sourcefile.fi targetfile.xml"
2) to write a shell script to invoke java.exe and pass the necessare arguments in:
  "fi-convert -f sourcefile.fi -x targetfile.xml"
  or something of the sort.




