# NanoSat MO Framework

The [NanoSat MO Framework] is a software framework for small satellites based on CCSDS Mission Operations services.

It introduces the concept of apps in space that can be started and stopped from ground. Apps can retrieve data from the platform through a set of well-defined MO services. Many possibilities for extensions are available due to its modular and flexible design approach which is not limited to the space segment but extends down to ground by providing all the building blocks for a complete and free end-to-end solution.

A Software Development Kit (SDK) is available in order to facilitate the development with the [NanoSat MO Framework].

Wikipedia page: https://en.wikipedia.org/wiki/NanoSat_MO_Framework

Documentation: https://nanosat-mo-framework.readthedocs.io/en/latest/

Interfaces of the services: https://dmarszk.github.io/MOWebViewer4NMF/

Research Work Dissertation: https://www.researchgate.net/publication/321825076

[![][ESAImage]][website]

The research was carried by Graz University of Technology in partnership with the European Space Agency.

## Release
The latest NMF SDK release tag can be downloaded in [Releases]. However, the currently recommended distribution channel is directly from Git repository.

## Building Prerequisites

1. Install Java SDK 1.8 (will work with higher SDKs but 1.8 is the recommended)
```bash
sudo apt-get install openjdk-8-jdk
```
2. Install Apache Maven
```bash
sudo apt-get install maven
```

## Building Instructions

3. Clone this repository
```bash
git clone https://github.com/esa/nanosat-mo-framework.git
```

4. Set the environment variable NMF\_HOME with the path to this repository's root directory
```bash
export NMF_HOME=path_to_repository
```

5. Build the cloned NanoSat MO Framework project:
```bash
mvn clean install
```

Note that the errors about missing module descriptors during Javadoc generation are to be expected and non-breaking.

In order to produce independently runnable Java executables (JAR artifacts with dependencies - equivalent of statically linked executables), use `mvn install -P assembly-with-dependencies`

## Getting Started

### SDK and examples

More documentation about code examples, SDK packaging and usage is available under [sdk](sdk) directory.

### Logging

This project uses the default Java logger (java.util.Logger) to generate log messages. The verbosity of these log messages can be changed by configuring the logging.properties file inside the NMF\_HOME directory.

## Source Code

The source code of the NanoSat MO Framework can be found on [GitHub].

## Bugs Reporting

Bug Reports can be submitted on: [Issues]

Or directly in the respective source code repository.

## License

The NanoSat MO Framework is **licensed** under:

**[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4]**.

[![][ESAImage]][website]

[ESAImage]: http://www.esa.int/esalogo/images/logotype/img_colorlogo_darkblue.gif
[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4]: https://github.com/esa/nanosat-mo-framework/blob/master/LICENCE.md
[GitHub]: https://github.com/esa/nanosat-mo-framework
[Releases]: https://github.com/esa/nanosat-mo-framework/releases
[Issues]: https://gitlab.com/esa/NMF/nmf-issues/-/issues
[website]: http://www.esa.int/
[NanoSat MO Framework]: https://nanosat-mo-framework.github.io/
