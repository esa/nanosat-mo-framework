SDK
===
The SDK provides basic functionality to develop and test your space apps our ground applications. The SDK is located is the **sdk/** folder of the repository. 

The SDK mainly consists of:

* Ground application examples
* Space app examples
* Documentation (will be exported to RTD)
* Release package

To build the SDK you can just run ``mvn install`` in the **sdk/** directory. This will build all examples and package them into a release zip which you can find under **sdk/sdk-package/target/**.

Running the SDK
---------------
1. To run the supervisor, go to **sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/bin/space/nanosat-mo-supervisor-sim/** and run **nanosat-mo-supervisor-sim.sh**.

2. To run the CTT, go to **sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/bin/tools/consumer-test-tool/** and run **consumer-test-tool.sh**.

3. You can now connect to the supervisor by entering the URI displayed in the supervisor's output.

4. Run one of the demo apps. The apps can be launched from the **Apps Launcher Service** tab.

To get started with your own app, follow the links in the following table of contents.

.. toctree::
   :maxdepth: 2

   netbeans
   eclipse
   sdk-package 
