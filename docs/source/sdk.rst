===
SDK
===

.. contents:: Table of Contents
    :local:

The NMF includes a Software Development Kit (or SDK).

The SDK provides support and tools to develop and test your space apps and ground applications. The SDK generator is located in the **sdk/** folder of the repository. 
To generate the SDK you must build the code by running ``mvn install`` in the **sdk/** directory.

This will build all examples and put them into a zip release and a folder which you can find under **sdk/sdk-package/target/**.

Space app examples
------------------
In the folder `sdk/examples/space <https://github.com/esa/nanosat-mo-framework/tree/master/sdk/examples/space>`_ you will find several completely implemented apps which can run on any cubesat running the NMF.
These examples are also a great starting point when you begin to develop your own apps.

The examples include:

- Benchmark App - was used during the development in order to obtain some performance metrics for the framework
- Blank App - the simplest NMF App that one can develop; does not include any logic
- Hello World Simple App - a simple NMF app demonstrating MC::Parameter service using a simplified NMF MC API
- Hello World Full App - a simple NMF app demonstrating MC::Parameter service using a full NMF MC API
- Push Clock App - exposing clock over MC services
- 10 seconds Alert App - publishing periodic alert using MC::Alert service
- 5 stages Action App - implementing multistage asynchronous action
- GPS data App - exposing GPS data over MC::Parameter service
- All MC services App - exposing multiple MC services
- All MC services + Simulator App - exposing multiple MC services; standalone application not requiring Supervisor to provide NMF Platform services
- Camera App - consuming NMF Platform::Camera service and exposing a monitoring and control interface
- Serialized object - serializing a Java object and exposing it over MAL Blob Attribute

Ground application examples
---------------------------
In the folder `sdk/examples/ground <https://github.com/esa/nanosat-mo-framework/tree/master/sdk/examples/ground>`_ you will find several completely implemented ground applications which can run on ground and connect to remote NMF Apps.

ESA has developed a generic M&C system which can already connect to any NMF App. This software is known as EUD4MO.

If you just want to test your app locally without using the CTT you can write a fitting ground application which automates your testing process for you.

The examples include:

- Ground Zero
- Ground with Directory service
- Ground Set and Command
- Ground Facebook

Consumer Test Tool (CTT)
------------------------
The Consumer Test Tool (CTT) is your best friend when manually verifying your app. You can use the CTT to connect to the supervisor, launch apps and then connect to your apps and interact with them.
How to do all of that is described further in the next section.

Running the CubeSat Simulator
-----------------------------
You can run a CubeSat simulator to try out your app in a playground environment which mimics the CubeSat system.

In order to do this, Please do the following:

1. Run the Supervisor, go to **sdk/sdk-package/target/nmf-sdk-2.1.0-SNAPSHOT/home/nmf/nanosat-mo-supervisor-sim/** and run **nanosat-mo-supervisor-sim.sh**.

2. Run the CTT, go to **sdk/sdk-package/target/nmf-sdk-2.1.0-SNAPSHOT/home/nmf/consumer-test-tool/** and run **consumer-test-tool.sh**.

Connecting to the Supervisor using CTT
---------------------------------------
The supervisor outputs a URI on the console. This URI follows the pattern 
``maltcp://SOME_ADDRESS:PORT/nanosat-mo-supervisor-Directory``
Paste this URI into the field in the **Communication Settings** tab of the CTT and click the button **Fetch information**. In the *Providers List*, the supervisor should show up. The table on the right side should list some services. Now click the button **Connect to Selected Provider** which results in a new tab appearing next to the **Communication Settings**. You now have a working connection to the supervisor and are able to start apps and check messages.

.. image:: _images/CTT_presentation.png
   :width: 600

Running and connecting to an App
--------------------------------
The nanosat-mo-supervisor tab offers you several sub-tabs. One of these tabs controls the **Apps Launcher Service**. By selecting this tab, you see a list of every app that is currently registered on the Supervisor.
Select the app you want to launch (e.g. camera in the default package) and click the button **runApp**. All output produced by the app is printed in the **Apps Launcher Service** tab.
When you return to the **Communication Settings** tab and refresh your *Providers List* by selecting **Fetch Information**, your app should appear. You can connect to it in the same way as you did for the supervisor.

To get started with your own app, follow the upcoming links.

.. toctree::
   :maxdepth: 1

   netbeans
   eclipse
