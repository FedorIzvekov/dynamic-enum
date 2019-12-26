# Dynamic Enum Reflection

The goal of this project is to explore the capabilities of Java 11 and reflection by dynamically creating enums at runtime. This project is
purely experimental and not intended for use in real-world projects.


## Environment Setup

You'll need JDK 11 to work with this project.


## Building and Running

1. Navigate to the project directory:
   ```
   cd <projects_directory>/dynamic-enum
   ```

2. Build the project by executing the following command in the project directory:
   ```
   ./gradlew build
   ```

3. Run the project using this command in the terminal:
   ```
   java --add-opens=java.base/jdk.internal.reflect=ALL-UNNAMED \
       --add-opens=java.base/java.lang=ALL-UNNAMED \
       --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
       -jar build/libs/dynamic-enum-1.0.0-SNAPSHOT.jar
   ```


## Checking
To retrieve all enums:
   ```
    curl -v http://localhost:8080/enums
   ```

To add a new enum:
   ```
   curl -v -X PUT http://localhost:8080/enums/<enum_name>
   ```