Ground MO Proxy
============

The Ground MO Proxy is an NMF Composite that acts as a proxy for ground systems that use the Ground MO Adapter. The Ground MO Proxy has 3 main functionalities, to act as a protocol bridge, as a COM Archive mirror, and it allows the queuing of actions. As a result, multiple consumers can share the same ground-to-space connection to the spacecraft, and connect to independent NMF Apps simultaneously.

This component has been included as part of the NMF Composites in order to solve the problem of using mission-specific protocols in the space-to-ground link while still remaining agnostic from any mission.

The MO Architecture allows the creation of a protocol-matching bridge or Gateway that allows translation from one encoding/transport choice to another because it supports the separation of information interoperability (MAL and higher layers) and protocol interoperability (encoding and transport).

The Ground MO Proxy shall connect to the NanoSat MO Supervisor, typically via SPP (but IP connections are not uncommon in nanosatellites), and expose on ground all the providers available on the Central Directory service. The connections details are edited to include the URI of the protocol bridge to route correctly forthcoming ground consumers. Examples of suitable protocol transports that can be exposed to ground consumers are HTTP, TCP/IP, ActiveMQ and/or RMI.

When the connection to the spacecraft is done using the MAL-SPP transport, address mapping has to be defined. This is because the MAL-SPP transport binding does not support routing as part of the binding protocol. A solution is to use a range of APIDs dedicated to the creation of virtual URIs that are dynamically mapped to the source URI upon the connection of a new consumer.

For some services, the Ground MO Proxy follows the concept of proxy service extension defined in the MO Reference Model book. This allows, for example, exposing a Directory service with re-routed URIs to the correct location, having an Archive service replica on ground in order to minimize the access to the on-board Archive services of each NMF App and activity tracking for the forwarding of actions.

