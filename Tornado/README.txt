To compile this code properly using Maven you need to install the Disco depenecey and jar in your local 
maven repository
using the following statement
the jar file exists in the lib folder of this project

mvn install:install-file -Dfile=disco-2.0.jar -DgroupId=com.disco -DartifactId=disco -Dversion=10.2.0 -Dpackaging=jar