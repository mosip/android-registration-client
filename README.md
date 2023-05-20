# android-registration-client
Reference Android Registration Client Software - WIP

# Use of Java Packages:
    1. Added all the java packages in android directory of the project: <package-name>
    2. Add implementation in settings.gradle file of the project:
        Eg: include(:<package-name>)
    3. Set the server base url in  android/build.gradle file.
    4. Add implementation for packages in android/app/build.gradle files inside dependency block as: 
        implementation project(:<package-name>)

# Creation of Services in  the project's java package.
    1. Added all the required functions by creating individual services for each functionality.
    2. Using AppComponent interface which inherits Dagger for dependecny injection.
    3. Dependency injection performed in MainActivity.java file by calling inject() method.

# Creation of MethodChannels in dart classes.
    1. Create MethodChannel with a constant string name in a dart class and try to invoke a method 
        provided by this channel.
    2. This invoke method will take the function name and arguments which will be sent to native side
        for getting a response.
    3. The function name in invoke method and on native side should be the same as:
        methodChannel.invokeMethod("fetchDetails");
    4. On native side, in MainActivity file, inside MethodChennel() use a switch case to check for method name
        call.method argument which checks "fetchDetails" case. On matching, it implements "fetchDetails" method.
        case "fetchDetails:
            Service().fetchDetails(result);
            break;

# Fetch Machine Details
    1. Method channel on Credential Page invokes "getMachineDetails" method and gets a response from
        native side as a string. 
    2. Then this string can be decoded into a map<String, dynamic> and the details can be displayed
        on a page.  
    3. It also contains functionality to copy the details and save a file containing details in internal storage.z
