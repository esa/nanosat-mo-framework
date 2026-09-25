#!/bin/sh

###############################################################################
# Set Kepler element variables: A[km];E;i[deg];RAAN[deg];ARG_PER[deg];TRUE_A[deg]
#
# A spacecraft told nothing flies the orbit below rather than a row of zeros. A
# semi-major axis of zero is no orbit: the propagator answered every question
# about where the spacecraft was with NaN, and it hung motionless wherever the
# visualisation had last drawn it, while looking as healthy as any other.
if [ -n "$KEPLER_A" ];       then kepler_A=$KEPLER_A;             else kepler_A="7021.0"; fi
if [ -n "$KEPLER_E" ];       then kepler_E=$KEPLER_E;             else kepler_E="0.0"; fi
if [ -n "$KEPLER_I" ];       then kepler_I=$KEPLER_I;             else kepler_I="98.05"; fi
if [ -n "$KEPLER_RAAN" ];    then kepler_RAAN=$KEPLER_RAAN;       else kepler_RAAN=340.0; fi
if [ -n "$KEPLER_ARG_PER" ]; then kepler_ARG_PER=$KEPLER_ARG_PER; else kepler_ARG_PER=0.0; fi
if [ -n "$KEPLER_TRUE_A" ];  then kepler_TRUE_A=$KEPLER_TRUE_A;   else kepler_TRUE_A=0.0; fi

kepler_elements="$kepler_A;$kepler_E;$kepler_I;$kepler_RAAN;$kepler_ARG_PER;$kepler_TRUE_A"
###############################################################################

echo "Kepler elements set to $kepler_elements"
sed -i "s/0.0;0.0;0.0;0.0;0.0;0.0/$kepler_elements/g" ./_SIMULATOR-header.txt