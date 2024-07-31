================
MP Services Demo
================

.. contents:: Table of contents
    :local:

The MP services demo uses three applications, that can be started from different shells:

- MPSpaceDemo
- MPGroundDemo
- CTT

*MPSpaceDemo* is an App in space segment, it provides MP services.

*MPGroundDemo* is an App in ground segment, it connects to and configures MPSpaceDemo with MP definitions, such as requests and activities.

*CTT* is Consumer Test Tool, it connects to MP services provided by MPSpaceDemo.

*<nmf_src>* is the root of NMF installation.

In case you need to build the source, use the following commands:

	$ cd <nmf_src>

	$ mvn clean install

Start MPSpaceDemo
-----------------
	$ cd <nmf_src>/sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/home/mp-demo

	$ ./start_mp_space_demo.sh

Start CTT
---------

	$ cd <nmf_src>/sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/home/nmf/consumer-test-tool

	$ ./consumer-test-tool.sh

Connect CTT to MPSpaceDemo. Press the “Fetch Infromation” button. The “App: mp-demo” lists services in a table. There are the four MP services. MPSpaceDemo does not implement any MC services.

In CTT press the “Connect to Selected Provider” button. An “App: mp-demo” tab opens.

Go to “Plan Distribution service” tab. MPSpaceDemo created an empty plan. 

Go to “Archive Manager” tab and press “Get All”. It shows the COM objects that were created with a new Plan (“MP – PlanDistribution:” objects).

Go to “Plan Information Management service” tab. Press the listRequestDefs button: there are no definitions. Similarly for listActivityDefs, etc. The Definitions will be loaded by MPGroundDemo.

Start MPGroundDemo
------------------
	$ cd <nmf_src>/sdk/sdk-package/target/nmf-sdk-2.0.0-SNAPSHOT/home/mp-demo

	$ ./ start_mp_ground_demo.sh

MPGroundTest executes  hard-coded operation calls that configure MPSpaceDemo App with Request Templates and Activity / Event / Resource Definitions.

In CTT
------
On “Plan Information Management service” tab press listRequestDefs. A Request definition appears, that was added by MPGroundDemo. Its Definition ID is “1”. Similarly for listActivityDefs, etc.

Go to “Planning Request service” tab. Press submitPlanningRequest. An “Identifier” box opens. Identifier is the first argument in submitRequest operation. Click Submit. RequestVersionDetails box opens, it is the second argument of submitRequest. Make sure the “template” is “1”, it is the RequestTemplate from Plan Information Management service (listRequestDefs). Click Submit.  The added Request shows in the table.

The “Archive Manager” tab (press “Get All”) shows PlanningReqeust COM objects that were created as part of submitRequest.

The “Event Service” tabs show the events fired by COM Archive. Look for the Planning Request events at the end of table. The configuration object RequestVersionToRequestStatusUpdate is updated, but in COM Archive it is implmemented as ObjectDeleted and ObjectStored.
