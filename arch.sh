#!/bin/bash
PWD=`pwd`
cd ./springJdbi
cp pom.xml src/test/resources/
mvn wro4j:run archetype:create-from-project -Darchetype.properties=../archetype.properties
cp target/generated-sources/archetype/src/main/resources/archetype-resources/src/test/resources/pom.xml target/generated-sources/archetype/target/classes/archetype-resources/
rm src/test/resources/pom.xml
cd PWD
