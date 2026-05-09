# CS304 Tutorial 8: Testing with JUnit and JaCoCo

_2025-04-02_

In this tutorial, we'll learn about the basics of JUnit testing. We'll also use Teedy to demonstrate common testing practices using maven, JUnit, and test coverage tools.

## Getting Started with JUnit

JUnit is essentially a dependency to your project, which could be downloaded and managed using Maven. You may refer to this official guide of IntelliJ IDEA to create a Maven project and add JUnit dependency in pom.xml.

```xml
<dependencies>
  <dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.7.1</version>
  </dependency>
</dependencies>
```

Follow this official guide to create application code, generate tests, and execute the tests to observe the results.

## Examining Tests in Teedy

Teedy has 3 modules docs-core, docs-web-common, and docs-web, each can be built and tested independently. You may observe the JUnit dependency in pom.xml of any module, and observe the test cases written for any of the modules.

```
Teedy [docs-parent]
├── github
├── .idea
├── data
├── docs-android
├── docs-core
│   ├── src
│   │   ├── main
│   │   └── test
│   ├── target
│   └── pom.xml
├── docs-importe
├── docs-web
│   ├── src
│   │   ├── dev
│   │   │   ├── main
│   │   │   └── test
│   │   └── prod
│   ├── target
│   └── pom.xml
├── docs-web-common
│   ├── src
│   │   ├── main
│   │   └── test
│   └── pom.xml
```

## Running Teedy Tests

The Surefire Plugin is used during the test phase of the build lifecycle to execute the unit tests of an application. In previous labs, we skipped tests when building Teedy using `mvn clean -DskipTests install`. You could simply remove the `-DskipTests` option if you want to run tests in building.

Alternatively, you could run `mvn test` directly to execute all unit tests in the project. By default, it automatically executes all test classes with the following wildcard patterns:

- `**/Test*.java`
- `**/*Test.java`
- `**/*Tests.java`
- `**/*TestCase.java`

If the test classes do not follow the default wildcard patterns, then override them by configuring the Surefire Plugin and specify the tests you want to include (or exclude) or another patterns.

```xml
<project>
  [...]
  <build>
    <plugins>
      <plugin>
        <artifactId>maven-surefire-plugin</artifactId>
        <groupId>org.apache.maven.plugins</groupId>
        <version>3.0.0</version>
        <configuration>
          <includes>
            <include>Sample.java</include>
          </includes>
          <excludes>
            <exclude>**/TestCircle.java</exclude>
            <exclude>**/TestSquare.java</exclude>
          </excludes>
        </configuration>
      </plugin>
    </plugins>
  </build>
  [...]
</project>
```

Use `mvn test --fail-never` so that the testing continues even if certain test cases fail.

Running all tests may take a long time. Sometimes you may want to run only a few interesting test classes or test methods. In that case, you could run:

- `mvn -Dtest=TestCss test`
- `mvn -Dtest=TestCss,TestImageUtil test`
- `mvn -Dtest=TestEncryptUtil#encryptStreamTest+decryptStreamTest test`

You may also use `-pl` to specify a module to run tests:

- `mvn test -pl docs-core`

See here for detailed syntax on running single test.

## Checking Test Report

If you want to get easy access to test report, run `mvn surefire-report:report` which generates report in html format in `target/site/surefire-report.html` for each module. Note that you have to execute tests first before you could generate report.

You could open the report in a browser for examination.

### Surefire Report

#### Summary

| Tests | Errors | Skipped | Success Rate |
| ----- | ------ | ------- | ------------ |
|       | 0      | 0       |              |

Note: failures are anticipated and checked for with assertions while errors are unanticipated.

#### Package List

| Package                    | Errors | Failures | Time  |
| -------------------------- | ------ | -------- | ----- |
| com.sismics.util.format    | 0      | 0        | 3.209 |
| com.sismics.docs.core.util | 0      | 0        | 2.733 |

## Test Coverage

Code coverage is a software metric used to measure how many parts of our code are executed during automated tests. In this tutorial, we'll use JaCoCo, a free code coverage reports generator for Java projects, to check the test coverage of Teedy.

First, add the following into the pom.xml of Teedy (you might want to manually reload the project to reflect the change):

```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.9</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
    <!-- attached to Maven test phase -->
    <execution>
      <id>report</id>
      <phase>test</phase>
      <goals>
        <goal>report</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Then, run `mvn test jacoco:report -Dmaven.test.failure.ignore=true`. Again, you need to run tests first in order to generate reports. `-Dmaven.test.failure.ignore=true` will ignore any failures occurred during test execution.

This will generate a coverage report at `target/site/jacoco/index.html` within each module. Open the report in a browser to navigate and observe the results.

### Docs Core Coverage

| Element                              | Cov. \*              | Missed Branches | Missed Lines |
| ------------------------------------ | -------------------- | --------------- | ------------ |
| com.sismics.util                     | 36%                  | 121 / 167       | 10           |
| com.sismics.docs.core.listener.async | 0%                   | 60              | 33 / 55      |
| com.sismics.docs.core.service        | 59%                  | 36 / 74         | 17           |
| org.apache.pdfbox.pdmodel.font       | 35%                  | 12%             | 76 / 123     |
| com.sismics.util.tdtp                | 0%                   | n/a             | 76           |
| com.sismics.docs.core.util.jpa       | 0%                   | 66              | 23           |
| com.sismics.docs.core.model.context  | 26%                  | 18 / 26         | 13           |
| com.sismics.util.context             | 16%                  | 11%             | 4            |
| com.sismics.docs.core.util.pdf       | 83%                  | 3               | 0            |
| **Total**                            | **15,368 of 20,011** | **115**         | **186**      |

JaCoCo mainly provides three important metrics:

- **Lines coverage**: reflects the amount of code that has been exercised based on the number of Java byte code instructions called by the tests.
- **Branches coverage**: shows the percent of exercised branches in the code, typically related to if/else and switch statements.
- **Cyclomatic complexity**: reflects the complexity of code by giving the number of paths needed to cover all the possible paths in a code through linear combination. This includes not only the conditional branches but also other control structures like loops and try-catch blocks.

Click any element to observe detailed code coverage.

### JaCoCo Report Color Rules

JaCoCo reports help us visually analyze code coverage by using diamonds with colors for branches, and background colors for lines:

- **Red diamond**: no branches have been exercised during the test phase.
- **Yellow diamond**: code is partially covered – some branches have not been exercised.
- **Green diamond**: all branches have been exercised during the test.

The same color code applies to the background color, but for lines coverage.

### Code Example

```java
/**
 * Update tags list on a document.
 * @param documentId Document ID
 * @param tagList Tag ID list
 */
private void updateTagList(String documentId, List<String> tagList) {
  if (tagList != null) {
    TagDao tagDao = new TagDao();
    Set<String> tagSet = new HashSet<>();
    Set<String> tagIdSet = new HashSet<>();
    List<TagDto> tagDtoList = tagDao.findByCriteria(new TagCriteria().setTargetIdList(getTargetIdList(null)), null);
    for (TagDto tagDto : tagDtoList) {
      tagIdSet.add(tagDto.getId());
    }
    for (String tagId : tagList) {
      if (!tagIdSet.contains(tagId)) {
        throw new ClientException("TagNotFound");
      }
      tagSet.add(tagId);
    }
    tagDao.updateTagList(documentId, tagSet);
  }
}
```

## Integrate Test Reports in Site Documentation

Finally, we may want to add test reports in our site documentation. Add the following in pom.xml:

```xml
<reporting>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-report-plugin</artifactId>
      <version>3.0.0</version>
      <reportSets>
        <reportSet>
          <id>aggregate</id>
          <inherited>false</inherited>
          <reports>
            <report>report</report>
          </reports>
          <configuration>
            <aggregate>true</aggregate>
          </configuration>
        </reportSet>
      </reportSets>
    </plugin>
    <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
      <version>0.8.9</version>
      <reportSets>
        <reportSet>
          <id>aggregate</id>
          <inherited>false</inherited>
          <reports>
            <report>report-aggregate</report>
          </reports>
        </reportSet>
      </reportSets>
    </plugin>
  </plugins>
</reporting>
```

Then run: `mvn clean test site -Dmaven.test.failure.ignore=true`. Open `target/site/index.html` in your browser. Now, you could navigate the site doc to explore the surefire and JaCoCo reports easily.

### Docs Core Site Doc Info

- Last Published: 2025-04-02
- Version: 1.12-SNAPSHOT
- Parent Project: Docs Parent

#### Project Documentation

- Project Information
- Project Reports
  - CPD
  - PMD
  - Surefire Report
  - JaCoCo
  - JaCoCo Aggregate

Built by: maven

#### Generated Reports Overview

| Report           | Description                      |
| ---------------- | -------------------------------- |
| CPD              | Duplicate code detection.        |
| PMD              | Verification of coding rules.    |
| Surefire Report  | Unit test execution report.      |
| JaCoCo Aggregate | Aggregated code coverage report. |

## References

- Maven Surefire documentation
- JaCoCo tutorial
