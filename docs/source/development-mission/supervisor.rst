==================================
Implementing the NMF Supervisor
==================================

.. contents:: Table of contents
   :local:

The ``NanoSatMOSupervisor`` abstract class in ``core/nmf-composites`` is the base for every mission-specific
Supervisor. It handles all the generic NMF machinery — COM services, MC services, Heartbeat, Apps Launcher,
Package Management, Command Executor, and the Central Directory service. The mission subclass supplies only
what is unique to its hardware: the Platform service adapters.

The ``init`` method
-------------------

The standard entry point is::

    public void init(MonitorAndControlNMFAdapter mcAdapter,
                     PlatformServicesConsumer platformServices,
                     PMBackend packageManagementBackend)

Most missions call through to this from a one-argument override::

    @Override
    public void init(MonitorAndControlNMFAdapter mcAdapter) {
        init(mcAdapter,
             new PlatformServicesConsumer(),
             new NMFPackagePMBackend("packages", this.getAppsLauncherService()));
    }

The ``MCSupervisorBasicAdapter`` bundled in ``nmf-composites`` is a ready-to-use MC adapter that exposes basic
Supervisor health parameters and actions. Pass an instance of it (after calling
``adapter.setNmfSupervisor(supervisor)``) unless the mission needs custom Supervisor-level parameters or
actions.

The ``initPlatformServices`` hook
----------------------------------

``NanoSatMOSupervisor`` calls this abstract method once the COM stack is up, before returning from ``init``::

    public abstract void initPlatformServices(COMServicesProvider comServices);

This is where the mission instantiates and initialises its platform services provider. The pattern used by the
simulator is:

.. code-block:: java

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        // 1. Create and initialise the provider side (hardware adapters)
        PlatformServicesProviderInterface provider = new MyMissionPlatformProvider();
        provider.init(comServices);

        // 2. Connect the consumer loopback so that NMF Apps can reach the services
        ConnectionConsumer conn = new ConnectionConsumer();
        conn.setServicesDetails(ConnectionProvider.getGlobalProvidersDetailsPrimary());
        super.getPlatformServices().init(conn, null);
    }

The ``super.getPlatformServices()`` consumer is what the ``NanoSatMOConnectorImpl`` inside each NMF App
ultimately talks to.

Minimal mission: Barebone Supervisor
--------------------------------------

The ``nmf-mission-barebone`` module contains the simplest possible mission — one that deliberately omits all
Platform services:

.. code-block:: java

    public class BareboneSupervisorImpl extends NanoSatMOSupervisor {

        public static void main(final String[] args) throws Exception {
            BareboneSupervisorImpl supervisor = new BareboneSupervisorImpl();
            MCSupervisorBasicAdapter adapter = new MCSupervisorBasicAdapter();
            adapter.setNmfSupervisor(supervisor);
            supervisor.init(adapter);
        }

        @Override
        public void init(MonitorAndControlNMFAdapter mcAdapter) {
            init(mcAdapter,
                 new PlatformServicesConsumer(),
                 new NMFPackagePMBackend("packages", this.getAppsLauncherService()));
        }

        @Override
        public void initPlatformServices(COMServicesProvider comServices) {
            // No platform services — apps cannot access hardware
        }
    }

This is a valid, runnable Supervisor. Apps deployed on top of it can use MC services (Parameters, Actions,
Alerts, Aggregations) but will get a ``null`` platform service stub if they call
``getNMFProvider().getPlatformServices()``.

Default MC set
--------------

Every NMF Supervisor exposes a **default set** of Monitor & Control parameters and actions, present
regardless of the mission. This is a contract: ground tooling and cross-mission software can rely on these
existing on any Supervisor. The mission's own ``MonitorAndControlNMFAdapter`` is composed *on top* of the
default set — both coexist.

The composition is transparent: ``NanoSatMOSupervisor.init`` wraps the mission adapter together with the
default adapters (from ``DefaultSupervisorAdapters``) in a ``CompositeMCAdapter``, which the MC services see
as a single listener and which forwards each callback to its children. Missions write their adapter as
usual; nothing extra is required, and the default set appears even when no mission adapter is supplied.

The current default parameters (both read-only) are:

.. list-table::
   :header-rows: 1
   :widths: 30 70

   * - Parameter
     - Meaning
   * - ``nmf.version``
     - The version of the NMF framework the Supervisor runs.
   * - ``nmf.uptime``
     - The uptime of the Supervisor process, in seconds.
   * - ``memory.ram.*``, ``memory.swap.*``
     - Host RAM and swap totals, usage and percentages.
   * - ``bootloader.{primary,secondary,factory}.{nmf-version,mission-version,java,main-class}``
     - The fields of the three software baseline files the NMF Bootloader selects between.
   * - ``bootloader.rung``, ``bootloader.failed-attempts``
     - The fallback ladder rung the bootloader will boot next, and the failed-attempt count for it.

The memory parameters are sourced from Linux ``/proc``; values unavailable on the host default
to zero. The ``bootloader.*`` parameters are read live from the ``bootloader/`` domain (the baseline files
and runtime state) that the bootloader itself consumes; see the :doc:`/background/bootloader-specification`.

Bootloader baseline commanding
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The bootloader parameters are read-only; the **primary** baseline is commanded through an action instead,
so the request can be validated before acceptance and its progress reported:

- ``bootloader.setPrimaryBaseline(nmf-version, mission-version, java, main-class)`` — sets the baseline the
  bootloader boots next. The action validates, as reported stages, that the requested framework and mission
  versions are installed and pass their ``SHA256SUMS`` integrity tests and that the Java runtime executes;
  any failure rejects the whole command and leaves the baseline file untouched. It writes only the
  **primary** baseline.
- ``bootloader.restart()`` — restarts the Supervisor. It acknowledges, then exits with the restart code,
  which the bootloader recognises to re-boot from the current primary baseline. It is generic: applying a
  newly activated baseline is one use (``setPrimaryBaseline`` then ``restart``), but the restart carries no
  update-specific logic.

The **secondary** baseline is not operator-settable — it is written only by the NMF itself. The bootloader
*promotes* the running baseline into the secondary on each confirmed boot, so the secondary is always the
last known-good baseline (rather than the previously commanded primary, which may never have booted). The
**factory** baseline is immutable in flight. A rollback to the secondary is therefore performed by calling
``setPrimaryBaseline`` with the secondary baseline's field values.

Default parameter and action names follow a **dotted hierarchy** (``domain.group.leaf``), lowercase
segments — for example ``nmf.version`` or ``bootloader.primary.nmf-version``. A default capability is added
by writing a small name-based adapter and registering it in ``DefaultSupervisorAdapters.create()``; it then
appears on every Supervisor automatically.

``main`` class and startup script
------------------------------------

The Supervisor's ``main`` class must be passed to the ``nmf-linux-maven-plugin`` as the
``supervisorMainClass`` parameter (see :doc:`filesystem`). The plugin generates a startup script that invokes
the class by name.

Preventing root execution
--------------------------

``NanoSatMOSupervisor.init`` throws a ``RuntimeException`` if the JVM user is ``root``. This is intentional —
running the Supervisor as root on a spacecraft is a security risk. The deployment must create a dedicated
non-root user and launch the Supervisor under that account.
