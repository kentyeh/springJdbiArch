#!/bin/bash
CD=`pwd`
cd ./springJdbi
cp pom.xml src/test/resources/
mvn clean wro4j:run archetype:create-from-project -Darchetype.properties=../archetype.properties && \
mv ./target/generated-sources/archetype/src/main/resources/archetype-resources/src/test/resources/pom.xml ./target/generated-sources/archetype/src/main/resources/archetype-resources/ && \
sed -i s/springJdbi-archetype/springJdbiArch/g ./target/generated-sources/archetype/pom.xml && \
sed -i -E '0,/<version>[0-9]+\.[0-9]*<\/version>/s//<version>1.0-SNAPSHOT<\/version>/' ./target/generated-sources/archetype/src/main/resources/archetype-resources/pom.xml &&\
find ./target/generated-sources/archetype/src -name "*.java" -o -name "*.xml"|xargs sed -i s/$\{groupId\}/$\{package\}/g && \
sed -i '$ d' ./target/generated-sources/archetype/pom.xml && \
cat $CD/profiles.xml >> ./target/generated-sources/archetype/pom.xml && \
echo -e "\n</project>" >> ./target/generated-sources/archetype/pom.xml && \
cd ./target/generated-sources/archetype && \
mvn clean install archetype:integration-test
rm $CD/springJdbi/src/test/resources/pom.xml
rm -rf $CD/springJdbi/src/main/webapp/wro/
cd $CD
#echo 'mvn archetype:generate -DarchetypeRepository=local -DarchetypeGroupId=com.github.kentyeh -DarchetypeArtifactId=springJdbiArch -DarchetypeVersion=2.1'
#echo 'cd ./springJdbi/target/generated-sources/archetype && mvn clean deploy -P release'
