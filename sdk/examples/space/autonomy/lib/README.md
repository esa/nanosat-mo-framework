How to update APSI jar
===========================================

1. Update .gitlab-ci.yml with file name and new version:

        mvn install:install-file \
            -Dfile=./sdk/examples/space/autonomy/lib/ops-sat-plan-generation.jar \
            -DgroupId=external \
            -DartifactId=ops-sat-plan-generation \
            -Dversion=NEW.VERSION \
            -Dpackaging=jar

2. Use the above command to install the jar locally

3. Update dependency version in pom.xml
