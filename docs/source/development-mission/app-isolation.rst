===============
App Isolation
===============

.. contents:: Table of contents
   :local:

The NMF Supervisor can launch each NMF App under one of several isolation strategies. The chosen strategy is
set once per mission deployment and applies to every app managed by that Supervisor.

Overview
--------

.. list-table::
   :header-rows: 1
   :widths: 18 20 18 18 26

   * - Mode
     - Filesystem isolation
     - Process isolation
     - Extra dependencies
     - Use cases
   * - ``none``
     - No
     - No
     - None
     - Development and the SDK simulator
   * - ``linux-userspace``
     - Yes (per-user)
     - No
     - ``sudo``, ``adduser``
     - OBC images that cannot take extra packages
   * - ``bubblewrap``
     - Yes (read-only host)
     - Yes (namespaces)
     - ``bwrap``
     - OBCs where ``bwrap`` can be installed
   * - ``docker-containers``
     - Yes (container)
     - Yes (container)
     - Docker daemon
     - Missions already running Docker, or apps needing different Java versions

How isolation is configured
----------------------------

The isolation mode is selected via the ``appsIsolation`` parameter of the ``nmf-linux-maven-plugin``. The
plugin bakes the chosen value into the Supervisor startup script as the JVM system property
``esa.mo.nmf.packagemanager.appsIsolation``. The Supervisor reads this property at startup and selects the
corresponding ``AppsLauncherManager`` implementation.

Add the parameter inside the plugin's ``<configuration>`` block in the mission filesystem module (see
:doc:`filesystem`):

.. code-block:: xml

    <configuration>
      <supervisorMainClass>esa.mo.nmf.mission.example.MySupervisor</supervisorMainClass>
      <appsIsolation>linux-userspace</appsIsolation>
    </configuration>

If ``appsIsolation`` is omitted the default value is ``none``.

.. note::

   The isolation mode applies on Linux. When the Supervisor runs on Windows it always uses the Windows app
   launcher, whatever ``appsIsolation`` is set to.

Isolation mode: none
--------------------

All NMF Apps run as the same OS user that launched the Supervisor. No additional OS-level separation is
applied.

This is the default and is appropriate for development and for running the SDK simulator. It is not
recommended for a production spacecraft because a misbehaving or compromised app can read and write the files
of every other app.

Isolation mode: linux-userspace
-------------------------------

Each NMF App runs under a dedicated Linux user account. The account is created automatically by the Package
Management service when the ``*.nmfpackage`` file is installed; the username is derived from the app name.

The Supervisor launches each app via ``sudo su - <appuser> -c <start-script>``, which gives each app a
separate home directory and file-permission boundary enforced by the OS kernel.

**Requirements:**

- The Supervisor user must have permission to call ``sudo adduser`` (or ``useradd`` on BusyBox-based systems)
  and ``sudo su``. The recommended approach is a ``sudoers`` rule scoped to those two commands for the
  Supervisor account only — not a blanket ``NOPASSWD: ALL``.
- The OS must have ``sudo`` and ``su`` available (standard on any full Linux distribution).

This mode is the recommended choice for production missions on Linux hardware. It provides strong per-app
filesystem isolation with no additional runtime dependencies beyond a standard Linux userland.

Isolation mode: bubblewrap
--------------------------

Each NMF App runs inside a bubblewrap (``bwrap``) sandbox. The sandbox configuration applied by the NMF is:

- The full host filesystem is mounted read-only inside the sandbox.
- The app's working directory and its log directory are bind-mounted read-write.
- ``/tmp`` is a private ``tmpfs``.
- All Linux namespaces except the network namespace are unshared (``--unshare-all --share-net``).
- The sandbox inherits only ``JAVA_OPTS`` and a minimal ``PATH`` from the Supervisor; all other environment
  variables are cleared.
- The sandbox process is linked to the Supervisor via ``--die-with-parent``: if the Supervisor is killed the
  sandboxed app is killed with it.

**Requirements:**

- ``/usr/bin/bwrap`` must be present and executable on the OBC. On Debian/Ubuntu: ``apt-get install
  bubblewrap``.
- The Supervisor user must have permission to execute ``bwrap``. On kernels with user-namespace support this
  is typically available without any special privilege.

Bubblewrap provides stronger isolation than ``linux-userspace`` because it also restricts namespace
visibility. The trade-off is the dependency on ``bwrap`` being available on the target hardware.

Isolation mode: docker-containers
---------------------------------

Each NMF App runs inside its own Docker container, started by the Supervisor with ``docker run``. The
container configuration applied by the NMF is:

- The container is named ``nmf-app-<appname>`` and is removed once the app exits (``--rm``).
- The NMF home directory is bind-mounted at the same absolute path inside the container, so the app's start
  script finds the shared JARs and its log directory unchanged.
- The container joins the Docker network configured below (``host`` by default) so that the app can reach the
  Supervisor's Directory service.
- The Directory service URI is passed into the container through ``JAVA_OPTS``.
- Stopping an app stops its container, rather than signalling a process.

**Configuration:**

- ``esa.mo.nmf.packagemanager.docker.image`` — the image the apps run in. Defaults to
  ``eclipse-temurin:21-jre``. It must provide a JRE the apps can run on and a POSIX shell.
- ``esa.mo.nmf.packagemanager.docker.network`` — the Docker network the containers join. Defaults to
  ``host``.

**Requirements:**

- The ``docker`` command must be on the Supervisor's ``PATH``.
- The Supervisor user must be allowed to talk to the Docker daemon, which usually means membership of the
  ``docker`` group. Note that this is equivalent to root on the host, so it weakens the boundary between the
  Supervisor and the OS even as it strengthens the one between the apps.

Docker containers isolate the filesystem and the process tree, and additionally pin the runtime that each app
sees, which none of the other modes do. The trade-offs are the Docker daemon as a runtime dependency and the
privilege that access to it carries.
