package com.omazicsekib

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import com.omazicsekib.dynamodb.DynamoDbTweetRepository
import com.omazicsekib.dynamodb.TweetRepository
import org.slf4j.LoggerFactory
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.http.apache.ApacheHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


class App(
    private val twitter: TwitterService = TwitterService(
            apiKey = System.getenv("API_KEY"),
            apiSecret = System.getenv("API_SECRET"),
            accessToken = System.getenv("ACCESS_TOKEN"),
            accessTokenSecret = System.getenv("ACCESS_TOKEN_SECRET")
    ),
    private val dynamoDbRepository: TweetRepository = DynamoDbTweetRepository(
            tableName = System.getenv("DYNAMODB_TABLE_NAME"),
            dynamoDbClient = DynamoDbClient.builder()
                    .region(Region.EU_CENTRAL_1)
                    .httpClientBuilder(ApacheHttpClient.builder().maxConnections(10))
                    .build()
    ),
    private val searchTerm: String = System.getenv("SEARCH_TERM")
) : RequestHandler<ScheduledEvent, Unit> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun handleRequest(event: ScheduledEvent, context: Context?) {
        val sinceId = dynamoDbRepository.lastTweet(searchTerm)?.tweetId ?: 0L

        val tweets = twitter.searchTwitter(searchTerm, sinceId)

        if (tweets.isNotEmpty()) {
            dynamoDbRepository.save(tweets)
        }

        logger.info(tweets.toString())
    }
}
