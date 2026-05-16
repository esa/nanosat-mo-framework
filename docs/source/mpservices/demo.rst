================
MP Services Demo
================

.. contents:: Table of contents
    :local:

The MP services demo consists of three applications, each launched from a separate shell:

- ``MPSpaceDemo``
- ``MPGroundDemo``
- ``CTT``

``MPSpaceDemo`` is an app in the space segment that provides MP services.

``MPGroundDemo`` is an app in the ground segment that connects to ``MPSpaceDemo`` and configures it with MP definitions, such as requests and activities.

``CTT`` is the Consumer Test Tool; it connects to the MP services exposed by ``MPSpaceDemo``.

``<nmf_src>`` refers to the root of the NMF installation throughout this guide.

To build the source if required:

	$ cd <nmf_src>

	$ mvn clean install

Starting MPSpaceDemo
--------------------
	$ cd <nmf_src>/sdk/sdk-execution-environment/target/nmf-sdk-XX.Y/home/mp-demo

	$ ./start_mp_space_demo.sh

Starting the CTT
----------------

	$ cd <nmf_src>/sdk/sdk-execution-environment/target/nmf-sdk-XX.Y/home/nmf/consumer-test-tool

	$ ./consumer-test-tool.sh

Connect the CTT to ``MPSpaceDemo`` and click **Fetch Information**. The ``App: mp-demo`` entry will list the four MP services in a table. ``MPSpaceDemo`` does not implement any MC services.

In the CTT, click **Connect to Selected Provider**. An ``App: mp-demo`` tab will open.

Open the **Plan Distribution service** tab. ``MPSpaceDemo`` will have created an empty plan.

Open the **Archive Manager** tab and click **Get All**. The COM objects created together with the new plan (``MP - PlanDistribution:`` objects) will be displayed.

Open the **Plan Information Management service** tab. Clicking ``listRequestDefs`` will return no definitions; the same applies to ``listActivityDefs`` and the other listing operations. These definitions are populated by ``MPGroundDemo``.

Starting MPGroundDemo
---------------------
	$ cd <nmf_src>/sdk/sdk-execution-environment/target/nmf-sdk-XX.Y/home/mp-demo

	$ ./start_mp_ground_demo.sh

``MPGroundDemo`` executes a set of predefined operation calls that configure the ``MPSpaceDemo`` app with Request Templates and Activity, Event, and Resource definitions.

In the CTT
----------
On the **Plan Information Management service** tab, clicking ``listRequestDefs`` will now return a Request definition with ID ``1``, added by ``MPGroundDemo``. The other listing operations return their corresponding definitions.

On the **Planning Request service** tab, clicking ``submitPlanningRequest`` opens an ``Identifier`` dialog, which is the first argument of the ``submitRequest`` operation. After submitting it, a ``RequestVersionDetails`` dialog opens for the second argument; ensure that ``template`` is set to ``1``, which corresponds to the Request Template registered through the Plan Information Management service (``listRequestDefs``). After submission, the new request will appear in the table.

On the **Archive Manager** tab, clicking **Get All** will display the ``PlanningRequest`` COM objects created as part of ``submitRequest``.

The **Event Service** tabs display the events emitted by the COM Archive. The Planning Request events appear at the end of the table. The ``RequestVersionToRequestStatusUpdate`` configuration object is updated, which is realised in the COM Archive as ``ObjectDeleted`` and ``ObjectStored`` events.
