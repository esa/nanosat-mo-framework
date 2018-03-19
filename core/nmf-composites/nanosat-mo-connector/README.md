NanoSat MO Connector
============

The NanoSat MO Connector is an NMF Composite that acts as a connector to the NanoSat MO Supervisor and it is intended to be deployed on the NanoSat segment for an on-board deployment with multiple applications. It is able to connect to the Directory service and register its services, connect to the Platform services to interact with the on-board peripherals, receive CloseApp event requests from the supervisor, and unregister from the Directory service. Additionally, it can provide monitoring and control capabilities to the layers above this component. This component doesn’t need to be extended but it is expected to be integrated with other software to form an NMF App.

The interactions between the NanoSat MO Connector and the NanoSat MO Supervisor are expected to occur via IPC when they both run on the same operating system. This means that 2 different transports might coexist simultaneously, one for ground communications and another for IPC.

Upon startup, it shall make available the service provider connections to ground and then connects to the NanoSat MO Supervisor services and register the exposed services on the Central Directory service for visibility.

It is possible to find the service URIs of the Platform services in the Central Directory service and then connect to them using the consumer stub of the desired service.

After the stopApp operation is called from ground on the NanoSat MO Supervisor, it will send an event to instruct the NanoSat MO Connector to close. After receiving the event to close, the component will unregister the application from the Central Directory service and gracefully close the application. Additionally, an adapter can be set which will execute application-specific logic upon the reception of such event for the gracefully termination of the application logic.

Optionally, the NanoSat MO Connector can provide monitoring and control capabilities that can be integrated by the developer via the registration of Parameters definitions, Aggregations definitions, Alerts definitions and Actions definitions. The M&C services’ logic doesn’t need to be reimplemented multiple times and instead, it can be implemented one single time and used by the developers by simply taking advantage of the adapter pattern for the retrieval of parameters and execution of actions.

The NanoSat MO Connector allows the development of NMF Apps.

