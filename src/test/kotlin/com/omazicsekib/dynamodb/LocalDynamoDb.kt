package com.omazicsekib.dynamodb

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.net.URI

class LocalDynamoDb {

    private var server: DynamoDBProxyServer? = null
    private lateinit var dynamoDbClient: DynamoDbClient

    private val port = "8000"

    fun start(tableName: String, hashKeyName: String, rangeKeyName: String): DynamoDbClient {

        System.setProperty("sqlite4java.library.path", "./build/libs/")
        server = ServerRunner.createServerFromCommandLineArgs(arrayOf("-inMemory", "-port", port))

        server?.start()

        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .endpointOverride(URI.create("http://localhost:${port}"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("fakeAccessKey","fakeSecretKey"))
                )
                // add delay
//                .overrideConfiguration(ClientOverrideConfiguration.builder()
//                        .apiCallTimeout(Duration.ofSeconds(1)).build()
//                )
                .build()

        createTable(dynamoDbClient, tableName, hashKeyName, rangeKeyName)

        return dynamoDbClient
    }

    fun stop() {
        server?.stop()
    }

    private fun createTable(dynamoDbClient: DynamoDbClient, tableName: String, hashKeyName: String, rangeKeyName: String) {
        dynamoDbClient.createTable { builder ->
            builder.tableName(tableName)

            builder.provisionedThroughput { provisionedThroughput ->
                provisionedThroughput.readCapacityUnits(1)
                provisionedThroughput.writeCapacityUnits(1)
            }

            builder.keySchema(
                    KeySchemaElement.builder()
                            .attributeName(hashKeyName)
                            .keyType(KeyType.HASH)
                            .build(),
                    KeySchemaElement.builder()
                            .attributeName(rangeKeyName)
                            .keyType(KeyType.RANGE)
                            .build()
            )

            builder.attributeDefinitions(
                    AttributeDefinition.builder()
                            .attributeName(hashKeyName)
                            .attributeType(ScalarAttributeType.S)
                            .build(),
                    AttributeDefinition.builder()
                            .attributeName(rangeKeyName)
                            .attributeType(ScalarAttributeType.N)
                            .build()
            )
        }
    }

}
