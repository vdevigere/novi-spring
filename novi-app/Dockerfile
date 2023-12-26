FROM eclipse-temurin:21-jdk-alpine as build
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
WORKDIR /Novi
ARG BUILD_FOLDER=/workspace/novi-app/web/target
COPY --from=build ${BUILD_FOLDER}/classes .
COPY --from=build ${BUILD_FOLDER}/libs libs
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -cp .:libs/*:/var/dsl-activations/* org.novi.web.NoviApplication ${0} ${@}"]