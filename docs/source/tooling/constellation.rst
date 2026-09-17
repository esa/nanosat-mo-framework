=============================
Constellation Management Tool
=============================

.. contents:: Table of contents
   :local:

The Constellation Management Tool (CMT) creates a constellation of simulated spacecraft on one machine and
manages it. Each segment is a container running a full NMF mission, with its own Supervisor, so the
constellation is consumed exactly as flying spacecraft are: over ``maltcp://``, through each segment's
Directory Service.

The tool lives in ``nmf-mission-swarms/constellation-management-tool`` and is started by the ``runCMT.sh``
script beside it. With no arguments it opens its window; with arguments it raises a constellation from the
command line and needs no display at all.

The images
----------

A segment runs the image of one of the framework's missions, built from the Dockerfile beside the module
that generates its space filesystem:

.. code-block:: bash

   mvn -pl nmf-mission-simulator-lite/lite-space-filesystem install -Pdocker

=============== ================================================================================
Name            What the segment answers
=============== ================================================================================
``lite``        The :doc:`simulator` without the orbital mechanics library. It works the orbit
                out analytically and reports the position, which is what a constellation of many
                segments usually needs. This is the default.
``orekit``      The simulator with the orbital mechanics library. It propagates the orbit and
                answers the rest of the Platform services.
``barebone``    The mission with no Platform services, for a segment that is only to be talked to
                rather than flown.
=============== ================================================================================

The segments take the orbit the same way, through the environment, so a constellation can mix them.

From the command line
---------------------

.. code-block:: bash

   cd nmf-mission-swarms/constellation-management-tool
   ./runCMT.sh --nodes 3

Three segments are raised and where to reach each of them is written out:

.. code-block:: text

   node 1    nmfsim-constellation-1       172.28.0.1       maltcp://172.28.0.1:1024/nanosat-mo-supervisor-Directory
   node 2    nmfsim-constellation-2       172.28.0.2       maltcp://172.28.0.2:1024/nanosat-mo-supervisor-Directory
   node 3    nmfsim-constellation-3       172.28.0.3       maltcp://172.28.0.3:1024/nanosat-mo-supervisor-Directory

   The constellation is up. Interrupt this command to remove it.

The constellation lives as long as the command does: interrupting it removes the segments, so a machine is
not left with containers nobody is watching. Those URIs are consumed by the :doc:`ctt`, the :doc:`cli`, or
an NMF ground application, as any other provider is.

=========================== ==============================================================================
Option                      Meaning
=========================== ==============================================================================
``--nodes <count>``         How many segments the constellation is made of. Required.
``--name <name>``           What the segments are called after: a segment is named
                            ``nmfsim-<name>-<node>``. Only letters and digits are kept.
                            Default: ``constellation``
``--image <name>``          The image every segment runs, from the table above. Default: ``lite``
``--container-tool <tool>`` What runs the segments, ``docker`` or ``kubernetes``. Default: ``docker``
``--help``                  How the tool is used.
=========================== ==============================================================================

Each segment is given its node number and its address from that number: node *n* is reached at
``172.28.<n/256>.<n%256>``, on a network of the constellation's own. The number also reaches the segment
itself, as the spacecraft node of its :doc:`../development-mission/mission-properties`, which is what tells
the segments of one mission apart.

From the window
---------------

Started with no arguments, the tool opens a window that lists the segments of the constellation and their
addresses, and shows the output of the selected one.

- **Add NanoSat → Create Simulation** creates a number of segments, the same way ``--nodes`` does.
- **Add NanoSat → Create Simulation from CSV File** creates segments from a ``.csv`` file that gives each of
  them a name and its six Keplerian elements, for a constellation whose orbits are chosen rather than
  shared.
- **Add NanoSat → Connect to NanoSat Segments** adds segments that are already running elsewhere, from a
  ``.csv`` file of names and addresses.
- **Package Manager** and **App Manager** install NMF packages and run apps on the selected segments.

Closing the window ends the session and removes the simulated segments with it.
