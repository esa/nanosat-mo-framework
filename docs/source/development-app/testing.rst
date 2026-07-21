===============
Testing the app
===============

.. contents:: Table of contents
   :local:

Once an app is implemented, it should be verified before deployment. Three test patterns are supported.

Unit tests
----------

Standard JUnit tests for any pure logic in the app — image processing, state machines, parsers — should live
alongside the source under ``src/test/java/``. Tests are run with ``mvn test`` for the app's module or ``mvn
test`` from the repository root.

Running in the SDK Playground environment
-----------------------------------------

The most realistic local test setup combines a Supervisor with the spacecraft simulator (for Platform
services) and the CTT (as the ground consumer):

1. Deploy the app into the SDK Playground by adding it as a dependency of the
   ``sdk-playground-environment`` module (see below).
2. Build the SDK with ``mvn install``.
3. Start the Supervisor with simulator and the CTT, as described in :doc:`../quickstart/index`.
4. From the CTT, navigate to the **Apps Launcher Service**, select the app, and click **runApp**.
5. Connect to the running app via **Communication Settings → Fetch Information** and exercise its parameters
   and actions.

Deploying into the SDK Playground
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Apps are distributed as ``.nmfpackage`` files and installed by the ``nmf-linux-maven-plugin`` during the
build. To include an app in the playground:

In ``sdk/sdk-playground-environment/pom.xml``, add the app as an ``nmfpack`` dependency:

.. code-block:: xml

   <dependency>
     <groupId>int.esa.nmf.sdk</groupId>
     <artifactId>my-app</artifactId>
     <version>${project.version}</version>
     <type>nmfpack</type>
   </dependency>

Rebuild with ``mvn install``. The plugin automatically installs the package into the playground filesystem
under ``target/space-filesystem/nanosat-mo-framework/apps/my-app/``.

Ground-side automation
----------------------

For automated regression testing without manual CTT interaction, two options are available:

- **CLI tool** (``sdk/cli-tool``): a command-line interface to NMF services that can script parameter reads,
  action triggers, and app lifecycle commands without writing a full ground application. Suitable for simple
  pipelines and shell-based automation.
- **Ground application**: a Java consumer written against the NMF consumer API for more complex orchestration.
  See the Ground Software Development Guide for the consumer-side patterns.

Running in a FlatSat
--------------------

A FlatSat is a hardware-in-the-loop bench where the real on-board computer runs the NMF Supervisor and apps
against actual (or simulated) hardware. Testing on a FlatSat catches issues that the SDK simulator cannot
reproduce: driver behaviour, timing, and hardware resource constraints.

**Deployment**

Build the ``.nmfpackage`` for your app with ``mvn install``, then transfer it to the on-board computer and
install it via the Supervisor's **Package Management** service — either through the CTT, the CLI tool, or a
dedicated ground application using the Ground MO Adapter.

**CI/CD integration**

A typical pipeline looks like this:

1. On every commit, run unit tests and the SDK playground end-to-end tests in CI (no hardware needed).
2. The CI job builds the ``.nmfpackage`` artifact and uploads it to the artifact repository.
3. A deployment step — triggered manually or automatically — transfers the package to the FlatSat and
   installs it via the Package Management service.
4. A ground-side test script connects to the running app and exercises its parameters and actions to verify
   correct behaviour on real hardware.

Keep deployment steps idempotent: the Package Management service supports reinstalling an already-installed
package, so pipeline retries are safe.
