From openjdk:8
copy ./target/spring-boot-docker-example-1.0-SNAPSHOT.jar spring-boot-docker-example-1.0-SNAPSHOT.jar
CMD ["java","-jar","spring-boot-docker-example-1.0-SNAPSHOT.jar"]