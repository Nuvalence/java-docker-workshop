# Docker Java Workshop
In this workshop, we will containerize a simple Hello World http app. The application
runs as a single executable jar file. Details on [building](#building-the-application) 
and [running](#running-the-application) the application
can be found below.

## Exercise
For this exercise, our goal is to move the Hello World app to a container. We will
* create a Dockerfile 
* build an image
* execute the container

Next, we'll explore docker-compose and run a caching container that our application
can interact with.
* create a docker-compose.yml for the application
* add the redis image to our docker-compose.yml
* set environment variable for the app to talk to redis
* use docker-compose to run multiple containers (1 app, 1 redis)

### Creating the Dockerfile
First, we need to create a Dockerfile for our app.
* The FROM image should be the java 8 openjdk
* All relevant files need to be added to the image
* Document published ports (default http for the app is 4567)

### Building the image
* Specify a name for your image

### Running a container
* Set environment variables
* Don't forget to map ports

## Hello World API
The hello world application presents a simple http API with two endpoints.
* `GET /hello` will return a hello world message
* `GET /cache` will validate redis connectivity

### Building the application
To build the application, execute the `build` task using one of the following commands
in the root of the repository:

Windows:
```shell
gradlew.bat build
```

Mac and Linux:
```shell
./gradlew build
```

The build will produce two jar files in the `build/libs/` directory.

### Running the application
To run the application, execute the shadow jar file (More information on 
[shadow](https://imperceptiblethoughts.com/shadow/introduction/)):

```shell
java -jar build/libs/shadow.jar
```

