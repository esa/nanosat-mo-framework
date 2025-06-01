Software Management services implementation
============

The implementation of the Software Management services includes 3 services: Heartbeat service, Apps Launcher service, and Package Management service.

The Heartbeat service is very simple and was implemented with a Timer set to publish a heartbeat message every 10 seconds.

The Apps Launcher service scans the specified folder for new applications every time the list of applications is requested or if an application is started, stopped, or killed. When an application is started, the implementation starts a new process and passes as parameters the first text line of the file runAppLin.sh or runAppWin.bat depending on the operating system where the service is running. This allows the same service to operate both in a Linux and Windows operating system.

The mechanism mentioned above is not limited to NMF Apps and can be used to start scripts or any other applications. The service is starting a new process per application and each of them has its own memory space. The service does not allow running multiple instances of the same application. This was enforced for simplicity reasons.

If the service is to be used to start “untrusted” applications, this procedure should be further improved by running each process inside a sandbox environment. This approach is further explained in César's Dissertation available on the docs folder of the NMF SDK.

The Package Management service was implemented using the Adapter pattern in order to be possible to exchange the backend with different types of package management systems.

============

The Software Management services are good candidates for future standardization of the CCSDS Software Management services. This implementation could be reused as a starting prototype.


