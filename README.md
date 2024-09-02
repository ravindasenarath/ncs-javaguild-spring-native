# README for NCS Java Guild, Latest and greatest of Spring Framework, Spring Native

Please note that commands are for a Linux environment

### Reference videos

 - [Going Native: Fast and Lightweight Spring Boot Applications with GraalVM by Alina Yurenko](https://youtu.be/8umoZWj6UcU?si=dEnlUQIW0OBTZcTc)
 - [A 1.5MB Java Container App? Yes you can! by Shaun Smith](https://youtu.be/6wYrAtngIVo?si=kPX5X6t8H5LrGZJf)
 - [Bootiful GraalVM by Josh Long and Alina Yurenko](https://youtu.be/3OBhk1c0GBs?si=hCFgRgOOFTfF3v-u)

### Prerequisites
 - Have GraalVM jdk 21 installed and set as current jdk ( download [here](https://www.graalvm.org/downloads/) )
 - Have docker installed and running

### Run with spring boot
```
./mwnw spring-boot:run
```

### Run the fat jar file standalone
Build the artifact
```
./mvnw clean package -DskipTests
```

Run the postgres container 
```
docker compose up
```
Run the jar file
```
./target/movie-service-0.0.1-SNAPSHOT.jar
```

### Run the fat jar in a docker container

Build the docker images
```
docker build -t movie-service:1.0.0 .
```
Create a docker network
```
docker network create postgres-network
```
Connect the postgres container to the network ( Run `docker ps` to find the container name. eg `movie-service-new-postgres-1`)
```
docker network connect postgres-network movie-service-new-postgres-1
```
Run the image
```
docker run --rm -p 8080:8080 --net postgres-network -e P_HOST=movie-service-new-posgtres-1 P_PORT=5432 movie-service:1.0.0
```

### Build to native executable

```
./mvnw clean native:compile -Pnative -DskipTests
```
Run the native executable
```
./target/movie-service
```

### Build native executable to a container
Spring will use your `version` from the pom file to name the image ( eg `movie-service:0.0.1-SNAPSHOT`)
```
./mvnw -Pnative spring-boot:build-image
```
Run the image
```
docker run --rm -p 8080:8080 --net postgres-network -e P_HOST=movie-service-new-posgtres-1 P_PORT=5432 movie-service:0.0.1-SNPASHOT
```

### Compress with UPX
```
upx -o movie-service-compressed movie-service 
```