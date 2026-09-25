#!/bin/sh
# =============================================================================
# NMF — makes etc/mission.properties say which spacecraft this one is.
#
# The file is written when the filesystem is generated, so every spacecraft
# built from one image carries the same designation. A spacecraft that is one
# of many is told which it is when it is started, in its environment:
#
#   MISSION_FLEET     whether the mission flies more than one spacecraft
#   SPACECRAFT_NODE   the node of this spacecraft within the fleet
#   SPACECRAFT_NAME   the name of this individual spacecraft
#
# Each of those is written into the file, so that what the file says is what
# the spacecraft is. A variable that is not set leaves its line as it was: a
# spacecraft started with none of them keeps the designation it was built with.
#
# The runtime reads the fleet and the node from the environment in preference
# to the file, so it addresses itself correctly whether or not this script has
# run. What this script adds is that everything else reading the file — an
# operator, a log, a tool — is told the same thing.
# =============================================================================
set -eu

NMF_HOME=$(cd "$(dirname "$0")" && pwd)
FILE=$NMF_HOME/etc/mission.properties

if [ ! -f "$FILE" ]; then
    echo "MISSION no $FILE to configure"
    exit 0
fi

# set_property <key> <value> — replaces the line of that key, or adds one when
# the file has none. The value is written as it was given.
set_property() {
    _key=$1
    _value=$2

    if grep -q "^$_key=" "$FILE"; then
        # The value goes into a sed replacement, so a delimiter that cannot
        # appear in it is used rather than the usual slash.
        sed -i "s|^$_key=.*|$_key=$_value|" "$FILE"
    else
        echo "$_key=$_value" >> "$FILE"
    fi
    echo "MISSION $_key=$_value"
}

if [ -n "${MISSION_FLEET:-}" ]; then
    set_property mission.fleet "$MISSION_FLEET"
fi

if [ -n "${SPACECRAFT_NODE:-}" ]; then
    set_property spacecraft.node "$SPACECRAFT_NODE"
fi

if [ -n "${SPACECRAFT_NAME:-}" ]; then
    set_property spacecraft.name "$SPACECRAFT_NAME"
fi
