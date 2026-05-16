===
SDK
===

.. contents:: Table of Contents
    :local:

The NMF includes a Software Development Kit (SDK) that provides tools and resources for developing and testing space apps and ground applications. The SDK generator is located in the **sdk/** folder of the repository.

To generate the SDK, run ``mvn install`` from the **sdk/** directory. This builds all examples and produces both a zip release and an expanded folder under **sdk/sdk-execution-environment/target/**.

Space app examples
------------------
The `sdk/examples-space <https://github.com/esa/nanosat-mo-framework/tree/master/sdk/examples-space>`_ folder contains a set of fully implemented apps that can run on any CubeSat running the NMF. These examples serve as a recommended starting point for developing custom apps.

The available examples are:

- **Benchmark App** — used during framework development to obtain performance metrics.
- **Blank App** — the minimal NMF App, with no application logic.
- **Hello World Simple App** — demonstrates the MC::Parameter service using the simplified NMF MC API.
- **Hello World Full App** — demonstrates the MC::Parameter service using the full NMF MC API.
- **Push Clock App** — exposes the system clock via MC services.
- **10 Seconds Alert App** — publishes a periodic alert using the MC::Alert service.
- **5 Stages Action App** — implements a multistage asynchronous action.
- **GPS Data App** — exposes GPS data via the MC::Parameter service.
- **All MC Services App** — exposes multiple MC services.
- **All MC Services + Simulator App** — exposes multiple MC services as a standalone application that does not require a Supervisor to provide the NMF Platform services.
- **Camera App** — consumes the NMF Platform::Camera service and exposes a monitoring and control interface.
- **Serialized Object** — serializes a Java object and exposes it via a MAL Blob Attribute.

Ground application examples
---------------------------
The `sdk/examples-ground <https://github.com/esa/nanosat-mo-framework/tree/master/sdk/examples-ground>`_ folder contains fully implemented ground applications that can connect to remote NMF Apps.

ESA has developed a generic M&C system, known as EUD4MO, that can connect to any NMF App.

For automated local testing without the CTT, a dedicated ground application can be written to drive the app programmatically.

The available examples are:

- Ground Zero
- Ground with Directory service
- Ground Set and Command
- Ground Facebook

Consumer Test Tool (CTT)
------------------------
The Consumer Test Tool (CTT) is the primary tool for manually verifying an app. It can be used to connect to the Supervisor, launch apps, and interact with them through their exposed MC services. The following sections describe its usage.

Running the CubeSat Simulator
-----------------------------
The CubeSat simulator provides a local environment that mimics the spacecraft system, allowing apps to be tested without access to flight hardware.

To start the simulator and CTT:

1. Run the Supervisor by executing **nanosat-mo-supervisor-sim.sh** from **sdk/sdk-execution-environment/target/nmf-sdk-XX.Y/home/nmf/nanosat-mo-supervisor-sim/**.

2. Run the CTT by executing **consumer-test-tool.sh** from **sdk/sdk-execution-environment/target/nmf-sdk-XX.Y/home/nmf/consumer-test-tool/**.

Connecting to the Supervisor using the CTT
------------------------------------------
On startup, the Supervisor prints its Directory Service URI to the console, in the form:

``maltcp://SOME_ADDRESS:PORT/nanosat-mo-supervisor-Directory``

Paste this URI into the corresponding field in the **Communication Settings** tab of the CTT and click **Fetch Information**. The Supervisor will appear in the *Providers List*, and the table on the right will display the available services. Clicking **Connect to Selected Provider** opens a new tab alongside **Communication Settings**, providing an active connection to the Supervisor from which apps can be started and messages inspected.

.. image:: _images/CTT_presentation.png
   :width: 600

Running and connecting to an App
--------------------------------
The nanosat-mo-supervisor tab contains several sub-tabs, one of which controls the **Apps Launcher Service**. Selecting this tab displays the list of apps currently registered on the Supervisor.
Select the desired app (for example, the camera app from the default package) and click **runApp**. All output produced by the app is displayed in the **Apps Launcher Service** tab.
Returning to the **Communication Settings** tab and clicking **Fetch Information** refreshes the *Providers List*, where the launched app will now appear. The app can then be connected to using the same procedure as for the Supervisor.

For instructions on developing a custom app, refer to the guides linked below.

.. toctree::
   :maxdepth: 1

   netbeans
   eclipse
