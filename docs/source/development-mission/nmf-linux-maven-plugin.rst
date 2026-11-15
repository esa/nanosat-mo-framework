======================
NMF Linux Maven Plugin
======================

The ``nmf-linux-maven-plugin`` generates the on-board filesystem of a mission: the directory tree, the baseline files, the bootloader and its configuration, and the checksum manifests.
It is a mission-side plugin and has nothing to do with the ``nmf-package-maven-plugin``, which packages an App (see :doc:`../development-app/packaging`).
The directory tree it produces is described in :doc:`filesystem`.

Configuration
-------------

Add the plugin to the mission's filesystem submodule, inside a Maven profile so it does not run on every build:

.. code-block:: xml

    <profiles>
      <profile>
        <id>generate-filesystem</id>
        <build>
          <plugins>
            <plugin>
              <groupId>int.esa.nmf.core</groupId>
              <artifactId>nmf-linux-maven-plugin</artifactId>
              <executions>
                <execution>
                  <phase>package</phase>
                  <goals>
                    <goal>generate-filesystem</goal>
                  </goals>
                  <configuration>
                    <supervisorMainClass>esa.mo.nmf.mission.example.MySupervisor</supervisorMainClass>
                    <appsIsolation>linux-userspace</appsIsolation>
                    <accessControl>enforce</accessControl>
                    <mission>
                      <missionName>Example</missionName>
                      <spacecraftName>Example-1</spacecraftName>
                      <organizationAbbreviation>ESA</organizationAbbreviation>
                    </mission>
                  </configuration>
                </execution>
              </executions>
            </plugin>
          </plugins>
        </build>
      </profile>
    </profiles>

Activate the profile to produce the filesystem::

    mvn package -P generate-filesystem -pl mission/my-mission-filesystem

The ``esa.nmf.version`` property must be set.
It is inherited from the NMF parent POM when the mission module is part of the NMF build; a standalone mission project that imports the parent POM as a dependency sets it explicitly:

.. code-block:: xml

    <properties>
      <esa.nmf.version>6.0-SNAPSHOT</esa.nmf.version>
    </properties>

``generate-filesystem`` settings
--------------------------------

.. list-table::
   :header-rows: 1
   :widths: 26 20 54

   * - Setting
     - Default
     - Meaning
   * - ``supervisorMainClass``
     - *(required)*
     - The fully-qualified class name of the mission's ``NanoSatMOSupervisor`` subclass (see :doc:`supervisor`).
   * - ``mission``
     - *(required)*
     - The mission and spacecraft identity written to ``etc/mission.properties``: ``missionName``, ``spacecraftName``, ``organizationAbbreviation`` and more (see :doc:`mission-properties`).
   * - ``nmfVersion``
     - ``${esa.nmf.version}``
     - The NMF framework version of the generated baseline.
   * - ``missionVersion``
     - ``${project.version}``
     - The mission software version of the generated baseline.
   * - ``appsIsolation``
     - ``none``
     - How each App is isolated: ``none``, ``linux-userspace``, ``bubblewrap`` or ``docker-containers`` (see :doc:`app-isolation`).
   * - ``requirePackageChecksum``
     - ``true``
     - Whether install and update refuse a package that arrives without its expected SHA-256 checksum.
   * - ``accessControl``
     - ``enforce``
     - The access control mode: ``off``, ``audit`` or ``enforce``.
   * - ``accessControlCredentials``
     - ``signed``
     - How callers prove their credentials: ``signed`` or ``plain``.
   * - ``additionalFiles``
     - *(none)*
     - Mission files added to the root of the generated filesystem; a directory is added under its own name.

Security and integrity
----------------------

Three settings govern the on-board security posture.
Their defaults are the secure ones, so a mission that configures nothing ships secured; a deployment relaxes them deliberately.

``accessControl`` decides what the Supervisor does with a request that lacks the required permission.
``enforce`` refuses it, ``audit`` lets it through but logs that enforce mode would have refused it, and ``off`` disables the checks.
``enforce`` is the default; for local development the SDK playground uses ``audit``.

``accessControlCredentials`` decides how a caller proves its credential.
``signed`` signs each request with the credential, which is never sent and cannot be replayed; ``plain`` sends the credential as it is, which is safe only when the Supervisor is reachable solely through the loopback.

``requirePackageChecksum`` decides whether install and update accept a package that arrives without the SHA-256 checksum approved on ground.

When access control is on, the Supervisor recognises a full-access caller by a secret generated for each filesystem and written to ``bootloader/supervisor-secret.credential``.
The Supervisor reads it; a ground tool holds a copy and presents it to the Supervisor only (never to an App).
See :doc:`../development-app/permissions` for the App permission model and how a ground tool presents the secret.

Baseline package goals
----------------------

The same plugin builds the two baseline components as NMF Packages, so that a framework or mission upgrade is delivered through the Package Management service rather than assembled by hand on the spacecraft:

- ``generate-nmf-core-package`` packages the framework jars from ``jars-nmf/<version>``.
- ``generate-mission-package`` packages the mission jars from ``jars-mission/<version>``.

Both read a directory that ``generate-filesystem`` has already produced, so they run after it, in the same profile.
See :doc:`filesystem` (*Delivering a new baseline*) for how these baselines are installed and activated on-board.
