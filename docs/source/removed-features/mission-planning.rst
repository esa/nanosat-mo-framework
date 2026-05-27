=========================
Mission Planning services
=========================

.. warning::

   The Mission Planning (MP) services have been removed from the NMF.
   This page is preserved as a historical reference for users
   maintaining deployments based on earlier versions. See
   ``RELEASE_NOTES.md`` for the removal context.

.. contents:: Table of contents
   :local:

Overview
--------

The Mission Planning services were a draft set of CCSDS-MP services that the NMF prototyped to support
on-board planning workflows including Planning Requests, Plan Distribution, Plan Information Management, and
Plan Edit.

The services had default implementations with app-specific overrides:

- ``PlanningRequestProviderServiceImpl``
- ``PlanInformationManagementProviderServiceImpl``
- ``PlanDistributionProviderServiceImpl``
- ``PlanEditProviderServiceImpl``

App-specific behaviour was registered via callbacks keyed by an ``MPServiceOperation`` enumeration. The COM
Archive was the central storage for plan-related objects.

Reference sample
----------------

The ``MPSpaceDemo`` (space) and ``MPGroundDemo`` (ground) sample applications demonstrated the services end to
end. ``MPGroundDemo`` populated request templates and activity/event/resource definitions; ``MPSpaceDemo``
exposed the four MP services for consumption by the CTT or other ground tools.

A typical demo flow:

1. Start ``MPSpaceDemo``; the CTT discovers its four MP services.
2. Start ``MPGroundDemo``; it pushes request templates and definitions to the space demo.
3. From the CTT, submit a planning request; the resulting COM objects are visible in the Archive Manager tab
   and trigger Event-service notifications.

Why it was removed
------------------

The CCSDS-MP specification did not stabilise on a timeline compatible with the NMF's release plan, and the
prototype implementation diverged from the eventual standard. Maintaining a partial implementation in the core
framework was not justified once the prototype's purpose was served.

For up-to-date plan handling, consult the CCSDS-MP working group's current outputs directly.
