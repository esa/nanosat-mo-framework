NMF Packages
============

.. contents:: Table of contents
    :local:

An **NMF Package** is the standard distribution format for deploying NMF Apps onto a spacecraft.
It is a ``.nmfpackage`` file that bundles the app's JAR with any additional resources — libraries, models, data files — required at runtime.
The Supervisor's **PackageManagement** service handles the installation and uninstallation of NMF Packages on the target system.

Generating an NMF Package
-------------------------

NMF Packages are produced by the ``nmf-package-maven-plugin``. Add the following profile to the app's ``pom.xml``:

.. code-block:: xml
   :linenos:

    <profiles>
        <profile>
            <id>generate-nmf-package</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>int.esa.nmf.core</groupId>
                        <artifactId>nmf-package-maven-plugin</artifactId>
                        <executions>
                            <execution>
                                <phase>package</phase>
                                <goals>
                                    <goal>generate-nmf-package</goal>
                                </goals>
                                <configuration>
                                    <mainClass>your.app.MainClass</mainClass>
                                    <libs>
                                        <!-- Additional files or folders to bundle -->
                                        <!-- <lib>${basedir}/my-model</lib> -->
                                    </libs>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>

Replace ``your.app.MainClass`` with the fully qualified name of the app's entry point.
Add any additional files or directories required at runtime inside the ``<libs>`` element.

Build the package with:

.. code-block:: bash

    mvn clean install -Pgenerate-nmf-package

The resulting ``.nmfpackage`` file will be located in the project's ``target/`` directory.

Installing and uninstalling
---------------------------

Once produced, the ``.nmfpackage`` file can be installed on the satellite through the Supervisor's **PackageManagement** service, accessible via the Consumer Test Tool (CTT) or any ground consumer.
The Supervisor manages the full lifecycle of the package: installation, uninstallation, and version tracking.

For initial hardware deployment, the ``nmf-linux-maven-plugin`` can generate the Linux filesystem layout and a ``fresh_install.sh`` script.

Mission-specific examples
-------------------------

For a worked example of integrating the plugin into a mission project, see :doc:`../phi-sat-2/packaging`.
