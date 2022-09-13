=====================
MP Services Reference
=====================

.. contents:: Table of contents
    :local:

This section describes how to access MP services documentation.

MOWebViewer
-----------
There is an online tool for browsing MO services:

https://esa.github.io/mo.viewer.web/

It is a graphical MMI for browsing MO service specs (XMLs) and it shows mature MO services.

In order to show the MP services (a WIP) you need to clone the MOWebViewer.

MOWebViewer - MP
-----------------
Clone from https://github.com/esa/mo.viewer.web

First you need to get "ServiceDefMP-nmf.xml" from NMF source. Copy the file to "xml" folder.

Edit config.js and add the XML path to "configServiceDefFiles".

Open "index.html" in a browser.

You can now browse the MP services operations, data structures, and COM Objects.

NMF Source Code
---------------
MP service default implementations may be inspected in source code:

- PlanningRequestProviderServiceImpl
- PlanInformationManagementProviderServiceImpl
- PlanDistributionProviderServiceImpl
- PlanEditProviderServiceImpl

The source code tells the arguments used in callbacks.
