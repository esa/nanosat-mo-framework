#!/bin/bash
cd ~/temp/_OPSSAT_SIMULATOR
tail -f -n 5000 `ls -1tr Sim*log | tail -1`
