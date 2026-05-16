=====================
MP Services Reference
=====================

.. contents:: Table of contents
    :local:

This section describes how to access the documentation for the MP services.

MOWebViewer
-----------
An online tool is available for browsing the MO services:

https://esa.github.io/mo.viewer.web/

MOWebViewer provides a graphical interface for inspecting MO service specifications (XML files) and currently displays the mature MO services.

To inspect the MP services, which are still a work in progress, a local clone of MOWebViewer is required.

MOWebViewer — MP
----------------
Clone https://github.com/esa/mo.viewer.web.

Copy the ``ServiceDefMP-nmf.xml`` file from the NMF source into the ``xml`` folder of the cloned repository.

Edit ``config.js`` to add the path to the XML file under ``configServiceDefFiles``.

Open ``index.html`` in a browser to browse the MP service operations, data structures, and COM objects.

NMF Source Code
---------------
The default implementations of the MP services can be inspected directly in the source code:

- ``PlanningRequestProviderServiceImpl``
- ``PlanInformationManagementProviderServiceImpl``
- ``PlanDistributionProviderServiceImpl``
- ``PlanEditProviderServiceImpl``

The source code is the authoritative reference for the arguments used in each callback.
