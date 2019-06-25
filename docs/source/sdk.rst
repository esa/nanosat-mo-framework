SDK
===
The SDK provides basic functionality to develop and test your space apps our ground applications. The SDK is located is the **sdk/** folder of the repository. 

The SDK mainly consists of:

* Ground application examples
* Space app examples
* Consumer Test Tool (CTT)
* Documentation (will be exported to RTD)
* Release package

To build the SDK you can just run ``mvn install`` in the **sdk/** directory. This will build all examples and package them into a release zip which you can find under **sdk/sdk-package/target/**.

Consumer Test Tool (CTT)
------------------------
The Consumer Test Tool (CTT) is your best friend when manually verifying your app. You can use the CTT to connect to the supervisor, launch apps and then connect to your apps and interact with them.
How to do all of that is described further in the next section.

Running the SDK
---------------
1. To run the supervisor, go to **sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/bin/space/nanosat-mo-supervisor-sim/** and run **nanosat-mo-supervisor-sim.sh**.

2. To run the CTT, go to **sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/bin/tools/consumer-test-tool/** and run **consumer-test-tool.sh**.

Connecting to the supervisor in the CTT
---------------------------------------
The supervisor outputs a URI on the console. This URI follows the pattern 
``maltcp://SOME_ADDRESS:1024/nanosat-mo-supervisor-Directory``
Paste this URI into the field in the **Communication Settings** tab of the CTT and click the button **Fetch information**. In the *Providers List*, the supervisor should show up. The table on the right side should list some services. Now click the button **Connect to Selected Provider** which results in a new tab appearing next to the **Communication Settings**. You now have a working connection to the supervisor and are able to start apps and check messages.

Running and connecting to an app
--------------------------------
The nanosat-mo-supervisor tab offers you several sub-tabs. One of these tabs controls the **Apps Launcher Service**. By selecting this tab, you see a list of every app that is currently registered to your supervisor.
Select the app you want to launch (e.g. camera in the default package) and click the button **runApp**. All output produced by the app is printed in the **Apps Launcher Service** tab.
When you return to the **Communication Settings** tab and refresh your *Providers List* by selecting **Fetch Information**, your app should appear. You can connect to it in the same way as you did for the supervisor.

To get started with your own app, follow the upcoming links.

.. toctree::
   :maxdepth: 1

   netbeans
   eclipse
   sdk-package 
