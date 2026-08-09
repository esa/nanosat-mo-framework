## NanoSat MO Framework - Mission - Simulator - Orekit

The simulator contains a set of libraries and tools capable of simulating
most of the spacecraft functionalities available through the NMF.

This project is also an example implementation of mission-specific components of the NMF.

The simulator platform is based on the classic CubeSat missions.

The orbit and the attitude are propagated with Orekit. For a simulator without
that library, which works the orbit out analytically and reports position only,
see `nmf-mission-simulator-lite`.

### Repository content

#### [CubeSat Spacecraft Simulator](cubesat-spacecraft-simulator)

Simulates most of the spacecraft functionalities available through the NMF.

#### [Orekit Resources](orekit-resources)

The Orekit library and the reference data it needs to propagate an orbit.

#### [Platform Services Implementation](platform-services-impl)

Implements NMF Platform Services adapter layer, binding it to the simulator.

#### [Nanosat MO Supervisor](nanosat-mo-supervisor-sim)

Implements Space Applications Supervisor, exposing NMF Platform Services to the space apps.

#### [Nanosat Monolithic](nanosat-monolithic)

Framework for a single, integrated space application, without Supervisor/Apps split.

#### [Celestia](celestia)

Shows the simulated spacecraft flying, in Celestia, running in a container.

### Building

Invoke `mvn clean install` in the main directory.

### Source Code

The source code of the NanoSat MO Framework can be found on [GitHub].

### Bugs Reporting

Bug Reports can be submitted on: [Issues]


## License

The NanoSat MO Framework is **licensed** under the **[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4]**.

[![][ESAImage]][website]

[ESAImage]: http://www.esa.int/esalogo/images/logotype/img_colorlogo_darkblue.gif
[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4]: https://github.com/esa/nanosat-mo-framework/blob/master/LICENCE.md
[GitHub]: https://github.com/esa
[Issues]: https://gitlab.com/esa/NMF/nmf-issues/-/issues
[website]: http://www.esa.int/
