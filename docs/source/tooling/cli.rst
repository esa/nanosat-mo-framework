========
CLI Tool
========

.. contents:: Table of contents
   :local:

The NMF CLI Tool is a command-line consumer for headless interaction with the COM Archive, MC services,
Platform services, and Software Management services. It complements the GUI :doc:`ctt` for scripting,
automation, and quick one-off queries.

The CLI is delivered with the SDK under ``sdk/cli-tool`` and is invoked as:

.. code-block:: bash

   esa.mo.nmf.clitool.CLITool <category> <command> [options]

Command categories
------------------

The CLI groups commands by service area:

- **archive** — query and back up a local or remote COM Archive.
- **log** — list apps with archived logs; export an app's logs to a file.
- **parameter** — subscribe to live updates, enable/disable reporting, list definitions, and dump historical
  samples from the archive.
- **aggregation** — subscribe to live updates, enable/disable reporting.
- **action** — list available actions and trigger them with optional arguments.
- **software-management** — find, install, uninstall, and upgrade NMF packages on a remote provider.
- **gps** — retrieve NMEA sentences from a remote GPS provider.
- **adcs** — query the ADCS provider's status.
- **camera** — request an image from a remote Camera provider.
- **apps-launcher** — subscribe to apps' stdout, run, stop, and kill apps remotely.
- **heartbeat** — subscribe to a provider's heartbeat.

Each command supports either a **local** mode (``-l <database>``) for querying a local SQLite COM Archive
file, or a **remote** mode (``-r <providerURI>``) for connecting to a running provider.

Examples
--------

Dump a local COM Archive to JSON:

.. code-block:: bash

   esa.mo.nmf.clitool.CLITool archive dump \
       -l ../nanosat-mo-supervisor-sim/comArchive.db dump.json

Subscribe to a remote app's stdout:

.. code-block:: bash

   esa.mo.nmf.clitool.CLITool apps-launcher subscribe \
       -r maltcp://host:port/nanosat-mo-supervisor-Directory my-app

Trigger an action on a remote provider:

.. code-block:: bash

   esa.mo.nmf.clitool.CLITool action trigger \
       -r maltcp://host:port/provider-Directory MyAction

Take a picture from a remote Camera provider:

.. code-block:: bash

   esa.mo.nmf.clitool.CLITool camera take-picture \
       -r maltcp://host:port/camera-Directory \
       --resolution 1920x1080 --exposure 0.2 \
       --gain-red 1.0 --gain-green 1.0 --gain-blue 1.0 \
       --output capture

Each command exposes a ``-h`` / ``--help`` flag that prints its full option set. The CLI is the right tool for
shell scripts, scheduled jobs, and any operation where opening the CTT is impractical.
