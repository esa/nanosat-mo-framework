NMF Quickstart
================================================

.. contents:: Table of contents
    :local:

After finishing this page you will have all the libraries needed to build the NMF and apps.

Getting the NMF
---------------
The recommended way to install the NMF is to get it directly from our `GitHub repository <https://github.com/esa/nanosat-mo-framework>`_.

``git clone https://github.com/esa/nanosat-mo-framework.git``

The currently stable branch is master, for an up-to-date version it is recommended to use the **dev** branch.

Installing the NMF
------------------
Make sure that a recent version of Maven is installed on your system and that you have a working internet connection. Open a shell/console in the root directory of the NMF and use the following command: ``mvn install``.
In order to produce independently runnable Java executables (JAR artifacts with dependencies - equivalent of statically linked executables), use mvn install -P assembly-with-dependencies.

*Please note:* warnings are completely natural and the errors concerning missing module descriptors during Javadoc generation are to be expected and non breaking.

**Congratulations!** You have completed the first step to develop cubesat software with the NMF. You should take a look at the :doc:`sdk` chapter next!
