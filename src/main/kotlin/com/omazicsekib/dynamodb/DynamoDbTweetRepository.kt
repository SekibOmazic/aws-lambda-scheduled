package com.omazicsekib.dynamodb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.*


class DynamoDbTweetRepository(
        private val tableName: String,
        private val dynamoDbClient: DynamoDbClient
) : TweetRepository {

    override fun lastId(): Long {
        val items = dynamoDbClient.scan { scan ->
            scan.tableName(tableName)
                    .attributesToGet("tweetId")
                    .limit(1)
        }.items()

        val lastId = items
                .map { it["tweetId"]!!.n().toLong()  }
                .max() ?: 0

        return lastId
    }

    override fun all(): List<Tweet> {
        val scanResponse = dynamoDbClient.scan { scan ->
            scan.tableName(tableName)
        }

        return scanResponse.items().map { it.toTweet() }
    }

    override fun lastTweet(searchTerm: String): Tweet? {
        val queryResponse = dynamoDbClient.query(
                QueryRequest.builder()
                        .tableName(tableName)
                        .keyConditionExpression("searchTerm = :key")
                        .expressionAttributeValues(
                                mapOf(
                                        ":key" to AttributeValue.builder().s(searchTerm).build()
                                )
                        )
                        .scanIndexForward(false)
                        .limit(1)
                        .build()
        ).items().firstOrNull()

        return queryResponse?.toTweet()
    }

    override fun delete(searchTerm: String, id: Long) {
        dynamoDbClient.deleteItem { delete ->
            delete.tableName(tableName)
            delete.key(
                    mapOf(
                        "searchTerm" to searchTerm.toAttributeValue(),
                        "tweetId" to id.toAttributeValue()
                    )
            )
        }
    }

    override fun save(tweet: Tweet) {
        dynamoDbClient.putItem { putItem ->
            putItem.tableName(tableName)
                    .item(tweet.toAttributeMap())
                    .conditionExpression("attribute_not_exists(tweetId)")
        }
    }

    override fun save (tweets: List<Tweet>) {

        val items = tweets.map {
            WriteRequest.builder()
                .putRequest(
                        PutRequest.builder()
                                .item(it.toAttributeMap())
                                .build()
                )
                .build()
        }

        dynamoDbClient.batchWriteItem { batchWrite ->
            batchWrite.requestItems(mapOf(
                    tableName to items
            ))
        }
    }

    private fun Long.toAttributeValue() = AttributeValue.builder().n(this.toString()).build()

    private fun String.toAttributeValue() = AttributeValue.builder().s(this).build()

    private fun Tweet.toAttributeMap() : Map<String, AttributeValue> {
        return mapOf(
                "searchTerm" to searchTerm.toAttributeValue(),
                "tweetId" to tweetId.toAttributeValue(),
                "text" to text.toAttributeValue(),
                "lang" to lang.toAttributeValue(),
                "createdAt" to createdAt.toAttributeValue()
        )
    }

    private fun MutableMap<String, AttributeValue>.toTweet() =
            Tweet(
                    this["searchTerm"]!!.s(),
                    this["tweetId"]!!.n().toLong(),
                    this["text"]!!.s(),
                    this["lang"]!!.s(),
                    this["createdAt"]!!.s()
            )

}
