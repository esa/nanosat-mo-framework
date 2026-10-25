===============
App Permissions
===============

An App may use only the services it declares, and only once ground has acknowledged them at installation.
The model is that of Android: a permission is declared in the package and granted at install time.

Permission names
----------------

A permission names whose service it is, then the area and service exactly as in the MO service definitions:

- ``Supervisor.<Area>.<Service>`` for a service of the Supervisor, for example ``Supervisor.Platform.Camera``,
  ``Supervisor.SM.PackageManagement`` or ``Supervisor.MC.Action``.
- ``App.<name>.*`` for all the services of another App, for example ``App.cloud-filter.*``.
- ``App.*`` for all the services of every App.

Declaring permissions
---------------------

An App declares its permissions in the configuration of the ``nmf-package-maven-plugin`` in its ``pom.xml``,
which writes them into the package:

.. code-block:: xml

   <configuration>
       <mainClass>${assembly.mainClass}</mainClass>
       <permissions>
           <permission>Supervisor.Platform.Camera</permission>
           <permission>Supervisor.Platform.GPS</permission>
       </permissions>
   </configuration>

Baseline permissions
--------------------

Every App has these without declaring them, as it needs them to work at all:

=============================== ============================================
Service                         Operations
=============================== ============================================
``Supervisor.COM.Directory``    ``lookup``, and ``add`` and ``remove`` of its own entry
``Supervisor.SM.AppsLauncher``  ``monitorEventsRegister`` and ``monitorEventsDeregister``
``Supervisor.SM.Heartbeat``     all
=============================== ============================================

Any other service, including the rest of ``Supervisor.SM.AppsLauncher`` (running, stopping and killing other Apps), must be declared.
``Supervisor.SM.CommandExecutor`` gives the App the privileges of the Supervisor itself.

Acknowledgment by ground
------------------------

An install or update request must acknowledge every permission that the package declares,
otherwise it is refused in ``enforce`` mode, and logged in ``audit`` mode.
With the CLI: ``software-management install <package> --grant Supervisor.Platform.Camera,Supervisor.Platform.GPS``.
The granted permissions are recorded in the ``PackageInstalled`` and ``PackageUpdated`` objects of the Archive.

Without the permission
----------------------

In ``enforce`` mode, a call to a service that the App was not granted fails in the App
with an ``AuthorisationFailException`` that names the missing permission and where to declare it:

.. code-block:: text

   org.ccsds.moims.mo.mal.AuthorisationFailException: Missing permission: Supervisor.Platform.Camera.
   Declare it in the <permissions> of the nmf-package-maven-plugin in the App's pom.xml.

In ``audit`` mode, the call goes through, and the App logs a warning with the same text.

In both modes, the Supervisor logs the call and records it in the Archive as an ``UnauthorisedCall`` object,
so that ground can see which Apps used, or tried to use, a service they were not granted.

Modes
-----

The mission chooses the mode when it generates its filesystem,
with the ``accessControl`` parameter of the ``nmf-linux-maven-plugin``:

- ``off``: no checks.
- ``audit``: nothing is refused, but every call that ``enforce`` would refuse is logged.
  The playground and the testbeds use it.
- ``enforce``: calls without the permission are refused. For flight. The default.

A Supervisor started without a mode, for example from an IDE, also enforces access control.

In ``enforce`` mode, ground tools present the credential of ground,
which is generated with the filesystem in ``bootloader/ground.credential``.
With the CLI: ``--credential-file <path to ground.credential>``.
The credential is not exchanged: the mission keeps a copy of that file on ground and gives it to its tools.
It travels in clear in every request, so the link must be encrypted.

In the playground, ``run_CTT.sh`` and ``run_CLI.sh`` point the tools at the playground's own
``bootloader/ground.credential``, which they read from the same filesystem as the Supervisor.
A tool presents the credential only to the Supervisor whose Directory it connects to, never to an App.
