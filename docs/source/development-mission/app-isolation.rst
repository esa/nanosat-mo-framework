===============
App Isolation
===============

.. contents:: Table of contents
   :local:

The NMF Supervisor can launch each NMF App under one of several
isolation strategies. The chosen strategy is set once per mission
deployment and applies to every app managed by that Supervisor.

How isolation is configured
----------------------------

The isolation mode is selected via the ``appsIsolation`` parameter of
the ``nmf-linux-maven-plugin``. The plugin bakes the chosen value into
the Supervisor startup script as the JVM system property
``esa.mo.nmf.packagemanager.appsIsolation``. The Supervisor reads this
property at startup and selects the corresponding
``AppsLauncherManager`` implementation.

Add the parameter inside the plugin's ``<configuration>`` block in the
mission filesystem module (see :doc:`filesystem`):

.. code-block:: xml

    <configuration>
      <supervisorMainClass>esa.mo.nmf.mission.example.MySupervisor</supervisorMainClass>
      <appsIsolation>linux-userspace</appsIsolation>
    </configuration>

If ``appsIsolation`` is omitted the default value is ``none``.

Isolation modes
----------------

none
~~~~~

All NMF Apps run as the same OS user that launched the Supervisor. No
additional OS-level separation is applied.

This is the default and is appropriate for development and for running
the SDK simulator. It is not recommended for a production spacecraft
because a misbehaving or compromised app can read and write the files
of every other app.

linux-userspace
~~~~~~~~~~~~~~~~

Each NMF App runs under a dedicated Linux user account. The account is
created automatically by the Package Management service when the
``*.nmfpackage`` file is installed; the username is derived from the
app name.

The Supervisor launches each app via ``sudo su - <appuser> -c
<start-script>``, which gives each app a separate home directory and
file-permission boundary enforced by the OS kernel.

**Requirements:**

- The Supervisor user must have permission to call ``sudo adduser`` (or
  ``useradd`` on BusyBox-based systems) and ``sudo su``. The recommended
  approach is a ``sudoers`` rule scoped to those two commands for the
  Supervisor account only — not a blanket ``NOPASSWD: ALL``.
- The OS must have ``sudo`` and ``su`` available (standard on any full
  Linux distribution).

This mode is the recommended choice for production missions on Linux
hardware. It provides strong per-app filesystem isolation with no
additional runtime dependencies beyond a standard Linux userland.

bubblewrap
~~~~~~~~~~~

Each NMF App runs inside a bubblewrap (``bwrap``) sandbox. The
sandbox configuration applied by the NMF is:

- The full host filesystem is mounted read-only inside the sandbox.
- The app's working directory and its log directory are bind-mounted
  read-write.
- ``/tmp`` is a private ``tmpfs``.
- All Linux namespaces except the network namespace are unshared
  (``--unshare-all --share-net``).
- The sandbox inherits only ``JAVA_OPTS`` and a minimal ``PATH`` from
  the Supervisor; all other environment variables are cleared.
- The sandbox process is linked to the Supervisor via
  ``--die-with-parent``: if the Supervisor is killed the sandboxed app
  is killed with it.

**Requirements:**

- ``/usr/bin/bwrap`` must be present and executable on the OBC. On
  Debian/Ubuntu: ``apt-get install bubblewrap``.
- The Supervisor user must have permission to execute ``bwrap``. On
  kernels with user-namespace support this is typically available
  without any special privilege.

Bubblewrap provides stronger isolation than ``linux-userspace`` because
it also restricts namespace visibility. The trade-off is the dependency
on ``bwrap`` being available on the target hardware.

docker-containers
~~~~~~~~~~~~~~~~~~

Reserved for a future implementation. Selecting this mode has no effect
in the current version.

Choosing a mode
----------------

.. list-table::
   :header-rows: 1
   :widths: 20 20 20 20 20

   * - Mode
     - Filesystem isolation
     - Process isolation
     - Extra dependencies
     - Recommended for
   * - ``none``
     - No
     - No
     - None
     - Development / simulator
   * - ``linux-userspace``
     - Yes (per-user)
     - No
     - ``sudo``, ``adduser``
     - Production missions
   * - ``bubblewrap``
     - Yes (read-only host)
     - Yes (namespaces)
     - ``bwrap``
     - High-assurance deployments
