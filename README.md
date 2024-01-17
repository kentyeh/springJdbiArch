# springJdbiArch

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.kentyeh/springJdbiArch/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.kentyeh/springJdbiArch)


Sample archetype form [Spring](https://spring.io/projects/spring-framework),[Spring Security](https://spring.io/projects/spring-security) and [JDBI3](http://jdbi.org/).

### Requirement ###

Require JDK 17+ AND [Maven](https://maven.apache.org/) preinstalled.

### Usage ###

generate sample project:
```
mvn archetype:generate -DarchetypeGroupId=com.github.kentyeh \
-DarchetypeArtifactId=springJdbiArch -DarchetypeVersion=4.0.3
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
  mvn verify site
  ```
  
  Final site report generated at target/site/index.html.
  
* running program

  ```
  mvn jetty:run
  ```

then open [http://localhost:8080/{project.artifactId}/](http://localhost:8080/springJdbi/) to see the pages. In the meantime, the [H2 database](https://h2database.com/html/main.html) is available for connecting by `jdbc:h2:tcp://localhost/{project.artifactId}` with `sa/{project.artifactI}`
### MISC

* Analysis your code problems after compiled.(by [SpotBugs](https://spotbugs.github.io/).)

  ```
  mvn compile spotbugs:check spotbugs:gui
  ```
  
* Deploy Web Application

  ```bash
  mvn package
  ```

  Find war file inner `${project.basedir}/target` folder, before deployment, It is required to build a `jndi/${project.artifactId}`database resource in your web container.

