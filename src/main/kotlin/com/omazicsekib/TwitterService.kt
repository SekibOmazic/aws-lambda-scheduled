package com.omazicsekib

import org.slf4j.LoggerFactory
import twitter4j.*
import twitter4j.conf.Configuration
import twitter4j.conf.ConfigurationBuilder


class TwitterService(
    apiKey: String,
    apiSecret: String,
    accessToken: String,
    accessTokenSecret: String
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val configuration: Configuration = ConfigurationBuilder()
            .setDebugEnabled(true)
            .setOAuthConsumerKey(apiKey)
            .setOAuthConsumerSecret(apiSecret)
            .setOAuthAccessToken(accessToken)
            .setOAuthAccessTokenSecret(accessTokenSecret).build()

    private val twitter: Twitter = TwitterFactory(configuration).getInstance()

    fun searchTwitter(keyword: String): List<Status> {
        val query = Query(keyword + " -filter:retweets -filter:links -filter:replies -filter:images")
        query.setCount(100)

        try {
            return twitter.search(query).tweets
        } catch (e: TwitterException) {
            logger.error(e.toString())
        }

        return listOf()
    }
}
