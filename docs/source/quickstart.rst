NMF Quickstart
================================================

.. contents:: Table of contents
    :local:

This page describes how to obtain and build the NMF, providing all libraries required to develop and run NMF apps.

Obtaining the NMF
-----------------
The recommended installation method is to clone the source from the official `GitHub repository <https://github.com/esa/nanosat-mo-framework>`_:

``git clone https://github.com/esa/nanosat-mo-framework.git``

The stable branch is **master**. Active development takes place on version branches (e.g. **v5.0**).

Building the NMF
----------------
Ensure that a recent version of Maven is installed and that an internet connection is available. From the root directory of the NMF, run:

``mvn install``

To produce independently runnable Java executables (JAR artifacts bundled with their dependencies, equivalent to statically linked executables), use:

``mvn install -P assembly-with-dependencies``

.. note::
   Build warnings are expected. In particular, errors reported during Javadoc generation regarding missing module descriptors are non-breaking and can be safely ignored.

Once the build completes, proceed to the :doc:`sdk` chapter to explore the SDK and available tooling.
