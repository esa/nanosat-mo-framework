#!/bin/bash
cd ~/temp/_OPSSAT_SIMULATOR
tail -f `ls -1tr Cen*log | tail -1`
