#
# Build
#
FROM maven:3.9.6-amazoncorretto-17-al2023@sha256:34cd4a7ad651b8d6a4e8abd51f2987fe92620a9f683fceb8cddd4044fb641d03 AS buildtime

WORKDIR /build
COPY . .

RUN ./gradlew bootJar

#
# Docker RUNTIME
#
FROM amazoncorretto:17-alpine3.19@sha256:070c3a37ea2465375014fbbdfbe0414d1e1a64339e1cc103d0059372bcc77647 AS runtime

WORKDIR /app

COPY --from=buildtime /build/build/libs/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.5.2/applicationinsights-agent-3.5..jar /app/applicationinsights-agent.jar

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar","/app/app.jar"]