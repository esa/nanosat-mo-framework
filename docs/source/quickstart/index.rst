===========
Quick Start
===========

.. contents:: Table of contents
   :local:

This guide takes you from a fresh checkout to a running NMF App connected to a Consumer Test Tool session.

Prerequisites
-------------

- **Java 11** or compatible. Verify with ``java -version``.
- **Apache Maven 3.6** or newer. Verify with ``mvn -version``.
- An internet connection for the initial dependency download.

Get the source
--------------

Clone the repository:

.. code-block:: bash

   git clone https://github.com/esa/nanosat-mo-framework.git
   cd nanosat-mo-framework

The stable branch is ``master``; active development takes place on version branches such as ``v5.0``.

Build
-----

From the repository root, run:

.. code-block:: bash

   mvn install

This compiles the framework, generates the MO Java APIs from the XML service definitions, and assembles the
SDK Playground Environment. The first build downloads dependencies and takes a few minutes.

For a faster intermediate build that skips Javadoc and the slow assembly step:

.. code-block:: bash

   mvn install -Dmaven.javadoc.skip=true -Desa.nmf.sdk.assembly.quickbuild=true

.. note::

   Javadoc generation produces non-breaking warnings about missing module
   descriptors. These are expected and may be ignored.

Run the SDK Playground Environment
----------------------------------

After the build completes, the SDK Playground Environment is available under
``sdk/sdk-playground-environment/``.

Start the Supervisor with simulator
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

The Supervisor with simulator combines a Supervisor process with an in-process spacecraft simulator, so
Platform services return realistic data without requiring real hardware:

.. code-block:: bash

   sdk/sdk-playground-environment/run_Supervisor.sh

On startup, the Supervisor prints its Directory Service URI to the console, in the form:

.. code-block:: text

   maltcp://<host>:<port>/nanosat-mo-supervisor-Directory

Keep this URI to hand — the CTT needs it to connect.

Start the Consumer Test Tool
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

In a second terminal:

.. code-block:: bash

   sdk/sdk-playground-environment/run_CTT.sh

The CTT window opens.

Connect to the Supervisor
^^^^^^^^^^^^^^^^^^^^^^^^^

In the CTT:

1. Paste the Supervisor's Directory Service URI into the field on the **Communication Settings** tab.
2. Click **Fetch Information**. The Supervisor appears in the *Providers List* and its services appear in the
   table on the right.
3. Click **Connect to Selected Provider**. A new tab opens for the Supervisor with sub-tabs for each service
   it exposes.

Start an example app
^^^^^^^^^^^^^^^^^^^^

1. In the Supervisor tab, open the **Apps Launcher Service** sub-tab.
2. Select an example app such as ``hello-world-simple``.
3. Click **runApp**.

The Supervisor spawns the app process and the app's output appears in the Apps Launcher tab. Returning to
**Communication Settings** and clicking **Fetch Information** refreshes the providers list; the app now
appears and can be connected to in the same way as the Supervisor.

Next steps
----------

You now have a running NMF deployment.

- For the architectural model behind what you just ran, read :doc:`../concepts/index`.
- To develop your own app, see the App Development Guide.
- To consume an app from your own ground software, see the Ground Software Development Guide.
- For deeper coverage of the Supervisor, simulator, CTT, and CLI tools, see the Tooling section.
