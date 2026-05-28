============================
On-board Filesystem Layout
============================

.. contents:: Table of contents
   :local:

The NMF expects a specific directory structure on the spacecraft's file system. The ``nmf-linux-maven-plugin``
generates this layout automatically from the mission's Maven project and produces a ``fresh_install.sh``
script for the initial hardware deployment.

Standard directory layout
--------------------------

After generation the output tree looks like::

    space-filesystem/
    └── nanosat-mo-framework/
        ├── apps/                    ← Installed NMF Apps, one subdirectory per app
        ├── drivers/                 ← native driver binaries for Platform services
        ├── etc/                     ← configuration files (logging.properties, …)
        ├── jars-nmf/                ← NMF Core and MO service jars
        ├── jars-mission/            ← mission-specific jars
        ├── jars-shared-libraries/   ← dependency-type NMF Packages shared by apps
        ├── java/                    ← bundled JRE installations
        ├── logs/                    ← Logs of the Supervisor and all NMF Apps
        ├── packages/                ← Stored NMF Packages, one .nmfpackage per app
        └── public/                  ← arbitrary shared resources

The constants for each directory name live in ``esa.mo.nmf.environment.Deployment``; helper getters such as
``Deployment.getAppsDir()`` and ``Deployment.getDriversDir()`` return absolute paths under ``NMF_HOME``.

The Supervisor startup script is placed at the root of the output alongside ``fresh_install.sh``.

Maven plugin configuration
---------------------------

Add the ``nmf-linux-maven-plugin`` to the mission's filesystem submodule. The recommended practice is to put
this inside a Maven profile so it is not executed on every build:

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
                    <supervisorMainClass>
                      esa.mo.nmf.mission.example.MySupervisor
                    </supervisorMainClass>
                  </configuration>
                </execution>
              </executions>
            </plugin>
          </plugins>
        </build>
      </profile>
    </profiles>

Activate the profile when you want to produce the filesystem artefact::

    mvn package -P generate-filesystem -pl mission/my-mission-filesystem

The ``supervisorMainClass`` must be the fully-qualified class name of the mission's ``NanoSatMOSupervisor``
subclass (see :doc:`supervisor`).

The ``esa.nmf.version`` property must be set (it is inherited from the NMF parent POM when the mission module
is part of the NMF build). For standalone mission projects that import the parent POM as a dependency, set it
explicitly:

.. code-block:: xml

    <properties>
      <esa.nmf.version>5.0-SNAPSHOT</esa.nmf.version>
    </properties>

``fresh_install.sh``
----------------------

The generated ``fresh_install.sh`` script copies the filesystem tree to the correct locations on a freshly
provisioned spacecraft. It is executed once during initial hardware bring-up and should be run by the same
non-root user that will operate the Supervisor at runtime.

Mission project structure
--------------------------

The ``nmf-linux-maven-plugin`` is being extended to also generate the Maven project structure for new mission
integrations. Until that feature is complete, the existing missions — :doc:`../mission-integration/ops-sat`
and :doc:`../mission-integration/phi-sat-2` — are the working references for how to lay out a mission Maven
project.

Barebone reference
-------------------

The ``nmf-mission-barebone/barebone-space-filesystem`` module is the simplest working example of a filesystem
module. Its ``pom.xml`` demonstrates the profile structure and the minimal dependency set required to produce
a valid NMF filesystem layout.
