This project was developed using Eclipse Luna and the Gradle plug-in.

WHAT APPEARS BELOW REQUIRES MORE WORK TO BE ACCURATE

## Running the Application

Please read the instructions carefully.

To run the application:

1. (Menu Bar) Run->Run Configurations
2. Under Java Applications, select your run configuration for this app
3. Open the Arguments tab
4. In VM Arguments, provide the following information to use the
   default keystore provided with the sample code:

   -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit

5. Note, this keystore is highly insecure! If you want more security, you 
   should obtain a real SSL certificate:

   http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
   
6. This keystore is not secured and should be in a more secure directory -- preferably
   completely outside of the app for non-test applications -- and with strict permissions
   on which user accounts can access it

## Accessing the Service

Note: you need to use "https" and port "8443":

https://localhost:8443/video

You will almost certainly see a warning about the site's certificate in your browser. This
warning is being generated because the keystore includes a certificate that has not been
signed by a certificate authority. 

If you try to access the above URL in your browser, the server is going to generate an error 
that looks something like "An Authentication object was not found in the SecurityContext." 
If you want to use your browser to test the service, you will need to use a plug-in like 
Postman and an understanding of how to use it to manually construct and obtain a bearer token.

The VideoSvcClientApiTest shows how to programmatically access the video service. You should
look at the SecuredRestBuilder class that is used to automatically intercept requests to the
VideoSvcApi methods, automatically obtain an OAuth 2.0 bearer token if needed, and add this
bearer token to HTTP requests. 


To run the application:

Right-click on the Application class in the org.magnum.mobilecloud.video
package, Run As->Java Application

To stop the application:

Open the Eclipse Debug Perspective (Window->Open Perspective->Debug), right-click on
the application in the "Debug" view (if it isn't open, Window->Show View->Debug) and
select Terminate

## Accessing the Service

To view a list of the videos that have been added, open your browser to the following
URL:

http://localhost:8080/video

To add a test video, run the VideoSvcClientApiTest by right-clicking on it in 
Eclipse->Run As->JUnit Test (make sure that you run the application first!)

## What to Pay Attention to

In this version of the VideoSvc application, we have added dependency injection:

1. The VideoRepository interface defines the application's interface to the database.
   There is no implementation of the VideoRepository in the project, Spring dynamically
   creates the implementation when it discovers the @Repository annotated interface.
2. This "videos" member variable of the VideoSvc is automatically auto-wired with the
   implementation of the VideoRepository that Spring creates. 
3. The VideoRepository inherits methods, such as save(...), that are defined in the
   CrudRepository interface that it extends. 
4. The "compile("com.h2database:h2")" line in the build.gradle file adds the H2 database
   as a dependency and Spring Boot automatically discovers it and embeds a database 
   instance in the application. By default, the database is configured to be in-memory
   only and will not persist data across restarts. However, another database could
   easily be swapped in and data would be persisted durably.
5. Notice that the VideoRepository is automatically discovered by Spring.
