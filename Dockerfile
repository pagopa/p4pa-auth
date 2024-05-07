#
# Build
#
FROM amazoncorretto:17-alpine3.19@sha256:2122cb140fa94053abce343fb854d24f4c62ba3c1ac701882dce12980396b477 AS buildtime

WORKDIR /build
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

#
# Docker RUNTIME
#
FROM amazoncorretto:17-alpine3.19@sha256:2122cb140fa94053abce343fb854d24f4c62ba3c1ac701882dce12980396b477 AS runtime

WORKDIR /app

COPY --from=buildtime /build/build/libs/*.jar /app/app.jar
# The agent is enabled at runtime via JAVA_TOOL_OPTIONS.
ADD https://github.com/microsoft/ApplicationInsights-Java/releases/download/3.5.2/applicationinsights-agent-3.5.2.jar /app/applicationinsights-agent.jar

RUN chown -R nobody:nobody /app
EXPOSE 8080
USER 65534 # user nobody

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
