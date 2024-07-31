Platform services implementation
============

The implementation of the Platform services includes all the services with a “thread-safe” design as they are expected to be consumed by multiple NMF Apps simultaneously. For the consumer side, all the services' stubs have been made available while for the provider, the skeletons of the services were implemented however without any specific backend. Just like in the M&C services, the Adapter pattern was used.

The Adapter pattern allows an adapter to be submitted to the service and therefore different missions can reuse the same implementation of the service while the adapter holds just the mission-specific logic to interact with the actual unit.

============

The Adapter pattern used in the Platform services implementation also allows possibility of integration with an Electronic Data Sheet (EDS) device by having an adapter making calls on the stubs generated from the EDS.

