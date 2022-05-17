# Usage manual
This one is specially for linux users, struggling for setting up JDBC in their system.
### My environment
1. java 11 amazon corretto as my default JVM (you can use any JAVA 11 JDK)
2. Eclipse as IDE. 
3. Ubuntu 20.04.4 LTS as my Operating system (check yours via- lsb_release -a)

### Initial Steps
- Firstly you have to install one JAR file from [here](https://dev.mysql.com/downloads/connector/j/) 
- Give your Operating system's detail and then download the package accordingly
- Install the package.
- all the jar files are then stored inside this directory `usr/share/java`
- Over there you will find one file called "mysql-connector-java-x.x.xx.jar" you have to refer to this library externally.
- if you are not able to find out the file then there is one more option
- go to the terminal type `sudo apt-get install libmariadb-java` this package now contains for both `mariadb` and `mysql`.
- Now you have to open Eclipse, in your existing project(the project must be created via eclipse only otherwise you will not get the java build option).
- In the package explorer right click the project go to properties.
- Select 'JAVA Build Path'> 'libraries'> 'classpath'; here you add external jar file, go to `usr/share/java/mysql-connector-java-x.x.xx.jar`
- then apply and close

All the external JAR files are added in the Referenced Libraries

- now you can add [this file CRUD_JDBC.java](https://github.com/TanmayKumar-EngStud/JDBC-CRUD/blob/main/CRUD_JDBC.java)
- check out how I have used the created operations in `public class CRUD_JDBC` as a reference of how to use the above created class (which is `Mysql`)
- After understanding the reference, you can remove `public class CRUD_JDBC` and can use `class Mysql` directly in your respective class.

### As of now I have covered only two types of datatypes, i.e "varchar" & "int", I will add other datatype features later.

I have also added the sql query, in order to check how the file is working.
one more thing, I have to change the return type of these methods, so that you guys can store the return in variables directly.
