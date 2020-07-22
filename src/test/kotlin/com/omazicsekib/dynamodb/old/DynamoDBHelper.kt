package com.omazicsekib.dynamodb.old

import com.omazicsekib.dynamodb.ItemNotFoundInTable
import com.omazicsekib.dynamodb.Tweet
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*
import java.lang.IllegalStateException
import java.net.URI

class DynamoDBHelper(val dynamoDbClient: DynamoDbClient) {

    private val hashKey = "searchTerm"
    private val rangeKey = "tweetId"
    private val tableName = "tweets"

    init {
        setupTable()
    }

    companion object {

        fun connect(endpoint: String = "http://localhost:8000"): DynamoDBHelper {
            val dynamoDbClient = DynamoDbClient.builder()
                    .endpointOverride(URI.create(endpoint))
                    .build() ?: throw IllegalStateException()

            return DynamoDBHelper(dynamoDbClient)
        }
    }

    fun findByPrimaryKey(searchTerm: String, tweetId: String): Tweet {
        val item = dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(tableName)
                        .key(
                                mapOf(
                                    hashKey to AttributeValue.builder().s(searchTerm).build(), // partition key
                                    rangeKey to AttributeValue.builder().n(tweetId).build() // sort key
                                )
                        )
                        .build()
        ).item()

        if (item.isEmpty())
            throw ItemNotFoundInTable()

        return Tweet.from(item)
    }

    fun save(vararg tweets: Tweet) {
        tweets.forEach {
            dynamoDbClient.putItem(
                    PutItemRequest.builder()
                            .tableName(tableName)
                            .item(it.toAttributeMap())
                            .conditionExpression("attribute_not_exists($rangeKey)")
                            .build())
        }
    }

    fun setupTable() {
        deleteTable()
        createTable()
    }

    private fun createTable() {
        dynamoDbClient.createTable { builder ->
            builder.tableName(tableName)

            builder.provisionedThroughput { provisionedThroughput ->
                provisionedThroughput.readCapacityUnits(5)
                provisionedThroughput.writeCapacityUnits(5)
            }

            builder.keySchema(
                    KeySchemaElement.builder()
                            .attributeName(hashKey)
                            .keyType(KeyType.HASH)
                            .build()
            )

            builder.attributeDefinitions(
                    AttributeDefinition.builder()
                            .attributeName(hashKey)
                            .attributeType(ScalarAttributeType.N)
                            .build()
            )
        }
    }

    private fun deleteTable() {
        val tableExists = dynamoDbClient.listTables()
                .tableNames()
                .contains(tableName)

        if (tableExists) {
            dynamoDbClient.deleteTable(
                    DeleteTableRequest
                            .builder()
                            .tableName(tableName)
                            .build()
            )
        }
    }

    private fun Tweet.Companion.from(item: MutableMap<String, AttributeValue>) =
            Tweet(item["searchTerm"]!!.s(), item[rangeKey]!!.n().toLong(), item["text"]!!.s(), item["lang"]!!.s(), item["createdAt"]!!.s())

    private fun Tweet.toAttributeMap() : Map<String, AttributeValue> {
        return mapOf(
                hashKey to AttributeValue.builder().s(searchTerm).build(),
                rangeKey to AttributeValue.builder().n(tweetId.toString()).build(),
                "text" to AttributeValue.builder().s(text).build(),
                "lang" to  AttributeValue.builder().s(lang).build(),
                "createdAt" to AttributeValue.builder().s(createdAt).build()
        )
    }
}
