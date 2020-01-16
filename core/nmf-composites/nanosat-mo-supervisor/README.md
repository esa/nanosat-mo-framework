NanoSat MO Supervisor
============

The NanoSat MO Supervisor is an NMF Composite that acts as a supervisor and it is intended to be deployed on the NanoSat segment for an on-board deployment with multiple applications. It supports the discoverability of the providers available on-board, and it allows different applications to interact with the peripherals on-board via a set of Platform services. Additionally, it includes software management capabilities such as: starting, stopping, installing, updating and uninstalling applications on-board of the spacecraft. This component must be extended to the specific platform and particular mission use case.

This component includes the Central Directory service that provides discoverability functionality by holding the connections information about all the providers running on-board. Specifically, the NanoSat MO Supervisor provider and the set of registered running provider applications with their respective services.

The implementation of the Platform services to the platform peripherals on the NanoSat MO Supervisor allows multiple consumers to interact with the peripherals available on-board. The connection between a consumer and a provider of a Platform service is expected to happen via Inter-Process Communication (IPC).

The NanoSat MO Supervisor is able to install packages carrying applications or libraries via the Package Management service. After an application is installed, the Apps Launcher service can be used to start the application and afterwards stop it. The Package Management is able to update a certain package which allows, for example, new features or bug fixes to be ported into an application. Lastly, a package can be uninstalled by the NanoSat MO Supervisor.


