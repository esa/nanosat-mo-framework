===============
Testing the app
===============

.. contents:: Table of contents
   :local:

Once an app is implemented, it should be verified before deployment.
Three test patterns are supported.

Unit tests
----------

Standard JUnit tests for any pure logic in the app — image processing,
state machines, parsers — should live alongside the source under
``src/test/java/``. Tests are run with ``mvn test`` for the app's
module or ``mvn test`` from the repository root.

Running in the SDK environment
------------------------------

The most realistic local test setup combines a Supervisor with the
spacecraft simulator (for Platform services) and the CTT (as the
ground consumer):

1. Deploy the app into the SDK by adding it as a dependency of the
   ``sdk-execution-environment`` module and registering its start
   script in ``antpkg/build.xml`` (see below).
2. Build the SDK with ``mvn install``.
3. Start the Supervisor with simulator and the CTT, as described in
   :doc:`../quickstart/index`.
4. From the CTT, navigate to the **Apps Launcher Service**, select the
   app, and click **runApp**.
5. Connect to the running app via **Communication Settings → Fetch
   Information** and exercise its parameters and actions.

Deploying into the SDK
^^^^^^^^^^^^^^^^^^^^^^

To make the SDK assembly include the app:

1. In ``sdk/sdk-execution-environment/pom.xml``, add the app to the
   dependencies:

   .. code-block:: xml

      <dependency>
        <groupId>int.esa.nmf.sdk</groupId>
        <artifactId>my-app</artifactId>
        <version>${project.version}</version>
      </dependency>

2. Add a copy task so the app's property files are deployed:

   .. code-block:: xml

      <copy todir="${esa.nmf.sdk.assembly.outputdir}/home/my-app">
        <fileset dir="${basedir}/src/main/resources/space-common"/>
        <fileset dir="${basedir}/src/main/resources/space-app-root"/>
      </copy>

3. In ``sdk/sdk-execution-environment/antpkg/build.xml``, register the
   start-script subtarget:

   .. code-block:: xml

      <target name="emit-space-app-my-app">
        <ant antfile="antpkg/build_shell_script.xml">
          <property name="mainClass" value="esa.mo.nmf.apps.MyApp"/>
          <property name="id" value="start_my_app"/>
          <property name="binDir" value="my-app"/>
        </ant>
        <ant antfile="antpkg/build_batch_script.xml">
          <property name="mainClass" value="esa.mo.nmf.apps.MyApp"/>
          <property name="id" value="start_my_app"/>
          <property name="binDir" value="my-app"/>
        </ant>
      </target>

4. Add ``emit-space-app-my-app`` to the ``build`` target's
   ``depends=`` attribute.

The ``id`` property must use the ``start_`` prefix so the Supervisor
recognises the script as an app launcher.

Alternative: Monolithic providers (not an App pattern)
------------------------------------------------------

For cases that do not require the multi-App model, a **Monolithic
provider** (``NanoSatMOMonolithic``) offers an alternative. A
Monolithic provider is a self-contained process that exposes
Supervisor-level services and a custom MC adapter directly, without
being managed by an Apps Launcher. It is **not** an NMF App; see
:doc:`../concepts/apps-and-supervisor` for the distinction.

This pattern is useful for self-contained demonstrations and standalone
examples (such as
``sdk/all-mc-services-with-sim``) but is not
appropriate when the goal is to develop and deploy a real NMF App that
will coexist with others on a spacecraft. If your code needs to run
under a Supervisor in flight, develop it as an App from the start.

Ground-side automation
----------------------

For automated regression testing without manual CTT interaction, a
ground application can be written to drive the app programmatically.
See the Ground Software Development Guide for the consumer-side
patterns.
