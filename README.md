# docker-window10-home
spring boot app with mysql in two containers

## Step 1 - install Docker on Windows 10 Home
### Dictionary

Docker Machine - a CLI tool for installing Docker Engine on virtual hosts\
Docker Engine - runs on top of the Linux Kernel; used for building and running containers\
Docker Client - a CLI tool for issuing commands to Docker Engine via REST API\
Docker Compose - a tool for defining and running multi-container applications\

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

=======
docker vm ports

Allow Docker to mount volumes located on your hard drive. In VirtualBox GUI select default VM and go to Settings > Shared Folders. Add a new one by clicking the plus symbol. Enter the fields like so. If there’s an option called Permanent, enable it.

=======
docker vm volumes

To get rid of the invalid settings error as seen in the above screenshot, simply increase Video Memory under the Display tab in the settings option\

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
export DOCKER_CERT_PATH="C:\Users\Michael Wanyoike\.docker\machine\machines\default"
export DOCKER_MACHINE_NAME="default"
export COMPOSE_CONVERT_WINDOWS_PATHS="true"
IMPORTANT: for the DOCKER_CERT_PATH, you’ll need to change the Linux file path to a Windows path format. Also take note that there’s a chance the IP address assigned might be different from the one you saved every time you start the default VM.
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
  empId VARCHAR(10) NOT NULL,
  empName VARCHAR(100) NOT NULL
);
```

```
In the application.properties file specify the datasource properties
spring.datasource.url=jdbc:mysql://localhost/bootdb?createDatabaseIfNotExist=true&autoReconnect=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.platform=mysql
spring.datasource.initialization-mode=always
```

Start the Spring Boot Application.
Open POSTMAN and create a POST request to url - 
```
localhost:8080/insertemployee with employee object to be persisted in DB.
```
Spring Boot Postman call
In the browser use 
```
localhost:8080/employees
```
to fetch the list of employees.
Spring Boot REST call

# Deploying Spring Boot + MYSQL to Docker-
Since are going to create two docker containers which should communicate with each other, we will need to start them on same network. We had seen the Docker Networking details in a previous tutorial. Open the terminal and start the docker
systemctl start docker
First lets create a network named employee-mysql
docker network create employee-mysql

spring boot mysql docker tutorial

mysql docker tutorial
MYSQL provides an image in dockerhub which we can run as container.
mysql docker tutorial
Ad by Valueimpression
We will use the image provided by dockerhub to run as container. Also we will specify following when running the container
a. name of the mysql container
a. What should be the password for MYSQL
b. We want to create the Database named bootdb.
c. specify the network employee-mysql on which this container should be created.
d. start the container in detached mode.
docker container run --name mysqldb --network employee-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=bootdb -d mysql:8

spring boot mysql docker start container
Next let us check if container has started correctly using logs command.
```
docker container logs -f ae
```

spring boot mysql docker container logs
Using the exec command we can also inspect if the database named bootdb is created successfully.
```
docker container exec -it ae bash
```

spring boot mysql docker container exec
Ad by Valueimpression
Next we will modify the application.properties in the Spring Boot application to make use of the mysql container name i.e.mysqldb instead of localhost
```
spring.datasource.url=jdbc:mysql://mysqldb/bootdb
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.platform=mysql
spring.datasource.initialization-mode=always
```
The docker file for spring boot project will be as follows-
```
From openjdk:8
copy ./target/employee-jdbc-0.0.1-SNAPSHOT.jar employee-jdbc-0.0.1-SNAPSHOT.jar
CMD ["java","-jar","employee-jdbc-0.0.1-SNAPSHOT.jar"]
```

docker-dockerhub
Build the docker image for the spring boot project
docker image build -t employee-jdbc .

spring boot mysql docker container image build
Ad by Valueimpression
Next run this as a container. Also we are running the container on the employee-mysql network.
docker container run --network employee-mysql --name employee-jdbc-container -p 8080:8080 -d employee-jdbc

spring boot mysql docker container image run
```
docker container logs -f 34 
```

spring boot mysql docker container image run logs
Both our containers have started successfully. Let us insert data with POST request using curl-
curl --header "Content-Type: application/json"   --request POST   --data '{"empId":"emp001","empName":"emp001"}'   http://localhost:8080/insertemployee

spring boot mysql docker container image run curl
Finally if we go to localhost:8080/employees we get the inserted records.
spring boot mysql docker container image run output
