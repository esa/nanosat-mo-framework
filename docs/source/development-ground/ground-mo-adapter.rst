==========================
The Ground MO Adapter
==========================

.. contents:: Table of contents
   :local:

The ``GroundMOAdapterImpl`` is the primary entry point for ground applications. It encapsulates connections to
a remote provider (an NMF App or the Supervisor) and exposes typed accessors for that provider's MO services.

Connecting to a provider
------------------------

The standard pattern is two steps: look up the Directory Service for the providers registered under it, then
construct a ``GroundMOAdapterImpl`` from the chosen provider.

A ground application typically receives the Directory Service URI as a command-line argument, then:

.. code-block:: java

   ProviderList providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(directoryURI));

   // Pick by name (or take providers.get(0) if there is only one)
   Provider target = providers.stream()
           .filter(p -> "my-app".equals(p.getProviderName().getValue()))
           .findFirst().orElseThrow();

   GroundMOAdapterImpl gma = new GroundMOAdapterImpl(target);

``retrieveProvidersFromDirectory`` queries the Directory Service and returns every provider currently
registered. The constructor then resolves the provider's service URIs so the adapter is ready to issue
operations.

For providers requiring authentication, use the overloaded constructor that takes a ``Blob authenticationId``
and a ``String localNamePrefix``.

Accessing service consumer stubs
--------------------------------

The adapter exposes typed accessors for each service category:

- ``gma.getMCServices()`` — Monitor & Control (Parameter, Action, Alert, Aggregation, Conversion).
- ``gma.getCOMServices()`` — Common Object Model (Archive, Event, Directory, etc.).
- ``gma.getSMServices()`` — Software Management (AppsLauncher, PackageManagement, Heartbeat, CommandExecutor).

Each accessor returns the relevant service stub on which operations can be invoked.

Releasing resources
-------------------

When a ground application no longer needs a remote connection, call ``gma.closeConnections()`` to release
transport resources. In short-lived ground applications this happens implicitly at JVM exit; long-lived
applications should manage the lifecycle explicitly.

The CTT
-------

The Consumer Test Tool uses ``GroundMOAdapterImpl`` internally for every provider it connects to. The patterns
documented here are the same ones the CTT uses; a custom ground application is essentially a scripted CTT.
