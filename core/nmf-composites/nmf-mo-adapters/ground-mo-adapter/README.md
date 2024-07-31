Ground MO Adapter
============

The Ground MO Adapter is an NMF Composite that enables ground systems to connect to MO providers. It is intended to be deployed on ground and it is able to connect to all the other NMF Composites. Service interfaces are exposed to the ground system for interactions with MO providers. The services' communication configurations can be automatically discovered from the Directory service by using the provider's URI of the Directory service.

This component is foreseen to be used for the development of Monitor and Control Systems (MCS) or ground applications capable of interfacing with an NMF App however it is not limited NMF software. This means that a ground consumer using this component shall be able to connect to other different providers, such as a simple provider with only a Parameter service.

The integration of this component with another ground system application is reduced to linking the services that are intended to be used to the rest of the application. This integration can be done for an MCS, a mission automation system, or a dedicated application designed to connect to a specific NMF App.

The Consumer Test Tool (CTT) is a tool to test newly developed NMF Apps and it is part of the NMF SDK. This tool integrates the Ground MO Adapter and provides an easy GUI to interact with some of the services available on the NMF App. Additionally, a set of Ground demos exemplifying how to use the Ground MO Adapter are also presented in the NMF SDK.

The Ground MO Adapter opens many possibilities for the integration with other ground systems.





