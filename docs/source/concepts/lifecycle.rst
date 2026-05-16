=================
NMF App Lifecycle
=================

.. contents:: Table of contents
   :local:

This section describes how an NMF App moves from "not running" to "running"
and back, and the role the Supervisor plays in each transition.

Observable state
----------------

The NMF does not define a formal multi-state lifecycle enumeration for apps.
From the perspective of the ``AppsLauncher`` service, an app's state is
binary:

- ``running = false`` — the Supervisor has registered the app's metadata but
  has not spawned its process.
- ``running = true`` — the Supervisor has spawned the app's process and it
  has registered with the Directory Service.

This boolean is exposed on every ``AppDetails`` entry returned by the
``listApp`` operation, and changes are emitted as events via the COM Event
service.

.. mermaid::

    stateDiagram-v2
        [*] --> Stopped
        Stopped --> Running: runApp
        Running --> Stopped: stopApp (graceful)
        Running --> Stopped: killApp (forced)

Operations
----------

The ``AppsLauncher`` service exposes the following operations:

- **runApp** (Submit) — start a registered app. Returns once the Supervisor
  has accepted the request and started the process; the resulting
  ``running`` transition is observed via the Directory Service registration
  of the app.
- **stopApp** (Progress) — graceful shutdown. Multi-stage: the Supervisor
  signals the app to terminate, waits for the app to release its services
  and exit, and reports each stage back to the consumer.
- **killApp** (Submit) — forced termination. Used when an app has become
  unresponsive and ``stopApp`` cannot complete.
- **listApp** (Request) — returns the ``AppDetails`` for all registered apps,
  including each app's ``running`` flag.
- **monitorExecution** (PubSub) — subscribe to an app's stdout stream so the
  consumer receives execution output in real time.

Running an app
^^^^^^^^^^^^^^

A typical run sequence:

.. mermaid::

    sequenceDiagram
        autonumber
        participant C as Consumer (CTT)
        participant S as Supervisor (AppsLauncher)
        participant A as App process
        participant D as Directory Service
        C->>S: runApp(appId)
        S->>A: spawn JVM
        A->>D: register services
        A-->>S: started
        S-->>C: SUBMIT ACK
        Note over A,D: App is now discoverable

Stopping an app
^^^^^^^^^^^^^^^

``stopApp`` is a Progress operation, so the consumer receives multiple
updates rather than a single ack:

.. mermaid::

    sequenceDiagram
        autonumber
        participant C as Consumer (CTT)
        participant S as Supervisor (AppsLauncher)
        participant A as App process
        C->>S: stopApp(appId)
        S-->>C: PROGRESS ACK
        S->>A: stop signal
        A->>A: release services
        A-->>S: stopped
        S-->>C: PROGRESS UPDATE
        S-->>C: PROGRESS RESPONSE (final)

If the app does not exit within the configured timeout, the consumer may
follow up with ``killApp`` to force termination.

Liveness via Heartbeat
----------------------

The SM ``Heartbeat`` service provides an independent liveness signal. A
provider (typically the Supervisor or an individual app) periodically
publishes a heartbeat message that subscribers can use to detect
unresponsive services. ``Heartbeat`` is a full MO service in its own right
(see :doc:`mo-architecture`), not merely a configuration flag.

Service discovery
-----------------

When an app starts, it locates the Supervisor's services through the
**Directory Service**. The same mechanism lets consumers find apps and lets
apps find one another (the foundation for the *app chaining* pattern
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
