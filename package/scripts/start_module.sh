#!/bin/bash
while getopts p:c:a:s:x:f:l flag
do
    case "${flag}" in
        p) snapshotPath=${OPTARG};;
        c) centralDirectoryURI=${OPTARG};;
        a) appClass=${OPTARG};;
        s) xms=${OPTARG};;
        x) xmx=${OPTARG};;
        i) platformImpl=${OPTARG};;
    esac
done
if [ -z "$xms" ]; then xms="32m"; fi
if [ -z "$xmx" ]; then xmx="96m"; fi
if [ -z "$centralDirectoryURI" ]; then "warn: no centralDirectoryURI provided, continuing..."; fi
if [ -z "$snapshotPath" ]; then echo "error: no snapshot path provided!" && exit 1; fi
if [ -z "$plarformImpl" ]; then echo "warn: nmf.platform.impl is empty, continuing..."; fi
if [ -z "$appClass" ]; then echo "error: no app class provided!\n" && exit 1; fi
echo "*************************************************"
echo "java -Xms$xms -Xmx$xmx -Desa.mo.nmf.centralDirectoryURI=$centralDirectoryURI -classpath $snapshotPath/home/nmf/lib/*:lib/*:/usr/lib/java/* -Dnmf.platform.impl=$platformImpl -Djava.util.logging.config.file=$snapshotPath/home/nmf/logging.properties esa.mo.nmf.apps.$appClass"
java -Xms$xms -Xmx$xmx -Desa.mo.nmf.centralDirectoryURI=$centralDirectoryURI -classpath $snapshotPath/home/nmf/lib/*:lib/*:/usr/lib/java/* -Dnmf.platform.impl=$platformImpl -Djava.util.logging.config.file=$snapshotPath/home/nmf/logging.properties esa.mo.nmf.apps.$appClass