NMF Composites
============

An NMF Composite is a software component that consists of interconnected services based on the NMF Generic Model specialized for a certain purpose and to be deployed on the NanoSat segment or Ground segment. The NMF Composites are based on SOA’s service composability design principle that encourages reusing existing services and combine them together to build an advanced solution.

The objective of the NMF Composites is to provide prebuilt components that allow quick development of new software solutions that are interoperable in end-to-end scenarios.

The design of the NMF Composites is done in a modular and flexible manner which allows them to be reconfigured or adapted depending on the needs in the overall design of the system. This is similar to a Lego® type approach where the bricks can be recombined to form something different.


The family of NMF Composites is the following:
* NanoSat MO Monolithic
* NanoSat MO Supervisor
* NanoSat MO Connector
* Ground MO Adapter
* Ground MO Proxy

The names for the NMF Composites follow the convention: "Segment" MO "Purpose"

============

The NMF Generic Model implementation includes an abstract class, the NMFProvider that is extended by other classes such as the NanoSatMOConnectorImpl, NanoSatMOSupervisor, and NanoSatMOMonolithic. The NMFProvider class implements the NMFInterface which allows the application logic to be always exposed to the same methods for accessing the 5 services’ sets: COM, M&C, Common, Platform, and Software Management services. It also allows having a common interface to push parameter values, publish alert events or report the execution progress of an action instances.

A generic implementation of the NanoSat MO Supervisor and NanoSat MO Monolithic must be extended for the specific platform of the mission because the peripherals available on the platform can be different between spacecraft. In that case, different implementations of the Platform services and different transport layers for the communication with ground might need to be developed. The generic nature of the design is flexible enough to accommodate this need.

The implementation of the Ground MO Proxy was implemented in a generic way to support mission-specific extensions. It includes a local Directory service that “mimics” the Central Directory service available on the NanoSat segment. It uses the Ground MO Adapter to connect to the Central Directory service and retrieve its information. It also includes a local Archive service to cache the information from the NMF Apps.

A dedicated SPP protocol bridge was implemented because it needs to do address translation from SPP to another transport and vice-versa. The MAL-SPP binding does not support routing natively as this would increase the size of the Space Packets and therefore address translation needs to be done.

