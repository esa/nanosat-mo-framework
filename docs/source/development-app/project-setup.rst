=============
Project setup
=============

.. contents:: Table of contents
   :local:

The recommended way to start a new NMF App is to copy an existing example from ``sdk/examples-space/`` whose
shape resembles the intended application, then rename the project and adjust its POM.

Copy and rename an example
--------------------------

1. Identify an example whose feature set is closest to the target app (e.g. ``hello-world-simple`` for a
   minimal app, ``camera`` for an app that drives the Platform camera service, ``all-mc-services`` for a broad
   demonstration of Monitor & Control).
2. Copy the example folder under ``sdk/examples-space/`` and rename it to match the new app, for example
   ``my-app``.
3. In the copy's ``pom.xml``:

   - Change ``artifactId`` from the example's value to ``my-app``.
   - Update ``name`` and ``description`` to describe the new app.
   - Update the ``author`` tag.

4. Rename the Java classes and update their package declarations to match the new app.

Project layout
--------------

An NMF App project follows the standard Maven layout. A typical app contains:

.. code-block:: text

   my-app/
     pom.xml
     src/main/java/
       <package>/
         MyApp.java              ← main class
         MyAppAdapter.java       ← Monitor & Control adapter

The **main class** instantiates the ``NanoSatMOConnectorImpl``, hands it the M&C adapter, and lets the
Supervisor manage the rest of the lifecycle. The **adapter class** exposes parameters and actions through
either the listener-interface API or the annotation API; see :doc:`monitor-and-control`.

Two classes are recommended over one because they have distinct responsibilities: the main class is
responsible for instantiation and wiring to the NMF, while the adapter is responsible for exposing the app's
services. Apps with substantial logic typically push their domain logic into additional classes invoked from
the adapter.

The Connector
-------------

The ``NanoSatMOConnectorImpl`` is the single dependency the main class needs. In the main class:

.. code-block:: java

   public class MyApp {
       private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();

       public MyApp() {
           connector.init(new MyAppAdapter());
       }

       public static void main(final String[] args) {
           new MyApp();
       }
   }

``connector.init(adapter)`` registers the adapter's parameters, actions, alerts, and aggregations with the
Supervisor's Directory Service. The app is now reachable by any consumer that queries the Directory Service.

To access Platform services from inside the adapter, the adapter must hold a reference to the connector. The
pattern used in the SDK examples is to construct the adapter first, then pass the connector to it via a setter
or constructor argument:

.. code-block:: java

   public MyApp() {
       MyAppAdapter adapter = new MyAppAdapter();
       adapter.setNMF(connector);
       connector.init(adapter);
   }

From the adapter, Platform services are reached via ``connector.getPlatformServices()`` — see
:doc:`platform-services`.
