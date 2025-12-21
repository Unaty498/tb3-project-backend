FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
# utilise le wrapper si présent, sinon la distribution gradle de l'image
RUN gradle bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre
ARG JAR_FILE=build/libs/*.jar
COPY --from=builder /home/gradle/project/${JAR_FILE} /app/app.jar
EXPOSE 8080
ENV JAVA_OPTS=""
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]