FROM openjdk:11
WORKDIR /app
COPY build/libs/telegram-printeg-bot-1.0-SNAPSHOT.jar /app/printer-bot-app.jar
ENTRYPOINT [ "java","-jar","/app/printer-bot-app.jar" ]
