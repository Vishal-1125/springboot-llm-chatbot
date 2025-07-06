FROM openjdk:17-jdk-slim

VOLUME [ "/logs" ]

ARG JAR_FILE=/target/chatbot-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /chatbot.jar

ENTRYPOINT [ "java" , "-jar" , "chatbot.jar" ]