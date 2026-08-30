#!/bin/sh

###############################################################################
# Set Kepler element variables: A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg]
if [ -n "$KEPLER_A" ];       then kepler_A=$KEPLER_A;             else kepler_A="0.0"; fi
if [ -n "$KEPLER_E" ];       then kepler_E=$KEPLER_E;             else kepler_E="0.0"; fi
if [ -n "$KEPLER_I" ];       then kepler_I=$KEPLER_I;             else kepler_I="0.0"; fi
if [ -n "$KEPLER_RAAN" ];    then kepler_RAAN=$KEPLER_RAAN;       else kepler_RAAN=0.0; fi
if [ -n "$KEPLER_ARG_PER" ]; then kepler_ARG_PER=$KEPLER_ARG_PER; else kepler_ARG_PER=0.0; fi
if [ -n "$KEPLER_TRUE_A" ];  then kepler_TRUE_A=$KEPLER_TRUE_A;   else kepler_TRUE_A=0.0; fi

kepler_elements="$kepler_A;$kepler_E;$kepler_I;$kepler_RAAN;$kepler_ARG_PER;$kepler_TRUE_A"
###############################################################################

echo "Kepler elements set to $kepler_elements"
sed -i "s/0.0;0.0;0.0;0.0;0.0;0.0/$kepler_elements/g" ./_OPS-SAT-SIMULATOR-header.txt