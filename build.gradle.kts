import java.util.Objects

plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "6.2.0.5505"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.openapi.generator") version "7.13.0"
    id("org.ajoberstar.grgit") version "5.3.2"
    id("com.gorylenko.gradle-git-properties") version "2.5.0"
}

group = "it.gov.pagopa.payhub"
version = "0.1.0"
description = "p4pa-auth"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


repositories {
    mavenCentral()
}

val springDocOpenApiVersion = "2.8.9"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.6"
val javaJwtVersion = "4.5.0"
val jwksRsaVersion = "0.22.2"
val nimbusJoseJwtVersion = "10.3"
val jjwtVersion = "0.12.6"
val wiremockVersion = "3.13.1"
val bouncycastleVersion = "1.81"
val micrometerVersion = "1.5.1"
val caffeineVersion = "3.2.1"
val httpClientVersion = "5.5"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion")
    implementation("org.codehaus.janino:janino:$janinoVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")

    // validation token jwt
    implementation("com.auth0:java-jwt:$javaJwtVersion")
    implementation("com.auth0:jwks-rsa:$jwksRsaVersion")
    implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJoseJwtVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    //	Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.projectlombok:lombok")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")

}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}
tasks {
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
    }
}

val projectInfo = mapOf(
    "artifactId" to project.name,
    "version" to project.version
)

tasks {
    val processResources by getting(ProcessResources::class) {
        filesMatching("**/application.yml") {
            expand(projectInfo)
        }
    }
}

configurations {
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

tasks.compileJava {
    dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
    group = "AutomaticallyGeneratedCode"
    description = "grouping all together automatically generate code tasks"

    dependsOn(
        "openApiGenerateP4PAAUTH",
        "openApiGenerateORGANIZATION"
    )
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("$projectDir/build/generated/src/main/java")
    }
}

springBoot {
    buildInfo()
    mainClass.value("it.gov.pagopa.payhub.auth.PayhubAuthApplication")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateP4PAAUTH") {
    group = "openapi"
    description = "description"

    generatorName.set("spring")
    inputSpec.set("$rootDir/openapi/p4pa-auth.openapi.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.payhub.controller.generated")
    modelPackage.set("it.gov.pagopa.payhub.dto.generated")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "requestMappingMode" to "api_interface",
        "useSpringBoot3" to "true",
        "interfaceOnly" to "true",
        "useTags" to "true",
        "useBeanValidation" to "true",
        "generateConstructorWithAllArgs" to "true",
        "generatedConstructorWithRequiredArgs" to "true",
        "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    ))
}

var targetEnv = when (Objects.requireNonNullElse(System.getProperty("targetBranch"), grgit.branch.current().name)) {
    "uat" -> "uat"
    "main" -> "main"
    else -> "develop"
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateORGANIZATION") {
    group = "openapi"
    description = "description"

    generatorName.set("java")
    remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-organization/refs/heads/$targetEnv/openapi/generated.openapi.json")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.pu.p4pa-organization.controller.generated")
    modelPackage.set("it.gov.pagopa.pu.p4pa-organization.dto.generated")
    configOptions.set(mapOf(
        "swaggerAnnotations" to "false",
        "openApiNullable" to "false",
        "dateLibrary" to "java17",
        "useSpringBoot3" to "true",
        "useJakartaEe" to "true",
        "serializationLibrary" to "jackson",
        "generateSupportingFiles" to "true",
        "generateConstructorWithAllArgs" to "true",
        "generatedConstructorWithRequiredArgs" to "true",
        "enumPropertyNaming" to "original",
        "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
    ))
    library.set("resttemplate")
}