#
# Build
#
FROM eclipse-temurin:22_36-jdk-alpine AS buildtime

WORKDIR /build
COPY . .

RUN ./gradlew bootJar

#
# Docker RUNTIME
#
FROM eclipse-temurin:22_36-jre-alpine AS runtime

WORKDIR /app

COPY --from=buildtime /build/build/libs/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.5.1/applicationinsights-agent-3.5.1.jar /app/applicationinsights-agent.jar

ENTRYPOINT ["java", "$JAVA_OPTS", "-jar","/app/app.jar"]