# Cats as a service

This project contains an [HTTP service](src/main) called **cats-as-a-service** that provides data related to some cats stored in a [SQLite database](src/main/resources/cats.db).

The API is only intended to serve as a target for the [Karate test suite](https://github.com/arcones/cats-service-tests) so every design decision regarding its code has been took to provide a rich playground where **Karate features** can show all their capabilities.

## Prerequisites
- [Java 8 or above](https://www.java.com) should be installed on your machine in order to run either the server or the test suite.
- Have at least a look at [Karate project](https://github.com/intuit/karate)
> The project uses [gradle](https://gradle.org/) as build tool but it comes **bundled** in its [wrapper](gradle) so no need to have it installed in your machine 

## Execution
These are the steps needed to run the project in any Unix-based terminal.
The steps can be run equivalently with Windows machines replacing ```./gradlew``` by ``gradle`` in each command.

1) Clean old binaries and compile the project with:
    ```bash
    ./gradlew clean build
    ```
2) Then, you will be able to run the HTTP server with:
    ```bash
    ./gradlew run
    ```
    It will log every call received and processed.