# `jdbl-pom`: Tools to Investigate About Bloated Dependencies in the Maven Ecosystem


`jdbl-pom` is a set of tools to perform dependency analysis of Java projects that build with Maven. Specifically, it concentrates on analyzing bloated dependencies, i.e. dependencies that are entirely added to the project's dependency tree, yet no single method of its API is actually being used. It has two modules:

- `jdbl-pom-experiments`: A tool to automatically collect and analyze dependency usages between Maven artifacts in the Maven Central repository
- `jdbl-pom-maven-plugin`: A Maven plugin to automatically debloat `pom.xml` files in Maven projects 

## License

Distributed under the MIT License. See [LICENSE](https://github.com/castor-software/royal-debloat/blob/master/LICENSE) for more information.