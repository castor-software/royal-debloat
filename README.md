[![Build Status](https://travis-ci.org/castor-software/royal-debloat.svg?branch=master)](https://travis-ci.org/castor-software/royal-debloat)
[![Coverage Status](https://coveralls.io/repos/github/castor-software/royal-debloat/badge.svg?branch=master)](https://coveralls.io/github/castor-software/royal-debloat?branch=master)
# Royal Debloat 

This repo contains software debloating tools invented at KTH Royal Institute of Technology. Every tool is organized in a separate folder in this repo, with a detailed README file inside.

## JDbl (pom)

A tool to seek at the root of software bloat: declared but unused dependencies. It automatically detects and removes all the unused dependencies in the `pom.xml` file of Maven projects. This tool relies on static analysis and do not transform any part of the application. It can be integrated directly in Maven projects as a plugin.


## JDbl (tracer)

A tool for automatically specializing Java applications through dynamic debloat. It removes unused classes and methods from Maven projects (including its dependencies) by tracing the application execution at runtime and modifying the bytecode on the fly during the Maven building process. It can be used directly as a Maven plugin or executed out-of-the-box as a standalone debloat application.

:warning: WIP: use at your own risk
