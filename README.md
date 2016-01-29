# springJdbiArch
Sample arthetype form Spring,spring security and JNDI.

## Usage ###
generate sample project:
```
mvn archetype:generate -DarchetypeGroupId=com.github.kentyeh \
-DarchetypeArtifactId=springJdbiArch -DarchetypeVersion=2.4
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
* If you with to build a runnable war (by  [capsule](http://www.capsule.io/))

  ```
  mvn -Prunwar package
  java -jar yourFinalBuildPackage.war
  ```


* Clean will remove folder: ./target and ./src/main/webapp/wro

  ```
  mvn clean
  ```

* Regenerate compressed css and javascript if one of them to be changed or cleaned.

  ```
  mvn wro4j:run
  ```

* Find your code problems after compiled.(by [FindBugs](http://findbugs.sourceforge.net/).

  ```
  mvn test-compile findbugs:gui
  ```

