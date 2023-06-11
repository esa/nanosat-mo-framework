#!/bin/bash
VERSION=$(mvn exec:exec -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive -q)
echo $VERSION