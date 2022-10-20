# tut-java-junit5
Uber Simple JUnit 5 Implementation Examples



## pom.xml

- by using MAVEN dependencyManagement junit-bom artifact, the versioning is 'automatically' handled by the bom pom.xml
- this is particularly useful when using multiple org.junit packages - their version element can be omitted so the bom handles it

```
	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.junit</groupId>
				<artifactId>junit-bom</artifactId>
				<version>5.9.1</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<!-- Include the following dependencies -->
	<dependencies>
		<dependency>
			<groupId>org.junit.jupiter</groupId>
			<artifactId>junit-jupiter</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

## NOTE!!! testResources needs additional testResource element for JUNIT CsvFileSource files

- nearly had the keyboard thrown across the room triaging why the JUnit tests couldn't pick up the csv file
- it was working in another non-maven Java project, but in the maven project it was posing a challenge
- 

	@CsvFileSource(resources = "/mycsv.csv", numLinesToSkip = 1)

- documentation alluded to the fact that JUnit would search in the test/resources folder

	/tut-java-junit5/src/test/resources/mycsv.csv
	
- maven 'breaks' this in the way it manages builds:

				<targetPath>${basedir}/target/src/test/resources</targetPath>
				<directory>${basedir}/src/test/resources</directory>
- JUnit tests look for this file as a sibling & not in 'test/resources', so the following ADDTIONAL testResource spec is needed

				<targetPath>${basedir}/target/test-classes</targetPath>
				<directory>${basedir}/src/test/resources</directory>
				

```
		<!--
			NORMAL MAVEN testResource SPEC
		-->
		<testResources>

			<testResource>
				<excludes>
					<exclude>**/~*.xlsx</exclude>
					<exclude>**/~*.log</exclude>
				</excludes>
				<targetPath>${basedir}/target/src/test/resources</targetPath>
				<directory>${basedir}/src/test/resources</directory>
				<filtering>false</filtering>
			</testResource>

			<!-- 
				because JUNIT5 is stupid with how it looks for CsvFileResource, 
				add another test-resource element for copying it to test-classes folder
			-->
			<testResource>
				<includes>
					<include>**/*.csv</include>
				</includes>
				<targetPath>${basedir}/target/test-classes</targetPath>
				<directory>${basedir}/src/test/resources</directory>
				<filtering>false</filtering>
			</testResource>
		</testResources>

```