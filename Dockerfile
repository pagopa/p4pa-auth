#
# Build
#
FROM maven:3.9.6-amazoncorretto-17-al2023@sha256:459be099faa25a32c06cd45ed1ef2bc9dbbf8a5414da4e72349459a1bb4d6166 AS buildtime

WORKDIR /build
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

#
# Docker RUNTIME
#
FROM amazoncorretto:17-alpine3.19@sha256:539a0a188ce5a2bed985aa311e9a26d473c6c3f37d08d4fc8b6cf6c18075b9ab AS runtime

WORKDIR /app

COPY --from=buildtime /build/build/libs/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.5.2/applicationinsights-agent-3.5.2.jar /app/applicationinsights-agent.jar

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar","/app/app.jar"]