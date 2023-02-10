# Registration Client for Android
This document contains the instructions for setup,configuration, running and testing aspects of Registration Client for Android  
devices using IntelliJ /Android Studio.

Registration Client application is built with two different modules *app* and *clientmanager*.
* app contains the activites, views and UI components.
* clientmanager has all the services and other additional implementations. Below is the overview of the clientmanager module.
    * constant - error codes and other relevant messages stored in enumerated class form.
    * dao - code for DAO approach for database implementation on SQLite.
    * dto - has Data Transfer Objects (DTOs).
    * service - implementation of the sync and all the services used by the UI components.
    * spi - Interfaces defined to be used in UI components/activities.
    
## Prerequisites
### System Requirements
* 64-bit Microsoft Windows 10 OS.
* x86_64 CPU architecture; 2nd generation Intel Core or higher/AMD CPU with support for a Windows Hypervisor.
* 8 GB RAM or more.
* 8 GB of available disk space minimum (IDE + Android SDK + Android Emulator)

### Application Requirements
To run the application the following are required:
* Intellij version 2020.3.+ (or) Android Studio version 2020.3.+
* Android SDK 31.0
* JDK 1\.8 or higher


## Run project 
Download the project repository from Github. 
In intelliJ IDE or Android Studio, go to file->open and choose the project build.gradle file (in "client" directory). 
Choose the option to load the entire project. 
Wait until the project is loaded in the IDE. 
Configure the sdk version as prompted. Sync the build.gradle files to download the project dependencies.
Build and run the project.

## Dependency Injection

### Dagger2 - Dependency Injection and Inversion of Control
The project uses Dagger as the IoC framework.

#### resources
[Dagger](https://github.com/google/dagger)
[Documentation](https://developer.android.com/training/dependency-injection/dagger-android)
[Video](https://youtube.com/playlist?list=PLgCYzUzKIBE8AOAspC3DHoBNZIBHbIOsC)

### Lombok
Project uses lombok for boilerplate code reduction.

#### resources
[Lombok](https://projectlombok.org/)
[Documentation](https://projectlombok.org/features/all)

### Room for Android - Room persistence Library
The project uses Room library from Android Jetpack for fluent access to SQLite Database access with DAO approach

#### to use Android Room
To use Android Room, we need three components: Entities, DAO and the database itself. 
We need to create Entities with appropriate annotations describing the schema. 
To run the actual methods on entities,create DAO classes.Describe a database and create an instance to store the entities in and run methods on.

#### resources
[Documentation](https://developer.android.com/training/data-storage/room)
[Reference](https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942)

### JUnit - Open Source Java Unit Testing Tool
The project uses JUnit for unit testing the code.

#### to use JUnit
To test the code, create a test class and initialize the objects on which methods are to be run. 
Use appropriate assert statements provided by JUnit to verify the method outputs.

#### resources
[Documentation](https://developer.android.com/training/testing/unit-testing/local-unit-tests)

