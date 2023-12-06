FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/novi-app
COPY . .
RUN ./mvnw clean install -DskipTests


FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=web/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]