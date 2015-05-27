# springJdbiArch
Sample arthetype form Spring,spring security and JNDI.

## Usage ###
generate sample project:
```
mvn archetype:generate -DarchetypeGroupId=com.github.kentyeh \
-DarchetypeArtifactId=springJdbiArch -DarchetypeVersion=1.0
```
#### After generation####
* Test Program

  ```
  mvn test 
  ```
* Integration test

  ```
  mvn integration-test 
  ```
* generate document

  ```
  mvn site
  ```
* running program

  ```
  mvn jetty:run&
  ```

### MISC

* Remove folder: ./target and ./src/main/webapp/wro

  ```
  mvn jetty:clean
  ```

* Generate compressed css and javascript.

  ```
  mvn wro4j:run
  ```