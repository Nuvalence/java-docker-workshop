FROM openjdk:8

COPY build/libs/shadow.jar /opt/shadow.jar
ENV WORLD_NAME=World
EXPOSE 4567

CMD java -jar /opt/shadow.jar docker
