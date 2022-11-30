FROM adoptopenjdk/openjdk11:alpine-slim

RUN addgroup -S app && adduser -S app -G app
USER app

ENV JAVA_OPTS=""
ENV NODE=""
ENV DOMAIN=""

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]