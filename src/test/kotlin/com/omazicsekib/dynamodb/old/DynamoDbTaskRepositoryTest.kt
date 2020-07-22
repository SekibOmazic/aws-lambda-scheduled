package com.omazicsekib.dynamodb.old

import com.omazicsekib.dynamodb.DynamoDbTweetRepository
import com.omazicsekib.dynamodb.ItemNotFoundInTable
import com.omazicsekib.dynamodb.Tweet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertNull

class DynamoDbTaskRepositoryTest {
    private val searchTerm = "#awsrules"

    private val dynamoDBHelper: DynamoDBHelper = DynamoDBHelper.connect()
    private lateinit var dynamoDbTweetRepository: DynamoDbTweetRepository

    @BeforeEach
    internal fun setUp() {
        dynamoDbTweetRepository = DynamoDbTweetRepository("tweets", dynamoDBHelper.dynamoDbClient)
        dynamoDBHelper.setupTable()
    }

    @Test
    internal fun `add Tweet to DynamoDB`() {
        val tweet = Tweet(searchTerm, 1, "Tweet description", "en", "2020-02-03")

        dynamoDbTweetRepository.save(tweet)

        val storedTweet = dynamoDBHelper.findByPrimaryKey(searchTerm, tweet.tweetId.toString())
        assertEquals(storedTweet, tweet)
    }

    @Test
    internal fun `retrieve all Tweets`() {
        val tweet1 = Tweet(searchTerm, 1, "Tweet text", "en", "2020-02-03")
        val tweet2 = Tweet(searchTerm, 2, "Another tweet text", "en", "2020-02-03")
        dynamoDBHelper.save(tweet1, tweet2)

        val tasks = dynamoDbTweetRepository.all()

        assertEquals(listOf(tweet2, tweet1), tasks)
    }

    @Test
    internal fun `delete Tweet from the table`() {
        val tweet = Tweet(searchTerm, 1, "Tweet text", "en", "2020-02-03")
        dynamoDBHelper.save(tweet)

        dynamoDbTweetRepository.delete(searchTerm, tweet.tweetId)

        assertThrows<ItemNotFoundInTable> {
            dynamoDBHelper.findByPrimaryKey(searchTerm, tweet.tweetId.toString())
        }
    }

    @Test
    internal fun `retrieve the last inserted tweet id`() {
        val tweet1 = Tweet(searchTerm, 1, "Tweet description", "en", "2020-02-04")
        val tweet2 = Tweet(searchTerm, 2, "another tweet description", "en", "2020-02-03")
        dynamoDBHelper.save(tweet1, tweet2)

        val lastId = dynamoDbTweetRepository.lastTweet(searchTerm)?.tweetId

        assertEquals(2, lastId)
    }

    @Test
    internal fun `first id should be 1`() {
        val tweet = dynamoDbTweetRepository.lastTweet("#awsrules")

        assertNull(tweet)
    }
}
