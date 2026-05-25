#!/bin/sh
SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
NMF_HOME=$SCRIPT_DIR

JAVA_ORACLE_8=$NMF_HOME/java/jdk-8-oracle-arm32-vfp-hflt/bin/java
JAVA_OPENJDK_8=$NMF_HOME/java/jdk8u292-b10-aarch32-20210423-jre/bin/java
#JAVA_CMD=$JAVA_OPENJDK_8
JAVA_CMD=java
JAVA_LOGGER=$NMF_HOME/etc/logging.properties
NMF_VERSION=@NMF_VERSION@
MISSION_VERSION=@MISSION_VERSION@

NOW=$(date +"%F")
FILENAME=supervisor_$NOW.log
LOG_PATH=$NMF_HOME/logs/supervisor
mkdir -p $LOG_PATH

$JAVA_CMD \
    -Xms16M \
    -Djava.util.logging.config.file=$JAVA_LOGGER \
    -classpath "$NMF_HOME/libs/*:$NMF_HOME/jars-mission/$MISSION_VERSION/*:$NMF_HOME/jars-nmf/$NMF_VERSION/*" \
    @SUPERVISOR_MAIN_CLASS@ \
    2>&1 | tee -a $LOG_PATH/$FILENAME
