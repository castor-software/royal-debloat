# Royal Debloat  [![Build Status](https://travis-ci.org/castor-software/royal-debloat.svg?branch=master)](https://travis-ci.org/castor-software/royal-debloat)

This repo contains software debloating tools invented at KTH Royal Institute of Technology. Every tool is organized in a separate folder in this repo, with a detailed README file inside.

## Depclean

[Depclean](https://github.com/castor-software/depclean) is a set of tools to perform dependency analysis of Java projects. Specifically, it focuses on automatically detecting and removing bloated dependencies, i.e. dependencies that are entirely added to the project's dependency tree, yet no single method of its API is actually being used.

## JDbl

[JDbl](https://github.com/castor-software/jdbl) is a tool for automatically specializing Java applications through dynamic debloat. It removes unused classes and methods from Maven projects (including its dependencies) by tracing the application execution at runtime and modifying the bytecode on the fly during the Maven building process. It can be used directly as a Maven plugin or executed out-of-the-box as a standalone debloat application.

:warning: WIP: use at your own risk
