=================
Service reference
=================

This page indexes the MO services exposed by the NMF. The XML files under
``core/mo-services-xml/src/main/resources/xml/`` are the source of truth for service definitions, operations,
data types, and documentation strings.

For where each service sits architecturally, see :doc:`../concepts/mo-architecture`.

COM (``area051-COM.xml``)
-------------------------

- **Event** — generic publish/subscribe of COM events.
- **Archive** — persistent storage and querying of COM objects.
- **ArchiveSync** — synchronisation of a COM Archive with a remote provider.
- **Directory** — discovery of services by domain and provider.
- **Login** — authentication of consumers (XML-defined; provider implementation is currently consumer-side
  only).
- **Configuration** — persistence of provider configuration (XML-defined; configuration persistence is
  currently handled by ``esa.mo.reconfigurable.provider.PersistProviderConfiguration``).

Monitor & Control (``area052-Monitor-and-Control.xml``)
-------------------------------------------------------

- **Parameter** — telemetry parameter definition, generation, and subscription.
- **Action** — command invocation with multi-stage progress reporting.
- **Aggregation** — grouping of parameters into reports.
- **Alert** — operational alert generation and distribution.
- **Conversion** — declarative raw-to-engineering conversions.

Software Management (``area053-Software-Management.xml``)
---------------------------------------------------------

- **AppsLauncher** — run, stop, kill, list, and monitor apps.
- **PackageManagement** — install, uninstall, upgrade NMF Packages.
- **Heartbeat** — periodic liveness publication.
- **CommandExecutor** — execute shell commands on the spacecraft host (where permitted).

Platform (``area054-Platform.xml``)
-----------------------------------

For consumer-side usage, see :doc:`../development-app/platform-services`.

- **Camera** (#1)
- **GPS** (#2)
- **AutonomousADCS** (#3)
- **SoftwareDefinedRadio** (#4)
- **OpticalDataReceiver** (#5)
- **PowerControl** (#7)
- **Clock** (#8)
- **ArtificialIntelligence** (#9)
- **FPGA** (#10)

Numbering gaps reflect services that have been retired since earlier NMF versions.
