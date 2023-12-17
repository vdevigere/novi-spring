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

FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=/workspace/novi-app/web/target/*.jar
COPY --from=compile ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar ${0} ${@}"]