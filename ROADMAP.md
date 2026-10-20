# Project Roadmap

The NanoSat MO Framework is an end-to-end software framework for small satellites.

## 🗺️ Activities and Features Timeline

### 🟢 Near Term (Next 1-3 Months)
* [ ] **ISL integration**: Add Inter-Satellite Link (ISL) capabilities: either via an ISL service OR better integration with the transport layers.
* [ ] **Access Control**: Authenticate and authorise the consumers of the MO services, so that each service and operation can be restricted to the consumers allowed to use it.

### 🟡 Medium Term & Long Term (3+ Months)
* [ ] **Logs service**: Add Logs service to the Software Management area.
* [ ] **ArchiveHistory service**: Add ArchiveHistory service to COM area.
* [ ] **File Transfer Integration**: Add built-in File Transfer capabilities.
* [ ] **NMF Package Deltas**: Add the capability for creating delta packages, in order to reduce the bandwidth on the ground to space link when exchanging NMF Packages. Until then, packages of type `delta` are refused.
* [ ] **COM Objects removal**: The NMF will eventually remove the concept of COM Object. Alternative still to be designed.

### ✅ Done
* [x] **Mission Integrations**: Disclose more information on how to integrate the NMF with missions. Done in 5.0: the *Development Guide for Missions* of the documentation.
* [x] **Constellations**: Merge the constellations work into the main repo. Done in 5.1: the Swarms mission and the Constellation Management Tool.
* [x] **Raspberry Pi**: Merge the Raspberry Pi repo into the main repo. Done in 5.1.

## 🛑 Out of Scope
* The CCSDS Standard services like M&C will not be followed. The NMF defines the better and optimized version for M&C.


