=============================
Project setup
=============================
.. contents:: Table of contents
    :local:

Introduction
------------
This guide walks through the development of a complete NMF App. The example app uses the camera service to capture images of the Earth and applies a Sobel edge-detection filter to each captured image, reporting progress to the user throughout the operation.

The implementation first uses the iADCS service to point the camera toward the ground, then invokes the camera service to acquire the image. The Sobel filter is then applied in plain Java. On OPS-SAT, this computation could alternatively be offloaded to the FPGA for hardware acceleration.

Creating the project
--------------------
The recommended way to create a new NMF App project is to copy an existing example — preferably the one most similar to the intended application — and rename the resulting folder.
For this example, the camera example project folder is copied and renamed to ``sobel``.
To simplify importing the project into an IDE workspace, the app's POM should be updated to provide a unique name and artifact identifier.
In the ``pom.xml`` inside the ``sobel`` folder, change the ``artifactId`` from ``camera`` to ``sobel``. The ``name`` and ``description`` tags should also be updated to reflect the new app, and the ``author`` tag should be set accordingly.
If the app uses Orekit without supplying custom Orekit data, add the following dependency:

.. code-block:: xml
    :linenos:

	<dependency>
	  <groupId>int.esa.nmf.sdk</groupId>
	  <artifactId>orekit-resources</artifactId>
	  <version>${project.version}</version>
	  <type>jar</type>
	</dependency>

The project can now be imported into NetBeans or Eclipse, using the same procedure as for the NMF itself. For consistency, the example files and classes should be renamed to reflect the new app. Rename the main class from :java:type:`~esa.mo.nmf.apps.SnapNMF` to :java:type:`~esa.mo.nmf.apps.SobelApp`, and the adapter class from :java:type:`~esa.mo.nmf.apps.MCSnapNMFAdapter` to :java:type:`~esa.mo.nmf.apps.SobelMCAdapter`.

With the environment and project now in place, the example app can be adapted to the requirements of the Sobel use case.

Why two classes?
----------------
Following sound software engineering practices, the separation of concerns principle is applied to keep the codebase well structured. The purpose of :java:type:`~esa.mo.nmf.apps.SobelApp` is solely to instantiate the app and establish the necessary connections to the NMF. The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter` is responsible for the communication between the app and the services provided by the NMF. The main class can be reused as-is; it is examined in further detail in the next chapter (:doc:`main`).
