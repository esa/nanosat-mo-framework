==========================
The Ground MO Adapter
==========================

.. contents:: Table of contents
   :local:

The ``GroundMOAdapterImpl`` is the primary entry point for ground
applications. It encapsulates connections to a remote provider (an NMF
App or the Supervisor) and exposes typed accessors for that provider's
MO services.

Connecting to a provider
------------------------

Two factory methods are commonly used:

- ``GroundMOAdapterImpl.forApp(directoryURI, appName)`` — connects to
  a named NMF App registered in the given Directory Service.
- ``GroundMOAdapterImpl.forSupervisor(directoryURI)`` — connects to the
  Supervisor at the given Directory Service URI.

Example:

.. code-block:: java

   String directoryURI = System.getProperty("esa.mo.nmf.centralDirectoryURI");
   GroundMOAdapterImpl gma = GroundMOAdapterImpl.forApp(directoryURI, "my-app");

After the call returns, the adapter has resolved the provider's
service URIs through the Directory Service and is ready to issue
operations.

Accessing service consumer stubs
--------------------------------

The adapter exposes typed accessors for each service category:

- ``gma.getMCServices()`` — Monitor & Control (Parameter, Action,
  Alert, Aggregation, Conversion).
- ``gma.getCOMServices()`` — Common Object Model (Archive, Event,
  Directory, etc.).
- ``gma.getSMServices()`` — Software Management (AppsLauncher,
  PackageManagement, Heartbeat, CommandExecutor).

Each accessor returns the relevant service stub on which operations
can be invoked.

Releasing resources
-------------------

When a ground application no longer needs a remote connection, call
``gma.closeConnections()`` to release transport resources. In
short-lived ground applications this happens implicitly at JVM exit;
long-lived applications should manage the lifecycle explicitly.

The CTT
-------

The Consumer Test Tool uses ``GroundMOAdapterImpl`` internally for
every provider it connects to. The patterns documented here are the
same ones the CTT uses; a custom ground application is essentially a
scripted CTT.
