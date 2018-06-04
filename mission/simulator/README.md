# NanoSat MO Framework - Mission - Simulator
The simulator contains a set of libraries and tools capable of simulating
most of the spacecraft functionalities available through the NMF.

This project is also an example implementation of mission-specific components of the NMF.

The simulator platform is based on OPS-SAT mission.

## Repository content

### [OPS-SAT GPS Simulator](opssat-gps-simulator)
Lightweight simulator of the basic orbital parameters, using an analytical solution.

### [OPS-SAT Spacecraft Simulator](opssat-spacecraft-simulator)
Simulates most of the spacecraft functionalities available through the NMF.

### [Platform Services Implementation](platform-services-impl)
Implements NMF Platform Services adapter layer, binding it to the simulator.

### [Nanosat MO Supervisor](nanosat-mo-supervisor)
Implements Space Applications Supervisor, exposing NMF Platform Services to the space apps.

### [Nanosat Monolithic](nanosat-monolithic)
Framework for a single, integrated space application, without Supervisor/Apps split.

## Building
Invoke `mvn clean install` in the main directory.

## Source Code
The source code of the NanoSat MO Framework can be found on [GitHub].

## Bugs Reporting
Bug Reports can be submitted on: [Bug Reports]

## License
The NanoSat MO Framework is **licensed** under the **[European Space Agency Public License - v2.0]**.

[![][ESAImage]][website]
	
	
[ESAImage]: http://www.esa.int/esalogo/images/logotype/img_colorlogo_darkblue.gif
[here]: https://nanosat-mo-framework.github.io/
[European Space Agency Public License - v2.0]: https://github.com/esa/CCSDS_MO_TRANS/blob/master/LICENCE.md
[GitHub]: https://github.com/esa
[Bug Reports]: https://github.com/esa/nanosat-mo-framework/issues
[website]: http://www.esa.int/
