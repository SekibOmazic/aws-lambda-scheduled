package com.omazicsekib

import com.omazicsekib.dynamodb.Tweet
import twitter4j.*
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder
import kotlin.math.min


class TwitterService(
    apiKey: String,
    apiSecret: String,
    accessToken: String,
    accessTokenSecret: String
) {
    private val configuration: Configuration = ConfigurationBuilder()
            .setDebugEnabled(true)
            .setOAuthConsumerKey(apiKey)
            .setOAuthConsumerSecret(apiSecret)
            .setOAuthAccessToken(accessToken)
            .setOAuthAccessTokenSecret(accessTokenSecret).build()

    private val twitter: Twitter = TwitterFactory(configuration).getInstance()

    fun searchTwitter(searchTerm: String, sinceId: Long = 0): List<Tweet> {
        val query = Query(searchTerm)
        query.sinceId(sinceId)

        val tweets = mutableListOf<Tweet>()
        var maxId = Long.MAX_VALUE
        val maxBatchSize = 512

        do {
            val batchSize  = min(100, maxBatchSize - tweets.size)
            query.count(batchSize)
                    .maxId(maxId - 1)

            val batchOfTweets = twitter.search(query).tweets
                    .map { Tweet(searchTerm, it.id, it.text, it.lang, it.createdAt.toString()) }

            tweets.addAll(batchOfTweets)

            batchOfTweets.forEach {
                if (it.tweetId < maxId) {
                    maxId = it.tweetId
                }
            }
        } while (batchOfTweets.size > 0 && tweets.size < maxBatchSize )

        return tweets
    }
}
