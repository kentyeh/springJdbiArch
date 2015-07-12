# springJdbiArch
Sample arthetype form Spring,spring security and JNDI.

## Usage ###
generate sample project:
```
mvn archetype:generate -DarchetypeGroupId=com.github.kentyeh \
-DarchetypeArtifactId=springJdbiArch -DarchetypeVersion=2.0
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

* If you change [cucumber](https://cucumber.io/) step definitions, use follow command to regenerate function prototype.

  ```
  mvn -Pcuke initialize
  ```

* Clean will remove folder: ./target and ./src/main/webapp/wro

  ```
  mvn clean
  ```

* Regenerate compressed css and javascript if one of them to be changed or cleaned.

  ```
  mvn wro4j:run
  ```

