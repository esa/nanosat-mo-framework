============================
NMF on the OPS-SAT mission
============================

.. contents:: Table of contents
   :local:

The NMF was the default software framework for experimenters on `OPS-SAT
<https://www.esa.int/Our_Activities/Operations/OPS-SAT>`_. The mission launched in December 2019 and the
framework was first executed in space in 2020.

.. warning::

   The ``nmf-mission-ops-sat`` repository may no longer be actively
   maintained. The instructions below are kept for reference and for
   users maintaining legacy deployments.

Packaging for OPS-SAT
---------------------

Packaging an app for OPS-SAT produces a directory structure that the mission's Flight Control Team
subsequently builds into an IPK for upload to the spacecraft.

1. Clone the ``nmf-mission-ops-sat`` repository.
2. Check out the latest version branch (e.g. ``v5.0``).
3. Run ``mvn install`` in both the NMF Core and ``nmf-mission-ops-sat`` clones so the local Maven repository
   has current artifacts.
4. Run ``mvn install`` in the app's project to install its artifact.
5. Open ``opssat-package/experiment/pom.xml`` and set:

   - ``expId`` — the experimenter ID assigned by ESA.
   - ``expApid`` — typically ``expId + 1024``.
   - ``expMainClass`` — the fully qualified name of the app's main class.
   - ``expVersion`` — must match the version declared in the app's POM.

6. Update the ``maven-dependency-plugin`` ``artifactItems`` to match the app's Maven coordinates and add one
   ``artifactItem`` per external JAR dependency.
7. If additional resources must be packaged, add copy tasks in ``ant_copy_jobs.xml``.
8. Run ``mvn clean package`` in ``opssat-package/experiment``.

The generated directory tree under ``target/experiment-package/`` is ready for IPK assembly. Submit it to the
OPS-SAT Flight Control Team following the operational procedures (registration as an experimenter is
required).

Testing in an OPS-SAT-like environment
--------------------------------------

For pre-flight testing, the mission build also produces a **Ground MO Proxy** (see
:doc:`../development-ground/ground-mo-proxy`) and a Supervisor with simulator targeted at the OPS-SAT
platform.

1. Run ``mvn install -Pground`` in ``nmf-mission-ops-sat``. The build adds the OPS-SAT Supervisor with
   simulator and the Ground MO Proxy under ``opssat-package/nmf/target/nmf-ops-sat-VERSION/``.
2. Copy the app's experiment folder from ``opssat-package/experiment/target/experiment-package/home/`` into
   ``opssat-package/nmf/target/.../home/expXYZ/``, replacing ``XYZ`` with the experimenter ID.
3. For stand-alone testing, remove the ``helpertools.configurations.provider.app.user`` property from the
   app's ``provider.properties``. The property must be present when packaging for the EM FlatSat or the Flight
   Model.

Two Supervisor configurations are available:

- **Standard OPS-SAT Supervisor.** Faster startup. Does not initialise the payload interfaces, so Platform
  services are not functional. Suitable for checking that the app starts and registers correctly.
- **Hybrid Supervisor with simulator.** Slower startup (initialises Orekit). Provides functional Platform
  services backed by the simulator. Configurable per-service via ``platformsim.properties``.

Run order: start the Ground MO Proxy first, wait for its Directory Service URI to appear, then start the
Supervisor. Connect the CTT to the Ground MO Proxy's Directory Service.

The standard CTT (built with the SDK) works unchanged against the OPS-SAT Supervisor through the Ground MO
Proxy; no mission-specific CTT is required.
