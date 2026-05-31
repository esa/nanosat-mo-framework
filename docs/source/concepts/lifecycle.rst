=================
NMF App Lifecycle
=================

.. contents:: Table of contents
   :local:

This section describes how an NMF App moves from "not running" to "running" and back, and the role the
Supervisor plays in each transition.

Observable state
----------------

The ``AppsLauncher`` service exposes a structured lifecycle through the ``monitorEvents`` PubSub operation.
Each event carries an ``AppEventType`` value, an optional ``exitCode``, and an optional ``extraInfo`` string.

.. list-table:: AppEventType values
   :header-rows: 1
   :widths: 25 55 20

   * - Value
     - When it fires
     - exitCode present?
   * - ``START_REQUESTED``
     - The Supervisor received a ``runApp`` call and is about to spawn the process.
     - No
   * - ``STARTED``
     - The process has been spawned and is running.
     - No
   * - ``STOP_REQUESTED``
     - The Supervisor received a ``stopApp`` call and has signalled the app to shut down gracefully.
     - No
   * - ``STOPPED``
     - The app process ended after a graceful stop request.
     - Yes
   * - ``KILLED``
     - The app process was forcibly terminated by ``killApp``.
     - Yes
   * - ``EXITED``
     - The app self-terminated with exit code 0.
     - Yes (0)
   * - ``CRASHED``
     - The app self-terminated with a non-zero exit code.
     - Yes (≠ 0)

Lifecycle diagram
-----------------

.. mermaid::

    flowchart TD
        idle([Idle])
        starting([Starting...])
        running([Running])
        stopping([Stopping...])

        idle      -->|"START_REQUESTED\n(runApp received)"| starting
        starting  -->|"STARTED\n(process alive)"| running
        running   -->|"STOP_REQUESTED\n(stopApp received)"| stopping
        stopping  -->|"STOPPED\n(exit code)"| idle
        running   -->|"KILLED\n(exit code)"| idle
        running   -->|"EXITED\n(exit code = 0)"| idle
        running   -->|"CRASHED\n(exit code ≠ 0)"| idle

The ``running`` boolean on ``AppDetails`` (returned by ``listApp``) still tracks the binary
not-running / running distinction. ``monitorEvents`` provides the finer-grained view above.

Operations
----------

The ``AppsLauncher`` service exposes the following operations:

- **runApp** (Submit) — start a registered app. Fires ``START_REQUESTED`` then ``STARTED`` on
  ``monitorEvents`` as the process lifecycle progresses.
- **stopApp** (Progress) — graceful shutdown. Fires ``STOP_REQUESTED`` on acknowledgement and
  ``STOPPED`` once the process exits.
- **killApp** (Submit) — forced termination. Used when an app has become unresponsive. Fires
  ``KILLED`` once the process exits.
- **listApp** (Request) — returns the ``AppDetails`` for all registered apps, including each
  app's ``running`` flag.
- **monitorExecution** (PubSub) — subscribe to an app's stdout/stderr stream in real time.
- **monitorEvents** (PubSub) — subscribe to app lifecycle events. Subscription key: ``appName``
  and/or ``appId``; use the wildcard to receive events for all apps.

Running an app
^^^^^^^^^^^^^^

.. mermaid::

    sequenceDiagram
        autonumber
        participant C as Consumer
        participant S as Supervisor (AppsLauncher)
        participant A as App process
        participant D as Directory Service
        C->>S: runApp(appId)
        S-->>C: monitorEvents NOTIFY: START_REQUESTED
        S->>A: spawn JVM
        A->>D: register services
        S-->>C: monitorEvents NOTIFY: STARTED
        Note over A,D: App is now discoverable

Stopping an app
^^^^^^^^^^^^^^^

``stopApp`` is a Progress operation. The consumer receives interaction-pattern updates (ACK,
RESPONSE) from the INVOKE as well as a ``monitorEvents`` notification when the process actually
exits:

.. mermaid::

    sequenceDiagram
        autonumber
        participant C as Consumer
        participant S as Supervisor (AppsLauncher)
        participant A as App process
        C->>S: stopApp(appId)
        S-->>C: PROGRESS ACK
        S-->>C: monitorEvents NOTIFY: STOP_REQUESTED
        S->>A: stop signal
        A->>A: release services, exit
        S-->>C: monitorEvents NOTIFY: STOPPED (exit code)
        S-->>C: PROGRESS RESPONSE (final)

If the app does not exit within the configured timeout, the consumer may follow up with
``killApp`` to force termination:

.. mermaid::

    sequenceDiagram
        autonumber
        participant C as Consumer
        participant S as Supervisor (AppsLauncher)
        participant A as App process
        C->>S: killApp(appId)
        S->>A: SIGKILL
        S-->>C: monitorEvents NOTIFY: KILLED (exit code)

Self-termination
^^^^^^^^^^^^^^^^

When an app exits on its own (without an explicit ``stopApp`` or ``killApp``), the Supervisor
classifies the exit by exit code:

.. mermaid::

    sequenceDiagram
        autonumber
        participant S as Supervisor (AppsLauncher)
        participant A as App process
        participant C as Consumer
        A->>A: System.exit(0)
        S-->>C: monitorEvents NOTIFY: EXITED (exit code = 0)

        A->>A: crash / System.exit(N≠0)
        S-->>C: monitorEvents NOTIFY: CRASHED (exit code ≠ 0)

Liveness via Heartbeat
----------------------

The SM ``Heartbeat`` service provides an independent liveness signal. A provider (typically the Supervisor or
an individual app) periodically publishes a heartbeat message that subscribers can use to detect unresponsive
services. ``Heartbeat`` is a full MO service in its own right (see :doc:`mo-architecture`), not merely a
configuration flag.

Service discovery
-----------------

When an app starts, it locates the Supervisor's services through the **Directory Service**. The same mechanism
lets consumers find apps and lets apps find one another (the foundation for the *app chaining* pattern
described in :doc:`apps-and-supervisor`).

.. mermaid::

    sequenceDiagram
        autonumber
        participant A as App (Connector)
        participant D as Directory Service
        participant P as Supervisor.GPS service
        A->>D: lookup(service=GPS, domain=...)
        D-->>A: GPS provider URI
        A->>P: getPosition()
        P-->>A: position
