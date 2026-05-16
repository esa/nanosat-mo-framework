==========================
Consumer Test Tool (CTT)
==========================

.. contents:: Table of contents
   :local:

The Consumer Test Tool (CTT) is the GUI used to interact with any
NMF provider — the Supervisor or any app — for manual testing.

Running the CTT
---------------

After ``mvn install``, start the CTT from the SDK execution
environment:

.. code-block:: bash

   sdk/sdk-execution-environment/target/nmf-sdk-5.0-SNAPSHOT/home/nmf/consumer-test-tool/consumer-test-tool.sh

The CTT window opens with the **Communication Settings** tab selected.

Connecting to a provider
------------------------

1. Paste the provider's **Directory Service URI** into the field on
   the **Communication Settings** tab. For a local Supervisor with
   simulator, this is the URI it prints on startup. For a deployment
   behind a Ground MO Proxy, use the proxy's Directory Service URI.
2. Click **Fetch Information**. The CTT queries the Directory Service
   and populates the *Providers List* with every provider currently
   registered.
3. Select the provider of interest (typically the Supervisor first,
   then specific apps) and click **Connect to Selected Provider**.

A new tab opens for each provider, with sub-tabs for the services it
exposes.

Service interaction
-------------------

Each sub-tab corresponds to one MO service exposed by the provider:

- **Apps Launcher Service** — list apps registered on the Supervisor,
  run them, stop them, and subscribe to their stdout.
- **Parameter Service** — list parameters, read current values,
  subscribe to updates, and set new values.
- **Action Service** — list actions, invoke them with arguments, and
  observe progress for multi-stage actions.
- **Alert Service** — subscribe to alerts published by the provider.
- **Archive Manager** — query the COM Archive for stored objects.
- **Event Service** — subscribe to COM events.
- **Configuration Service** — store and restore provider configurations.

Each tab presents service-specific controls; the layout is consistent
across providers since every provider is described by the same MO XML.

Logs and execution output
-------------------------

When an app is running, its stdout is republished by the Supervisor
through the **AppsLauncher → monitorExecution** PubSub operation.
The Apps Launcher Service tab shows this stream in real time, allowing
log inspection without separate SSH access to the host.

Login
-----

For providers requiring authentication (where the COM Login service is
in use), the CTT presents a login dialog before opening the provider
tab.

Limitations
-----------

The CTT is a manual tool; for automated scripting of consumer
interactions, write a ground application using ``GroundMOAdapterImpl``
or use the :doc:`cli` for headless invocations.
