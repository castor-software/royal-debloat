clitools
========

A bundle of command-line tools that are quite useful in day-to-day life. Some of these tools
are available directly on some OS platforms - but not all - and hence, the cross-platform
implementation.

Changelog
---------

**Current Development**

```
Available commands:

  base64     Base64 encode/decode
  cleantmp   Clean the current temp directories
  crc32      Compute the CRC32 hash of given file/file pattern(s)
  curr       Currency converter
  day        Display the day today or a given date
  epoch      Show current time as epoch, millis in GMT
  fbinfo     Show information about a facebook account or page
  filesort   Tool to sort files in a directory by prefixing numerals
  findfile   Find files in a directory
  format     Format the file with proper white spaces and indentation
  gd         Google dictionary from command line
  headers    Display response headers for a given URL
  hex        Dump a given file as hex
  hostname   Prints the name of the current host
  htalk      Search hackernews threads
  imdb       Fetch movie information from IMDB
  ldap       Connect to an LDAP server
  ltrim      Remove whitespaces from start of each line of the file
  md2        Compute the MD2 hash of given file/file pattern(s)
  md5        Compute the MD5 hash of given file/file pattern(s)
  mvnclean   Clean erroroneous Maven artifacts
  mvnsearch  Search Maven artifacts
  myip       Display the IP address of this machine
  prop       Provides access to user properties
  quakes     Get details of recently reported earthquakes from US Geological Survey
  randfile   Generate a random file of given size
  rtrim      Remove whitespaces from end of each line of the file
  sha1       Compute the SHA1 hash of given file/file pattern(s)
  sha256     Compute the SHA256 hash of given file/file pattern(s)
  sha384     Compute the SHA384 hash of given file/file pattern(s)
  sha512     Compute the SHA512 hash of given file/file pattern(s)
  sleep      Pause for NUMBER seconds
  stock      Stock quotes from NASDAQ
  tree       Displays a directory tree of the folder
  trim       Remove whitespaces from each line of the file
  uuidgen    Generates a new universally unique identifier (UUID)
  ver        Display the OS version
  vol        Displays the volume name
  whoami     Show current user's name
```

Versioning
----------

For transparency and insight into our release cycle, and for striving to maintain backward compatibility,
`clitools` will be maintained under the Semantic Versioning guidelines as much as possible.

Releases will be numbered with the follow format:

`<major>.<minor>.<patch>`

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major
* New additions without breaking backward compatibility bumps the minor
* Bug fixes and misc changes bump the patch

For more information on SemVer, please visit http://semver.org/.

License
-------

```
clitools - Simple command line tools
Copyright (c) 2014 - 2015, Sandeep Gupta

	http://sangupta.com/projects/clitools

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
