# NanoSat MO Framework
The NanoSat MO Framework is a software framework for nanosatellites based on CCSDS Mission Operations services.

It introduces the concept of apps in space that can be started and stopped from ground. Apps can retrieve data from the platform through a set of well-defined MO services. Many possibilities for extensions are available due to its modular and flexible design approach which is not limited to the space segment but extends down to ground by providing all the building blocks for a complete and free end-to-end solution. The research was carried by Graz University of Technology in partnership with the European Space Agency.

A Software Development Kit (SDK) is available in order to facilitate the development with the NanoSat MO Framework.

# Release
The latest NMF SDK release tag can be downloaded in [Releases]. However, the currently recommended distribution channel is directly from Git repository.

# Getting Started

## Building
Prerequisites:
- Java SDK 1.8+
- Apache Maven

Clone this repository and run `mvn install` in the root directory.

In order to produce independently runnable Java executables (JAR artifacts with dependencies), use `mvn install -P assembly-with-dependencies`.

## SDK and examples
More documentation about code examples, SDK packaging and usage is available under [sdk](sdk) directory.

# Source Code
The source code of the NanoSat MO Framework can be found on [GitHub].

# Bugs Reporting
Bug Reports can be submitted on: [Bug Reports]

Or directly in the respective source code repository.

# License
The NanoSat MO Framework is **licensed** under the **[European Space Agency Public License - v2.0]**.

[![][ESAImage]][website]
	
	
[ESAImage]: http://www.esa.int/esalogo/images/logotype/img_colorlogo_darkblue.gif
[European Space Agency Public License - v2.0]: https://github.com/esa/CCSDS_MO_TRANS/blob/master/LICENCE.md
[GitHub]: https://github.com/esa
[Releases]: https://github.com/esa/nanosat-mo-framework/releases
[Bug Reports]: https://github.com/esa/nanosat-mo-framework/issues
[website]: http://www.esa.int/
