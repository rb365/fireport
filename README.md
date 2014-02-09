Fireport
========
Forward ports behind router/firewall through [Firebase](https://www.firebase.com/).

BEWARE: this is not secure at all!!!

Sample usecase: connect to VNC server behind router
vnc client <---> Fireport server <---> Firebase <---> Fireport client <---> vnc server

1. Run VNC Server on machine1
2. Run Fireport client on machine1: java -jar fireport.jar vnc-test client 5900
3. Run Fireport server on machine2: java -jar fireport.jar vnc-test server 5900
4. Run VNC Viewer on machine2 and connect to localhost
5. Enjoy!

Build instructions:
- download and install [java](http://java.com/en/download/index.jsp)
- download and install [maven](http://maven.apache.org/download.cgi)
- clone the [repository](https://github.com/vskarine/fireport)
- compile and generate jar: mvn clean compile assembly:single
- run the jar: java -jar target/fireport-1.0-SNAPSHOT-jar-with-dependencies.jar

