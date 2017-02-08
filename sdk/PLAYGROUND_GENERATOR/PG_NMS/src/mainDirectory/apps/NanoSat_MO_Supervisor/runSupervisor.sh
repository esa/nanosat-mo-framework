#!/bin/bash
cd ${0%/*}
java -classpath "./../../libs/NanoSat_MO_Framework/*" esa.mo.nmf.provider.NanoSatMOSupervisorSoftSimImpl
