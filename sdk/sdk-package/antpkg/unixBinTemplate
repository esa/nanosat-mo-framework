#!/bin/sh

# NMF_LIB can be provided by the parent app (i.e. supervisor) or set locally
if [ -z "$NMF_LIB" ] ; then
    NMF_LIB=@NMF_LIB@
fi

if [ -z "$NMF_HOME" ] ; then
    NMF_HOME=@NMF_HOME@
fi

if [ -z "$JAVA_OPTS" ] ; then
    JAVA_OPTS="-Xms32m -Xmx512m"
fi

export JAVA_OPTS
export NMF_LIB
export NMF_HOME

# Replaced with the main class name
MAIN_CLASS_NAME=@MAIN_CLASS_NAME@

exec java $JAVA_OPTS \
  -classpath "$NMF_LIB:lib/*:/usr/lib/java/*" \
  -Djava.util.logging.config.file="$NMF_HOME/logging.properties" \
  "$MAIN_CLASS_NAME" \
  "$@"

