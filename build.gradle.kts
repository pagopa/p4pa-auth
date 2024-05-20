plugins {
	java
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	jacoco
	id("org.sonarqube") version "5.0.0.4638"
	id("com.github.ben-manes.versions") version "0.51.0"
	id ("org.openapi.generator") version "7.5.0"
}

group = "it.gov.pagopa.payhub"
version = "0.0.1"
description = "p4pa-auth"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}


repositories {
	mavenCentral()
}

val springDocOpenApiVersion = "2.5.0"
val janinoVersion = "3.1.12"
val openApiToolsVersion = "0.2.6"
val snakeYamlVersion = "2.0"
val javaJwtVersion = "4.4.0"
val jwksRsaVersion = "0.22.1"
val nimbusJoseJwtVersion = "9.38-rc5"
val jjwtVersion = "0.12.5"
val wiremockVersion = "3.5.4"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
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

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	//	Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test")
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
	dependsOn("openApiGenerate")
}

configure<SourceSetContainer> {
	named("main") {
		java.srcDir("$projectDir/build/generated/src/main/java")
	}
}

springBoot {
	mainClass.value("pagopa.payhub.ionotification.IoNotificationApplication")
}

openApiGenerate {
	generatorName.set("spring")
	inputSpec.set("$rootDir/openapi/p4pa-auth.openapi.yaml")
	outputDir.set("$projectDir/build/generated")
	apiPackage.set("org.openapi.example.api")
	invokerPackage.set("org.openapi.example.invoker")
	modelPackage.set("org.openapi.example.model")
	configOptions.set(mapOf(
			"dateLibrary" to "java8",
			"requestMappingMode" to "api_interface",
			"useSpringBoot3" to "true"
	))
}