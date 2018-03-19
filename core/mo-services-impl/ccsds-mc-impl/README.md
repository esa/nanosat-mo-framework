M&C services implementation
============

The implementation of the Monitor and Control (M&C) services includes all the 6 main services and the 2 auxiliary services. The Check service and the Statistic service were never used after their implementation.

Each service contains dedicated managers in order to manage the existing definitions in the service. They have small differences among them although they all support adding, removing, or updating definitions.

Besides the functionality mentioned above, the Parameter Manager also includes the possibility of using an adapter that connects to another entity of software in order to link the getters and setters to interact with parameter values. Instead of having a fixed list of parameters, the adapter allows the developer to have any combination of parameters and shifts the responsibility of linking a parameter name to an actual value to the developer implementing the adapter. This is possible by using the Adapter pattern.

For the Action service, there is an Action Manager that allows the management of the definitions and also allows the possibility of submitting an adapter to dispatch the actions. It is up to the implementer of the adapter how the actions are dispatched and linked to an actual activity. The Archive service allows the reporting of the different progress stages of the action.

============

The Managers have interactions with the Archive service in order to store and retrieve the definitions. Additionally, each service has a certain configuration that can be restored at any time by the provider. For the NMF’s Java implementation, this occurs during start up.

The implementation of the services was started when the 3rd revision of the M&C services specification was on-going. It was developed in collaboration with DLR and found many problems with the specification which were later fixed. Both the specification and implementation had to be updated to cope with these changes.


