# springJdbiArch

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kentyeh/springJdbiArch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.kentyeh/springJdbiArch)


Sample arthetype form [Spring](http://projects.spring.io/spring-framework/),[spring security](http://projects.spring.io/spring-security/) , [JDBI3](http://jdbi.org/) and [Hazelcast IMDB](https://docs.hazelcast.com/imdg/4.2/index.html).

### Usage ###
generate sample project:
```
mvn archetype:generate -DarchetypeGroupId=com.github.kentyeh \
-DarchetypeArtifactId=springJdbiArch -DarchetypeVersion=3.0.1
```
#### After generation ####
* Test Program

  ```
  mvn test 
  ```
* Integration test

  ```
  mvn integration-test 
  ```
* Test and generate document

  ```
  mvn integration-test  site
  ```
  
  Final site report generated at target/site/index.html.
  
* running program

  ```
  mvn jetty:run &
  ```

### MISC

* Find your code problems after compiled.(by [FindBugs](http://findbugs.sourceforge.net/).

  ```
  mvn test-compile findbugs:gui
  ```

* It is suitable for small-scale clusters. If you decide to deploy to several nodes (an odd number is more helpful to an even number), open pom.xml and fill in the node's IP into <hazelcastMembers>

  ```
  <hazelcastMembers>192.168.0.100,192.168.0.101,192.168.0.102</hazelcastMembers>
  ``
