plugins {
	java
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	jacoco
	id("org.sonarqube") version "6.0.1.5171"
	id("com.github.ben-manes.versions") version "0.51.0"
	id("org.openapi.generator") version "7.10.0"
	id("org.ajoberstar.grgit") version "5.3.0"
}

group = "it.gov.pagopa.payhub"
version = "0.1.0"
description = "p4pa-auth"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}


repositories {
	mavenCentral()
}

val springDocOpenApiVersion = "2.7.0"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.6"
val snakeYamlVersion = "2.0"
val javaJwtVersion = "4.4.0"
val jwksRsaVersion = "0.22.1"
val nimbusJoseJwtVersion = "9.47"
val jjwtVersion = "0.12.6"
val wiremockVersion = "3.10.0"
val bouncycastleVersion = "1.79"
val micrometerVersion = "1.4.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-tracing-bridge-otel:$micrometerVersion")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springDocOpenApiVersion")
	implementation("org.codehaus.janino:janino:$janinoVersion")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	implementation("org.openapitools:jackson-databind-nullable:$openApiToolsVersion")

	// Security fixes
	implementation("org.yaml:snakeyaml:$snakeYamlVersion")

	// validation token jwt
	implementation("com.auth0:java-jwt:$javaJwtVersion")
	implementation("com.auth0:jwks-rsa:$jwksRsaVersion")
	implementation("com.nimbusds:nimbus-jose-jwt:$nimbusJoseJwtVersion")
	implementation("io.jsonwebtoken:jjwt:$jjwtVersion")
	implementation("org.bouncycastle:bcprov-jdk18on:$bouncycastleVersion")

	// PostgreSQL
	runtimeOnly("org.postgresql:postgresql")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("org.junit.jupiter:junit-jupiter-engine")
	testImplementation("org.mockito:mockito-core")
	testImplementation ("org.projectlombok:lombok")
	testImplementation ("org.wiremock:wiremock-standalone:$wiremockVersion")

}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
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
	dependsOn("openApiGenerateP4PAAUTH","openApiGenerateOrganization")
}

configure<SourceSetContainer> {
	named("main") {
		java.srcDir("$projectDir/build/generated/src/main/java")
	}
}

springBoot {
	mainClass.value("it.gov.pagopa.payhub.auth.PayhubAuthApplication")
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateP4PAAUTH") {
	group = "openapi"
	description = "description"

	generatorName.set("spring")
	inputSpec.set("$rootDir/openapi/p4pa-auth.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	apiPackage.set("it.gov.pagopa.payhub.controller.generated")
	modelPackage.set("it.gov.pagopa.payhub.model.generated")
	configOptions.set(mapOf(
			"dateLibrary" to "java8",
			"requestMappingMode" to "api_interface",
			"useSpringBoot3" to "true",
			"interfaceOnly" to "true",
			"useTags" to "true",
			"generateConstructorWithAllArgs" to "false",
			"generatedConstructorWithRequiredArgs" to "true",
			"additionalModelTypeAnnotations" to "@lombok.Data @lombok.Builder @lombok.AllArgsConstructor"
	))
}

var targetEnv = when (grgit.branch.current().name) {
	"uat" -> "uat"
	"main" -> "main"
	else -> "develop"
}

tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateOrganization") {
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
			"generateSupportingFiles" to "true"
	))
	library.set("resttemplate")
}