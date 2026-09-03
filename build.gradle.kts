import com.github.jk1.license.filter.*
import com.github.jk1.license.render.*
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask
import java.util.Objects

plugins {
    java
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    jacoco
    id("org.sonarqube") version "7.4.0.8496"
    id("com.github.ben-manes.versions") version "0.54.0"
    id("org.openapi.generator") version "7.25.0"
    id("org.ajoberstar.grgit") version "5.3.2"
    id("com.gorylenko.gradle-git-properties") version "4.0.1"
    id("com.github.jk1.dependency-license-report") version "3.1.4"
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
    compileClasspath {
        resolutionStrategy.activateDependencyLocking()
    }
}

licenseReport {
    renderers = arrayOf(XmlReportRenderer("third-party-libs.xml", "Back-End Libraries"))
    outputDir = "$projectDir/dependency-licenses"
    filters = arrayOf(SpdxLicenseBundleNormalizer())
}
tasks.dependencies {
  finalizedBy(tasks.generateLicenseReport)
}

repositories {
    mavenCentral()
}

val springDocOpenApiVersion = "3.1.0"
val openApiToolsVersion = "0.2.11"
val javaJwtVersion = "4.6.0"
val jwksRsaVersion = "0.24.1"
val nimbusJoseJwtVersion = "10.9.1"
val jjwtVersion = "0.13.0"
val wiremockVersion = "3.13.2"
val bouncycastleVersion = "1.85.2"
val micrometerVersion = "1.7.1"
val caffeineVersion = "3.2.4"
val httpClientVersion = "5.6.4"
val httpCoreVersion = "5.4.3"
val kafkaAppender = "0.2.0-RC2"
val lz4JavaVersion = "1.11.2"
val commonsLang3Version = "3.20.0"
val podamVersion = "8.0.2.RELEASE"

// CVE Security dependencies
val tomcatEmbedCoreVersion = "11.0.25"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("com.github.ben-manes.caffeine:caffeine:$caffeineVersion")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion") {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.apache.commons:commons-lang3:$commonsLang3Version")
    implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")
    implementation("org.apache.httpcomponents.client5:httpclient5:$httpClientVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5-h2:$httpCoreVersion")
    implementation("org.apache.httpcomponents.core5:httpcore5:$httpCoreVersion")
    implementation("com.github.danielwegener:logback-kafka-appender:$kafkaAppender") {
        exclude(group = "org.lz4", module = "lz4-java")
    }
    implementation("at.yawk.lz4:lz4-java:$lz4JavaVersion")
    // validation token jwt
    implementation("com.auth0:java-jwt:$javaJwtVersion")
    implementation("com.auth0:jwks-rsa:$jwksRsaVersion")
    implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJoseJwtVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    implementation("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")
    implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")

    // CVE Security dependencies
    implementation("org.apache.tomcat.embed:tomcat-embed-core:$tomcatEmbedCoreVersion")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    //	Testing
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.projectlombok:lombok")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
    testImplementation("uk.co.jemos.podam:podam:$podamVersion")

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
    jar {
        from("${rootProject.projectDir}") {
            include("LICENSE.md")
            into("META-INF")
        }
    }
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
    testLogging.events = setOf(TestLogEvent.FAILED)
    testLogging.exceptionFormat = TestExceptionFormat.FULL
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
    processResources.dependsOn("dependenciesBuild")
}

tasks.compileJava {
    dependsOn("dependenciesBuild")
}

tasks.register("dependenciesBuild") {
    group = "AutomaticallyGeneratedCode"
    description = "grouping all together automatically generate code tasks"

    dependsOn(
        "openApiGenerateP4PAAUTH",
        "openApiGenerateORGANIZATION",
        "openApiGenerateDEBTPOSITIONS"
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

tasks.register<GenerateTask>("openApiGenerateP4PAAUTH") {
    group = "openapi"
    description = "description"

    generatorName.set("spring")
    inputSpec.set("$rootDir/openapi/p4pa-auth.openapi.yaml")
    outputDir.set("$projectDir/build/generated")
    apiPackage.set("it.gov.pagopa.payhub.controller.generated")
    modelPackage.set("it.gov.pagopa.payhub.dto.generated")
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "requestMappingMode" to "api_interface",
            "useSpringBoot4" to "true",
            "useJackson3" to "true",
            "interfaceOnly" to "true",
            "useTags" to "true",
            "useBeanValidation" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
}

var targetEnv = when (Objects.requireNonNullElse(System.getProperty("targetBranch"), grgit.branch.current().name)) {
    "uat" -> "uat"
    "main" -> "main"
    else -> "develop"
}

tasks.register<GenerateTask>("openApiGenerateORGANIZATION") {
    group = "openapi"
    description = "description"

    generatorName.set("java")
    remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-doc/refs/heads/main/openapi/$targetEnv/internal/p4pa-organization.generated.openapi.json")
    outputDir.set("$projectDir/build/generated")
    invokerPackage.set("it.gov.pagopa.pu.organization.generated")
    apiPackage.set("it.gov.pagopa.pu.organization.client.generated")
    modelPackage.set("it.gov.pagopa.pu.organization.dto.generated")
    configOptions.set(
        mapOf(
            "swaggerAnnotations" to "false",
            "openApiNullable" to "false",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "useSpringBoot4" to "true",
            "useJackson3" to "true",
            "useJakartaEe" to "true",
            "useOneOfInterfaces" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "generateSupportingFiles" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "enumPropertyNaming" to "original",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
    library.set("resttemplate")
}

tasks.register<GenerateTask>("openApiGenerateDEBTPOSITIONS") {
    group = "AutomaticallyGeneratedCode"
    description = "openapi"

    generatorName.set("java")
    remoteInputSpec.set("https://raw.githubusercontent.com/pagopa/p4pa-debt-positions/refs/heads/develop/openapi/generated.openapi.json")
    outputDir.set("$projectDir/build/generated")
    invokerPackage.set("it.gov.pagopa.pu.debtpositions.generated")
    apiPackage.set("it.gov.pagopa.pu.debtpositions.client.generated")
    modelPackage.set("it.gov.pagopa.pu.debtpositions.dto.generated")
    typeMappings.set(
        mapOf(
            "LocalDateTime" to "java.time.LocalDateTime"
        )
    )
    configOptions.set(
        mapOf(
            "swaggerAnnotations" to "false",
            "openApiNullable" to "false",
            "dateLibrary" to "java8",
            "serializableModel" to "true",
            "useSpringBoot4" to "true",
            "useJackson3" to "true",
            "useJakartaEe" to "true",
            "useOneOfInterfaces" to "true",
            "useBeanValidation" to "true",
            "serializationLibrary" to "jackson",
            "generateSupportingFiles" to "true",
            "generateConstructorWithAllArgs" to "true",
            "generatedConstructorWithRequiredArgs" to "true",
            "enumPropertyNaming" to "original",
            "additionalModelTypeAnnotations" to "@lombok.experimental.SuperBuilder(toBuilder = true)"
        )
    )
    library.set("resttemplate")
}