package com.omazicsekib

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent
import org.slf4j.LoggerFactory


class App(
    // private val eventLogger: EventLogger = EventLogger()
    private val twitter: TwitterService = TwitterService(
            apiKey = System.getenv("API_KEY"),
            apiSecret = System.getenv("API_SECRET"),
            accessToken = System.getenv("ACCESS_TOKEN"),
            accessTokenSecret = System.getenv("ACCESS_TOKEN_SECRET")
    )
) : RequestHandler<ScheduledEvent, Unit> {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun handleRequest(event: ScheduledEvent, context: Context?) {
        val tweets = twitter.searchTwitter("#traton")
        logger.info(tweets.toString())
    }
}
