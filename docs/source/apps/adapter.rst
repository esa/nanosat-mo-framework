===================================
Taking a look at the SobelMCAdapter
===================================
The **SobelMCAdapter** is an extension of the **MonitorAndControlNMFAdapter**.
The **MonitorAndControlNMFAdapter** provides some default implementations for methods from the **ActionInvocationListener** and **ParameterStatusListener**.
These listeners tell the app which methods it needs to expose in order to communicate properly with the NMF, so it can forward requests for parameter values and actions.
The purpose of the **SobelMCAdapter** is to take the default implementations of the **MonitorAndControlNMFAdapter** and put everything that it needs on top of that.
We should also provide the method `setNMF` where we put in the **connector** in the SobelApp class.
Since this method is already existing, we don't need to rewrite it.
To enable interactions with the NMF, we need to provide :doc:`parameters` and :doc:`actions` which can be called from the ground.
