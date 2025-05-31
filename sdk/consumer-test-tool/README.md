Consumer Test Tool (CTT)
============

The Consumer Test Tool (CTT) is an application to aid developers in the process of testing newly developed NMF Apps from a user-friendly interface. It includes a set of GUI panels that provides a Graphical User Interface (GUI) to interact with the services on a provider. CTT can also be used as a learning tool for MO services, or to connect to MO providers other than NMF providers.

In implementation terms, it uses the Ground MO Adapter for the connection to the providers. Although CTT is provided as “ready to be used” application, its source code is also available for developers to learn and take as example.

CTT was designed to support the connection to multiple NMF Apps simultaneously via a tabbed view mechanism. After pressing the “Connect to Selected Provider” button, it creates a new instance of the Ground MO Adapter that connects to the provider, and then opens a new tab on top for interacting with it.

Inside the provider tab there is the provider status bar that indicates the status of the provider and calculates the round-trip delay time. In terms of implementation, this bar takes advantage of the Heartbeat service from the Software Management services by subscribing and listening to the periodic beats coming from the service. If the beats are being received within the expected time window then the status is set as “Alive!”. If CTT stops receiving the beats then the status changes to “Unresponsive”.
If the Heartbeat service does not exist on the provider, the bar displays the message: "Heartbeat service not available."

============

Historically, CTT derived from the “Consumer Interface (CCSDS MO)” application that was developed for the “Implementation of 2 CCSDS MO services on a MityArm board” of a master thesis project. During the research period, this code was reiterated multiple times for improvement and therefore multiple new features were implemented to a point where the original code of the project is almost inexistent. CTT has been called “Configuration Manager” during the first year of the research however the name was not appropriate and therefore it was changed to “Consumer Test Tool” or just CTT.


