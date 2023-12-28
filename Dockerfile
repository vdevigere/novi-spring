FROM eclipse-temurin:21-jdk-alpine as application
WORKDIR /workspace/novi-app
COPY novi-app/.mvn .mvn
COPY novi-app/mvnw .
COPY novi-app/pom.xml .
COPY novi-app/core core
COPY novi-app/persistence persistence
COPY novi-app/web web
RUN ./mvnw clean install -DskipTests
WORKDIR /workspace/novi-activations
COPY novi-activations .
RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine
ARG APP_WEB_TARGET=/workspace/novi-app/web/target
ARG ACTIVATION_TARGET=/workspace/novi-activations/target/uber-jar
WORKDIR /Novi
COPY --from=application ${APP_WEB_TARGET}/classes .
COPY --from=application ${APP_WEB_TARGET}/libs libs
COPY --from=application ${ACTIVATION_TARGET}/*.jar /var/builtIn-activations/
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -cp .:libs/*:/var/builtIn-activations/*:/var/plugin-activations/* org.novi.web.NoviApplication ${0} ${@}"]