NanoSat MO Monolithic
============

The NanoSat MO Monolithic is an NMF Composite intended to be deployed on the NanoSat segment for an on-board monolithic architecture. It consists of a set of services that facilitate not only the monitoring and control of the nanosatellite, but also the interaction with the platform peripherals. This component must be extended to the specific platform and particular mission use case.

There are three mission-specific parts to be integrated with this component. First, the communication binding to an appropriate transport needs to be selected for communication with ground. Second, the Platform services implementation for the specific platform. Third, the mission-specific logic for unique features dedicated to a specific mission scenario.

The Platform services includes an instance of both the service consumer and provider side. The provider side is intended to be consumed by both ground and the mission-specific logic of the application therefore a service consumer is needed to be present.

For a deployment that consists of a single application running on-board or for a constrained device incapable of running a full operating system, this simplistic monolithic approach is a possibility. However this is not the recommended approach because it does not take full advantage of the new NanoSat MO Framework concepts.

In order to have multiple applications running on the same machine with the NMF, two components were defined: the NanoSat MO Supervisor and the NanoSat MO Connector.


