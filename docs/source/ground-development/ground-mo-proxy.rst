===================
The Ground MO Proxy
===================

.. contents:: Table of contents
   :local:

The **Ground MO Proxy** is a ground-segment application that bridges
between the operational ground network and the spacecraft's space
link. Its primary role is to translate MO traffic between two
transports:

- **MALTCP** on the ground side, used by the CTT and other ground
  applications running over TCP/IP.
- **MALSPP** on the space side, used over CCSDS Space Packet Protocol
  for the actual radio link.

In addition to transport translation, the Ground MO Proxy provides a
**Directory Service** of its own that mirrors the on-board
Supervisor's, so that ground consumers can discover space-side apps
without speaking SPP directly.

When the Ground MO Proxy is needed
----------------------------------

For local testing, where the ground application and the Supervisor
run on the same machine over MALTCP, the Ground MO Proxy is not
needed. The ground application connects directly to the Supervisor's
Directory Service URI.

For deployment, where the space link uses MALSPP, the Ground MO Proxy
is the standard component. The topology becomes:

.. mermaid::

    flowchart LR
        GA[Ground application]
        Proxy[Ground MO Proxy]
        Sup[Supervisor on spacecraft]
        GA -- MALTCP --> Proxy
        Proxy -- MALSPP over space link --> Sup

Running the Ground MO Proxy
---------------------------

The Ground MO Proxy is built and packaged as part of the mission's NMF
deployment. The OPS-SAT mission build produces a
``ground-mo-proxy.sh`` script that starts the proxy with the mission's
default configuration.

On startup, the proxy prints its own Directory Service URI, in the
form:

.. code-block:: text

   maltcp://<host>:<port>/ground-mo-proxy-Directory

Ground applications use this URI as their
``esa.mo.nmf.centralDirectoryURI``. The proxy then handles
Directory-Service queries and forwards subsequent operations across
the link.

The proxy can be started before the Supervisor; warnings about an
unreachable spacecraft are expected until the Supervisor comes up and
the two synchronise their Directory entries.

Synchronisation lag
-------------------

After a space-side app starts or stops, the Ground MO Proxy may take
a few seconds to synchronise its Directory Service. If a freshly
started app does not appear in the proxy's provider list immediately,
wait briefly and refresh.
