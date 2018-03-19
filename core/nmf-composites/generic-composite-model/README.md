NMF Generic Model
============

The NMF Generic Model defines the set of services and building blocks to be used together and its respective orchestration. This is the foundation to the NMF Composites, a set of specialized components that when connected together create end-to-end scenarios. The MO architecture is the underlying architecture of the NMF Generic Model.

As part of the MO architecture, the MAL is present in order to decouple the Services Layer from the Transport Layer. This allows the use of the same service over different transports.

In order to cover the core functionalities of the framework, the NMF Generic Model takes advantage of five sets of services:
* COM services
* M&C services
* Common services
* Platform services
* Software Management services

The same underlying transport layer must be present on the consumer and provider sides in order to exchange data between the two entities. At least one transport layer binding needs to be available in all the NMF Composites in order to guarantee that the components are able to exchange data. Therefore, the NMF Generic Model has TCP/IP transport layer binding as default due its general availability in today’s computers, and for being already a standardized CCSDS transport binding. The NMF Generic Model has the Binary encoding as default due to its simplicity.

If a mission needs to use a mission-specific transport, a protocol bridge can be used to accommodate this need.

As previously mentioned the NMF Generic Model uses the MO architecture and specifies the set of services to be orchestrated with it that allow the design of specialized components that include dedicated functionality depending on its use case. These specialized components are the “NMF Composites”. They can produce complete applications that are able to operate in different system scenarios.


