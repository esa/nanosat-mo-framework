Common services implementation
============

The implementation of the Common services includes all the 3 services: Configuration service, Directory service, and Login service.

The implementation of the Common services was done before there was an official CCSDS standard of the services. Throughout the development many problems were found and these were directly fed back into the draft version. There was also a strong collaboration in evolving the Configuration service from a single operation service with implementation/specific Configurations into something more complex which includes a concept itself of a “Configuration” in MO.

The NMF Composites do not instantiate the Configuration service itself however they strongly rely on its COM objects data model in order to store the state of other services. This is very useful because upon start up, it allows restoring the previous state of the service.

============

The Directory service plays an important role in the NMF because the provider side can be found in 3 different places of the system: in the NMF App itself, in the NanoSat MO Supervisor as Central Directory service, and in the Ground MO Proxy as a replica of the Central Directory service. The Directory service implementation includes all its operations except for the getServiceXML operation.

The Login service implementation was developed by a student participating in ESA’s Summer of Code in Space (SOCIS). This implementation uses Apache Shiro and the MAL Access Control mechanism for handling the logins.



