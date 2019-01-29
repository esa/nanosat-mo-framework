Example NMF Apps
============

The source code examples include a package containing a set of demos with NMF Apps. These demos are very simple applications which can be used by the developers as models. The following NMF App examples are available:
* Benchmark App - was used during the development in order to obtain some performance metrics for the framework
* Blank App - the simplest NMF App that one can develop; does not include any logic
* Hello World Simple App - a simple NMF app demonstrating MC::Parameter service using a simplified NMF MC API
* Hello World Full App - a simple NMF app demonstrating MC::Parameter service using a full NMF MC API
* Push Clock App - exposing clock over MC services
* 10 seconds Alert App - publishing periodic alert using MC::Alert service
* 5 stages Action App - implementing multistage asynchronous action
* GPS data App - exposing GPS data over MC::Parameter service
* All MC services App - exposing multiple MC services
* All MC services + Simulator App - exposing multiple MC services; standalone application not requiring Supervisor to provide NMF Platform services
* Camera App - consuming NMF Platform::Camera service and exposing a monitoring and control interface
* Serialized object - serializing a Java object and exposing it over MAL Blob Attribute

The NMF Apps development guide includes more information.

