NMF Composites
============

An NMF Composite is a software component that consists of interconnected services based on the NMF Generic Model specialized for a certain purpose and to be deployed on the NanoSat segment or Ground segment. The NMF Composites are based on SOA's service composability design principle that encourages reusing existing services and combine them together to build an advanced solution.

The objective of the NMF Composites is to provide prebuilt components that allow quick development of new software solutions that are interoperable in end-to-end scenarios.

The design of the NMF Composites is done in a modular and flexible manner which allows them to be reconfigured or adapted depending on the needs in the overall design of the system. This is similar to a Lego® type approach where the bricks can be recombined to form something different.


The family of NMF Composites is the following:
* NanoSat MO Monolithic
* NanoSat MO Supervisor
* NanoSat MO Connector
* Space MO Adapter
* Ground MO Adapter
* Ground MO Proxy

The names for the NMF Composites follow the convention: "Segment" MO "Purpose"

The NMF Generic Model implementation includes an abstract class, the NMFProvider that is extended by other classes such as the NanoSatMOConnectorImpl, NanoSatMOSupervisor, and NanoSatMOMonolithic. The NMFProvider class implements the NMFInterface which allows the application logic to be always exposed to the same methods for accessing the 5 services' sets: COM, M&C, Common, Platform, and Software Management services. It also allows having a common interface to push parameter values, publish alert events or report the execution progress of an action instances.

A generic implementation of the NanoSat MO Supervisor and NanoSat MO Monolithic must be extended for the specific platform of the mission because the peripherals available on the platform can be different between spacecraft. In that case, different implementations of the Platform services and different transport layers for the communication with ground might need to be developed. The generic nature of the design is flexible enough to accommodate this need.

The implementation of the Ground MO Proxy was implemented in a generic way to support mission-specific extensions. It includes a local Directory service that “mimics” the Central Directory service available on the NanoSat segment. It uses the Ground MO Adapter to connect to the Central Directory service and retrieve its information. It also includes a local Archive service to cache the information from the NMF Apps.

A dedicated SPP protocol bridge was implemented because it needs to do address translation from SPP to another transport and vice-versa. The MAL-SPP binding does not support routing natively as this would increase the size of the Space Packets and therefore address translation needs to be done.

The Space MO Adapter is an NMF Composite that allows NMF space applications to easily consume services of MO providers (supervisor or other space applications). It is intended to be deployed on-board.

============
NanoSat MO Monolithic
============

The NanoSat MO Monolithic is an NMF Composite intended to be deployed on the NanoSat segment for an on-board monolithic architecture. It consists of a set of services that facilitate not only the monitoring and control of the nanosatellite, but also the interaction with the platform peripherals. This component must be extended to the specific platform and particular mission use case.

There are three mission-specific parts to be integrated with this component. First, the communication binding to an appropriate transport needs to be selected for communication with ground. Second, the Platform services implementation for the specific platform. Third, the mission-specific logic for unique features dedicated to a specific mission scenario.

The Platform services includes an instance of both the service consumer and provider side. The provider side is intended to be consumed by both ground and the mission-specific logic of the application therefore a service consumer is needed to be present.

For a deployment that consists of a single application running on-board or for a constrained device incapable of running a full operating system, this simplistic monolithic approach is a possibility. However this is not the recommended approach because it does not take full advantage of the new NanoSat MO Framework concepts.

In order to have multiple applications running on the same machine with the NMF, two components were defined: the NanoSat MO Supervisor and the NanoSat MO Connector.

============
NanoSat MO Supervisor
============

The NanoSat MO Supervisor is an NMF Composite that acts as a supervisor and it is intended to be deployed on the NanoSat segment for an on-board deployment with multiple applications. It supports the discoverability of the providers available on-board, and it allows different applications to interact with the peripherals on-board via a set of Platform services. Additionally, it includes software management capabilities such as: starting, stopping, installing, updating and uninstalling applications on-board of the spacecraft. This component must be extended to the specific platform and particular mission use case.

This component includes the Central Directory service that provides discoverability functionality by holding the connections information about all the providers running on-board. Specifically, the NanoSat MO Supervisor provider and the set of registered running provider applications with their respective services.

The implementation of the Platform services to the platform peripherals on the NanoSat MO Supervisor allows multiple consumers to interact with the peripherals available on-board. The connection between a consumer and a provider of a Platform service is expected to happen via Inter-Process Communication (IPC).

The NanoSat MO Supervisor is able to install packages carrying applications or libraries via the Package Management service. After an application is installed, the Apps Launcher service can be used to start the application and afterwards stop it. The Package Management is able to update a certain package which allows, for example, new features or bug fixes to be ported into an application. Lastly, a package can be uninstalled by the NanoSat MO Supervisor.

============
NanoSat MO Connector
============

The NanoSat MO Connector is an NMF Composite that acts as a connector to the NanoSat MO Supervisor and it is intended to be deployed on the NanoSat segment for an on-board deployment with multiple applications. It is able to connect to the Directory service and register its services, connect to the Platform services to interact with the on-board peripherals, receive CloseApp event requests from the supervisor, and unregister from the Directory service. Additionally, it can provide monitoring and control capabilities to the layers above this component. This component doesn't need to be extended but it is expected to be integrated with other software to form an NMF App.

The interactions between the NanoSat MO Connector and the NanoSat MO Supervisor are expected to occur via IPC when they both run on the same operating system. This means that 2 different transports might coexist simultaneously, one for ground communications and another for IPC.

Upon startup, it shall make available the service provider connections to ground and then connects to the NanoSat MO Supervisor services and register the exposed services on the Central Directory service for visibility.

It is possible to find the service URIs of the Platform services in the Central Directory service and then connect to them using the consumer stub of the desired service.

After the stopApp operation is called from ground on the NanoSat MO Supervisor, it will send an event to instruct the NanoSat MO Connector to close. After receiving the event to close, the component will unregister the application from the Central Directory service and gracefully close the application. Additionally, an adapter can be set which will execute application-specific logic upon the reception of such event for the gracefully termination of the application logic.

Optionally, the NanoSat MO Connector can provide monitoring and control capabilities that can be integrated by the developer via the registration of Parameters definitions, Aggregations definitions, Alerts definitions and Actions definitions. The M&C services' logic doesn't need to be reimplemented multiple times and instead, it can be implemented one single time and used by the developers by simply taking advantage of the adapter pattern for the retrieval of parameters and execution of actions.

The NanoSat MO Connector allows the development of NMF Apps.

============
Space MO Adapter
============

The Space MO Adapter is an NMF Composite that allows NMF space applications to easily consume services of MO providers (supervisor or other space applications). It is intended to be deployed on-board.

============
Ground MO Adapter
============

The Ground MO Adapter is an NMF Composite that enables ground systems to connect to MO providers. It is intended to be deployed on ground and it is able to connect to all the other NMF Composites. Service interfaces are exposed to the ground system for interactions with MO providers. The services' communication configurations can be automatically discovered from the Directory service by using the provider's URI of the Directory service.

This component is foreseen to be used for the development of Monitor and Control Systems (MCS) or ground applications capable of interfacing with an NMF App however it is not limited NMF software. This means that a ground consumer using this component shall be able to connect to other different providers, such as a simple provider with only a Parameter service.

The integration of this component with another ground system application is reduced to linking the services that are intended to be used to the rest of the application. This integration can be done for an MCS, a mission automation system, or a dedicated application designed to connect to a specific NMF App.

The Consumer Test Tool (CTT) is a tool to test newly developed NMF Apps and it is part of the NMF SDK. This tool integrates the Ground MO Adapter and provides an easy GUI to interact with some of the services available on the NMF App. Additionally, a set of Ground demos exemplifying how to use the Ground MO Adapter are also presented in the NMF SDK.

The Ground MO Adapter opens many possibilities for the integration with other ground systems.

============
Ground MO Proxy
============

The Ground MO Proxy is an NMF Composite that acts as a proxy for ground systems that use the Ground MO Adapter. The Ground MO Proxy has 3 main functionalities, to act as a protocol bridge, as a COM Archive mirror, and it allows the queuing of actions. As a result, multiple consumers can share the same ground-to-space connection to the spacecraft, and connect to independent NMF Apps simultaneously.

This component has been included as part of the NMF Composites in order to solve the problem of using mission-specific protocols in the space-to-ground link while still remaining agnostic from any mission.

The MO Architecture allows the creation of a protocol-matching bridge or Gateway that allows translation from one encoding/transport choice to another because it supports the separation of information interoperability (MAL and higher layers) and protocol interoperability (encoding and transport).

The Ground MO Proxy shall connect to the NanoSat MO Supervisor, typically via SPP (but IP connections are not uncommon in nanosatellites), and expose on ground all the providers available on the Central Directory service. The connections details are edited to include the URI of the protocol bridge to route correctly forthcoming ground consumers. Examples of suitable protocol transports that can be exposed to ground consumers are HTTP, TCP/IP, ActiveMQ and/or RMI.

When the connection to the spacecraft is done using the MAL-SPP transport, address mapping has to be defined. This is because the MAL-SPP transport binding does not support routing as part of the binding protocol. A solution is to use a range of APIDs dedicated to the creation of virtual URIs that are dynamically mapped to the source URI upon the connection of a new consumer.

For some services, the Ground MO Proxy follows the concept of proxy service extension defined in the MO Reference Model book. This allows, for example, exposing a Directory service with re-routed URIs to the correct location, having an Archive service replica on ground in order to minimize the access to the on-board Archive services of each NMF App and activity tracking for the forwarding of actions.

