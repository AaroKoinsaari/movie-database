# Movie Database

This is a lightweight and efficient small movie database application developed in Java.

## Introduction

MovieDatabase is a Java application designed to help users manage and explore their movie collections. This is part of coursework at JYU.

## Features

- **Manage Movies**: Users can add new movies to the database, edit their information, and remove them when necessary. Each movie entry includes details like title, director, release year, and other key information.

- **Actor Management**: When adding movies, new actors can be added to the database. These actors are then available for selection in future movie entries.

- **Genre Assignment**: Users can assign genres to each movie.

- **Search and Sort Functionality**: The application includes a search feature to find movies by titles. It also allows sorting the movie list based on different criteria, such as name, release year, or director.

## Usage

### Prerequisites

Before you start, ensure you have the following installed:

- Java JDK (Java Development Kit)
- Maven, for managing project dependencies and building the application.
- A modern IDE like IntelliJ IDEA, Eclipse, or similar which includes support for JavaFX and Maven projects.
- FXGUI.jar package, which can be found from [here](https://gitlab.jyu.fi/tie/ohj2/esimerkit/fxexamples/-/raw/master/FXGui/fxgui.jar). This is a JAR library provided by the University of Jyväskylä (JYU), commonly utilized in their programming courses. It offers additional functionalities for Java-based GUI development, simplifying various tasks and operations commonly encountered in academic projects.

### Setting Up the Project

1. **Clone the Repository**: Clone the MovieDatabase repository to your local development environment:

2. **IDE configuration**: Open the project in an IDE that supports Maven projects, such as IntelliJ IDEA or Eclipse. Ensure the IDE is configured to handle JavaFX and Maven dependencies.

3. **Configure VM Options**: Adjust VM options for compatibility with JavaFX. In your IDE’s run configurations, add the following:

   ```bash
   --add-opens javafx.base/com.sun.javafx.event=org.controlsfx.controls
   ```

   This step is crucial for allowing certain JavaFX operations that are necessary for the application.

4. **Install Local Library**: The MovieDatabase project uses the `fxgui` library, which is a local dependency not available in public Maven repositories. To set up this library download it from [here](https://gitlab.jyu.fi/tie/ohj2/esimerkit/fxexamples/-/raw/master/FXGui/fxgui.jar).

5. **Install the Library to Your Local Maven Repository**:
   Use the following Maven command to install the library to your local Maven repository:

   ```bash
   mvn install:install-file -Dfile=path-to-fxgui.jar -DgroupId=fi.mit.jyu -DartifactId=fxgui -Dversion=1.0 -Dpackaging=jar
   ```

6. **Verify the Dependency in `pom.xml`**: Ensure that you `pom.xml` file contains the following dependency:

   ```xml
   <dependency>
       <groupId>fi.mit.jyu</groupId>
       <artifactId>fxgui</artifactId>
       <version>1.0</version>
   </dependency>
   ```

This dependency should match the groupId, artifactId, and version specified in the Maven install command.

### Building and Running the Application

1. **Build the Project**: Build the project using Maven:

   ```bash
   mvn clean install
   ```

   This will compile the source code, resolve dependencies, and generate the executable JAR.

2. **Run the Application**: Run the application using the generated JAR file:

   ```
   java -jar MovieDatabase.jar
   ```

Replace MovieDatabase.jar with the actual name of the output JAR if it differs.

## Contact

For any inquiries, feel free to contact me via email: [aaro.koinsaari@proton.me](mailto:aaro.koinsaari@proton.me) or connect in LinkedIn: [aarokoinsaari](https://www.linkedin.com/in/AaroKoinsaari).
