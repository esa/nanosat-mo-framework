========
Glossary
========

.. glossary::
   :sorted:

   Adapter
      A class that implements one of the NMF Platform service adapter
      interfaces, forwarding service operations to either a simulator
      or real hardware.

   App
      A JVM process that exposes MO services and is managed by an NMF
      Supervisor. See :doc:`../concepts/apps-and-supervisor`.

   App chaining
      A pattern in which one app consumes another app's services so
      that one app's output drives the next's behaviour. Also called
      *Cascading NMF Apps* in earlier documentation.

   CCSDS
      Consultative Committee for Space Data Systems. Publishes the
      space-data standards on which MO is built.

   COM
      Common Object Model. The MO area that provides Event, Archive,
      ArchiveSync, Directory, Login, and Configuration services.

   COM Archive
      Persistent storage of COM objects, queryable through the COM
      Archive service. Backed by SQLite in the default NMF
      deployment.

   Connector
      The in-process gateway between an NMF App and its Supervisor.
      Class: ``NanoSatMOConnectorImpl``.

   CTT
      Consumer Test Tool. The GUI for manual interaction with NMF
      providers. See :doc:`../tooling/ctt`.

   Directory Service
      The COM service through which providers register their service
      URIs and consumers discover them.

   Ground MO Adapter
      The class (``GroundMOAdapterImpl``) used by ground applications
      to connect to a remote provider.

   Ground MO Proxy
      A bridge between MALTCP on the ground and MALSPP over a space
      link. See :doc:`../ground-development/ground-mo-proxy`.

   MAL
      Message Abstraction Layer. The lowest CCSDS MO layer; defines
      interaction patterns, data types, and transport bindings.

   MC
      Monitor & Control. The MO area for telemetry parameters, action
      invocation, aggregations, alerts, and conversions.

   Monolithic provider
      A self-contained provider composite (``NanoSatMOMonolithic``)
      that exposes Supervisor-level services and a custom MC adapter
      from a single process. Not an NMF App — there is no Apps
      Launcher hosting multiple coexisting Apps. Used for standalone
      testing and self-contained demonstrations.

   NMF Package
      The deployment unit for NMF software. A ``.nmfpackage`` ZIP
      archive. See :doc:`../concepts/packages`.

   OPS-SAT
      An ESA CubeSat mission, launched 2019, on which NMF was first
      flown.

   ɸ-Sat-2
      An ESA mission combining a multispectral imaging payload with
      on-board AI inference, hosting NMF apps.

   Platform services
      The MO area exposing spacecraft platform functions (camera,
      GPS, ADCS, power, etc.) to apps.

   SDK
      Software Development Kit. The set of NMF tools and examples
      shipped together for app developers.

   SM
      Software Management. The MO area for managing apps on the
      spacecraft (AppsLauncher, PackageManagement, Heartbeat,
      CommandExecutor).

   Supervisor
      The process owning the spacecraft-side of the NMF runtime: SM
      and Platform services, Directory Service, app lifecycle.
      Class: ``NanoSatMOSupervisor``.
