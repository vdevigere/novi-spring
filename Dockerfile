FROM eclipse-temurin:21-jdk-alpine as compile
WORKDIR /workspace/novi-app
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY activations activations
COPY core core
COPY persistence persistence
COPY web web
RUN ./mvnw clean install -DskipTests
ARG JAR_FILE=web/target/*.jar
RUN cp ${JAR_FILE} app.jar

FROM compile as start
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar ${0} ${@}"]