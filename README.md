# docker-windows10-Home
spring boot app with mysql in two containers

## Step 1 - install Docker on Windows 10 Home
### Dictionary

Docker Machine - a CLI tool for installing Docker Engine on virtual hosts\
Docker Engine - runs on top of the Linux Kernel; used for building and running containers\
Docker Client - a CLI tool for issuing commands to Docker Engine via REST API\
Docker Compose - a tool for defining and running multi-container applications

### Initial Setup
* Install Git Bash 
This will be our primary terminal for running Docker commands.

* Install Chocolatey 
It will make the work of installing the rest of the programs easier.

* Install VirtualBox 
```
C:\ choco install virtualbox
```

### Docker Engine Setup
Install Docker Machine. You can execute this command inside an elevated PowerShell terminal:
```
C:\ choco install docker-machine
```

Using Git Bash terminal, use Docker Machine to install Docker Engine. This will download a Linux image containing the Docker Engine and have it run as a VM using VirtualBox. 
```
$ docker-machine create --driver virtualbox default
```

Configure which ports are exposed when running Docker containers. To do this, you’ll need to launch Oracle VM VirtualBox from your start menu. Select default VM on the side menu. Next click on Settings > Network > Adapter 1 > Port Forwarding. You should find the ssh forwarding port already set up for you. You can add more like so:

![alt text](https://github.com/idmitrymolchanov/spring-boot-docker-example/blob/for_annotation/img/ports.png "ports")

Allow Docker to mount volumes located on your hard drive. In VirtualBox GUI select default VM and go to Settings > Shared Folders. Add a new one by clicking the plus symbol. Enter the fields like so. If there’s an option called Permanent, enable it.

To get rid of the invalid settings error as seen in the above screenshot, simply increase Video Memory under the Display tab in the settings option

![alt text](https://github.com/idmitrymolchanov/spring-boot-docker-example/blob/for_annotation/img/screen_set.png "screen_s")

Start the Linux VM\Give it some time for the boot process to complete. It shouldn’t take more than a minute. You’ll need to do this every time you boot your host OS:
```
$ docker-machine start vbox
```

Next, need to set up Docker environment variables\
This is to allow the Docker client and Docker Compose to communicate with the Docker Engine running in the Linux VM, default.\
in Git Bash:

* Print out docker machine instance settings
```
$ docker-machine env default
```

* Set environment variables using Linux 'export' command
```
$ eval $(docker-machine env default --shell linux)
```

You’ll need to set the environment variables every time you start a new Git Bash terminal. If you’d like to avoid this, you can copy eval output and save it in your .bashrc file. It should look something like this:
```
export DOCKER_TLS_VERIFY="1"
export DOCKER_HOST="tcp://192.168.99.101:2376"
export DOCKER_CERT_PATH="C:\***\default"
export DOCKER_MACHINE_NAME="default"
export COMPOSE_CONVERT_WINDOWS_PATHS="true"
```

### Install Docker Client and Docker Compose

Docker Tools Setup\
Using PowerShell in admin mode using Chocolatey:

```
C:\ choco install docker-cli
C:\ choco install docker-compose
```

Switch back to Git Bash terminal

* Start Docker VM
```
$ docker-machine start default
```

* Confirm Docker VM is running
```
$ docker-machine ls
```

* Configure Docker Envrionment to use Docker Vm
```
$ eval $(docker-machine env default --shell linux)
```

* Confirm Docker is connected. Should output Docker VM specs
```
$ docker info
```

* Run hello-world docker image. Should output "Hello from Docker"
```
$ docker run hello-world
```

# Create Spring Boot + MYSQL Application
Create the schema-mysql.sql file and specify the initialization scripts-
```
CREATE TABLE IF NOT EXISTS employee (
  id VARCHAR(10) NOT NULL,
  firstName VARCHAR(100) NOT NULL,
  lastName VARCHAR(100) NOT NULL,
  age INT
);
```

In the application.properties file specify the datasource properties
```
spring.datasource.url=jdbc:mysql://localhost/project_db?useUnicode=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.platform=mysql
spring.datasource.initialization-mode=always
```

Start the Spring Boot Application.
Open POSTMAN and create a POST request to url - 
```
localhost:8080/insert
```
![alt text](https://github.com/idmitrymolchanov/spring-boot-docker-example/blob/for_annotation/img/postman_post.png "Postman POST")

Spring Boot Postman call\
In the browser or in Postman again use 
```
localhost:8080/employees
```
![alt text](https://github.com/idmitrymolchanov/spring-boot-docker-example/blob/for_annotation/img/postman_get.png "Postman GET")


# Deploying Spring Boot + MYSQL to Docker

Firstly create a network named employee-mysql
```
start docker
docker network create employee-mysql
```

Check that you created
```
docker network ls
```

Next step: write config data of your db
```
docker container run --name mysqldb --network employee-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=project_db -d mysql:5
```

And start it
```
docker container start mysqldb
```

Using the exec command we can inspect if the database named project_db is created successfully.
```
docker container exec -tty [name or id container] mysql -uroot -proot "show databases"
```

Next we will modify the application.properties in the Spring Boot application to make use of the mysql container name i.e.mysqldb instead of localhost
```
spring.datasource.url=jdbc:mysql://mysqldb/project_db
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.platform=mysql
spring.datasource.initialization-mode=always
```

The docker file for spring boot project will be as follows
```
From openjdk:8
copy ./target/spring-boot-docker-example-1.0-SNAPSHOT.jar spring-boot-docker-example-1.0-SNAPSHOT.jar
CMD ["java","-jar","spring-boot-docker-example-1.0-SNAPSHOT.jar"]
```

Build the docker image for the spring boot project
```
docker image build -t spring-boot-docker-example .
```

Next run this as a container. Also we are running the container on the employee-mysql network.
docker container run --network employee-mysql --name employee-jdbc-container -p 8080:8080 -d spring-boot-docker-example

spring boot mysql docker container image run
```
docker container logs -f 34 
```

![alt text](https://github.com/idmitrymolchanov/spring-boot-docker-example/blob/for_annotation/img/run_app.png "run_app")

Insert data with POST request using curl
```
curl --header "Content-Type: application/json"   --request POST   --data '{"empId":"emp001","empName":"emp001"}'   http://localhost:8080/insertemployee
```
