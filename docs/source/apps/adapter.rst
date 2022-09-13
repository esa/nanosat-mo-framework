===================================
Taking a look at the SobelMCAdapter
===================================

.. contents:: Table of contents
    :local:

.. java:package:: esa.mo.mc.impl.interfaces

The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter` is an extension of the :java:type:`~esa.mo.nmf.MonitorAndControlNMFAdapter`.
The :java:type:`~esa.mo.nmf.MonitorAndControlNMFAdapter` provides some default implementations for methods from the :java:type:`ActionInvocationListener` and :java:type:`ParameterStatusListener`.
These listeners tell the app which methods it needs to expose in order to communicate properly with the NMF, so it can forward requests for parameter values and actions.
The purpose of the :java:type:`~esa.mo.nmf.apps.SobelMCAdapter` is to take the default implementations of the :java:type:`MonitorAndControlNMFAdapter` and put everything that it needs on top of that.
We should also provide the method `setNMF` where we put in the ``connector`` in the SobelApp class.
Since this method is already existing, we don't need to rewrite it.
To enable interactions with the NMF, we need to provide :doc:`parameters` and :doc:`actions` which can be called from the ground.
