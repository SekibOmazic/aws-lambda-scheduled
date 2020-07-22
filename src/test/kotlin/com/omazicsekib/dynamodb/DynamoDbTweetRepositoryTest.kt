package com.omazicsekib.dynamodb

import org.junit.jupiter.api.*
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest

class DynamoDbTweetRepositoryTest {
    private val searchTerm = "#awsrules -filter:retweets -filter:replies"

    @Test
    fun `add Tweet to DynamoDB`() {
        // given
        val tweet = Tweet(searchTerm, 1, "Tweet description","en", "2020-02-03")

        dynamoDbTweetRepository.save(tweet)

        // when
        val storedTweet = findByKey(searchTerm, tweet.tweetId.toString())

        // then
        Assertions.assertEquals(storedTweet, tweet)
    }

    @Test
    fun `retrieve the last inserted tweet id`() {
        // given
        val tweet1 = Tweet(searchTerm, 4, "Tweet description", "en", "2020-02-04")
        val tweet2 = Tweet(searchTerm, 5, "another tweet description", "en", "2020-02-03")
        val tweet3 = Tweet(searchTerm, 6, "yet another tweet description", "en", "2020-02-04")

        dynamoDbTweetRepository.save(listOf(tweet1, tweet2, tweet3))

        // when
        val tweet4 = Tweet(searchTerm, 5, "IGNORE ME", "en", "2020-02-04")
        assertThrows<ConditionalCheckFailedException> {
            dynamoDbTweetRepository.save(tweet4)
        }
        val last = dynamoDbTweetRepository.lastTweet(searchTerm)

        // then
        Assertions.assertEquals(6, last?.tweetId)
    }

    companion object {
        private val localDynamoDb = LocalDynamoDb()
        private lateinit var dynamoDbClient: DynamoDbClient
        private lateinit var dynamoDbTweetRepository: DynamoDbTweetRepository
        private val tableName = "tweets"
        private val hashKey = "searchTerm"
        private val rangeKey = "tweetId"

        @BeforeAll
        @JvmStatic
        fun setUp() {
            dynamoDbClient = localDynamoDb.start(tableName, hashKey, rangeKey)
            dynamoDbTweetRepository = DynamoDbTweetRepository(tableName, dynamoDbClient)
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            localDynamoDb.stop()
        }
    }

    private fun findByKey(searchTerm: String, tweetId: String): Tweet {
        val item = dynamoDbClient.getItem(
                GetItemRequest.builder()
                        .tableName(tableName)
                        .key(
                                mapOf(
                                        hashKey to AttributeValue.builder().s(searchTerm).build(),
                                        rangeKey to AttributeValue.builder().n(tweetId).build()
                                )
                        )
                        .build()
        ).item()

        if (item.isEmpty())
            throw ItemNotFoundInTable()

        return Tweet.from(item)
    }

    private fun Tweet.Companion.from(item: MutableMap<String, AttributeValue>) =
            Tweet(
                    item[hashKey]!!.s(),
                    item[rangeKey]!!.n().toLong(),
                    item["text"]!!.s(),
                    item["lang"]!!.s(),
                    item["createdAt"]!!.s()
            )
}
