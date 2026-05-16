==============================
The SobelMCAdapter
==============================

.. contents:: Table of contents
    :local:

.. java:package:: esa.mo.mc.impl.interfaces

The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter` extends :java:type:`~esa.mo.nmf.MonitorAndControlNMFAdapter`, which provides default implementations for the methods declared by :java:type:`ActionInvocationListener` and :java:type:`ParameterStatusListener`.
These listener interfaces define the methods that an app must expose in order to communicate with the NMF, allowing it to forward requests for parameter values and actions.
The :java:type:`~esa.mo.nmf.apps.SobelMCAdapter` builds on the default implementations provided by :java:type:`MonitorAndControlNMFAdapter` and adds application-specific behaviour on top.
The adapter also exposes a ``setNMF`` method, used by the ``SobelApp`` class to inject the ``connector``. Since this method is inherited, no further implementation is required.
To enable interactions with the NMF, the adapter must expose :doc:`parameters` and :doc:`actions` that can be invoked from the ground.
