# Movie Database

A lightweight and efficient movie database application developed in Java.

## Introduction

MovieDatabase is a Java application designed to help users manage and explore their movie collections. This is part of my course work at JYU.


## Features

* **Manage Movies**: Users can add new movies to the database, edit their information, and remove them when necessary. Each movie entry includes details like title, director, release year, and other key information.

* **Actor Management**: When adding movies, new actors can be added to the database. These actors are then available for selection in future movie entries. This feature simplifies the process of managing actor information across multiple movies.

* **Genre Assignment**: Users can assign genres to each movie.

* **Search and Sort Functionality**: The application includes a search feature to find movies by titles. It also allows sorting the movie list based on different criteria, such as name, release year, or director.

* **Simple User Interface**: The application has a straightforward and user-friendly interface, making it suitable for educational purposes and personal use.

As a university project, Movie Database focuses on fundamental features, making it a great tool for learning and managing a basic collection of movies.


## Usage

### Prerequisites

Before you start, ensure you have the following installed:
- Java JDK (Java Development Kit)
- Maven, for managing project dependencies and building the application.
- An IDE (Integrated Development Environment) like IntelliJ IDEA, Eclipse, or similar.
- FXGUI.jar package, which can be found from [here](https://gitlab.jyu.fi/tie/ohj2/esimerkit/fxexamples/-/raw/master/FXGui/fxgui.jar). This is a JAR library provided by the University of Jyväskylä (JYU), commonly utilized in their programming courses. It offers additional functionalities for Java-based GUI development, simplifying various tasks and operations commonly encountered in academic projects.

### Setting Up the Project

1. **Clone or Download the Repository**: If you haven’t already, clone or download the MovieDatabase project repository to your local machine.

2. **Open the Project in an IDE**: Import the project into your preferred IDE. Most modern IDEs like IntelliJ IDEA or Eclipse have built-in support for Maven projects.

3. **Configure VM Options**: To ensure all parts of the application function correctly, you need to configure the VM options in your IDE. Go to your run configurations and add the following line to the VM options:

```
--add-opens javafx.base/com.sun.javafx.event=org.controlsfx.controls
 ```

This step is crucial for allowing certain JavaFX operations that are necessary for the application.

4. **Add Local Library Dependency**: The MovieDatabase project uses the `fxgui` library, which is a local dependency not available in public Maven repositories. To set up this library:

5. **Download the Library**: First, download the `fxgui.jar` file from [here](https://gitlab.jyu.fi/tie/ohj2/esimerkit/fxexamples/-/raw/master/FXGui/fxgui.jar).

6. **Install the Library to Your Local Maven Repository**:
   Use the following Maven command to install the library to your local Maven repository:

   ```bash
   mvn install:install-file -Dfile=path-to-fxgui.jar -DgroupId=fi.mit.jyu -DartifactId=fxgui -Dversion=1.0 -Dpackaging=jar
   ```

   Replace `path-to-fxgui.jar` with the actual path to the downloaded fxgui.jar file.

7. **Verify the Dependency in `pom.xml`**: Ensure that you `pom.xml` file contains the following dependency:

```xml
<dependency>
    <groupId>fi.mit.jyu</groupId>
    <artifactId>fxgui</artifactId>
    <version>1.0</version>
</dependency>
```

This dependency should match the groupId, artifactId, and version specified in the Maven install command.


### Building and Running the Application

1. **Build the Project**: Use Maven to build the project. You can do this within your IDE or by running `mvn clean install` in the command line within the project directory. This will compile the code and package it, typically into a `.jar` file.

2. **Run the Application**: Once built, you can run the application from your IDE or from the command line. To run from the command line, navigate to the location of the generated `.jar` file and execute:

```
java -jar MovieDatabase.jar
```

Replace `MovieDatabase.jar` with the actual name of the jar file if it is different.


## Contact

For any inquiries, feel free to contact me via email: [aaro.koinsaari@proton.me](mailto:aaro.koinsaari@proton.me) or connect in LinkedIn: [aarokoinsaari](https://www.linkedin.com/in/AaroKoinsaari).