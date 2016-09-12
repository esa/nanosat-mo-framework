#!/bin/bash
cd ${0%/*}
java -classpath "./../../libs/NanoSat_MO_Framework/*" esa.mo.nanosatmoframework.provider.NanoSatMOSupervisorSoftSimImpl
