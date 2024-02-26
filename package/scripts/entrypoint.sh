#!/bin/bash
echo "*************************************************"
echo app=$APP
echo supervisor_uri=$SUPERVISOR
echo pom_version=$VERSION
echo "*************************************************"
./start_module.sh -p /mof/sdk/sdk-package/target/nmf-sdk-$VERSION -c $SUPERVISOR -a $APP