#!/bin/bash
cd ${0%/*}
java -classpath "./target/Demo_Hello_World-1.0-SNAPSHOT.jar:./target/NANOSAT_MO_FRAMEWORK_APP-1.0-SNAPSHOT-jar-with-dependencies.jar" esa.mo.demo.provider.helloworld.DemoHelloWorld
