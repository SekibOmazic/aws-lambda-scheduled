plugins {
    base

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.72"

    // Apply the application plugin to add support for building a CLI application.
    application
    java
    idea
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()

    maven {
        name = "DynamoDB Local Release Repository - EU (Frankfurt) Region"
        url = uri("https://s3.eu-central-1.amazonaws.com/dynamodb-local-frankfurt/release")
    }
}

tasks.register<Zip>("distribution") {
    from(tasks.compileKotlin)
    from(tasks.processResources)
    into("lib") {
        from(configurations.compileClasspath)
    }
}

tasks.build {
    dependsOn("distribution")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    // Copy SQLite4Java dynamic libs needed by DynamoDB local,
    // cf. https://jeboyer.wordpress.com/2017/10/18/tests-using-gradle-junit-and-dynamodb-local/
    register("copyNativeDeps", Copy::class) {
        from(configurations.testCompile) {
            include("*.dll")
            include("*.dylib")
            include("*.so")
        }
        into("build/libs")
    }
}

tasks.named<Test>("test") {
    dependsOn("copyNativeDeps")
    systemProperty("java.library.path", "build/libs")
    useJUnitPlatform()
}


dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(platform("software.amazon.awssdk:bom:2.13.59"))
    implementation("software.amazon.awssdk:dynamodb")
    implementation("software.amazon.awssdk:url-connection-client")
    // implementation("software.amazon.awssdk:apache-client")
    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")
    implementation("com.amazonaws:aws-lambda-java-events:2.2.9")

    implementation("org.twitter4j:twitter4j-core:4.0.7")

    // test libraries
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation( "io.mockk:mockk:1.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:1.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")

    // DynamoDB local
    testCompile("com.amazonaws:DynamoDBLocal:1.13.1")
    testCompile("com.almworks.sqlite4java:sqlite4java:1.0.392")
}

application {
    // Define the main class for the application.
    mainClassName = "com.omazicsekib.AppKt"
}
