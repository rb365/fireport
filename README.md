Fireport
========
Forward ports through Firebase.

BEWARE: this is not secure at all!!!

Sample usecase: connect to VNC server behind router

1. Run VNC Server on machine1
2. Run fireport on machine1: java -jar fireport.jar server localhost 5900
3. Run fireport on machine2: java -jar fireport.jar client localhost 5901
4. Run VNC Viewer on machine2 and connect to localhost:5901
5. Enjoy!

Build instructions:
- download and install [java](http://java.com/en/download/index.jsp)
- download and install [maven](http://maven.apache.org/download.cgi)
- clone the [repository](https://github.com/vskarine/fireport)
- compile and generate jar: mvn clean compile assembly:single
- run the jar: java -jar target/fireport-1.0-SNAPSHOT-jar-with-dependencies.jar

