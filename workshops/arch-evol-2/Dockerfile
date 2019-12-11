FROM openjdk:8

COPY build/libs/shadow.jar /opt/shadow.jar
EXPOSE 4567

ENV TABLE_NAME=nuvalence-workshop-table
ENV BUCKET_NAME=nuvalence-workshop-documents

CMD java -jar /opt/shadow.jar docsapi
