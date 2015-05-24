#!/bin/bash
PWD=`pwd`
cd ./springJdbi
cp pom.xml src/test/resources/
mvn clean wro4j:run archetype:create-from-project -Darchetype.properties=../archetype.properties
mv ./target/generated-sources/archetype/src/main/resources/archetype-resources/src/test/resources/pom.xml ./target/generated-sources/archetype/src/main/resources/archetype-resources/
sed -i s/springJdbi-archetype/springJdbiArch/g ./target/generated-sources/archetype/pom.xml
rm src/test/resources/pom.xml
rm -rf ./src/main/webapp/wro/
cd ./target/generated-sources/archetype
mvn clean install archetype:integration-test
cd $PWD
