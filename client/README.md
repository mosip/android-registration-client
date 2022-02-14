# Registration Client for Android
This document contains the instructions for setup,configuration, running and testing aspects of Registration Client for Android  
devices using IntelliJ /Android Studio.

This is an android application that allows to encrypt, decrypt, sign, and verify functionalities using RSA based algorithms.
It uses multiple programming approaches to achieve this such as dependency injection for services,aspect oriented programming etc. 
It also contains implementation of RestServices using *Fast* Android Networking and a basic SQLite database access using Room for Android.
## Application Build
OpenJDK version "8.0" version is used to build the application.

Below is the file structure of the application.
```
android-registration-client-develop
│
└client
│    │
│    ├──app
│    │      ├─ src
│    │            ├─main
│    │               ├─ java
│    │                    ├─ io
│    │                        ├─ mosip
│    │                            ├─ registration
│    │                                   ├─ BaseApplication.java
│    │                                   ├─ MainActivity.java  
│    │                                   └─ di 
│    │                                   
│    │
│    │
│    ├──clientmanager
│            ├─ src
│                ├─main
│                    ├─ java
│                    │    ├─ io
│                    │       ├─ mosip
│                    │           ├─ registration
│                    │              ├─ clientmanager
│                    │                  ├─ constant
│                    │                  ├─ database
│                    │                  ├─ dto
│                    │                  ├─ service
│                    │                  ├─ spi
│                    │                  └─ util
│                    │
│                    ├─ res 
│                        ├─ xml
│                             ├─ network_security_config.xml
```
Registration Client application is built with two different modules *app* and *clientmanager*.
* app contains the android application code and dependency injection for clientcrypto services (in the *di* subfolder).
* clientmanager has the implementations of clientcrypto services and other additional implementations.Below is the overview of the clientmanager module.
    * constant - error codes and other relevant messages stored in enumerated class form.
    * database - code for DAO approach for database implementation on SQLite.
    * dto - has Data Transfer Objects (DTOs) for RestServices as well as CLientCrypto services.
    * service - implementation of the clientcrypto service.
    * util - it contains implementation of RestServices using FAST Android Networking and sample TestAdvice.
    * network_security_config.xml - stores the http domains FAST Android Networking is allowed to communicate using cleartext
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

## Dependencies

### Dagger2 - Dependency Injection and Inversion of Control
The project uses Dagger as the IoC framework.

#### build.gradle 
Add the following to dependencies in the module build file
```
implementation 'com.google.dagger:dagger:2.38.1'
implementation 'com.google.dagger:dagger-android-support:2.38.1'
annotationProcessor 'com.google.dagger:dagger-compiler:2.38.1'
annotationProcessor 'com.google.dagger:dagger-android-processor:2.38.1'
```

#### to use IoC
By convention create the di package in the appropriate module. Add relevant app services in AppModule. 
Add relevant activity services in similar activity modules.

#### resources
[Dagger](https://github.com/google/dagger)
[Documentation](https://developer.android.com/training/dependency-injection/dagger-android)
[Video](https://youtube.com/playlist?list=PLgCYzUzKIBE8AOAspC3DHoBNZIBHbIOsC)

### AspectJ - Aspect-Oriented Programming
The project uses AspectJ as the AOP framework.

#### build.gradle
Add the following to dependencies in the module build file
```
implementation 'org.aspectj:aspectjrt:1.9.7'
```

Add the following to plugins in the module build file
```
id 'com.ibotta.gradle.aop'
```

Add the following to the buildscript in the project build file
```
buildscript {
...
repositories {
...
maven { url "https://plugins.gradle.org/m2/" }
...
}

	dependencies {
		...
		classpath "com.ibotta:plugin:1.2.0"
		...
	}
}
```

#### to use AOP
Create a name-Advice.java with required annotations anywhere in the module.

#### resources
[Gradle-aspectj-pipeline-plugin](https://github.com/Ibotta/gradle-aspectj-pipeline-plugin)
[Aspectj](https://docs.spring.io/spring-framework/docs/3.0.0.M4/reference/html/ch07s02.html)

### Fast Android Networking - Android Networking Library
The project uses Fast Android Networking(FAN) for its rest services.  

#### build.gradle
Add the following to dependencies in the module build file
```
implementation 'com.github.amitshekhariitbhu:Fast-Android-Networking:1.0.2'
```

Add the additional resource download location in dependencyResolutionManagement of projects gradle setting
``` 
dependencyResolutionManagement {
...
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
    ...
    maven { url "https://jitpack.io" }
    ....
    }
...
}
``` 
#### to use AOP
First initialize FAN with the application's context. Call either synchronous or asynchronous network operations.
Android Studio by default does not allow http clear text connections. To request with http connection you need to add appropriate permissions.
Permissions can be updated in network_security_config.xml.

#### resources
[Fast Android Networking](https://github.com/amitshekhariitbhu/Fast-Android-Networking)
[Documentation](https://amitshekhar.me/Fast-Android-Networking/)
[Async Methods](https://amitshekhar.me/Fast-Android-Networking/get_request.html)
[Sync Methods](https://amitshekhar.me/Fast-Android-Networking/synchronous_request.html)


### Lombok
Project uses lombok for boilerplate code reduction.

#### build.gradle
Add the following to dependencies in the module build file
```
compileOnly 'org.projectlombok:lombok:1.18.20'
annotationProcessor 'org.projectlombok:lombok:1.18.20'
```

#### to use lombok
Add annotations at appropriate places.

#### resources
[Lombok](https://projectlombok.org/)
[Documentation](https://projectlombok.org/features/all)

### Room for Android - Room persistence Library
The project uses Room library from Android Jetpack for fluent access to SQLite Database access with DAO approach

#### build.gradle
Add the following to dependencies in the module build file
```
    implementation "androidx.room:room-runtime:2.3.0"
    annotationProcessor "androidx.room:room-compiler:2.3.0"
```
#### to use Android Room
To use Android Room, we need three components: Entities, DAO and the database itself. 
We need to create Entities with appropriate annotations describing the schema. 
To run the actual methods on entities,create DAO classes.Describe a database and create an instance to store the entities in and run methods on.

#### resources
[Documentation](https://developer.android.com/training/data-storage/room)
[Reference](https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942)

### JUnit - Open Source Java Unit Testing Tool
The project uses JUnit for unit testing the code.

#### build.gradle
Add the following to dependencies in the module build file
```
testImplementation "junit:junit:4.13.2"
androidTestImplementation 'androidx.test.ext:junit:1.1.3'
```

Add the following to the project build file
```
ext {
    ...
    junitVersion = "4.+"
    ...
}
```

#### to use JUnit
To test the code, create a test class and initialize the objects on which methods are to be run. 
Use appropriate assert statements provided by JUnit to verify the method outputs.

#### resources
[Documentation](https://developer.android.com/training/testing/unit-testing/local-unit-tests)

